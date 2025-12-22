package com.vibecoding.flowerstore.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.vibecoding.flowerstore.Adapter.AddressAdapter;
import com.vibecoding.flowerstore.Model.AddressDTO;
import com.vibecoding.flowerstore.R;
import com.vibecoding.flowerstore.Service.ApiService;
import com.vibecoding.flowerstore.Service.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends AppCompatActivity {

    private RecyclerView rvAddresses;
    private AddressAdapter addressAdapter;
    private ImageView btnBack;
    private MaterialButton btnAddAddress;
    private ApiService apiService;
    private String userToken;
    private ProgressBar progressBar;

    // Launcher cho việc thêm hoặc sửa địa chỉ
    private final ActivityResultLauncher<Intent> addressResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Chỉ load lại dữ liệu nếu màn hình con trả về RESULT_OK
                    fetchAddresses();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title not the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//int flag, int mask
        setContentView(R.layout.activity_address);

        initViews();
        setupRecyclerView();

        // Lấy token và gọi API
        if (checkLogin()) {
            apiService = RetrofitClient.getClient(this).create(ApiService.class);
            // Load lần đầu tiên
            fetchAddresses();
        }

        setupEvents();
    }

    private void initViews() {
        rvAddresses = findViewById(R.id.rvAddresses);
        btnBack = findViewById(R.id.btnBack);
        btnAddAddress = findViewById(R.id.btnAddAddress);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        rvAddresses.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter với callback xử lý Sửa/Xóa
        addressAdapter = new AddressAdapter(this, new ArrayList<>(), new AddressAdapter.OnAddressActionListener() {
            @Override
            public void onEdit(AddressDTO address) {
                // Chuyển sang màn hình EditAddressActivity và truyền address sang
                Intent intent = new Intent(AddressActivity.this, EditAddressActivity.class);
                intent.putExtra("address_data", address);
                // Sử dụng launcher để bắt kết quả trả về
                addressResultLauncher.launch(intent);
            }

            @Override
            public void onDelete(AddressDTO address) {
                Toast.makeText(AddressActivity.this, "Xóa địa chỉ ID: " + address.getId(), Toast.LENGTH_SHORT).show();
                // TODO: Gọi API xóa địa chỉ
                // Sau khi xóa xong cũng nên gọi fetchAddresses()
            }
        });

        rvAddresses.setAdapter(addressAdapter);
    }

    private boolean checkLogin() {
        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        String rawToken = prefs.getString("ACCESS_TOKEN", null);

        if (rawToken == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        userToken = "Bearer " + rawToken;
        return true;
    }

    private void fetchAddresses() {
        // Gọi API lấy danh sách địa chỉ
        progressBar.setVisibility((View.VISIBLE));
        Call<List<AddressDTO>> call = apiService.getAddresses(userToken);

        call.enqueue(new Callback<List<AddressDTO>>() {
            @Override
            public void onResponse(Call<List<AddressDTO>> call, Response<List<AddressDTO>> response) {
                progressBar.setVisibility((View.GONE));
                btnAddAddress.setVisibility((View.VISIBLE));
                if (response.isSuccessful() && response.body() != null) {
                    List<AddressDTO> addresses = response.body();
                    addressAdapter.setAddressList(addresses);
                    if (addresses.isEmpty()) {
                        Toast.makeText(AddressActivity.this, "Bạn chưa lưu địa chỉ nào", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddressActivity.this, "Lỗi tải địa chỉ: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AddressDTO>> call, Throwable t) {
                progressBar.setVisibility((View.GONE));
                Toast.makeText(AddressActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnAddAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddAddressActivity.class);
            // Sử dụng launcher để bắt kết quả trả về
            addressResultLauncher.launch(intent);
        });
    }
}