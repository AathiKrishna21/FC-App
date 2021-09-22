package com.joker.fcapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joker.fcapp1.Database.Database;
import com.joker.fcapp1.Interface.ItemClickListener;
import com.joker.fcapp1.Model.Cart;
import com.joker.fcapp1.Model.Food;
import com.joker.fcapp1.Model.SFlag;
import com.joker.fcapp1.ViewHolder.FoodViewHolder;
import com.joker.fcapp1.ViewHolder.MenuViewHolder;
import com.joker.fcapp1.ui.home.HomeFragment;
import com.squareup.picasso.Picasso;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class Menu_trial extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    List<Cart> listData = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference dRef,Ref;
    ImageView img,back;
    Button gotocart;
    ShimmerFrameLayout shimmerFrameLayout;
    TextView shop_name,items,amount;
    FirebaseRecyclerAdapter<Food, MenuViewHolder> adapter;
    Window window;
    String shopId,FoodId;
    List<Cart> cart;
    int total;
    RelativeLayout cartrl;
    Cart cart1;
    ImageButton backbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_trial);
        img=findViewById(R.id.shop_img);
        shop_name=findViewById(R.id.shopname);
        backbtn=findViewById(R.id.back_button);
        database= FirebaseDatabase.getInstance();
        dRef= database.getReference("Foods");
        Ref = database.getReference("Shops");
        recyclerView = findViewById(R.id.menurecyclerview);
        amount = findViewById(R.id.amount);
        gotocart = findViewById(R.id.gotocart);
        cartrl = findViewById(R.id.cart_relative);
        items = findViewById(R.id.items);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        window=this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.status_bar));
//        recyclerView.setVisibility(View.GONE);
        shopId = getIntent().getStringExtra("ShopId");
        onadd();
        PushDownAnim.setPushDownAnimTo(gotocart)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
        shimmerFrameLayout = findViewById(R.id.shimmerLayout);
        if(new Database(this).getItemCount()==0)
            cartrl.setVisibility(View.GONE);
        if(SFlag.getSflag()==0)
            shimmerFrameLayout.startShimmer();
        if(SFlag.getSflag()==1) {
            shimmerFrameLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        PushDownAnim.setPushDownAnimTo(backbtn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
        loadFoods(shopId);
//        shimmerFrameLayout = findViewById(R.id.shimmerLayout);
//        shimmerFrameLayout.startShimmer();
//        new Menu_trial.LoadDataTask().execute(0);
    }

    private void loadFoods(final String shopId) {
        gotocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Menu_trial.this,Main2Activity.class);
                intent.putExtra("isCart","true");
                startActivity(intent);
                finish();
            }
        });
        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(dRef.orderByChild("menuId").equalTo(shopId),Food.class)
                        .build();
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Menu_trial.this,Main2Activity.class);
                startActivity(intent);
            }
        });
        Ref.child(shopId).child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String url=snapshot.getValue(String.class);
                Picasso.get().load(url).into(img);
                shop_name.setText(convertCodetoShop(shopId));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new FirebaseRecyclerAdapter<Food, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MenuViewHolder holder, final int position, @NonNull final Food model) {
                if(model.isAvailable()) {
                    cart = new Database(getBaseContext()).getCarts();
                    holder.cost.setText("Rs." + model.getPrice());
                    holder.name.setText(model.getName());
                    holder.add.setVisibility(View.VISIBLE);
                    holder.quantity.setNumber("1");
                    Picasso.get().load(model.getImage()).into(holder.photo);
                    if (!cart.isEmpty()) {
                        cartrl.setVisibility(View.VISIBLE);
                        cart1 = cart.get(0);
                        for (Cart c : cart) {
                            if (c.getProductId().equals(adapter.getRef(position).getKey())) {
                                //holder.cost.setText(adapter.getRef(position).getKey());
                                holder.add.setVisibility(View.GONE);
                                holder.quantity.setVisibility(View.VISIBLE);
                                holder.quantity.setNumber(c.getQuantity());
                            }
                        }
                        FoodId = cart1.getProductId();
                        dRef.child(FoodId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String ShopId = snapshot.child("menuId").getValue().toString();
                                if (!shopId.equals(ShopId)) {
                                    holder.add.setVisibility(View.GONE);
                                    holder.quantity.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        cartrl.setVisibility(View.GONE);
                        Picasso.get().load(model.getImage()).into(holder.photo);
                        holder.quantity.setNumber("1");
                        holder.quantity.setVisibility(View.GONE);
                    }


                    holder.add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new Database(getBaseContext()).addToCart(new Cart(adapter.getRef(position).getKey(), model.getName(), "1", model.getPrice()));
                            holder.quantity.setNumber("1");
                            holder.quantity.setVisibility(View.VISIBLE);
                            holder.add.setVisibility(View.GONE);
                            cartrl.setVisibility(View.VISIBLE);
                            onadd();
                        }

                    });

                    holder.quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                        @Override
                        public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                            Database database = new Database(getBaseContext());
                            listData = database.getCarts();
                            if (String.valueOf(newValue).equals("0")) {
                                holder.add.setVisibility(View.VISIBLE);
                                holder.quantity.setVisibility(View.GONE);
                            }
                            for (Cart c : listData) {
                                if (c.getProductId().equals(adapter.getRef(position).getKey())) {
                                    c.setQuantity(String.valueOf(newValue));
//                                new Database(getBaseContext()).updateCart(c);
                                    database.updateCart(c);
                                    onadd();
//                                holder.cost.setText();
                                }
                            }
                        }
                    });
                }
                else{
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }

            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dish,parent,false);
                return new MenuViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
//        if(SFlag.getSflag()==0) {
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    shimmerFrameLayout.setVisibility(View.GONE);
////                    shimmerFrameLayout.stopShimmer();
////                    recyclerView.setVisibility(View.VISIBLE);
//                    SFlag.setSflag(1);
//                }
//            }, 1200);
//
//        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Menu_trial.this,Main2Activity.class);
        startActivity(intent);
    }
    @NotNull
    private String convertCodetoShop(@NotNull String menuId) {
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
    class LoadDataTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            loadFoods(shopId);

        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
    public void onadd(){
        cartrl.setVisibility(View.VISIBLE);
        total=0;
        Database database=new Database(this);
        long temp=database.getItemCount();
        if(temp==0){
            cartrl.setVisibility(View.GONE);
        }
        items.setText("x"+String.valueOf(temp)+" Items");
        cart = new Database(this).getCarts();
        for(Cart cart : this.cart)
            total+=(Integer.parseInt(cart.getPrice())*(Integer.parseInt(cart.getQuantity())));
        amount.setText("₹"+String.valueOf(total));

    }
}