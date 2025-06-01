package com.rajukumar.project.airBnbApp.service;

import com.nimbusds.jose.proc.SecurityContext;
import com.rajukumar.project.airBnbApp.dto.HotelDto;
import com.rajukumar.project.airBnbApp.dto.HotelInfoDto;
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.rajukumar.project.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;


    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating a new Hotel with name: {}",hotelDto.getName());
        Hotel hotel=modelMapper.map(hotelDto,Hotel.class);
        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(user);


        hotel.setActive(false);
        hotel=hotelRepository.save(hotel);
        log.info("Created a new hotel with ID: {}",hotel.getId());
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting the hotel with ID: {}",id);
       Hotel hotel= hotelRepository.
                 findById(id).orElseThrow(()->new ResourceNotFoundException("Hotel not found with ID:{}" +id));

        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthoriseException("This user doest not own this hotel with id: "+id);
        }
        return  modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {

        log.info("update the hotel with id: {}",id);
        Hotel hotel=hotelRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id: {}"+id));
        modelMapper.map(hotelDto,hotel);

        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthoriseException("This user doest not own this hotel with id: "+id);
        }

        hotel.setId(id);
        hotel.setActive(false);
        hotel=hotelRepository.save(hotel);
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public void  deleteHotelById(Long id) {
        Hotel hotel=hotelRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id: "+id));

        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthoriseException("This user doest not own this hotel with id: "+id);
        }

       for(Room room :hotel.getRooms()){
           inventoryService.deleteAllInventories(room);
           roomRepository.deleteById(room.getId());
       }
        hotelRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void activateHotel(Long id) {
        log.info("Activating the Hotel with the ID: {}",id);
        Hotel hotel=hotelRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id: "+id));

        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthoriseException("This user doest not own this hotel with id: "+id);
        }

        hotel.setActive(true);
        for(Room room: hotel.getRooms()){
            inventoryService.initializeRoomForAYear(room);
        }
        hotelRepository.save(hotel);
    }

    @Override
    public HotelInfoDto getHotelInfoBy(Long hotelId) {
        Hotel hotel=hotelRepository
                .findById(hotelId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with hotel id: "+hotelId));

        List<RoomDto> rooms=hotel.getRooms()
                .stream().map((element)->modelMapper.map(element,RoomDto.class))
                .toList();

        return new HotelInfoDto(modelMapper.map(hotel,HotelDto.class),rooms);

    }

    @Override
    public List<HotelDto> getAllHotels() {

        User user=getCurrentUser();
        log.info("Getting alll hotels for the admin user with id: {}",user.getId());
        List<Hotel> hotels=hotelRepository.findByOwner(user);
        return hotels.stream()
                .map((element)->modelMapper.map(element,HotelDto.class))
                .collect(Collectors.toList());
    }


}
