package com.vibecoding.flowerstore.Model;

import java.math.BigDecimal;

// 1. ShippingCarrierDTO
public class ShippingCarrierDTO {
    private Long id;
    private String name;
    private String phone;
    private String website;
    private BigDecimal shippingFee;
    private boolean isActive;

    // Getters/Setters rút gọn
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }
}