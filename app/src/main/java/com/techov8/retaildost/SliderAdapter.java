package com.techov8.retaildost;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapter extends PagerAdapter {
    private List<SliderModel> sliderModalList;
    public SliderAdapter(List<SliderModel>sliderModalList){
        this.sliderModalList=sliderModalList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view= LayoutInflater.from(container.getContext()).inflate(R.layout.slider_layout,container,false);
        ImageView banner=view.findViewById(R.id.imageView13);
        ConstraintLayout banner_container=view.findViewById(R.id.banner_container);
        banner_container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(sliderModalList.get(position).getBackground_color())));
        Glide.with(container.getContext()).load(sliderModalList.get(position).getBanner()).apply(new RequestOptions().placeholder(R.drawable.banner_placeholder)).into(banner);
        container.addView(view,0);
        return view;

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return sliderModalList.size();
    }
}