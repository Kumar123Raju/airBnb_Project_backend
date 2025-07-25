package com.rajukumar.project.airBnbApp.dto;

import com.rajukumar.project.airBnbApp.entity.enums.Gender;
import lombok.Data;
import lombok.Generated;

import java.time.LocalDate;

@Data
public class UserDto {
    private Long id;
    private  String email;
    private String name;
    private Gender gender;
    private LocalDate dateOfBirth;
}
