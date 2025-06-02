package com.rajukumar.project.airBnbApp.service;

import com.nimbusds.jose.proc.SecurityContext;
import static com.rajukumar.project.airBnbApp.util.AppUtils.getCurrentUser;

import com.rajukumar.project.airBnbApp.dto.*;
import com.rajukumar.project.airBnbApp.entity.*;
import com.rajukumar.project.airBnbApp.entity.enums.BookingStatus;
import com.rajukumar.project.airBnbApp.exception.ResourceNotFoundException;
import com.rajukumar.project.airBnbApp.exception.UnAuthoriseException;
import com.rajukumar.project.airBnbApp.repository.*;
import com.rajukumar.project.airBnbApp.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final GuestRepository guestRepository;
    private final CheckoutService checkoutService;
    private final PricingService pricingService;
    private final EmailSenderService emailSenderService;

    @Value("${frontend.url}")
    private String frontendUrl;


    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequestDto bookingRequest) {

        log.info("Intialising booking for hotel: {} ,room:{},date {}-{}",bookingRequest.getRoomId(),bookingRequest.getCheckInDate(),
                    bookingRequest.getCheckOutDate());
        Hotel hotel=hotelRepository.findById(bookingRequest.getHotelId()).orElseThrow(()->
                new ResourceNotFoundException("Hotel not found with id: "+bookingRequest.getHotelId()));

        Room room=roomRepository.findById(bookingRequest.getRoomId()).orElseThrow(()->
                new ResourceNotFoundException("room not found with id: "+bookingRequest.getRoomId()));

        List<Inventory> inventoryList=inventoryRepository.findAndLockAvailableInventory(room.getId(),
                bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate(),bookingRequest.getRoomsCount());

        Long daysCount= ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate())+1;
        if(inventoryList.size()!=daysCount){
            throw new IllegalStateException("Room is not avaialbe anymore");
        }

//        Reserve the room/update the booked count oof inventoreis
        inventoryRepository.initBooking(room.getId(),bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate(),bookingRequest.getRoomsCount());


