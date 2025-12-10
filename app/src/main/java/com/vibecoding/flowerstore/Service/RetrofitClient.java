package com.vibecoding.flowerstore.Service;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://holetinnghia-vibe-coding-store-api.hf.space/api/v1/";

    private static Retrofit retrofitPublic;
    private static Retrofit retrofitAuth;

    // --- CẤU HÌNH TIMEOUT Ở ĐÂY ---
    // Tạo một hàm cấu hình Client chung để tăng thời gian chờ lên 60s
    private static OkHttpClient.Builder getBaseOkHttpBuilder() {
        return new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS) // Chờ kết nối 60s
                .readTimeout(60, TimeUnit.SECONDS)    // Chờ đọc dữ liệu 60s
                .writeTimeout(60, TimeUnit.SECONDS);  // Chờ ghi dữ liệu 60s
    }

    // 1. Client cho API KHÔNG cần Token (Home, Categories...)
    public static Retrofit getClient(){
        if (retrofitPublic == null) {
            // Sửa lại: Dùng OkHttpClient có timeout 60s
            OkHttpClient client = getBaseOkHttpBuilder().build();

            retrofitPublic = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // <-- QUAN TRỌNG: Gắn client vào đây
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitPublic;
    }

    // 2. Client cho API CẦN Token (Order, Cart...)
    public static Retrofit getClient(Context context) {
        if (retrofitAuth == null) {
            Interceptor authInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    SharedPreferences prefs = context.getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE);
                    String token = prefs.getString("ACCESS_TOKEN", null);

                    if (token != null) {
                        Request newRequest = originalRequest.newBuilder()
                                .header("Authorization", "Bearer " + token)
                                .method(originalRequest.method(), originalRequest.body())
                                .build();
                        return chain.proceed(newRequest);
                    }
                    return chain.proceed(originalRequest);
                }
            };

            // Sửa lại: Dùng Builder có timeout 60s rồi mới add interceptor
            OkHttpClient okHttpClient = getBaseOkHttpBuilder() // Lấy cấu hình 60s
                    .addInterceptor(authInterceptor)       // Thêm xử lý Token
                    .build();

            retrofitAuth = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitAuth;
    }
}