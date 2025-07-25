package com.rajukumar.project.airBnbApp.repository;

import com.rajukumar.project.airBnbApp.entity.Guest;
import com.rajukumar.project.airBnbApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestRepository extends JpaRepository<Guest,Long> {


    List<Guest> findByUser(User user);
}
