package kioskDB;

//이 클래스는 메뉴와 토핑 정보를 MySQL DB에서 읽어와서, 자바의 Map 형태로 저장하고 제공
//프로그램 실행 시 static 블록에서 자동으로 DB에서 데이터를 불러옴

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemDB {
	// 메뉴 카테고리별 (서브_Item 메뉴명, 가격) Map
    static Map<String, LinkedHashMap<String, Integer>> menu = new LinkedHashMap<>();
    // 서브메뉴별_Item (토핑명, 가격) Map
    static Map<String, LinkedHashMap<String, Integer>> option = new LinkedHashMap<>();

    // 클래스 로딩 시 DB에서 메뉴/토핑 정보를 불러옴
    static {
        loadMenuFromDB();
        loadToppingFromDB();
    }

    // 메뉴(카테고리별 서브메뉴) DB에서 불러오기
    static void loadMenuFromDB() {
        menu.clear();
        //DB 연결을 시도
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/kioskdb?serverTimezone=Asia/Seoul", "root", "1111")) {
            // 메뉴와 서브메뉴를 JOIN하여 카테고리별로 메뉴명과 가격을 가져옴
        	String sql = "SELECT m.name AS category, s.name AS item, s.price " +
                         "FROM menu m JOIN submenu s ON m.menu_id = s.menu_id " +
                         "ORDER BY m.menu_id, s.submenu_id";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                // 결과를 Map에 저장
            	while (rs.next()) {
                    String category = rs.getString("category");
                    String item = rs.getString("item");
                    int price = rs.getInt("price");
                    menu.computeIfAbsent(category, k -> new LinkedHashMap<>()).put(item, price);
                }
            }
        } catch (SQLException e) {// DB 연결 또는 쿼리 실행 중 오류 발생 시 예외 출력
            e.printStackTrace();
        }
    }

    // 서브메뉴별 토핑 DB에서 불러오기
    static void loadToppingFromDB() {
        option.clear();
        // DB 연결을 시도합니다.
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/kioskdb?serverTimezone=Asia/Seoul", "root", "1111")) {
        	// 서브메뉴와 토핑을 LEFT JOIN하여 서브메뉴별 토핑명과 가격을 가져옵니다.
        	String sql = "SELECT s.name AS submenu, t.name AS topping, t.price " +
                         "FROM submenu s LEFT JOIN topping t ON s.submenu_id = t.submenu_id " +
                         "ORDER BY s.submenu_id, t.topping_id";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
            	//결과를 Map에 저장
                while (rs.next()) {
                    String submenu = rs.getString("submenu");
                    String topping = rs.getString("topping");
                    int price = rs.getInt("price");
                    if (topping != null) {
                        option.computeIfAbsent(submenu, k -> new LinkedHashMap<>()).put(topping, price);
                    }
                }
            }
        } catch (SQLException e) {
        	// DB 연결 또는 쿼리 실행 중 오류 발생 시 예외 출력
            e.printStackTrace();
        }
    }

    // 메뉴 정보 반환
    public static Map<String, LinkedHashMap<String, Integer>> getMenu() {
        return menu;
    }
    
    // 토핑 정보 반환
    public static Map<String, LinkedHashMap<String, Integer>> getOption() {
        return option;
    }
}
