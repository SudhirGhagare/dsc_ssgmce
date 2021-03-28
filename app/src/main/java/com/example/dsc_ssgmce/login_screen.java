package com.example.dsc_ssgmce;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;
import java.util.List;


public class login_screen<GoogleSignInClient> extends Activity {

    private FirebaseAuth mAuth;

    private EditText mUserName;
    private EditText mPassword;
    private TextView mPasswordReset;
    private ImageView mGoogleSignIn;
    private LoginButton mFaceBookSignIn;
    private ImageView mGitHubSignIn;

    private static final int RC_SIGN_IN = 9001;
    private CallbackManager mCallbackManager;
    private static final String TAG = "Login";

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

        // Request read access to a user's email addresses.
        // This must be preconfigured in the app's API permissions.

        mGitHubSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");
                provider.addCustomParameter("login", "your-email@gmail.com");
                List<String> scopes =
                        new ArrayList<String>() {
                            {
                                add("user:email");
                            }
                        };
                provider.setScopes(scopes);

                Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
                if (pendingResultTask != null) {
                    // There's something already here! Finish the sign-in for your user.
                    pendingResultTask
                            .addOnSuccessListener(
                                    new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {

                                             authResult.getAdditionalUserInfo().getProfile();

                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle failure.
                                        }
                                    });

                    // The user is already signed-in.



                } else {
                    // There's no pending result so you need to start the sign-in flow.
                    // See below.
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                    firebaseUser
                            .startActivityForLinkWithProvider( login_screen.this, provider.build())
                            .addOnSuccessListener(
                                    new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            // GitHub credential is linked to the current user.
                                            // IdP data available in
                                            authResult.getAdditionalUserInfo().getProfile();
                                            // The OAuth access token can also be retrieved:
                                            authResult.getCredential().getSignInMethod();
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle failure.
                                            Toast.makeText(login_screen.this,"Login Error :"+e.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });

                    // The user is already signed-in.

                    firebaseUser
                            .startActivityForReauthenticateWithProvider(login_screen.this, provider.build())
                            .addOnSuccessListener(
                                    new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            // User is re-authenticated with fresh tokens and
                                            // should be able to perform sensitive operations
                                            // like account deletion and email or password
                                            // update.
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle failure.
                                        }
                                    });

                }

            }
        });




        mGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  signIn();

            }
        });

        FacebookSdk.sdkInitialize(login_screen.this);
        mCallbackManager = CallbackManager.Factory.create();
        mFaceBookSignIn.setReadPermissions("email", "public_profile");
        mFaceBookSignIn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(login_screen.this, "facebook:onCancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(login_screen.this, "facebook:onError", Toast.LENGTH_LONG).show();
            }
        });
        mPasswordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password");
                passwordResetDialog.setView(resetMail);
                passwordResetDialog.setMessage(" \n Enter your Email ID to Receive Password Reset Email ");
                passwordResetDialog.setIcon(R.drawable.logo);

                 passwordResetDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {

                         String mail = mUserName.getText().toString();
                         mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void aVoid) {

                                 Toast.makeText(login_screen.this,"Reset Link send to your Mail",Toast.LENGTH_LONG).show();

                             }
                         }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 Toast.makeText(login_screen.this,"Error ! Reset Link is not Sent"+ e.getMessage(),Toast.LENGTH_LONG).show();
                             }
                         });

                     }
                 } );

                  passwordResetDialog.create().show();

            }
        });

    }





    private void updateUI(FirebaseUser fUser) {
         Intent intent = new Intent(login_screen.this , MainActivity.class);
         startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }

    }


    public void logIn(View view) {

        String email = mUserName.getText().toString().trim();
        String password = mPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(login_screen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });


        Intent intent = new Intent(login_screen.this, MainActivity.class);
        startActivity(intent);

    }

    public void sign_Up(View view) {

        Intent intent = new Intent(login_screen.this, signUp.class);
        startActivity(intent);


    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            Toast.makeText(login_screen.this, "signIn With Credential:success",Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(login_screen.this, "signIn With Credential:failure", Toast.LENGTH_LONG).show();

                            updateUI(null);
                        }


                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(login_screen.this,"Firebase Integrated Successfully ",Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(login_screen.this,"Error"+e.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(login_screen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



   
}
