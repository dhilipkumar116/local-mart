package com.example.localmart.userActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localmart.Prevalent.productType;
import com.example.localmart.Prevalent.userPrevalent;
import com.example.localmart.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;


public class SettingActivity extends AppCompatActivity {

    private  CircularImageView userprofileImageView;
    private EditText setusername, setuserorderphone, setuseraddress,setuserpassword , setuseremail;
    private TextView userprofileChangeTextBtn, usersettingsaveTextButton;
    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    private String checker = "";
    private Boolean hide = true;
    private ImageView hidepass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        userprofileImageView = findViewById(R.id.User_setting_profile_image);
        setuseremail = (EditText)findViewById(R.id.User_setting_email) ;
        setusername = (EditText) findViewById(R.id.User_setting_name);
        setuserorderphone = (EditText) findViewById(R.id.User_setting_phone_number);
        setuseraddress = (EditText) findViewById(R.id.User_setting_address);
        setuserpassword=(EditText)findViewById(R.id.User_setting_password);
        userprofileChangeTextBtn = (TextView) findViewById(R.id.User_profile_image_change_btn);
        usersettingsaveTextButton = (TextView) findViewById(R.id.User_update_setting);
        hidepass = findViewById(R.id.hide_pass_icon_U);


        userInfoDisplay(userprofileImageView, setuseremail,setusername, setuserorderphone, setuseraddress,setuserpassword);

        usersettingsaveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                validateForm();
            }
        });


        userprofileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingActivity.this);
            }
        });

        hidepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hide==true){
                    setuserpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    hide=false;
                }else {

                    setuserpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    hide=true;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == Activity.RESULT_OK && data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            checker = "clicked";
            userprofileImageView.setImageURI(imageUri);
        }

    }

    private void validateForm() {

        if(setuseremail.getText().toString().trim().equals("")){
            Toast.makeText(SettingActivity.this ,"enter email" ,Toast.LENGTH_SHORT).show();
        }
        else if(setusername.getText().toString().trim().equals("")){
            Toast.makeText(SettingActivity.this ,"enter name" ,Toast.LENGTH_SHORT).show();
        }
        else if(setuserorderphone.getText().toString().trim().equals("")){
            Toast.makeText(SettingActivity.this ,"enter phone number" ,Toast.LENGTH_SHORT).show();
        }
        else if(setuserpassword.getText().toString().trim().equals("")){
            Toast.makeText(SettingActivity.this ,"enter password" ,Toast.LENGTH_SHORT).show();
        }
        else if(setuseraddress.getText().toString().trim().equals("")){
            Toast.makeText(SettingActivity.this ,"enter address" ,Toast.LENGTH_SHORT).show();
        }
        else if(setuserorderphone.getText().toString().trim().equals(productType.phonePattern)){
            Toast.makeText(SettingActivity.this ,"enter valid phone number" ,Toast.LENGTH_SHORT).show();
        }
        else if(setuserpassword.length()<productType.passwordLength){
            Toast.makeText(SettingActivity.this ,"minimum character should be "+productType.passwordLength ,Toast.LENGTH_SHORT).show();
        }
        else {
            if (checker.equals("clicked")) {
                uploadImage();
            }
            else {
                updateOnlyUserInfo();
            }
        }

    }

    private void uploadImage()
    {
        if (imageUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait, while we are updating your account information");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(userPrevalent.current_user.getPhone() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task)
                        {
                            if (task.isSuccessful())
                            {
                                Uri downloadUrl = task.getResult();
                                myUrl = downloadUrl.toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap. put("name", setusername.getText().toString().trim());
                                userMap. put("email", setuseremail.getText().toString().trim());
                                userMap. put("address", setuseraddress.getText().toString().trim());
                                userMap. put("phoneOrder", setuserorderphone.getText().toString().trim());
                                userMap. put("password", setuserpassword.getText().toString().trim());
                                userMap. put("image", myUrl);
                                ref.child(userPrevalent.current_user.getPhone()).updateChildren(userMap);

                                progressDialog.dismiss();
                                Toast.makeText(SettingActivity.this, "profile updated", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(SettingActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }




    private void updateOnlyUserInfo()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("name", setusername.getText().toString().trim());
        userMap. put("email", setuseremail.getText().toString().trim());
        userMap. put("address", setuseraddress.getText().toString().trim());
        userMap. put("phoneOrder", setuserorderphone.getText().toString().trim());
        userMap. put("password", setuserpassword.getText().toString().trim());

        ref.child(userPrevalent.current_user.getPhone()).updateChildren(userMap);

        Toast.makeText(SettingActivity.this, "updated", Toast.LENGTH_SHORT).show();

    }

    private void userInfoDisplay(final CircularImageView userprofileImageView,
                                 final EditText setuseremail, final EditText setusername,
                                 final EditText setuserorderphone, final EditText setuseraddress,
                                 final EditText setuserpassword) {

        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userPrevalent.current_user.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists()) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(userprofileImageView);
                    }
                    if(dataSnapshot.child("address").exists()){
                        String address = dataSnapshot.child("address").getValue().toString();
                        setuseraddress.setText(address);
                    }
                    String email = dataSnapshot.child("email").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String phone = dataSnapshot.child("phone").getValue().toString();
                    String password = dataSnapshot.child("password").getValue().toString();
                    setuseremail.setText(email);
                    setusername.setText(name);
                    setuserorderphone.setText(phone);
                    setuserpassword.setText(password);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
