package com.techov8.retaildost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddressActivity extends AppCompatActivity {
    private EditText city;
    private EditText locality;
    private EditText pinCode;
    private EditText mobileNo;

    private String [] districtList ;
    private String selectedDistrict;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Your Address");

            loadingDialog = new Dialog(this);
            loadingDialog.setContentView(R.layout.loading_progress_dialog);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            /////loading dialog
            districtList = getResources().getStringArray(R.array.india_states);

            city = findViewById(R.id.city);
            locality = findViewById(R.id.locality);
            pinCode = findViewById(R.id.pin_code);
            mobileNo = findViewById(R.id.mobile_no);
            Spinner stateSpinner = findViewById(R.id.district_spinner);
            Button saveBtn = findViewById(R.id.save_btn);

            ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, districtList);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            stateSpinner.setAdapter(spinnerAdapter);

            stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedDistrict = districtList[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if (getIntent().getStringExtra("INTENT_TYPE").equals("edit_address")) {
                city.setText(DBqueries.city);
                locality.setText(DBqueries.locality);
                pinCode.setText(DBqueries.pinCode);
                mobileNo.setText(DBqueries.phoneNo);

                for (int i = 0; i < districtList.length; i++) {
                    if (districtList[i].equals(DBqueries.district)) {
                        stateSpinner.setSelection(i);
                    }
                }
                saveBtn.setText("Update");
            } else {
                saveBtn.setText("Save");
            }


            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!TextUtils.isEmpty(city.getText())) {
                        if (!TextUtils.isEmpty(locality.getText())) {
                            if (!TextUtils.isEmpty(pinCode.getText()) && pinCode.getText().length() == 6) {
                                if (!TextUtils.isEmpty(mobileNo.getText()) && mobileNo.getText().length() == 10) {


                                    loadingDialog.show();
                                    final Map<String, Object> addressData = new HashMap<>();

                                    addressData.put("phone", mobileNo.getText().toString());
                                    addressData.put("pin", pinCode.getText().toString());
                                    addressData.put("city", city.getText().toString());
                                    addressData.put("locality", locality.getText().toString());
                                    addressData.put("state", "Jharkhand");
                                    addressData.put("district", selectedDistrict);
                                    addressData.put("is_shipping_available", true);

                                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).update(addressData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    DBqueries.phoneNo = mobileNo.getText().toString();
                                                    DBqueries.city = city.getText().toString();
                                                    DBqueries.locality = locality.getText().toString();
                                                    DBqueries.pinCode = pinCode.getText().toString();
                                                    DBqueries.district = selectedDistrict;

                                                    Intent addressIntent;
                                                    if (getIntent().getStringExtra("INTENT").equals("edit_address_account")) {
                                                    } else {
                                                        addressIntent = new Intent(AddressActivity.this, DeliveryActivity.class);
                                                        addressIntent.putExtra("TYPE", "");
                                                        addressIntent.putExtra("ORDER_ID", "");
                                                        startActivity(addressIntent);
                                                    }
                                                    finish();

                                                    loadingDialog.dismiss();
                                                }
                                            });
                                } else {
                                    mobileNo.requestFocus();
                                    Toast.makeText(AddressActivity.this, "Please provide a valid No.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                pinCode.requestFocus();
                                Toast.makeText(AddressActivity.this, "Please provide a valid pin code", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            locality.requestFocus();
                        }
                    } else {
                        city.requestFocus();
                    }
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