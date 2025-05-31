package com.rajukumar.project.airBnbApp.controller;

import com.rajukumar.project.airBnbApp.dto.BookingDto;
import com.rajukumar.project.airBnbApp.dto.HotelDto;
import com.rajukumar.project.airBnbApp.dto.HotelReportDto;
import com.rajukumar.project.airBnbApp.entity.Hotel;
import com.rajukumar.project.airBnbApp.service.BookingService;
import com.rajukumar.project.airBnbApp.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@RequestBody HotelDto hotelDto){
        log.info("Attemping to  create a new hotel with name: "+hotelDto.getName());
        HotelDto hotel=hotelService.createNewHotel(hotelDto);
        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId){
        HotelDto hotelDto=hotelService.getHotelById(hotelId);
        return new ResponseEntity<>(hotelDto,HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long id, @RequestBody HotelDto hotelDto){
        log.info("Attemping to  update a new hotel with name: "+hotelDto.getName());
        HotelDto hotel=hotelService.updateHotelById(id,hotelDto);
        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    @DeleteMapping("/{hotelid}")
    public ResponseEntity<Void> deleteById(@PathVariable Long hotelid){
        hotelService.deleteHotelById(hotelid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{hotelid}/activate")
    public ResponseEntity<Void> activateHotel(@PathVariable Long hotelid){
        hotelService.activateHotel(hotelid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels(){
        List<HotelDto> hotelList=hotelService.getAllHotels();
        return ResponseEntity.ok(hotelList);
    }

    @GetMapping("/{hotelId}/booking")
    public ResponseEntity<List<BookingDto>> getAllBookingsHotelId(@PathVariable Long hotelId){
        return ResponseEntity.ok(bookingService.getAllBookingByHotelId(hotelId));
    }

    @GetMapping("/{hotelId}/reports")
    public ResponseEntity<HotelReportDto> getHotelReport(@PathVariable Long hotelId , @RequestParam(required=false)LocalDate startDate
                                                                , @RequestParam(required = false) LocalDate endDate){
        if(startDate==null) startDate=LocalDate.now().minusMonths(1);
        if(endDate==null) endDate=LocalDate.now();

        return ResponseEntity.ok(bookingService.getHotelReport(hotelId,startDate,endDate));
    }


}
