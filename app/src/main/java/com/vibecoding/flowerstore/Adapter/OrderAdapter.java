package com.vibecoding.flowerstore.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Cần thêm dependency Glide vào build.gradle
import com.vibecoding.flowerstore.Model.OrderDTO;
import com.vibecoding.flowerstore.Model.OrderDetailDTO;
import com.vibecoding.flowerstore.R;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<OrderDTO> orderList;

    public OrderAdapter(Context context, List<OrderDTO> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public void setOrderList(List<OrderDTO> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderDTO order = orderList.get(position);

        // 1. Set ID
        holder.tvOrderId.setText("Mã đơn hàng: #" + order.getId());

        // 2. Set Price (Format currency)
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormat.format(order.getTotalAmount());
        holder.tvTotalAmount.setText("Tổng tiền: " + formattedPrice);

        // 3. Set Date (Xử lý đơn giản chuỗi LocalDateTime)
        if (order.getCreatedAt() != null) {
            // Giả sử createdAt là String hoặc Object toString trả về dạng ISO
            // Bạn có thể dùng DateTimeFormatter nếu minSdk >= 26
            holder.tvOrderDate.setText("Ngày đặt: " + order.getCreatedAt().toString().substring(0, 10));
        }

        // 4. Set Status & Color
        String status = order.getStatus(); // PENDING, COMPLETED, CANCELLED, etc.
        updateStatusView(holder.tvStatus, status);

        // 5. Load Image (Lấy ảnh từ sản phẩm đầu tiên trong chi tiết đơn hàng)
        if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
            OrderDetailDTO firstItem = order.getOrderDetails().get(0);
            if (firstItem.getProduct() != null) {
                Glide.with(context)
                        .load(firstItem.getProduct().getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background) // Ảnh mặc định
                        .into(holder.imgProduct);
            }
        }
    }

    private void updateStatusView(TextView tv, String status) {
        if (status == null) return;

        switch (status) {
            case "COMPLETED":
            case "DELIVERED":
                tv.setText("Đã giao");
                tv.setBackgroundResource(R.drawable.bg_status_green);
                tv.setTextColor(Color.parseColor("#166534")); // Green 800
                break;
            case "PENDING":
            case "PENDING_PAYMENT":
            case "IN_DELIVERY":
                tv.setText(status.equals("IN_DELIVERY") ? "Đang giao" : "Đang xử lý");
                tv.setBackgroundResource(R.drawable.bg_status_orange);
                tv.setTextColor(Color.parseColor("#9A3412")); // Orange 800
                break;
            case "CANCELLED":
                tv.setText("Đã huỷ");
                tv.setBackgroundResource(R.drawable.bg_status_gray);
                tv.setTextColor(Color.parseColor("#374151")); // Gray 700
                break;
            default:
                tv.setText(status);
                tv.setBackgroundResource(R.drawable.bg_status_gray);
                tv.setTextColor(Color.BLACK);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvTotalAmount, tvOrderDate, tvStatus;
        ImageView imgProduct, btnDetail;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }
    }
}