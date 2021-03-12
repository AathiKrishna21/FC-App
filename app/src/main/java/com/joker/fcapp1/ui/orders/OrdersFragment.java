package com.joker.fcapp1.ui.orders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joker.fcapp1.Database.Database;
import com.joker.fcapp1.Model.Cart;
import com.joker.fcapp1.Model.Order;
import com.joker.fcapp1.R;
import com.joker.fcapp1.ViewHolder.CartAdapter;
import com.joker.fcapp1.ViewHolder.OrderViewHolder;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private OrdersViewModel ordersViewModel;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    TextView tx;
    FirebaseDatabase database;
    DatabaseReference dRef,profileRef;
    List<Order> order = new ArrayList<>();
    FirebaseRecyclerAdapter <Order, OrderViewHolder> adapter;
    String userKey,phnno;
    Order od;
    List<Cart> cart;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ordersViewModel =
                ViewModelProviders.of(this).get(OrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_orders, container, false);
        database = FirebaseDatabase.getInstance();
//        tx = root.findViewById(R.id.sample);
        dRef = database.getReference("Orders");
        recyclerView = root.findViewById(R.id.ordesrrecyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userKey = user.getUid();

//        dRef.child(userKey).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                od= snapshot.child("foods").getValue(Order.class);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        Log.d("myTag", od.toString());
//          cart=od.getFoods();
//        phnno=od.getName();
//        tx.setText(phnno);

        loadOrder(userKey);
//        final TextView textView = root.findViewById(R.id.text_notifications);
//        ordersViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }
    private void loadOrder(String uerkey) {
        FirebaseRecyclerOptions<Order> options=new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(dRef.orderByChild("uid").equalTo(userKey),Order.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Order, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Order model) {
                holder.orderId.setText(adapter.getRef(position).getKey());
                holder.name.setText(model.getShopId());
//                holder.items.setText();
                holder.status.setText(model.getStatus());
                holder.total_cost.setText(model.getTotalcost());
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_items,parent,false);
                return new OrderViewHolder(view);
//                return null;
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}