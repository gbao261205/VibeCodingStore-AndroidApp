package com.vibecoding.flowerstore.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager; // Thêm import này

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.vibecoding.flowerstore.Adapter.ProductAdapter;
import com.vibecoding.flowerstore.Model.ApiResponse;
import com.vibecoding.flowerstore.Model.Product;
import com.vibecoding.flowerstore.R;
import com.vibecoding.flowerstore.Service.ApiClient;
import com.vibecoding.flowerstore.Service.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    // Sửa: Chỉ cần một RecyclerView và một Adapter
    private RecyclerView recyclerProducts;
    private ProductAdapter productAdapter;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Giả sử layout chính của bạn có RecyclerView với id recycler_products

        setupViews();
        setupRecyclerView();
        fetchProductsFromApi();
    }

    private void setupViews() {
        // Ánh xạ RecyclerView từ layout activity_main.xml
        recyclerProducts = findViewById(R.id.recycler_products); // <-- Đảm bảo id này tồn tại trong activity_main.xml
    }

    private void setupRecyclerView() {
        // Khởi tạo Adapter với danh sách rỗng ban đầu
        productAdapter = new ProductAdapter(new ArrayList<>(), this);

        // Sử dụng GridLayoutManager để hiển thị 2 cột
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerProducts.setLayoutManager(layoutManager);

        recyclerProducts.setAdapter(productAdapter);
    }

    private void fetchProductsFromApi() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // --- BẮT ĐẦU THAY ĐỔI ---

        // Định nghĩa các tham số cho API
        String query = "";
        String page = "";
        String size = "";
        String sortBy = "bestselling";

        // Gọi API với đầy đủ các tham số được yêu cầu
        Call<ApiResponse> call = apiService.getProducts(query, page, size, sortBy);

        // --- KẾT THÚC THAY ĐỔI ---

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> bestsellingProducts = response.body().getProducts();

                    if (bestsellingProducts != null && !bestsellingProducts.isEmpty()) {
                        productAdapter.updateData(bestsellingProducts);
                        Log.d(TAG, "Tải và hiển thị thành công " + bestsellingProducts.size() + " sản phẩm nổi bật.");
                    } else {
                        Log.d(TAG, "API không trả về sản phẩm nào cho query 'sort=" + sortBy + "'.");
                        Toast.makeText(MainActivity.this, "Hiện không có sản phẩm nổi bật nào", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Log thêm body của lỗi để dễ debug
                    try {
                        Log.e(TAG, "Lỗi API: " + response.code() + " - " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi khi đọc errorBody", e);
                    }
                    Toast.makeText(MainActivity.this, "Lỗi khi tải dữ liệu từ server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Lỗi kết nối mạng, vui lòng thử lại", Toast.LENGTH_LONG).show();
            }
        });
    }
}
