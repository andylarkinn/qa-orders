package org.example.service;

import org.junit.jupiter.api.Test;

import static org.example.constants.CustomerTypeEnum.GOLD;
import static org.example.constants.CustomerTypeEnum.SILVER;
import static org.junit.jupiter.api.Assertions.*;

public class MutationKillingTest {

    private final OrderService original = new OrderService();
    private final OrderMutantService mutant = new OrderMutantService();

    @Test
    void killM1_RelationalOperatorChange() {
        double expected = -1.0;

        assertEquals(expected,
                original.calculateDiscount(0, GOLD, false, false), 0.001);

        assertNotEquals(expected,
                mutant.calculateDiscount_M1(0, GOLD, false, false), 0.001);
    }

    @Test
    void killM2_GoldDiscountConstantChange() {
        double expected = 160.0;

        assertEquals(expected,
                original.calculateDiscount(200, GOLD, false, false), 0.001);

        assertNotEquals(expected,
                mutant.calculateDiscount_M2(200, GOLD, false, false), 0.001);
    }

    @Test
    void killM3_SilverDiscountConstantChange() {
        double expected = 180.0;

        assertEquals(expected,
                original.calculateDiscount(200, SILVER, false, false), 0.001);

        assertNotEquals(expected,
                mutant.calculateDiscount_M3(200, SILVER, false, false), 0.001);
    }

    @Test
    void killM4_CouponArithmeticOperatorChange() {
        double expected = 150.0;

        assertEquals(expected,
                original.calculateDiscount(200, GOLD, true, false), 0.001);

        assertNotEquals(expected,
                mutant.calculateDiscount_M4(200, GOLD, true, false), 0.001);
    }

    @Test
    void killM5_HolidayConstantChange() {
        double expected = 154.0;

        assertEquals(expected,
                original.calculateDiscount(200, GOLD, false, true), 0.001);

        assertNotEquals(expected,
                mutant.calculateDiscount_M5(200, GOLD, false, true), 0.001);
    }

    @Test
    void killM6_CouponConditionNegated() {
        double expected = 160.0;

        assertEquals(expected,
                original.calculateDiscount(200, GOLD, false, false), 0.001);

        assertNotEquals(expected,
                mutant.calculateDiscount_M6(200, GOLD, false, false), 0.001);
    }

    @Test
    void killM7_ReturnValueChanged() {
        double expected = -1.0;

        assertEquals(expected,
                original.calculateDiscount(-50, GOLD, true, true), 0.001);

        assertNotEquals(expected,
                mutant.calculateDiscount_M7(-50, GOLD, true, true), 0.001);
    }

    @Test
    void killM8_ArithmeticOperatorChange() {
        double expected = 160.0;

        assertEquals(expected,
                original.calculateDiscount(200, GOLD, false, false), 0.001);

        assertNotEquals(expected,
                mutant.calculateDiscount_M8(200, GOLD, false, false), 0.001);
    }

    @Test
    void killM9_HolidayStatementDeleted() {
        double expected = 154.0;

        assertEquals(expected,
                original.calculateDiscount(200, GOLD, false, true), 0.001);

        assertNotEquals(expected,
                mutant.calculateDiscount_M9(200, GOLD, false, true), 0.001);
    }

    @Test
    void killM10_HolidayConditionNegated() {
        double expected = 160.0;

        assertEquals(expected,
                original.calculateDiscount(200, GOLD, false, false), 0.001);

        assertNotEquals(expected,
                mutant.calculateDiscount_M10(200, GOLD, false, false), 0.001);
    }
}