package org.example.service;

import org.example.constants.CustomerTypeEnum;
import org.example.constants.RegionEnum;
import org.example.entities.OrderStatus;

import static org.example.constants.CustomerTypeEnum.GOLD;
import static org.example.constants.CustomerTypeEnum.SILVER;
import static org.example.constants.RegionEnum.DOMESTIC;
import static org.example.constants.RegionEnum.INTERNATIONAL;
import static org.example.constants.ResponseEnum.*;

public class OrderService {

    /* ──── ORIGINAL METHODS (untouched) ──── */

    public double calculateDiscount(double amount, CustomerTypeEnum customerType,
                                    boolean hasCoupon, boolean isHoliday) {
        double discount = 0;

        if (amount <= 0) {
            return -1;
        }

        if (customerType.equals(GOLD)) {
            discount = 20;
        } else if (customerType.equals(SILVER)) {
            discount = 10;
        } else {
            discount = 0;
        }

        if (hasCoupon) {
            discount += 5;
        }

        if (isHoliday) {
            discount += 3;
        }

        return amount - (amount * discount / 100);
    }

    public OrderStatus validateAndProcessOrder(int quantity, double unitPrice,
                                               RegionEnum region,
                                               boolean isExpress) {
        if (quantity <= 0)
            return new OrderStatus(INVALID_QUANTITY, null);

        if (unitPrice <= 0)
            return new OrderStatus(INVALID_PRICE, null);

        double total = quantity * unitPrice;

        switch (region) {
            case DOMESTIC:
                total += 5;
                break;
            case INTERNATIONAL:
                total += 25;
                break;
            case INVALID:
                return new OrderStatus(INVALID_REGION, null);
        }

        if (isExpress)
            total += 15.0;

        if (total > 1000)
            return new OrderStatus(ORDER_REQUIRES_APPROVAL, total);

        return new OrderStatus(ORDER_CONFIRMED, total);
    }

    /* ──── NEW METHODS (added for integration) ──── */

    public double calculateTax(double amount, RegionEnum region) {
        if (amount <= 0) return 0;
        if (region == DOMESTIC) return Math.round(amount * 0.08 * 100.0) / 100.0;
        if (region == INTERNATIONAL) return Math.round(amount * 0.15 * 100.0) / 100.0;
        return 0;
    }

    public boolean isOrderEligibleForReturn(double total, int daysSinceOrder) {
        return total > 0 && total < 500 && daysSinceOrder <= 30;
    }

    public String getOrderSummary(int quantity, double unitPrice) {
        return "Order: " + quantity + " x $"
               + String.format("%.2f", unitPrice)
               + " = $" + String.format("%.2f", quantity * unitPrice);
    }
}