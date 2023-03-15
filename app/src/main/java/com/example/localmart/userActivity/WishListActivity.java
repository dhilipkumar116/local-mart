package com.example.localmart.userActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class WishListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private String shopname;
    private LinearLayout empty_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        empty_layout = findViewById(R.id.empty_wishListLayout);
        recyclerView = findViewById(R.id.wishListRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        shopname=getIntent().getStringExtra("shopname");
        loadProducts();

    }

    private void loadProducts() {

        final DatabaseReference wishlistproductref= FirebaseDatabase.getInstance().getReference()
                .child("wishlist");
        wishlistproductref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child(userPrevalent.current_user.getPhone()).child(shopname).exists()){
                    empty_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseRecyclerOptions<Carts> options =
                new FirebaseRecyclerOptions.Builder<Carts>()
                        .setQuery(wishlistproductref.child(userPrevalent.current_user.getPhone())
                                .child(shopname),Carts.class).build();

        FirebaseRecyclerAdapter<Carts,wishListViewHolder> adapter =
                new FirebaseRecyclerAdapter<Carts, wishListViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull wishListViewHolder holder, int i, @NonNull final Carts carts) {

                        holder.titile.setText(carts.getPname());
                        holder.price.setText(carts.getSelling_price());
                        Picasso.get().load(carts.getImage()).into(holder.wishlist_image);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[]= new CharSequence[]
                                        {
                                                "add to cart","remove"
                                        };

                                final AlertDialog.Builder builder=new AlertDialog.Builder(WishListActivity.this);
                                builder.setCancelable(true);
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        if(which==0)
                                        {
                                            Intent intent=new Intent(WishListActivity.this,
                                                    ProductDetailActivity.class);
                                            intent.putExtra("shop_name",shopname);
                                            intent.putExtra("pid",carts.getPid());
                                            startActivity(intent);
                                        }
                                        if(which==1)
                                        {
                                            wishlistproductref.child(userPrevalent.current_user.getPhone())
                                                    .child(shopname)
                                                    .child(carts.getPid()).removeValue();
                                            notifyDataSetChanged();

                                        }

                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public wishListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.wishlist_layout , parent , false);

                        return new wishListViewHolder(view);
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    public class wishListViewHolder extends RecyclerView.ViewHolder{

        public TextView titile , price , noofpdt;
        public ImageView wishlist_image;

        public wishListViewHolder(@NonNull View itemView) {
            super(itemView);
            titile = itemView.findViewById(R.id.Cart_product_NameQuantity);
            price = itemView.findViewById(R.id.Cart_product_Price);
            noofpdt =  itemView.findViewById(R.id.Cart_product_Noofproduct);
            wishlist_image = itemView.findViewById(R.id.wishlist_image_lay);

        }
    }
}
