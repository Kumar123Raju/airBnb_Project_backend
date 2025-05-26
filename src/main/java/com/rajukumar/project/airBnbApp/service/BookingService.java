package com.rajukumar.project.airBnbApp.service;

import com.rajukumar.project.airBnbApp.dto.BookingDto;
import com.rajukumar.project.airBnbApp.dto.BookingRequestDto;
import com.rajukumar.project.airBnbApp.dto.GuestDto;
import com.stripe.model.Event;

import java.util.List;
import java.util.Map;

public interface BookingService {


    BookingDto initialiseBooking(BookingRequestDto bookingRequest);

    BookingDto addGuest(Long bookingid, List<GuestDto> guestDtoList);

    String initiatePayment(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    String getBookingStatus(Long bookingId);
}
