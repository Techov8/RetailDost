package com.techov8.retaildost;

public class SliderModel {
    private String banner;
    private String background_color;

    public SliderModel(String banner, String background_color) {
        this.banner = banner;
        this.background_color = background_color;
    }

    String  getBanner() {
        return banner;
    }

    String getBackground_color() {
        return background_color;
    }

}