package kiosk;



public class Kiosk {
	
	public static void main(String[] args) {
		kioskOn();
	}
	public static void kioskOn() {//키오스크가 실행 될때
		Item menu = new Item();
		menu.runningItem();//Item에서 menu 실행
		Order order = new Order();
		order.runningOrder();//Order
	}
	
	



}

