package com.rajukumar.project.airBnbApp.service;

import com.rajukumar.project.airBnbApp.dto.BookingDto;
import com.rajukumar.project.airBnbApp.dto.BookingRequestDto;
import com.rajukumar.project.airBnbApp.dto.GuestDto;

import java.util.List;

public interface BookingService {


    BookingDto initialiseBooking(BookingRequestDto bookingRequest);

    BookingDto addGuest(Long bookingid, List<GuestDto> guestDtoList);
}
