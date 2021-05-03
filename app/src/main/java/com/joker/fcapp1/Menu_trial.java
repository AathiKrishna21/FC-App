package com.joker.fcapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
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
import com.joker.fcapp1.ViewHolder.FoodViewHolder;
import com.joker.fcapp1.ViewHolder.MenuViewHolder;
import com.joker.fcapp1.ui.home.HomeFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Menu_trial extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    List<Cart> listData = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference dRef,Ref;
    ImageView img,back;
    String shopId;
    TextView shop_name;
    FirebaseRecyclerAdapter<Food, MenuViewHolder> adapter;
    Window window;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_trial);
        img=findViewById(R.id.shop_img);
        shop_name=findViewById(R.id.shopname);
        back=findViewById(R.id.back_button);
        database= FirebaseDatabase.getInstance();
        dRef= database.getReference("Foods");
        Ref = database.getReference("Shops");
        recyclerView = findViewById(R.id.menurecyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        window=this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.status_bar));
        shopId = getIntent().getStringExtra("ShopId");
        loadFoods(shopId);
    }

    private void loadFoods(final String shopId) {
        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                    .setQuery(dRef.orderByChild("MenuId").equalTo(shopId),Food.class)
                    .build();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Menu_trial.this,Main2Activity.class);
                startActivity(intent);
            }
        });
        Ref.child(shopId).child("Image").addValueEventListener(new ValueEventListener() {
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

                Picasso.get().load(model.getImage()).into(holder.photo);
                holder.quantity.setNumber("1");
                holder.quantity.setVisibility(View.GONE);
                holder.cost.setText(model.getPrice());
                holder.name.setText(model.getName());

                holder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Database(getBaseContext()).addToCart(new Cart(adapter.getRef(position).getKey(),model.getName(),"1",model.getPrice()));
                        holder.quantity.setVisibility(View.VISIBLE);
                        holder.add.setVisibility(View.GONE);
                    }

                });

                holder.quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                    @Override
                    public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                        Database database=new Database(getBaseContext());
                        listData=database.getCarts();
                        for(Cart c: listData){
                            if(c.getProductId().equals(adapter.getRef(position).getKey())){
                                c.setQuantity(String.valueOf(newValue));
//                                new Database(getBaseContext()).updateCart(c);
                                database.updateCart(c);
//                                holder.cost.setText();
                            }
                        }
//                        Cart cart=listData.get(position);
//                        cart.setQuantity(String.valueOf(newValue));
//                        new Database(getBaseContext()).updateCart(cart);
                    }
                });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dish,parent,false);
                return new MenuViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Menu_trial.this,Main2Activity.class);
        startActivity(intent);
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
