package com.example.localmart.userActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.localmart.Prevalent.productType;
import com.example.localmart.Prevalent.userPrevalent;
import com.example.localmart.R;
import com.example.localmart.modelClass.Carts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private String shopName;
    private Button proceedBtn;
    private LinearLayout cartEmptyLayout;
    private int overalltotalprice;
    private ProgressDialog progressDialog;
    private TextView isOrderIsPlaced;
    private String shippedstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        shopName = getIntent().getStringExtra("shopname");
        cartEmptyLayout = findViewById(R.id.cart_empty_layout);
        proceedBtn = findViewById(R.id.cart_proceed_btn);
        recyclerView = findViewById(R.id.cart_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        progressDialog = new ProgressDialog(this);
        isOrderIsPlaced = findViewById(R.id.text_cart_check);
        progressDialog.setMessage("loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        loadCartProducts();

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    proceedToNextStep();
            }
        });


    }

    private void proceedToNextStep() {
        final AlertDialog.Builder builder= new AlertDialog.Builder(CartActivity.this);
        builder.setMessage("Total price = "+ productType.notation+overalltotalprice);
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
                Intent intent=new Intent(CartActivity.this,PlaceOrderActivity.class);
                intent.putExtra("total_price", ((String.valueOf(overalltotalprice)) ));
                intent.putExtra("shop_name",shopName);
                startActivity(intent);

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Checkorderstate();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                .child("cart_list").child("User view")
                .child(userPrevalent.current_user.getPhone())
                .child("cart_products");;
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child(shopName).exists()){
                    progressDialog.dismiss();
                    cartEmptyLayout.setVisibility(View.VISIBLE);
                }
                else if(dataSnapshot.child(shopName).exists()&&shippedstatus.equals("orderplaced")){
                    progressDialog.dismiss();
                    cartEmptyLayout.setVisibility(View.VISIBLE);
                    isOrderIsPlaced.setText("congratulations your order was successfully placed !!.\n" +
                            " you can purchase more product, " +
                            "once your order confirmed by shop.");
                    recyclerView.setVisibility(View.GONE);

                }else {
                    progressDialog.dismiss();
                    Animation animation = AnimationUtils.loadAnimation(CartActivity.this , R.anim.bottom_top);
                    proceedBtn.setAnimation(animation);
                    proceedBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private  void Checkorderstate()
    {
        DatabaseReference orderref=FirebaseDatabase.getInstance().getReference().child("cart_list")
                .child("checkcartenable").child(userPrevalent.current_user.getPhone()).child(shopName);
        orderref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists()) {
                    shippedstatus=dataSnapshot.child("status").getValue().toString();
                }else {
                    shippedstatus="noorderplaced";
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadCartProducts() {
        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                .child("cart_list").child("User view")
                .child(userPrevalent.current_user.getPhone()).child("cart_products");

        FirebaseRecyclerOptions<Carts> options =
                new FirebaseRecyclerOptions.Builder<Carts>()
                .setQuery(cartRef.child(shopName) , Carts.class).build();


        FirebaseRecyclerAdapter<Carts,cartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Carts, cartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull cartViewHolder holder, int i, @NonNull final Carts carts) {
                      progressDialog.dismiss();
                        holder.title.setText(carts.getPname());
                        holder.price.setText(carts.getSelling_price());
                        Picasso.get().load(carts.getImage()).into(holder.image);
                        holder.noofpdt.setText("no of items : "+carts.getNo_of_product());
                        int onetypetotalprice = 0;
                        onetypetotalprice = carts.getSellP()+onetypetotalprice;
                        overalltotalprice=overalltotalprice+onetypetotalprice;
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                CharSequence options[]=new CharSequence[]
                                        {
                                                "Edit", "Remove"
                                        };

                                final AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("cart options");
                                builder.setCancelable(true);
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        if(which==0)
                                        {
                                            Intent intent=new Intent(CartActivity.this,
                                                    ProductDetailActivity.class);
                                            intent.putExtra("pid",carts.getPid());
                                            intent.putExtra("shop_name",shopName);
                                            startActivity(intent);
                                        }
                                        if(which==1)
                                        {

                                            DatabaseReference usercart = FirebaseDatabase.getInstance()
                                                    .getReference().child("cart_list").child("User view")
                                                    .child(userPrevalent.current_user.getPhone())
                                                    .child("cart_products");
                                            DatabaseReference shopcart = FirebaseDatabase.getInstance()
                                                    .getReference().child("cart_list").child("Admins view")
                                                    .child(userPrevalent.current_user.getPhone())
                                                    .child("cart_products");
                                            usercart.child(shopName)
                                                    .child(carts.getPid())
                                                    .removeValue();
                                            shopcart.child(shopName)
                                                    .child(carts.getPid())
                                                    .removeValue();

                                        }
                                    }
                                });

                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public cartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.wishlist_layout , parent ,false);
                        return new cartViewHolder(view);
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    public class cartViewHolder extends RecyclerView.ViewHolder{

        private ImageView image;
        private TextView title , noofpdt , price;
        public cartViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.Cart_product_NameQuantity);
            price = itemView.findViewById(R.id.Cart_product_Price);
            noofpdt =  itemView.findViewById(R.id.Cart_product_Noofproduct);
            image = itemView.findViewById(R.id.wishlist_image_lay);

        }
    }
}
