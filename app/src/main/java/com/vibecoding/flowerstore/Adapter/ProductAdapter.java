package com.vibecoding.flowerstore.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vibecoding.flowerstore.Model.Product;
import com.vibecoding.flowerstore.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private final Context context;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view từ layout item_product.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // Lấy sản phẩm ở vị trí hiện tại
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    // Phương thức để cập nhật dữ liệu cho adapter
    public void updateData(List<Product> newProductList) {
        this.productList.clear();
        this.productList.addAll(newProductList);
        notifyDataSetChanged(); // Báo cho RecyclerView biết dữ liệu đã thay đổi để vẽ lại UI
    }

    // ViewHolder chứa các view con của item_product.xml
    class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productTitle;
        TextView productPrice;
        ImageButton favoriteButton;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các view từ layout
            productImage = itemView.findViewById(R.id.image_product);
            productTitle = itemView.findViewById(R.id.text_product_title);
            productPrice = itemView.findViewById(R.id.text_product_price);
            favoriteButton = itemView.findViewById(R.id.button_favorite);
        }

        // Gán dữ liệu từ đối tượng Product vào các view
        void bind(Product product) {
            productTitle.setText(product.getName());

            // Định dạng giá tiền theo kiểu Việt Nam (vd: 1.200.000₫)
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            productPrice.setText(currencyFormatter.format(product.getPrice()));

            // Sử dụng Glide để tải ảnh từ URL
            Glide.with(context)
                    .load(product.getImage()) // Dùng getImage() theo model của bạn
                    .placeholder(R.drawable.placeholder_product) // Ảnh hiển thị trong lúc tải
                    .error(R.drawable.placeholder_product) // Ảnh hiển thị khi có lỗi
                    .into(productImage);

            // Bắt sự kiện click cho toàn bộ item
            itemView.setOnClickListener(v -> {
                // Ví dụ: hiển thị Toast khi click vào sản phẩm
                Toast.makeText(context, "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
                // Ở đây bạn có thể mở màn hình chi tiết sản phẩm
            });

            // Bắt sự kiện click cho nút yêu thích
            favoriteButton.setOnClickListener(v -> {
                Toast.makeText(context, "Đã thêm " + product.getName() + " vào yêu thích", Toast.LENGTH_SHORT).show();
                // Thay đổi icon của nút favorite nếu cần
            });
        }
    }
}
