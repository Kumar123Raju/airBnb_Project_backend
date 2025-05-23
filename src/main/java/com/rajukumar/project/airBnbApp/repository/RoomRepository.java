package com.rajukumar.project.airBnbApp.repository;

import com.rajukumar.project.airBnbApp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository  extends JpaRepository<Room,Long> {
}
