package com.joker.fcapp1.Tab;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.joker.fcapp1.Model.Cart;
import com.joker.fcapp1.Model.Order;
import com.joker.fcapp1.R;
import com.joker.fcapp1.ViewHolder.OrderViewHolder;
import com.joker.fcapp1.ViewHolder.PastOrderViewHolder;
import com.joker.fcapp1.ui.orders.OrdersViewModel;

import java.util.ArrayList;
import java.util.List;

public class PastOrders extends Fragment {
    private OrdersViewModel ordersViewModel;
    RecyclerView recyclerView;

    TextView tx;
    FirebaseDatabase database;
    DatabaseReference dRef,profileRef;
    List<Order> order = new ArrayList<>();
    FirebaseRecyclerAdapter<Order, PastOrderViewHolder> adapter;
    String userKey,phnno;
    Order od;
    List<Cart> cart;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ordersViewModel =
                ViewModelProviders.of(this).get(OrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_pastorders, container, false);
        database = FirebaseDatabase.getInstance();
//        tx = root.findViewById(R.id.sample);
        dRef = database.getReference("PastOrders");
        recyclerView = root.findViewById(R.id.pastordesrrecyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
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
        return root;
    }
    private void loadOrder(String uerkey) {
        FirebaseRecyclerOptions<Order> options=new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(dRef.orderByChild("uid").equalTo(userKey),Order.class)
                .build();


        adapter=new FirebaseRecyclerAdapter<Order, PastOrderViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull PastOrderViewHolder holder, int position, @NonNull Order model) {
                String id=adapter.getRef(position).getKey();
                id="#"+id;
                holder.orderid.setText(id);
                holder.cost.setText("₹"+model.getTotalcost());
                holder.date.setText(model.getDate());
                holder.items.setText("x"+model.getitems()+" Items");
                holder.shopname.setText(convertCodetoShop(model.getShopId()));

            }

            @NonNull
            @Override
            public PastOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_order_items,parent,false);
                return new PastOrderViewHolder(view);
            }

        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private String convertCodetoShop(String menuId) {
        if(menuId.equals("01"))
            return "Lakshmi Bhavan";
        else if(menuId.equals("02"))
            return "Idly Italy";
        else if(menuId.equals("03"))
            return "Chat Shop";
        else if(menuId.equals("04"))
            return "Juice Shop";
        return "";
    }
}