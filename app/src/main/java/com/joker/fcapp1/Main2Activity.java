package com.joker.fcapp1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joker.fcapp1.Database.Database;
import com.joker.fcapp1.Model.APIModel;
import com.joker.fcapp1.Model.Notification;
import com.joker.fcapp1.Model.Order;
import com.joker.fcapp1.Model.Payment;
import com.joker.fcapp1.Model.Response;
import com.joker.fcapp1.Model.Sender;
import com.joker.fcapp1.Model.Token;
import com.joker.fcapp1.Remote.APIService;
import com.joker.fcapp1.ui.cart.CartFragment;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;

public class Main2Activity extends AppCompatActivity implements PaymentResultListener {
    Window window;
    BottomNavigationView navView;
    public View notificationBadge;
    TextView tv;
    FirebaseDatabase db;
    Database database;
    DatabaseReference dRef;
    BottomNavigationItemView itemView;
    FirebaseAuth mAuth;
    String date,time,time2;
    Boolean alerter=false;
    APIService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_cart, R.id.navigation_orders,R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        window=this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.status_bar));
        db=FirebaseDatabase.getInstance();
        dRef = db.getReference("Orders");
        database=new Database(this);
        mService= APIModel.getFCMService();
        String count=String.valueOf(database.getItemCount());
        mAuth = FirebaseAuth.getInstance();
        if(database.getItemCount()!=0)
            addBadgeView(count);
    }
    public void addBadgeView(String s) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navView.getChildAt(0);
        itemView = (BottomNavigationItemView) menuView.getChildAt(1);
        notificationBadge = LayoutInflater.from(this).inflate(R.layout.view_navigation_badge, menuView, false);
        tv=notificationBadge.findViewById(R.id.badge);
        if((ViewGroup)notificationBadge.getParent()!=null)
            ((ViewGroup)notificationBadge.getParent()).removeView(notificationBadge);
        if(database.getItemCount()!=0) tv.setText(s);
        itemView.addView(notificationBadge);
    }
    public void razorpay(){
        FirebaseUser user=mAuth.getCurrentUser();
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        String email=acct.getEmail();
        Checkout checkout=new Checkout();
        checkout.setKeyID("rzp_test_v9Eu6C6hCOiCvb");
        checkout.setImage(R.drawable.fc_logo);
        JSONObject object=new JSONObject();
        try {
            // to put name
            object.put("name", convertCodetoShop(new Payment().getOrder().getShopId()));

            // put description
            object.put("description", new Payment().getOrderId());

            // to set theme color
            object.put("theme.color", "#FF7104");

            // put the currency
            object.put("currency", "INR");

            // put amount
            object.put("amount", new Payment().getOrder().getTotalcost());

            // put mobile number
            object.put("prefill.contact", user.getPhoneNumber());

            // put email
            object.put("prefill.email", email);

            // open razorpay to checkout activity
            checkout.open(Main2Activity.this, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void editBadgeView(){
        tv.setText(String.valueOf(database.getItemCount()));
    }
    public void removeBadgeView() {
        notificationBadge.setVisibility(View.GONE);
    }
    @Override
    public void onPaymentSuccess(String s) {
        onCompletedPayment();
        Toast.makeText(this, "Payment is successful : " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        // on payment failed.
        Toast.makeText(this, "Payment Failed due to error : " + s, Toast.LENGTH_SHORT).show();
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
    public void onCompletedPayment(){
        Date currentTime = Calendar.getInstance().getTime();
        String datetime=currentTime.toString();
        String[] words=datetime.split("\\s");
        date=words[1]+" "+words[2]+" "+words[5];
        time=words[3];
        time2=time.substring(0,5);
        Payment.order.setDate(date);
        Payment.order.setTime(time);
        String tt=Payment.order.getTotalcost();
        tt=tt.substring(0,tt.length()-2);
        Payment.order.setTotalcost(tt);
//        order.setDate(date);
//        order.setTime(time);
        dRef.child(Payment.getOrderId()).setValue(Payment.getOrder());
        if(Payment.isIsCart())
            new Database(this).cleanCart();
        alerter=true;
        sendNotificationOrder(Payment.getOrderId(),Payment.getOrder());
    }
    private void sendNotificationOrder(final String orderid,final Order order) {
        DatabaseReference tokenref=db.getReference("Tokens");
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
                                                    Intent intent = new Intent(Main2Activity.this, Main2Activity.class);
                                                    intent.putExtra("Alerter", alerter);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(Main2Activity.this, "Failed", Toast.LENGTH_SHORT).show();
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