package com.joker.fcapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joker.fcapp1.Model.Username;

public class StdFrontPageActivity extends AppCompatActivity {
    //a constant for detecting the login intent result
    Window window;
    private static final int RC_SIGN_IN = 234;
    private SignInButton signInButton;
    private Button defaultsigninbutton,signupbutton;
    String name;
    String email;
    String imgurl;
    String userKey;
    private TextView edtphone;
    //Tag for the logs optional
    private static final String TAG = "fcapp";
    //creating a GoogleSignInClient object
    GoogleSignInClient mGoogleSignInClient;
    private String domain;
    //And also a Firebase Auth object
    FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    final DatabaseReference dbRef = database.getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_std_front_page);
        window=this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.status_bar));
        //first we intialized the FirebaseAuth object
        mAuth = FirebaseAuth.getInstance();
        //Then we need a GoogleSignInOptions object
        //And we need to build it as below
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .setHostedDomain("student.tce.edu")
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //final DatabaseReference dbRef = database.getReference("Users");

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton=findViewById(R.id.sign_in_button);
        defaultsigninbutton=findViewById(R.id.button);
        signupbutton=findViewById(R.id.button2);
        defaultsigninbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StdFrontPageActivity.this,SignInActivity.class));

            }
        });
        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StdFrontPageActivity.this,SignUpActivity.class));
            }
        });
        //Now we will attach a click listener to the sign_in_button
        //and inside onClick() method we are calling the signIn() method that will open
        //google sign in intent
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //if the user is already signed in
        //we will close this activity
        //and take the user to profile activity
        if (mAuth.getCurrentUser() != null ) {
            finish();
            startActivity(new Intent(this, profileactivity.class));
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(StdFrontPageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
//    private void firebaseAuthWithGoogle(String idToken) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//
//                        // ...
//                    }
//                });
//    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            userKey = user.getUid();

                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(StdFrontPageActivity.this);
                            name=acct.getDisplayName();
                            email=acct.getEmail();
                            imgurl= String.valueOf(acct.getPhotoUrl());
                            String[] split=email.split("@");
                            final String username=split[0];
                            dbRef.child(userKey).child("Name").setValue(name);
                            dbRef.child(userKey).child("Email").setValue(email);
                            dbRef.child(userKey).child("ProfileUrl").setValue(imgurl);
                            Username u1=new Username();
                            u1.setUsername(username);
                            Toast.makeText(StdFrontPageActivity.this, "User Signed In", Toast.LENGTH_SHORT).show();
                            dbRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.child(userKey).child("Phonenumber").exists()){
                                        startActivity(new Intent(StdFrontPageActivity.this,Main2Activity.class));
                                        finish();
                                    }
                                    else{
                                        startActivity(new Intent(StdFrontPageActivity.this,g_s_mobileverification.class));
                                        finish();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(StdFrontPageActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }


    //this method is called on click
    private void signIn() {
        //getting the google signin intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(StdFrontPageActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
