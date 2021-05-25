package com.joker.fcapp1.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
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
import com.joker.fcapp1.Model.Flag;
import com.joker.fcapp1.Model.User;
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
    Button signoutbtn;
    public static String uname,email,phnno;
    static String imgurl,userKey;
    SpinKitView waveview;
    CardView mcardView,scardView;
    Flag flag;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("Users");
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        signoutbtn=root.findViewById(R.id.button);
        //final TextView cpass = root.findViewById(R.id.cpass);
        profileImage= root.findViewById(R.id.imageView13);
        profileName=root.findViewById(R.id.textView3);
        profileEmail=root.findViewById(R.id.textView5);
        profilephnno=root.findViewById(R.id.textView6);
        mcardView=root.findViewById(R.id.cardView5);
        mcardView.setVisibility(View.GONE);
        scardView=root.findViewById(R.id.summacard);
        scardView.setVisibility(View.VISIBLE);
//        profileEmail.setVisibility(View.GONE);
//        profileName.setVisibility(View.GONE);
//        profilephnno.setVisibility(View.GONE);
//        signoutbtn.setVisibility(View.GONE);
        waveview = root.findViewById(R.id.spin_kit);
        Sprite wave = new Wave();
        waveview.setIndeterminateDrawable(wave);
        if(Flag.getFlag()==1){
            scardView.setVisibility(View.GONE);
            mcardView.setVisibility(View.VISIBLE);
        }
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
                waveview.setVisibility(View.VISIBLE);
                User user = dataSnapshot.getValue(User.class);
                profileName.setText(user.getName());
                profileEmail.setText(user.getEmail());
                profilephnno.setText(user.getPhonenumber());
                Picasso.get().load(user.getProfileurl()).into(profileImage);
//                cardView.setVisibility(View.VISIBLE);
//                profileEmail.setVisibility(View.VISIBLE);
//                profileName.setVisibility(View.VISIBLE);
//                profilephnno.setVisibility(View.VISIBLE);
//                signoutbtn.setVisibility(View.VISIBLE);

                if(Flag.getFlag()==0) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scardView.setVisibility(View.GONE);
                            mcardView.setVisibility(View.VISIBLE);
                        }
                    }, 1400);
                    Flag.setFlag(1);

//                waveview.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}