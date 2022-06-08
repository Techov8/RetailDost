package com.techov8.retaildost;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import static com.techov8.retaildost.DBqueries.firebaseFirestore;
import static com.techov8.retaildost.MainActivity.showCart;

public class ItemDetailsAdapter  extends RecyclerView.Adapter{

    List<ItemDetailsModel> itemDetailsModelList;
    private int lastPosition = -1;
    private final String productID, mainId;
    public static  QuantityAdapter quantityAdapter;
    private boolean isFromList;
    public ItemDetailsAdapter(List<ItemDetailsModel> itemDetailsModelList,String productID,String mainId,boolean isFromList) {
        this.itemDetailsModelList = itemDetailsModelList;
        this.productID=productID;
        this.mainId=mainId;
        this.isFromList=isFromList;
    }

    @Override
    public int getItemViewType(int position) {
        switch (itemDetailsModelList.get(position).getType()) {
            case 0:
                return ItemDetailsModel.BASIC_DETAILS;
            case 1:
                return ItemDetailsModel.SINGLE_PRODUCT;
            case 2:
                return ItemDetailsModel.MULTIPLE_PRODUCT;
            case 3:
                return ItemDetailsModel.PRODUCT_DESCRIPTION;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ItemDetailsModel.BASIC_DETAILS:
                View basicView = LayoutInflater.from(parent.getContext()).inflate(R.layout.basic_item_details_layout, parent, false);
                return new ItemDetailsAdapter.basicViewHolder(basicView);
            case ItemDetailsModel.SINGLE_PRODUCT:
                View singleProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_product_details_layout, parent, false);
                return new ItemDetailsAdapter.singleProductViewHolder(singleProductView);
            case ItemDetailsModel.MULTIPLE_PRODUCT:
                View multipleProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rate_chart_layout, parent, false);
                return new ItemDetailsAdapter.multipleProductViewHolder(multipleProductView);
            case ItemDetailsModel.PRODUCT_DESCRIPTION:
                View productDescriptionView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pd_details_layout, parent, false);
                return new ItemDetailsAdapter.productDescriptionViewHolder(productDescriptionView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (itemDetailsModelList.get(position).getType()) {
            case ItemDetailsModel.BASIC_DETAILS:
                String fullName = itemDetailsModelList.get(position).getItemName();
                String discount = itemDetailsModelList.get(position).getDiscount();
                List<String> images=itemDetailsModelList.get(position).getItemImages();
                ((ItemDetailsAdapter.basicViewHolder) holder).setBasicDetails(fullName,discount,images);
                break;
            case ItemDetailsModel.SINGLE_PRODUCT:
                String name=itemDetailsModelList.get(position).getItemName();
                String image= itemDetailsModelList.get(position).getImage();
                String price = itemDetailsModelList.get(position).getPrice();
                String cutedPrice = itemDetailsModelList.get(position).getCutedPrice();
                String saved=itemDetailsModelList.get(position).getSavedPrice();
                String quantity = itemDetailsModelList.get(position).getQuantity();
                Long min=itemDetailsModelList.get(position).getMinItem();
                Long no=itemDetailsModelList.get(position).getNo_of_item();


                ((ItemDetailsAdapter.singleProductViewHolder) holder).setSingleProductDetails(image,name,price,cutedPrice,saved,quantity,min,no);
                break;

            case ItemDetailsModel.MULTIPLE_PRODUCT:
               // List<ItemListModel> itemListModelList = itemDetailsModelList.get(position).getItemListModelList();
                ((ItemDetailsAdapter.multipleProductViewHolder) holder).setMultipleProductDetails();
                break;
            case ItemDetailsModel.PRODUCT_DESCRIPTION:
                String description = itemDetailsModelList.get(position).getProductDescription();
                ((ItemDetailsAdapter.productDescriptionViewHolder) holder).setProductDescription(description);
                break;
            default:
        }

        if(lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }


    }

    @Override
    public int getItemCount() {
        return itemDetailsModelList.size();
    }


    public static class basicViewHolder extends RecyclerView.ViewHolder {

        private TextView itemName,discountText;
        private TabLayout viewpagerIndicator;
        private ViewPager itemImagesViewPager;

        public basicViewHolder(@NonNull View itemView) {
            super(itemView);
            viewpagerIndicator=itemView.findViewById(R.id.view_pager_indicator);
            itemImagesViewPager=itemView.findViewById(R.id.product_images_view_pager);
            discountText = itemView.findViewById(R.id.discount_text);
            itemName = itemView.findViewById(R.id.pd_item_name);

        }
        private void setBasicDetails(String name,String discount,List<String> images){
            ItemImagesAdapter productImagesAdapter = new ItemImagesAdapter(images);
            itemImagesViewPager.setAdapter(productImagesAdapter);
            itemName.setText(name);
            discountText.setText(discount+"% OFF");
        }
    }

    public  class singleProductViewHolder extends RecyclerView.ViewHolder {
        private TextView spPrice,spCutedPrice,spQuantity,spNoOfItems;
        private ImageView spMinusBtn,spPlusBtn;
        private Button spAddToCartBtn,spBuyNowBtn;
        public singleProductViewHolder(@NonNull View itemView) {
            super(itemView);
            spPrice=itemView.findViewById(R.id.d_our_price);
            spCutedPrice=itemView.findViewById(R.id.d_mrp);
            spQuantity=itemView.findViewById(R.id.d_quantity);
            spNoOfItems=itemView.findViewById(R.id.no_of_items);
            spAddToCartBtn = itemView.findViewById(R.id.pd_add_to_cart_btn);
            spBuyNowBtn = itemView.findViewById(R.id.pd_buy_now_btn);
            spMinusBtn=itemView.findViewById(R.id.minus_items);
            spPlusBtn=itemView.findViewById(R.id.plus_items);
        }
        private void setSingleProductDetails(final String image, final String name, final String price, final String cutedPrice, final String saved, final String quantity, final Long minItem, Long no_of_item){
            try {
                spPrice.setText("₹ " + price);
                spCutedPrice.setText("₹ " + cutedPrice);
                spQuantity.setText(quantity);
                spNoOfItems.setText(String.valueOf(no_of_item));

                if (DBqueries.cartList.contains(productID) && DBqueries.cartSubIds.contains("Basic")) {
                    spAddToCartBtn.setText("Added");
                } else {
                    spAddToCartBtn.setText("Add to Cart");
                }

                spMinusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Long temp = Long.valueOf(spNoOfItems.getText().toString());
                        if (temp > minItem) {
                            temp = temp - 1;
                            spNoOfItems.setText(String.valueOf(temp));

                        }
                    }
                });

                spPlusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Long temp = Long.valueOf(spNoOfItems.getText().toString());
                        temp = temp + 1;
                        spNoOfItems.setText(String.valueOf(temp));
                    }
                });


                spBuyNowBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isFromList) {
                            callSaveCartData(true, image, name, price, cutedPrice, saved, quantity, minItem);
                        }

                    }
                });
                spAddToCartBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isFromList) {
                            callSaveCartData(false, image, name, price, cutedPrice, saved, quantity, minItem);
                        }

                    }
                });
            }catch (Exception e) {
                e.printStackTrace();
            }

        }
        private void callSaveCartData(boolean isBuy, final String image, final String name, final String price, final String cuted, final String saved, final String quantity, final Long min){


            if (!ItemListAdapter.running_cart_query) {
                ItemListAdapter.running_cart_query = true;
                if (DBqueries.cartList.contains(productID) && DBqueries.cartSubIds.contains("Basic")) {
                    ItemListAdapter.running_cart_query = false;
                    if(isBuy) {
                        Intent cartIntent = new Intent(itemView.getContext(), MainActivity.class);
                        showCart = true;
                        itemView.getContext().startActivity(cartIntent);

                    }
                } else {

                    ItemDetailsActivity.loadingDialog.show();
                    Map<String, Object> addProduct = new HashMap<>();
                    addProduct.put("product_ID_" + DBqueries.cartList.size(), productID);
                    addProduct.put("product_sub_ID_" + DBqueries.cartList.size(), "Basic");
                    addProduct.put("no_of_items_" + DBqueries.cartList.size(), Long.valueOf(spNoOfItems.getText().toString()));
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
                                            productID,
                                            "Basic",
                                            true,
                                            Long.valueOf(spNoOfItems.getText().toString()),
                                            mainId
                                    ));
                                }
                                DBqueries.cartList.add(productID);
                                DBqueries.cartSubIds.add("Basic");
                                DBqueries.cartNoOfItems.add(Long.valueOf(spNoOfItems.getText().toString()));
                                Toast.makeText(itemView.getContext(), "Added to cart Successfully", Toast.LENGTH_SHORT).show();
                                if (DBqueries.cartList.size() < 99) {
                                    ItemListActivity.badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                                } else {
                                    ItemListActivity.badgeCount.setText("99");
                                }
                                ItemListAdapter.running_cart_query = false;

                            } else {
                                ItemListAdapter.running_cart_query = false;
                                String error = task.getException().getMessage();
                                Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    ItemDetailsActivity.loadingDialog.dismiss();
                    ItemListActivity.refreshList=true;
                }
            }
        }
    }

    public class multipleProductViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;


        public multipleProductViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.rate_recycler_view);

        }
        private void setMultipleProductDetails() {
            try {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                final List<ItemListModel> quantityModelList = new ArrayList<>();
                firebaseFirestore.collection("PRODUCTS").document(mainId).collection("ITEM_LIST").document(productID).collection("ITEM_QUANTITIES").get()
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
                                            productID,
                                            documentSnapshot.getString("sub_id"),
                                            true,
                                            (long) 1,
                                            mainId
                                    ));

                                }
                                quantityAdapter = new QuantityAdapter(quantityModelList, false, new Dialog(itemView.getContext()), -1);
                                recyclerView.setAdapter(quantityAdapter);
                                quantityAdapter.notifyDataSetChanged();

                            }
                        });


            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class productDescriptionViewHolder extends RecyclerView.ViewHolder {
        private TextView productDescription;

        public productDescriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            productDescription = itemView.findViewById(R.id.pd_product_details);
        }
        private void setProductDescription(String description){
            productDescription.setText(description);
        }
    }




}
