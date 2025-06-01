package com.rajukumar.project.airBnbApp.service;

import com.rajukumar.project.airBnbApp.dto.RoomDto;
import com.rajukumar.project.airBnbApp.entity.Hotel;
import com.rajukumar.project.airBnbApp.entity.Room;
import com.rajukumar.project.airBnbApp.entity.User;
import com.rajukumar.project.airBnbApp.exception.ResourceNotFoundException;
import com.rajukumar.project.airBnbApp.exception.UnAuthoriseException;
import com.rajukumar.project.airBnbApp.repository.HotelRepository;
import com.rajukumar.project.airBnbApp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.rajukumar.project.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService{
    private final ModelMapper modelMapper;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;


    @Override
    @Transactional
    public RoomDto createNewRoom(Long hotelId,RoomDto roomDto) {
        log.info("Creating a new Room in Hotel with Id: {} ",hotelId);
        Hotel hotel=hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id: "+hotelId));
        Room room=modelMapper.map(roomDto,Room.class);
        room.setHotel(hotel);

        //validate the user is authenticated or not

        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthoriseException("This user doest not own this hotel with id: "+hotelId);
        }

        room=roomRepository.save(room);
        if(hotel.getActive()){
            inventoryService.initializeRoomForAYear(room);
        }
        //TODO: create inventory as soon as room is created and if hotel isactivate
        return modelMapper.map(room,RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all rooms in Hotel with ID: {}",hotelId);
        Hotel hotel=hotelRepository
                .findById(hotelId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with Id: "+hotelId));
        List<RoomDto> roomdtolist=hotel.getRooms()
                .stream()
                .map((room)->modelMapper.map(room, RoomDto.class))
                .toList();
        return roomdtolist;
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting the room with Id: {}",roomId);
        Room room=roomRepository.findById(roomId)
                .orElseThrow(()->new ResourceNotFoundException("Room not found with Id:"+roomId));
        return modelMapper.map(room,RoomDto.class);

    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("Deleting the ID: {}",roomId);
        Room room=roomRepository.findById(roomId)
                .orElseThrow(()->new ResourceNotFoundException("Room not found with Id:"+roomId));

        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(room.getHotel().getOwner())){
            throw new UnAuthoriseException("This user doest not own this hotel with id: "+roomId);
        }
        inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(roomId);
    }

    @Override
    public RoomDto updateRoomById(Long hotelId, Long roomId, RoomDto roomDto) {
        log.info("update the room with id: {}",roomId);
        Hotel hotel=hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id: {}"+hotelId));
        User user=getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthoriseException("This user doest not own this hotel with id: "+hotelId);
        }
        Room room=roomRepository.findById(roomId)
                .orElseThrow(()->new ResourceNotFoundException("Room not found with id: "+roomId));

        modelMapper.map(roomDto,room);
        room.setId(roomId);
        //TODO: if price or inventory us updated,then update the inventory for this room
        room=roomRepository.save(room);

        return modelMapper.map(room,RoomDto.class);
    }
}
