package com.techov8.retaildost;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MyOrderFragment extends Fragment {

    public MyOrderFragment() {
    }

    public static OrderItemAdapter orderItemAdapter;
    public static ConstraintLayout noOrderLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_my_order, container, false);

        /////loading dialog
        Dialog loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        /////loading dialog

        try {
            RecyclerView recyclerView = view.findViewById(R.id.my_order_recycler_view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            noOrderLayout = view.findViewById(R.id.no_order_layout);

            if (DBqueries.orderItemModelList.size() == 0) {
                DBqueries.loadOrderPage(loadingDialog, true);
            }

            orderItemAdapter = new OrderItemAdapter(DBqueries.orderItemModelList);
            recyclerView.setAdapter(orderItemAdapter);
            orderItemAdapter.notifyDataSetChanged();

            loadingDialog.dismiss();


        }catch (Exception e) {
            e.printStackTrace();
        }
        return view;

    }
}