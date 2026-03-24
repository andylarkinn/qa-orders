package org.example.service;

import org.example.constants.RegionEnum;

import static org.example.constants.RegionEnum.INVALID;

public class ShippingService {

    public double determineShippingCost(boolean isMember,
                                        double orderTotal,
                                        boolean isExpress) {
        double cost;

        if (isMember && orderTotal > 100) {
            cost = 0.0;
        } else if ((isMember && orderTotal <= 100)
                   || (!isMember && orderTotal > 100)) {
            cost = 3.99;
        } else {
            cost = 5.99;
        }

        if (isExpress) {
            cost += 9.99;
        }

        return Math.round(cost * 100.0) / 100.0;
    }

    public boolean isDeliveryAvailable(RegionEnum region) {
        return region != INVALID;
    }

    public int estimateDeliveryDays(RegionEnum region, boolean isExpress) {
        int baseDays = (region == RegionEnum.DOMESTIC) ? 5 : 14;
        return isExpress ? Math.max(1, baseDays - 3) : baseDays;
    }

    public double calculateInsurance(double orderTotal) {
        if (orderTotal > 200) {
            return Math.round(orderTotal * 0.02 * 100.0) / 100.0;
        }
        return 0.0;
    }

    public String getTrackingPrefix(RegionEnum region) {
        return (region == RegionEnum.DOMESTIC) ? "DOM" : "INT";
    }
}