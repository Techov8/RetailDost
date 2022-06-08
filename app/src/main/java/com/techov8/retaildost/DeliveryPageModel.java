package com.techov8.retaildost;

import java.util.List;

public class DeliveryPageModel {
    private  int type;
    public static final int ADDRESS_PAGE=0;
    public static final  int ITEM_RECYCLER_PAGE=1;
    public static final int ITEM_TOTAL_PAGE = 2;
    public int getType() {
        return type;
    }


    /// shipping page
    private String fullName_totalItem,address_total_price,pinCode_saved_price;

    public DeliveryPageModel(int type, String fullName_totalItem, String address_total_price, String pinCode_saved_price) {
        this.type = type;
        this.fullName_totalItem = fullName_totalItem;
        this.address_total_price = address_total_price;
        this.pinCode_saved_price = pinCode_saved_price;
    }

    public String getFullName_totalItem() {
        return fullName_totalItem;
    }

    public String getAddress_total_price() {
        return address_total_price;
    }

    public String getPinCode_saved_price() {
        return pinCode_saved_price;
    }

    //// item list
    private List<ItemListModel> itemListModelList;

    public DeliveryPageModel(int type, List<ItemListModel> itemListModelList) {
        this.type = type;
        this.itemListModelList = itemListModelList;
    }

    public List<ItemListModel> getItemListModelList() {
        return itemListModelList;
    }
    //// item list
}
