package org.example.entities;

import org.example.constants.ResponseEnum;

public record CheckoutResult(
        ResponseEnum status,
        double subtotal,
        double discountedTotal,
        double tax,
        double shippingCost,
        double insurance,
        double finalTotal,
        int estimatedDeliveryDays,
        String trackingPrefix,
        String orderSummary,
        boolean returnable,
        String message
) {}