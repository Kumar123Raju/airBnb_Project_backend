package com.rajukumar.project.airBnbApp.service;

import com.rajukumar.project.airBnbApp.entity.Booking;
import com.rajukumar.project.airBnbApp.entity.User;
import com.rajukumar.project.airBnbApp.repository.BookingRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImpl implements CheckoutService{
    private final BookingRepository bookingRepository;

    @Override
    public String getCheckoutSession(Booking booking, String successUlr, String failureUrl) {
        log.info("Creating session for booking with id: {}",booking.getId());
        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

       try{
           CustomerCreateParams customeParams= CustomerCreateParams.builder()
                   .setName(user.getName())
                   .setEmail(user.getEmail())
                   .build();

           Customer customer=Customer.create(customeParams);

           SessionCreateParams sessionParams= SessionCreateParams.builder()
                   .setMode(SessionCreateParams.Mode.PAYMENT)
                   .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                   .setCustomer(customer.getId())
                   .setSuccessUrl(successUlr)
                   .setCancelUrl(failureUrl)
                   .addLineItem(
                           SessionCreateParams.LineItem.builder()
                                   .setQuantity(1L)
                                   .setPriceData(
                                           SessionCreateParams.LineItem.PriceData.builder()
                                                   .setCurrency("inr")
                                                   .setUnitAmount(booking.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                                                   .setProductData(
                                                           SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                   .setName(booking.getHotel().getName()+":"+booking.getRoom().getType())
                                                                   .setDescription("Booking ID: "+booking.getId())
                                                                   .build()
                                                   )
                                                   .build()

                                   ).build()
                   ) .build();


           Session session=Session.create(sessionParams);
           booking.setPaymentSessionId(session.getId());

           log.info("Succesfully Created session for booking with id: {}",booking.getId());
           return session.getUrl();


       }catch (StripeException e){
           throw new RuntimeException(e);
       }
    }
}
