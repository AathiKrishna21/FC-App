package com.joker.fcapp1.ui.cart;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
import com.joker.fcapp1.Model.Internet;
import com.joker.fcapp1.Model.Notification;
import com.joker.fcapp1.Model.Order;
import com.joker.fcapp1.Model.Payment;
import com.joker.fcapp1.Model.Response;
import com.joker.fcapp1.Model.Sender;
import com.joker.fcapp1.Model.Token;
import com.joker.fcapp1.R;
import com.joker.fcapp1.Remote.APIService;
import com.joker.fcapp1.StdFrontPageActivity;
import com.joker.fcapp1.ViewHolder.CartAdapter;
import com.joker.fcapp1.ui.orders.OrdersFragment;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.tapadoo.alerter.Alerter;
import com.thekhaeng.pushdownanim.PushDownAnim;

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
    RelativeLayout relativeLayout,amt;
    RadioButton op,cash;
    RadioGroup radioGroup;
    FirebaseDatabase database;
    DatabaseReference dRef,FoodRef,profiledRef;
    Boolean alerter=false;
    public TextView total_cost;
    Button orderbtn;
    ImageView bg;
    int cost,tax;
    TextView a1,a2,a3;
    Cart cart1;
    String date,time,time2,orderid;
    List<Cart> cart = new ArrayList<>();
    CartAdapter adapter;
    String userKey,ShopId,FoodId,name,phnno;
    APIService mService;
    Order order;
    LottieAnimationView view;
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
//        view=root.findViewById(R.id.animation_view);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userKey = user.getUid();

        if(Internet.isConnectedToInternet(getContext())){
            bg.setVisibility(View.GONE);
            loadCart(0);
        }
        else{
            relativeLayout.setVisibility(View.GONE);
            if(new Database(getContext()).getItemCount()!=0)
                ((Main2Activity)getActivity()).removeBadgeView();
        }
        PushDownAnim.setPushDownAnimTo(orderbtn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
        orderbtn.setOnClickListener(new View.OnClickListener() {
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
//                if(op.isChecked()){
//                    amt.setVisibility(View.VISIBLE);
//                }

                alertDialog.setView(view1);
                alertDialog.setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(op.isChecked()){

                            String samount=a3.getText().toString().substring(1);
                            int amount =Math.round(Float.parseFloat(samount)*100);
                            order = new Order("","",ShopId,String.valueOf(amount),"0",userKey,cart,false);
                            orderid=String.valueOf(System.currentTimeMillis());
                            Payment.setOrderId(orderid);
                            Payment.setOrder(order);
                            Payment.setIsCart(true);
                            ((Main2Activity)getActivity()).razorpay();
//                            Checkout checkout=new Checkout();
//                            checkout.setKeyID("rzp_test_v9Eu6C6hCOiCvb");
//                            checkout.setImage(R.drawable.fc_logo);
//                            JSONObject object=new JSONObject();
//                            orderid=String.valueOf(System.currentTimeMillis());
//                            try {
//                                // to put name
//                                object.put("name", convertCodetoShop(order.getShopId()));
//
//                                // put description
//                                object.put("description", orderid);
//
//                                // to set theme color
//                                object.put("theme.color", "#FF7104");
//
//                                // put the currency
//                                object.put("currency", "INR");
//
//                                // put amount
//                                object.put("amount", amount);
//
//                                // put mobile number
//                                object.put("prefill.contact", user.getPhoneNumber());
//
//                                // put email
//                                object.put("prefill.email", email);
//
//                                // open razorpay to checkout activity
//                                checkout.open(getActivity(), object);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
                        }
                        if(cash.isChecked()){
                            Date currentTime = Calendar.getInstance().getTime();
                            String datetime=currentTime.toString();
                            String[] words=datetime.split("\\s");
                            date=words[1]+" "+words[2]+" "+words[5];
                            time=words[3];
                            time2=time.substring(0,5);
                            order = new Order(date,time,ShopId,total_cost.getText().toString().substring(3),"0",userKey,cart,false);
                            orderid=String.valueOf(System.currentTimeMillis());
                            Payment.setOrder(order);
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
                            cost=Integer.parseInt(total_cost.getText().toString().substring(3  ));
                            tax=(int)Math.ceil(cost*0.0236);
                            a1.setText("₹"+String.valueOf(cost));
                            a2.setText("₹"+String.valueOf((tax)));
                            a3.setText("₹"+String.valueOf(cost+tax));
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
//                Date currentTime = Calendar.getInstance().getTime();
//                String datetime=currentTime.toString();
//                String[] words=datetime.split("\\s");
//                date=words[1]+" "+words[2]+" "+words[5];
//                time=words[3];
//                time2=time.substring(0,5);
//                order = new Order(date,time,ShopId,total_cost.getText().toString(),"0",userKey,cart,false);
//                orderid=String.valueOf(System.currentTimeMillis());
//                FirebaseUser user=mAuth.getCurrentUser();
//                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
//                String email=acct.getEmail();
//                String samount=total_cost.getText().toString();
//                int amount =Math.round(Float.parseFloat(samount)*100);
//                Checkout checkout=new Checkout();
//                checkout.setKeyID("rzp_test_v9Eu6C6hCOiCvb");
//                checkout.setImage(R.drawable.fc_logo);
//                JSONObject object=new JSONObject();
//                try {
//                    // to put name
//                    object.put("name", convertCodetoShop(order.getShopId()));
//
//                    // put description
//                    object.put("description", orderid);
//
//                    // to set theme color
//                    object.put("theme.color", "#FF7104");
//
//                    // put the currency
//                    object.put("currency", "INR");
//
//                    // put amount
//                    object.put("amount", amount);
//
//                    // put mobile number
//                    object.put("prefill.contact", user.getPhoneNumber());
//
//                    // put email
//                    object.put("prefill.email", email);
//
//                    // open razorpay to checkout activity
//                    checkout.open(getActivity(), object);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });



        if(!cart.isEmpty()) {
            cart1=cart.get(0);
            FoodId = cart1.getProductId();
            FoodRef.child(FoodId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ShopId = snapshot.child("menuId").getValue().toString();
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

    public void loadCart(int flag) {
        cart = new Database(this.getContext()).getCarts();
        Database database=new Database(getContext());
        adapter = new CartAdapter(cart,this);
        if(cart.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.GONE);
            bg.setVisibility(View.VISIBLE);
            if(flag==1)
                ((Main2Activity)getActivity()).removeBadgeView();
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.VISIBLE);
            bg.setVisibility(View.GONE);
            ((Main2Activity)getActivity()).editBadgeView();
        }

        //Calculate full total price
        int total=0 ;
        for(Cart cart : this.cart)
            total+=(Integer.parseInt(cart.getPrice())*(Integer.parseInt(cart.getQuantity())));
        total_cost.setText("Rs."+String.valueOf(total));
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
        dRef.child(orderid).setValue(Payment.getOrder());
        if(Payment.isIsCart())
            new Database(getContext()).cleanCart();
        alerter=true;
        sendNotificationOrder(orderid,Payment.getOrder());
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