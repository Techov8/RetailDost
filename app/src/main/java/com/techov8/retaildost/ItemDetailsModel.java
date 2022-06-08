package com.techov8.retaildost;

import java.util.List;

public class ItemDetailsModel {

    private  int type;
    public static final int BASIC_DETAILS=0;
    public static final  int SINGLE_PRODUCT=1;
    public static final int MULTIPLE_PRODUCT = 2;
    public static final int PRODUCT_DESCRIPTION = 3;
    public int getType() {
        return type;
    }

    private String discount,itemName;
    private List<String> itemImages;

    public ItemDetailsModel(int type, String discount, String itemName, List<String> itemImages) {
        this.type = type;
        this.discount = discount;
        this.itemName = itemName;
        this.itemImages = itemImages;
    }

    public String getDiscount() {
        return discount;
    }

    public String getItemName() {
        return itemName;
    }

    public List<String> getItemImages() {
        return itemImages;
    }

    private String cutedPrice,price,quantity,savedPrice,image;
    private Long minItem,no_of_item;

    public ItemDetailsModel(int type,String image, String cutedPrice, String price, String quantity, String savedPrice, Long minItem, Long no_of_item) {
        this.type = type;
        this.image=image;
        this.cutedPrice = cutedPrice;
        this.price = price;
        this.quantity = quantity;
        this.savedPrice = savedPrice;
        this.minItem = minItem;
        this.no_of_item = no_of_item;
    }

    public String getImage() {
        return image;
    }

    public String getSavedPrice() {
        return savedPrice;
    }

    public String getCutedPrice() {
        return cutedPrice;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public Long getMinItem() {
        return minItem;
    }

    public Long getNo_of_item() {
        return no_of_item;
    }


    public ItemDetailsModel(int type) {
        this.type = type;
    }

    private String productDescription;

    public ItemDetailsModel(int type, String productDescription) {
        this.type = type;
        this.productDescription = productDescription;
    }

    public String getProductDescription() {
        return productDescription;
    }
}
