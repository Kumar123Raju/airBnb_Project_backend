package com.rajukumar.project.airBnbApp.controller;

import com.rajukumar.project.airBnbApp.dto.BookingDto;
import com.rajukumar.project.airBnbApp.dto.BookingRequestDto;
import com.rajukumar.project.airBnbApp.dto.GuestDto;
import com.rajukumar.project.airBnbApp.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/booking")
@AllArgsConstructor
public class HotelBookingController {
    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingDto> initialiseBooking(@RequestBody BookingRequestDto bookingRequest){
        return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequest));

    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuest(@PathVariable Long bookingId, @RequestBody List<GuestDto> guestDtoList){
        return ResponseEntity.ok(bookingService.addGuest(bookingId,guestDtoList));
    }

    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<Map<String ,String>> initiatePayment(@PathVariable Long bookingId){
        String sessionUrl=bookingService.initiatePayment(bookingId);
        return ResponseEntity.ok(Map.of("sessionUrl",sessionUrl));
    }


    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Map<String ,String>> cancelBooking(@PathVariable Long bookingId){
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{bookingId}/status")
    public ResponseEntity<Map<String,String>> getBookingStatus(@PathVariable Long bookingId){
        return ResponseEntity.ok(Map.of("status",bookingService.getBookingStatus(bookingId)));
    }
}
