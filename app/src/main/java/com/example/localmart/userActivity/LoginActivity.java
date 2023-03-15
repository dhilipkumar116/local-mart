package com.example.localmart.userActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localmart.Prevalent.productType;
import com.example.localmart.Prevalent.userPrevalent;
import com.example.localmart.R;
import com.example.localmart.modelClass.Shops;
import com.example.localmart.modelClass.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import io.paperdb.Paper;

import static com.example.localmart.Prevalent.productType.passwordLength;

public class LoginActivity extends AppCompatActivity {
    private EditText phonenumber, password;
    private Button Loginbutton, signupbtn, forgetpassbtn;
    private ProgressDialog Loadingbar;
    private CheckBox Checkboxrememberme;

    private FirebaseAuth mAuth;
    private DatabaseReference curruserref;



    private RelativeLayout r1, r2, r3;

//
//    Handler handler = new Handler();
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//
//            r2.setVisibility(View.VISIBLE);
//            r1.setVisibility(View.VISIBLE);
//            Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.side_right);
//            r2.setAnimation(animation);
//            r1.setAnimation(animation);
//            checkifalreadyLOGEDIN();
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        r1 = (RelativeLayout) findViewById(R.id.r1);
        r2 = (RelativeLayout) findViewById(R.id.r2);
        r3 = (RelativeLayout) findViewById(R.id.r3);
//        handler.postDelayed(runnable, 2000);


        password = (EditText) findViewById(R.id.Ulogin_password);
        phonenumber = (EditText) findViewById(R.id.Ulogin_username);
        signupbtn = (Button) findViewById(R.id.Usignup_btn);
        forgetpassbtn = (Button) findViewById(R.id.Uforgotpassword_btn);
        Loginbutton = (Button) findViewById(R.id.Ulogin_btn);
        Loadingbar = new ProgressDialog(this);
        Checkboxrememberme = (CheckBox) findViewById(R.id.Ucheckbox);
        Paper.init(this);

        checkifalreadyLOGEDIN();


      forgetpassbtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              startActivity(new Intent(LoginActivity.this, UserForgotActivity.class));
          }
      });

      signupbtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
          }
      });

      Loginbutton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              validateData();
          }
      });

    }

    private void validateData()
    {
        String phno = phonenumber.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if(phno.isEmpty()){
            Toast.makeText(LoginActivity.this,"enter phonenumber",Toast.LENGTH_SHORT).show();
        }
        else if(pass.isEmpty()){
            Toast.makeText(LoginActivity.this,"enter password",Toast.LENGTH_SHORT).show();
        }
        else if(!phno.trim().matches(productType.phonePattern)){
            Toast.makeText(LoginActivity.this,"Invalid PhoneNumber",Toast.LENGTH_SHORT).show();
        }
        else if(pass.length()<productType.passwordLength){
            Toast.makeText(LoginActivity.this,"minimum charater should be "+ passwordLength,Toast.LENGTH_SHORT).show();
        }
        else {
            Loadingbar.setMessage("verifying your account...");
            Loadingbar.setCanceledOnTouchOutside(false);
            Loadingbar.show();

            AllowAccessToAccount(phno,pass);
        }

    }

    private void AllowAccessToAccount(final String phno, final String pass)
    {
        curruserref = FirebaseDatabase.getInstance().getReference().child("Users");
        curruserref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(phno).exists())
                {
                    final Users users = dataSnapshot.child(phno).getValue(Users.class);
                    if(pass.equals(users.getPassword()))
                    {
                        String device_token = FirebaseInstanceId.getInstance().getToken();
//                        String currentShopID = mAuth.getCurrentUser().getUid();
                        curruserref.child(phno).child("device_token").setValue(device_token);
                        curruserref.child(phno).child("uid").setValue("currentShopID")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Loadingbar.dismiss();
                                            userPrevalent.current_user = users;
                                            if(Checkboxrememberme.isChecked())
                                            {
                                                Paper.book("user").write(userPrevalent.Userphkey, phno);
                                                Paper.book("user").write(userPrevalent.Userpasskey, pass);
                                            }
                                            startActivity(new Intent(LoginActivity.this,ShopListActivity.class));

                                        }else {
                                            Loadingbar.dismiss();
                                            Toast.makeText(LoginActivity.this,"error",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }else {
                        Loadingbar.dismiss();
                        Toast.makeText(LoginActivity.this,"incorrect password",Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Loadingbar.dismiss();
                    Toast.makeText(LoginActivity.this,"account not present",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void  checkifalreadyLOGEDIN()
    {
        String userph = Paper.book("user").read(userPrevalent.Userphkey);
        String userpass = Paper.book("user").read(userPrevalent.Userpasskey);


            if (!TextUtils.isEmpty(userpass) && !TextUtils.isEmpty(userph)) {
                Loadingbar.setMessage("already logged in...  please wait");
                Loadingbar.setCanceledOnTouchOutside(false);
                Loadingbar.show();
                AllowAccessToAccount(userph, userpass);
            }
    }



}
