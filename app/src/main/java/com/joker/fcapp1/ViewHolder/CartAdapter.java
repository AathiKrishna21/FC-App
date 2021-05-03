package com.joker.fcapp1.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joker.fcapp1.Database.Database;
import com.joker.fcapp1.Interface.ItemClickListener;
import com.joker.fcapp1.Model.Cart;
import com.joker.fcapp1.Model.Order;
import com.joker.fcapp1.R;
import com.joker.fcapp1.ui.cart.CartFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView foodname,cost;
    public ImageView photo,veg;
    public ElegantNumberButton count;

    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        foodname=itemView.findViewById(R.id.food_name);
        cost=itemView.findViewById(R.id.tcost);
        photo=itemView.findViewById(R.id.imgpic);
        count=itemView.findViewById(R.id.count);
        veg=itemView.findViewById(R.id.veg);
    }

    @Override
    public void onClick(View view) {

    }
}
public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{
    FirebaseDatabase database;
    DatabaseReference dRef;

    private List<Cart> listData = new ArrayList<>();
    private CartFragment cart1;
    int total=0 ;

    public CartAdapter(List<Cart> listData, CartFragment cart1) {
        this.listData = listData;
        this.cart1=cart1;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cart1.getContext());
        View itemView = inflater.inflate(R.layout.cart_items,parent,false);
        database = FirebaseDatabase.getInstance();
        dRef=database.getReference("Foods");
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {

        String foodid=listData.get(position).getProductId();
        holder.foodname.setText(listData.get(position).getProductName());
        holder.cost.setText("Rs."+String.valueOf(Integer.parseInt(listData.get(position).getPrice())*Integer.parseInt(listData.get(position).getQuantity())));
        holder.count.setNumber(listData.get(position).getQuantity());
        dRef.child(foodid).child("Image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Picasso.get().load(snapshot.getValue(String.class)).into(holder.photo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.count.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Cart cart=listData.get(position);
                cart.setQuantity(String.valueOf(newValue));
                new Database(cart1.getContext()).updateCart(cart);
                holder.cost.setText("Rs."+String.valueOf(Integer.parseInt(listData.get(position).getPrice())*Integer.parseInt(listData.get(position).getQuantity())));
                total=0;
                for(Cart c : listData)
                    total+=((Integer.parseInt(c.getPrice()))*(Integer.parseInt(c.getQuantity())));
                cart1.total_cost.setText(String.valueOf(total));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
