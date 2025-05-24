package com.rajukumar.project.airBnbApp.dto;

import com.rajukumar.project.airBnbApp.entity.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HotelPriceDto {
    private Hotel hotel;
    private Double price;

}
