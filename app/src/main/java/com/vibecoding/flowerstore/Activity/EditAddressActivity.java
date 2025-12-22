package com.vibecoding.flowerstore.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.vibecoding.flowerstore.Model.AddressDTO;
import com.vibecoding.flowerstore.R;
import com.vibecoding.flowerstore.Service.ApiService;
import com.vibecoding.flowerstore.Service.MessageResponse;
import com.vibecoding.flowerstore.Service.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAddressActivity extends AppCompatActivity {

    private static final String TAG = "EditAddressActivity";
    private TextInputEditText edtName, edtPhone, edtCity, edtDetailAddress;
    private MaterialSwitch switchDefault;
    private MaterialButton btnSave;
    private ImageButton btnBack;
    private TextView tvTitle;
    private ProgressBar progressBar;
    private int id;
    private String userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title not the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//int flag, int mask
        setContentView(R.layout.activity_address_detail);

        //Ánh xạ
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtCity = findViewById(R.id.edtCity);
        edtDetailAddress = findViewById(R.id.edtDetailAddress);
        switchDefault = findViewById(R.id.switchDefault);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        progressBar = findViewById(R.id.progressBar);

        // Thay đổi tiêu đề
        tvTitle.setText("Chỉnh sửa địa chỉ");

        // Nhận dữ liệu từ Intent
        if (getIntent().hasExtra("address_data")) {
            AddressDTO address = (AddressDTO) getIntent().getSerializableExtra("address_data");
            if (address != null) {
                id = address.getId();
                edtName.setText(address.getRecipientName());
                edtPhone.setText(address.getPhoneNumber());
                edtCity.setText(address.getCity());
                edtDetailAddress.setText(address.getDetailAddress());
                switchDefault.setChecked(address.isDefault());
            }
        }

        // Lấy Token (để gọi API update cần token nếu API yêu cầu, hiện tại hàm EditAddress chưa dùng token nhưng có thể cần trong tương lai)
        userToken = "Bearer " + getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE).getString("ACCESS_TOKEN", "");

        //Nút Back
        btnBack.setOnClickListener(v -> finish());

        //Nút Lưu
        btnSave.setOnClickListener(v -> handleEditAddress());
    }

    private void handleEditAddress() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String city = edtCity.getText().toString().trim();
        String detail = edtDetailAddress.getText().toString().trim();
        boolean isDefault = switchDefault.isChecked();

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

        EditAddress(id, name, phone, detail, city, isDefault);

    }

    private void EditAddress(int id, String name, String phone, String detail, String city, boolean isDefault) {
        setLoading(true);

        AddressDTO request = new AddressDTO(name, phone, detail, city, isDefault);
        // Lưu ý: Request Body không cần ID, ID được truyền qua PathVariable
        
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        // Gọi API update (lưu ý: kiểm tra xem API có cần header Authorization ko, hiện tại theo code cũ thì không thấy truyền token vào hàm updateAddress)
        apiService.updateAddress(id, request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditAddressActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.d(TAG, "Lỗi: " + errorBody);
                        Toast.makeText(EditAddressActivity.this, "Lỗi cập nhật: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Có lỗi xảy ra!", e);
                    }
                }
            }
            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                setLoading(false);
                Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(EditAddressActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
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