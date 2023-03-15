package com.example.localmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.localmart.Adapters.categoryAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class AddCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private String userORadmin , shopName;

    ArrayList<String> categorylist = new ArrayList<>(Arrays.asList(
            "Rice & Grains",
            "Delicious foods",
            "Cakes & Deserts",
            "Packed foods",
            "spices",
            "meats & fishs",
            "Fruits",
            "Vegetables",
            "Sauces & jams",
            "Beverages",
            "Snacks",
            "Canned & Frozen foods"
    ));


    ArrayList catIconlist = new ArrayList(Arrays.asList(
            R.drawable.rice,
            R.drawable.foods,
            R.drawable.cake,
            R.drawable.packed,
            R.drawable.spices,
            R.drawable.meat,
            R.drawable.fruits,
            R.drawable.vegitables,
            R.drawable.sauce,
            R.drawable.beverages,
            R.drawable.snack,
            R.drawable.canned
    ));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        userORadmin = getIntent().getStringExtra("userORadmin");
        shopName = getIntent().getStringExtra("shopName");
        recyclerView = findViewById(R.id.add_cate_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        categoryAdapter categoryAdapter = new categoryAdapter(AddCategoryActivity.this
                ,categorylist,catIconlist,userORadmin , shopName);
        recyclerView.setAdapter(categoryAdapter);

    }
}
