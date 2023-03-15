package com.example.localmart.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localmart.R;
import com.example.localmart.userActivity.CategoryProductsActivity;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.myviewHolder> {

    private Context context;
    private String user_or_admin , shopname;
    private ArrayList categorylist ,catIconlist;

    public categoryAdapter(Context context,ArrayList categorylist,
                           ArrayList catIconlist , String useroradmin , String shopname ) {
        this.context = context;
        this.user_or_admin = useroradmin;
        this.shopname = shopname;
        this.categorylist = categorylist;
        this.catIconlist = catIconlist;
    }


    @NonNull
    @Override
    public myviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(user_or_admin.equals("userHorizonatal")){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.horizon_category,parent,false);

        }else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.add_category_layout,parent,false);

        }
        myviewHolder myviewHolder = new myviewHolder(view);
        return myviewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull myviewHolder holder, final int position) {

        holder.circularImageView.setImageResource(catIconlist.get(position).hashCode());
        holder.listname.setText(categorylist.get(position).toString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent indent = new Intent(context, CategoryProductsActivity.class);
                    indent.putExtra("category", categorylist.get(position).toString());
                    indent.putExtra("shopname", shopname);
                    context.startActivity(indent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categorylist.size();
    }


    public class myviewHolder extends RecyclerView.ViewHolder {

        private TextView listname;
        private CircularImageView circularImageView;

        public myviewHolder(@NonNull View itemView) {
            super(itemView);

            listname = itemView.findViewById(R.id.add_cate_txt);
            circularImageView = itemView.findViewById(R.id.add_cate_icon);
        }
    }
}