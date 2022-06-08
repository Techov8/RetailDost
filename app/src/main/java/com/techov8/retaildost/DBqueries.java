package com.techov8.retaildost;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

public class DBqueries {

    public static FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
    public static String userName,userEmail,ownerPhone,userShopName,ownerName,shopType,AccountID;
    public static Long no_of_orders;
    //shipping details
    public static String phoneNo,locality,city,district,pinCode;
    public static boolean isShippingAvailable=false;
    public static String totalItem,totalAmount,savedAmount;

    public static List<HomePageModel> lists =new ArrayList<>();
    public static List<ItemListModel> itemListModelList=new ArrayList<>();
    public static List<ItemListModel> cartItemModelList=new ArrayList<>();
    public static List<String> cartList = new ArrayList<>();
    public static List<String> cartSubIds=new ArrayList<>();
    public static List<String> mainIds=new ArrayList<>();
    public static List<Long> cartNoOfItems=new ArrayList<>();
    public static List<DeliveryPageModel> deliveryPageModelList=new ArrayList<>();
    public static List<ItemListModel> orderDetails=new ArrayList<>();
    public static List<OrderItemModel> orderItemModelList=new ArrayList<>();
    public static List<ItemDetailsModel> itemDetailsModelList=new ArrayList<>();

    public static void loadFragmentData(final RecyclerView homePageRecyclerView, final Context context){
        try {
            lists.clear();
            firebaseFirestore.collection("HOME")
                    .orderBy("Index").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            if (((long) documentSnapshot.get("Index")) == 1) {
                                List<SliderModel> sliderModelList = new ArrayList<>();
                                long no_of_banners = (long) documentSnapshot.get("no_of_banners");
                                for (long x = 1; x < no_of_banners + 1; x++) {
                                    sliderModelList.add(new SliderModel(documentSnapshot.getString("banner_" + x),
                                            documentSnapshot.getString("banner_" + x + "_background")));
                                }
                                lists.add(new HomePageModel(1, sliderModelList));

                            } else if (((long) documentSnapshot.get("Index")) == 2) {
                                lists.add(new HomePageModel(0, documentSnapshot.getString("strip_ad_banner"), documentSnapshot.get("background").toString()));
                            } else if (((long) documentSnapshot.get("Index")) == 3) {
                                List<GridCategoryModel> gridLayoutScrollModelList = new ArrayList<>();
                                long no_of_icons = (long) documentSnapshot.get("no_of_icons");
                                for (long x = 1; x < no_of_icons + 1; x++) {
                                    gridLayoutScrollModelList.add(new GridCategoryModel(documentSnapshot.getString("Icon_" + x + "_image"),
                                            documentSnapshot.getString("Icon_" + x + "_title"),
                                            documentSnapshot.getString("id_" + x),
                                            documentSnapshot.getString("text_background_" + x),
                                            documentSnapshot.getString("image_background_" + x)
                                    ));
                                }

                                lists.add(new HomePageModel(2, documentSnapshot.getString("layout_background"), gridLayoutScrollModelList, no_of_icons));

                            }

                        }
                        HomePageAdapter homePageAdapter = new HomePageAdapter(lists);
                        homePageRecyclerView.setAdapter(homePageAdapter);
                        homePageAdapter.notifyDataSetChanged();

                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void loadListItems(final String id, final Dialog loadingDialog){
        try {
            itemListModelList.clear();

            firebaseFirestore.collection("PRODUCTS").document(id).collection("ITEM_LIST").orderBy("Index", Query.Direction.ASCENDING).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                itemListModelList.add(new ItemListModel(
                                        documentSnapshot.getString("item_image_1"),
                                        documentSnapshot.getString("name"),
                                        documentSnapshot.getString("price"),
                                        documentSnapshot.getString("cuted_price"),
                                        documentSnapshot.getString("saved_price"),
                                        documentSnapshot.getString("quantity_name"),
                                        documentSnapshot.getLong("min_items"),
                                        documentSnapshot.getId(),
                                        "Basic",
                                        (boolean) documentSnapshot.get("is_available"),
                                        documentSnapshot.getLong("no_of_items"),
                                        id
                                ));

                            }
                            ItemListActivity.itemListAdapter.notifyDataSetChanged();
                            loadingDialog.dismiss();
                        }
                    });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void loadCartList(final Context context, final Dialog loadingDialog, final boolean loadProductData, final TextView badgeCount){

        try {
            cartList.clear();
            cartSubIds.clear();
            cartNoOfItems.clear();
            mainIds.clear();
            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USERS_DATA").document("MY_CART")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        final long size = (long) task.getResult().get("list_size");
                        for (long x = 0; x < size; x++) {
                            cartList.add(task.getResult().get("product_ID_" + x).toString());
                            cartSubIds.add(task.getResult().getString("product_sub_ID_" + x));
                            cartNoOfItems.add(task.getResult().getLong("no_of_items_" + x));
                            mainIds.add(task.getResult().getString("product_main_ID_" + x));
                            if (loadProductData) {

                                final String productID = task.getResult().get("product_ID_" + x).toString();
                                final String productSubId = task.getResult().getString("product_sub_ID_" + x);
                                final Long no_of_items = task.getResult().getLong("no_of_items_" + x);
                                final String mainId = task.getResult().getString("product_main_ID_" + x);
                                firebaseFirestore.collection("PRODUCTS").document(mainId).collection("ITEM_LIST").document(productID)
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            final DocumentSnapshot documentSnapshot = task.getResult();

                                            firebaseFirestore.collection("PRODUCTS").document(mainId).collection("ITEM_LIST").document(productID).collection("ITEM_QUANTITIES").document(productSubId).get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task2) {

                                                            cartItemModelList.add(new ItemListModel(
                                                                    documentSnapshot.getString("item_image_1"),
                                                                    documentSnapshot.getString("name"),
                                                                    task2.getResult().getString("price"),
                                                                    task2.getResult().getString("cuted_price"),
                                                                    task2.getResult().getString("saved_price"),
                                                                    task2.getResult().getString("quantity_name"),
                                                                    documentSnapshot.getLong("min_items"),
                                                                    productID,
                                                                    productSubId,
                                                                    true,
                                                                    no_of_items,
                                                                    mainId
                                                            ));
                                                            calculateCartTotalAmount(loadingDialog);
                                                            MyCartFragment.cartAdapter.notifyDataSetChanged();
                                                            loadingDialog.dismiss();

                                                        }
                                                    });

                                        } else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            }
                        }

                        loadingDialog.dismiss();

                        if (cartList.size() != 0) {
                            if (loadProductData) {
                                MyCartFragment.emptyCartLayout.setVisibility(View.GONE);
                            }
                            badgeCount.setText(String.valueOf(cartList.size()));
                            badgeCount.setVisibility(View.VISIBLE);

                        } else {
                            if (loadProductData) {
                                MyCartFragment.emptyCartLayout.setVisibility(View.VISIBLE);
                            }
                            badgeCount.setVisibility(View.INVISIBLE);
                        }

                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void removeFromCart(final String productId, final Context context,final Dialog dialog){
        try {
            for (int y = 0; y < cartList.size(); y++) {
                if (cartList.get(y).equals(productId)) {
                    final String removedProductId = cartList.get(y);
                    final String removedsubProductId = cartSubIds.get(y);
                    final Long no_of_items = cartNoOfItems.get(y);
                    final String mainId = mainIds.get(y);
                    cartList.remove(y);
                    cartSubIds.remove(y);
                    cartNoOfItems.remove(y);
                    mainIds.remove(y);


                    Map<String, Object> updateCartList = new HashMap<>();

                    for (int x = 0; x < cartList.size(); x++) {
                        updateCartList.put("product_ID_" + x, cartList.get(x));
                        updateCartList.put("product_sub_ID_" + x, cartSubIds.get(x));
                        updateCartList.put("no_of_items_" + x, cartNoOfItems.get(x));
                        updateCartList.put("product_main_ID_" + x, mainIds.get(x));
                    }
                    updateCartList.put("list_size", (long) cartList.size());

                    final int finalY = y;
                    firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USERS_DATA")
                            .document("MY_CART").set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                if (cartItemModelList.size() != 0) {
                                    for (int i = 0; i < cartItemModelList.size() - 1; i++) {
                                        if (cartItemModelList.get(i).getId().equals(productId)) {
                                            cartItemModelList.remove(i);
                                            MyCartFragment.cartAdapter.notifyDataSetChanged();
                                            calculateCartTotalAmount(dialog);
                                            ItemListActivity.refreshList = true;
                                        }
                                    }
                                }
                                if (cartList.size() == 0) {
                                    MyCartFragment.linearLayout.setVisibility(View.GONE);
                                    cartItemModelList.clear();
                                    MyCartFragment.emptyCartLayout.setVisibility(View.VISIBLE);
                                }
                                Toast.makeText(context, "Removed Successfully", Toast.LENGTH_SHORT).show();
                            } else {

                                cartList.add(finalY, removedProductId);
                                cartSubIds.add(finalY, removedsubProductId);
                                cartNoOfItems.add(finalY, no_of_items);
                                mainIds.add(finalY, mainId);
                                String error = task.getException().getMessage();
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    break;

                }
            }
            dialog.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void calculateCartTotalAmount(Dialog loadingDialog){
        try {
            int totalItems = 0;
            Double totalItemPrice = 0.0;
            int totalSavings = 0;

            for (int x = 0; x < cartItemModelList.size(); x++) {
                int quantity = Integer.parseInt(String.valueOf(DBqueries.cartItemModelList.get(x).getNo_of_items()));
                totalItems = totalItems + quantity;
                totalItemPrice = totalItemPrice + (Double.parseDouble(DBqueries.cartItemModelList.get(x).getPrice()) * quantity);
                totalSavings = totalSavings + ((int) Double.parseDouble(DBqueries.cartItemModelList.get(x).getSavedPrice()) * quantity);
            }
            if (totalItems == 0) {
                MyCartFragment.linearLayout.setVisibility(View.GONE);
            } else {
                totalItem = String.valueOf(totalItems);
                totalAmount = String.format("%.2f", totalItemPrice);
                savedAmount = String.valueOf(totalSavings);

                MyCartFragment.cartTotalItem.setText(totalItem);
                MyCartFragment.cartTotalAmount.setText("₹ " + totalAmount);
                MyCartFragment.cartTotalSaving.setText("₹ " + savedAmount);
                MyCartFragment.linearLayout.setVisibility(View.VISIBLE);
            }

            loadingDialog.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void loadDeliveryPage(Dialog loadingDialog,boolean isFromOrder,String ORDER_ID){
        try {
            deliveryPageModelList.clear();

            if (isFromOrder) {
                orderDetails.clear();

                firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USERS_DATA").document("MY_ORDERS").collection("ITEM_LIST").document(ORDER_ID)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        for (long x = 0; x < task.getResult().getLong("NO_OF_ITEMS"); x++) {

                            orderDetails.add(new ItemListModel(
                                    task.getResult().getString("image_" + x),
                                    task.getResult().getString("name_" + x),
                                    task.getResult().getString("price_" + x),
                                    task.getResult().getString("cuted_price_" + x),
                                    task.getResult().getString("saved_price_" + x),
                                    task.getResult().getString("quantity_" + x),
                                    (long) 1,
                                    task.getResult().getString("product_ID_" + x),
                                    task.getResult().getString("product_sub_ID_" + x),
                                    true,
                                    task.getResult().getLong("no_of_items_" + x),
                                    task.getResult().getString("product_main_ID_" + x)


                            ));
                        }
                        deliveryPageModelList.add(new DeliveryPageModel(1, orderDetails));
                        deliveryPageModelList.add(new DeliveryPageModel(2, task.getResult().getString("Total_Items"),
                                task.getResult().getString("Total_Amount"),
                                task.getResult().getString("Saved_Amount")));
                        DeliveryActivity.deliveryPageAdapter.notifyDataSetChanged();

                    }
                });

            } else {
                String fullNameText = userName + " - " + phoneNo;
                String fullAddress = locality + ", " + city + ", " + district + ", Jhrakhand";
                deliveryPageModelList.add(new DeliveryPageModel(0, fullNameText, fullAddress, pinCode));
                deliveryPageModelList.add(new DeliveryPageModel(1, cartItemModelList));
                deliveryPageModelList.add(new DeliveryPageModel(2, totalItem, totalAmount, savedAmount));
            }

            loadingDialog.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void loadOrderPage(final Dialog loadingDialog, final boolean isFromOrder){
        try {

            orderItemModelList.clear();

            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USERS_DATA").document("MY_ORDERS").collection("ITEM_LIST")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                        orderItemModelList.add(new OrderItemModel(
                                documentSnapshot.getString("Order_Id"),
                                documentSnapshot.getString("Order_Status"),
                                documentSnapshot.getString("Total_Amount"),
                                documentSnapshot.getString("Payment_mode"),
                                documentSnapshot.getDate("Ordered_time"),
                                documentSnapshot.getString("Expected_delivery_date")
                        ));

                    }
                    if (isFromOrder) {

                        if (orderItemModelList.size() == 0) {
                            MyOrderFragment.noOrderLayout.setVisibility(View.VISIBLE);
                        } else {
                            MyOrderFragment.noOrderLayout.setVisibility(View.GONE);
                            MyOrderFragment.orderItemAdapter.notifyDataSetChanged();
                        }
                    }
                    loadingDialog.dismiss();

                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void loadItemDetailsPage(final Dialog loadingDialog, final String mainId, final String productId){
        try {
            itemDetailsModelList.clear();
            firebaseFirestore.collection("PRODUCTS").document(mainId).collection("ITEM_LIST").document(productId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                final List<String> itemImages = new ArrayList<>();
                                for (long x = 1; x < (long) task.getResult().get("no_of_images") + 1; x++) {
                                    itemImages.add(task.getResult().get("item_image_" + x).toString());
                                }

                                itemDetailsModelList.add(new ItemDetailsModel(0, task.getResult().getString("discount"), task.getResult().getString("name"), itemImages));

                                if (!task.getResult().getBoolean("is_multiple_products")) {
                                    itemDetailsModelList.add(new ItemDetailsModel(1,
                                            task.getResult().getString("item_image_1"),
                                            task.getResult().getString("price"),
                                            task.getResult().getString("cuted_price"),
                                            task.getResult().getString("quantity_name"),
                                            task.getResult().getString("saved_price"),
                                            task.getResult().getLong("min_items"),
                                            task.getResult().getLong("no_of_items")
                                    ));
                                } else {

                                    itemDetailsModelList.add(new ItemDetailsModel(2));

                                }

                                itemDetailsModelList.add(new ItemDetailsModel(3, task.getResult().getString("description")));


                            }
                            ItemDetailsActivity.itemDetailsAdapter.notifyDataSetChanged();
                            loadingDialog.dismiss();
                        }
                    });

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void clearData(){
        lists.clear();
        cartItemModelList.clear();
        itemListModelList.clear();
        cartList.clear();
        cartSubIds.clear();
        cartNoOfItems.clear();
        mainIds.clear();
        deliveryPageModelList.clear();
        orderItemModelList.clear();
    }

}
