package com.example.localmart.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localmart.Prevalent.productType;
import com.example.localmart.Prevalent.userPrevalent;
import com.example.localmart.userActivity.ProductDetailActivity;
import com.example.localmart.R;
import com.example.localmart.modelClass.Products;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class productAdapter extends RecyclerView.Adapter<productAdapter.myViewHolder> {


    private ArrayList<Products> productList;
    private Context context;

    public productAdapter(Context context, ArrayList<Products> productList) {
        this.context = context;
        this.productList = productList;
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_layout,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, final  int position) {
        holder.txtproductname.setText(productList.get(position).getName() + " : "
                + productList.get(position).getQuantity() + productList.get(position).getKgmglml());
        holder.txtnofoproducts.setText("available : " + productList.get(position).getNoofitem());
        holder.txtsellingprice.setText(productType.notation+ productList.get(position).getSelling() );
        Picasso.get().load(productList.get(position).getImage()).into(holder.imageView);
        Picasso.get().load(productList.get(position).getProducttype()).into(holder.pdttype);
        holder.txtsold.setText(productList.get(position).getSold()+" sold");
        String shopname = productList.get(position).getShopname();
         final DatabaseReference wishlistref=FirebaseDatabase.getInstance().getReference()
                .child("wishlist").child(userPrevalent.current_user.getPhone()).child(shopname);
         wishlistref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String UIDW = productList.get(position).getPid();
                if(dataSnapshot.child(UIDW).exists()) {
                    holder.unwishlist.setVisibility(View.VISIBLE);
                } else {
                    holder.wishlist.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.unwishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.wishlist.setVisibility(View.VISIBLE);
                holder.unwishlist.setVisibility(View.GONE);
                String shopname = productList.get(position).getShopname();
                final String UID = productList.get(position).getPid();

                removeFromWishList(shopname,UID);
            }
        });
        holder.wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.wishlist.setVisibility(View.GONE);
                holder.unwishlist.setVisibility(View.VISIBLE);
                final String UID = productList.get(position).getPid();

                String name = productList.get(position).getName() + " : "
                        + productList.get(position).getQuantity() + productList.get(position).getKgmglml();
                String sellp = "price : "+productType.notation + productList.get(position).getSelling();
                String image = productList.get(position).getImage();
                String shopname = productList.get(position).getShopname();

                addToWishList(UID,name,sellp,image,shopname);
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shopname = productList.get(position).getShopname();
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("shop_name", shopname);
                intent.putExtra("pid", productList.get(position).getPid());
//                intent.putExtra("image", Products.get(position).getImage());
                context.startActivity(intent);
            }
        });
    }


    private void removeFromWishList(String shopname, String UID) {


        final DatabaseReference wishlist = FirebaseDatabase.getInstance().getReference()
                .child("wishlist").child(userPrevalent.current_user.getPhone()).child(shopname);


        wishlist.child(UID).removeValue();
    }

    private void addToWishList(final String UID, final String name, final String sellp,
                               final String image, final String shopname) {

        final DatabaseReference wishlist = FirebaseDatabase.getInstance().getReference()
                .child("wishlist");

        wishlist.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                if(dataSnapshot.child(userPrevalent.current_user.getPhone())
                        .child(shopname).child(UID).exists()) {
                    Toast.makeText(context,"already added",Toast.LENGTH_SHORT).show();
                }
                else {

                    final HashMap<String,Object> productwishlistH=new HashMap<>();
                    productwishlistH.put("pid", UID);
                    productwishlistH.put("pname",name);
                    productwishlistH.put("selling_price",sellp);
                    productwishlistH.put("image",image);
                    wishlist.child(userPrevalent.current_user.getPhone()).child(shopname)
                            .child(UID).updateChildren(productwishlistH);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    public void updateNewList(ArrayList<Products> newList){
        productList = new ArrayList<>();
        productList.addAll(newList);
        notifyDataSetChanged();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView txtproductname,txtnofoproducts,txtsellingprice , txtsold;
        ImageView imageView,unwishlist,wishlist,pdttype;
        Button btn;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.product_Image);
            txtproductname = (TextView) itemView.findViewById(R.id.product_Name);
            txtnofoproducts = (TextView) itemView.findViewById(R.id.product_Noofproduct);
            txtsellingprice=(TextView)itemView.findViewById(R.id.selling_price);
            unwishlist=(ImageView)itemView.findViewById(R.id.product_unwishlistbtn);
            wishlist=(ImageView)itemView.findViewById(R.id.product_wishlistbtn);
            pdttype=(ImageView)itemView.findViewById(R.id.product_type);
            txtsold = itemView.findViewById(R.id.product_Sold);

        }
    }
}
