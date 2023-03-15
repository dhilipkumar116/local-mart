package com.example.localmart.userActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.localmart.Adapters.shopListAdapter;
import com.example.localmart.R;
import com.example.localmart.modelClass.Shops;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class showMoreShopActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private shopListAdapter shopListAdapter;
    private ArrayList<Shops> AllshopList;
    private  String category;
    private LinearLayout notfoundLayout,noshopLayout;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_more_shop);


        category = getIntent().getStringExtra("category");
        notfoundLayout = findViewById(R.id.shoplist_no_results_found);
        recyclerView = findViewById(R.id.showAllShopRecyclerView);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        noshopLayout = findViewById(R.id.noshopLL);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("loading...");
        progressDialog.show();

        final DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins");
        shopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                AllshopList = new ArrayList<>();
                for(DataSnapshot shop : dataSnapshot.getChildren()){
                    if(shop.child("approval").getValue().equals("approved")){
                        Shops shops = shop.getValue(Shops.class);
                        if(shops.getCategory().equals(category)){
                            AllshopList.add(shops);
                        }

                    }
                }
                if(AllshopList.isEmpty()){
                    noshopLayout.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
                shopListAdapter =
                        new shopListAdapter(showMoreShopActivity.this,AllshopList,progressDialog,recyclerView);
                recyclerView.setAdapter(shopListAdapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_bar, menu);
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

                ArrayList<Shops> shopFiltered = new ArrayList<>();

                for (Shops filter : AllshopList) {

                    if (filter.getShop_name().toLowerCase().contains(userinput)) {
                        if(notfoundLayout.getVisibility() == View.VISIBLE){
                            notfoundLayout.setVisibility(View.INVISIBLE);
                        }
                        shopFiltered.add(filter);
                    }
                    else {
                        if(shopFiltered.isEmpty()) {
                            notfoundLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
                shopListAdapter.updateshoplist(shopFiltered);
                return true;
            }
        });
        return true;
    }
}
