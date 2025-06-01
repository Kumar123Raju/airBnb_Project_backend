package com.rajukumar.project.airBnbApp.controller;

import com.rajukumar.project.airBnbApp.dto.BookingDto;
import com.rajukumar.project.airBnbApp.dto.GuestDto;
import com.rajukumar.project.airBnbApp.dto.HotelDto;
import com.rajukumar.project.airBnbApp.dto.HotelReportDto;
import com.rajukumar.project.airBnbApp.entity.Hotel;
import com.rajukumar.project.airBnbApp.service.BookingService;
import com.rajukumar.project.airBnbApp.service.GuestService;
import com.rajukumar.project.airBnbApp.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Hotels", description = "Operations related to Hotel management")
public class HotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;
    private final GuestService guestService;

    @Operation(summary = "Create a new hotel", description = "Adds a new hotel to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Hotel created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@RequestBody HotelDto hotelDto){
        log.info("Attemping to  create a new hotel with name: "+hotelDto.getName());
        HotelDto hotel=hotelService.createNewHotel(hotelDto);
        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    @Operation(summary = "Get a hotel by ID", description = "Returns the details of a hotel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel found"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId){
        HotelDto hotelDto=hotelService.getHotelById(hotelId);
        return new ResponseEntity<>(hotelDto,HttpStatus.CREATED);
    }

    @Operation(summary = "Update hotel", description = "Updates the details of a hotel by ID")
    @PutMapping("/{id}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long id, @RequestBody HotelDto hotelDto){
        log.info("Attemping to  update a new hotel with name: "+hotelDto.getName());
        HotelDto hotel=hotelService.updateHotelById(id,hotelDto);
        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete a hotel", description = "Removes a hotel by its ID")
    @DeleteMapping("/{hotelid}")
    public ResponseEntity<Void> deleteById(@PathVariable Long hotelid){
        hotelService.deleteHotelById(hotelid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Activate a hotel", description = "Marks a hotel as active")
    @PatchMapping("/{hotelid}/activate")
    public ResponseEntity<Void> activateHotel(@PathVariable Long hotelid){
        hotelService.activateHotel(hotelid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get all hotels", description = "Retrieves a list of all hotels")
    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels(){
        List<HotelDto> hotelList=hotelService.getAllHotels();
        return ResponseEntity.ok(hotelList);
    }

    @Operation(summary = "Get all bookings by hotel ID", description = "Retrieves all bookings for a specific hotel")
    @GetMapping("/{hotelId}/booking")
    public ResponseEntity<List<BookingDto>> getAllBookingsHotelId(@PathVariable Long hotelId){
        return ResponseEntity.ok(bookingService.getAllBookingByHotelId(hotelId));
    }

    @Operation(summary = "Get hotel report", description = "Generates a report for a hotel between two dates")
    @GetMapping("/{hotelId}/reports")
    public ResponseEntity<HotelReportDto> getHotelReport(@PathVariable Long hotelId , @RequestParam(required=false)LocalDate startDate
                                                                , @RequestParam(required = false) LocalDate endDate){
        if(startDate==null) startDate=LocalDate.now().minusMonths(1);
        if(endDate==null) endDate=LocalDate.now();

        return ResponseEntity.ok(bookingService.getHotelReport(hotelId,startDate,endDate));
    }

    @GetMapping("/guests")
    @Operation(summary = "Get all my guests", tags = {"Booking Guests"})
    public ResponseEntity<List<GuestDto>> getAllGuests() {
        return ResponseEntity.ok(guestService.getAllGuests());
    }

    @PostMapping("/guests")
    @Operation(summary = "Add a new guest to my guests list", tags = {"Booking Guests"})
    public ResponseEntity<GuestDto> addNewGuest(@RequestBody GuestDto guestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.addNewGuest(guestDto));
    }

    @PutMapping("guests/{guestId}")
    @Operation(summary = "Update a guest", tags = {"Booking Guests"})
    public ResponseEntity<Void> updateGuest(@PathVariable Long guestId, @RequestBody GuestDto guestDto) {
        guestService.updateGuest(guestId, guestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("guests/{guestId}")
    @Operation(summary = "Remove a guest", tags = {"Booking Guests"})
    public ResponseEntity<Void> deleteGuest(@PathVariable Long guestId) {
        guestService.deleteGuest(guestId);
        return ResponseEntity.noContent().build();
    }


}
