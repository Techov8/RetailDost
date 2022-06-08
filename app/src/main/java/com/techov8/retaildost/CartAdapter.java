package com.techov8.retaildost;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<ItemListModel> cartItemModelList;
    private int lastPosition = -1;

    private boolean isDelivery;
    public CartAdapter(List<ItemListModel> cartItemModelList, boolean isDelivery) {
        this.cartItemModelList = cartItemModelList;
        this.isDelivery = isDelivery;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (isDelivery) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_item_layout_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);

        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {

        String productID = cartItemModelList.get(position).getId();
        String productImage = cartItemModelList.get(position).getImage();
        String productTitle = cartItemModelList.get(position).getName();
        String productPrice = cartItemModelList.get(position).getPrice();
        String cutedPrice = cartItemModelList.get(position).getCuttedPrice();
        String productQuantity = cartItemModelList.get(position).getQuantity();
        Long minQty = cartItemModelList.get(position).getMin_items();
        String savedPrice = cartItemModelList.get(position).getSavedPrice();
        Long no_of_items = cartItemModelList.get(position).getNo_of_items();
        String mainId=cartItemModelList.get(position).getMainId();
        holder.setItemDetails(productID, productImage, productTitle, productPrice, cutedPrice, position, productQuantity, minQty, savedPrice, no_of_items,mainId);


        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }

    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage, deleteBtn, minusBtn, plusBtn;
        private TextView productTitle, productPrice, cutedPrice, no_of_items;
        private TextView savedPrice;
        private TextView originalPrice,minQty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            if (isDelivery) {

                productImage = itemView.findViewById(R.id.item_image);
                productTitle = itemView.findViewById(R.id.item_name);
                productPrice = itemView.findViewById(R.id.item_price);
                cutedPrice = itemView.findViewById(R.id.item_cutted_price);
                savedPrice = itemView.findViewById(R.id.saved_price);
                originalPrice = itemView.findViewById(R.id.sum_price);
                no_of_items = itemView.findViewById(R.id.no_of_items);

            } else {
                productImage = itemView.findViewById(R.id.cart_item_image);
                productTitle = itemView.findViewById(R.id.cart_item_name);
                productPrice = itemView.findViewById(R.id.cart_item_price);
                cutedPrice = itemView.findViewById(R.id.cart_item_cutted_price);
                deleteBtn = itemView.findViewById(R.id.item_delete_btn);
                minusBtn = itemView.findViewById(R.id.cart_minus_items);
                plusBtn = itemView.findViewById(R.id.cart_plus_items);
                originalPrice = itemView.findViewById(R.id.cart_item_price_green);
                no_of_items = itemView.findViewById(R.id.cart_no_of_items);
                minQty=itemView.findViewById(R.id.cart_min_qty);
            }

        }

        private void setItemDetails(final String productID, String resource, String title, final String productPriceText, String cutedPriceText, final int position
                , String quantity, final Long minQuantity, String saved, final Long itemNo, final String mainId) {

            try {
                Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.product_placeholder)).into(productImage);
                productTitle.setText(title +" "+ quantity);

                Double totalPrice = (Double.parseDouble(productPriceText) * itemNo.intValue());
                originalPrice.setText("₹ " + String.format("%.2f", totalPrice));
                productPrice.setText("₹ " + productPriceText);
                productPrice.setTextColor(Color.parseColor("#000000"));
                cutedPrice.setText("₹ " + cutedPriceText);
                if (isDelivery) {
                    no_of_items.setText("Qty: " + itemNo);
                    savedPrice.setText("Saved ₹ " + saved + " on per item");
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent detailsIntent = new Intent(itemView.getContext(), ItemDetailsActivity.class);
                            detailsIntent.putExtra("main_id", mainId);
                            detailsIntent.putExtra("id", productID);
                            detailsIntent.putExtra("is_list", "YS");
                            itemView.getContext().startActivity(detailsIntent);

                        }
                    });
                } else {
                    minQty.setText("Min qty: " + minQuantity + " pcs");
                    no_of_items.setText(String.valueOf(itemNo));
                    productImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent detailsIntent = new Intent(itemView.getContext(), ItemDetailsActivity.class);
                            detailsIntent.putExtra("main_id", mainId);
                            detailsIntent.putExtra("id", productID);
                            detailsIntent.putExtra("is_list", "YS");
                            itemView.getContext().startActivity(detailsIntent);

                        }
                    });
                    productTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent detailsIntent = new Intent(itemView.getContext(), ItemDetailsActivity.class);
                            detailsIntent.putExtra("main_id", mainId);
                            detailsIntent.putExtra("id", productID);
                            detailsIntent.putExtra("is_list", "YS");
                            itemView.getContext().startActivity(detailsIntent);
                        }
                    });
                }


                if (!isDelivery) {

                    minusBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Long temp = itemNo;
                            if (temp > minQuantity) {
                                temp = temp - 1;
                                MyCartFragment.loadingDialog.show();
                                no_of_items.setText(String.valueOf(temp));
                                cartItemModelList.get(position).setNo_of_items(temp);
                                notifyItemChanged(position);
                                updateData(position, temp);
                                ////
                            }
                        }
                    });

                    plusBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Long temp = itemNo;
                            temp = temp + 1;
                            MyCartFragment.loadingDialog.show();
                            no_of_items.setText(String.valueOf(temp));
                            cartItemModelList.get(position).setNo_of_items(temp);
                            notifyItemChanged(position);
                            updateData(position, temp);
                            /////

                        }
                    });

                    deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyCartFragment.loadingDialog.show();
                            DBqueries.removeFromCart(productID, itemView.getContext(), MyCartFragment.loadingDialog);

                            if (DBqueries.cartList.size() < 99) {
                                ItemListActivity.badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                            } else {
                                ItemListActivity.badgeCount.setText("99");
                            }
                        }
                    });
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    private void updateData(int position, Long data) {

        try {
            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USERS_DATA")
                    .document("MY_CART").update("no_of_items" + position, data)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            DBqueries.calculateCartTotalAmount(MyCartFragment.loadingDialog);
                        }
                    });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}