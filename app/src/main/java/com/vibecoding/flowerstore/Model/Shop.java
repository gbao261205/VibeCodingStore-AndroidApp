package com.vibecoding.flowerstore.Model;

/**
 * Lớp này đại diện cho đối tượng "shop" trong dữ liệu JSON trả về từ API.
 */
public class Shop {
    private int id;
    private String name;
    private boolean active;

    // --- Getters ---

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    // --- Setters (tùy chọn, không bắt buộc cho việc đọc dữ liệu) ---

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
