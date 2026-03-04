package org.example.service;

import org.example.constants.CustomerTypeEnum;
import org.example.constants.RegionEnum;
import org.example.entities.OrderStatus;

import static org.example.constants.CustomerTypeEnum.GOLD;
import static org.example.constants.CustomerTypeEnum.SILVER;
import static org.example.constants.ResponseEnum.*;


/**
 * N stands for Node;
 * D stands for Decision
 */
public class OrderService {

    public double calculateDiscount(double amount, CustomerTypeEnum customerType,
                                    boolean hasCoupon, boolean isHoliday) {
        double discount = 0;   // N 1

        if (amount <= 0) {  // D1 (N 1)
            return -1;                                // N 2
        }

        if (customerType.equals(GOLD)) {    // D2 (N 3)
            discount = 20;                            // N 4
        } else if (customerType.equals(SILVER)) {   // D3 (N 5)
            discount = 10;                            // N 6
        } else {
            discount = 0;                // N 7
        }

        if (hasCoupon) {    // D4 (N 8)
            discount += 5;        // N 9
        }

        if (isHoliday) {                              // D5 (N 10)
            discount += 3;                            // N 11
        }

        return amount - (amount * discount / 100);    // N 12
    }

    public OrderStatus validateAndProcessOrder(int quantity, double unitPrice,
                                               RegionEnum region, boolean isExpress) {

        if (quantity <= 0)                            // D1 (N 1)
            return new OrderStatus(INVALID_QUANTITY, null);                     // N 2

        if (unitPrice <= 0)                             // D2 (N 3)
            return new OrderStatus(INVALID_PRICE, null);                        // N 4

        double total = quantity * unitPrice;               // N 5

        switch (region) {
            case DOMESTIC: // D3 (N 5)
                total+=5;
                break;// N 6
            case INTERNATIONAL: // D4 (N 7)
                total+=25;  // N 8
                break;
            case INVALID: return new OrderStatus(INVALID_REGION, null); // N 9
        }

        if (isExpress)                                   // D5 (N 10)
            total += 15.0;                                 // N 11


        if (total > 1000)                                // D6 (N 12)
            return new OrderStatus(ORDER_REQUIRES_APPROVAL, total);           // N 13

        return new OrderStatus(ORDER_CONFIRMED, total);               //  N 14
    }
}