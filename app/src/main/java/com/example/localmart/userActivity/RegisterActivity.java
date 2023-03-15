package com.example.localmart.userActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.localmart.Prevalent.productType;
import com.example.localmart.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText Newname,Newpassword,Newphonenumber,NewEmail;
    private Button Registerbutton;
    private ProgressDialog Loadingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Newname=(EditText)findViewById(R.id.Register_username);
        Newpassword=(EditText)findViewById(R.id.Register_password);
        Newphonenumber=(EditText)findViewById(R.id.Register_phonenumber);
        Registerbutton=(Button) findViewById(R.id.Register_btn);
        NewEmail =(EditText)findViewById(R.id.Register_useremail);
        Loadingbar = new ProgressDialog(this);

        Registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

    }

    private void validate()
    {
        String usermail = NewEmail.getText().toString().trim();
        String username = Newname.getText().toString().trim();
        String phno = Newphonenumber.getText().toString().trim();
        String password = Newpassword.getText().toString().trim();

        if(usermail.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "enter e-mail" ,Toast.LENGTH_SHORT).show();
        }
        else if(username.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "enter username" ,Toast.LENGTH_SHORT).show();
        }
        else if(phno.isEmpty()){
            Toast.makeText(RegisterActivity.this, "enter phone number" ,Toast.LENGTH_SHORT).show();
        }
        else if(password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "enter password" ,Toast.LENGTH_SHORT).show();
        }
        else if(!usermail.matches(productType.emailPattern)){
            Toast.makeText(RegisterActivity.this,"Invalid email", Toast.LENGTH_SHORT).show();
        }
        else if(!phno.matches(productType.phonePattern)){
            Toast.makeText(RegisterActivity.this, "Invalid phone number" ,Toast.LENGTH_SHORT).show();
        }
        else if(password.length()<productType.passwordLength){
            Toast.makeText(RegisterActivity.this, "minimum character should be "+productType.passwordLength ,Toast.LENGTH_SHORT).show();
        }
        else {
            Loadingbar.setMessage("registering your account...");
            Loadingbar.setCanceledOnTouchOutside(false);
            Loadingbar.show();
            createUser(username ,  phno ,  password , usermail);
        }

    }

    private void createUser(final String username, final String phno, final String password, final String usermail)
    {
        final DatabaseReference addUser = FirebaseDatabase.getInstance().getReference().child("Users");
        addUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child(phno).exists())
                {
                    HashMap<String , Object> setacc = new HashMap<>();
                    setacc.put("phone",phno);
                    setacc.put("email", usermail);
                    setacc.put("name",username);
                    setacc.put("password",password);
                    addUser.child(phno).updateChildren(setacc)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Loadingbar.dismiss();
                                        Toast.makeText(RegisterActivity.this ,"registered",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        finish();
                                    }else {
                                        Loadingbar.dismiss();
                                        Toast.makeText(RegisterActivity.this ,"error occured",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    Loadingbar.dismiss();
                    Toast.makeText(RegisterActivity.this ,"this phone number already taken",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
