package com.rajukumar.project.airBnbApp.dto;

import com.rajukumar.project.airBnbApp.entity.Guest;
import com.rajukumar.project.airBnbApp.entity.Hotel;
import com.rajukumar.project.airBnbApp.entity.Room;
import com.rajukumar.project.airBnbApp.entity.User;
import com.rajukumar.project.airBnbApp.entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDto {
    private long id;
    private Integer roomsCount;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BookingStatus bookingStatus;
    private Set<GuestDto> guest;
}
