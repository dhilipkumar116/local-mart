package com.example.localmart.userActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localmart.Prevalent.productType;
import com.example.localmart.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserForgotActivity extends AppCompatActivity {


    private EditText email, phno;
    private Button getpassword;
    private TextView showpassword;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_forgot);

        email = findViewById(R.id.forgot_email);
        phno = findViewById(R.id.forgot_phno);
        getpassword = findViewById(R.id.getPassword);
        showpassword = findViewById(R.id.showPassword);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("getting password...");
        progressDialog.setCanceledOnTouchOutside(false);

        getpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().trim().isEmpty()) {
                    Toast.makeText(UserForgotActivity.this, "enter email", Toast.LENGTH_SHORT).show();
                } else if (phno.getText().toString().trim().isEmpty()) {
                    Toast.makeText(UserForgotActivity.this, "enter phone number", Toast.LENGTH_SHORT).show();
                } else if (!email.getText().toString().trim().matches(emailPattern)) {
                    Toast.makeText(UserForgotActivity.this, "incorrect email", Toast.LENGTH_SHORT).show();
                } else if (!phno.getText().toString().trim().matches(productType.phonePattern)) {
                    Toast.makeText(UserForgotActivity.this, "incorrect phone number", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.show();
                    getPassword();
                }

            }
        });
    }

    private void getPassword() {
        DatabaseReference passRef = FirebaseDatabase.getInstance().getReference()
                .child("Users");
        passRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(phno.getText().toString().trim()).exists()) {
                    if (email.getText().toString().trim().equals(
                            dataSnapshot.child(phno.getText().toString().trim())
                                    .child("email").getValue().toString()
                    )) {

                        progressDialog.dismiss();
                        showpassword.setVisibility(View.VISIBLE);
                        showpassword.setText("password : " + dataSnapshot.child(phno.getText().toString().trim())
                                .child("password").getValue().toString());

                    } else {
                        Toast.makeText(UserForgotActivity.this, "email is not matching", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(UserForgotActivity.this, "phone number is not present", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
