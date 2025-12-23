package com.vibecoding.flowerstore.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.vibecoding.flowerstore.Model.CartDTO;
import com.vibecoding.flowerstore.Model.DataStore;
import com.vibecoding.flowerstore.Model.Product;
import com.vibecoding.flowerstore.R;
import com.vibecoding.flowerstore.Service.ApiService;
import com.vibecoding.flowerstore.Service.RetrofitClient;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imgProduct, btnBack, btnDecrease, btnIncrease;
    private TextView tvName, tvPrice, tvSupplier, tvDescription, tvQuantity, tvStockStatus;
    private Button btnAddToCart;

    private Product currentProduct;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title not the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//int flag, int mask
        setContentView(R.layout.activity_product_detail);

        initViews();
        loadProductData();
        setupEvents();
    }

    private void initViews() {
        imgProduct = findViewById(R.id.img_detail_product);
        btnBack = findViewById(R.id.btn_back);
        tvName = findViewById(R.id.tv_detail_name);
        tvPrice = findViewById(R.id.tv_detail_price);
        tvSupplier = findViewById(R.id.tv_supplier);
        tvDescription = findViewById(R.id.tv_detail_description);
        tvStockStatus = findViewById(R.id.tv_stock_status);

        // Phần số lượng
        btnDecrease = findViewById(R.id.btn_decrease);
        btnIncrease = findViewById(R.id.btn_increase);
        tvQuantity = findViewById(R.id.tv_quantity);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
    }

    private void loadProductData() {
        // Lấy ID sản phẩm từ Intent
        int productId = getIntent().getIntExtra("product_id", -1);

        if (productId != -1 && DataStore.cachedProducts != null) {
            // Tìm sản phẩm trong Cache
            for (Product p : DataStore.cachedProducts) {
                if (p.getId() == productId) {
                    currentProduct = p;
                    break;
                }
            }
        }

        // Hiển thị dữ liệu lên màn hình
        if (currentProduct != null) {
            // 1. Tên sản phẩm
            tvName.setText(currentProduct.getName());

            // 2. Giá tiền
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvPrice.setText(formatter.format(currentProduct.getPrice()));

            // 3. Hình ảnh
            String imageUrl = currentProduct.getImage();
            if (imageUrl != null && imageUrl.startsWith("http")) {
                Glide.with(this).load(imageUrl).into(imgProduct);
            } else {
                int resId = getResources().getIdentifier(imageUrl != null ? imageUrl.replace("-", "_") : "banner1", "drawable", getPackageName());
                imgProduct.setImageResource(resId != 0 ? resId : R.drawable.banner1);
            }

            // --- CÁC PHẦN MỚI CẬP NHẬT DƯỚI ĐÂY ---

            // 4. Nhà cung cấp (Shop)
            if (currentProduct.getShop() != null) {
                tvSupplier.setText("Cung cấp bởi: " + currentProduct.getShop().getName());
            } else {
                tvSupplier.setText("Cung cấp bởi: StarShop"); // Giá trị mặc định
            }

            // 5. Tình trạng kho (Stock)
            int stock = currentProduct.getStock();
            if (stock > 0) {
                tvStockStatus.setText("Tình trạng: Còn hàng (" + stock + " sản phẩm)");
                tvStockStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Màu xanh lá

                // Mở khóa nút mua/tăng giảm
                btnAddToCart.setEnabled(true);
                btnAddToCart.setAlpha(1.0f);
            } else {
                tvStockStatus.setText("Tình trạng: Hết hàng");
                tvStockStatus.setTextColor(android.graphics.Color.RED); // Màu đỏ

                // Khóa nút mua nếu hết hàng
                btnAddToCart.setEnabled(false);
                btnAddToCart.setAlpha(0.5f); // Làm mờ nút
            }

            // 6. Mô tả (Vì API không có trường description, ta tự tạo chuỗi mô tả)
            String categoryName = (currentProduct.getCategory() != null) ? currentProduct.getCategory().getName() : "Hoa tươi";
            String shopName = (currentProduct.getShop() != null) ? currentProduct.getShop().getName() : "Cửa hàng";

            String fakeDescription = "Sản phẩm " + currentProduct.getName() + " chất lượng cao.\n" +
                    "Thuộc danh mục: " + categoryName + ".\n" +
                    "Được phân phối chính hãng bởi " + shopName + ".\n" +
                    "Cam kết hoa tươi, giao hàng nhanh chóng trong ngày.";

            tvDescription.setText(fakeDescription);

        } else {
            Toast.makeText(this, "Không tìm thấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        // Giảm số lượng
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        // Tăng số lượng
        btnIncrease.setOnClickListener(v -> {
            if (currentProduct != null && quantity < currentProduct.getStock()) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(this, "Số lượng đã đạt giới hạn kho", Toast.LENGTH_SHORT).show();
            }
        });

        // Thêm vào giỏ
        btnAddToCart.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE);
            String token = prefs.getString("ACCESS_TOKEN", null);

            if (token != null) {
                addToCart("Bearer " + token);
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProductDetailActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addToCart(String authToken) {
        if (currentProduct == null) return;

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<CartDTO> call = apiService.addToCart(authToken, currentProduct.getId(), quantity);

        call.enqueue(new Callback<CartDTO>() {
            @Override
            public void onResponse(Call<CartDTO> call, Response<CartDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Lỗi thêm giỏ hàng: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartDTO> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
