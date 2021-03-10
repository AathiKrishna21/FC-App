package com.joker.fcapp1.Model;

import java.util.List;

public class Order {
    private String Name,Phonenumber,Totalcost,status,UId;
    private List<Cart> Foods;

    public Order(String name, String phonenumber, String totalcost, String uid, List<Cart> foods) {
        Name = name;
        Phonenumber = phonenumber;
        Totalcost = totalcost;
        Foods = foods;
        this.status="0";
        UId=uid;
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

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhonenumber() {
        return Phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        Phonenumber = phonenumber;
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
