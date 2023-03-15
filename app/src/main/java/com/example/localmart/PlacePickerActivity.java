package com.example.localmart;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.localmart.Prevalent.productType;
import com.example.localmart.Prevalent.userPrevalent;
import com.example.localmart.userActivity.PlaceOrderActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.HashMap;

public class PlacePickerActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int PERMISSION_ID = 44;
    private FusedLocationProviderClient mfusedLocation;
    private Location mLastLocation;
    private Double lat , lang ;

    private LinearLayout linearLayout;
    private EditText phno, name, full_address;
    private Button set_location;
    private TextView dummyText;

    private String useroradmin, shopname , totalprice;
    private ProgressDialog progressDialog;
    private Double shopLat, shopLong ,distance,Pricein20km,Priceoout20km;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);


        linearLayout = findViewById(R.id.place_picker_layout);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.bottom_top);
        linearLayout.setAnimation(animation);

        useroradmin = getIntent().getStringExtra("user/admin");
        shopname = getIntent().getStringExtra("shopname");
        totalprice = getIntent().getStringExtra("totolprice");

        phno = (EditText) findViewById(R.id.pick_confirm_phonenumber);
        name = (EditText) findViewById(R.id.pick_confirm_name);
        full_address = (EditText) findViewById(R.id.pick_confirm_address);
        set_location = (Button) findViewById(R.id.pick_set_location_btn);
        dummyText = findViewById(R.id.pick_confirm_text);

        mfusedLocation = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if(useroradmin.equals("admin")){
            dummyText.setText("*choose shop location");
            phno.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
            full_address.setVisibility(View.GONE);
        }else {
            getsavedaddress();
        }

        set_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkItUserOrAdmin();

            }
        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                CameraPosition cameraPosition = mMap.getCameraPosition();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                lat = cameraPosition.target.latitude;
                lang = cameraPosition.target.longitude;

            }
        });

        if(checkPermissions()){
            if(isLocationEnabled()){
                enableUserLocation();
                zoomToUserLocation();
            }else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }else {
            requestPermissions();
        }

    }

    private boolean checkPermissions() {
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }
    private void enableUserLocation() {
        mMap.setMyLocationEnabled(true);
    }
    private void zoomToUserLocation() {
        Task<Location> locationTask = mfusedLocation.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if(location.getLatitude() != 0.0 && location.getLongitude() != 0.0){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                }else {
                    zoomToUserLocation();
                }

            }
        });
    }

    private void checkItUserOrAdmin() {
            if(lang.equals(0.0) && lat.equals(0.0)){
                Toast.makeText(this, "choose location", Toast.LENGTH_LONG).show();
            }else {
                getsavedaddress();
                deliveryPaymentCalculation();
                userAction();
            }
    }

    private void userAction() {
        if (TextUtils.isEmpty(phno.getText().toString()))
            Toast.makeText(PlacePickerActivity.this, "enter phone number", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(name.getText().toString()))
            Toast.makeText(PlacePickerActivity.this, "enter name", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(full_address.getText().toString()))
            Toast.makeText(PlacePickerActivity.this, "enter delivery address", Toast.LENGTH_SHORT).show();
        else if (!phno.getText().toString().trim().matches(productType.phonePattern))
            Toast.makeText(PlacePickerActivity.this, "incorrect phone number", Toast.LENGTH_SHORT).show();
        else {

            DatabaseReference saveaddressref=FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(userPrevalent.current_user.getPhone());
            HashMap<String,Object> saveadd=new HashMap<>();
            saveadd.put("phone",phno.getText().toString());
            saveadd.put("name",name.getText().toString());
            saveadd.put("address",full_address.getText().toString());
            saveaddressref.child("orderaddress").updateChildren(saveadd);

            Intent intent = new Intent(PlacePickerActivity.this, PlaceOrderActivity.class);
            intent.putExtra("lat", lat);
            intent.putExtra("lang", lang);
            intent.putExtra("shop_name", shopname);
            intent.putExtra("price20km",Pricein20km);
            intent.putExtra("priceA20km",Priceoout20km);
            intent.putExtra("total_price", totalprice);
            intent.putExtra("phno", phno.getText().toString().trim());
            intent.putExtra("name", name.getText().toString().trim());
            intent.putExtra("address", full_address.getText().toString().trim());
            intent.putExtra("distance", String.format("%.2f", distance/1000));
            startActivity(intent);
            finish();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled()) {
                    enableUserLocation();
                    zoomToUserLocation();
                } else {
                    Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }

            } else {
                requestPermissions();
            }
        }
    }
    private void deliveryPaymentCalculation() {

            LatLng fromLatLang = new LatLng(lat,lang);
            LatLng toLatLang = new LatLng(shopLat,shopLong);
            distance = SphericalUtil.computeDistanceBetween(fromLatLang,toLatLang);
    }
    @Override
    protected void onStart() {
        super.onStart();
        final DatabaseReference shopLocationRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(shopname);
        shopLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("latitude").exists()){
                    shopLat = (Double) dataSnapshot.child("latitude").getValue();
                    shopLong = (Double) dataSnapshot.child("longtitude").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference utilites = FirebaseDatabase.getInstance().getReference()
                .child("UTILITIES");
        utilites.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Pricein20km = Double.parseDouble(dataSnapshot.child("delivery20km").getValue().toString());
                Priceoout20km = Double.parseDouble(dataSnapshot.child("deliveryabove20km").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getsavedaddress() {
        DatabaseReference saveaddressref2;

        saveaddressref2 = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userPrevalent.current_user.getPhone());
        saveaddressref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("orderaddress").exists()) {
                    name.setText(dataSnapshot.child("orderaddress").child("name").getValue().toString());
                    full_address.setText(dataSnapshot.child("orderaddress").child("address").getValue().toString());
                    phno.setText(dataSnapshot.child("orderaddress").child("phone").getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
