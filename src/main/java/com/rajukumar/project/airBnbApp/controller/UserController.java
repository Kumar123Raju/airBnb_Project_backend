package com.rajukumar.project.airBnbApp.controller;

import com.rajukumar.project.airBnbApp.dto.BookingDto;
import com.rajukumar.project.airBnbApp.dto.ProfileUpdateRequestDto;
import com.rajukumar.project.airBnbApp.dto.UserDto;
import com.rajukumar.project.airBnbApp.service.BookingService;
import com.rajukumar.project.airBnbApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;

    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody ProfileUpdateRequestDto profileUpdateRequestDto){
        userService.updateProfile(profileUpdateRequestDto);
        return  ResponseEntity.noContent().build();
    }

    @GetMapping("/myBookings")
    public ResponseEntity<List<BookingDto>> getMyBookings(){
        return ResponseEntity.ok(bookingService.getMyBooking());
    }


    @GetMapping("/profile")
    public ResponseEntity<UserDto> getMyProfile(){
        return ResponseEntity.ok(bookingService.getMyProfile());
    }


}
