package com.joker.fcapp1.Model;

public class Food {
    private String Image;
    private String MenuId;



    private String Name;
    private String Price;
    private boolean Available;

    public Food(String image, String menuId, String name, String price,boolean available) {
        Image = image;
        MenuId = menuId;
        Name = name;
        Price = price;
        Available=available;
    }

    public Food() {

    }
    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
    public boolean getAvailable() {
        return Available;
    }

    public void setAvailable(boolean available) {
        Available = available;
    }
    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }




}
