package org.example.service;

public class ShippingService {

    public double determineShippingCost(boolean isMember,
                                        double orderTotal,
                                        boolean isExpress) {
        double cost;

        if (isMember && orderTotal > 100) {
            cost = 0.0;
        } else if ((isMember && orderTotal <= 100) || (!isMember && orderTotal > 100)) {
            cost = 3.99;
        } else {
            cost = 5.99;
        }

        if (isExpress) {
            cost += 9.99;
        }

        return Math.round(cost * 100.0) / 100.0;
    }
}