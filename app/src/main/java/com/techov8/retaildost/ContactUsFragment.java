package com.techov8.retaildost;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class ContactUsFragment extends Fragment {



    public ContactUsFragment() {
        // Required empty public constructor
    }

    private Button callBtn,whatsappBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_contact_us, container, false);

        try {

            callBtn = view.findViewById(R.id.call_btn);
            whatsappBtn = view.findViewById(R.id.whatsapp_btn);

            callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri u = Uri.parse("tel:9304566832");
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try {
                        // Launch the Phone app's dialer with a phone
                        // number to dial a call.
                        startActivity(i);
                    } catch (SecurityException s) {
                        // show() method display the toast with
                        // exception message.
                        Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_LONG)
                                .show();
                    }
                }
            });


            whatsappBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String contact = "+91 9304566832"; // use country code with your phone number
                    String url = "https://api.whatsapp.com/send?phone=" + contact;
                    try {
                        PackageManager pm = getActivity().getPackageManager();
                        pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    } catch (PackageManager.NameNotFoundException e) {
                        Toast.makeText(getActivity(), "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
            return view;

    }
}