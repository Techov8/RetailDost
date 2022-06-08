package com.techov8.retaildost;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class MyAccountFragment extends Fragment {

    public MyAccountFragment() {
        // Required empty public constructor
    }

    private TextView userName,userEmail,accountID,shopName,ownerName,contactNo,shopType,shippingFullName,shippingFullAddress,shippingPinCode;
    private Button editAddressBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_my_account, container, false);

        final Dialog loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        userName=view.findViewById(R.id.user_name);
        userEmail=view.findViewById(R.id.user_email);
        accountID=view.findViewById(R.id.account_id);
        shopName=view.findViewById(R.id.shop_name);
        shopType=view.findViewById(R.id.shop_type);
        ownerName=view.findViewById(R.id.owner_name);
        contactNo=view.findViewById(R.id.a_contact_no);
        shippingFullName=view.findViewById(R.id.a_full_name);
        shippingFullAddress=view.findViewById(R.id.a_full_address);
        shippingPinCode=view.findViewById(R.id.a_pin_code);
        editAddressBtn=view.findViewById(R.id.edit_addess_btn);


        try {
            if (DBqueries.userEmail == null) {
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

                                    userName.setText(DBqueries.userName);
                                    userEmail.setText(DBqueries.userEmail);
                                    accountID.setText(DBqueries.AccountID);
                                    shopName.setText(DBqueries.userShopName);
                                    shopType.setText(DBqueries.shopType);
                                    ownerName.setText(DBqueries.ownerName);
                                    contactNo.setText(DBqueries.ownerPhone);

                                    if (task.getResult().getBoolean("is_shipping_available")) {
                                        DBqueries.isShippingAvailable = true;
                                        DBqueries.locality = task.getResult().getString("locality");
                                        DBqueries.city = task.getResult().getString("city");
                                        DBqueries.district = task.getResult().getString("district");
                                        DBqueries.pinCode = task.getResult().getString("pin");
                                        DBqueries.phoneNo = task.getResult().getString("phone");
                                        editAddressBtn.setText("Edit Address");
                                        shippingFullName.setText(DBqueries.userName + "( " + DBqueries.phoneNo + " )");
                                        shippingPinCode.setText(DBqueries.pinCode);
                                        shippingFullAddress.setText(DBqueries.locality + ", " + DBqueries.city + ", " + DBqueries.district + ", Jharkhand");
                                    } else {
                                        editAddressBtn.setText("Add Address");
                                        shippingFullName.setText("-");
                                        shippingFullAddress.setText("-");
                                        shippingPinCode.setText("-");
                                    }
                                    loadingDialog.dismiss();
                                } else {
                                    loadingDialog.dismiss();
                                    String error = Objects.requireNonNull(task.getException()).getMessage();
                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

            } else {
                userName.setText(DBqueries.userName);
                userEmail.setText(DBqueries.userEmail);
                accountID.setText(DBqueries.AccountID);
                shopName.setText(DBqueries.userShopName);
                shopType.setText(DBqueries.shopType);
                ownerName.setText(DBqueries.ownerName);
                contactNo.setText(DBqueries.ownerPhone);

                if (DBqueries.isShippingAvailable) {
                    editAddressBtn.setText("Edit Address");
                    shippingFullName.setText(DBqueries.userName + "( " + DBqueries.phoneNo + " )");
                    shippingPinCode.setText(DBqueries.pinCode);
                    shippingFullAddress.setText(DBqueries.locality + ", " + DBqueries.city + ", " + DBqueries.district + ", Jharkhand");
                } else {
                    editAddressBtn.setText("Add Address");
                    shippingFullName.setText("-");
                    shippingFullAddress.setText("-");
                    shippingPinCode.setText("-");
                }

                loadingDialog.dismiss();
            }


            editAddressBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addressIntent = new Intent(getContext(), AddressActivity.class);
                    if (DBqueries.isShippingAvailable) {
                        addressIntent.putExtra("INTENT_TYPE", "edit_address");
                    } else {
                        addressIntent.putExtra("INTENT_TYPE", "");
                    }
                    addressIntent.putExtra("INTENT", "edit_address_account");
                    startActivity(addressIntent);
                }
            });

        }catch (Exception e) {
            e.printStackTrace();
        }
        return  view;
    }
}