package com.techov8.retaildost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import static com.techov8.retaildost.MainActivity.showCart;

public class ItemDetailsActivity extends AppCompatActivity {

    private RecyclerView detailsRecyclerView;
   public static Dialog loadingDialog;
    private String productID,mainId;
    public static Button finalBuyBtn;
    public static   ItemDetailsAdapter itemDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Item Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /////loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        /////loading dialog


        try {
            finalBuyBtn = findViewById(R.id.final_buy_now_btn);
            detailsRecyclerView = findViewById(R.id.item_details_recycler_view);
            productID = getIntent().getStringExtra("id");
            mainId = getIntent().getStringExtra("main_id");
            boolean isFromList;
            isFromList = getIntent().getStringExtra("is_list").equals("YES");
            ////////////

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            detailsRecyclerView.setLayoutManager(linearLayoutManager);
            DBqueries.loadItemDetailsPage(loadingDialog, mainId, productID);

            itemDetailsAdapter = new ItemDetailsAdapter(DBqueries.itemDetailsModelList, productID, mainId, isFromList);
            detailsRecyclerView.setAdapter(itemDetailsAdapter);
            itemDetailsAdapter.notifyDataSetChanged();


            // BUY NOW WORK
            finalBuyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mainIntent = new Intent(ItemDetailsActivity.this, MainActivity.class);
                    showCart = true;
                    startActivity(mainIntent);
                    finish();
                }
            });
        }catch (Exception e) {
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