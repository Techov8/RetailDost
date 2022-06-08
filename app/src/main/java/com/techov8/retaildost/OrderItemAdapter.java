package com.techov8.retaildost;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    List<OrderItemModel> orderItemModelList;

    public OrderItemAdapter(List<OrderItemModel> orderItemModelList) {
        this.orderItemModelList = orderItemModelList;
    }

    @NonNull
    @Override
    public OrderItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemAdapter.ViewHolder holder, int position) {

        String orderId=orderItemModelList.get(position).getOrderId();
        Date orderDate=orderItemModelList.get(position).getOrderedDate();
        String deliveryDate=orderItemModelList.get(position).getDeliveryDate();
        String orderStatus=orderItemModelList.get(position).getOrderStatus();
        String amount=orderItemModelList.get(position).getTotalAmount();
        String paymentMode=orderItemModelList.get(position).getPaymentMode();
        holder.setData(orderId,orderDate,deliveryDate,orderStatus,amount,paymentMode,position);
    }

    @Override
    public int getItemCount() {
        return orderItemModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView orderId,orderPlacedOn,expectedDelivery,orderStatus,totalAmount,paymentMode;
        private Button cancelBtn,viewDetailsBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId=itemView.findViewById(R.id.order_id_text);
            orderPlacedOn=itemView.findViewById(R.id.order_placed_on);
            expectedDelivery=itemView.findViewById(R.id.expedted_delivery);
            orderStatus=itemView.findViewById(R.id.order_status);
            totalAmount=itemView.findViewById(R.id.total_amount);
            paymentMode=itemView.findViewById(R.id.payment_mode);
            cancelBtn=itemView.findViewById(R.id.cancel_btn);
            viewDetailsBtn=itemView.findViewById(R.id.view_details_btn);

        }
        private void setData(final String id, Date orderDate, String deliveryDate, String status, String amount, String mode, final int position){

            try {
                orderId.setText(id);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM YYYY");
                orderPlacedOn.setText(simpleDateFormat.format(orderDate));
                expectedDelivery.setText(deliveryDate);
                if (status.equals("Cancelled")) {
                    orderStatus.setTextColor(Color.parseColor("#ED2828"));
                    cancelBtn.setVisibility(View.GONE);
                } else {
                    orderStatus.setTextColor(Color.parseColor("#27EF30"));
                    cancelBtn.setVisibility(View.VISIBLE);
                }
                orderStatus.setText(status);
                totalAmount.setText("₹ " + amount);
                paymentMode.setText(mode);

                final Dialog yesOrNoDialog = new Dialog(itemView.getContext());
                yesOrNoDialog.setContentView(R.layout.cancel_order_dialog);
                yesOrNoDialog.setCancelable(false);
                yesOrNoDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        yesOrNoDialog.show();
                    }
                });

                Button yesBtn = yesOrNoDialog.findViewById(R.id.yes_btn);
                Button noBtn = yesOrNoDialog.findViewById(R.id.no_btn);

                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyCartFragment.loadingDialog.show();
                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USERS_DATA")
                                .document("MY_ORDERS").collection("ITEM_LIST").document(id).update("Order_Status", "Cancelled")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        FirebaseFirestore.getInstance().collection("ORDERS").document(id).update("Order_Status", "Cancelled")
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        DBqueries.orderItemModelList.get(position).setOrderStatus("Cancelled");
                                                        notifyItemChanged(position);
                                                        MyCartFragment.loadingDialog.dismiss();
                                                    }
                                                });
                                    }
                                });
                        cancelBtn.setVisibility(View.GONE);

                        yesOrNoDialog.dismiss();
                    }
                });

                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        yesOrNoDialog.dismiss();
                    }
                });
                viewDetailsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent detailsIntent = new Intent(itemView.getContext(), DeliveryActivity.class);
                        detailsIntent.putExtra("TYPE", "ORDER_DETAILS");
                        detailsIntent.putExtra("ORDER_ID", id);
                        itemView.getContext().startActivity(detailsIntent);
                    }
                });

            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
