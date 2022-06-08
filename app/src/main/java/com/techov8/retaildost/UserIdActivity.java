package com.techov8.retaildost;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class UserIdActivity extends AppCompatActivity {

    private EditText userId;
    private String name,email;
    private Dialog loadingDialog;
     private  boolean isFound=false,isLoggedIn=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_id);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        userId=findViewById(R.id.user_id_no);
        Button userIdBtn = findViewById(R.id.user_id_btn);

        userIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(userId.getText().toString())){
                    try {
                        checkUserId();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        SharedPreferences prefs3 = getSharedPreferences("RegisterData", 0);
        SharedPreferences.Editor editor3 = prefs3.edit();
        editor3.putString("data", "Yes");
        editor3.apply();

        SharedPreferences prefs = getSharedPreferences("FirebaseData",
                0);
        name = prefs.getString("name_data",
                "");
        email = prefs.getString("email_data",
                "");

    }
    private void checkUserId() {
        loadingDialog.show();
            FirebaseFirestore.getInstance().collection("USER_IDS").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                if (documentSnapshot.get("user_id").equals(userId.getText().toString())) {
                                    isFound=true;
                                    if (!documentSnapshot.getBoolean("is_logged_in")) {
                                        isLoggedIn=true;
                                        loadingDialog.show();
                                        Map<String, Object> setData = new HashMap<>();
                                        setData.put("account_ID", userId.getText().toString());
                                        setData.put("shop_name", documentSnapshot.getString("shop_name"));
                                        setData.put("shop_type", documentSnapshot.getString("shop_type"));
                                        setData.put("owner_name", documentSnapshot.getString("owner_name"));
                                        setData.put("owner_phone", documentSnapshot.getString("owner_phone"));
                                        setData.put("full_name", name);
                                        setData.put("email", email);
                                        setData.put("is_shipping_available", false);
                                        setData.put("no_of_orders", 0);

                                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).set(setData)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {


                                                        Map<String, Object> nDATA = new HashMap<>();
                                                        nDATA.put("list_size", 0);
                                                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USERS_DATA").document("MY_CART").set(nDATA);
                                                        SharedPreferences prefs3 = getSharedPreferences("LoginData", 0);
                                                        SharedPreferences.Editor editor3 = prefs3.edit();
                                                        editor3.putString("data", "Yes");
                                                        editor3.apply();

                                                        FirebaseFirestore.getInstance().collection("USER_IDS").document(userId.getText().toString()).update("is_logged_in", true);
                                                        Intent mainIntent = new Intent(UserIdActivity.this, MainActivity.class);
                                                        startActivity(mainIntent);
                                                        finish();

                                                    }
                                                });


                                    }
                                }
                            }
                            if(!isFound){
                                Toast.makeText(UserIdActivity.this, "Incorrect ID!", Toast.LENGTH_SHORT).show();
                            }else{
                                if(!isLoggedIn){
                                    Toast.makeText(UserIdActivity.this, "You are Logged In!", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });
        loadingDialog.dismiss();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}