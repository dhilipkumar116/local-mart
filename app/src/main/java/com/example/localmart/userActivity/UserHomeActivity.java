package com.example.localmart.userActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localmart.Adapters.productAdapter;
import com.example.localmart.AddCategoryActivity;
import com.example.localmart.HelpActivity;
import com.example.localmart.Prevalent.userPrevalent;
import com.example.localmart.R;
import com.example.localmart.TermsandCondActivity;
import com.example.localmart.modelClass.Products;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.paperdb.Paper;

public class UserHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<Products> productList;
    private productAdapter productAdapter;
    private DatabaseReference productref;
    private CircularImageView profilePic;
    private TextView profileName,shopName;
    private LinearLayout product_no_results_found,noProductLayout;
    private ImageView offerImage;
    private String selectedshopname ;
    private CardView offerview;
    private ProgressDialog progressDialog;
    private boolean isOfferAvail=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        selectedshopname = getIntent().getStringExtra("shopname");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("loading...");
        progressDialog.show();

        Toolbar toolbar = findViewById(R.id.user_nav_tool_bar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.user_drawer_layout);
        NavigationView navigationView = findViewById(R.id.user_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.navigation_open_drawer, R.string.navigation_close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        FloatingActionButton floatingActionButton = findViewById(R.id.fab_btn);

        offerview = findViewById(R.id.offerImageCardView);
        View Header = navigationView.getHeaderView(0);
        profilePic = Header.findViewById(R.id.user_header_profilepic);
        profileName = Header.findViewById(R.id.user_header_username);
        shopName = Header.findViewById(R.id.user_header_shopname);

        noProductLayout = findViewById(R.id.noproductLL);
        offerImage = findViewById(R.id.offerImageProduct);
        product_no_results_found = findViewById(R.id.product_no_results_found);
        recyclerView = findViewById(R.id.user_product_recyclerView);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(UserHomeActivity.this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserHomeActivity.this , CartActivity.class);
                intent.putExtra("shopname" , selectedshopname);
                startActivity(intent);
            }
        });

        offerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserHomeActivity.this , OfferActivity.class);
                intent.putExtra("shopname" , selectedshopname);
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        loadProducts();
        loadOffer();
//      Picasso.get().load(userPrevalent.current_user.getImage()).into(profilePic);
        profileName.setText("Hello "+userPrevalent.current_user.getName()+"!");
        shopName.setText("shopName : "+selectedshopname);

    }

    private void loadOffer() {

        DatabaseReference offerref = FirebaseDatabase
                .getInstance().getReference().child("Admins")
                .child(selectedshopname).child("Todayoffer");

        offerref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Picasso.get().load(dataSnapshot.child("image").getValue().toString()).into(offerImage);
                    offerview.setVisibility(View.VISIBLE);
                    isOfferAvail=true;
                    noProductLayout.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }else {
                    offerview.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void loadProducts() {
        productref=FirebaseDatabase.getInstance().getReference().child("Admins")
                .child(selectedshopname);
        productref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("products").exists()){
                    //no products
                    noProductLayout.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
                productList = new ArrayList<Products>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.child("products").getChildren()) {
                    Products p = dataSnapshot1.getValue(Products.class);
                    productList.add(p);
                }
                progressDialog.dismiss();
                productAdapter = new productAdapter(UserHomeActivity.this,productList);
                recyclerView.setAdapter(productAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserHomeActivity.this, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_bar, menu);
        MenuItem item = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String userinput = s.toLowerCase();
                ArrayList<Products> productFiltered = new ArrayList<>();
                for (Products filter : productList) {
                    if (filter.getName().toLowerCase().contains(userinput)) {
                        if(product_no_results_found.getVisibility() == View.VISIBLE){
                            product_no_results_found.setVisibility(View.GONE);
                            if(isOfferAvail){
                                offerview.setVisibility(View.VISIBLE);
                            }
                        }
                        productFiltered.add(filter);
                    }else {
                           if(productFiltered.isEmpty()) {
                               product_no_results_found.setVisibility(View.VISIBLE);
                               offerview.setVisibility(View.GONE);
                           }
                    }
                }
                productAdapter.updateNewList(productFiltered);
                return true;
            }
        });
        return true;

    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_cart: {
                Intent intent = new Intent(UserHomeActivity.this , CartActivity.class);
                intent.putExtra("shopname" , selectedshopname);
                startActivity(intent);
                break;
            }
            case R.id.nav_order: {
                Intent intent=new Intent(UserHomeActivity.this,orderListActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_wishlist: {
                Intent intent = new Intent(UserHomeActivity.this , WishListActivity.class);
                intent.putExtra("shopname" , selectedshopname);
                startActivity(intent);
                break;
            }
            case R.id.nav_category: {
                Intent intent = new Intent(UserHomeActivity.this , AddCategoryActivity.class);
                intent.putExtra("shopName" , selectedshopname);
                intent.putExtra("userORadmin" , "user");
                startActivity(intent);
                break;
            }
            case R.id.nav_setting: {
                startActivity(new Intent(UserHomeActivity.this, SettingActivity.class));
                break;

            }
            case R.id.nav_termsandcond: {
                Intent intent = new Intent(UserHomeActivity.this , TermsandCondActivity.class);
                startActivity(intent);
                break;

            }
            case R.id.nav_help: {
                Intent intent = new Intent(UserHomeActivity.this , HelpActivity.class);
                startActivity(intent);
                break;

            }
            case R.id.nav_logout: {

                Paper.book("user").destroy();
                Intent intent=new Intent(UserHomeActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            }

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
