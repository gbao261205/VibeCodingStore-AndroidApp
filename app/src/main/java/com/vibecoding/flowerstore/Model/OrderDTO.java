package com.vibecoding.flowerstore.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime; // Hoặc dùng String nếu không cấu hình Gson Adapter
import java.util.List;

public class OrderDTO {
    private Integer id;
    private UserDTO user;
    private String shippingAddress;
    private String shippingPhone;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String status;
    private String notes;
    private UserDTO shipper;
    private ShippingCarrierDTO shippingCarrier;
    private PromotionDTO promotion;
    private String discountCode;
    private BigDecimal discountAmount;
    private String createdAt; // JSON trả về chuỗi ISO-8601 (ví dụ: "2024-01-01T14:30:00")
    private List<OrderDetailDTO> orderDetails;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getShippingPhone() { return shippingPhone; }
    public void setShippingPhone(String shippingPhone) { this.shippingPhone = shippingPhone; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public UserDTO getShipper() { return shipper; }
    public void setShipper(UserDTO shipper) { this.shipper = shipper; }

    public ShippingCarrierDTO getShippingCarrier() { return shippingCarrier; }
    public void setShippingCarrier(ShippingCarrierDTO shippingCarrier) { this.shippingCarrier = shippingCarrier; }

    public PromotionDTO getPromotion() { return promotion; }
    public void setPromotion(PromotionDTO promotion) { this.promotion = promotion; }

    public String getDiscountCode() { return discountCode; }
    public void setDiscountCode(String discountCode) { this.discountCode = discountCode; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<OrderDetailDTO> getOrderDetails() { return orderDetails; }
    public void setOrderDetails(List<OrderDetailDTO> orderDetails) { this.orderDetails = orderDetails; }
}