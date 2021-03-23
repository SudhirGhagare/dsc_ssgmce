package com.example.dsc_ssgmce;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class login_screen extends Activity {
    private FirebaseAuth mAuth;
    private EditText mUserName;
    private EditText mPassword;
    private TextView mPasswordReset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        // checking the id's for all element
        ConstraintLayout constraintLayout = findViewById(R.id.root_layout);
        mUserName = findViewById(R.id.user_name);
        mPassword = findViewById(R.id.user_password);
        mPasswordReset = findViewById(R.id.reset_password);

        //for Setting up the background Animation
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    private void reload() {
    }

    public void logIn(View view) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();



        Intent intent = new Intent(login_screen.this, MainActivity.class);
        startActivity(intent);
    }

    public void sign_Up(View view) {

        Intent intent = new Intent(login_screen.this, signUp.class);
        startActivity(intent);


    }
}
