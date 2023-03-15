package com.example.localmart.userActivity;

import android.app.ProgressDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.localmart.Prevalent.productType;
import com.example.localmart.Prevalent.userPrevalent;
import com.example.localmart.R;
import com.example.localmart.modelClass.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class OfferActivity extends AppCompatActivity {

    private TextView name, price, save, sellingprice, available;
    private String name1, sellp1, save1, pic1, price1, available1;
    private String shopName;
    private ImageView offerImage;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private LinearLayout dummyLayout;
    private ElegantNumberButton elegantNumberButton;
    private Button cartBtn;
    private Boolean isElegantbtnClicked = false;
    private int getnoofitem , mrp , sellprice , discount , noofpdt_avail;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        shopName = getIntent().getStringExtra("shopname");
        progressDialog = new ProgressDialog(this);
        name = (TextView) findViewById(R.id.offer_name);
        price = (TextView) findViewById(R.id.offer_price);
        save = (TextView) findViewById(R.id.offer_save);
        available = (TextView)findViewById(R.id.offer_available);
        sellingprice = (TextView) findViewById(R.id.offer_sellp);
        offerImage = findViewById(R.id.offer_image_user);
        recyclerView = findViewById(R.id.offer_recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(layoutManager);
        dummyLayout = findViewById(R.id.dummy_layout);
        elegantNumberButton = findViewById(R.id.offer_elegantbtn);
        cartBtn = findViewById(R.id.offer_cartbtn);
        dummyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("adding to cart...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                String Oname = name.getText().toString();
                String Oprice  = sellingprice.getText().toString();
                String Onoofitem_selected = elegantNumberButton.getNumber();
                addproducttocartlist(Oname, Oprice,Onoofitem_selected,pic1,"-");
            }
        });

        getProductDetails();
    }

    private void addproducttocartlist(final String oname, String oprice,
                                      String onoofitem_selected, String pic1, final  String s) {
        final DatabaseReference cartlistref =FirebaseDatabase.getInstance().getReference()
                .child("cart_list");
        final HashMap<String,Object> cartproductdetials=new HashMap<>();
        cartproductdetials.put("pid",oname);
        cartproductdetials.put("pname",oname);
        cartproductdetials.put("selling_price",oprice);
        cartproductdetials.put("no_of_product",onoofitem_selected);
        cartproductdetials.put("image",pic1);
        if(isElegantbtnClicked){
            cartproductdetials.put("sellP" , sellprice);
        }else {
            cartproductdetials.put("sellP" , Integer.parseInt(sellp1));
        }
        cartlistref.child("User view").child(userPrevalent.current_user.getPhone())
                .child("cart_products").child(shopName).child(oname)
                .updateChildren(cartproductdetials)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            cartlistref.child("Admins view").child(userPrevalent.current_user.getPhone())
                                    .child("cart_products").child(shopName).child(oname)
                                    .updateChildren(cartproductdetials)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                progressDialog.dismiss();
                                                Toast.makeText(OfferActivity.this,"added",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }

                    }
                });

    }

    private void getProductDetails() {

        DatabaseReference offerRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(shopName).child("Todayoffer");

        offerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name1 = dataSnapshot.child("cname").getValue().toString();
                price1 = dataSnapshot.child("cprice").getValue().toString();
                sellp1 = dataSnapshot.child("csellp").getValue().toString();
                save1 = dataSnapshot.child("cdiscount").getValue().toString();
                available1 =dataSnapshot.child("cavailable").getValue().toString();
                pic1 = dataSnapshot.child("image").getValue().toString();

                name.setText(name1);
                price.setText("Mrp : "+ productType.notation + price1);
                price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                sellingprice.setText("Price : "+productType.notation + sellp1);
                save.setText("Save : "+productType.notation + save1);
                available.setText("Available : "+String.valueOf(Integer.parseInt(available1) - 1));
                Picasso.get().load(pic1).into(offerImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        elegantNumberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                getnoofitem = Integer.parseInt(elegantNumberButton.getNumber());
                isElegantbtnClicked = true;
                mrp=Integer.parseInt(price1)*getnoofitem;
                sellprice= Integer.parseInt(sellp1)*getnoofitem;
                discount=Integer.parseInt(save1)*getnoofitem;
                if(Integer.parseInt(available1)-getnoofitem-1<0){
                    dummyLayout.setVisibility(View.VISIBLE);
                }else {
                    dummyLayout.setVisibility(View.INVISIBLE);
                }
                noofpdt_avail = Integer.parseInt(available1)-getnoofitem;
                price.setText("Mrp : "+productType.notation+" "+mrp);
                sellingprice.setText("Price : "+productType.notation+" "+sellprice);
                save.setText("Save : "+productType.notation+" "+discount);
                available.setText("Available : "+noofpdt_avail);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference offProductRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(shopName).child("Todayoffer");
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(offProductRef.child("product"), Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, offerViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, offerViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull offerViewHolder offerViewHolder, int i, @NonNull Products products) {

                        offerViewHolder.name$quantity
                                .setText(products.getName() + " : " + products.getQuantity() + products.getKgmglml());
                        Picasso.get().load(products.getProducttype()).into(offerViewHolder.type);
                    }

                    @NonNull
                    @Override
                    public offerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.offer_detail_layout, parent, false);
                        offerViewHolder holder = new offerViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public class offerViewHolder extends RecyclerView.ViewHolder {

        public TextView name$quantity;
        public ImageView type;

        public offerViewHolder(@NonNull View itemView) {
            super(itemView);
            name$quantity = (TextView) itemView.findViewById(R.id.offerdetails_proname);
            type = (ImageView) itemView.findViewById(R.id.offerdetails_protype);
        }
    }
}
