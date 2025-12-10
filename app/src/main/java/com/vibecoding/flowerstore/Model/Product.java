package com.vibecoding.flowerstore.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Lớp này đại diện cho một đối tượng "Product" (Sản phẩm).
 * Cấu trúc của lớp này khớp với cấu trúc của một sản phẩm trong mảng "content" từ API.
 */
public class Product {

    private int id;
    private String name;

    @SerializedName("imageUrl")
    private String image;

    private double price;
    private double discountedPrice;
    private int stock;
    private boolean active;


    private Shop shop;


    private Category category;



    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public double getPrice() {
        return price;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public int getStock() {
        return stock;
    }

    public boolean isActive() {
        return active;
    }

    public Shop getShop() {
        return shop;
    }

    public Category getCategory() {
        return category;
    }

    // --- Setters (Không bắt buộc nếu bạn chỉ đọc dữ liệu từ API) ---

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
