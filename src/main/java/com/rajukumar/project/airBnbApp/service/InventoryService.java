package com.rajukumar.project.airBnbApp.service;

import com.rajukumar.project.airBnbApp.dto.HotelDto;
import com.rajukumar.project.airBnbApp.dto.HotelPriceDto;
import com.rajukumar.project.airBnbApp.dto.HotelSearchRequest;
import com.rajukumar.project.airBnbApp.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
