package com.joker.fcapp1.Model;

import java.util.List;

public class Order {
    private String date,time,ShopId,Totalcost,status,UId;
    private List<Cart> Foods;

    public Order(String date,String time,String shopId, String totalcost, String status, String uid, List<Cart> foods){
        this.date=date;
        this.time=time;
        ShopId = shopId;
        Totalcost = totalcost;
        Foods = foods;
        this.status=status;
        UId=uid;
    }

    public String getDate() {
        return date;

    }
    public int getitems(){
        return Foods.size();
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUId() {
        return UId;
    }

    public void setUId(String UId) {
        this.UId = UId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Order() {
    }

    public String getShopId() {
        return ShopId;
    }

    public void setShopId(String shopId) {
        ShopId = shopId;
    }

    public String getTotalcost() {
        return Totalcost;
    }

    public void setTotalcost(String totalcost) {
        Totalcost = totalcost;
    }

    public List<Cart> getFoods() {
        return Foods;
    }

    public void setFoods(List<Cart> foods) {
        Foods = foods;
    }
}
