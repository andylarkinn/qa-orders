package org.example.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.example.constants.CustomerTypeEnum.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceDiscountTest {

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService();
    }

    @Test
    void testPath1_InvalidAmount_ReturnsNegativeOne() {
        double result = orderService.calculateDiscount(
            -50, GOLD, true, true);
        assertEquals(-1, result, 0.001);
    }

    @Test
    void testPath1_ZeroAmount_ReturnsNegativeOne() {
        double result = orderService.calculateDiscount(
            0, SILVER, false, false);
        assertEquals(-1, result, 0.001);
    }

    @Test
    void testPath2_GoldNoCouponNoHoliday() {
        double result = orderService.calculateDiscount(
            200, GOLD, false, false);
        assertEquals(160.0, result, 0.001);
    }

    @Test
    void testPath3_SilverNoCouponNoHoliday() {
        double result = orderService.calculateDiscount(
            200, SILVER, false, false);
        assertEquals(180.0, result, 0.001);
    }

    @Test
    void testPath4_RegularNoCouponNoHoliday() {
        double result = orderService.calculateDiscount(
            200, BRONZE, false, false);
        assertEquals(200.0, result, 0.001);
    }

    @Test
    void testPath5_GoldWithCouponNoHoliday() {
        double result = orderService.calculateDiscount(
            200, GOLD, true, false);
        assertEquals(150.0, result, 0.001);
    }

    @Test
    void testPath6_GoldNoCouponHoliday() {
        double result = orderService.calculateDiscount(
            200, GOLD, false, true);
        assertEquals(154.0, result, 0.001);
    }
}