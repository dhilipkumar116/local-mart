package com.example.localmart.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localmart.R;
import com.example.localmart.modelClass.Shops;
import com.example.localmart.userActivity.UserHomeActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class shopListAdapter extends RecyclerView.Adapter<shopListAdapter.myviewHolder> {


    private ArrayList<Shops> shopList;
    private Context context;
    private ProgressDialog progressDialog;
    private RecyclerView catRecyclerView;

    public shopListAdapter(Context context, ArrayList<Shops> shopListAll,
                           ProgressDialog progressDialog,RecyclerView catRecyclerView) {
        this.shopList = shopListAll;
        this.context = context;
        this.progressDialog = progressDialog;
        this.catRecyclerView = catRecyclerView;
    }

    @NonNull
    @Override
    public myviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shop_list_layout, parent, false);
        return new myviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewHolder holder, final int position) {
        progressDialog.dismiss();
        catRecyclerView.setVisibility(View.VISIBLE);
        holder.txtshopname.setText(shopList.get(position).getShop_name());
        holder.txtdistrict.setText(shopList.get(position).getDistrict());
        Picasso.get().load(shopList.get(position).getImage()).into(holder.shopimage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserHomeActivity.class);
                intent.putExtra("shopname", shopList.get(position).getShop_name());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    public void updateshoplist(ArrayList<Shops> shopFiltered) {
        shopList = new ArrayList<>();
        shopList.addAll(shopFiltered);
        notifyDataSetChanged();
    }

    public class myviewHolder extends RecyclerView.ViewHolder {

        public TextView txtshopname, txtdistrict;
        public ImageView shopimage;

        public myviewHolder(@NonNull View itemView) {
            super(itemView);

            txtshopname = itemView.findViewById(R.id.shopsearch_name);
            txtdistrict = itemView.findViewById(R.id.search_shop_district);
            shopimage = itemView.findViewById(R.id.shopsearch_pic);

        }
    }
}
