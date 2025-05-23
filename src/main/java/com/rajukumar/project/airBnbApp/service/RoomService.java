package com.rajukumar.project.airBnbApp.service;

import com.rajukumar.project.airBnbApp.dto.RoomDto;
import com.rajukumar.project.airBnbApp.entity.Room;

import java.util.List;

public interface RoomService {
    RoomDto createNewRoom(Long hotelId,RoomDto roomDto);
    List<RoomDto> getAllRoomsInHotel(Long hotelId);
    RoomDto getRoomById(Long roomId);
    void deleteRoomById(Long roomId);

}
