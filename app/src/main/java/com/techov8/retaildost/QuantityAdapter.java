package com.techov8.retaildost;

import android.app.Dialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.techov8.retaildost.DBqueries.firebaseFirestore;

public class QuantityAdapter extends RecyclerView.Adapter<QuantityAdapter.ViewHolder>{

    List<ItemListModel> quantityModelList;
    boolean isFromList;
    Dialog check;
    private final int place;
    public QuantityAdapter(List<ItemListModel> quantityModelList,Boolean isFromList,Dialog check,int place) {
        this.quantityModelList = quantityModelList;
        this.isFromList=isFromList;
        this.check=check;
        this.place=place;
    }

    @NonNull
    @Override
    public QuantityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(isFromList) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quality_dialog_item, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rate_chart_layout_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuantityAdapter.ViewHolder holder, int position) {
        String name=quantityModelList.get(position).getQuantity();
        String id=quantityModelList.get(position).getSubId();
        String price=quantityModelList.get(position).getPrice();
        String cutedPrice=quantityModelList.get(position).getCuttedPrice();
        String savedPrice=quantityModelList.get(position).getSavedPrice();
        String image=quantityModelList.get(position).getImage();
        String productName=quantityModelList.get(position).getName();
        String productId=quantityModelList.get(position).getId();
        String mainId=quantityModelList.get(position).getMainId();
        Long minItem=quantityModelList.get(position).getMin_items();
        holder.setData(name,id,price,cutedPrice,savedPrice,position,image,productName,minItem,productId,mainId);
    }

    @Override
    public int getItemCount() {
        return quantityModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name,itemQuantity;
        private TextView priceText,cutedPriceText,savedPriceText,no_of_items,minQty;
        private ImageView itemImage,minusBtn,plusBtn;
        private Button addToCartBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            if(isFromList) {
                itemQuantity = itemView.findViewById(R.id.quantity_text);
            }else{
                name= itemView.findViewById(R.id.item_name);
                itemQuantity=itemView.findViewById(R.id.item_quantity);
                priceText=itemView.findViewById(R.id.item_price);
                cutedPriceText=itemView.findViewById(R.id.item_cutted_price);
                savedPriceText=itemView.findViewById(R.id.saved_price);
                itemImage=itemView.findViewById(R.id.item_image);
                no_of_items=itemView.findViewById(R.id.no_of_items);
                minusBtn=itemView.findViewById(R.id.minus_items);
                plusBtn=itemView.findViewById(R.id.plus_items);
                addToCartBtn=itemView.findViewById(R.id.add_to_cart_btn);
                minQty=itemView.findViewById(R.id.min_qty);


            }

        }
        private void setData(final String itemQuantityText, final String subId, final String price, final String cutedPrice, final String savedPrice, final int position, final String image,
                             final String productName, final Long minItems, final String productId, final String mainId){


            if(isFromList) {
                itemQuantity.setText(itemQuantityText);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isFromList) {
                            try {
                                DBqueries.itemListModelList.get(place).setSubId(subId);
                                DBqueries.itemListModelList.get(place).setPrice(price);
                                DBqueries.itemListModelList.get(place).setCuttedPrice(cutedPrice);
                                DBqueries.itemListModelList.get(place).setSavedPrice(savedPrice);
                                DBqueries.itemListModelList.get(place).setQuantity(itemQuantityText);
                                ItemListActivity.itemListAdapter.notifyDataSetChanged();
                                check.dismiss();
                            }catch (Exception e) {
                                Toast.makeText(itemView.getContext(), "", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }

                        }
                    }
                });
            }else {

                try{
                itemQuantity.setText("Pack of "+itemQuantityText);
                name.setText(productName);
                no_of_items.setText(String.valueOf(minItems));
                priceText.setText("₹ " + price);
                cutedPriceText.setText("₹ " + cutedPrice);
                savedPriceText.setText("Save ₹ " + savedPrice + " on per item");
                minQty.setText("Min qty: " + minItems + " pcs");
                Glide.with(itemView.getContext()).load(image).apply(new RequestOptions().placeholder(R.drawable.product_placeholder)).into(itemImage);

                minusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Long temp = Long.valueOf(no_of_items.getText().toString());
                        if (temp > minItems) {
                            temp = temp - 1;
                            no_of_items.setText(String.valueOf(temp));
                            quantityModelList.get(position).setNo_of_items(temp);
                            notifyItemChanged(position);
                        }
                    }
                });

                plusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Long temp = Long.valueOf(no_of_items.getText().toString());
                        temp = temp + 1;
                        no_of_items.setText(String.valueOf(temp));
                        quantityModelList.get(position).setNo_of_items(temp);
                        notifyItemChanged(position);
                    }
                });
                if (DBqueries.cartList.contains(productId) && DBqueries.cartSubIds.contains(subId)) {
                    addToCartBtn.setText("Added");
                    addToCartBtn.setBackgroundResource(R.drawable.edittextbackground);
                    addToCartBtn.setTextColor(Color.parseColor("#1BA9C3"));
                } else {
                    addToCartBtn.setText("Add");
                    addToCartBtn.setBackgroundResource(R.drawable.item_list_background);
                    addToCartBtn.setTextColor(Color.parseColor("#ffffff"));
                }
                addToCartBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //ADD TO CART WORK

                        if (!ItemListAdapter.running_cart_query) {
                            ItemListAdapter.running_cart_query = true;
                            if (DBqueries.cartList.contains(productId) && DBqueries.cartSubIds.contains(subId)) {
                                ItemListAdapter.running_cart_query = false;
                                Toast.makeText(itemView.getContext(), "Already added to cart", Toast.LENGTH_SHORT).show();
                            } else {
                                addToCartBtn.setText("Added");
                                addToCartBtn.setBackgroundResource(R.drawable.edittextbackground);
                                addToCartBtn.setTextColor(Color.parseColor("#1BA9C3"));
                                Map<String, Object> addProduct = new HashMap<>();
                                addProduct.put("product_ID_" + DBqueries.cartList.size(), productId);
                                addProduct.put("product_sub_ID_" + DBqueries.cartList.size(), subId);
                                addProduct.put("no_of_items_" + DBqueries.cartList.size(), Long.valueOf(no_of_items.getText().toString()));
                                addProduct.put("product_main_ID_" + DBqueries.cartList.size(), mainId);
                                addProduct.put("list_size", (long) (DBqueries.cartList.size() + 1));
                                firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USERS_DATA")
                                        .document("MY_CART").update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            if (DBqueries.cartItemModelList.size() != 0) {
                                                DBqueries.cartItemModelList.add(DBqueries.cartItemModelList.size(), new ItemListModel(
                                                        image,
                                                        productName,
                                                        price,
                                                        cutedPrice,
                                                        savedPrice,
                                                        itemQuantityText,
                                                        minItems,
                                                        productId,
                                                        subId,
                                                        true,
                                                        quantityModelList.get(position).getNo_of_items(),
                                                        mainId
                                                ));
                                            }
                                            DBqueries.cartList.add(productId);
                                            DBqueries.cartSubIds.add(subId);
                                            DBqueries.cartNoOfItems.add(Long.valueOf(no_of_items.getText().toString()));
                                            Toast.makeText(itemView.getContext(), "Added to cart Successfully", Toast.LENGTH_SHORT).show();
                                            notifyDataSetChanged();
                                            if (DBqueries.cartList.size() < 99) {
                                                ItemListActivity.badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                                            } else {
                                                ItemListActivity.badgeCount.setText("99");
                                            }
                                            ItemListAdapter.running_cart_query = false;
                                            if (ItemDetailsActivity.finalBuyBtn.getVisibility() == View.GONE) {
                                                ItemDetailsActivity.finalBuyBtn.setVisibility(View.VISIBLE);
                                            }

                                        } else {
                                            ItemListAdapter.running_cart_query = false;
                                            String error = task.getException().getMessage();
                                            Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }

                        //ADD TO CART WORK

                    }
                });
            }catch (Exception e) {
                e.printStackTrace();
            }
            }


        }
    }
}
