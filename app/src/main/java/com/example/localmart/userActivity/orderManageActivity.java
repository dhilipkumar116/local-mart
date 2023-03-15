package com.example.localmart.userActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localmart.Prevalent.productType;
import com.example.localmart.Prevalent.userPrevalent;
import com.example.localmart.R;
import com.example.localmart.modelClass.Orders;
import com.example.localmart.modelClass.Shops;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kofigyan.stateprogressbar.StateProgressBar;

public class orderManageActivity extends AppCompatActivity {

    private String shopName,orderId;
    String[] descriptionData = {"placed","confirmed", "picked up", "on the way", "delivered"};
    private DatabaseReference orderuserRef,ordershopRef,cancelRef,orderIdRef;
    private StateProgressBar stateProgressBar;
    private TextView sname,sphone,semail,sstreet,sdistrict,spincode,
            cancelid,cancelamount,canceldate;
    private Button cancelbtn,cancelcall,deliverycall;
    private TextView deliName,deliPhone,deliAmount,deliVnum,deliVtype;
    private String deliveryID;
    private String orderStatus;
    private CardView delivery_cardView;
    private TextView codORcos ,estimatedText;
    private String shopPhno,deliveryGuyPhno,estimateddeliveryTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_manage);

        shopName=getIntent().getStringExtra("shop_name");
        orderId = getIntent().getStringExtra("orderID");


        ordershopRef = FirebaseDatabase.getInstance().getReference().child("Admins")
                .child(shopName).child("Orders").child(orderId);
        orderuserRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userPrevalent.current_user.getPhone()).child("orders").child(orderId);
        orderIdRef = FirebaseDatabase.getInstance().getReference().child("orders_ID").child(orderId);

        stateProgressBar = (StateProgressBar) findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(descriptionData);

        sname = (TextView)findViewById(R.id.shopinfo_name);
        sphone = (TextView)findViewById(R.id.shopinfo_phone);
        semail = (TextView)findViewById(R.id.shopinfo_email);
        sstreet = (TextView)findViewById(R.id.shopinfo_street);
        sdistrict = (TextView)findViewById(R.id.shopinfo_area);
        spincode = (TextView)findViewById(R.id.shopinfo_pincode);
        codORcos = findViewById(R.id.codORcos);
        estimatedText = findViewById(R.id.estimatedText);

        cancelamount = (TextView)findViewById(R.id.cancelordermanage_amount);
        cancelid = (TextView)findViewById(R.id.cancelordermanage_id);
        canceldate = (TextView)findViewById(R.id.cancelordermanage_date);
        cancelbtn = findViewById(R.id.cancel_order_btn);
        cancelcall = findViewById(R.id.cancel_order_phone);

        deliName = findViewById(R.id.delivery_name);
        deliPhone = findViewById(R.id.delivery_phone);
        deliAmount = findViewById(R.id.delivery_amount);
        deliverycall = findViewById(R.id.delivery_call);
        deliverycall.setEnabled(false);
        deliVnum = findViewById(R.id.delivery_vehicleNum);
        deliVtype = findViewById(R.id.delivery_vehicleType);
        delivery_cardView = findViewById(R.id.delivery_cardView);

        cancelcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent makecall = new Intent(Intent.ACTION_DIAL);
                makecall.setData(Uri.parse("tel:" + shopPhno));
                startActivity(makecall);
            }
        });

        deliverycall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent makecall = new Intent(Intent.ACTION_DIAL);
                makecall.setData(Uri.parse("tel:" + deliveryGuyPhno));
                startActivity(makecall);
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(orderManageActivity.this);
                builder.setCancelable(true);
                builder.setMessage("Do you want to cancel order ?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(orderStatus.equals("picked up")||orderStatus.equals("on the way")||
                                orderStatus.equals("delivered")){
                            Toast.makeText(orderManageActivity.this,"you cannot able to cancel order" +
                                    " , after picked up !",Toast.LENGTH_SHORT).show();
                        }else {
                            ordershopRef.child("status").setValue("cancelled").isSuccessful();
                            orderuserRef.child("status").setValue("cancelled").isSuccessful();
                            orderIdRef.child("status").setValue("cancelled").isSuccessful();
                            estimatedText.setVisibility(View.VISIBLE);
                            estimatedText.setText("your has been cancelled!");
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        orderManageRef();
        getshopDetails();
        getdeliveryTime();

    }


    private void getdeliveryTime() {
        DatabaseReference util = FirebaseDatabase.getInstance().getReference()
                .child("UTILITIES");
        util.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                estimateddeliveryTime = dataSnapshot.child("mindeliverytime").getValue().toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void orderManageRef() {
        orderuserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("status").getValue().equals("orderplaced")) {
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                } else if(dataSnapshot.child("status").getValue().equals("ready for delivery")) {
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                }else if(dataSnapshot.child("status").getValue().equals("confirmed")) {
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                } else if(dataSnapshot.child("status").getValue().equals("picked up")) {
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
                } else if(dataSnapshot.child("status").getValue().equals("on the way")) {
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FIVE);
                }  else if(dataSnapshot.child("status").getValue().equals("delivered")) {
                    stateProgressBar.setAllStatesCompleted(true);
                    estimatedText.setVisibility(View.VISIBLE);
                    estimatedText.setText("order sucessfully delivered, thankyou!");
                }else {
                    stateProgressBar.setVisibility(View.INVISIBLE);
                    cancelbtn.setText("ORDER CANCELLED");
                    cancelbtn.setEnabled(false);
                    estimatedText.setVisibility(View.VISIBLE);
                    estimatedText.setText("your has been cancelled!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getshopDetails() {
        DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(shopName);
        shopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Orders").child(orderId).exists()) {
                    Shops shops = dataSnapshot.getValue(Shops.class);
                    sname.setText("shop name : "+shops.getShop_name());
                    sphone.setText("shop phno : "+shops.getShop_phone());
                    semail.setText("email : "+shops.getShop_email());
                    sstreet.setText("street : "+shops.getStreet());
                    sdistrict.setText("district : "+shops.getDistrict());
                    spincode.setText("postal code : "+shops.getPostcode());
                    shopPhno = shops.getShop_phone();

                    Orders orderdetial = dataSnapshot.child("Orders").child(orderId).getValue(Orders.class);
                    cancelid.setText("order id : "+orderId);
                    cancelamount.setText("product amount : RM"+orderdetial.getTotprice());
                    canceldate.setText("date : "+orderdetial.getDate()+" time : "+orderdetial.getTime());
                    orderStatus = orderdetial.getStatus();
                    deliAmount.setText("total amount : "+ productType.notation+orderdetial.getTotprice()+" + "+productType.notation+orderdetial.getDelivery_fee());
                    if(orderdetial.getPayment().equals("COD") &&
                            dataSnapshot.child("Orders").child(orderId).child("deliveryID").exists()){
                        getDeliveryInfo(orderdetial.getDeliveryID());
                        deliverycall.setEnabled(true);
                        codORcos.setText("COD");
                    }else if(orderdetial.getPayment().equals("COS")){
                        delivery_cardView.setVisibility(View.GONE);
                        codORcos.setText("COS");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getDeliveryInfo(String deliveryID) {
        final DatabaseReference deliveryRef = FirebaseDatabase.getInstance().getReference()
                .child("delivery_account").child(deliveryID);
        deliveryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deliName.setText("name : "+dataSnapshot.child("username").getValue().toString());
                deliPhone.setText("ph no : "+dataSnapshot.child("phno").getValue().toString());
                deliVnum.setText("vehicle num : "+dataSnapshot.child("vehicleNum").getValue().toString());
                deliVtype.setText("vehicle type : "+dataSnapshot.child("vehicleType").getValue().toString());
                deliveryGuyPhno = dataSnapshot.child("phno").getValue().toString();
                estimatedText.setVisibility(View.VISIBLE);
                estimatedText.setText("your order will be recieved within "+estimateddeliveryTime+" hours !");

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
