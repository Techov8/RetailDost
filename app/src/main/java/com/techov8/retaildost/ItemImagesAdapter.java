package com.techov8.retaildost;


import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ItemImagesAdapter extends PagerAdapter {
    private List<String> ItemImages;

    public ItemImagesAdapter(List<String> ItemImages) {
        this.ItemImages = ItemImages;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView productImage = new ImageView(container.getContext());
        productImage.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(container.getContext()).load(ItemImages.get(position)).apply(new RequestOptions().placeholder(R.drawable.product_placeholder)).into(productImage);
        container.addView(productImage,0);
        return productImage;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView)object);

    }

    @Override
    public int getCount() {
        return ItemImages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}

