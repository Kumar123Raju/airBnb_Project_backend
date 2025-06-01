package com.rajukumar.project.airBnbApp.service;

import com.rajukumar.project.airBnbApp.dto.*;
import com.stripe.model.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BookingService {


    BookingDto initialiseBooking(BookingRequestDto bookingRequest);

    BookingDto addGuest(Long bookingid, List<GuestDto> guestDtoList);

    String initiatePayment(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    String getBookingStatus(Long bookingId);

    List<BookingDto> getAllBookingByHotelId(Long hotelId);

    HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);

    List<BookingDto> getMyBooking();

    UserDto getMyProfile();
}
