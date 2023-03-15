package com.example.localmart.userActivity;

import android.app.ProgressDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.localmart.Adapters.categoryAdapter;
import com.example.localmart.Prevalent.productType;
import com.example.localmart.Prevalent.userPrevalent;
import com.example.localmart.R;
import com.example.localmart.modelClass.Products;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView detialproductpic;
    private TextView detialproductname, detialproductnoofpdt, detialproductdes, detialproductmrp, noofferavail,
            detialproductsell_p, detialproductdis,detialpt;
    private String pnoofpdt, pmrp, pselling, pdiscount;
    private ElegantNumberButton detialpdtincreasingbtn;
    private Button cartbtn;
    private ImageView wishlist, unwishlist, detialproducttype;
    private String producttype;
    private ImageView offer_image;
    private String shopname, productId, imageId, image;
    private int getnoofitem = 1, noofpdt_avail, mrp, sellprice, discount;
    private LinearLayout disable_btn_layout;
    private boolean isElegantbtnClicked = false;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private LinearLayoutManager horizontalLayoutManager;
    private categoryAdapter categoryAdapter;
    private String shippedstatus = "";
    private TextView sold;

    ArrayList<String> categorylist = new ArrayList<>(Arrays.asList(
            "Delicious foods",
            "Cakes and Deserts",
            "Fruits",
            "Vegetables",
            "Sauces",
            "Jams",
            "Pasta and Noodels",
            "Packed foods",
            "Beverages",
            "Rice and Grains",
            "Snacks",
            "Canned and Frozen foods"
    ));


    ArrayList catIconlist = new ArrayList(Arrays.asList(
            R.drawable.foods,
            R.drawable.cake,
            R.drawable.fruits,
            R.drawable.vegitables,
            R.drawable.sauce,
            R.drawable.jam,
            R.drawable.pasta,
            R.drawable.packed,
            R.drawable.beverages,
            R.drawable.rice,
            R.drawable.snack,
            R.drawable.canned
    ));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        shopname = getIntent().getStringExtra("shop_name");
        productId = getIntent().getStringExtra("pid");
        imageId = getIntent().getStringExtra("image");

        progressDialog = new ProgressDialog(this);
        disable_btn_layout = findViewById(R.id.disable_btn_layout);
        detialpdtincreasingbtn = (ElegantNumberButton) findViewById(R.id.product_detials_elegantbtn);
        detialproductname = (TextView) findViewById(R.id.product_detials_name);
        detialproductnoofpdt = (TextView) findViewById(R.id.product_detials_noofproduct);
        detialproductdes = (TextView) findViewById(R.id.product_detials_descreption);
        detialproductmrp = (TextView) findViewById(R.id.product_detials_mrp);
        detialproductsell_p = (TextView) findViewById(R.id.product_detials_sellingprice);
        detialproductdis = (TextView) findViewById(R.id.product_detials_discount);
        detialpt =(TextView)findViewById(R.id.product_detials_product_type);
        detialproductpic = (ImageView) findViewById(R.id.product_detials_image);
        cartbtn = (Button) findViewById(R.id.product_detials_cartbtn);
        wishlist = (ImageView) findViewById(R.id.product_detials_wishlistbtn);
        unwishlist = (ImageView) findViewById(R.id.product_detials_unwishlistbtn);
        detialproducttype = (ImageView) findViewById(R.id.product_detials_type);
        sold = findViewById(R.id.product_Sold);

        recyclerView = findViewById(R.id.horizon_recyclerView);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        categoryAdapter = new categoryAdapter(ProductDetailActivity.this
                , categorylist, catIconlist, "userHorizonatal", shopname);
        horizontalLayoutManager = new LinearLayoutManager(ProductDetailActivity.this
                , LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setAdapter(categoryAdapter);


        getproductdetials(productId, shopname);

        disable_btn_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wishlist.setVisibility(View.GONE);
                unwishlist.setVisibility(View.VISIBLE);
                addtowishlist(imageId, productId, producttype);
            }
        });
        unwishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wishlist.setVisibility(View.VISIBLE);
                unwishlist.setVisibility(View.GONE);
                deletewishlist(productId);
            }
        });
        cartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (shippedstatus.equals("orderplaced")) {
                    Toast.makeText(ProductDetailActivity.this, "you can purchase more products," +
                            "once your order is " +
                            "confirmed", Toast.LENGTH_LONG).show();
                } else if (Integer.parseInt(pnoofpdt) == 0) {
                    Toast.makeText(ProductDetailActivity.this, "this product not available", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("adding to cart...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    String name = detialproductname.getText().toString();
                    String price = detialproductsell_p.getText().toString();
                    String noofitem_selected = detialpdtincreasingbtn.getNumber().toString();
                    addproducttocartlist(name, price, noofitem_selected, imageId, productId);
                }

            }
        });

    }

    private void addproducttocartlist(String name, String price, String noofitem_selected,
                                      String imageId, final String productId) {

        final DatabaseReference cartlistref = FirebaseDatabase.getInstance().getReference()
                .child("cart_list");
        final HashMap<String, Object> cartproductdetials = new HashMap<>();
        cartproductdetials.put("pid", productId);
        cartproductdetials.put("pname", name);
        cartproductdetials.put("selling_price", price);
        cartproductdetials.put("no_of_product", noofitem_selected);
        cartproductdetials.put("image", image);
        if (isElegantbtnClicked) {
            cartproductdetials.put("sellP", sellprice);
        } else {
            cartproductdetials.put("sellP", Integer.parseInt(pselling));
        }
        cartlistref.child("User view").child(userPrevalent.current_user.getPhone())
                .child("cart_products").child(shopname).child(productId)
                .updateChildren(cartproductdetials)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(ProductDetailActivity.this, "added",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkWishListState(productId);
        Checkorderstate();
    }

    private void checkWishListState(final String productId) {
        final DatabaseReference wishlistref = FirebaseDatabase.getInstance().getReference()
                .child("wishlist").child(userPrevalent.current_user.getPhone()).child(shopname);

        wishlistref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(productId).exists()) {
                    unwishlist.setVisibility(View.VISIBLE);
                } else {
                    wishlist.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void Checkorderstate() {
        DatabaseReference orderref = FirebaseDatabase.getInstance().getReference().child("cart_list")
                .child("checkcartenable").child(userPrevalent.current_user.getPhone()).child(shopname);
        orderref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    shippedstatus = dataSnapshot.child("status").getValue().toString();
                } else {
                    shippedstatus = "norderplaced";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void deletewishlist(String productId) {
        final DatabaseReference wishlist = FirebaseDatabase.getInstance().getReference()
                .child("wishlist").child(userPrevalent.current_user.getPhone()).child(shopname);
        wishlist.child(productId).removeValue();
    }

    private void addtowishlist(final String imageId, final String productId, String producttype) {
        final DatabaseReference wishlist = FirebaseDatabase.getInstance().getReference()
                .child("wishlist");

        wishlist.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                final HashMap<String, Object> productwishlistH = new HashMap<>();
                productwishlistH.put("pid", productId);
                productwishlistH.put("pname", detialproductname.getText().toString());
                productwishlistH.put("selling_price", detialproductsell_p.getText().toString());
                productwishlistH.put("image", image);
                wishlist.child(userPrevalent.current_user.getPhone()).child(shopname)
                        .child(productId).updateChildren(productwishlistH);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getproductdetials(String productId, String shopname) {
        DatabaseReference detialref = FirebaseDatabase.getInstance().getReference().child("Admins")
                .child(shopname).child("products").child(productId);
        detialref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    final Products products = dataSnapshot.getValue(Products.class);


                    pmrp = products.getPrice();
                    pnoofpdt = products.getNoofitem();
                    pselling = products.getSelling();
                    pdiscount = products.getDiscount();
                    producttype = products.getProducttype();
                    image = products.getImage();

                    String pT = products.getProducttype();
                    if(pT.equals(productType.veg)){detialpt.setText("Type : Veg");}
                    else if(pT.equals(productType.nonveg)){detialpt.setText("Type : Non Veg");}
                    else{detialpt.setText("Type : Natural");}


                    detialproductname.setText(products.getName() + " : " + products.getQuantity() + products.getKgmglml());
                    int available = Integer.valueOf(products.getNoofitem());
                    if (Integer.valueOf(products.getNoofitem()) == 0) {
                        available = 0;
                    }
                    detialproductnoofpdt.setText("Available : " + available);
                    detialproductdes.setText(products.getDescription());
                    detialproductmrp.setText("Mrp : RM " + pmrp);
                    detialproductmrp.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    detialproductsell_p.setText("Price : RM " + pselling);
                    detialproductdis.setText("Save : RM " + pdiscount);
                    Picasso.get().load(products.getImage()).into(detialproductpic);
                    Picasso.get().load(products.getProducttype()).into(detialproducttype);
                    sold.setText(products.getSold() + " sold");

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        detialpdtincreasingbtn.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                    getnoofitem = Integer.parseInt(detialpdtincreasingbtn.getNumber());
                    isElegantbtnClicked = true;
                    mrp = Integer.parseInt(pmrp) * getnoofitem;
                    sellprice = Integer.parseInt(pselling) * getnoofitem;
                    discount = Integer.parseInt(pdiscount) * getnoofitem;
                    if (Integer.parseInt(pnoofpdt) - getnoofitem - 1 < 0) {
                        disable_btn_layout.setVisibility(View.VISIBLE);
                    } else {
                        disable_btn_layout.setVisibility(View.INVISIBLE);
                    }
                if (Integer.parseInt(pnoofpdt) > 0) {
                    noofpdt_avail = Integer.parseInt(pnoofpdt) - getnoofitem;
                    detialproductmrp.setText("Mrp : RM " + mrp);
                    detialproductsell_p.setText("Price : RM " + sellprice);
                    detialproductdis.setText("Save : RM " + discount);
                    detialproductnoofpdt.setText("Available : " + noofpdt_avail);

                }
            }
        });


    }
}
