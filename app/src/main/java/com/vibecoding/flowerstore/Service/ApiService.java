package com.vibecoding.flowerstore.Service;

import com.vibecoding.flowerstore.Model.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    /**
     * Lấy danh sách sản phẩm với đầy đủ các tùy chọn lọc và sắp xếp.
     * @param query Từ khóa tìm kiếm (có thể rỗng)
     * @param page Số trang (có thể rỗng)
     * @param size Số lượng trên trang (có thể rỗng)
     * @param sortBy Cách sắp xếp
     * @return Call<ApiResponse>
     */
    @GET("api/v1/products")
    Call<ApiResponse> getProducts(
            @Query("query") String query,
            @Query("page") String page,  // Dùng String để có thể truyền rỗng
            @Query("size") String size,  // Dùng String để có thể truyền rỗng
            @Query("sort") String sortBy
    );
}
