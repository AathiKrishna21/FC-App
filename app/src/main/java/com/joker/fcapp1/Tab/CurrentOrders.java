package com.joker.fcapp1.Tab;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.joker.fcapp1.Model.Cart;
import com.joker.fcapp1.Model.Order;
import com.joker.fcapp1.R;
import com.joker.fcapp1.ViewHolder.OrderViewHolder;
import com.joker.fcapp1.ui.orders.OrdersViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CurrentOrders extends Fragment {
    private OrdersViewModel ordersViewModel;
    RecyclerView recyclerView;
    ImageView bg;
    RecyclerView.LayoutManager layoutManager;
    TextView tx;
    FirebaseDatabase database;
    DatabaseReference dRef,Ref;
    List<Order> order = new ArrayList<>();
    FirebaseRecyclerAdapter<Order, OrderViewHolder> adapter;
    String userKey,phnno;
    Order od;
    List<Cart> cart;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ordersViewModel =
                ViewModelProviders.of(this).get(OrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_currentorders, container, false);
        database = FirebaseDatabase.getInstance();
//        tx = root.findViewById(R.id.sample);
        dRef = database.getReference("Orders");
        Ref = database.getReference("Shops");
        recyclerView = root.findViewById(R.id.ordesrrecyclerview);
        bg=root.findViewById(R.id.bg_img);
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
        return root;
    }
    private void loadOrder(String uerkey) {

        FirebaseRecyclerOptions<Order> options=new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(dRef.orderByChild("uid").equalTo(userKey),Order.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Order, OrderViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final OrderViewHolder holder, int position, @NonNull final Order model) {
                Query queries =dRef.orderByChild("uid").equalTo(userKey);
                queries.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            bg.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        else{
                            bg.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                String orderid;
                orderid=adapter.getRef(position).getKey();
                orderid='#'+orderid;
                holder.orderId.setText(orderid);
                holder.status.setText(convertCodetoStatus(model.getStatus()));
                holder.time.setText("Today, "+model.getTime());
                final String menuId=model.getShopId();

                Ref.child(menuId).child("Image").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String url=snapshot.getValue(String.class);
                        Picasso.get().load(url).into(holder.image);
                        holder.name.setText(convertCodetoShop(menuId));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//                holder.items.setText();
//                holder.status.setText(model.getStatus());
//                holder.total_cost.setText(model.getTotalcost());

            }
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_items,parent,false);
                return new OrderViewHolder(view);
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

    private String convertCodetoStatus(String status) {
        if(status.equals("0")){
            return "Order Placed";
        }
        else if(status.equals("1")){
            return "Preparing Food";
        }
        else if(status.equals("2")){
            return "Ready To Be Collected";
        }
        return "";
    }
}