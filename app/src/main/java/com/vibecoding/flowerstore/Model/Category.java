package com.vibecoding.flowerstore.Model;

/**
 * Lớp này đại diện cho đối tượng "category" trong dữ liệu JSON trả về từ API.
 */
public class Category {
    private int id;
    private String name;
    private String slug;

    // --- Getters ---

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    // --- Setters (tùy chọn, không bắt buộc cho việc đọc dữ liệu) ---

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
