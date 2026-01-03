package com.vibecoding.flowerstore.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.vibecoding.flowerstore.Model.UserDTO;
import com.vibecoding.flowerstore.R;

public class AdminUserDetailActivity extends AppCompatActivity {

    private ImageView btnBack, ivAvatar;
    private TextView tvRole, tvUsername, tvFullName, tvEmail, tvPhone, tvCreatedAt, tvStatus;
    private View vStatusIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin_user_detail);

        initViews();
        setupListeners();
        
        // Lấy dữ liệu từ Intent
        if (getIntent().hasExtra("user_detail")) {
            UserDTO user = (UserDTO) getIntent().getSerializableExtra("user_detail");
            if (user != null) {
                displayUserData(user);
            } else {
                Toast.makeText(this, "Lỗi: Không tìm thấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Lỗi: Không có dữ liệu được truyền", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        ivAvatar = findViewById(R.id.ivAvatar);
        tvRole = findViewById(R.id.tvRole);
        tvUsername = findViewById(R.id.tvUsername);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvStatus = findViewById(R.id.tvStatus);
        vStatusIndicator = findViewById(R.id.vStatusIndicator);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void displayUserData(UserDTO user) {
        // 1. Avatar
        Glide.with(this)
                .load(user.getAvatar())
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .fallback(R.drawable.ic_person)
                .into(ivAvatar);

        // 2. Role
        if (user.getRole() != null) {
            tvRole.setText(user.getRole().getName());
        } else {
            tvRole.setText("--");
        }

        // 3. Username
        tvUsername.setText(user.getUsername());

        // 4. FullName
        tvFullName.setText(user.getFullName() != null ? user.getFullName() : "--");

        // 5. Email (xử lý HTML nếu cần)
        String email = user.getEmail();
        if (email != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvEmail.setText(Html.fromHtml(email, Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvEmail.setText(Html.fromHtml(email));
            }
        } else {
            tvEmail.setText("--");
        }

        // 6. Phone
        tvPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "--");

        // 7. Created At
        // Giả sử API trả về chuỗi ngày giờ, cần format nếu muốn đẹp hơn
        tvCreatedAt.setText(user.getCreatedAt() != null ? user.getCreatedAt() : "--");

        // 8. Status
        if (user.isActive()) {
            tvStatus.setText("Hoạt động");
            tvStatus.setTextColor(Color.parseColor("#166534")); // Xanh lá đậm
            vStatusIndicator.setBackgroundResource(R.drawable.bg_status_green);
        } else {
            tvStatus.setText("Đã khóa");
            tvStatus.setTextColor(Color.parseColor("#991B1B")); // Đỏ đậm
            vStatusIndicator.setBackgroundResource(R.drawable.bg_status_red);
        }
    }
}
