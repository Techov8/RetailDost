package com.techov8.retaildost;
public class GridCategoryModel {


    private String categoryImage;
    private String categoryTitle;
    private String id;
    private String textBackGround,imageBackground;


    public GridCategoryModel(String categoryImage, String categoryTitle,String id,String textBackGround,String imageBackground) {
        this.categoryImage = categoryImage;
        this.categoryTitle = categoryTitle;
        this.id=id;
        this.textBackGround=textBackGround;
        this.imageBackground=imageBackground;
    }

    public String getId() {
        return id;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public String getTextBackGround() {
        return textBackGround;
    }

    public String getImageBackground() {
        return imageBackground;
    }
}
