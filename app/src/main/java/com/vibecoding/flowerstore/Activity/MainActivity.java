package com.vibecoding.flowerstore.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager; // Thêm import này
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vibecoding.flowerstore.Adapter.ProductAdapter;
import com.vibecoding.flowerstore.Adapter.SlideAdapter;
import com.vibecoding.flowerstore.Model.ApiResponse;
import com.vibecoding.flowerstore.Model.Product;
import com.vibecoding.flowerstore.Model.Category;
import com.vibecoding.flowerstore.Adapter.CategoryAdapter;
import com.vibecoding.flowerstore.Model.SlideItem;
import com.vibecoding.flowerstore.R;
import com.vibecoding.flowerstore.Service.ApiClient;
import com.vibecoding.flowerstore.Service.ApiService;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    // Sửa: Chỉ cần một RecyclerView và một Adapter
    private RecyclerView recyclerProducts;
    private ProductAdapter productAdapter;
    private RecyclerView recyclerCategories;
    private CategoryAdapter categoryAdapter;

    private ViewPager2 viewPagerImageSlider;
    private LinearLayout bannerDotsLayout; // Layout chứa các dấu chấm
    private SlideAdapter slideAdapter;
    private final Handler sliderHandler = new Handler(Looper.getMainLooper())   ;
    private Runnable sliderRunnable;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Giả sử layout chính của bạn có RecyclerView với id recycler_products

        setupViews();
        setupImageSliderAndDots();
        setupRecyclerView();
        fetchProductsFromApi();
        fetchCategoriesFromApi();
    }

    private void setupViews() {
        // Ánh xạ RecyclerView từ layout activity_main.xml
        viewPagerImageSlider = findViewById(R.id.view_pager_image_slider);
        bannerDotsLayout = findViewById(R.id.banner_dots); // Ánh xạ layout cho dots
        recyclerProducts = findViewById(R.id.recycler_products);
        recyclerCategories = findViewById(R.id.recycler_categories); // <-- THÊM DÒNG NÀY// <-- Đảm bảo id này tồn tại trong activity_main.xml
    }

    private void setupImageSliderAndDots() {
        List<SlideItem> slideItems = new ArrayList<>();
        // THAY THẾ bằng các ảnh của bạn trong /res/drawable
        // Ví dụ:
        slideItems.add(new SlideItem(R.drawable.banner1));
        slideItems.add(new SlideItem(R.drawable.banner2));
        slideItems.add(new SlideItem(R.drawable.banner3));

        slideAdapter = new SlideAdapter(slideItems);
        viewPagerImageSlider.setAdapter(slideAdapter);

        // Thiết lập hiệu ứng chuyển slide (phóng to/thu nhỏ)
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        viewPagerImageSlider.setPageTransformer(transformer);

        // **BẮT ĐẦU: Logic cho Dots**
        setupBannerDots(slideItems.size()); // Tạo các dấu chấm

        viewPagerImageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateBannerDots(position); // Cập nhật trạng thái chấm khi slide thay đổi
                // Reset timer mỗi khi người dùng tự vuốt
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000); // Tự động chuyển sau 3 giây
            }
        });
        // **KẾT THÚC: Logic cho Dots**

        // Logic tự động chuyển slide
        sliderRunnable = () -> {
            int currentItem = viewPagerImageSlider.getCurrentItem();
            if (currentItem == slideItems.size() - 1) {
                viewPagerImageSlider.setCurrentItem(0, true); // Quay về slide đầu
            } else {
                viewPagerImageSlider.setCurrentItem(currentItem + 1);
            }
        };
    }

    private void setupBannerDots(int count) {
        ImageView[] dots = new ImageView[count];
        bannerDotsLayout.removeAllViews(); // Xóa các dots cũ nếu có

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_inactive)); // Sử dụng drawable cho chấm không active

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0); // Khoảng cách giữa các chấm
            bannerDotsLayout.addView(dots[i], params);
        }

        // Kích hoạt dot đầu tiên
        if (dots.length > 0) {
            dots[0].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_active)); // Sử dụng drawable cho chấm active
        }
    }

    // --- HÀM CẬP NHẬT TRẠNG THÁI DOTS KHI SLIDE THAY ĐỔI ---
    private void updateBannerDots(int currentPage) {
        int childCount = bannerDotsLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) bannerDotsLayout.getChildAt(i);
            if (i == currentPage) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_inactive));
            }
        }
    }

    // --- Quản lý vòng đời của Handler để tránh rò rỉ bộ nhớ ---
    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Bắt đầu chạy slider sau một khoảng trễ
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    private void setupRecyclerView() {
        // Khởi tạo Adapter với danh sách rỗng ban đầu
        productAdapter = new ProductAdapter(new ArrayList<>(), this);

        // Sử dụng GridLayoutManager để hiển thị 2 cột
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerProducts.setLayoutManager(layoutManager);

        recyclerProducts.setAdapter(productAdapter);

        // --- BẮT ĐẦU THÊM: Setup cho Category RecyclerView ---
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), this);
        // LinearLayoutManager với chiều ngang đã được set trong XML, nhưng set ở đây cũng được
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerCategories.setLayoutManager(categoryLayoutManager);
        recyclerCategories.setAdapter(categoryAdapter);
        // --- KẾT THÚC THÊM ---
    }
    private void fetchCategoriesFromApi() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Category>> call = apiService.getCategories();

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    if (!categories.isEmpty()) {
                        categoryAdapter.updateData(categories);
                        Log.d(TAG, "Tải thành công " + categories.size() + " danh mục.");
                    } else {
                        Log.d(TAG, "API không trả về danh mục nào.");
                    }
                } else {
                    Log.e(TAG, "Lỗi API Categories: " + response.code());
                    Toast.makeText(MainActivity.this, "Lỗi tải danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi lấy danh mục: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Lỗi mạng khi tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });
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
