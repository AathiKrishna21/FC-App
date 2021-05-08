package com.joker.fcapp1.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joker.fcapp1.R;

public class FavoriteViewHolder extends RecyclerView.ViewHolder{
    public TextView shopname,cost,orderid,items,item;
    public Button receipt;
    public FavoriteViewHolder(@NonNull View itemView) {
        super(itemView);
        orderid=itemView.findViewById(R.id.orderid);
        items=itemView.findViewById(R.id.items);
        shopname=itemView.findViewById(R.id.shopname);
        cost=itemView.findViewById(R.id.total_cost);
        receipt=itemView.findViewById(R.id.btnreceipt);
        item=itemView.findViewById(R.id.item);
    }
}
