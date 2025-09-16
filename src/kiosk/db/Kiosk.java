package kiosk.db;


public class Kiosk {
    public static void main(String[] args) {
        kioskOn();
    }
    public static void kioskOn() {
        Order order = new Order();
        order.run();
    }
}



