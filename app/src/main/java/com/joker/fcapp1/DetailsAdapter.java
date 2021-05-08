package com.joker.fcapp1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joker.fcapp1.Model.Cart;
import com.joker.fcapp1.R;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder> {
    Context context;
    List<Cart> carts;
    public DetailsAdapter(Context context,List<Cart> carts){
        this.context=context;
        this.carts=carts;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.detailsrcview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (carts!=null && carts.size()>0){
            Cart model=carts.get(position);
            holder.sno.setText("1");
            holder.fname.setText(model.getProductName());
            holder.cost.setText(model.getPrice());
            holder.quantity.setText(model.getQuantity());
        }
        else {
            return;
        }
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView sno,fname,quantity,cost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sno=itemView.findViewById(R.id.s_no);
            fname=itemView.findViewById(R.id.fname);
            quantity=itemView.findViewById(R.id.quantity);
            cost=itemView.findViewById(R.id.cost);
        }
    }
}
