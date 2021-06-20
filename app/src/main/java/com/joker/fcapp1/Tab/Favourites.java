package com.joker.fcapp1.Tab;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
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
import com.joker.fcapp1.Database.Database;
import com.joker.fcapp1.Details;
import com.joker.fcapp1.Main2Activity;
import com.joker.fcapp1.Model.APIModel;
import com.joker.fcapp1.Model.Cart;
import com.joker.fcapp1.Model.Notification;
import com.joker.fcapp1.Model.Order;
import com.joker.fcapp1.Model.Payment;
import com.joker.fcapp1.Model.Response;
import com.joker.fcapp1.Model.Sender;
import com.joker.fcapp1.Model.Token;
import com.joker.fcapp1.R;
import com.joker.fcapp1.Remote.APIService;
import com.joker.fcapp1.ViewHolder.FavoriteViewHolder;
import com.joker.fcapp1.ViewHolder.PastOrderViewHolder;
import com.joker.fcapp1.ui.cart.CartFragment;
import com.joker.fcapp1.ui.orders.OrdersViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class Favourites extends Fragment {
    private OrdersViewModel ordersViewModel;
    RecyclerView recyclerView;
    ImageView bg;
    TextView tx;
    String id,orderid;
    FirebaseDatabase database;
    DatabaseReference dRef,profileRef,dbRef;
    List<Order> order = new ArrayList<>();
    FirebaseRecyclerAdapter<Order, FavoriteViewHolder> adapter;
    String userKey,date,time,time2,item="";
    Order od;
    List<Cart> cart;
    RadioButton op,cash;
    RadioGroup radioGroup;
    TextView total_cost;
    int cost,tax;
    TextView a1,a2,a3;
    RelativeLayout relativeLayout,amt;
    Order forder;
    Boolean alerter=false;
    APIService mService;
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
        mService= APIModel.getFCMService();
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
                        if(c==cart.get(cart.size()-1)){
                            item+=c.getProductName()+" x "+c.getQuantity();
                        }
                        else {
                            item+=c.getProductName()+" x "+c.getQuantity()+" , ";
                        }
                    }
                    id = adapter.getRef(position).getKey();
                    Date currentTime = Calendar.getInstance().getTime();
                    String datetime=currentTime.toString();
                    String[] words=datetime.split("\\s");
                    date=words[1]+" "+words[2]+" "+words[5];
                    time=words[3];
                    time2=time.substring(0,5);
