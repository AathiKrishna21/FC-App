package com.joker.fcapp1.Model;

public class Food {
    private String image;
    private String menuId;



    private String name;
    private String price;
    private boolean available;

    public Food() {
    }

    public Food(String image, String menuId, String name, String price, boolean available) {
        this.image = image;
        this.menuId = menuId;
        this.name = name;
        this.price = price;
        this.available = available;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}