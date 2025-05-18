package com.rajukumar.project.airBnbApp.entity;

import com.rajukumar.project.airBnbApp.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="app_user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Enumerated
    private Gender gender;


    private Integer age;

    @ManyToMany(mappedBy = "guest")
    private Set<Booking> bookings;
}
