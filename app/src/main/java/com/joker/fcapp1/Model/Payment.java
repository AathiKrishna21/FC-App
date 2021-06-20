package com.joker.fcapp1.Model;

public class Payment {
    public static String orderId;
    public static Order order;
    public static boolean isCart;

    public Payment() {
    }

    public static String getOrderId() {
        return orderId;
    }

    public static void setOrderId(String orderId) {
        Payment.orderId = orderId;
    }

    public static Order getOrder() {
        return order;
    }

    public static void setOrder(Order order) {
        Payment.order = order;
    }

    public static boolean isIsCart() {
        return isCart;
    }

    public static void setIsCart(boolean isCart) {
        Payment.isCart = isCart;
    }
}
