package com.rajukumar.project.airBnbApp.dto;

import com.rajukumar.project.airBnbApp.entity.Booking;
import com.rajukumar.project.airBnbApp.entity.User;
import com.rajukumar.project.airBnbApp.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
public class GuestDto {
    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
//    private Set<Booking> bookings;
}
