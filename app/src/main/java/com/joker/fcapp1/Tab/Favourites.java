package com.joker.fcapp1.Tab;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.joker.fcapp1.ViewHolder.FavoriteViewHolder;
import com.joker.fcapp1.ViewHolder.PastOrderViewHolder;
import com.joker.fcapp1.ui.orders.OrdersViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Favourites extends Fragment {
    private OrdersViewModel ordersViewModel;
    RecyclerView recyclerView;
    ImageView bg;
    TextView tx;
    String id;
    FirebaseDatabase database;
    DatabaseReference dRef,profileRef,dbRef;
    List<Order> order = new ArrayList<>();
    FirebaseRecyclerAdapter<Order, FavoriteViewHolder> adapter;
    String userKey,date,time,time2,item="";
    Order od;
    List<Cart> cart;
    public Favourites(){

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ordersViewModel =
                ViewModelProviders.of(this).get(OrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_favourites, container, false);
        database = FirebaseDatabase.getInstance();
//        tx = root.findViewById(R.id.sample);
        dRef = database.getReference("PastOrders");
        dbRef=database.getReference("Orders");
        recyclerView = root.findViewById(R.id.pastordesrrecyclerview);
        bg=root.findViewById(R.id.bg_img);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userKey = user.getUid();
        loadOrder(userKey);
//        return inflater.inflate(R.layout.fragment_favourites, container, false);
        return  root;
    }
    private void loadOrder(String uerkey) {

        FirebaseRecyclerOptions<Order> options=new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(dRef.orderByChild("uid").equalTo(userKey),Order.class)
                .build();


        adapter=new FirebaseRecyclerAdapter<Order, FavoriteViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position, @NonNull final Order model) {
                if(model.isFavorite()) {
                    Query queries = dRef.orderByChild("uid").equalTo(userKey);
                    queries.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                bg.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            } else {
                                bg.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    cart=model.getFoods();
                    for(Cart c: cart){
                        item+=c.getProductName()+" x "+c.getQuantity()+" , ";
                    }
                    id = adapter.getRef(position).getKey();
                    Date currentTime = Calendar.getInstance().getTime();
                    String datetime=currentTime.toString();
                    String[] words=datetime.split("\\s");
                    date=words[1]+" "+words[2]+" "+words[5];
                    time=words[3];
                    time2=time.substring(0,5);
                    holder.orderid.setText("#" + id);
                    holder.cost.setText("₹" + model.getTotalcost());
                    holder.items.setText("x" + model.getitems() + " Items");
                    holder.item.setText(item);
                    holder.shopname.setText(convertCodetoShop(model.getShopId()));
                    holder.receipt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            // Set a title for alert dialog
                            builder.setTitle("Re-Order");
                            // Ask the final question
                            builder.setMessage("Are you sure to place the same order again?");
                            // Set the alert dialog yes button click listener
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Order order = new Order(date,time,model.getShopId(),model.getTotalcost(),"0",model.getUId(),model.getFoods(),false);
                                    dbRef.child(String.valueOf(System.currentTimeMillis())).setValue(order);
                                }
                            });
                            // Set the alert dialog no button click listener
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do something when No button clicked
//                                    Toast.makeText(getApplicationContext(),
//                                            "No Button Clicked",Toast.LENGTH_SHORT).show();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            // Display the alert dialog on interface
                            dialog.show();
                        }
                    });
                    item="";
                }
                else{
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
            }

            @NonNull
            @Override
            public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_items,parent,false);
                return new FavoriteViewHolder(view);
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
    private void moveRecord(final DatabaseReference fromPath, final DatabaseReference toPath) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
//                            fromPath.removeValue();
                        } else {
//                                                Log.d(TAG, "Copy failed!");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        fromPath.addListenerForSingleValueEvent(valueEventListener);
    }
}
