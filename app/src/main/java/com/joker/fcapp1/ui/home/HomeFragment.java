package com.joker.fcapp1.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.joker.fcapp1.Menu_trial;
import com.joker.fcapp1.Model.Internet;
import com.joker.fcapp1.Model.Token;
import com.joker.fcapp1.R;
import com.joker.fcapp1.ui.cart.CartFragment;
import com.joker.fcapp1.ui.orders.OrdersFragment;
import com.tapadoo.alerter.Alerter;

public class HomeFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference category;
    String key;
    LottieAnimationView view;
    ImageView fc;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        Intent intent=getActivity().getIntent();
        Bundle extras=intent.getExtras();
        String flag=intent.getStringExtra("uid");
        fc=root.findViewById(R.id.imageView7);

        if(flag!=null){
            intent.removeExtra("uid");
            NavController navController = NavHostFragment.findNavController(HomeFragment.this);
            navController.navigate(
                    R.id.action_fragment1_to_fragment3,
                    null,
                    new NavOptions.Builder()
                            .build()
            );
        }
        String isCart=intent.getStringExtra("isCart");
        if(isCart!=null){
            intent.removeExtra("isCart");
            NavController navController = NavHostFragment.findNavController(HomeFragment.this);
            navController.navigate(
                    R.id.action_fragment1_to_fragment2,
                    null,
                    new NavOptions.Builder()
                            .build()
            );
        }
        if(extras!=null){
            if(extras.containsKey("Alerter")){
                boolean code = extras.getBoolean("Alerter",false);
                if(code){

                    Alerter.create(getActivity())
                            .setTitle("Order Placed!")
                            .setText("Click to see Order Status")
                            .setIcon(R.drawable.ic_order)
                            .setBackgroundColorRes(R.color.orange)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Alerter.hide();
                                    NavController navController = NavHostFragment.findNavController(HomeFragment.this);
                                    navController.navigate(
                                            R.id.action_fragment1_to_fragment3,
                                            null,
                                            new NavOptions.Builder()
                                                    .build()
                                    );
                                }
                            })
                            .enableSwipeToDismiss()
                            .show();
                    intent.removeExtra("Alerter");
            }
//                else if(extras.containsKey("isCart")){
//
//                }
        }


        }
        database=FirebaseDatabase.getInstance();
        category=database.getReference("Shops");
        final String s1="Lakshmi Bhavan";
        final CardView c1 = root.findViewById(R.id.cardview1);
        final CardView c2 = root.findViewById(R.id.cardview2);
        final CardView c3 = root.findViewById(R.id.cardview3);
        final CardView c4 = root.findViewById(R.id.cardview4);
        view=root.findViewById(R.id.animation_view);
        if(Internet.isConnectedToInternet(getContext())){
            view.setVisibility(View.GONE);
        }
        else{
            c1.setVisibility(View.GONE);
            c2.setVisibility(View.GONE);
            c3.setVisibility(View.GONE);
            c4.setVisibility(View.GONE);
            fc.setVisibility(View.GONE);
        }
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                c1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeFragment.this.getActivity(), Menu_trial.class);
                        intent.putExtra("ShopId", "01");
                        startActivity(intent);

                    }
                });
                c2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeFragment.this.getActivity(), Menu_trial.class);
                        intent.putExtra("ShopId", "02");
                        startActivity(intent);

                    }
                });
                c3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeFragment.this.getActivity(), Menu_trial.class);
                        intent.putExtra("ShopId", "03");
                        startActivity(intent);

                    }
                });
                c4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeFragment.this.getActivity(), Menu_trial.class);
                        intent.putExtra("ShopId", "04");
                        startActivity(intent);

                    }
                });
            }
        });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        String token=task.getResult();
                        updateTokenToFirebase(token);
                    }
                });
        return root;
    }

    private void updateTokenToFirebase(String token) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokenref=db.getReference("Tokens");
        Token tokenobj= new Token(token,"false");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = user.getUid();
        tokenref.child(userKey).setValue(tokenobj);
    }
}