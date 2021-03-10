package com.joker.fcapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joker.fcapp1.Model.Username;
import com.squareup.picasso.Picasso;

public class profileactivity extends AppCompatActivity {
    public static final String GOOGLE_ACCOUNT = "google_account";
    private TextView profileName, profileEmail;
    private String hi,h;
    private ImageView profileImage;
    public Button signOut;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference dbRef = database.getReference("Users");
    private void findviews(){
        signOut=findViewById(R.id.sign_out);
        //profileName=findViewById(R.id.uname);
        //profileEmail=findViewById(R.id.mail);
    }
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(profileactivity.this,MainActivity.class));
                finish();
            }
        });
    }
    private void setDataOnView() {
        GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);
        Picasso.get().load(googleSignInAccount.getPhotoUrl()).centerInside().fit().into(profileImage);
        Username n=new Username();
        String name=n.getUsername();
        userKey = user.getUid();
        dbRef.child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String h = dataSnapshot.child("Name").getValue(String.class);
                String hi = dataSnapshot.child("Email").getValue(String.class);
//                Log.d(TAG, "Name: " + h);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        profileName.setText(h);
        profileEmail.setText(hi);

    }

    @Override
    public void onBackPressed() {

    }
}