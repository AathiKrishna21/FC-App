package com.joker.fcapp1.ViewHolder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joker.fcapp1.Interface.ItemClickListener;
import com.joker.fcapp1.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ImageView food_image;

    private ItemClickListener itemCLickListener;

    public void setItemCLickListener(ItemClickListener itemCLickListener) {
        this.itemCLickListener = itemCLickListener;
    }

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);

        food_image=itemView.findViewById(R.id.food_image);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemCLickListener.onClick(view,getAdapterPosition(),false);
    }
}
