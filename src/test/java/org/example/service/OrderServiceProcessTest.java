package org.example.service;

import org.example.entities.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.example.constants.RegionEnum.*;
import static org.example.constants.ResponseEnum.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderServiceProcessTest {

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService();
    }

    @Test
    void testPath1_InvalidQuantity() {
        OrderStatus result = orderService.validateAndProcessOrder(
            0, 10.0, DOMESTIC, false);
        assertEquals(new OrderStatus(INVALID_QUANTITY, null), result);
    }

    @Test
    void testPath1_NegativeQuantity() {
        OrderStatus result = orderService.validateAndProcessOrder(
            -5, 10.0, DOMESTIC, false);
        assertEquals(new OrderStatus(INVALID_QUANTITY, null), result);
    }

    @Test
    void testPath2_InvalidPrice() {
        OrderStatus result = orderService.validateAndProcessOrder(
            5, -10.0, DOMESTIC, false);
        assertEquals(new OrderStatus(INVALID_PRICE, null), result);
    }

    @Test
    void testPath2_ZeroPrice() {
        OrderStatus result = orderService.validateAndProcessOrder(
            5, 0, DOMESTIC, false);
        assertEquals(new OrderStatus(INVALID_PRICE, null), result);
    }

    @Test
    void testPath3_DomesticStandard() {
        OrderStatus result = orderService.validateAndProcessOrder(
            5, 10.0, DOMESTIC, false);
        assertEquals(new OrderStatus(ORDER_CONFIRMED, 55.00), result);
    }

    @Test
    void testPath4_InternationalStandard() {
        OrderStatus result = orderService.validateAndProcessOrder(
            5, 10.0, INTERNATIONAL, false);
        assertEquals(new OrderStatus(ORDER_CONFIRMED, 75.00), result);
    }

    @Test
    void testPath5_InvalidRegion() {
        OrderStatus result = orderService.validateAndProcessOrder(
            5, 10.0, INVALID, false);
        assertEquals(new OrderStatus(INVALID_REGION, null), result);
    }

    @Test
    void testPath6_DomesticExpress() {
        OrderStatus result = orderService.validateAndProcessOrder(
            5, 10.0, DOMESTIC, true);
        assertEquals(new OrderStatus(ORDER_CONFIRMED, 70.00), result);
    }

    @Test
    void testPath7_HighValueOrder() {
        OrderStatus result = orderService.validateAndProcessOrder(
            100, 20.0, DOMESTIC, false);
        assertEquals(new OrderStatus(ORDER_REQUIRES_APPROVAL, 2005.00), result);
    }
}