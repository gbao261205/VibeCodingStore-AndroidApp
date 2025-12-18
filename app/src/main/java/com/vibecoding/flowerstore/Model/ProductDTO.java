package com.vibecoding.flowerstore.Model;

import java.math.BigDecimal;

public class ProductDTO {
    private Integer id;
    private String name;
    private String imageUrl; // <-- Trường ảnh nằm ở đây
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private Integer stock;
    private boolean active;
    private ShopDTO shop;
    private CategoryDTO category;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(BigDecimal discountedPrice) { this.discountedPrice = discountedPrice; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public ShopDTO getShop() { return shop; }
    public void setShop(ShopDTO shop) { this.shop = shop; }

    public CategoryDTO getCategory() { return category; }
    public void setCategory(CategoryDTO category) { this.category = category; }
}