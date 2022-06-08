package com.techov8.retaildost;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private TextView textView;
    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private String mainId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.search_view);
        textView = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.recycler_view);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#1BA9C3"));
        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        firebaseFirestore=FirebaseFirestore.getInstance();

        try {
            final List<ItemListModel> list = new ArrayList<>();
            final List<String> ids = new ArrayList<>();

            final Adapter adapter = new Adapter(list);
            adapter.setFromSearch(true);
            recyclerView.setAdapter(adapter);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(final String s) {
                    list.clear();
                    ids.clear();

                    final String[] tags = s.toLowerCase().split(" ");
                    for (final String tag : tags) {
                        tag.trim();

                        firebaseFirestore.collection("PRODUCTS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot documentSnapshot1 : task.getResult()) {
                                    mainId = documentSnapshot1.getString("id");

                                    firebaseFirestore.collection("PRODUCTS").document(mainId).collection("ITEM_LIST").whereArrayContains("tags", tag).get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {

                                                        for (final DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                                                            ItemListModel model = new ItemListModel(
                                                                    documentSnapshot.getString("item_image_1"),
                                                                    documentSnapshot.getString("name"),
                                                                    documentSnapshot.getString("price"),
                                                                    documentSnapshot.getString("cuted_price"),
                                                                    documentSnapshot.getString("saved_price"),
                                                                    documentSnapshot.getString("quantity_name"),
                                                                    documentSnapshot.getLong("min_items"),
                                                                    documentSnapshot.getString("id"),
                                                                    "Basic",
                                                                    (boolean) documentSnapshot.get("is_available"),
                                                                    documentSnapshot.getLong("no_of_items"),
                                                                    mainId
                                                            );

                                                            model.setTags((ArrayList<String>) documentSnapshot.get("tags"));

                                                            if (!ids.contains(model.getId())) {
                                                                list.add(model);
                                                                ids.add(model.getId());

                                                            }
                                                            if (documentSnapshot.getBoolean("is_multiple_products")) {
                                                                firebaseFirestore.collection("PRODUCTS").document(mainId).collection("ITEM_LIST").document(documentSnapshot.getString("id")).collection("ITEM_QUANTITIES").get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                for (final QueryDocumentSnapshot documentSnapshot3 : task.getResult()) {
                                                                                    if (!documentSnapshot3.getString("sub_id").equals("Basic")) {
                                                                                        ItemListModel model2 = new ItemListModel(
                                                                                                documentSnapshot3.getString("image"),
                                                                                                documentSnapshot3.getString("name"),
                                                                                                documentSnapshot3.getString("price"),
                                                                                                documentSnapshot3.getString("cuted_price"),
                                                                                                documentSnapshot3.getString("saved_price"),
                                                                                                documentSnapshot3.getString("quantity_name"),
                                                                                                documentSnapshot.getLong("min_item"),
                                                                                                documentSnapshot.getString("id"),
                                                                                                documentSnapshot3.getString("sub_id"),
                                                                                                true,
                                                                                                documentSnapshot.getLong("no_of_items"),
                                                                                                mainId
                                                                                        );


                                                                                        list.add(model2);
                                                                                        ids.add(model2.getId());

                                                                                    }
                                                                                }
                                                                            }
                                                                        });


                                                            }

                                                        }
                                                        if (tag.equals(tags[tags.length - 1])) {
                                                            if (list.size() == 0) {
                                                                textView.setVisibility(View.VISIBLE);
                                                                recyclerView.setVisibility(View.GONE);
                                                            } else {
                                                                textView.setVisibility(View.GONE);
                                                                recyclerView.setVisibility(View.VISIBLE);
                                                                adapter.getFilter().filter(s);
                                                            }
                                                        }

                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(SearchActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });


                                }
                            }
                        });

                    }

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }

    }


    class Adapter extends ItemListAdapter implements Filterable {
        private List<ItemListModel> originalList;

        public Adapter(List<ItemListModel> wishListModelList) {
            super(wishListModelList,mainId);
            originalList = wishListModelList;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults results = new FilterResults();
                    List<ItemListModel> filteredList = new ArrayList<>();

                    final String[] tags = charSequence.toString().toLowerCase().split(" ");
                    for(ItemListModel model : originalList){
                        ArrayList<String> presentTags = new ArrayList<>();
                        for(String tag : tags){
                            if(model.getTags().contains(tag)){
                                presentTags.add(tag);

                            }
                        }
                        model.setTags(presentTags);
                    }
                    for(int i=tags.length ; i >0 ; i--){
                        for(ItemListModel model : originalList){
                            if(model.getTags().size() == i){
                                filteredList.add(model);
                            }
                        }
                    }
                    results.values = filteredList;
                    results.count = filteredList.size();

                    return results;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                    if(filterResults.count > 0){
                        setItemListModelList((List<ItemListModel>) filterResults.values,mainId);
                    }
                    notifyDataSetChanged();
                }
            };
        }
    }
}