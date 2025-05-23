package com.rajukumar.project.airBnbApp.dto;

import com.rajukumar.project.airBnbApp.entity.HontelContactInfo;
import com.rajukumar.project.airBnbApp.entity.Room;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HotelDto {
    private Long id;
    private String name;
    private String city;
    private String[] photos;
    private String[] amenities;
    private HontelContactInfo contactInfo;
    private Boolean active;
}
