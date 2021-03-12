package com.joker.fcapp1.Model;

import java.util.List;

public class Order {
    private String name,phonenumber,ShopId,Totalcost,status,UId;
    private List<Cart> Foods;

    public Order(String name,String phonenumber,String shopId, String totalcost, String status, String uid, List<Cart> foods){
        this.name=name;
        this.phonenumber=phonenumber;
        ShopId = shopId;
        Totalcost = totalcost;
        Foods = foods;
        this.status=status;
        UId=uid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
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