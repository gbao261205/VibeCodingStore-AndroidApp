package com.vibecoding.flowerstore.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.vibecoding.flowerstore.Model.User;
import com.vibecoding.flowerstore.R;
// TODO: 1. Thêm thư viện Cloudinary vào build.gradle
// implementation "com.cloudinary:cloudinary-android:2.2.0"
//
// import com.cloudinary.android.MediaManager;
// import com.cloudinary.android.callback.ErrorInfo;
// import com.cloudinary.android.callback.UploadCallback;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    private ImageView ivAvatar, btnBack;
    private TextView tvNameDisplay;
    private TextView tvEmailDisplay;
    private EditText edtName, edtEmail, edtPhone;
    private Button btnSave;

    private User currentUser;
    private Uri selectedImageUri;

    // Trình khởi chạy để chọn ảnh từ thư viện
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // Hiển thị ảnh vừa chọn
                    Glide.with(this).load(selectedImageUri).into(ivAvatar);
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Ánh xạ (Giả sử bạn có các ID này trong file XML)
        ivAvatar = findViewById(R.id.ivAvatar);
        tvNameDisplay = findViewById(R.id.tvNameDisplay);
        tvEmailDisplay = findViewById(R.id.tvEmailDisplay);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        // TODO: 2. Khởi tạo Cloudinary (thường làm trong lớp Application)

        // Lấy dữ liệu người dùng từ Intent
        if (getIntent().hasExtra("user")) {
            currentUser = (User) getIntent().getSerializableExtra("user");
        }

        if (currentUser != null) {
            populateUserData();
        } else {
            Toast.makeText(this, "Không có dữ liệu người dùng", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupListeners();
    }

    /**
     * Điền thông tin người dùng lên giao diện
     */
    private void populateUserData() {
        tvNameDisplay.setText(currentUser.getFullName());
        tvEmailDisplay.setText(currentUser.getEmail());
        edtName.setText(currentUser.getFullName());
        edtEmail.setText(currentUser.getEmail());
        edtPhone.setText(currentUser.getPhoneNumber());

        // Dùng Glide để hiển thị ảnh đại diện
        Glide.with(this)
                .load(currentUser.getAvatar())
                .placeholder(R.drawable.ic_person) // Ảnh chờ
                .error(R.drawable.ic_person)       // Ảnh lỗi
                .into(ivAvatar);
    }

    /**
     * Cài đặt sự kiện cho các nút
     */
    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        ivAvatar.setOnClickListener(v -> {
            // Mở thư viện để chọn ảnh
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                if (selectedImageUri != null) {
                    // Nếu có ảnh mới, tải lên trước
                    uploadImageToCloudinary(selectedImageUri);
                } else {
                    // Nếu không có ảnh mới, cập nhật thông tin với URL ảnh cũ
                    updateProfile(currentUser.getAvatar());
                }
            }
        });
    }

    /**
     * Kiểm tra dữ liệu nhập vào
     */
    private boolean validateInput() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            edtName.setError("Tên không được để trống");
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Số điện thoại không được để trống");
            return false;
        }
        // Có thể thêm các kiểm tra khác cho SĐT nếu muốn

        return true;
    }

    /**
     * Tải ảnh lên Cloudinary
     */
    private void uploadImageToCloudinary(Uri imageUri) {
        Toast.makeText(this, "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show();

        // TODO: 3. Viết mã xử lý tải ảnh lên Cloudinary

        MediaManager.get().upload(imageUri).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                // Ví dụ: Hiện ProgressBar
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                // Cập nhật ProgressBar
            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                // Lấy URL ảnh đã tải lên
                String imageUrl = (String) resultData.get("url");
                Log.d(TAG, "Tải ảnh thành công: " + imageUrl);
                // Sau khi có URL, gọi hàm cập nhật thông tin
                updateProfile(imageUrl);
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                Log.e(TAG, "Lỗi tải ảnh: " + error.getDescription());
                Toast.makeText(EditProfileActivity.this, "Tải ảnh lên thất bại", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                // Xử lý
            }
        }).dispatch();


//        // ---- MÃ GIẢ LẬP: Xóa phần này khi đã có Cloudinary thật ----
//        String placeholderUrl = "https://res.cloudinary.com/demo/image/upload/sample.jpg";
//        updateProfile(placeholderUrl);
//        // ---- KẾT THÚC MÃ GIẢ LẬP ----
    }

    /**
     * Gọi API để cập nhật thông tin người dùng
     */
    private void updateProfile(String avatarUrl) {
        String newName = edtName.getText().toString().trim();
        String newPhone = edtPhone.getText().toString().trim();

        Log.d(TAG, "Chuẩn bị cập nhật: Tên: " + newName + ", SĐT: " + newPhone + ", Ảnh: " + avatarUrl);
        Toast.makeText(this, "Đang cập nhật...", Toast.LENGTH_SHORT).show();

        // TODO: 4. Gọi API cập nhật thông tin bằng Retrofit
        /*
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<User> call = apiService.updateUserProfile(newName, newPhone, avatarUrl); // Giả sử có phương thức này
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    // Xóa cache cũ để ProfileActivity tải lại thông tin mới
                    ProfileActivity.invalidateProfileCache();
                    finish(); // Quay về trang Profile
                } else {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thất bại, mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
        */

        // ---- MÃ GIẢ LẬP: Xóa phần này khi đã có API thật ----
        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
        ProfileActivity.invalidateProfileCache();
        finish();
        // ---- KẾT THÚC MÃ GIẢ LẬP ----
    }
}
