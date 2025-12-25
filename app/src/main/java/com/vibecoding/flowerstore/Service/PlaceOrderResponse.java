package com.vibecoding.flowerstore.Service;

public class PlaceOrderResponse {
    private int orderId;
    private String status;
    private double totalAmount;
    private String message;
    private String paymentUrl;

    public int getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public double getTotalAmount() { return totalAmount; }
    public String getMessage() { return message; }
    public String getPaymentUrl() { return paymentUrl; }
}
