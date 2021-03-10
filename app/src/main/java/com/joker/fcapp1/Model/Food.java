package com.joker.fcapp1.Model;

public class Food {
    private String Image;
    private String MenuId;
    private String Name;
    private String Price;

    public Food(String image, String menuId, String name, String price) {
        Image = image;
        MenuId = menuId;
        Name = name;
        Price = price;
    }

    public Food() {

    }
    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
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
