package com.techov8.retaildost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Sign_in_fragment extends Fragment {

    public Sign_in_fragment() {
    }

    private EditText email, password;
    private Button signInBtn;
    private FirebaseAuth firebaseAuth;
    private TextView dontHaveAnAccount, forgetPassword,signInTermBtn;
    private FrameLayout parentFrameLayout;
    private ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_sign_in_fragment, container, false);

        dontHaveAnAccount =view.findViewById(R.id.tv_dont_have_an_account);
        forgetPassword =view.findViewById(R.id.sign_in_forgot_password);
        parentFrameLayout = requireActivity().findViewById(R.id.register_framelayout);
        email =view.findViewById(R.id.sign_in_email);
        password =view.findViewById(R.id.sign_in_password);
        firebaseAuth =FirebaseAuth.getInstance();
        signInBtn =view.findViewById(R.id.yes_btn);
        progressBar = view.findViewById(R.id.sign_in_progress_bar);
        signInTermBtn=view.findViewById(R.id.sign_in_term_btn);


        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dontHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.setSignUpFragment=true;
                setFragment(new Sign_up_fragment());
            }
        });
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.onReset_password =true;
                setFragment(new Reset_Password());

            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPassword();

            }
        });

        signInTermBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent=new Intent("android.intent.action.VIEW", Uri.parse("http://www.retaildost.in/privacy_policy/"));
                startActivity(viewIntent);
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction= requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right,R.anim.slideout_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
    private void checkInputs(){
        if (!TextUtils.isEmpty(email.getText())) {

                if (!TextUtils.isEmpty(password.getText()) && password.length() >= 7) {

                        signInBtn.setEnabled(true);
                    signInBtn.setTextColor(Color.rgb(255, 255, 255));

                } else {
                    signInBtn.setEnabled(false);
                    signInBtn.setTextColor(Color.argb(50, 255, 255, 255));
                }

        } else {
            signInBtn.setEnabled(false);
            signInBtn.setTextColor(Color.argb(50, 255, 255, 255));

        }
    }

    private void checkEmailAndPassword(){
        String email_pattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
        String emailV= Sign_in_fragment.this.email.getText().toString();
        String passwordV= password.getText().toString();

        if (email.getText().toString().matches(email_pattern)) {
            if (password.length()>=8) {
                progressBar.setVisibility(View.VISIBLE);
                signInBtn.setEnabled(false);
                signInBtn.setTextColor(Color.argb(50, 255, 255, 255));

                firebaseAuth.signInWithEmailAndPassword(emailV, passwordV).addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Login error, Please login again", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            signInBtn.setEnabled(true);
                            signInBtn.setTextColor(Color.rgb( 255, 255, 255));

                        } else {
                           mainIntent();
                        }
                    }
                });
            }else{
                Toast.makeText(getContext(), "Incorrect Email or Password", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getContext(), "Incorrect Email or Password", Toast.LENGTH_SHORT).show();
        }


    }

    private void mainIntent(){
        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String userId=task.getResult().getString("account_ID");
                        FirebaseFirestore.getInstance().collection("USER_IDS").document(userId).update("is_logged_in", true)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        SharedPreferences prefs3 =getActivity().getSharedPreferences("LoginData", 0);
                                        SharedPreferences.Editor editor3 = prefs3.edit();
                                        editor3.putString("data", "Yes");
                                        editor3.apply();
                                        Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_LONG).show();
                                        Intent ii = new Intent(getActivity(), MainActivity.class);
                                        startActivity(ii);
                                        getActivity().finish();
                                    }
                                });

                    }
                });

    }
}
