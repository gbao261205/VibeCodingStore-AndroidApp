package com.vibecoding.flowerstore.Model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Lớp này đại diện cho cấu trúc tổng thể của phản hồi từ API.
 */
public class ApiResponse {

    // --- SỬA LẠI TẠI ĐÂY ---
    // Key trong JSON thực tế là "products", không phải "content".
    @SerializedName("products")
    private List<Product> products;

    // --- CÁC TRƯỜNG KHÁC GIỮ NGUYÊN ---
    @SerializedName("currentPage")
    private int currentPage;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("totalElements")
    private int totalElements;

    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("currentCategorySlug")
    private String currentCategorySlug;

    @SerializedName("currentSortBy")
    private String currentSortBy;


    // --- Getters ---
    public List<Product> getProducts() {
        return products;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public int getPageSize() {
        return pageSize;
    }

    public String getCurrentCategorySlug() {
        return currentCategorySlug;
    }

    public String getCurrentSortBy() {
        return currentSortBy;
    }
}
