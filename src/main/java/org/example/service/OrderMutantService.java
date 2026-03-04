package org.example.service;

import org.example.constants.CustomerTypeEnum;

import static org.example.constants.CustomerTypeEnum.GOLD;
import static org.example.constants.CustomerTypeEnum.SILVER;

public class OrderMutantService {
    public double calculateDiscount_M1(double amount, CustomerTypeEnum customerType,
                                       boolean hasCoupon, boolean isHoliday) {
        double discount;
        if (amount < 0) { return -1; }  // MUTATED: <= changed to <
        if (customerType.equals(GOLD))        { discount = 20; }
        else if (customerType.equals(SILVER)) { discount = 10; }
        else                                    { discount = 0;  }
        if (hasCoupon) { discount += 5; }
        if (isHoliday) { discount += 3; }
        return amount - (amount * discount / 100);
    }

    public double calculateDiscount_M2(double amount, CustomerTypeEnum customerType,
                                       boolean hasCoupon, boolean isHoliday) {
        double discount;
        if (amount <= 0) { return -1; }
        if (customerType.equals(GOLD))        { discount = 21; } // MUTATED 20->21
        else if (customerType.equals(SILVER)) { discount = 10; }
        else                                    { discount = 0;  }
        if (hasCoupon) { discount += 5; }
        if (isHoliday) { discount += 3; }
        return amount - (amount * discount / 100);
    }

    public double calculateDiscount_M3(double amount, CustomerTypeEnum customerType,
                                       boolean hasCoupon, boolean isHoliday) {
        double discount;
        if (amount <= 0) { return -1; }
        if (customerType.equals(GOLD))        { discount = 20; }
        else if (customerType.equals(GOLD)) { discount = 20; } // MUTATED 10->20
        else                                    { discount = 0;  }
        if (hasCoupon) { discount += 5; }
        if (isHoliday) { discount += 3; }
        return amount - (amount * discount / 100);
    }

    public double calculateDiscount_M4(double amount, CustomerTypeEnum customerType,
                                       boolean hasCoupon, boolean isHoliday) {
        double discount;
        if (amount <= 0) { return -1; }
        if (customerType.equals(GOLD))        { discount = 20; }
        else if (customerType.equals(SILVER)) { discount = 10; }
        else                                    { discount = 0;  }
        if (hasCoupon) { discount -= 5; }  // MUTATED: += changed to -=
        if (isHoliday) { discount += 3; }
        return amount - (amount * discount / 100);
    }

    public double calculateDiscount_M5(double amount, CustomerTypeEnum customerType,
                                       boolean hasCoupon, boolean isHoliday) {
        double discount;
        if (amount <= 0) { return -1; }
        if (customerType.equals(GOLD))        { discount = 20; }
        else if (customerType.equals(SILVER)) { discount = 10; }
        else                                    { discount = 0;  }
        if (hasCoupon) { discount += 5; }
        if (isHoliday) { discount += 30; } // MUTATED: 3 -> 30
        return amount - (amount * discount / 100);
    }

    public double calculateDiscount_M6(double amount, CustomerTypeEnum customerType,
                                       boolean hasCoupon, boolean isHoliday) {
        double discount;
        if (amount <= 0) { return -1; }
        if (customerType.equals(GOLD))        { discount = 20; }
        else if (customerType.equals(SILVER)) { discount = 10; }
        else                                    { discount = 0;  }
        if (!hasCoupon) { discount += 5; } // MUTATED: has -> !has
        if (isHoliday) { discount += 3; }
        return amount - (amount * discount / 100);
    }

    public double calculateDiscount_M7(double amount, CustomerTypeEnum customerType,
                                       boolean hasCoupon, boolean isHoliday) {
        double discount;
        if (amount <= 0) { return 0; }  // MUTATED: -1 -> 0
        if (customerType.equals(GOLD))        { discount = 20; }
        else if (customerType.equals(SILVER)) { discount = 10; }
        else                                    { discount = 0;  }
        if (hasCoupon) { discount += 5; }
        if (isHoliday) { discount += 3; }
        return amount - (amount * discount / 100);
    }

    public double calculateDiscount_M8(double amount, CustomerTypeEnum customerType,
                                       boolean hasCoupon, boolean isHoliday) {
        double discount;
        if (amount <= 0) { return -1; }
        if (customerType.equals(GOLD))        { discount = 20; }
        else if (customerType.equals(SILVER)) { discount = 10; }
        else                                    { discount = 0;  }
        if (hasCoupon) { discount += 5; }
        if (isHoliday) { discount += 3; }
        return amount - (amount + discount / 100); // MUTATED: * -> +
    }

    public double calculateDiscount_M9(double amount, CustomerTypeEnum customerType,
                                       boolean hasCoupon, boolean isHoliday) {
        double discount;
        if (amount <= 0) { return -1; }
        if (customerType.equals(GOLD))        { discount = 20; }
        else if (customerType.equals(SILVER)) { discount = 10; }
        else                                    { discount = 0;  }
        if (hasCoupon) { discount += 5; }
        // MUTATED: holiday block deleted entirely
        return amount - (amount * discount / 100);
    }

    public double calculateDiscount_M10(double amount, CustomerTypeEnum customerType,
                                        boolean hasCoupon, boolean isHoliday) {
        double discount;
        if (amount <= 0) { return -1; }
        if (customerType.equals(GOLD))        { discount = 20; }
        else if (customerType.equals(SILVER)) { discount = 10; }
        else                                    { discount = 0;  }
        if (hasCoupon) { discount += 5; }
        if (!isHoliday) { discount += 3; } // MUTATED: negated condition
        return amount - (amount * discount / 100);
    }
}
