package com.techov8.retaildost;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MyCartFragment extends Fragment {

    public MyCartFragment() {
        // Required empty public constructor
    }

    public static Dialog loadingDialog;
    public static  CartAdapter cartAdapter;
    public  static TextView cartTotalItem,cartTotalSaving,cartTotalAmount;
    public  static LinearLayout  linearLayout;
    public static ConstraintLayout emptyCartLayout;
    private Button continueShoppingBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_cart, container, false);

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        try {
            emptyCartLayout = view.findViewById(R.id.empty_cart_layout);
            continueShoppingBtn = view.findViewById(R.id.cart_continue_shopping_btn);
            RecyclerView cartItemsRecyclerView = view.findViewById(R.id.cart_items_recycler_view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            cartItemsRecyclerView.setLayoutManager(linearLayoutManager);
            Button continueBtn = view.findViewById(R.id.cart_continue_btn);
            cartTotalAmount = view.findViewById(R.id.total_cart_amount);
            cartTotalItem = view.findViewById(R.id.cart_total_item);
            cartTotalSaving = view.findViewById(R.id.cart_total_savings);
            linearLayout = view.findViewById(R.id.linearLayout5);
            cartAdapter = new CartAdapter(DBqueries.cartItemModelList, false);
            cartItemsRecyclerView.setAdapter(cartAdapter);

            if (DBqueries.cartItemModelList.size() == 0) {
                DBqueries.loadCartList(getContext(), loadingDialog, true, new TextView(getContext()));

            } else {
                DBqueries.calculateCartTotalAmount(loadingDialog);
            }
            cartAdapter.notifyDataSetChanged();

            continueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadingDialog.show();

                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DBqueries.userEmail = task.getResult().getString("email");
                                        DBqueries.AccountID = task.getResult().getString("account_ID");
                                        DBqueries.userShopName = task.getResult().getString("shop_name");
                                        DBqueries.shopType = task.getResult().getString("shop_type");
                                        DBqueries.ownerName = task.getResult().getString("owner_name");
                                        DBqueries.ownerPhone = task.getResult().getString("owner_phone");

                                        if (task.getResult().getBoolean("is_shipping_available")) {
                                            DBqueries.isShippingAvailable = true;
                                            DBqueries.locality = task.getResult().getString("locality");
                                            DBqueries.city = task.getResult().getString("city");
                                            DBqueries.district = task.getResult().getString("district");
                                            DBqueries.pinCode = task.getResult().getString("pin");
                                            DBqueries.phoneNo = task.getResult().getString("phone");
                                        }
                                        Intent deliveryIntent;
                                        if (DBqueries.city != null) {
                                            deliveryIntent = new Intent(getContext(), DeliveryActivity.class);
                                            deliveryIntent.putExtra("TYPE", "");
                                            deliveryIntent.putExtra("ORDER_ID", "");
                                        } else {
                                            deliveryIntent = new Intent(getContext(), AddressActivity.class);
                                            deliveryIntent.putExtra("INTENT", "update_address");
                                            deliveryIntent.putExtra("INTENT_TYPE", "");
                                        }
                                        startActivity(deliveryIntent);
                                        loadingDialog.dismiss();
                                    } else {
                                        loadingDialog.dismiss();
                                        String error = Objects.requireNonNull(task.getException()).getMessage();
                                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                }
            });

            continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}