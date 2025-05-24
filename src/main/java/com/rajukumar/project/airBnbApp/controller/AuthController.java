package com.rajukumar.project.airBnbApp.controller;

import com.rajukumar.project.airBnbApp.dto.LoginDto;
import com.rajukumar.project.airBnbApp.dto.LoginResponseDto;
import com.rajukumar.project.airBnbApp.dto.SignUpRequestDto;
import com.rajukumar.project.airBnbApp.dto.UserDto;
import com.rajukumar.project.airBnbApp.security.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignUpRequestDto signUpRequestDto){
        return new ResponseEntity<>(authService.signUp(signUpRequestDto), HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        String[] tokens=authService.login(loginDto);
        Cookie cookie=new Cookie("refreshToken",tokens[1]);
        cookie.setHttpOnly(true);

        httpServletResponse.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponseDto(tokens[0]));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest httpServletRequest){
        String refreshToken= Arrays.stream(httpServletRequest.getCookies()).
                filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(()->new AuthenticationCredentialsNotFoundException("Refresh token not found inside the cookies"));

        String accessToken=authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new LoginResponseDto(accessToken));
    }

}
