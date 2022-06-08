package com.techov8.retaildost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DeliveryActivity extends AppCompatActivity {

    public static  DeliveryPageAdapter deliveryPageAdapter;
    private ConstraintLayout orderConfirmationLayout;
    private TextView orderId;
    private Button placeOrderBtn,continueShoppingBtn;
    private String order_id;
    private FirebaseFirestore firebaseFirestore;
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        try {

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

            placeOrderBtn = findViewById(R.id.place_order_btn);
            Dialog loadingDialog = new Dialog(this);
            loadingDialog.setContentView(R.layout.loading_progress_dialog);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            loadingDialog.show();
            String type = getIntent().getStringExtra("TYPE");
            String ORDER_ID = getIntent().getStringExtra("ORDER_ID");
            boolean isFromOrder = type.equals("ORDER_DETAILS");
            /////loading dialog
            orderConfirmationLayout = findViewById(R.id.order_confirmation_layout);
            continueShoppingBtn = findViewById(R.id.continue_shopping_btn_text);
            orderId = findViewById(R.id.order_id);

            RecyclerView deliveryRecyclerView = findViewById(R.id.delivery_recycler_view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            deliveryRecyclerView.setLayoutManager(linearLayoutManager);

            firebaseFirestore = FirebaseFirestore.getInstance();
            UID = FirebaseAuth.getInstance().getUid();
            DBqueries.loadDeliveryPage(loadingDialog, isFromOrder, ORDER_ID);
            deliveryPageAdapter = new DeliveryPageAdapter(DBqueries.deliveryPageModelList);
            deliveryRecyclerView.setAdapter(deliveryPageAdapter);
            deliveryPageAdapter.notifyDataSetChanged();

            if (DBqueries.orderItemModelList.size() == 0) {
                DBqueries.loadOrderPage(loadingDialog, false);
            }

            if (isFromOrder) {
                getSupportActionBar().setTitle("Order Details");
                placeOrderBtn.setVisibility(View.GONE);
            } else {
                getSupportActionBar().setTitle("Checkout");
                placeOrderBtn.setVisibility(View.VISIBLE);

                placeOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        order_id = "RD" + DBqueries.AccountID + (DBqueries.no_of_orders + 1);
                        Map<String, Object> orderData = new HashMap<>();
                        for (int x = 0; x < DBqueries.cartItemModelList.size(); x++) {
                            orderData.put("product_ID_" + x, DBqueries.cartItemModelList.get(x).getId());
                            orderData.put("product_sub_ID_" + x, DBqueries.cartItemModelList.get(x).getSubId());
                            orderData.put("no_of_items_" + x, DBqueries.cartItemModelList.get(x).getNo_of_items());
                            orderData.put("product_main_ID_" + x, DBqueries.cartItemModelList.get(x).getMainId());
                            orderData.put("image_" + x, DBqueries.cartItemModelList.get(x).getImage());
                            orderData.put("name_" + x, DBqueries.cartItemModelList.get(x).getName());
                            orderData.put("price_" + x, DBqueries.cartItemModelList.get(x).getPrice());
                            orderData.put("cuted_price_" + x, DBqueries.cartItemModelList.get(x).getCuttedPrice());
                            orderData.put("saved_price_" + x, DBqueries.cartItemModelList.get(x).getSavedPrice());
                            orderData.put("quantity_" + x, DBqueries.cartItemModelList.get(x).getQuantity());

                        }
                        orderData.put("Order_Id", order_id);
                        orderData.put("Ordered_time", FieldValue.serverTimestamp());
                        Calendar c = Calendar.getInstance();
                        final Date d = c.getTime();

                        orderData.put("Expected_delivery_date", "Within 7 days");
                        orderData.put("Payment_mode", "Cash On Delivery");
                        orderData.put("Order_Status", "Pending");

                        orderData.put("Total_Items", DBqueries.totalItem);
                        orderData.put("Total_Amount", DBqueries.totalAmount);
                        orderData.put("Saved_Amount", DBqueries.savedAmount);
                        orderData.put("Delivery_charge", "-");
                        orderData.put("NO_OF_ITEMS", DBqueries.cartItemModelList.size());


                        firebaseFirestore.collection("USERS").document(UID).collection("USERS_DATA")
                                .document("MY_ORDERS").collection("ITEM_LIST").document(order_id).set(orderData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                         Map<String, Object> orderData2 = new HashMap<>();
                                        for (int x = 0; x < DBqueries.cartItemModelList.size(); x++) {
                                            orderData2.put("product_ID_" + x, DBqueries.cartItemModelList.get(x).getId());
                                            orderData2.put("product_sub_ID_" + x, DBqueries.cartItemModelList.get(x).getSubId());
                                            orderData2.put("no_of_items_" + x, DBqueries.cartItemModelList.get(x).getNo_of_items());
                                            orderData2.put("product_main_ID_" + x, DBqueries.cartItemModelList.get(x).getMainId());
                                            orderData2.put("image_" + x, DBqueries.cartItemModelList.get(x).getImage());
                                            orderData2.put("name_" + x, DBqueries.cartItemModelList.get(x).getName());
                                            orderData2.put("price_" + x, DBqueries.cartItemModelList.get(x).getPrice());
                                            orderData2.put("cuted_price_" + x, DBqueries.cartItemModelList.get(x).getCuttedPrice());
                                            orderData2.put("saved_price_" + x, DBqueries.cartItemModelList.get(x).getSavedPrice());
                                            orderData2.put("quantity_" + x, DBqueries.cartItemModelList.get(x).getQuantity());

                                        }
                                        orderData2.put("Order_Id", order_id);
                                        orderData2.put("User_Id", UID);
                                        orderData2.put("Ordered_time", FieldValue.serverTimestamp());
                                        Calendar c = Calendar.getInstance();
                                        final Date d = c.getTime();

                                        orderData2.put("Expected_delivery_date", "Within 7 days");
                                        orderData2.put("Payment_mode", "Cash On Delivery");
                                        orderData2.put("Order_Status", "Pending");

                                        String fullNameText = DBqueries.userName + " - " + DBqueries.phoneNo;
                                        String fullAddress = DBqueries.locality + ", " + DBqueries.city + ", " + DBqueries.district + ", Jhrakhand";

                                        orderData2.put("full_name",fullNameText);
                                        orderData2.put("full_address",fullAddress);

                                        orderData2.put("Total_Items", DBqueries.totalItem);
                                        orderData2.put("Total_Amount", DBqueries.totalAmount);
                                        orderData2.put("Saved_Amount", DBqueries.savedAmount);
                                        orderData2.put("Delivery_charge", "-");
                                        orderData2.put("NO_OF_ITEMS", DBqueries.cartItemModelList.size());

                                        firebaseFirestore.collection("ORDERS").document(order_id).set(orderData2)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        DBqueries.orderItemModelList.add(DBqueries.orderItemModelList.size(), new OrderItemModel(
                                                                order_id,
                                                                "Pending",
                                                                DBqueries.totalAmount,
                                                                "Cash On Delivery",
                                                                d,
                                                                "Within 7 days"
                                                        ));
                                                        firebaseFirestore.collection("USERS").document(UID).update("no_of_orders", (DBqueries.no_of_orders + 1));
                                                        DBqueries.cartItemModelList.clear();
                                                        DBqueries.cartList.clear();
                                                        DBqueries.mainIds.clear();
                                                        DBqueries.cartSubIds.clear();
                                                        DBqueries.cartNoOfItems.clear();
                                                        firebaseFirestore.collection("USERS").document(UID).collection("USERS_DATA")
                                                                .document("MY_CART").update("list_size", 0)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        showConfirmationLayout();
                                                                    }
                                                                });

                                                    }
                                                });
                                    }
                                });
                    }
                });
            }

            ////////////////////
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void showConfirmationLayout(){
        try {
            placeOrderBtn.setEnabled(false);
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
            orderId.setText("Order Id: " + order_id);
            if (MainActivity.mainActivity != null) {
                MainActivity.mainActivity.finish();
                MainActivity.mainActivity = null;
                MainActivity.showCart = false;
            }
            orderConfirmationLayout.setVisibility(View.VISIBLE);
            continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mainIntent = new Intent(DeliveryActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}