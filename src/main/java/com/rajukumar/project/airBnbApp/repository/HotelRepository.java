package com.rajukumar.project.airBnbApp.repository;

import com.rajukumar.project.airBnbApp.entity.Hotel;
import com.rajukumar.project.airBnbApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long> {
    List<Hotel> findByOwner(User user);
}
