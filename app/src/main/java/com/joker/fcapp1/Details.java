package com.joker.fcapp1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.joker.fcapp1.Model.Order;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.joker.fcapp1.DetailsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joker.fcapp1.Model.Cart;
import com.joker.fcapp1.R;

import java.util.List;

public class Details extends Activity {
    ProgressDialog mProgressBar;
    FirebaseDatabase database;
    DatabaseReference dRef,dbRef;
    List<Cart> cart;
    Order order1;
    String id;
    static boolean favo;
    Button b;
    TextView t1,shopname,menu;
    TableRow table;
    RecyclerView detialsrcview;
    DetailsAdapter adapter;
    SpinKitView threebounce;
    RelativeLayout rv;
    ToggleButton toggleButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        DisplayMetrics dm=new DisplayMetrics();
        database= FirebaseDatabase.getInstance();
        dRef= database.getReference("PastOrders");
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.6));
        rv=findViewById(R.id.rv);
//        b=findViewById(R.id.favo);
//        b.setVisibility(View.GONE);
        rv.setVisibility(View.GONE);
        threebounce = findViewById(R.id.spin_kit);
        menu=findViewById(R.id.id);
        shopname=findViewById(R.id.shopname);
        Sprite tb = new ThreeBounce();
        threebounce.setIndeterminateDrawable(tb);
        detialsrcview=findViewById(R.id.detailsrcview);
        detialsrcview.setHasFixedSize(true);
        detialsrcview.setLayoutManager(new LinearLayoutManager(this));
        toggleButton = (ToggleButton) findViewById(R.id.myToggleButton);
        toggleButton.setChecked(false);
        if(favo){
            toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_heartfill));
        }
        else{
            toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_heart));
        }
        toggleButton.setVisibility(View.GONE);

        startLoadData();
    }
    public void startLoadData() {
        threebounce.setVisibility(View.VISIBLE);
        new LoadDataTask().execute(0);
    }
    public void loadData() {
        int leftRowMargin = 0;
        int topRowMargin = 0;
        int rightRowMargin = 0;
        int bottomRowMargin = 0;
        int textSize = 0, smallTextSize = 0, mediumTextSize = 0;
        textSize = (int) getResources().getDimension(R.dimen.font_size_verysmall);
        smallTextSize = (int) getResources().getDimension(R.dimen.font_size_small);
        mediumTextSize = (int) getResources().getDimension(R.dimen.font_size_medium);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
//        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        Bundle extras = getIntent().getExtras();
//        Cart[] target=new Cart[];
        id = extras.getString("id");
        menu.setText("#"+id);

        dRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                order1 = snapshot.getValue(Order.class);
                shopname.setText(convertCodetoShop(order1.getShopId()));
                cart = order1.getFoods();
                favo=order1.isFavorite();
                adapter=new DetailsAdapter(Details.this,cart);
                detialsrcview.setAdapter(adapter);
//                hi.setText(order1.getName());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dRef.child(id).child("favorite").setValue(true);
//            }
//        });
//        menu.setText(String.valueOf(favo));
        toggleButton.setOnCheckedChangeListener(new Tlistener(favo));

    }
    class LoadDataTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            threebounce.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
//            b.setVisibility(View.VISIBLE);
            toggleButton.setVisibility(View.VISIBLE);
            loadData();
        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
    class Tlistener implements CompoundButton.OnCheckedChangeListener{
        boolean favo;
        public Tlistener(boolean favo){
            this.favo=favo;
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (favo||isChecked) {
                toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_heartfill));
                dRef.child(id).child("favorite").setValue(true);
            }
            else{
                toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_heart));
                dRef.child(id).child("favorite").setValue(false);
            }
        }
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