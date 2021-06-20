package com.joker.fcapp1.Tab;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.joker.fcapp1.Details;
import com.joker.fcapp1.Model.Cart;
import com.joker.fcapp1.Model.Order;
import com.joker.fcapp1.R;
import com.joker.fcapp1.ViewHolder.OrderViewHolder;
import com.joker.fcapp1.ViewHolder.PastOrderViewHolder;
import com.joker.fcapp1.ui.orders.OrdersViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PastOrders extends Fragment {
    private OrdersViewModel ordersViewModel;
    RecyclerView recyclerView;
    ImageView bg;
    TextView tx;
    public static String id;
    FirebaseDatabase database;
    DatabaseReference dRef,profileRef;
    List<Order> order = new ArrayList<>();
    FirebaseRecyclerAdapter<Order, PastOrderViewHolder> adapter;
    String userKey,date;
    Order od;
    List<Cart> cart;
    Button showmore;
    View kodu;
    public static int limit=1;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ordersViewModel =
                ViewModelProviders.of(this).get(OrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_pastorders, container, false);
        database = FirebaseDatabase.getInstance();
//        tx = root.findViewById(R.id.sample);
        dRef = database.getReference("PastOrders");
        recyclerView = root.findViewById(R.id.pastordesrrecyclerview);
        bg=root.findViewById(R.id.bg_img);
        showmore=root.findViewById(R.id.showmore);
        kodu=root.findViewById(R.id.view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userKey = user.getUid();

        loadOrder();

//        final TextView textView = root.findViewById(R.id.text_notifications);
//        ordersViewModel.getText().observe(this, new Observer<String>() {
        return root;
    }
    private void loadOrder() {

        FirebaseRecyclerOptions<Order> options=new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(dRef.orderByChild("uid").equalTo(userKey),Order.class)
                .build();


        adapter=new FirebaseRecyclerAdapter<Order, PastOrderViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull PastOrderViewHolder holder, int position, @NonNull Order model) {
                if(getItemCount()==0){
                    bg.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    showmore.setVisibility(View.GONE);
                    kodu.setVisibility(View.GONE);
                }
                else{
                    bg.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    if(getItemCount()-1<limit) {
                        kodu.setVisibility(View.GONE);
                        showmore.setVisibility(View.GONE);
                    }
                    else {
                        showmore.setVisibility(View.VISIBLE);
                        kodu.setVisibility(View.VISIBLE);
                    }
                }
//                Query queries =dRef.orderByChild("uid").equalTo(userKey);
//                queries.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(snapshot.exists()){
//                            bg.setVisibility(View.GONE);
//                            recyclerView.setVisibility(View.VISIBLE);
//                            if(getItemCount()-1<limit) {
//                                kodu.setVisibility(View.GONE);
//                                showmore.setVisibility(View.GONE);
//                            }
//                            else {
//                                showmore.setVisibility(View.VISIBLE);
//                                kodu.setVisibility(View.VISIBLE);
//                            }
//                        }
//                        else{
//                            bg.setVisibility(View.VISIBLE);
//                            recyclerView.setVisibility(View.GONE);
//                            showmore.setVisibility(View.GONE);
//                            kodu.setVisibility(View.GONE);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
                id=adapter.getRef(position).getKey();
                Date currentTime = Calendar.getInstance().getTime();
                String datetime=currentTime.toString();
                String[] words=datetime.split("\\s");
                date=words[1]+" "+words[2]+" "+words[5];
//                id="#"+id;
                if(!(position>=getItemCount()-limit)) {
                        holder.itemView.setVisibility(View.GONE);
                        holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
//                else{
//                    holder.itemView.setVisibility(View.GONE);
//                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
//                }
                holder.orderid.setText("#"+id);
                holder.cost.setText("₹"+model.getTotalcost());
                if(model.getDate().equals(date)){
                    holder.date.setText("Today");
                }
                else {
                    holder.date.setText(model.getDate());
                }
                holder.items.setText("x"+model.getitems()+" Items");
                holder.shopname.setText(convertCodetoShop(model.getShopId()));
//                public class ReceiptOnClickListener implements View.OnClickListener
//                {
//
//                    int id;
//                    public ReceiptOnClickListener(int id) {
//                        this.id = id;
//                    }
//
//                    @Override
//                    public void onClick(View v)
//                        Intent intent = new Intent(getActivity(),Details.class);
//                        intent.putExtra("id",adapter.getRef(position).getKey());
//                        startActivity(intent);
//                        //read your lovely variable
//                    }

//                };
//                ReceiptOnClickListener t=new ReceiptOnClickListener(id);
                holder.receipt.setOnClickListener(new ReceiptOnClickListener(id,getActivity())

//                    @Override
//                    public void onClick(View view) {
//
//                        Intent intent = new Intent(getActivity(),Details.class);
//                        intent.putExtra("id",id);
//                        startActivity(intent);
////                        finish();
//                    }
                );

            }

            @NonNull
            @Override
            public PastOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_order_items,parent,false);
                return new PastOrderViewHolder(view);
            }

        };
        showmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limit=limit+1;
                loadOrder();
            }
        });
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
    public class ReceiptOnClickListener implements View.OnClickListener
    {

        String id;
        Context context;
        public ReceiptOnClickListener(String id,Context context) {
            this.id = id;
            this.context=context;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, Details.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
            //read your lovely variable
        }
}