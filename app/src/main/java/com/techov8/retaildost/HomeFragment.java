package com.techov8.retaildost;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static com.techov8.retaildost.DBqueries.lists;
import static com.techov8.retaildost.DBqueries.loadFragmentData;


public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    private   HomePageAdapter adapter;
    private ImageView noInternetConnection;
    @SuppressLint("StaticFieldLeak")
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private RecyclerView homePageRecyclerView;
    private List<HomePageModel> homePageModelFakeList = new ArrayList<>();
    private Button retryBtn;
    public static Boolean isFromUserDetails=false;

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        noInternetConnection = root.findViewById(R.id.no_internet_connection);
        retryBtn = root.findViewById(R.id.retry_button);


        homePageRecyclerView =root.findViewById(R.id.testing);
        LinearLayoutManager testingLayoutManager=new LinearLayoutManager(getContext());
        testingLayoutManager.setOrientation(RecyclerView.VERTICAL);
        homePageRecyclerView.setLayoutManager(testingLayoutManager);

        //home page fake list
        List<SliderModel> sliderModelFakeList = new ArrayList<>();
        sliderModelFakeList.add(new SliderModel("null","#ffffff"));
        sliderModelFakeList.add(new SliderModel("null","#ffffff"));
        sliderModelFakeList.add(new SliderModel("null","#ffffff"));

        List<GridCategoryModel> gridCategoryModelFakeList = new ArrayList<>();
        gridCategoryModelFakeList.add(new GridCategoryModel("","","","#ffffff","#f2f2f2"));
        gridCategoryModelFakeList.add(new GridCategoryModel("","","","#ffffff","#f2f2f2"));
        gridCategoryModelFakeList.add(new GridCategoryModel("","","","#ffffff","#f2f2f2"));
        gridCategoryModelFakeList.add(new GridCategoryModel("","","","#ffffff","#f2f2f2"));
        gridCategoryModelFakeList.add(new GridCategoryModel("","","","#ffffff","#f2f2f2"));
        gridCategoryModelFakeList.add(new GridCategoryModel("","","","#ffffff","#f2f2f2"));
        gridCategoryModelFakeList.add(new GridCategoryModel("","","","#ffffff","#f2f2f2"));
        gridCategoryModelFakeList.add(new GridCategoryModel("","","","#ffffff","#f2f2f2"));
        gridCategoryModelFakeList.add(new GridCategoryModel("","","","#ffffff","#f2f2f2"));
        gridCategoryModelFakeList.add(new GridCategoryModel("","","","#ffffff","#f2f2f2"));
        gridCategoryModelFakeList.add(new GridCategoryModel("","","","#ffffff","#f2f2f2"));
        gridCategoryModelFakeList.add(new GridCategoryModel("","","","#ffffff","#f2f2f2"));

        homePageModelFakeList.add(new HomePageModel(1, sliderModelFakeList));
        homePageModelFakeList.add(new HomePageModel(0,"","#ffffff"));
        homePageModelFakeList.add(new HomePageModel(2,"#dfdfdf", gridCategoryModelFakeList,(long)12));
        //home page fake list

        connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()) {
            MainActivity.noConnection=true;
            MainActivity.drawer.setDrawerLockMode(0);
            retryBtn.setVisibility(View.GONE);
            noInternetConnection.setVisibility(View.GONE);
            homePageRecyclerView.setVisibility(View.VISIBLE);

            if(lists.size() == 0 || isFromUserDetails){
                DBqueries.clearData();
                adapter=new HomePageAdapter(homePageModelFakeList);
                loadFragmentData(homePageRecyclerView, getContext());
            }else{
                adapter=new HomePageAdapter(lists);
                adapter.notifyDataSetChanged();
            }
            homePageRecyclerView.setAdapter(adapter);
        }else{
            MainActivity.noConnection=false;
            MainActivity.drawer.setDrawerLockMode(1);
            homePageRecyclerView.setVisibility(View.GONE);
            Glide.with(this).load(R.drawable.product_placeholder).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.VISIBLE);
        }

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadPage();
            }
        });

        return root;
    }

    @SuppressLint("WrongConstant")
    private void reloadPage(){
        MainActivity.noConnection=true;
        networkInfo = connectivityManager.getActiveNetworkInfo();
        retryBtn.setVisibility(View.GONE);

        DBqueries.clearData();
        if(networkInfo != null && networkInfo.isConnected()) {
            MainActivity.drawer.setDrawerLockMode(0);
            noInternetConnection.setVisibility(View.GONE);
            homePageRecyclerView.setVisibility(View.VISIBLE);


            adapter = new HomePageAdapter(homePageModelFakeList);


            homePageRecyclerView.setAdapter(adapter);
            loadFragmentData(homePageRecyclerView, getContext());


        }else{
            MainActivity.drawer.setDrawerLockMode(1);
            homePageRecyclerView.setVisibility(View.GONE);
            Glide.with(requireContext()).load(R.drawable.product_placeholder).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
    }
}
