package com.techov8.retaildost;

import java.util.Date;

public class OrderItemModel {

    private String OrderId,OrderStatus,TotalAmount,PaymentMode,DeliveryDate;
    private Date OrderedDate;

    public OrderItemModel(String orderId, String orderStatus, String totalAmount, String paymentMode, Date orderedDate, String deliveryDate) {
        OrderId = orderId;
        OrderStatus = orderStatus;
        TotalAmount = totalAmount;
        PaymentMode = paymentMode;
        OrderedDate = orderedDate;
        DeliveryDate = deliveryDate;
    }

    public String getOrderId() {
        return OrderId;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getTotalAmount() {
        return TotalAmount;
    }

    public String getPaymentMode() {
        return PaymentMode;
    }

    public Date getOrderedDate() {
        return OrderedDate;
    }

    public String getDeliveryDate() {
        return DeliveryDate;
    }
}
