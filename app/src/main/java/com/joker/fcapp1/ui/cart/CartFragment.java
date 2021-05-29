package com.joker.fcapp1.ui.cart;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.joker.fcapp1.Database.Database;
import com.joker.fcapp1.Main2Activity;
import com.joker.fcapp1.Model.APIModel;
import com.joker.fcapp1.Model.Cart;
import com.joker.fcapp1.Model.Notification;
import com.joker.fcapp1.Model.Order;
import com.joker.fcapp1.Model.Response;
import com.joker.fcapp1.Model.Sender;
import com.joker.fcapp1.Model.Token;
import com.joker.fcapp1.R;
import com.joker.fcapp1.Remote.APIService;
import com.joker.fcapp1.ViewHolder.CartAdapter;
import com.joker.fcapp1.ui.orders.OrdersFragment;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class CartFragment extends Fragment implements PaymentResultListener {

    private CartViewModel cartViewModel;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RelativeLayout relativeLayout;
    FirebaseDatabase database;
    DatabaseReference dRef,FoodRef,profiledRef;
    Boolean alerter=false;
    public TextView total_cost;
    Button orderbtn;
    ImageView bg;
    Cart cart1;
    String date,time,time2,orderid;
    List<Cart> cart = new ArrayList<>();
    CartAdapter adapter;
    String userKey,ShopId,FoodId,name,phnno;
    APIService mService;
    Order order;
    private static CartFragment instance;
    FirebaseAuth mAuth;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cartViewModel =
                ViewModelProviders.of(this).get(CartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        //final TextView textView = root.findViewById(R.id.text_dashboard);
        instance=this;
        mService= APIModel.getFCMService();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        dRef = database.getReference("Orders");
        FoodRef=database.getReference("Foods");
        profiledRef=database.getReference("Users");
        relativeLayout=root.findViewById(R.id.cart_relative);
        //Init
        bg=root.findViewById(R.id.bg_img);
        recyclerView = root.findViewById(R.id.cartrecyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        total_cost=root.findViewById(R.id.amount);
        orderbtn = root.findViewById(R.id.button3);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userKey = user.getUid();



        orderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date currentTime = Calendar.getInstance().getTime();
                String datetime=currentTime.toString();
                String[] words=datetime.split("\\s");
                date=words[1]+" "+words[2]+" "+words[5];
                time=words[3];
                time2=time.substring(0,5);
                order = new Order(date,time,ShopId,total_cost.getText().toString(),"0",userKey,cart,false);
                orderid=String.valueOf(System.currentTimeMillis());
                FirebaseUser user=mAuth.getCurrentUser();
                String samount=total_cost.getText().toString();
                int amount =Math.round(Float.parseFloat(samount)*100);
                Checkout checkout=new Checkout();
                checkout.setKeyID("rzp_test_v9Eu6C6hCOiCvb");
                checkout.setImage(R.drawable.fc_logo);
                JSONObject object=new JSONObject();
                try {
                    // to put name
                    object.put("name", convertCodetoShop(order.getShopId()));

                    // put description
                    object.put("description", orderid);

                    // to set theme color
                    object.put("theme.color", "#FF7104");

                    // put the currency
                    object.put("currency", "INR");

                    // put amount
                    object.put("amount", amount);

                    // put mobile number
                    object.put("prefill.contact", user.getPhoneNumber());

                    // put email
                    object.put("prefill.email", user.getEmail());

                    // open razorpay to checkout activity
                    checkout.open(getActivity(), object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                dRef.child(orderid).setValue(order);
//                new Database(getContext()).cleanCart();
//                alerter=true;
//                sendNotificationOrder(orderid,order);

                /*Alerter.create(getActivity())
                        .setTitle("Order Placed!")
                        .setText("Click to see Order Status")
                        .setIcon(R.drawable.ic_order)
                        .setDuration(3000)
                        .setBackgroundColorRes(R.color.status_bar)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(CartFragment.this.getActivity(), OrdersFragment.class));
                            }
                        })
                        .enableSwipeToDismiss()
                        .show();*/
            }
        });

        loadCart();

        if(!cart.isEmpty()) {
            cart1=cart.get(0);
            FoodId = cart1.getProductId();
            FoodRef.child(FoodId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ShopId = snapshot.child("MenuId").getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            profiledRef.child(userKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    name = snapshot.child("Name").getValue().toString();
                    phnno = snapshot.child("Phonenumber").getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        cartViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }
    @Override
    public void onPaymentSuccess(String s) {
        // this method is called on payment success.

        Toast.makeText(getContext(), "Payment is successful : " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        // on payment failed.
        Toast.makeText(getContext(), "Payment Failed due to error : " + s, Toast.LENGTH_SHORT).show();
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
                                                    Intent intent = new Intent(CartFragment.this.getActivity(), Main2Activity.class);
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

    public void loadCart() {
        cart = new Database(this.getContext()).getCarts();
        adapter = new CartAdapter(cart,this);
        if(cart.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.GONE);
            bg.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.VISIBLE);
            bg.setVisibility(View.GONE);
        }

        //Calculate full total price
        int total=0 ;
        for(Cart cart : this.cart)
            total+=(Integer.parseInt(cart.getPrice())*(Integer.parseInt(cart.getQuantity())));
        total_cost.setText(String.valueOf(total));
        recyclerView.setAdapter(adapter);
    }

    //    Override
//    public void onPaymentSuccess(String s) {
//        dRef.child(orderid).setValue(order);
//        new Database(getContext()).cleanCart();
//        alerter=true;
//        sendNotificationOrder(orderid,order);
//        Toast.makeText(getContext(), "Payment is successful : " + s, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onPaymentError(int i, String s) {
//        Toast.makeText(getContext(), "Payment Failed due to error : " + s, Toast.LENGTH_SHORT).show();
//    }@
    public static CartFragment getInstance() {
        return instance;
    }
    public void onCompletedPayment(){
        dRef.child(orderid).setValue(order);
        new Database(getContext()).cleanCart();
        alerter=true;
        sendNotificationOrder(orderid,order);
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