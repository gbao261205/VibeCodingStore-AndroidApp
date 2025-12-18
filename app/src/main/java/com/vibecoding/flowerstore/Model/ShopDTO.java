package com.vibecoding.flowerstore.Model;

// 3. ShopDTO
public class ShopDTO {
    private Integer id;
    private String name;
    private boolean active;

    // Getters/Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}