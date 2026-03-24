package org.example.service;

import org.example.entities.CheckoutResult;
import org.example.entities.OrderStatus;
import org.example.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.example.constants.CustomerTypeEnum.*;
import static org.example.constants.RegionEnum.*;
import static org.example.constants.ResponseEnum.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PairwiseIntegrationTest {

    @Mock
    private OrderService orderService;
    @Mock private ShippingService shippingService;
    @Mock private CustomerRepository customerRepository;

    private CheckoutService checkoutService;

    private static final Long   CUSTOMER_ID = 1L;
    private static final int    QTY         = 3;
    private static final double UNIT_PRICE  = 50.0;
    private static final double SUBTOTAL    = QTY * UNIT_PRICE; // 150.0

    @BeforeEach
    void init() {
        checkoutService = new CheckoutService(
                orderService, shippingService, customerRepository);
    }

    private void happyPath() {

        /* CustomerRepository — 5 stubs */
        lenient().when(customerRepository.getCustomerType(CUSTOMER_ID))
                .thenReturn(GOLD);
        lenient().when(customerRepository.isMember(CUSTOMER_ID))
                .thenReturn(true);
        lenient().when(customerRepository.hasCoupon(CUSTOMER_ID))
                .thenReturn(false);
        lenient().when(customerRepository.getRegion(CUSTOMER_ID))
                .thenReturn(DOMESTIC);
        lenient().when(customerRepository.getCustomerName(CUSTOMER_ID))
                .thenReturn("Alice");

        /* OrderService — 5 stubs */
        lenient().when(orderService.calculateDiscount(
                        eq(SUBTOTAL), any(), anyBoolean(), anyBoolean()))
                .thenReturn(120.0);
        lenient().when(orderService.validateAndProcessOrder(
                        eq(QTY), eq(UNIT_PRICE), any(), anyBoolean()))
                .thenReturn(new OrderStatus(ORDER_CONFIRMED, 170.0));
        lenient().when(orderService.calculateTax(anyDouble(), any()))
                .thenReturn(9.60);
        lenient().when(orderService.getOrderSummary(QTY, UNIT_PRICE))
                .thenReturn("Order: 3 x $50.00 = $150.00");
        lenient().when(orderService.isOrderEligibleForReturn(
                        anyDouble(), eq(0)))
                .thenReturn(true);

        /* ShippingService — 5 stubs */
        lenient().when(shippingService.isDeliveryAvailable(any()))
                .thenReturn(true);
        lenient().when(shippingService.determineShippingCost(
                        anyBoolean(), anyDouble(), anyBoolean()))
                .thenReturn(0.0);
        lenient().when(shippingService.calculateInsurance(anyDouble()))
                .thenReturn(0.0);
        lenient().when(shippingService.estimateDeliveryDays(
                        any(), anyBoolean()))
                .thenReturn(5);
        lenient().when(shippingService.getTrackingPrefix(any()))
                .thenReturn("DOM");
    }
    /** PAIR A:  CheckoutService  ↔  OrderService
     *  (CustomerRepository & ShippingService mocked) */
    @Nested
    @DisplayName("PAIR A — CheckoutService ↔ OrderService")
    class PairA_OrderService {

        @Test @DisplayName("1 ─ GOLD customer receives 20 % discount")
        void goldDiscount() {
            happyPath();
            // GOLD, no coupon, not holiday → 20%  →  150 - 30 = 120
            when(orderService.calculateDiscount(SUBTOTAL, GOLD, false, false))
                    .thenReturn(120.0);

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, false);

            assertEquals(ORDER_CONFIRMED, r.status());
            assertEquals(120.0, r.discountedTotal(), 0.01);
            verify(orderService).calculateDiscount(
                    SUBTOTAL, GOLD, false, false);
        }

        @Test @DisplayName("2 ─ SILVER customer receives 10 % discount")
        void silverDiscount() {
            happyPath();
            when(customerRepository.getCustomerType(CUSTOMER_ID))
                    .thenReturn(SILVER);
            // 10% of 150 = 15  →  135
            when(orderService.calculateDiscount(SUBTOTAL, SILVER, false, false))
                    .thenReturn(135.0);

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, false);

            assertEquals(135.0, r.discountedTotal(), 0.01);
            verify(orderService).calculateDiscount(
                    SUBTOTAL, SILVER, false, false);
        }

        @Test @DisplayName("3 ─ BRONZE customer receives 0 % discount")
        void bronzeDiscount() {
            happyPath();
            when(customerRepository.getCustomerType(CUSTOMER_ID))
                    .thenReturn(BRONZE);
            when(orderService.calculateDiscount(SUBTOTAL, BRONZE, false, false))
                    .thenReturn(150.0);

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, false);

            assertEquals(150.0, r.discountedTotal(), 0.01);
        }

        @Test @DisplayName("4 ─ calculateDiscount returns -1 → checkout fails")
        void discountNegative() {
            happyPath();
            when(orderService.calculateDiscount(
                    anyDouble(), any(), anyBoolean(), anyBoolean()))
                    .thenReturn(-1.0);

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, false);

            assertEquals(INVALID_PRICE, r.status());
            verify(orderService, never()).validateAndProcessOrder(
                    anyInt(), anyDouble(), any(), anyBoolean());
        }

        @Test @DisplayName("5 ─ validateAndProcessOrder → INVALID_QUANTITY")
        void invalidQuantity() {
            happyPath();
            when(orderService.validateAndProcessOrder(
                    eq(QTY), eq(UNIT_PRICE), any(), anyBoolean()))
                    .thenReturn(new OrderStatus(INVALID_QUANTITY, null));

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, false);

            assertEquals(INVALID_QUANTITY, r.status());
            verify(orderService, never()).calculateTax(
                    anyDouble(), any());
        }

        @Test @DisplayName("6 ─ Large order gets ORDER_REQUIRES_APPROVAL")
        void requiresApproval() {
            happyPath();
            when(orderService.validateAndProcessOrder(
                    eq(QTY), eq(UNIT_PRICE), any(), anyBoolean()))
                    .thenReturn(new OrderStatus(
                            ORDER_REQUIRES_APPROVAL, 1020.0));

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, false);

            assertEquals(ORDER_REQUIRES_APPROVAL, r.status());
            // processing should still continue for approval orders
            verify(orderService).calculateTax(anyDouble(), any());
        }

        @Test @DisplayName("7 ─ Holiday + coupon adds extra discount")
        void holidayAndCoupon() {
            happyPath();
            when(customerRepository.hasCoupon(CUSTOMER_ID))
                    .thenReturn(true);
            // GOLD 20% + coupon 5% + holiday 3% = 28%
            // 150 - 42 = 108
            when(orderService.calculateDiscount(
                    SUBTOTAL, GOLD, true, true))
                    .thenReturn(108.0);

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, true);

            assertEquals(108.0, r.discountedTotal(), 0.01);
            verify(orderService).calculateDiscount(
                    SUBTOTAL, GOLD, true, true);
        }
    }

    /** PAIR B:  CheckoutService  ↔  ShippingService
     *  (CustomerRepository & OrderService mocked)*/
    @Nested
    @DisplayName("PAIR B — CheckoutService ↔ ShippingService")
    class PairB_ShippingService {

        @Test @DisplayName("8 ─ Member with order > 100 gets free shipping")
        void memberFreeShipping() {
            happyPath();
            // discountedAmount = 120 > 100, isMember = true → 0.0
            when(shippingService.determineShippingCost(true, 120.0, false))
                    .thenReturn(0.0);

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, false);

            assertEquals(0.0, r.shippingCost(), 0.01);
            verify(shippingService)
                    .determineShippingCost(true, 120.0, false);
        }

        @Test @DisplayName("9 ─ Non-member with order ≤ 100 → $5.99")
        void nonMemberLowOrder() {
            happyPath();
            when(customerRepository.isMember(CUSTOMER_ID))
                    .thenReturn(false);
            when(orderService.calculateDiscount(
                    anyDouble(), any(), anyBoolean(), anyBoolean()))
                    .thenReturn(80.0);
            when(shippingService.determineShippingCost(false, 80.0, false))
                    .thenReturn(5.99);

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, false);

            assertEquals(5.99, r.shippingCost(), 0.01);
        }

        @Test @DisplayName("10 ─ Express adds $9.99 to shipping")
        void expressShipping() {
            happyPath();
            // member + >100 base = 0, + express 9.99
            when(shippingService.determineShippingCost(true, 120.0, true))
                    .thenReturn(9.99);

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, true, false);

            assertEquals(9.99, r.shippingCost(), 0.01);
            verify(shippingService)
                    .determineShippingCost(true, 120.0, true);
        }

        @Test @DisplayName("11 ─ Insurance added for orders > $200")
        void insuranceApplied() {
            happyPath();
            when(orderService.calculateDiscount(
                    anyDouble(), any(), anyBoolean(), anyBoolean()))
                    .thenReturn(250.0);
            // 2% of 250 = 5.0
            when(shippingService.calculateInsurance(250.0))
                    .thenReturn(5.0);

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, false);

            assertEquals(5.0, r.insurance(), 0.01);
            verify(shippingService).calculateInsurance(250.0);
        }

        @Test @DisplayName("12 ─ Delivery unavailable → INVALID_REGION")
        void deliveryUnavailable() {
            happyPath();
            when(customerRepository.getRegion(CUSTOMER_ID))
                    .thenReturn(INVALID);
            when(shippingService.isDeliveryAvailable(INVALID))
                    .thenReturn(false);

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, false);

            assertEquals(INVALID_REGION, r.status());
            verify(shippingService).isDeliveryAvailable(INVALID);
            verify(shippingService, never())
                    .determineShippingCost(
                            anyBoolean(), anyDouble(), anyBoolean());
        }

        @Test @DisplayName("13 ─ Domestic standard → 5 delivery days")
        void domesticStandardDays() {
            happyPath();
            when(shippingService.estimateDeliveryDays(DOMESTIC, false))
                    .thenReturn(5);

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, false);

            assertEquals(5, r.estimatedDeliveryDays());
            verify(shippingService)
                    .estimateDeliveryDays(DOMESTIC, false);
        }

        @Test @DisplayName("14 ─ International express → 11 delivery days")
        void internationalExpressDays() {
            happyPath();
            when(customerRepository.getRegion(CUSTOMER_ID))
                    .thenReturn(INTERNATIONAL);
            when(shippingService.isDeliveryAvailable(INTERNATIONAL))
                    .thenReturn(true);
            when(shippingService.estimateDeliveryDays(
                    INTERNATIONAL, true))
                    .thenReturn(11);
            when(shippingService.getTrackingPrefix(INTERNATIONAL))
                    .thenReturn("INT");

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, true, false);

            assertEquals(11, r.estimatedDeliveryDays());
            assertEquals("INT", r.trackingPrefix());
        }
    }

    /**  PAIR C:  CheckoutService  ↔  CustomerRepository
     *  (OrderService & ShippingService mocked)*/
    @Nested
    @DisplayName("PAIR C — CheckoutService ↔ CustomerRepository")
    class PairC_CustomerRepository {

        @Test @DisplayName("15 ─ GOLD member: all 5 fields retrieved")
        void goldMemberDataFlows() {
            happyPath();

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, false);

            verify(customerRepository).getCustomerType(CUSTOMER_ID);
            verify(customerRepository).isMember(CUSTOMER_ID);
            verify(customerRepository).hasCoupon(CUSTOMER_ID);
            verify(customerRepository).getRegion(CUSTOMER_ID);
            verify(customerRepository).getCustomerName(CUSTOMER_ID);
            assertTrue(r.message().contains("Alice"));
        }

        @Test @DisplayName("16 ─ BRONZE non-member retrieval")
        void bronzeNonMember() {
            happyPath();
            when(customerRepository.getCustomerType(CUSTOMER_ID))
                    .thenReturn(BRONZE);
            when(customerRepository.isMember(CUSTOMER_ID))
                    .thenReturn(false);
            when(customerRepository.getCustomerName(CUSTOMER_ID))
                    .thenReturn("Bob");

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, false);

            assertTrue(r.message().contains("Bob"));
            verify(customerRepository).getCustomerType(CUSTOMER_ID);
            verify(customerRepository).isMember(CUSTOMER_ID);
        }

        @Test @DisplayName("17 ─ Customer coupon flag passed to OrderService")
        void couponFlagPassed() {
            happyPath();
            when(customerRepository.hasCoupon(CUSTOMER_ID))
                    .thenReturn(true);

            checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, false);

            // verify coupon=true was forwarded
            verify(orderService).calculateDiscount(
                    eq(SUBTOTAL), eq(GOLD), eq(true), eq(false));
        }

        @Test @DisplayName("18 ─ INTERNATIONAL region flows to all services")
        void internationalRegionFlows() {
            happyPath();
            when(customerRepository.getRegion(CUSTOMER_ID))
                    .thenReturn(INTERNATIONAL);
            when(shippingService.isDeliveryAvailable(INTERNATIONAL))
                    .thenReturn(true);

            checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, false);

            verify(shippingService).isDeliveryAvailable(INTERNATIONAL);
            verify(orderService).validateAndProcessOrder(
                    QTY, UNIT_PRICE, INTERNATIONAL, false);
            verify(orderService).calculateTax(anyDouble(), eq(INTERNATIONAL));
        }

        @Test @DisplayName("19 ─ INVALID region from repo → early exit")
        void invalidRegionFromRepo() {
            happyPath();
            when(customerRepository.getRegion(CUSTOMER_ID))
                    .thenReturn(INVALID);
            when(shippingService.isDeliveryAvailable(INVALID))
                    .thenReturn(false);

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, false);

            assertEquals(INVALID_REGION, r.status());
            verify(orderService, never()).calculateDiscount(
                    anyDouble(), any(), anyBoolean(), anyBoolean());
        }

        @Test @DisplayName("20 ─ Customer name appears in result message")
        void customerNameInMessage() {
            happyPath();
            when(customerRepository.getCustomerName(CUSTOMER_ID))
                    .thenReturn("Charlie");

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, false);

            assertTrue(r.message().contains("Charlie"));
        }
    }

    @Nested
    @DisplayName("CROSS-PAIR — Full Integration")
    class CrossPair {

        @Test @DisplayName("21 ─ Full happy-path: all services verify")
        void fullHappyPath() {
            happyPath();

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, false, false);

            /*  assert final result  */
            assertEquals(ORDER_CONFIRMED, r.status());
            assertEquals(SUBTOTAL, r.subtotal(), 0.01);
            assertEquals(120.0, r.discountedTotal(), 0.01);
            assertEquals(9.60, r.tax(), 0.01);
            assertEquals(0.0, r.shippingCost(), 0.01);
            assertEquals(5, r.estimatedDeliveryDays());
            assertEquals("DOM", r.trackingPrefix());
            assertNotNull(r.orderSummary());
            assertTrue(r.message().contains("Alice"));

            /* every mock should be called */
            // CustomerRepository (5)
            verify(customerRepository).getCustomerType(CUSTOMER_ID);
            verify(customerRepository).isMember(CUSTOMER_ID);
            verify(customerRepository).hasCoupon(CUSTOMER_ID);
            verify(customerRepository).getRegion(CUSTOMER_ID);
            verify(customerRepository).getCustomerName(CUSTOMER_ID);
            // OrderService (5)
            verify(orderService).calculateDiscount(
                    SUBTOTAL, GOLD, false, false);
            verify(orderService).validateAndProcessOrder(
                    QTY, UNIT_PRICE, DOMESTIC, false);
            verify(orderService).calculateTax(120.0, DOMESTIC);
            verify(orderService).getOrderSummary(QTY, UNIT_PRICE);
            verify(orderService).isOrderEligibleForReturn(
                    anyDouble(), eq(0));
            // ShippingService (5)
            verify(shippingService).isDeliveryAvailable(DOMESTIC);
            verify(shippingService).determineShippingCost(
                    true, 120.0, false);
            verify(shippingService).calculateInsurance(120.0);
            verify(shippingService).estimateDeliveryDays(
                    DOMESTIC, false);
            verify(shippingService).getTrackingPrefix(DOMESTIC);
        }

        @Test @DisplayName(
            "22 ─ GOLD member, intl express, holiday, coupon, "
            + "large order → REQUIRES_APPROVAL")
        void complexScenario() {
            /* CustomerRepository */
            when(customerRepository.getCustomerType(CUSTOMER_ID))
                    .thenReturn(GOLD);
            when(customerRepository.isMember(CUSTOMER_ID))
                    .thenReturn(true);
            when(customerRepository.hasCoupon(CUSTOMER_ID))
                    .thenReturn(true);
            when(customerRepository.getRegion(CUSTOMER_ID))
                    .thenReturn(INTERNATIONAL);
            when(customerRepository.getCustomerName(CUSTOMER_ID))
                    .thenReturn("Diana");

            when(shippingService.isDeliveryAvailable(INTERNATIONAL))
                    .thenReturn(true);
            when(shippingService.determineShippingCost(
                    true, 108.0, true))
                    .thenReturn(9.99);
            when(shippingService.calculateInsurance(108.0))
                    .thenReturn(0.0);
            when(shippingService.estimateDeliveryDays(
                    INTERNATIONAL, true))
                    .thenReturn(11);
            when(shippingService.getTrackingPrefix(INTERNATIONAL))
                    .thenReturn("INT");

            when(orderService.calculateDiscount(
                    SUBTOTAL, GOLD, true, true))
                    .thenReturn(108.0);
            when(orderService.validateAndProcessOrder(
                    QTY, UNIT_PRICE, INTERNATIONAL, true))
                    .thenReturn(new OrderStatus(
                            ORDER_REQUIRES_APPROVAL, 1040.0));
            when(orderService.calculateTax(108.0, INTERNATIONAL))
                    .thenReturn(16.20);       // 15%
            when(orderService.getOrderSummary(QTY, UNIT_PRICE))
                    .thenReturn("Order: 3 x $50.00 = $150.00");
            when(orderService.isOrderEligibleForReturn(
                    anyDouble(), eq(0)))
                    .thenReturn(true);

            CheckoutResult r = checkoutService.checkout(
                    CUSTOMER_ID, QTY, UNIT_PRICE, true, true);

            assertEquals(ORDER_REQUIRES_APPROVAL, r.status());
            assertEquals(SUBTOTAL, r.subtotal(), 0.01);
            assertEquals(108.0, r.discountedTotal(), 0.01);
            assertEquals(16.20, r.tax(), 0.01);
            assertEquals(9.99, r.shippingCost(), 0.01);
            assertEquals(0.0, r.insurance(), 0.01);
            // finalTotal = 108 + 16.20 + 9.99 + 0 = 134.19
            assertEquals(134.19, r.finalTotal(), 0.01);
            assertEquals(11, r.estimatedDeliveryDays());
            assertEquals("INT", r.trackingPrefix());
            assertTrue(r.returnable());
            assertTrue(r.message().contains("Diana"));
        }
    }
}