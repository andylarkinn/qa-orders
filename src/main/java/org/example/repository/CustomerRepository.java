package org.example.repository;

import org.example.constants.CustomerTypeEnum;
import org.example.constants.RegionEnum;

/**
 * Simulates a data-access layer.
 * In production this would query a database.
 */
public class CustomerRepository {

    public CustomerTypeEnum getCustomerType(Long customerId) {
        // stub – DB lookup in real app
        return CustomerTypeEnum.BRONZE;
    }

    public boolean isMember(Long customerId) {
        return false;
    }

    public boolean hasCoupon(Long customerId) {
        return false;
    }

    public RegionEnum getRegion(Long customerId) {
        return RegionEnum.DOMESTIC;
    }

    public String getCustomerName(Long customerId) {
        return "Customer#" + customerId;
    }
}