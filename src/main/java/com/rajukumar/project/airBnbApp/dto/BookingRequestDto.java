package com.rajukumar.project.airBnbApp.dto;

import com.rajukumar.project.airBnbApp.entity.Hotel;
import com.rajukumar.project.airBnbApp.entity.Room;
import com.rajukumar.project.airBnbApp.entity.User;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDto {
    private Long hotelId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer roomsCount;

}
