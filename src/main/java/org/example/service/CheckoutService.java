package org.example.service;

import org.example.constants.CustomerTypeEnum;
import org.example.constants.RegionEnum;
import org.example.constants.ResponseEnum;
import org.example.entities.CheckoutResult;
import org.example.entities.OrderStatus;
import org.example.repository.CustomerRepository;

import static org.example.constants.ResponseEnum.*;

/**
 * Orchestrator that ties together OrderService,
 * ShippingService and CustomerRepository.
 */
public class CheckoutService {

    private final OrderService orderService;
    private final ShippingService shippingService;
    private final CustomerRepository customerRepository;

    public CheckoutService(OrderService orderService,
                           ShippingService shippingService,
                           CustomerRepository customerRepository) {
        this.orderService = orderService;
        this.shippingService = shippingService;
        this.customerRepository = customerRepository;
    }

    public CheckoutResult checkout(Long customerId,
                                   int quantity,
                                   double unitPrice,
                                   boolean isExpress,
                                   boolean isHoliday) {

        CustomerTypeEnum customerType =
                customerRepository.getCustomerType(customerId);
        boolean isMember  = customerRepository.isMember(customerId);
        boolean hasCoupon = customerRepository.hasCoupon(customerId);
        RegionEnum region = customerRepository.getRegion(customerId);
        String name       = customerRepository.getCustomerName(customerId);

        if (!shippingService.isDeliveryAvailable(region)) {
            return errorResult(INVALID_REGION,
                    "Delivery unavailable for region");
        }

        double subtotal = quantity * unitPrice;

        double discountedAmount = orderService.calculateDiscount(
                subtotal, customerType, hasCoupon, isHoliday);
        if (discountedAmount < 0) {
            return errorResult(INVALID_PRICE,
                    "Invalid amount for discount calculation");
        }

        OrderStatus orderStatus = orderService
                .validateAndProcessOrder(
                        quantity, unitPrice, region, isExpress);

        if (orderStatus.responseEnum() != ORDER_CONFIRMED
            && orderStatus.responseEnum() != ORDER_REQUIRES_APPROVAL) {
            return errorResult(orderStatus.responseEnum(),
                    "Order validation failed: "
                    + orderStatus.responseEnum());
        }

        double tax = orderService.calculateTax(
                discountedAmount, region);

        String summary = orderService.getOrderSummary(
                quantity, unitPrice);

        double shippingCost = shippingService
                .determineShippingCost(
                        isMember, discountedAmount, isExpress);

        double insurance = shippingService
                .calculateInsurance(discountedAmount);

        int deliveryDays = shippingService
                .estimateDeliveryDays(region, isExpress);

        String prefix = shippingService.getTrackingPrefix(region);

        double finalTotal = discountedAmount + tax
                            + shippingCost + insurance;

        boolean returnable = orderService
                .isOrderEligibleForReturn(finalTotal, 0);

        return new CheckoutResult(
                orderStatus.responseEnum(),
                subtotal,
                discountedAmount,
                tax,
                shippingCost,
                insurance,
                finalTotal,
                deliveryDays,
                prefix,
                summary,
                returnable,
                "Checkout successful for " + name
        );
    }

    private CheckoutResult errorResult(ResponseEnum status,
                                       String message) {
        return new CheckoutResult(
                status, 0, 0, 0, 0, 0, 0,
                0, null, null, false, message);
    }
}