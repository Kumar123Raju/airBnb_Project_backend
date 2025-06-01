package com.rajukumar.project.airBnbApp.service;

import com.rajukumar.project.airBnbApp.dto.ProfileUpdateRequestDto;
import com.rajukumar.project.airBnbApp.entity.User;
import org.springframework.stereotype.Service;


public interface UserService {

    User getUserById(Long id);


    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);
}
