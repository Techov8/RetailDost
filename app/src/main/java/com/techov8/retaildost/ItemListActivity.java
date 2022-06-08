package com.techov8.retaildost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import static com.techov8.retaildost.MainActivity.showCart;
public class ItemListActivity extends AppCompatActivity {

    Dialog loadingDialog;
    public static  TextView badgeCount;
    public static ItemListAdapter itemListAdapter;
    public static MenuItem cartItem;
    private String id;
    public static boolean refreshList=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        /////loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        /////loading dialog

        try {
            id = getIntent().getStringExtra("main_id");
            getSupportActionBar().setTitle(id);
            RecyclerView itemListRecyclerView = findViewById(R.id.item_list_recycler_view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            itemListRecyclerView.setLayoutManager(linearLayoutManager);

            DBqueries.itemListModelList.clear();
            DBqueries.loadListItems(id, loadingDialog);
            itemListAdapter = new ItemListAdapter(DBqueries.itemListModelList, id);
            itemListRecyclerView.setAdapter(itemListAdapter);
            itemListAdapter.notifyDataSetChanged();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public  boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);

        cartItem = menu.findItem(R.id.main_cart_icon);
        cartItem.setActionView(R.layout.badge_layout);

        ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
        badgeIcon.setImageResource(R.mipmap.cart);
        badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

            if (DBqueries.cartList.size() == 0) {
                DBqueries.loadCartList(ItemListActivity.this, loadingDialog,false,badgeCount);
            }else {
                badgeCount.setVisibility(View.VISIBLE);

                if (DBqueries.cartList.size() < 99) {
                    badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                } else {
                    badgeCount.setText("99");
                }


        }


        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent cartIntent = new Intent(ItemListActivity.this, MainActivity.class);
                    showCart = true;
                    startActivity(cartIntent);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.main_cart_icon) {
                Intent cartIntent = new Intent(ItemListActivity.this, MainActivity.class);
                showCart = true;
                startActivity(cartIntent);
                return true;

        }
        else if (id == R.id.main_search_icon) {
            Intent searchIntent = new Intent(ItemListActivity.this, SearchActivity.class);
            startActivity(searchIntent);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(refreshList) {
            DBqueries.itemListModelList.clear();
            DBqueries.loadListItems(id, loadingDialog);
        }
    }
}