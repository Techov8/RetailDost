package com.techov8.retaildost;

import java.util.List;

public class HomePageModel {

    private int type;
    private String backgroundColor;
    public static final int STRIP_AD_BANNER=0;
    public static final  int BANNER_SLIDER=1;
    public static final int GRID_PRODUCT_VIEW = 2;

    //banner slider
    private List<SliderModel> sliderModelList;
    public HomePageModel(int type, List<SliderModel> sliderModelList) {
        this.type = type;
        this.sliderModelList = sliderModelList;
    }
    public int getType() {
        return type;
    }
    public List<SliderModel> getSliderModelList() {
        return sliderModelList;
    }

    //banner slider
    //strip
    private String resource;
    public HomePageModel(int type, String resource, String backgroundColor) {
        this.type = type;
        this.resource = resource;
        this.backgroundColor = backgroundColor;
    }

    public String getResource() {
        return resource;
    }
    public String getBackgroundColor() {
        return backgroundColor;
    }
    //strip

    ////grid product
    private List<GridCategoryModel> gridCategoryModelList;
    private long itemNo;
    public HomePageModel(int type,String backgroundColor, List<GridCategoryModel> gridCategoryModelList,long itemNo) {
        this.type = type;
        this.gridCategoryModelList = gridCategoryModelList;
        this.backgroundColor=backgroundColor;
        this.itemNo=itemNo;
    }
    public List<GridCategoryModel> getGridCategoryModelList() {
        return gridCategoryModelList;
    }

    public long getItemNo() {
        return itemNo;
    }
    ////grid product

}
