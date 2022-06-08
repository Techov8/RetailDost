package com.techov8.retaildost;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DeliveryPageAdapter extends RecyclerView.Adapter{

    private int lastPosition = -1;
    private List<DeliveryPageModel> deliveryPageModelList;

    public DeliveryPageAdapter(List<DeliveryPageModel> deliveryPageModelList) {
        this.deliveryPageModelList = deliveryPageModelList;
    }

    @Override
    public int getItemViewType(int position) {
        switch (deliveryPageModelList.get(position).getType()) {
            case 0:
                return DeliveryPageModel.ADDRESS_PAGE;
            case 1:
                return DeliveryPageModel.ITEM_RECYCLER_PAGE;
            case 2:
                return DeliveryPageModel.ITEM_TOTAL_PAGE;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case DeliveryPageModel.ADDRESS_PAGE:
                View addressView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shipping_details_layout, parent, false);
                return new addressViewHolder(addressView);
            case DeliveryPageModel.ITEM_RECYCLER_PAGE:
                View itemListView = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_item_layout, parent, false);
                return new itemListViewHolder(itemListView);
            case DeliveryPageModel.ITEM_TOTAL_PAGE:
                View itemTotalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_total_amount_layout, parent, false);
                return new itemTotalViewHolder(itemTotalView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (deliveryPageModelList.get(position).getType()) {
            case DeliveryPageModel.ADDRESS_PAGE:
                String fullName = deliveryPageModelList.get(position).getFullName_totalItem();
                String address = deliveryPageModelList.get(position).getAddress_total_price();
                String pin=deliveryPageModelList.get(position).getPinCode_saved_price();
                ((addressViewHolder) holder).setAddress(fullName,address, pin);
                break;
            case DeliveryPageModel.ITEM_RECYCLER_PAGE:
                List<ItemListModel> itemListModelList = deliveryPageModelList.get(position).getItemListModelList();
                ((itemListViewHolder) holder).setItemList(itemListModelList);
                break;


            case DeliveryPageModel.ITEM_TOTAL_PAGE:
                String totalItem = deliveryPageModelList.get(position).getFullName_totalItem();
                String totalPrice = deliveryPageModelList.get(position).getAddress_total_price();
                String savedPrice = deliveryPageModelList.get(position).getPinCode_saved_price();

                ((itemTotalViewHolder) holder).setTotalItem(totalItem,totalPrice,savedPrice);
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
        return deliveryPageModelList.size();
    }


    public static class addressViewHolder extends RecyclerView.ViewHolder {
        private TextView fullName,fullAddress,pinCode;
        private Button addOrChangeAddressBtn;

        public addressViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName=itemView.findViewById(R.id.full_name);
            fullAddress=itemView.findViewById(R.id.address);
            pinCode=itemView.findViewById(R.id.pin_code);
            addOrChangeAddressBtn=itemView.findViewById(R.id.change_or_address_btn);

        }

        private void setAddress(String name, String address,String pin) {
            try {
                fullName.setText(name);
                fullAddress.setText(address);
                pinCode.setText(pin);

                addOrChangeAddressBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent addressIntent = new Intent(itemView.getContext(), AddressActivity.class);
                        addressIntent.putExtra("INTENT", "edit_address_account");
                        addressIntent.putExtra("INTENT_TYPE", "edit_address");
                        itemView.getContext().startActivity(addressIntent);
                    }
                });
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }


    public static class itemListViewHolder extends RecyclerView.ViewHolder {
        private  RecyclerView deliveryItemRecyclerView;

        public itemListViewHolder(@NonNull View itemView) {
            super(itemView);
            deliveryItemRecyclerView=itemView.findViewById(R.id.delivery_item_reycler_view);
        }

        private void setItemList(List<ItemListModel> deliveryItemListModelList) {
            try {

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                deliveryItemRecyclerView.setLayoutManager(linearLayoutManager);

                CartAdapter cartAdapter = new CartAdapter(deliveryItemListModelList, true);
                deliveryItemRecyclerView.setAdapter(cartAdapter);
                cartAdapter.notifyDataSetChanged();
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }


    public static class itemTotalViewHolder extends RecyclerView.ViewHolder {
        private TextView totalItems,totalItemPrice,deliveryPrice,totalAmount,savedAmount;

        public itemTotalViewHolder(@NonNull View itemView) {
            super(itemView);
            totalItems=itemView.findViewById(R.id.total_items);
            totalItemPrice=itemView.findViewById(R.id.total_items_price);
            deliveryPrice=itemView.findViewById(R.id.delivery_price);
            totalAmount=itemView.findViewById(R.id.total_price);
            savedAmount=itemView.findViewById(R.id.saved_amount);

        }

        private void setTotalItem(String item, String price,String saved) {

            totalItems.setText("Price ( "+item+" ) Items");
            totalItemPrice.setText("₹ "+price);
            deliveryPrice.setText("-");
            totalAmount.setText("₹ "+price);
            savedAmount.setText("You Saved Rs."+saved+"/- on this order");

        }
    }
}
