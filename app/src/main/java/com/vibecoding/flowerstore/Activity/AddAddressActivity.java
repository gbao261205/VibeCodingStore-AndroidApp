package com.vibecoding.flowerstore.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.vibecoding.flowerstore.Model.AddressDTO;
import com.vibecoding.flowerstore.Service.MessageResponse;
import com.vibecoding.flowerstore.R;
import com.vibecoding.flowerstore.Service.ApiService;
import com.vibecoding.flowerstore.Service.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity {
    private static final String TAG = "AddAddressActivity";
    private TextInputEditText edtName, edtPhone, edtCity, edtDetailAddress;
    private MaterialSwitch switchDefault;
    private MaterialButton btnSave;
    private ImageButton btnBack;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title not the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//int flag, int mask
        setContentView(R.layout.activity_address_detail);

        initViews();
        setupListeners();
    }

    private void initViews() {
        // Ánh xạ View theo ID trong activity_address_detail.xml
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtCity = findViewById(R.id.edtCity);
        edtDetailAddress = findViewById(R.id.edtDetailAddress);
        switchDefault = findViewById(R.id.switchDefault);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);

        // Đặt lại title nếu cần thiết (vì layout này có thể dùng chung cho Edit)
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("Thêm địa chỉ mới");
    }

    private void setupListeners() {
        // Nút Back
        btnBack.setOnClickListener(v -> finish());

        // Nút Lưu
        btnSave.setOnClickListener(v -> handleCreateAddress());
    }

    private void handleCreateAddress() {
        // 1. Lấy dữ liệu từ input
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String city = edtCity.getText().toString().trim();
        String detail = edtDetailAddress.getText().toString().trim();
        boolean isDefault = switchDefault.isChecked();

        // 2. Validate dữ liệu
        if (TextUtils.isEmpty(name)) {
            edtName.setError("Vui lòng nhập họ tên");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Vui lòng nhập số điện thoại");
            return;
        }
        if (TextUtils.isEmpty(city)) {
            edtCity.setError("Vui lòng nhập Tỉnh/Thành phố");
            return;
        }
        if (TextUtils.isEmpty(detail)) {
            edtDetailAddress.setError("Vui lòng nhập địa chỉ chi tiết");
            return;
        }

        // 3. Gọi API
        AddAddress(name, phone, detail, city, isDefault);
    }

    private void AddAddress(String name, String phone, String detail, String city, boolean isDefault) {
        // Hiển thị loading và disable nút lưu
        setLoading(true);

        // Tạo đối tượng Request
        AddressDTO request = new AddressDTO(name, phone, detail, city, isDefault);

        // Lấy ApiService từ RetrofitClient (sử dụng context để tự động gắn Token)
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        apiService.createAddress(request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    // Lỗi từ server (400, 401...)
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(AddAddressActivity.this, "Lỗi: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(AddAddressActivity.this, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                setLoading(false);
                // Lỗi mạng hoặc lỗi kết nối
                Toast.makeText(AddAddressActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnSave.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSave.setEnabled(true);
        }
    }
}