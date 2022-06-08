package com.techov8.retaildost;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class GridCategoryAdapter extends BaseAdapter {

    List<GridCategoryModel> gridCategoryModelList;

    boolean isGrid;
    public GridCategoryAdapter(List<GridCategoryModel> gridCategoryModelList, boolean isGrid) {
        this.gridCategoryModelList = gridCategoryModelList;
        this.isGrid=isGrid;
    }

    @Override
    public int getCount() {
        return gridCategoryModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
       final View view;

        if(convertView == null){

            ImageView categoryImage;
            TextView name;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_grid_category_layout_item, null);
            categoryImage=view.findViewById(R.id.hospital_item_logo);
            name=view.findViewById(R.id.hospital_item_name);
            Glide.with(parent.getContext()).load(gridCategoryModelList.get(position).getCategoryImage()).apply(new RequestOptions().placeholder(R.drawable.product_placeholder)).into(categoryImage);
            final String nameId=gridCategoryModelList.get(position).getCategoryTitle();
            name.setText(nameId);
            name.setBackgroundColor(Color.parseColor(gridCategoryModelList.get(position).getTextBackGround()));
            categoryImage.setBackgroundColor(Color.parseColor(gridCategoryModelList.get(position).getImageBackground()));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent listIntent=new Intent(view.getContext(),ItemListActivity.class);
                    listIntent.putExtra("main_id",gridCategoryModelList.get(position).getId());
                    view.getContext().startActivity(listIntent);
                }
            });



        }else{
            view = convertView;
        }
        return view;
    }
}
