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

import com.vibecoding.flowerstore.Model.Product;
import com.vibecoding.flowerstore.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FavoriteProductAdapter extends RecyclerView.Adapter<FavoriteProductAdapter.ViewHolder> {

    private List<Product> productList;
    private Context context;

    public FavoriteProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        // 1. Set tên sản phẩm
        holder.tvName.setText(product.getName());

        // 2. Format giá tiền (double -> chuỗi tiền tệ Việt Nam)
        // Ví dụ: 750000 -> 750.000 ₫
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String priceString = currencyFormatter.format(product.getPrice());
        holder.tvPrice.setText(priceString);

        // 3. Xử lý ảnh tĩnh
        // Lấy tên ảnh từ model (ví dụ "hoa-hong-do") và tìm ID trong drawable
        int resId = getResId(product.getImage());
        holder.imgProduct.setImageResource(resId);

        // 4. Xử lý sự kiện click
        holder.btnAddCart.setOnClickListener(v -> {
            Toast.makeText(context, "Đã thêm " + product.getName() + " vào giỏ!", Toast.LENGTH_SHORT).show();
        });

        holder.btnFavorite.setOnClickListener(v -> {
            Toast.makeText(context, "Đã bỏ yêu thích: " + product.getName(), Toast.LENGTH_SHORT).show();
            // TODO: Code logic xóa item khỏi list ở đây nếu cần
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    /**
     * Hàm tìm ID ảnh trong thư mục drawable dựa trên tên file (String).
     * Tự động thay thế dấu "-" thành "_" để đúng chuẩn Android.
     */
    private int getResId(String imageName) {
        if (imageName == null || imageName.isEmpty()) return R.drawable.banner1; // Ảnh mặc định

        // Đổi "hoa-hong" thành "hoa_hong" để khớp tên file
        String cleanName = imageName.replace("-", "_").toLowerCase();

        int resId = context.getResources().getIdentifier(cleanName, "drawable", context.getPackageName());
        return (resId != 0) ? resId : R.drawable.banner1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice;
        ImageButton btnFavorite, btnAddCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
            btnAddCart = itemView.findViewById(R.id.btn_add_to_cart);
        }
    }
}