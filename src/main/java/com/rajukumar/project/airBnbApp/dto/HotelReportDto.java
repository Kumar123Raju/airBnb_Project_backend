package com.rajukumar.project.airBnbApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelReportDto {
    private Long bookingCount;
    private BigDecimal totalRevenue;
    private BigDecimal avgRevenue;

//    public HotelReportDto(Long totalConfirmedBooking, BigDecimal totalRevenuOfConfirmedBookings, BigDecimal avgRevenue) {
//    }
}
