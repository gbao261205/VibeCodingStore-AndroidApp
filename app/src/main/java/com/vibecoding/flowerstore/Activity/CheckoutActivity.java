package com.vibecoding.flowerstore.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vibecoding.flowerstore.Adapter.CheckoutAdapter;
import com.vibecoding.flowerstore.Model.AddressDTO;
import com.vibecoding.flowerstore.Model.CartDTO;
import com.vibecoding.flowerstore.Service.CheckoutDetailsResponse;
import com.vibecoding.flowerstore.Service.PlaceOrderRequest;
import com.vibecoding.flowerstore.Service.PlaceOrderResponse;
import com.vibecoding.flowerstore.R;
import com.vibecoding.flowerstore.Service.ApiService;
import com.vibecoding.flowerstore.Service.RetrofitClient;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView recyclerOrderSummary;
    private CheckoutAdapter checkoutAdapter;
    private TextView tvShippingAddress, btnChangeAddress;
    private TextView tvSubtotal, tvShippingFee, tvTotalAmount;
    private Button btnPlaceOrder;
    
    private String authToken;
    private AddressDTO selectedAddress;
    private CheckoutDetailsResponse.ShippingCarrier selectedCarrier;
    private CartDTO currentCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        setupToolbar();
        initViews();
        
        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE);
        String token = prefs.getString("ACCESS_TOKEN", null);
        if (token == null) {
            finish();
            return;
        }
        authToken = "Bearer " + token;

        loadCheckoutDetails();
        setupEvents();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initViews() {
        recyclerOrderSummary = findViewById(R.id.recycler_order_summary);
        tvShippingAddress = findViewById(R.id.tv_shipping_address);
        btnChangeAddress = findViewById(R.id.btn_change_address);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvShippingFee = findViewById(R.id.tv_shipping_fee);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        btnPlaceOrder = findViewById(R.id.btn_place_order);

        recyclerOrderSummary.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadCheckoutDetails() {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<CheckoutDetailsResponse> call = apiService.getCheckoutDetails(authToken);

        call.enqueue(new Callback<CheckoutDetailsResponse>() {
            @Override
            public void onResponse(Call<CheckoutDetailsResponse> call, Response<CheckoutDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    Toast.makeText(CheckoutActivity.this, "Lỗi tải thông tin thanh toán", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CheckoutDetailsResponse> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(CheckoutDetailsResponse data) {
        currentCart = data.getCart();
        
        // 1. Hiển thị danh sách sản phẩm
        if (currentCart != null && currentCart.getItems() != null) {
            checkoutAdapter = new CheckoutAdapter(currentCart.getItems(), this);
            recyclerOrderSummary.setAdapter(checkoutAdapter);
            
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvSubtotal.setText(currencyFormat.format(currentCart.getTotalAmount()));
        }

        // 2. Xử lý địa chỉ
        List<AddressDTO> addresses = data.getAddresses();
        if (addresses != null && !addresses.isEmpty()) {
            selectedAddress = addresses.get(0); 
            // Ưu tiên địa chỉ mặc định
            for (AddressDTO addr : addresses) {
                if (addr.isDefault()) {
                    selectedAddress = addr;
                    break;
                }
            }
            displayAddress(selectedAddress);
        } else {
            tvShippingAddress.setText("Vui lòng thêm địa chỉ giao hàng");
            selectedAddress = null;
        }

        // 3. Xử lý vận chuyển
        List<CheckoutDetailsResponse.ShippingCarrier> carriers = data.getShippingCarriers();
        if (carriers != null && !carriers.isEmpty()) {
            selectedCarrier = carriers.get(0);
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvShippingFee.setText(currencyFormat.format(selectedCarrier.getShippingFee()));
        } else {
            tvShippingFee.setText("0đ");
        }

        // 4. Tính tổng tiền
        calculateTotal();
    }

    private void displayAddress(AddressDTO address) {
        if (address == null) return;
        String fullAddress = address.getRecipientName() + " | " + address.getPhoneNumber() + "\n" +
                             address.getDetailAddress() + ", " + address.getCity();
        tvShippingAddress.setText(fullAddress);
    }

    private void calculateTotal() {
        if (currentCart == null) return;
        
        double subtotal = currentCart.getTotalAmount();
        double shippingFee = (selectedCarrier != null) ? selectedCarrier.getShippingFee() : 0;
        double total = subtotal + shippingFee;

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalAmount.setText(currencyFormat.format(total));
    }

    private void setupEvents() {
        btnChangeAddress.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng chọn địa chỉ đang phát triển", Toast.LENGTH_SHORT).show();
        });

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void placeOrder() {
        if (selectedAddress == null) {
            Toast.makeText(this, "Vui lòng chọn địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedCarrier == null) {
            Toast.makeText(this, "Lỗi: Không có đơn vị vận chuyển", Toast.LENGTH_SHORT).show();
            return;
        }

        PlaceOrderRequest request = new PlaceOrderRequest(
                selectedAddress.getId(),
                selectedCarrier.getId(),
                "COD",
                currentCart.getTotalAmount()
        );

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<PlaceOrderResponse> call = apiService.placeOrder(authToken, request);

        call.enqueue(new Callback<PlaceOrderResponse>() {
            @Override
            public void onResponse(Call<PlaceOrderResponse> call, Response<PlaceOrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CheckoutActivity.this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                    
                    Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CheckoutActivity.this, "Đặt hàng thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlaceOrderResponse> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
