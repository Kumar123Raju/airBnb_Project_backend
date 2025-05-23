package com.rajukumar.project.airBnbApp.advice;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.rajukumar.project.airBnbApp.advice.ApiError;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponse<T> {

    //    @JsonFormat(pattern = "hh:mm:ss dd-MM-yyyy")
    private LocalDateTime timeStamp;
    private T data;
    private ApiError error;

    public ApiResponse() {
        this.timeStamp = LocalDateTime.now();
    }

    public ApiResponse(T data) {
        this();
        this.data = data;
    }

    public ApiResponse(ApiError error) {
        this();
        this.error = error;
    }
}
