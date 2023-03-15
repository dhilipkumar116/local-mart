package com.example.localmart.userActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localmart.PlacePickerActivity;
import com.example.localmart.Prevalent.productType;
import com.example.localmart.Prevalent.userPrevalent;
import com.example.localmart.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PlaceOrderActivity extends AppCompatActivity {

    private String shopName, totalPrice="0.0";
    private TextView pic_location, pic_location_indicator, address_entered_indicator,
            payment_chosen, total_payment_cos, total_payment_cod;
    private EditText confirm_name, confirm_phone, confirm_address;
    private Button place_order;
    private RadioGroup paymentGrp;
    private String phoneNum, address, name;
    private Double lat = 0.0, lang = 0.0;
    private ProgressDialog progressDialog;
    private String savecurrentdate, savecurrenttime, orderID;
    private RadioButton paymentRadiobtn;
    private Double shopLat = 0.0, shopLong = 0.0;
    private String deliveryPrice="0.0", subtot;
    private String cod_total="0.0", cod_total_sub="0.0", distance;
    private DatabaseReference frommode, tonode;
    private Double deliveryPricein20km, deliveryPriceoout20km;


    private String shopAdd,shopPh,bankNum="not available",bankName="not available";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        progressDialog = new ProgressDialog(this);

        shopName = getIntent().getStringExtra("shop_name");
        totalPrice = getIntent().getStringExtra("total_price");

        phoneNum = getIntent().getStringExtra("phno");
        address = getIntent().getStringExtra("address");
        name = getIntent().getStringExtra("name");
        lat = getIntent().getDoubleExtra("lat", 0.0);
        lang = getIntent().getDoubleExtra("lang", 0.0);
        distance = getIntent().getStringExtra("distance");
        deliveryPricein20km = getIntent().getDoubleExtra("price20km", 0.0);
        deliveryPriceoout20km = getIntent().getDoubleExtra("priceA20km", 0.0);


        pic_location = findViewById(R.id.choose_delivery_add_from_map);
        pic_location_indicator = findViewById(R.id.address_indicator1);
        confirm_name = findViewById(R.id.confirm_name);
        confirm_phone = findViewById(R.id.confirm_phonenumber);
        confirm_address = findViewById(R.id.confirm_address);
        address_entered_indicator = findViewById(R.id.address_indicator2);
        payment_chosen = findViewById(R.id.payment_indicator3);
        place_order = findViewById(R.id.place_order);
        total_payment_cod = findViewById(R.id.total_payment_cod);
        total_payment_cos = findViewById(R.id.total_payment_cos);


        Animation animation = AnimationUtils.loadAnimation(PlaceOrderActivity.this, R.anim.bottom_top);
        place_order.setAnimation(animation);

        paymentGrp = findViewById(R.id.confirm_ordertyperadiogrp);
        paymentGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (lat != 0.0 && lang != 0.0) {
                    if (Double.parseDouble(distance) < 20) {

                        deliveryPrice = String.format("%.2f", Math.ceil(Double.parseDouble(distance) * deliveryPricein20km));
                        cod_total = String.format("%.2f", Double.parseDouble(deliveryPrice)
                                + Double.parseDouble(totalPrice));
                    } else {
                        deliveryPrice = String.format("%.2f", Math.ceil(Double.parseDouble(distance) * deliveryPriceoout20km));
                        cod_total = String.format("%.2f", Double.parseDouble(deliveryPrice)
                                + Double.parseDouble(totalPrice));
                    }
                }
                switch (checkedId) {
                    case R.id.radio_cos: {
                        if(lang==0.0&&lat==0.0){
                            Toast.makeText(PlaceOrderActivity.this,"pick location",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        total_payment_cod.setVisibility(View.GONE);
                        total_payment_cos.setVisibility(View.VISIBLE);
                        total_payment_cos.setText("product price : "+productType.notation + totalPrice + "\n" +
                                "delivery fee : "+productType.notation + 0.00 + "\n"
                                + "total price : "+productType.notation + totalPrice);
                        payment_chosen.setText("payment selected");
                        payment_chosen.setTextColor(getResources().getColor(R.color.green));
                        break;
                    }
                    case R.id.radio_cod: {
                        if(lang==0.0&&lat==0.0){
                            Toast.makeText(PlaceOrderActivity.this,"pick location",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        total_payment_cos.setVisibility(View.GONE);
                        total_payment_cod.setVisibility(View.VISIBLE);
                        total_payment_cod.setText("product price : "+productType.notation + totalPrice + "\n" +
                                "delivery fee : "+productType.notation + deliveryPrice + "\n"
                                + "total price : "+productType.notation + cod_total);
                        payment_chosen.setText("payment selected");
                        payment_chosen.setTextColor(getResources().getColor(R.color.green));
                        break;
                    }
                }
            }
        });



        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lang == 0.0 && lat == 0.0) {
                    Toast.makeText(PlaceOrderActivity.this, "pick location", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(confirm_phone.getText().toString())
                        || TextUtils.isEmpty(confirm_name.getText().toString())
                        || TextUtils.isEmpty(confirm_address.getText().toString())) {
                    Toast.makeText(PlaceOrderActivity.this, "enter all detials", Toast.LENGTH_SHORT).show();
                }
                else if (!confirm_phone.getText().toString().trim().matches(productType.phonePattern)) {
                    Toast.makeText(PlaceOrderActivity.this, "Invalid phonenumber", Toast.LENGTH_SHORT).show();
                }else if (paymentGrp.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(PlaceOrderActivity.this, "choose payment", Toast.LENGTH_SHORT).show();
                } else {
                    int type = paymentGrp.getCheckedRadioButtonId();
                    paymentRadiobtn = ((RadioButton) findViewById(type));
                    progressDialog.setMessage("placing order...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    placeOrderToShop();
                }
            }
        });

        pic_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaceOrderActivity.this, PlacePickerActivity.class);
                intent.putExtra("user/admin", "user");
                intent.putExtra("shopname", shopName);
                intent.putExtra("totolprice", totalPrice);
                startActivity(intent);
                finish();
            }
        });

        confirm_name.setText(name);
        confirm_address.setText(address);
        confirm_phone.setText(phoneNum);
        if (!TextUtils.isEmpty(confirm_phone.getText().toString())
                && !TextUtils.isEmpty(confirm_name.getText().toString())
                && !TextUtils.isEmpty(confirm_address.getText().toString())) {
            address_entered_indicator.setTextColor(getResources().getColor(R.color.green));
            address_entered_indicator.setText("address entered");
        }
        if (lang > 0.0 && lat > 0.0) {
            pic_location_indicator.setTextColor(getResources().getColor(R.color.green));
            pic_location_indicator.setText("address picked");
        }


    }


    private void placeOrderToShop() {

        Calendar calfordate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("MMM dd,yyyy");
        savecurrentdate = currentdate.format(calfordate.getTime());
        SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm:ss a");
        savecurrenttime = currenttime.format(calfordate.getTime());
        orderID = savecurrentdate + savecurrenttime;

        frommode = FirebaseDatabase.getInstance().getReference()
                .child("cart_list")
                .child("User view")
                .child(userPrevalent.current_user.getPhone())
                .child("cart_products")
                .child(shopName);

        tonode = FirebaseDatabase.getInstance().getReference()
                .child("orders_ID")
                .child(orderID);


        frommode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                tonode.child("products").setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Confirmorder();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(shopName);
        shopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("bank_name").exists()){
                    bankName = snapshot.child("bank_name").getValue().toString();
                }
                if(snapshot.child("bank_number").exists()){
                    bankNum = snapshot.child("bank_number").getValue().toString();
                }
                shopPh = snapshot.child("shop_phone").getValue().toString();
                shopAdd = snapshot.child("street").getValue().toString()+" "+
                        snapshot.child("district").getValue().toString()+" "+
                        snapshot.child("postcode").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Confirmorder() {
        final DatabaseReference userorderref = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userPrevalent.current_user.getPhone())
                .child("orders")
                .child(orderID);

        final DatabaseReference orderref = FirebaseDatabase.getInstance().getReference().child("Admins")
                .child(shopName).child("Orders").child(orderID);

        final DatabaseReference checkcartbtnref = FirebaseDatabase.getInstance().getReference()
                .child("cart_list").child("checkcartenable")
                .child(userPrevalent.current_user.getPhone())
                .child(shopName);

        checkcartbtnref.child("status").setValue("orderplaced").isSuccessful();

        HashMap<String, Object> ordermap = new HashMap<>();
        ordermap.put("ordername", confirm_name.getText().toString());
        ordermap.put("userId", userPrevalent.current_user.getPhone());
        ordermap.put("phone_number", confirm_phone.getText().toString());
        ordermap.put("address", confirm_address.getText().toString());
        ordermap.put("time", savecurrenttime);
        ordermap.put("date", savecurrentdate);
        ordermap.put("ID", orderID);
        ordermap.put("shopname", shopName);
        ordermap.put("shop_ph",shopPh);
        ordermap.put("shop_address",shopAdd);
        ordermap.put("bankNum",bankNum);
        ordermap.put("bankName",bankName);
        if (paymentRadiobtn.getText().toString().equals("cash on shop")) {
            ordermap.put("payment", "COS");
            ordermap.put("Totprice", totalPrice);
            ordermap.put("delivery_fee", "0");
            ordermap.put("km", distance);
        }
        if (paymentRadiobtn.getText().toString().equals("cash on delivery")) {
            ordermap.put("payment", "COD");
            ordermap.put("Totprice", totalPrice);
            ordermap.put("delivery_fee", deliveryPrice);
            ordermap.put("km", distance);
        }
        ordermap.put("latitude", lat);
        ordermap.put("longtitude", lang);
        ordermap.put("status", "orderplaced");

        userorderref.updateChildren(ordermap).isSuccessful();
        tonode.updateChildren(ordermap).isSuccessful();
        orderref.updateChildren(ordermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    DatabaseReference notificationref = FirebaseDatabase.getInstance()
                            .getReference().child("notification");
                    HashMap<String, String> notification = new HashMap<>();
                    notification.put("from", userPrevalent.current_user.getName());
                    notification.put("info", "placed an order to your shop!!");
                    notificationref.child(shopName).push().setValue(notification)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(PlaceOrderActivity.this,
                                                "order placed successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(PlaceOrderActivity.this,
                                                UserHomeActivity.class);
                                        intent.putExtra("shopname", shopName);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(PlaceOrderActivity.this, task.getException()
                                                .getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }


            }
        });
    }


}
