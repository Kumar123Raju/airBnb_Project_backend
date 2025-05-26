package com.rajukumar.project.airBnbApp.service;

import com.rajukumar.project.airBnbApp.entity.Booking;

public interface CheckoutService {

    String getCheckoutSession(Booking booking, String successUlr, String failureUrl);
}
