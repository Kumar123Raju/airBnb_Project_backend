package com.rajukumar.project.airBnbApp.service;

import com.rajukumar.project.airBnbApp.dto.BookingDto;
import com.rajukumar.project.airBnbApp.dto.BookingRequestDto;
import com.rajukumar.project.airBnbApp.dto.GuestDto;
import com.rajukumar.project.airBnbApp.entity.*;
import com.rajukumar.project.airBnbApp.entity.enums.BookingStatus;
import com.rajukumar.project.airBnbApp.exception.ResourceNotFoundException;
import com.rajukumar.project.airBnbApp.repository.*;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

        for(Inventory inventory:inventoryList){
            inventory.setReservedCount(inventory.getReservedCount()+bookingRequest.getRoomsCount());
        }

        inventoryRepository.saveAll(inventoryList);

//        create the booking
//        User user=new User();
//        user.setId(1L);   //TODO: REMOVE DUMMY USER

//        TODO: CALCULATE DYNAMIC AMOUNT

       Booking booking =Booking.builder()
               .bookingStatus(BookingStatus.RESERVED)
               .hotel(hotel)
               .room(room)
               .checkInDate(bookingRequest.getCheckInDate())
               .checkOutDate(bookingRequest.getCheckOutDate())
//               .user(getCurrentUser())
               .roomsCount(bookingRequest.getRoomsCount())
               .amount(BigDecimal.TEN)
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

    public boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser(){
        User user=new User();
        user.setId(1L);   //TODO: remove dummy user
        return user;
    }
}
