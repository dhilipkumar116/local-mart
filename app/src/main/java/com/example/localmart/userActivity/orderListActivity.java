package com.example.localmart.userActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.localmart.Prevalent.productType;
import com.example.localmart.Prevalent.userPrevalent;
import com.example.localmart.R;
import com.example.localmart.modelClass.Orders;
import com.example.localmart.orderProductsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class orderListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private DatabaseReference userordermanageref;
    private LinearLayout emptylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        recyclerView = findViewById(R.id.user_order_list_recyclerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        emptylist = findViewById(R.id.empty_orderListLayout);
    }


    @Override
    protected void onStart() {
        super.onStart();

        userordermanageref = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userPrevalent.current_user.getPhone());
        userordermanageref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("orders").exists()){
                    emptylist.setVisibility(View.VISIBLE);
                }else {
                    emptylist.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final FirebaseRecyclerOptions<Orders> options= new FirebaseRecyclerOptions.Builder<Orders>()
                .setQuery(userordermanageref.child("orders"),Orders.class)
                .build();


        FirebaseRecyclerAdapter<Orders,UserorderviewHolder> adapter = new FirebaseRecyclerAdapter<Orders, UserorderviewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserorderviewHolder userorderviewHolder, final int i, @NonNull final Orders orders) {


                userorderviewHolder.useroshopname.setText("shop name : "+orders.getShopname());
                userorderviewHolder.userototol.setText("totol price : "+ productType.notation+Double.parseDouble(orders.getTotprice())
                        +Double.parseDouble(orders.getDelivery_fee()));
                userorderviewHolder.userotime.setText("time : "+orders.getTime());
                userorderviewHolder.useroid.setText("order id : "+orders.getID());
                userorderviewHolder.userodate.setText("date : "+orders.getDate());

                if(orders.getPayment().equals("COD")){
                    userorderviewHolder.userotype.setText("COD");
                }else if(orders.getPayment().equals("COS")){
                    userorderviewHolder.userotype.setText("COS");
                }

                if(orders.getStatus().equals("delivered")){
                    userorderviewHolder.delete.setVisibility(View.VISIBLE);
                }

                userorderviewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(orderListActivity.this,orderManageActivity.class);
                        intent.putExtra("shop_name",orders.getShopname());
                        intent.putExtra("orderID",orders.getID());
                        startActivity(intent);

                    }
                });

                userorderviewHolder.userproduct_userviewbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(orderListActivity.this, orderProductsActivity.class);
                        intent.putExtra("orderId" , orders.getID());
                        startActivity(intent);
                    }
                });
                final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                        .child("Users").child(userPrevalent.current_user.getPhone())
                        .child("orders");
                final DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference()
                        .child("Admins").child(orders.getShopname()).child("Orders");
                final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                        .child("orders_ID");

                userorderviewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      userRef.child(orders.getID()).removeValue();
                      shopRef.child(orders.getID()).removeValue();
                      orderRef.child(orders.getID()).removeValue();
                    }
                });

            }

            @NonNull
            @Override
            public UserorderviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_layout,parent,false);
                UserorderviewHolder holder = new UserorderviewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public static class UserorderviewHolder extends RecyclerView.ViewHolder
    {

        public TextView userodate,useroid,userotime,userototol,useroshopname,delete,userotype;
        public Button userproduct_userviewbtn;


        public UserorderviewHolder(@NonNull View itemView) {
            super(itemView);

            userodate =itemView.findViewById(R.id.userorder_date);
            useroid =itemView.findViewById(R.id.userorder_orderid);
            userotime =itemView.findViewById(R.id.userorder_time);
            userototol =itemView.findViewById(R.id.userorder_totalprice);
            useroshopname =itemView.findViewById(R.id.userorder_shopname);
            userproduct_userviewbtn = itemView.findViewById(R.id.userorder_userviewbtn);
            delete = itemView.findViewById(R.id.userorder_delete);
            userotype = itemView.findViewById(R.id.userorder_type);

        }

    }
}
