package com.rajukumar.project.airBnbApp.service;

import com.rajukumar.project.airBnbApp.entity.User;
import com.rajukumar.project.airBnbApp.exception.ResourceNotFoundException;
import com.rajukumar.project.airBnbApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User id is not found:"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElse(null);
    }
}
