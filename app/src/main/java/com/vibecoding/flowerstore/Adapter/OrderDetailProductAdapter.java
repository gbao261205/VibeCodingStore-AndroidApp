package com.vibecoding.flowerstore.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.vibecoding.flowerstore.Model.OrderDetailDTO;
import com.vibecoding.flowerstore.Model.ProductDTO;
import com.vibecoding.flowerstore.R;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderDetailProductAdapter extends RecyclerView.Adapter<OrderDetailProductAdapter.ViewHolder> {
    private Context context;
    private List<OrderDetailDTO> detailsList;

    public OrderDetailProductAdapter(Context context, List<OrderDetailDTO> detailsList) {
        this.context = context;
        this.detailsList = detailsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetailDTO detail = detailsList.get(position);
        ProductDTO product = detail.getProduct();

        if (product != null) {
            holder.tvName.setText(product.getName());
            Glide.with(context)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.imgProduct);
        }

        holder.tvQuantity.setText("x" + detail.getQuantity());

        // Tính giá: Giá * Số lượng
        BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
        BigDecimal total = price.multiply(new BigDecimal(detail.getQuantity()));

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(currencyFormat.format(total));
    }

    @Override
    public int getItemCount() {
        return detailsList == null ? 0 : detailsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvQuantity, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgItemProduct);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvPrice = itemView.findViewById(R.id.tvItemPrice);
        }
    }
}