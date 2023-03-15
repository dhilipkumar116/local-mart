package com.example.localmart.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localmart.R;
import com.example.localmart.modelClass.Shops;
import com.example.localmart.userActivity.showMoreShopActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;

public class shopCatAdapter extends RecyclerView.Adapter<shopCatAdapter.myviewHolder> {


    ArrayList<String> shopCategory;
    ProgressDialog progressDialog;
    Context context;
    Location userLocation;
    RecyclerView catRecyclerView;
    ArrayList<Shops> shopList;


    public  shopCatAdapter(Context context, ArrayList<String> shopCategory,
                           ProgressDialog progressDialog,Location location,RecyclerView catRecyclerView){
        this.context = context;
         this.shopCategory = shopCategory;
         this.progressDialog = progressDialog;
         this.userLocation = location;
         this.catRecyclerView = catRecyclerView;
    }


    @NonNull
    @Override
    public myviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.parent_shop_list,parent,false);
        return new myviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final myviewHolder holder, final int position) {
        holder.title.setText("nearby "+shopCategory.get(position));
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL,false));

        final DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins");
        shopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                shopList = new ArrayList<>();
                for(DataSnapshot shop : dataSnapshot.getChildren()){
                    if(shop.child("approval").getValue().equals("approved")){
                        Shops shops = shop.getValue(Shops.class);
                        LatLng toLatLang = new LatLng(shops.getLatitude(),shops.getLongtitude());
                        LatLng fromLatLang = new LatLng(userLocation.getLatitude(),userLocation.getLongitude());
                        Double distance = SphericalUtil.computeDistanceBetween(fromLatLang,toLatLang);
                        distance = distance/1000;
                        if(shops.getCategory().equals(shopCategory.get(position))&&
                        distance<=20){
                            shopList.add(shops);
                        }

                    }

                }

                if(shopList.isEmpty()){
                    holder.notavailable.setVisibility(View.VISIBLE);
                    catRecyclerView.setVisibility(View.VISIBLE);
                    holder.ripple_effect.setVisibility(View.VISIBLE);
                    holder.ripple_effect.startRippleAnimation();
                    progressDialog.dismiss();
                }
                shopListAdapter shopListAdapter = new shopListAdapter(context,shopList
                        ,progressDialog,catRecyclerView);
                holder.recyclerView.setAdapter(shopListAdapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , showMoreShopActivity.class);
                intent.putExtra("category" , shopCategory.get(position));
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return shopCategory.size();
    }

    public class  myviewHolder extends  RecyclerView.ViewHolder{

        public TextView title,more,notavailable;
        public RecyclerView recyclerView;
        private RippleBackground ripple_effect;
        public myviewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView=(RecyclerView) itemView.findViewById(R.id.shopList_childRecyclerView);
            title=(TextView)itemView.findViewById(R.id.cat_title_shop);
            more=(TextView)itemView.findViewById(R.id.cat_title_more);
            notavailable = itemView.findViewById(R.id.notavailable);
            ripple_effect = itemView.findViewById(R.id.ripple_effect);
        }
    }
}
