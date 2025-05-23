package com.rajukumar.project.airBnbApp.controller;

import com.rajukumar.project.airBnbApp.dto.HotelDto;
import com.rajukumar.project.airBnbApp.dto.HotelInfoDto;
import com.rajukumar.project.airBnbApp.dto.HotelSearchRequest;
import com.rajukumar.project.airBnbApp.service.HotelService;
import com.rajukumar.project.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelDto>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest){

        Page<HotelDto> page=inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);

    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){

        return ResponseEntity.ok(hotelService.getHotelInfoBy(hotelId));
    }

}
