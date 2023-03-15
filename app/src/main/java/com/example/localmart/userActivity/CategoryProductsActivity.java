package com.example.localmart.userActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.localmart.Adapters.productAdapter;
import com.example.localmart.R;
import com.example.localmart.modelClass.Products;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoryProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private DatabaseReference catProductRef;
    private productAdapter productAdapter;
    private String shopName , category;
    private ArrayList<Products> selectedCatProducts;
    private LinearLayout empty_CatProduct_lay , cat_no_results_found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_products);

        shopName = getIntent().getStringExtra("shopname");
        category = getIntent().getStringExtra("category");

        empty_CatProduct_lay = findViewById(R.id.empty_CatProduct_lay);
        cat_no_results_found = findViewById(R.id.cat_no_results_found);
        recyclerView = findViewById(R.id.category_productRecyclerView);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);


    }

    @Override
    protected void onStart() {
        super.onStart();
        loadProducts();
    }

    private void loadProducts() {
        catProductRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(shopName).child("products");
        catProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                selectedCatProducts = new ArrayList<Products>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    if(dataSnapshot1.child("category").getValue().equals(category)){
                        Products p = dataSnapshot1.getValue(Products.class);
                        empty_CatProduct_lay.setVisibility(View.INVISIBLE);
                        selectedCatProducts.add(p);
                    }
                }
                productAdapter = new productAdapter(CategoryProductsActivity.this,selectedCatProducts);
                recyclerView.setAdapter(productAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_bar , menu);
        MenuItem item = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String userinput = newText.toLowerCase();

                ArrayList<Products> productFiltered = new ArrayList<>();

                for (Products filter : selectedCatProducts) {
                    if (filter.getName().toLowerCase().contains(userinput)) {
                        if(cat_no_results_found.getVisibility() == View.VISIBLE){
                            cat_no_results_found.setVisibility(View.INVISIBLE);
                        }
                        productFiltered.add(filter);
                    }else {
                        if (productFiltered.isEmpty()) {
                            cat_no_results_found.setVisibility(View.VISIBLE);
                        }
                    }
                }
                productAdapter.updateNewList(productFiltered);
                return true;
            }
        });

        return  true;
    }
}
