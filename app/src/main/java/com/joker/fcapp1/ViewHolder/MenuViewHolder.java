package com.joker.fcapp1.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.joker.fcapp1.R;
import com.squareup.picasso.Picasso;

public class MenuViewHolder extends RecyclerView.ViewHolder {
    public TextView name,cost;
    public ElegantNumberButton quantity;
    public ImageView photo;
    public Button add;
    public  MenuViewHolder(@NonNull View itemView) {
        super(itemView);
        name=itemView.findViewById(R.id.food_name);
        photo=itemView.findViewById(R.id.imgpic);
        cost=itemView.findViewById(R.id.cost);
        quantity=itemView.findViewById(R.id.count);
        add=itemView.findViewById(R.id.btnadd);

    }
}
