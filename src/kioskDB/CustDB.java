package kioskDB;

//고객의 전화번호와 포인트 정보를 MySQL DB와 연동하여 관리
//생성자에서 고객이 DB에 없으면 새로 등록하고, 있으면 포인트를 불러온다
//포인트 적립시 DB에 바로 반영

import java.sql.*;

public class CustDB {
    private String phoneNum;
    private int points;

    private static final double RATE = 0.01; // 1% 적립률

    private static final String DB_URL = "jdbc:mysql://localhost:3306/kioskdb?serverTimezone=Asia/Seoul";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "1111";

	// 생성자: 전화번호로 고객을 조회, 없으면 신규 등록
    public CustDB(String phoneNum) {
        this.phoneNum = phoneNum;
        //-> DB에서 고객 정보 조회 또는 신규 등록
        // DB연결
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
        	// 해당 전화번호로 고객이 이미 있는지 SELECT 쿼리로 확인
        	String sql = "SELECT point FROM customer WHERE phone = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, phoneNum);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                	// 이미 있으면 포인트 값을 불러옴
                    this.points = rs.getInt("point");
                } else {
                	// 없으면 INSERT 쿼리로 신규 고객을 등록
                    this.points = 0;
                    String insertSql = "INSERT INTO customer (phone, point) VALUES (?, 0)";
                    try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql)) {
                        insertPstmt.setString(1, phoneNum);
                        insertPstmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
        	// DB 연결 또는 쿼리 실행 중 오류 발생 시 예외 출력
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
    	//DB 연결을 시도
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            //UPDATE 쿼리로 포인트 값을 DB에 반영
        	String sql = "UPDATE customer SET point = ? WHERE phone = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, points);
                pstmt.setString(2, phoneNum);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
        	// DB 연결 또는 쿼리 실행 중 오류 발생 시 예외 출력
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "전화번호: " + phoneNum + ", 포인트: " + points;
    }
}
