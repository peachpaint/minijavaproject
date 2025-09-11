package kiosk;

public class Cust {
	private String phoneNum; // 전화번호
    private int points;         // 포인트
    
    public Cust(String phoneNum) {
        this.phoneNum = phoneNum;
        this.points = 0; // 기본 포인트 0
    }
    
    // 전화번호
    public String getPhoneNum() {
        return phoneNum;
    }
    
    // 포인트
    public int getPoints() {
        return points;
    }
    public void addPoints(int amount) {
        if(amount > 0) {
            points += amount;
        }
    }
    
    @Override
    public String toString() {
        return "전화번호: " + phoneNum + ", 포인트: " + points;
    }
}