//                    holder.orderid.setText("#" + id);
                    holder.cost.setText("₹" + model.getTotalcost());
                    holder.items.setText("x" + model.getitems() + " Items");
                    holder.item.setText(item);
                    holder.shopname.setText(convertCodetoShop(model.getShopId()));
                    holder.tb.setChecked(true);
                    holder.tb.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_heartfill));
                    holder.tb.setOnCheckedChangeListener(new Tlistener(holder,id));
                    holder.receipt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final AlertDialog.Builder alertDialog=new AlertDialog.Builder(getActivity());
                            alertDialog.setTitle("Payment");
                            alertDialog.setMessage("Choose your payment method");

                            LayoutInflater inflater = getActivity().getLayoutInflater();
                            final View view1=inflater.inflate(R.layout.payment_selector,null);
                            amt=view1.findViewById(R.id.amount);
                            a1=view1.findViewById(R.id.a1);
                            a2=view1.findViewById(R.id.a2);
                            a3=view1.findViewById(R.id.a3);
                            op=view1.findViewById(R.id.radioMale);
                            cash=view1.findViewById(R.id.radioFemale);
                            radioGroup=view1.findViewById(R.id.radioGroup);

                            amt.setVisibility(View.GONE);
                            alertDialog.setView(view1);
                            alertDialog.setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    if(op.isChecked()){

                                        String samount=a3.getText().toString();
                                        int amount =Math.round(Float.parseFloat(samount)*100);
                                        forder= new Order("","",model.getShopId(),String.valueOf(amount),"0",userKey,cart,false);
                                        orderid=String.valueOf(System.currentTimeMillis());
                                        Payment.setOrderId(orderid);
                                        Payment.setOrder(forder);
                                        Payment.setIsCart(false);
                                        ((Main2Activity)getActivity()).razorpay();
                                    }
                                    if(cash.isChecked()){
                                        Date currentTime = Calendar.getInstance().getTime();
                                        String datetime=currentTime.toString();
                                        String[] words=datetime.split("\\s");
                                        date=words[1]+" "+words[2]+" "+words[5];
                                        time=words[3];
                                        time2=time.substring(0,5);
                                        forder = new Order(date,time,model.getShopId(),model.getTotalcost(),"0",userKey,cart,false);
                                        orderid=String.valueOf(System.currentTimeMillis());
                                        Payment.setOrder(forder);
                                        Payment.setOrderId(orderid);
                                        Payment.isCart=true;
                                        onCompletedPayment();
                                    }

                                }
                            });
                            alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                            final AlertDialog dialog = alertDialog.create();
                            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                            {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    // checkedId is the RadioButton selected
                                    RadioButton rb=(RadioButton)view1.findViewById(checkedId);
                                    if(op.isChecked()){
                                        amt.setVisibility(View.VISIBLE);
                                        cost=Integer.parseInt(model.getTotalcost());
                                        tax=(int)Math.ceil(cost*0.0236);
                                        a1.setText(String.valueOf(cost));
                                        a2.setText(String.valueOf((tax)));
                                        a3.setText(String.valueOf(cost+tax));
                                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                                    }
                                    else{
                                        amt.setVisibility(View.GONE);
                                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                                    }
//                        rb.setText("hi " + String.valueOf(checkedId));
                                }
                            });



                            dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface arg0) {
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#FF7104"));
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FF7104"));
                                }
                            });
                            dialog.setCancelable(false);
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
    class Tlistener implements CompoundButton.OnCheckedChangeListener{
        FavoriteViewHolder holder;
        String id;
        public Tlistener(FavoriteViewHolder holder,String id){
            this.holder=holder;
            this.id=id;
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                holder.tb.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_heartfill));
            }
            else{
                holder.tb.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_heart));
                dRef.child(id).child("favorite").setValue(false);
            }
        }
    }
    public void onCompletedPayment(){
        Date currentTime = Calendar.getInstance().getTime();
        String datetime=currentTime.toString();
        String[] words=datetime.split("\\s");
        date=words[1]+" "+words[2]+" "+words[5];
        time=words[3];
        time2=time.substring(0,5);
        Payment.order.setDate(date);
        Payment.order.setTime(time);
//        order.setDate(date);
//        order.setTime(time);
        dbRef.child(Payment.getOrderId()).setValue(Payment.getOrder());
        if(Payment.isIsCart())
            new Database(getContext()).cleanCart();
        alerter=true;
        sendNotificationOrder(Payment.getOrderId(),Payment.getOrder());
    }
    private void sendNotificationOrder(final String orderid,final Order order) {
        DatabaseReference tokenref=database.getReference("Tokens");
        tokenref.orderByChild("serverToken").equalTo(order.getShopId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot postSnapshot:snapshot.getChildren()){
                            Token serverToken=postSnapshot.getValue(Token.class);
                            Notification notification=new Notification("FC App","You have a new order "+orderid);
                            Sender content=new Sender(serverToken.getToken(),notification);
                            mService.sendNotification(content)
                                    .enqueue(new Callback<Response>() {
                                        @Override
                                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                            if(response.code()==200) {
                                                if (response.body().success == 1) {
                                                    Intent intent = new Intent(getParentFragment().getActivity(), Main2Activity.class);
                                                    intent.putExtra("Alerter", alerter);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Response> call, Throwable t) {
                                            Log.e("Error",t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}