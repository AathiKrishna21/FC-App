package com.joker.fcapp1.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joker.fcapp1.Main2Activity;
import com.joker.fcapp1.MainActivity;
import com.joker.fcapp1.Model.Username;
import com.joker.fcapp1.R;
import com.joker.fcapp1.StdFrontPageActivity;
import com.joker.fcapp1.mobileverification;
import com.joker.fcapp1.profileactivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ProfileFragment extends Fragment{

    private ProfileViewModel profileViewModel;
    private TextView profileName, profileEmail,profilephnno;
    private ImageView profileImage;
    public static String uname,email,phnno;
    static String imgurl,userKey;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("Users");
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        final TextView textView = root.findViewById(R.id.textView4);
        final Button signoutbtn=root.findViewById(R.id.button);
        //final TextView cpass = root.findViewById(R.id.cpass);
        profileImage= root.findViewById(R.id.imageView13);
        profileName=root.findViewById(R.id.textView3);
        profileEmail=root.findViewById(R.id.textView5);
        profilephnno=root.findViewById(R.id.textView6);

        /*
        cpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileFragment.this.getActivity(), mobileverification.class));
            }
        });*/

        profileViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
                Username n=new Username();
                String name=n.getUsername();

                signoutbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(ProfileFragment.this.getActivity(),MainActivity.class));
                    }
                });

            }

        });
        return root;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userKey = user.getUid();
        dbRef.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uname = dataSnapshot.child("Name").getValue(String.class);
                email = dataSnapshot.child("Email").getValue(String.class);
                imgurl = dataSnapshot.child("ProfileUrl").getValue(String.class);
                phnno= dataSnapshot.child("Phonenumber").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        profileName.setText(uname);
        profileEmail.setText(email);
        profilephnno.setText(phnno);
        Picasso.get().load(imgurl).placeholder(R.drawable.profile).into(profileImage);
    }
}
