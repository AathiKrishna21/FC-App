package com.joker.fcapp1.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joker.fcapp1.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {
    public  TextView orderId,name,statustext,placed,status,time;
    public ImageView image;
    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        orderId=itemView.findViewById(R.id.orderid);
        name=itemView.findViewById(R.id.shopname);
        image=itemView.findViewById(R.id.pic);
        statustext=itemView.findViewById(R.id.statustext);
        placed=itemView.findViewById(R.id.placed);
        status=itemView.findViewById(R.id.status);
        time=itemView.findViewById(R.id.time);
//        items=itemView.findViewById(R.id.phnnum);
//        status=itemView.findViewById(R.id.status);
//        total_cost=itemView.findViewById(R.id.total);


    }
}