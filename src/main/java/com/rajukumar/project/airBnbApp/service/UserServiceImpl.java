package com.rajukumar.project.airBnbApp.service;

import com.rajukumar.project.airBnbApp.dto.ProfileUpdateRequestDto;
import com.rajukumar.project.airBnbApp.entity.User;
import com.rajukumar.project.airBnbApp.exception.ResourceNotFoundException;
import com.rajukumar.project.airBnbApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.rajukumar.project.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User id is not found:"));
    }

    @Override
    public void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto) {
        User user=getCurrentUser();

        if(profileUpdateRequestDto.getDateOfBirth()!=null ) user.setDateOfBirth(profileUpdateRequestDto.getDateOfBirth());
        if(profileUpdateRequestDto.getGender()!=null) user.setGeneder(profileUpdateRequestDto.getGender());
        if(profileUpdateRequestDto.getName()!=null) user.setName(profileUpdateRequestDto.getName());

        userRepository.save(user);

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElse(null);
    }
}
