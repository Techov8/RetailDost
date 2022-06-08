package com.techov8.retaildost;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class Reset_Password extends Fragment {

    public Reset_Password() {
        // Required empty public constructor
    }
    private TextView tvw_send_mail,tvw_go_back;
    private EditText edt_rg_mail;
    private Button bt_send;
    private ProgressBar pgb;
    private FrameLayout parent_frame_layout;
    private FirebaseAuth firebaseAuth;
    private ViewGroup email_container;
    private ImageView email_icon;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_reset__password, container, false);
        tvw_send_mail=view.findViewById(R.id.textView11);
        tvw_go_back=view.findViewById(R.id.textView12);
        edt_rg_mail=view.findViewById(R.id.editText);
        bt_send=view.findViewById(R.id.reset_btn);
        pgb=view.findViewById(R.id.progressBar2);
        email_container=view.findViewById(R.id.email_container);
        email_icon=view.findViewById(R.id.imageView);
        parent_frame_layout= requireActivity().findViewById(R.id.register_framelayout);
        firebaseAuth=FirebaseAuth.getInstance();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edt_rg_mail.addTextChangedListener(new TextWatcher() {
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

        tvw_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new Sign_in_fragment());
            }
        });

        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(email_container);
                tvw_send_mail.setVisibility(View.GONE);
                TransitionManager.beginDelayedTransition(email_container);
                tvw_send_mail.setVisibility(View.VISIBLE);
                pgb.setVisibility(View.VISIBLE);
                bt_send.setEnabled(false);
                firebaseAuth.sendPasswordResetEmail(edt_rg_mail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    ScaleAnimation scaleAnimation=new ScaleAnimation(1,0,1,0);
                                    scaleAnimation.setDuration(100);
                                    scaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                                    scaleAnimation.setRepeatMode(Animation.REVERSE);
                                    scaleAnimation.setRepeatCount(1);
                                    scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            tvw_send_mail.setText("dcvbdfb bfbfsb");
                                            tvw_send_mail.setTextColor(Color.green(100));
                                            TransitionManager.beginDelayedTransition(email_container);
                                            tvw_send_mail.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {
                                            email_icon.setImageResource(R.drawable.green_mail);
                                        }
                                    });



                                }else{
                                    String error= Objects.requireNonNull(task.getException()).getMessage();

                                    tvw_send_mail.setText(error);
                                    tvw_send_mail.setTextColor(Color.red(100));
                                    TransitionManager.beginDelayedTransition(email_container);
                                    tvw_send_mail.setTextColor(View.VISIBLE);
                                }
                                pgb.setVisibility(View.GONE);
                                bt_send.setEnabled(true);
                            }
                        });
            }
        });

    }
    private void checkInputs(){
        String email=edt_rg_mail.getText().toString();
        if(email.isEmpty()){
            bt_send.setEnabled(false);
        }else{
            bt_send.setEnabled(true);
        }
    }

    private void setFragment(Sign_in_fragment sign_in_Fragment) {

        FragmentTransaction fragmentTransaction= requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_fron_left,R.anim.slideout_from_right);
        fragmentTransaction.replace(parent_frame_layout.getId(),sign_in_Fragment);
        fragmentTransaction.commit();
    }
}
