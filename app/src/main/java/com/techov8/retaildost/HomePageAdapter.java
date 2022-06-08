package com.techov8.retaildost;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class HomePageAdapter extends RecyclerView.Adapter {
    private List<HomePageModel> homePageModelList;

    private int lastPosition = -1;

    public HomePageAdapter(List<HomePageModel> homePageModelList) {
        this.homePageModelList = homePageModelList;

    }

    @Override
    public int getItemViewType(int position) {
        switch (homePageModelList.get(position).getType()) {
            case 0:
                return HomePageModel.STRIP_AD_BANNER;
            case 1:
                return HomePageModel.BANNER_SLIDER;

            case 2:
                return HomePageModel.GRID_PRODUCT_VIEW;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        switch (viewType) {
            case HomePageModel.STRIP_AD_BANNER:
                View stripAdView = LayoutInflater.from(parent.getContext()).inflate(R.layout.strip_ad_layout, parent, false);
                return new StripAdBannerViewHolder(stripAdView);
            case HomePageModel.BANNER_SLIDER:
                View bannerSliderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliding_ad_banner, parent, false);
                return new BannerSliderViewHolder(bannerSliderView);
            case HomePageModel.GRID_PRODUCT_VIEW:
                View gridProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_grid_category_layout, parent, false);
                return new GridProductViewHolder(gridProductView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (homePageModelList.get(position).getType()) {
            case HomePageModel.STRIP_AD_BANNER:
                String resource = homePageModelList.get(position).getResource();
                String color = homePageModelList.get(position).getBackgroundColor();
                ((StripAdBannerViewHolder) holder).setStripAd(resource, color);
                break;
            case HomePageModel.BANNER_SLIDER:
                List<SliderModel> sliderModelList = homePageModelList.get(position).getSliderModelList();
                ((BannerSliderViewHolder) holder).setBanner_slider_view_pager(sliderModelList);
                break;


            case HomePageModel.GRID_PRODUCT_VIEW:
                String gridLayoutColor = homePageModelList.get(position).getBackgroundColor();
                List<GridCategoryModel> gridProductScrollModelList = homePageModelList.get(position).getGridCategoryModelList();
                long itemNo=homePageModelList.get(position).getItemNo();
                ((GridProductViewHolder) holder).setGridProductLayout(gridProductScrollModelList,gridLayoutColor,itemNo);
                break;

            default:
        }

        if(lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }

    }

    @Override
    public int getItemCount() {
        return homePageModelList.size();
    }

    public static class BannerSliderViewHolder extends RecyclerView.ViewHolder {
        private ViewPager banner_slider_view_pager;
        private int current_page ;
        private Timer timer;
        final private long DELAY_TIME = 3000;
        final private long PERIOD_TIME = 3000;
        private List<SliderModel> arrangedList;

        @SuppressLint("ClickableViewAccessibility")
        public BannerSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            banner_slider_view_pager = itemView.findViewById(R.id.banner_slider_viewpager);

        }

        @SuppressLint("ClickableViewAccessibility")
        private void setBanner_slider_view_pager(final List<SliderModel> sliderModelList) {
            current_page = 2;
            if(timer != null){
                timer.cancel();
            }
            arrangedList =new ArrayList<>();
            for(int x=0;x<sliderModelList.size();x++){
                arrangedList.add(x,sliderModelList.get(x));

            }
            arrangedList.add(0,sliderModelList.get(sliderModelList.size()-2));
            arrangedList.add(1,sliderModelList.get(sliderModelList.size()-1));

            arrangedList.add(sliderModelList.get(0));
            arrangedList.add(sliderModelList.get(1));

            SliderAdapter sliderAdapter = new SliderAdapter(arrangedList);
            banner_slider_view_pager.setAdapter(sliderAdapter);
            banner_slider_view_pager.setClipToPadding(false);
            banner_slider_view_pager.setPageMargin(20);
            banner_slider_view_pager.setCurrentItem(current_page);
            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    current_page = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        pageLooper(arrangedList);
                    }
                }
            };
            banner_slider_view_pager.addOnPageChangeListener(onPageChangeListener);
            start_banner_slideshow(arrangedList);
            banner_slider_view_pager.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    pageLooper(arrangedList);
                    stop_banner_slideshow();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        start_banner_slideshow(arrangedList);
                    }

                    return false;
                }
            });
        }

        private void pageLooper(List<SliderModel> sliderModelList) {
            if (current_page == sliderModelList.size() - 2) {
                current_page = 2;
                banner_slider_view_pager.setCurrentItem(current_page, false);
            }
            if (current_page == 1) {
                current_page = sliderModelList.size() - 3;
                banner_slider_view_pager.setCurrentItem(current_page, false);
            }
        }

        private void start_banner_slideshow(final List<SliderModel> sliderModelList) {
            final Handler handler = new Handler();
            final Runnable update = new Runnable() {
                @Override
                public void run() {
                    if (current_page >= sliderModelList.size()) {
                        current_page = 1;
                    }
                    banner_slider_view_pager.setCurrentItem(current_page++, true);
                }
            };
            timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, DELAY_TIME, PERIOD_TIME);
        }

        private void stop_banner_slideshow() {
            timer.cancel();
        }


    }

    public static class StripAdBannerViewHolder extends RecyclerView.ViewHolder {
        private ImageView stripAdImage;
        private RelativeLayout strip_ad_container;

        public StripAdBannerViewHolder(@NonNull View itemView) {
            super(itemView);
            //strip ad
            stripAdImage = itemView.findViewById(R.id.strip_ad_image);
            strip_ad_container = itemView.findViewById(R.id.strip_ad_container);

        }

        private void setStripAd(String resource, String color) {

            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.banner_placeholder)).into(stripAdImage);
            stripAdImage.setBackgroundColor(Color.parseColor(color));
        }
    }


    public static class GridProductViewHolder extends RecyclerView.ViewHolder{
        private ConstraintLayout constraintLayout;
        private GridView categoryGridLayout;
        public GridProductViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout=itemView.findViewById(R.id.c_layout);
            categoryGridLayout =itemView.findViewById(R.id.hospital_grid_layout);

        }
        private void setGridProductLayout(final List<GridCategoryModel> gridCategoryModelList,String color,long itemNo){

            categoryGridLayout.setAdapter(new GridCategoryAdapter(gridCategoryModelList,false));
            categoryGridLayout.setBackgroundColor(Color.parseColor(color));

            ViewGroup.LayoutParams layoutParams = categoryGridLayout.getLayoutParams();

            if(itemNo<7){
                layoutParams.height = convertDpToPixels(335,itemView.getContext());
            }else if(itemNo<10){
                layoutParams.height = convertDpToPixels(500,itemView.getContext());
            } else if(itemNo<13){
                layoutParams.height = convertDpToPixels(665,itemView.getContext());

            } else if(itemNo<16){
                layoutParams.height = convertDpToPixels(830,itemView.getContext());
            } else if(itemNo<19){
                layoutParams.height = convertDpToPixels(995,itemView.getContext());
            }
            else if(itemNo<22){
                layoutParams.height = convertDpToPixels(1160,itemView.getContext());
            }else{
                layoutParams.height = convertDpToPixels(1325,itemView.getContext());
            }

            categoryGridLayout.setLayoutParams(layoutParams);
        }
        public static int convertDpToPixels(float dp, Context context){
            Resources resources = context.getResources();
            return (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp,
                    resources.getDisplayMetrics()
            );
        }

    }
}
