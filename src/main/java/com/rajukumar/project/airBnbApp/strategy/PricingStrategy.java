package com.rajukumar.project.airBnbApp.strategy;

import com.rajukumar.project.airBnbApp.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
