package com.techov8.retaildost;

import java.util.ArrayList;

public class ItemListModel {

    private String image,name,price,cuttedPrice,savedPrice,id,subId,quantity,mainId;
    private Long min_items,no_of_items;
    private boolean available;
    private ArrayList<String> tags;
    private boolean isAdded;
    public ItemListModel(String image,String name, String price, String cuttedPrice, String savedPrice, String quantity, Long min_items, String id,String subId,boolean available,Long no_of_items,String mainId) {
        this.image = image;
        this.name=name;
        this.price = price;
        this.cuttedPrice = cuttedPrice;
        this.savedPrice = savedPrice;
        this.quantity = quantity;
        this.min_items = min_items;
        this.id = id;
        this.subId=subId;
        this.available=available;
        this.no_of_items=no_of_items;
        this.mainId=mainId;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public String getSubId() {
        return subId;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getPrice() {
        return price;
    }

    public String getCuttedPrice() {
        return cuttedPrice;
    }

    public String getSavedPrice() {
        return savedPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public Long getMin_items() {
        return min_items;
    }

    public String getId() {
        return id;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setCuttedPrice(String cuttedPrice) {
        this.cuttedPrice = cuttedPrice;
    }

    public void setSavedPrice(String savedPrice) {
        this.savedPrice = savedPrice;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Long getNo_of_items() {
        return no_of_items;
    }

    public void setNo_of_items(Long no_of_items) {
        this.no_of_items = no_of_items;
    }

    public String getMainId() {
        return mainId;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
