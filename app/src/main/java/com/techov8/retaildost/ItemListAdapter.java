package com.techov8.retaildost;

import android.app.Dialog;
import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    List<ItemListModel> itemListModelList;
    private String mainId;
    public static boolean running_cart_query = false;

    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();


    private  boolean fromSearch;

    public void setFromSearch(boolean fromSearch) {
        this.fromSearch = fromSearch;
    }

    public void setItemListModelList(List<ItemListModel> itemListModelList,String mainId) {
        this.itemListModelList = itemListModelList;
        this.mainId=mainId;
    }

    public ItemListAdapter(List<ItemListModel> itemListModelList,String mainId) {
        this.itemListModelList = itemListModelList;
        this.mainId=mainId;
    }

    @NonNull
    @Override
    public ItemListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListAdapter.ViewHolder holder, int position) {
        String resource=itemListModelList.get(position).getImage();
        String name=itemListModelList.get(position).getName();
        String price=itemListModelList.get(position).getPrice();
        String cuted=itemListModelList.get(position).getCuttedPrice();
        String saved=itemListModelList.get(position).getSavedPrice();
        String quantity=itemListModelList.get(position).getQuantity();
        Long min=itemListModelList.get(position).getMin_items();
        String id=itemListModelList.get(position).getId();
        String subId=itemListModelList.get(position).getSubId();
        boolean available=itemListModelList.get(position).isAvailable();
        Long itemNo=itemListModelList.get(position).getNo_of_items();
        holder.setData(resource,name,price,cuted,saved,quantity,min,id,subId,available,position,itemNo);
    }

    @Override
    public int getItemCount() {
        return itemListModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage, minusBtn, plusBtn;
        private TextView itemName, no_of_items, itemQuantity, itemPrice, itemCutedPrice, itemSavedPrice, minQty;
        private Button addToCartBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemCutedPrice = itemView.findViewById(R.id.item_cutted_price);
            itemSavedPrice = itemView.findViewById(R.id.saved_price);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            no_of_items = itemView.findViewById(R.id.no_of_items);
            addToCartBtn = itemView.findViewById(R.id.add_to_cart_btn);
            minusBtn = itemView.findViewById(R.id.minus_items);
            plusBtn = itemView.findViewById(R.id.plus_items);
            minQty = itemView.findViewById(R.id.min_qty);
        }

        private void setData(final String image, final String name, final String price, final String cuted, final String saved, final String quantity, final Long min, final String id, final String subId, final boolean available
                , final int position, final Long itemNo) {
try{
            Glide.with(itemView.getContext()).load(image).apply(new RequestOptions().placeholder(R.drawable.product_placeholder)).into(itemImage);
            itemName.setText(name);
            itemPrice.setText("₹ " + price);
            itemCutedPrice.setText("₹ " + cuted);
            itemSavedPrice.setText("Save ₹ " + saved + " on per item");
            itemQuantity.setText("Pack of "+quantity);
            minQty.setText("Min qty: " + min + " pcs");
            no_of_items.setText(String.valueOf(itemNo));

            minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Long temp = itemNo;
                    if (temp > min) {
                        temp = temp - 1;
                        no_of_items.setText(String.valueOf(temp));
                        itemListModelList.get(position).setNo_of_items(temp);
                        notifyItemChanged(position);
                    }
                }
            });
            plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Long temp = itemNo;
                    temp = temp + 1;
                    no_of_items.setText(String.valueOf(temp));
                    itemListModelList.get(position).setNo_of_items(temp);
                    notifyItemChanged(position);

                }
            });


            final Dialog checkQuantityDialog = new Dialog(itemView.getContext());
            checkQuantityDialog.setContentView(R.layout.item_quantity_dialog);
            checkQuantityDialog.setCancelable(true);
            checkQuantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            final RecyclerView quantityRecyclerView = checkQuantityDialog.findViewById(R.id.quantity_dialog_recycler_view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            quantityRecyclerView.setLayoutManager(linearLayoutManager);

            final List<ItemListModel> quantityModelList = new ArrayList<>();
            firebaseFirestore.collection("PRODUCTS").document(mainId).collection("ITEM_LIST").document(id).collection("ITEM_QUANTITIES").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                quantityModelList.add(new ItemListModel(documentSnapshot.getString("image"),
                                        documentSnapshot.getString("product_name"),
                                        documentSnapshot.getString("price"),
                                        documentSnapshot.getString("cuted_price"),
                                        documentSnapshot.getString("saved_price"),
                                        documentSnapshot.getString("quantity_name"),
                                        documentSnapshot.getLong("min_items"),
                                        id,
                                        documentSnapshot.getString("sub_id"),
                                        true,
                                        (long) 1,
                                        mainId
                                ));
                            }
                            QuantityAdapter quantityAdapter = new QuantityAdapter(quantityModelList, true, checkQuantityDialog, position);
                            quantityRecyclerView.setAdapter(quantityAdapter);
                            quantityAdapter.notifyDataSetChanged();
                        }
                    });


            itemQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkQuantityDialog.show();

                }
            });

            if (DBqueries.cartList.size() == 0) {
                DBqueries.loadCartList(itemView.getContext(), new Dialog(itemView.getContext()), false, ItemListActivity.badgeCount);
            }
            if (DBqueries.cartList.contains(id) && DBqueries.cartSubIds.contains(subId)) {
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


                    //
                    if (!running_cart_query) {
                        running_cart_query = true;
                        if (DBqueries.cartList.contains(id) && DBqueries.cartSubIds.contains(subId)) {
                            running_cart_query = false;
                            Toast.makeText(itemView.getContext(), "Already added to cart", Toast.LENGTH_SHORT).show();
                            addToCartBtn.setText("Added");
                            addToCartBtn.setBackgroundResource(R.drawable.edittextbackground);
                            addToCartBtn.setTextColor(Color.parseColor("#1BA9C3"));
                        } else {

                            addToCartBtn.setText("Added");
                            addToCartBtn.setBackgroundResource(R.drawable.edittextbackground);
                            addToCartBtn.setTextColor(Color.parseColor("#1BA9C3"));
                            Map<String, Object> addProduct = new HashMap<>();
                            addProduct.put("product_ID_" + DBqueries.cartList.size(), id);
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
                                                    name,
                                                    price,
                                                    cuted,
                                                    saved,
                                                    quantity,
                                                    min,
                                                    id,
                                                    subId,
                                                    available,
                                                    Long.valueOf(no_of_items.getText().toString()),
                                                    mainId
                                            ));
                                        }
                                        DBqueries.cartList.add(id);
                                        DBqueries.cartSubIds.add(subId);
                                        DBqueries.cartNoOfItems.add(Long.valueOf(no_of_items.getText().toString()));
                                        Toast.makeText(itemView.getContext(), "Added to cart Successfully", Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                        if (DBqueries.cartList.size() < 99) {
                                            ItemListActivity.badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                                        } else {
                                            ItemListActivity.badgeCount.setText("99");
                                        }
                                        running_cart_query = false;


                                    } else {
                                        running_cart_query = false;
                                        String error = task.getException().getMessage();
                                        Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }

                    //
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent detailsIntent = new Intent(itemView.getContext(), ItemDetailsActivity.class);
                    detailsIntent.putExtra("main_id", mainId);
                    detailsIntent.putExtra("id", id);
                    detailsIntent.putExtra("is_list", "YES");
                    itemView.getContext().startActivity(detailsIntent);

                }
            });
        }catch (Exception e) {
                e.printStackTrace();
            }
        }



    }
}
