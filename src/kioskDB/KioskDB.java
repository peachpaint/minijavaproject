package kioskDB;

//Main->전체 키오스크 시스템 실행

public class KioskDB {
    public static void main(String[] args) {
        kioskOn();
    }
    public static void kioskOn() {
        OrderDB order = new OrderDB();
        order.run();
    }
}
