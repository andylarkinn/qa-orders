package org.example.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class ShippingServiceDecisionTableTest {

    private ShippingService shippingService;

    @BeforeEach
    void setUp() {
        shippingService = new ShippingService();
    }

    @Test
    void testR1_MemberOver100Express() {
        double result = shippingService.determineShippingCost(
            true, 150.0, true);
        assertEquals(9.99, result, 0.001);
    }

    @Test
    void testR2_MemberOver100Standard() {
        double result = shippingService.determineShippingCost(
            true, 150.0, false);
        assertEquals(0.00, result, 0.001);
    }

    @Test
    void testR3_MemberUnder100Express() {
        double result = shippingService.determineShippingCost(
            true, 50.0, true);
        assertEquals(13.98, result, 0.001);
    }

    @Test
    void testR4_MemberUnder100Standard() {
        double result = shippingService.determineShippingCost(
            true, 50.0, false);
        assertEquals(3.99, result, 0.001);
    }

    @Test
    void testR5_NonMemberOver100Express() {
        double result = shippingService.determineShippingCost(
            false, 150.0, true);
        assertEquals(13.98, result, 0.001);
    }

    @Test
    void testR6_NonMemberOver100Standard() {
        double result = shippingService.determineShippingCost(
            false, 150.0, false);
        assertEquals(3.99, result, 0.001);
    }

    @Test
    void testR7_NonMemberUnder100Express() {
        double result = shippingService.determineShippingCost(
            false, 50.0, true);
        assertEquals(15.98, result, 0.001);
    }

    @Test
    void testR8_NonMemberUnder100Standard() {
        double result = shippingService.determineShippingCost(
            false, 50.0, false);
        assertEquals(5.99, result, 0.001);
    }
}