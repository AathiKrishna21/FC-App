package com.joker.fcapp1.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.joker.fcapp1.Model.Cart;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper  {
    private static final String DB_NAME="fcapp_cart1.db";
    private static final int DB_VER=1;
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }
    public List<Cart> getCarts(){
        SQLiteDatabase db=getReadableDatabase();
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();

        String[] sqlSelect={"ProductId","ProductName","Quantity","Price"};
        String sqlTable="Cart";

        qb.setTables(sqlTable);
        Cursor c=qb.query(db,sqlSelect,null,null,null,null,null);

        final List<Cart> result=new ArrayList<>();
        if(c.moveToFirst()){
            do{
                result.add(new Cart(c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price"))
                        ));
            }while(c.moveToNext());
        }
        return result;
    }

    public void addToCart(Cart cart){
        SQLiteDatabase db=getReadableDatabase();
        String query =String.format("INSERT OR REPLACE INTO Cart(ProductId,ProductName,Quantity,Price) VALUES('%s','%s','%s','%s')",
                cart.getProductId(),
                cart.getProductName(),
                cart.getQuantity(),
                cart.getPrice());
        db.execSQL(query);
    }

    public void cleanCart(){
        SQLiteDatabase db=getReadableDatabase();
        String query =String.format("DELETE FROM Cart");
        db.execSQL(query);
    }
}