//        TODO: CALCULATE DYNAMIC AMOUNT
        BigDecimal priceForOneRoom=pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice=priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));

       Booking booking =Booking.builder()
               .bookingStatus(BookingStatus.RESERVED)
               .hotel(hotel)
               .room(room)
               .checkInDate(bookingRequest.getCheckInDate())
               .checkOutDate(bookingRequest.getCheckOutDate())
               .user(getCurrentUser())
               .roomsCount(bookingRequest.getRoomsCount())
               .amount(totalPrice)
               .build();

       booking=bookingRepository.save(booking);
       return modelMapper.map(booking,BookingDto.class);

    }

    @Override
    @Transactional
    public BookingDto addGuest(Long bookingid, List<GuestDto> guestDtoList) {
        log.info("Adding guests for booking with id: {}",bookingid);
        Booking booking=bookingRepository.findById(bookingid).orElseThrow(()->
                new ResourceNotFoundException("Booking not found with id: "+bookingid));

        User user=getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthoriseException("Booking does not belong to this user with id: "+user.getId());

        }

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired");
        }
        if(booking.getBookingStatus()!=BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not under reserved state,cannot add guests");
        }
        for(GuestDto guestDto:guestDtoList){
            Guest guest=modelMapper.map(guestDto,Guest.class);
//            guest.setUser(getCurrentUser());
            guest=guestRepository.save(guest);
            booking.getGuest().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUEST_ADDED);
        booking=bookingRepository.save(booking);

        return  modelMapper.map(booking,BookingDto.class);
    }

    @Override
    @Transactional
    public String initiatePayment(Long bookingId) {
        Booking booking =bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking not found with id:"+bookingId));

        User user=getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthoriseException("Booking does not belong to this user with id: "+user.getId());
        }
        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired");
        }
        String sessionUrl=
        checkoutService.getCheckoutSession(booking,frontendUrl+"/payments/sucess",frontendUrl+"/payments/failure");

        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

            if(session==null) return;

            String sessionId = session.getId();
            Booking booking = bookingRepository
                        .findByPaymentSessionId(sessionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Booking not found for session ID: " + sessionId));

                booking.setBookingStatus(BookingStatus.CONFIRMED);
                bookingRepository.save(booking);

                inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),booking.getCheckInDate(),
                        booking.getCheckOutDate(),booking.getRoomsCount());
                inventoryRepository.confirmBooking(booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());
                //once booking confirm then send email
            if(booking.getBookingStatus()==BookingStatus.CONFIRMED) {
                emailSenderService.sendEmail(
                        booking.getUser().getEmail(),
                        "BOOKING CONFIRMED",
                        "Dear " + booking.getUser().getUsername() + ",\n\n" +
                                "Thank you for your booking! Your reservation has been successfully confirmed.\n\n" +
                                "Booking Details:\n" +
                                "- Hotel Name: " + booking.getHotel().getName() + "\n" +
                                "- Room ID: " + booking.getRoom().getId() + "\n" +
                                "- Check-in: " + booking.getCheckInDate() + "\n" +
                                "- Check-out: " + booking.getCheckOutDate() + "\n" +
                                "- Payment Amount: ₹" + booking.getAmount() + "\n\n" +
                                "We look forward to hosting you. If you have any questions or need to make changes to your reservation, feel free to contact us.\n\n" +
                                "Best regards,\n" +
                                "The Airbnb Team"
                );
            }else{
                emailSenderService.sendEmail(
                        booking.getUser().getEmail(),
                        "PAYMENT FAILED – Booking Not Confirmed",
                        "Dear " + booking.getUser().getUsername() + ",\n\n" +
                                "We regret to inform you that your payment could not be processed, and your booking was not confirmed.\n\n" +
                                "Booking Attempt Details:\n" +
                                "- Hotel Name: " + booking.getHotel().getName() + "\n" +
                                "- Room ID: " + booking.getRoom().getId() + "\n" +
                                "- Intended Check-in: " + booking.getCheckInDate() + "\n" +
                                "- Intended Check-out: " + booking.getCheckOutDate() + "\n\n" +
                                "- Payment Amount: ₹" + booking.getAmount() + "\n\n" +
                                "Please review your payment information and try again. If the problem persists, feel free to reach out to our support team for assistance.\n\n" +
                                "We hope to help you complete your booking soon.\n\n" +
                                "Best regards,\n" +
                                "The Airbnb Team"
                );

            }
                log.info("Booking confirmed for session ID: {}", sessionId);
            } else {
                log.warn("unhandle for eventType: {}", event.getType());
            }


    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking =bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking not found with id:"+bookingId));

        User user=getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthoriseException("Booking does not belong to this user with id: "+user.getId());
        }
       if(booking.getBookingStatus()!=BookingStatus.CONFIRMED){
           throw new IllegalStateException("Only confirmed booking can be cancelled");
       }
       booking.setBookingStatus(BookingStatus.CANCELLED);
       bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),booking.getCheckInDate(),
                booking.getCheckOutDate(),booking.getRoomsCount());
        inventoryRepository.cancelBooking(booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());
        //Handle the refund
        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundCreateParams=RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())

                    .build();

            Refund.create(refundCreateParams);

        }catch (StripeException e){
            throw new RuntimeException(e);

        }

    }

    @Override
    public String getBookingStatus(Long bookingId) {
        Booking booking =bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking not found with id:"+bookingId));
        User user=getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthoriseException("Booking does not belong to this user with id: "+user.getId());
        }
        return booking.getBookingStatus().name();
    }

    @Override
    public List<BookingDto> getAllBookingByHotelId(Long hotelId) {
        Hotel hotel=hotelRepository.findById(hotelId).orElseThrow(()->new ResourceNotFoundException("Hotel not found with Id: "+hotelId));
        User user=getCurrentUser();
        log.info("Getting all booking for the hotel with id: "+hotelId);
        if(!user.equals(hotel.getOwner())) throw new AccessDeniedException("You are not the owner of hotel with hotelid: "+hotelId);
       List<Booking> bookings=bookingRepository.findByHotel(hotel);
       return bookings.stream().map((element)->modelMapper.map(element,BookingDto.class))
               .collect(Collectors.toList());


    }

    @Override
    public HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {
        Hotel hotel=hotelRepository.findById(hotelId).orElseThrow(()->new ResourceNotFoundException("Hotel not found with Id: "+hotelId));
        User user=getCurrentUser();
        log.info("Generate Report for the hotel with id: "+hotelId);
        if(!user.equals(hotel.getOwner())) throw new AccessDeniedException("You are not the owner of hotel with hotelid: "+hotelId);

        LocalDateTime startDateTime=startDate.atStartOfDay();
        LocalDateTime endDateTime=endDate.atTime(LocalTime.MAX);

        List<Booking> bookings=bookingRepository.findByHotelAndCreatedAtBetween(hotel,startDateTime,endDateTime);
        Long totalConfirmedBooking=bookings.stream()
                .filter(booking->booking.getBookingStatus()==BookingStatus.CONFIRMED)
                .count();

        BigDecimal totalRevenuOfConfirmedBookings=bookings.stream()
                .filter(booking->booking.getBookingStatus()==BookingStatus.CONFIRMED)
                .map(Booking::getAmount)
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        BigDecimal avgRevenue=totalConfirmedBooking==0? BigDecimal.ZERO:
          totalRevenuOfConfirmedBookings.divide(BigDecimal.valueOf(totalConfirmedBooking));

      return new HotelReportDto(totalConfirmedBooking,totalRevenuOfConfirmedBookings,avgRevenue);
    }

    @Override
    public List<BookingDto> getMyBooking() {

        User user=getCurrentUser();
        return  bookingRepository.findByUser(user).stream()
                .map((element)->modelMapper.map(element,BookingDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getMyProfile() {
        User user=getCurrentUser();
        log.info("Getting the profile for user with id: {}",user.getId());
        return modelMapper.map(user,UserDto.class);
    }

    public boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

//    public User getCurrentUser(){
//        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//    }
}
