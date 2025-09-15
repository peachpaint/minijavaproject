package kiosk;

public class Cust {
    private String phoneNum; 
    private int points;         

    private static final double RATE = 0.01; // 1% 적립률

    public Cust(String phoneNum) {
        this.phoneNum = phoneNum;
        this.points = 0;
    }

    // 전화번호 getter
    public String getPhoneNum() {
        return phoneNum;
    }

    // 포인트 getter
    public int getPoints() {
        return points;
    }

    // 포인트 적립 (결제 금액을 입력받아 자동 계산)
    public int earnPoints(int paymentAmount) {
        int earned = (int)(paymentAmount * RATE);
        points += earned;
        return earned; // 적립된 포인트 반환 (영수증 표시용)
    }

    // 포인트 사용 (예: 차감 기능 추가)
    public boolean usePoints(int amount) {
        if (amount <= 0 || amount > points) return false;
        points -= amount;
        return true;
    }

    @Override
    public String toString() {
        return "전화번호: " + phoneNum + ", 포인트: " + points;
    }
}

