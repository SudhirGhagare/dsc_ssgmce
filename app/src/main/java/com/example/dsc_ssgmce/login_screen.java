package com.example.dsc_ssgmce;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class login_screen<GoogleSignInClient> extends Activity {

    private FirebaseAuth mAuth;

    private EditText mUserName;
    private EditText mPassword;
    private TextView mPasswordReset;
    private ImageView mGoogleSignIn;
    private ImageView mFaceBookSignIn;
    private ImageView mGitHubSignIn;
    private int RC_SIGN_IN = 1;

    com.google.android.gms.auth.api.signin.GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        // checking the id's for all element
        ConstraintLayout constraintLayout = findViewById(R.id.root_layout);
        mUserName = findViewById(R.id.user_name);
        mPassword = findViewById(R.id.user_password);
        mPasswordReset = findViewById(R.id.reset_password);
        mGoogleSignIn = findViewById(R.id.google_sign_in);
        mFaceBookSignIn = findViewById(R.id.facebook_sign_in);
        mGitHubSignIn = findViewById(R.id.git_hub_sign_in);

        //for Setting up the background Animation
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient =  GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        mGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                  startActivityForResult(signInIntent,RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    private  void handleSignInResult(Task<GoogleSignInAccount> taskCompletedTask){

        try{
            GoogleSignInAccount acc = taskCompletedTask.getResult(ApiException.class);
            Toast.makeText(login_screen.this, "Signed In Successfully", Toast.LENGTH_LONG).show();
            FirebaseGoogleAuth(null);
        }catch (ApiException e){
            Toast.makeText(login_screen.this, "Signed In Failed", Toast.LENGTH_LONG).show();
        }

    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {

        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(login_screen.this, "Signed In Successfully", Toast.LENGTH_LONG).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }else{
                    Toast.makeText(login_screen.this, "Signed In Failed", Toast.LENGTH_LONG).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser fUser) {

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
