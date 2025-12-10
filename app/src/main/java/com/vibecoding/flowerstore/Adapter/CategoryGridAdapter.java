package com.vibecoding.flowerstore.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vibecoding.flowerstore.Model.Category;
import com.vibecoding.flowerstore.R;

import java.util.List;

public class CategoryGridAdapter extends RecyclerView.Adapter<CategoryGridAdapter.ViewHolder> {

    private List<Category> categoryList;
    private Context context;

    // 1. Khai báo biến Listener để lắng nghe sự kiện click
    private OnCategoryClickListener listener;

    // 2. Tạo Interface giao tiếp với Activity
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    // 3. Hàm setter để Activity có thể đăng ký lắng nghe
    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public CategoryGridAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    public void updateData(List<Category> newCategories) {
        this.categoryList = newCategories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.name.setText(category.getName());

        // Tự động tìm ID ảnh dựa trên Slug
        int imageResId = getImageResIdBySlug(category.getSlug());

        // Set ảnh
        holder.image.setImageResource(imageResId);

        // 4. Bắt sự kiện click vào item và gửi ra ngoài qua Listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    /**
     * Hàm này dùng để ánh xạ từ SLUG của category sang ID ảnh trong drawable
     * Ví dụ: "hoa-hong" -> R.drawable.hoa_hong
     */
    private int getImageResIdBySlug(String slug) {
        if (slug == null || slug.isEmpty()) {
            return R.drawable.banner1; // Ảnh mặc định
        }

        // 1. Đổi dấu gạch ngang "-" thành gạch dưới "_" để đúng quy tắc tên file Android
        String imageName = slug.replace("-", "_").toLowerCase();

        // 2. Tìm ID của tài nguyên (Resource ID) dựa trên tên
        // "drawable" là tên thư mục, context.getPackageName() là tên gói ứng dụng
        int resId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        // 3. Nếu tìm thấy (resId != 0) thì trả về, không thấy thì trả về ảnh mặc định
        return (resId != 0) ? resId : R.drawable.banner1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.img_category);
            name = itemView.findViewById(R.id.text_category_name);
        }
    }
}