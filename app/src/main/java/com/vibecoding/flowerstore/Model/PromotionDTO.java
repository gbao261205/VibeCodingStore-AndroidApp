package com.vibecoding.flowerstore.Model;

import java.math.BigDecimal;

// 2. PromotionDTO
public class PromotionDTO {
    private Integer id;
    private String code;
    private String description;
    private double discountPercentage;
    private BigDecimal maxDiscountAmount;
    private String startDate; // Dùng String hoặc LocalDate
    private String endDate;
    private boolean isActive;
    private ShopDTO shop;

    // Getters/Setters rút gọn
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}