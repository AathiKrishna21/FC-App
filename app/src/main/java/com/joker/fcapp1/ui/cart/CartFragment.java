package com.joker.fcapp1.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joker.fcapp1.Database.Database;
import com.joker.fcapp1.Main2Activity;
import com.joker.fcapp1.Model.Cart;
import com.joker.fcapp1.Model.Order;
import com.joker.fcapp1.R;
import com.joker.fcapp1.ViewHolder.CartAdapter;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private CartViewModel cartViewModel;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference dRef,profileRef;

    TextView total_cost;
    Button orderbtn;

    List<Cart> cart = new ArrayList<>();
    CartAdapter adapter;
    String userKey,uname,phnno;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cartViewModel =
                ViewModelProviders.of(this).get(CartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        //final TextView textView = root.findViewById(R.id.text_dashboard);

        database = FirebaseDatabase.getInstance();
        dRef = database.getReference("Orders");
        profileRef=database.getReference("Users");

        //Init
        recyclerView = root.findViewById(R.id.cartrecyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        total_cost=root.findViewById(R.id.amount);
        orderbtn = root.findViewById(R.id.button3);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userKey = user.getUid();

        profileRef.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uname=snapshot.child("Name").getValue().toString();
                phnno=snapshot.child("Phonenumber").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        orderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(Cart cart : cart)
                    cart.setProductId("0"+cart.getProductId());
                Order order = new Order(uname,phnno,total_cost.getText().toString(),userKey,cart);
                dRef.child(String.valueOf(System.currentTimeMillis())).setValue(order);
                new Database(getContext()).cleanCart();
                Toast.makeText(CartFragment.this.getContext(),"Order Placed.",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CartFragment.this.getActivity(), Main2Activity.class));
            }
        });

        loadCart();

        cartViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }

    private void loadCart() {
        cart = new Database(this.getContext()).getCarts();
        adapter = new CartAdapter(cart,this.getContext());
        recyclerView.setAdapter(adapter);

        //Calculate full total price
        int total=0 ;
        for(Cart cart : this.cart)
            total+=(Integer.parseInt(cart.getPrice()));
        total_cost.setText(String.valueOf(total));
    }
}