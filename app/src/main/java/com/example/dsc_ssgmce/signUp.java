package com.example.dsc_ssgmce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class signUp extends AppCompatActivity {

    CircleImageView mImageView;
    EditText mFullName;
    EditText mPhoneNumber;
    EditText mPasswordSignUp;
    EditText mEmailId;
    ProgressBar progressBar;

    FirebaseAuth fAuth;
    DatabaseReference mRealTimeDatabase;
    StorageReference storageReference;

    Uri filepath;
    Bitmap bitmap;
    String UserID = "";

    RadioButton radioButton;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mRealTimeDatabase = FirebaseDatabase.getInstance().getReference().child("User Details");


        LinearLayout linearLayout = findViewById(R.id.linearLayout1);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();

        mFullName = findViewById(R.id.full_name);
        mImageView = findViewById(R.id.profile_image);
        mPhoneNumber = findViewById(R.id.phone_Number);
        mEmailId = findViewById(R.id.emailId);
        mPasswordSignUp = findViewById(R.id.password_sign_Up);

        radioGroup = findViewById(R.id.radioGender);


        progressBar = findViewById(R.id.progress_Bar);
        fAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        UserID=user.getUid();

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Please Select File"), 101);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();


            }

        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==101 && resultCode==RESULT_OK)
        {
            filepath=data.getData();
            try {

                InputStream inputStream = getContentResolver().openInputStream(filepath);
                bitmap= BitmapFactory.decodeStream(inputStream);
                mImageView.setImageBitmap(bitmap);

            }catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
            }
        }

    }

    public void resisterUser(View view) {

        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(selectedId);

        String username = mFullName.getText().toString().trim();
        String email = mEmailId.getText().toString().trim();
        String phoneNumber = mPhoneNumber.getText().toString().trim();
        String passWord = mPasswordSignUp.getText().toString().trim();
        String gender = radioButton.getText().toString().trim();


        User user = new User(username, email , passWord , phoneNumber,gender);
        mRealTimeDatabase.push().setValue(user);

        if (TextUtils.isEmpty(email)){
            mEmailId.setError("Email is Required");
            return;
        }

       if (TextUtils.isEmpty(passWord)){
           mPasswordSignUp.setError("Password is Required");
           return;
       }

       if (passWord.length() < 6){
           mPasswordSignUp.setError("Password Must be >= 6 Characters");
           return;
       }

       progressBar.setVisibility(View.VISIBLE);
       fAuth.createUserWithEmailAndPassword(email, passWord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if (task.isSuccessful()){
                   Toast.makeText(signUp.this, " Congratulations You had been Register",Toast.LENGTH_LONG).show();
                   startActivity(new Intent(getApplicationContext(), MainActivity.class));
               }else{
                     Toast.makeText(signUp.this, "Error"+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                     progressBar.setVisibility(View.GONE);
               }
           }
       });


        final StorageReference uploader = storageReference.child("Profile Images"+"img"+System.currentTimeMillis());
        uploader.putFile(filepath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final Map<String, Object> map = new HashMap<>();
                                map.put("Profile  Image",uri.toString());
                                map.put("User Name", username);

                                mRealTimeDatabase.child(UserID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists())
                                            mRealTimeDatabase.child(UserID).updateChildren(map);
                                        else
                                            mRealTimeDatabase.child(UserID).setValue(map);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });


                            }
                        });
                    }
                });

                }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        UserID = user.getUid();
        mRealTimeDatabase.child(UserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    mFullName.setText(snapshot.child("Profile  Image").getValue().toString());
                    Glide.with(getApplicationContext()).load(snapshot.child("User Name").getValue().toString()).into(mImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}