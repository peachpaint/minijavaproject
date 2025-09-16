package kiosk.db;


import java.sql.*;

public class Cust {
    private String phoneNum;
    private int points;

    private static final double RATE = 0.01; // 1% 적립률

    // DB 연결 정보 (환경에 맞게 수정)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/kioskdb?serverTimezone=Asia/Seoul";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "password";

    public Cust(String phoneNum) {
        this.phoneNum = phoneNum;
        // DB에서 고객 정보 조회 또는 신규 등록
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT point FROM customer WHERE phone = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, phoneNum);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    this.points = rs.getInt("point");
                } else {
                    this.points = 0;
                    String insertSql = "INSERT INTO customer (phone, point) VALUES (?, 0)";
                    try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql)) {
                        insertPstmt.setString(1, phoneNum);
                        insertPstmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.points = 0;
        }
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public int getPoints() {
        return points;
    }

    // 포인트 적립 (결제 금액을 입력받아 자동 계산)
    public int earnPoints(int paymentAmount) {
        int earned = (int)(paymentAmount * RATE);
        points += earned;
        updatePointsInDB();
        return earned;
    }

    // 포인트 사용 (예: 차감 기능 추가)
    public boolean usePoints(int amount) {
        if (amount <= 0 || amount > points) return false;
        points -= amount;
        updatePointsInDB();
        return true;
    }

    // DB에 포인트 업데이트
    private void updatePointsInDB() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "UPDATE customer SET point = ? WHERE phone = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, points);
                pstmt.setString(2, phoneNum);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "전화번호: " + phoneNum + ", 포인트: " + points;
    }
}