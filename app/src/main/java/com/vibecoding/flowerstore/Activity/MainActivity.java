package com.vibecoding.flowerstore.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo; // Import cho bàn phím
import android.view.inputmethod.InputMethodManager; // Import để ẩn bàn phím
import android.widget.EditText; // Import EditText
import android.widget.ImageButton;
import android.widget.ImageView;
import android.view.View;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import com.vibecoding.flowerstore.Adapter.ProductAdapter;
import com.vibecoding.flowerstore.Adapter.SlideAdapter;
import com.vibecoding.flowerstore.Model.DataStore;
import com.vibecoding.flowerstore.Model.Product;
import com.vibecoding.flowerstore.Model.ProductDTO;
import com.vibecoding.flowerstore.Model.Category;
import com.vibecoding.flowerstore.Adapter.CategoryAdapter;
import com.vibecoding.flowerstore.Model.SlideItem;
import com.vibecoding.flowerstore.R;
import com.vibecoding.flowerstore.Service.ApiService;
import com.vibecoding.flowerstore.Service.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.os.Handler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // --- UI COMPONENTS ---
    private RecyclerView recyclerProducts;
    private ProductAdapter productAdapter;
    private RecyclerView recyclerCategories;
    private CategoryAdapter categoryAdapter;

    private ViewPager2 viewPagerImageSlider;
    private LinearLayout bannerDotsLayout;
    private SlideAdapter slideAdapter;
    private final Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;

    private ShimmerFrameLayout shimmerFrameLayout;

    // Bottom Navigation
    private LinearLayout navHome;
    private LinearLayout navCategories;
    private LinearLayout navFavorites;
    private LinearLayout navAccount;
    private ImageButton cartButton;

    // --- SEARCH COMPONENTS (MỚI) ---
    private EditText edtSearch;
    private View btnSearchIcon; // Có thể là ImageView hoặc LinearLayout tùy XML của bạn

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cấu hình Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // 1. Khởi tạo View
        setupViews();

        // 2. Cài đặt các thành phần UI
        setupImageSliderAndDots();
        setupRecyclerView();
        setupNavBarListeners();

        // 3. Cài đặt chức năng Tìm kiếm (MỚI)
        setupSearchFunctionality();

        // 4. Logic Categories (Cache trước -> API sau)
        fetchCategoriesFromApi();

        // 5. Logic Products (Cache trước -> Shimmer/API sau)
        if (DataStore.cachedProducts != null && !DataStore.cachedProducts.isEmpty()) {
            productAdapter.updateData(DataStore.cachedProducts);
            stopShimmer();
        } else {
            startShimmer();
        }

        // Luôn gọi API để làm mới dữ liệu sản phẩm
        fetchHomeProductsMixed();
    }

    private void setupViews() {
        viewPagerImageSlider = findViewById(R.id.view_pager_image_slider);
        bannerDotsLayout = findViewById(R.id.banner_dots);
        recyclerProducts = findViewById(R.id.recycler_products);
        recyclerCategories = findViewById(R.id.recycler_categories);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);

        navHome = findViewById(R.id.nav_home);
        navCategories = findViewById(R.id.nav_categories);
        navFavorites = findViewById(R.id.nav_favorites);
        navAccount = findViewById(R.id.nav_account);
        cartButton = findViewById(R.id.button_cart);

        // Ánh xạ View tìm kiếm
        edtSearch = findViewById(R.id.edt_search);
        // Lưu ý: Trong XML bạn cần đặt ID cho cái nút kính lúp là btn_search_icon
        btnSearchIcon = findViewById(R.id.btn_search_icon);
    }

    // --- CHỨC NĂNG TÌM KIẾM (MỚI) ---
    private void setupSearchFunctionality() {
        // 1. Xử lý khi ấn nút Enter trên bàn phím ảo
        if (edtSearch != null) {
            edtSearch.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    navigateToSearch();
                    return true;
                }
                return false;
            });
        }

        // 2. Xử lý khi ấn vào nút Icon Kính Lúp trên giao diện
        if (btnSearchIcon != null) {
            btnSearchIcon.setOnClickListener(v -> navigateToSearch());
        }
    }

    // Hàm chung để chuyển sang màn hình Search
    private void navigateToSearch() {
        String keyword = edtSearch.getText().toString().trim();
        if (!keyword.isEmpty()) {
            // Ẩn bàn phím
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
            }

            // Chuyển trang
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            intent.putExtra("SEARCH_KEYWORD", keyword);
            startActivity(intent);

            // Xóa text để lần sau quay lại nhìn cho sạch (tùy chọn)
            edtSearch.setText("");
        } else {
            Toast.makeText(this, "Vui lòng nhập tên sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    // --- SETUP RECYCLERVIEWS ---
    private void setupRecyclerView() {
        // A. Setup Product Adapter
        productAdapter = new ProductAdapter(new ArrayList<>(), this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerProducts.setLayoutManager(layoutManager);
        recyclerProducts.setAdapter(productAdapter);

        // B. Setup Category Adapter (Kèm sự kiện Click)
        // Lưu ý: CategoryAdapter cần constructor có Listener như mình đã hướng dẫn
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), this, new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category) {
                // Chuyển sang trang CategoryProductsActivity
                Intent intent = new Intent(MainActivity.this, CategoryProductsActivity.class);
                intent.putExtra("category_slug", category.getSlug());
                intent.putExtra("category_name", category.getName());
                startActivity(intent);
            }
        });

        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerCategories.setLayoutManager(categoryLayoutManager);
        recyclerCategories.setAdapter(categoryAdapter);
    }

    // --- CÁC HÀM API & LOGIC DỮ LIỆU (GIỮ NGUYÊN) ---
    private void startShimmer() {
        if (shimmerFrameLayout != null) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.startShimmer();
            recyclerProducts.setVisibility(View.GONE);
        }
    }

    private void stopShimmer() {
        if (shimmerFrameLayout != null) {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            recyclerProducts.setVisibility(View.VISIBLE);
        }
    }

    private void fetchHomeProductsMixed() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Map<String, List<ProductDTO>>> call = apiService.getHomeProducts();

        call.enqueue(new Callback<Map<String, List<ProductDTO>>>() {
            @Override
            public void onResponse(Call<Map<String, List<ProductDTO>>> call, Response<Map<String, List<ProductDTO>>> response) {
                stopShimmer();
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, List<ProductDTO>> mapData = response.body();
                    List<Product> allProducts = new ArrayList<>();

                    for (List<ProductDTO> dtoList : mapData.values()) {
                        for (ProductDTO dto : dtoList) {
                            Product p = new Product();
                            p.setId(dto.getId());
                            p.setName(dto.getName());
                            p.setImage(dto.getImageUrl());
                            if (dto.getPrice() != null) p.setPrice(dto.getPrice().doubleValue());
                            if (dto.getDiscountedPrice() != null) p.setDiscountedPrice(dto.getDiscountedPrice().doubleValue());
                            p.setStock(dto.getStock());
                            p.setActive(dto.isActive());
                            allProducts.add(p);
                        }
                    }

                    if (!allProducts.isEmpty()) {
                        productAdapter.updateData(allProducts);
                        DataStore.cachedProducts = allProducts;
                    }
                } else {
                    if (DataStore.cachedProducts == null || DataStore.cachedProducts.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Không tải được dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<Map<String, List<ProductDTO>>> call, Throwable t) {
                stopShimmer();
            }
        });
    }

    private void fetchCategoriesFromApi() {
        if (DataStore.cachedCategories != null && !DataStore.cachedCategories.isEmpty()) {
            categoryAdapter.updateData(DataStore.cachedCategories);
            return;
        }
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Category>> call = apiService.getCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    if (!categories.isEmpty()) {
                        DataStore.cachedCategories = categories;
                        categoryAdapter.updateData(categories);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e(TAG, "Lỗi Categories: " + t.getMessage());
            }
        });
    }

    // --- NAVIGATION & SLIDER ---
    private void setupNavBarListeners() {
        navHome.setOnClickListener(this);
        navCategories.setOnClickListener(this);
        navFavorites.setOnClickListener(this);
        navAccount.setOnClickListener(this);
        cartButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;

        if (id == R.id.button_cart) {
            SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE);
            String token = prefs.getString("ACCESS_TOKEN", null);
            if (token == null) {
                Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            } else {
                intent = new Intent(this, CartActivity.class);
                startActivity(intent);
            }
            return;
        }

        if (id == R.id.nav_home) return;
        else if (id == R.id.nav_categories) intent = new Intent(this, CategoriesActivity.class);
        else if (id == R.id.nav_favorites) intent = new Intent(this, FavoriteActivity.class);
        else if (id == R.id.nav_account) intent = new Intent(this, ProfileActivity.class);

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }

    private void setupImageSliderAndDots() {
        List<SlideItem> slideItems = new ArrayList<>();
        slideItems.add(new SlideItem(R.drawable.banner1));
        slideItems.add(new SlideItem(R.drawable.banner2));
        slideItems.add(new SlideItem(R.drawable.banner3));

        slideAdapter = new SlideAdapter(slideItems);
        viewPagerImageSlider.setAdapter(slideAdapter);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        viewPagerImageSlider.setPageTransformer(transformer);
        setupBannerDots(slideItems.size());

        viewPagerImageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateBannerDots(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });

        sliderRunnable = () -> {
            int currentItem = viewPagerImageSlider.getCurrentItem();
            if (currentItem == slideItems.size() - 1) viewPagerImageSlider.setCurrentItem(0, true);
            else viewPagerImageSlider.setCurrentItem(currentItem + 1);
        };
    }

    private void setupBannerDots(int count) {
        ImageView[] dots = new ImageView[count];
        bannerDotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_inactive));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            bannerDotsLayout.addView(dots[i], params);
        }
        if (dots.length > 0) dots[0].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_active));
    }

    private void updateBannerDots(int currentPage) {
        int childCount = bannerDotsLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) bannerDotsLayout.getChildAt(i);
            if (i == currentPage) imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_active));
            else imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_inactive));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }
}