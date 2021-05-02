package com.joker.fcapp1.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joker.fcapp1.R;

public class PastOrderViewHolder extends RecyclerView.ViewHolder{
    public TextView shopname,cost,orderid,date,items;
    public Button receipt;
    public PastOrderViewHolder(@NonNull View itemView) {
        super(itemView);
        orderid=itemView.findViewById(R.id.orderid);
        items=itemView.findViewById(R.id.items);
        date=itemView.findViewById(R.id.date);
        shopname=itemView.findViewById(R.id.shopname);
        cost=itemView.findViewById(R.id.total_cost);
        receipt=itemView.findViewById(R.id.btnreceipt);
    }
}
