package com.rajukumar.project.airBnbApp.service;

import com.rajukumar.project.airBnbApp.entity.Room;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteFutureInventories(Room room);
}
