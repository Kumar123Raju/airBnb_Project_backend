package com.rajukumar.project.airBnbApp.repository;

import com.rajukumar.project.airBnbApp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking,Long> {
}
