package com.rajukumar.project.airBnbApp.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class HontelContactInfo {
    private String address;
    private String phoneNumber;
    private String location;
    private String email;
}
