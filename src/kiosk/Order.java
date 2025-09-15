package kiosk;

import java.util.*;

public class Order {
    private Scanner scanner = new Scanner(System.in);
    private Menu menu = new Menu();// 메뉴 관리 객체
    private Cart cart = new Cart();// 장바구니 관리 객체

    // 전화번호별 고객 관리(포인트 적립/조회 용도)
    private Map<String, Cust> customerDB = new HashMap<>();
    // 첫 주문 여부 → 주문완료 메뉴 활성화용
    private boolean firstOrder = true;

    public void run() {
        menu.menu();// 메뉴 데이터 초기화

        
        while (true) {
            menu.showMainMenu(); 
            String category = menu.pickMainMenu(scanner);

            if (category.equals("exit")) {// 주문완료 선택 시
                if (cart.isEmpty()) {
                    System.out.println("장바구니가 비어있습니다. 먼저 주문해주세요.");
                    continue;
                }
                processPayment(); // 주문완료 시 결제 진행
                continue;
            }
            
            // 카테고리 내부 반복 (음료 선택 → 옵션 선택 → 수량 입력 → 장바구니 담기)
            while (true) {
                menu.showItemsCategory(category);
                Drink drink = menu.pickDrink(scanner, category);
                if (drink == null) break;// 뒤로가기 처리

                // 온도 선택
                Temp temp = new Temp(drink.getTempOptions(), drink.getOptions(), scanner);
                if (!drink.getTempOptions().isEmpty()) {
                    temp.pick();
                    if (temp.getChoice() == null) break;// 뒤로가기 선택 시
                }

                // 토핑 선택
                Topping topping = new Topping(drink.getToppings(), drink.getOptions(), scanner);
                if (!drink.getToppings().isEmpty()) {
                    topping.pick();
                    if (topping.isBackRequested()) break;// 뒤로가기 선택 시
                }

                // 수량 입력
                int quantity = askQuantity();

                // 선택 옵션 적용
                if (!drink.getTempOptions().isEmpty()) drink.applyTemp(temp);
                if (!drink.getToppings().isEmpty()) drink.applyToppings(topping);

                // 장바구니에 담기
                cart.add(drink, quantity);

                // 첫 주문 시에만 주문완료 버튼 없이
                if (firstOrder) {
                    firstOrder = false;
                    menu.enableExit();
                }
                // 주문 후 "추가주문 여부" 물어보기
                int action = askNextAction();
                if (action == 1) {
                    // 추가주문 → 메인화면으로
                    break; 
                } else if (action == 2) {
                    // 결제 진행 → 바로 결제창
                    processPayment();
                    break; 
                }
                
         

            }//while2 end
        }//while1 end
    }//run end

    // 수량 입력 유효성 검사
    private int askQuantity() {
        System.out.print("수량을 입력해주세요: ");
        while (true) {
            try {
                int n = Integer.parseInt(scanner.nextLine());
                if (n > 0) return n;// 1개 이상
                System.out.print("1 이상 입력해주세요: ");
            } catch (NumberFormatException e) {
                System.out.print("숫자로 입력해주세요: ");
            }
        }
    }//askQuantity end
    
    private int askNextAction() {
        System.out.println("\n[추가 주문 여부]");
        System.out.println("1. 추가 주문");
        System.out.println("2. 결제 진행");

        while (true) {
            System.out.print("번호를 입력하세요: ");
            String input = scanner.nextLine().trim();
            if (input.equals("1") || input.equals("2")) {
                return Integer.parseInt(input);
            }
            System.out.println("잘못된 입력입니다. 1 또는 2를 선택해주세요.");
        }
    }

    private void processPayment() {
        String phone;
        Cust cust;
        
        // 결제 수단 선택
        Payment selectedPayment = choosePayment();
        int totalAmount = cart.getTotalAmount();
        
        while (true) { // 전화번호 입력 및 검증
            System.out.print("전화번호를 입력해주세요 (11자리): ");
            phone = scanner.nextLine().trim();
            if (phone.length() == 11 && phone.matches("\\d{11}")) break;
            System.out.println("전화번호는 11자리 숫자여야 합니다.");
        }
        // 기존 고객 여부 확인 후 불러오기/생성
        if (customerDB.containsKey(phone)) {
            cust = customerDB.get(phone);
        } else {
            cust = new Cust(phone);
            customerDB.put(phone, cust);
        }


        // Cust에서 직접 포인트 계산 + 적립
        int earnedPoints = cust.earnPoints(totalAmount);

        // 장바구니 결제 완료 처리 및 영수증 저장
        cart.checkout(selectedPayment, earnedPoints, true);
        cart.printReceipt();
        System.out.println("이번 결제 적립 포인트: " + earnedPoints);
        System.out.println("현재 보유 포인트: " + cust.getPoints());
        
        // 포인트 밑에 대기번호 출력
        System.out.println("대기번호 : " + Cart.getOrderCounter());
        Cart.incrementOrderCounter();
        
        // 주문완료 버튼 끄기
        firstOrder = true;
        menu.disableExit();
        
    }//processPayment end

    // 결제수단 선택
    private Payment choosePayment() {
        System.out.println("\n[결제 수단 선택]");
        for (Payment p : Payment.values()) {
            System.out.println((p.ordinal() + 1) + ". " + p.getLabel());
        }

        
        while (true) {
            System.out.print("번호 입력: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= Payment.values().length) {
                    Payment selected = Payment.values()[choice - 1];
                    System.out.println(selected.getLabel() + " 결제를 선택하셨습니다.");
                    return selected;
                } else {
                    System.out.println("잘못된 번호입니다. 다시 선택하세요.");
                }
            } catch (NumberFormatException e) {
                System.out.println("숫자로 입력해주세요.");
            }
        }
    }//processPayment
    
}//Order class end


// -------------------- Menu --------------------
class Menu {
    private Map<String, LinkedHashMap<String, Integer>> menuData;
    private Map<String, LinkedHashMap<String, Integer>> optionData;
    private boolean showExit = false;// 주문완료 버튼 노출 여부

    // 메뉴 데이터 초기화 (Item 클래스에서 불러옴)
    public void menu() {
        this.menuData = Item.getMenu();
        this.optionData = Item.getOption();
    }
    
    // 주문완료 버튼 활성화
    public void enableExit() {
        showExit = true;
    }
    // 주문완료 버튼 비활성화
    public void disableExit() {
        showExit = false;
    }

    //메인 메뉴 출력
    public void showMainMenu() {
        System.out.println("\n[메인 메뉴]");
        int i = 1;
        for (String category : menuData.keySet()) {
            System.out.println(i++ + ". " + category);
        }
        if (showExit) System.out.println(i + ". 주문완료");
    }
    //메인 메뉴 선택
    public String pickMainMenu(Scanner scanner) {
        System.out.print("메뉴 번호를 입력하세요: ");
        String input = scanner.nextLine().trim();

        try {
            int idx = Integer.parseInt(input) - 1;
            List<String> categories = new ArrayList<>(menuData.keySet());
            if (showExit && idx == categories.size()) return "exit";//주문 완료
            if (idx < 0 || idx >= menuData.size()) {
                System.out.println("잘못된 번호입니다.");
                return pickMainMenu(scanner);
            }
            return new ArrayList<>(menuData.keySet()).get(idx);
        } catch (NumberFormatException e) {
            System.out.println("숫자로 입력해주세요.");
            return pickMainMenu(scanner);
        }
    }
    
    // 카테고리 내 메뉴 출력
    public void showItemsCategory(String category) {
        System.out.println("\n[" + category + " 메뉴]");
        LinkedHashMap<String, Integer> items = menuData.get(category);
        int i = 1;
        for (String name : items.keySet()) {
            System.out.println(i++ + ". " + name + " (" + items.get(name) + "원)");
        }
        System.out.println("0. 뒤로가기");
    }

    // 음료 선택 → Drink 객체 생성
    public Drink pickDrink(Scanner scanner, String category) {
        LinkedHashMap<String, Integer> items = menuData.get(category);
        List<String> itemList = new ArrayList<>(items.keySet());

        System.out.print("메뉴 번호 입력 (0 입력 시 뒤로가기): ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx == -1) return null;
            if (idx < 0 || idx >= itemList.size()) return null;

            String itemName = itemList.get(idx);
            int basePrice = items.get(itemName);

            Map<String, Integer> options = optionData.getOrDefault(itemName, new LinkedHashMap<>());
            return new Drink(itemName, basePrice, options);
        } catch (NumberFormatException e) {
            System.out.println("숫자로 입력해주세요.");
            return null;
        }
    }
 
}//Menu class end

// -------------------- Drink --------------------
class Drink {
    private String name;
    private int basePrice;
    private Map<String, Integer> options;
    private Map<String, Integer> selectedToppings = new LinkedHashMap<>();
    private String temperature = "";
    private int extraCost = 0;

    public Drink(String name, int basePrice, Map<String, Integer> options) {
        this.name = name;
        this.basePrice = basePrice;
        this.options = options;
    }

    //온도 옵션
    public List<String> getTempOptions() {
        List<String> temps = new ArrayList<>();
        if (options.containsKey("ice")) temps.add("ice");
        if (options.containsKey("hot")) temps.add("hot");
        return temps;
    }

    //토핑 옵션
    public List<String> getToppings() {
        List<String> toppings = new ArrayList<>();
        for (String key : options.keySet()) {
            if (!key.equals("ice") && !key.equals("hot")) toppings.add(key);
        }
        return toppings;
    }

    public Map<String, Integer> getOptions() { return options; }
    //적용된 온도
    public void applyTemp(Temp temp) {
        temperature = temp.getChoice();
        extraCost += temp.getPrice();
        selectedToppings.put(temperature, 1);
    }
    //적용된 토핑
    public void applyToppings(Topping picker) {
        Map<String, Integer> tops = picker.getPicked();
        for (String top : tops.keySet()) {
            int count = tops.get(top);
            selectedToppings.put(top, selectedToppings.getOrDefault(top, 0) + count);
            extraCost += count * options.get(top);
        }
    }

    public String getFullName() {
        if (selectedToppings.isEmpty()) return name;
        StringBuilder sb = new StringBuilder(name + " (");
        for (Map.Entry<String, Integer> entry : selectedToppings.entrySet()) {
            sb.append(entry.getKey()).append("x").append(entry.getValue()).append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append(")");
        return sb.toString();
    }

    public int getTotalPrice() { return basePrice + extraCost; }
    
}//Drink class end

//------------------------------cart---------------------------------
class Cart {
    private Map<String, Integer> itemCount = new LinkedHashMap<>();
    private Map<String, Integer> itemPrice = new LinkedHashMap<>();

    private Map<String, Integer> lastReceipt = new LinkedHashMap<>();
    private Map<String, Integer> lastPrices  = new LinkedHashMap<>();
    private int lastTotal = 0;
    private Payment lastPayment = null;
    private int lastEarnedPoints = 1;
    
    public static int getOrderCounter() {
        return orderCounter;
    }
    public static void incrementOrderCounter() {
        orderCounter++;
    }

    // 대기번호를 모든 주문 공용 -> static으로 선언
    private static int orderCounter = 1;
    
    //장바구니에 아이템 추가
    public void add(Drink drink, int quantity) {
        String name = drink.getFullName();
        int price = drink.getTotalPrice();
        itemCount.put(name, itemCount.getOrDefault(name, 0) + quantity);
        itemPrice.put(name, price);
    }

    //장바구니 비었는지 확인
    public boolean isEmpty() { return itemCount.isEmpty(); }

    //장바구니 총액 계산
    public int getTotalAmount() {
        int total = 0;
        for (String name : itemCount.keySet()) {
            total += itemCount.get(name) * itemPrice.get(name);
        }
        return total;
    }

    //결제 처리->영수증 데이터 저장
    public void checkout(Payment payment, int earnedPoints, boolean clearCart) {
        lastReceipt.clear();
        lastPrices.clear();
        lastReceipt.putAll(itemCount);
        lastPrices.putAll(itemPrice);
        lastTotal = getTotalAmount();
        lastPayment = payment;
        lastEarnedPoints = earnedPoints;

        if (clearCart) {//결제후 카드 비우기
            itemCount.clear();
            itemPrice.clear();
        }
    }

    //영수증 출력
    public void printReceipt() {
        if (lastReceipt.isEmpty()) {
            System.out.println("영수증 내역이 없습니다.");
            return;
        }
        System.out.println("\n[영수증]");
        
        
        for (String name : lastReceipt.keySet()) {
            int count = lastReceipt.get(name);
            int unit = lastPrices.getOrDefault(name, 0);
            int itemTotal = count * unit;
            System.out.printf("- %s x%d = %d원 (단가: %d원)\n", name, count, itemTotal, unit);
        }
        System.out.println("-----------------------------------------------------------------");
        System.out.println("총 결제 금액: " + lastTotal + "원");
        System.out.println("결제 수단: " + (lastPayment != null ? lastPayment.getLabel() : "N/A"));
        
    }
    
}//Cart class end

//-------------------------------Temp-----------------------------
class Temp {
    private final List<String> choices;
    private final Map<String, Integer> prices;
    private final Scanner scanner;

    private String choice = null;
    private int price = 0;

    public Temp(List<String> choices, Map<String, Integer> prices, Scanner scanner) {
        this.choices = choices;
        this.prices = prices;
        this.scanner = scanner;
    }

    // 온도 선택이 필요한 경우만 true
    public boolean needPick() {
        return choices.contains("ice") && choices.contains("hot") && choices.size() == 2;
    }

    // 온도 선택
    public void pick() {
        if (!needPick()) return;

        System.out.println("\n[온도 선택]");
        for (int i = 0; i < choices.size(); i++) {
            String name = choices.get(i);
            int cost = prices.getOrDefault(name, 0);
            System.out.println((i + 1) + ". " + name + (cost > 0 ? " (+" + cost + "원)" : ""));
        }
        System.out.println("0. 뒤로가기");

        while (true) {
            System.out.print("옵션 번호를 입력하세요: ");
            String input = scanner.nextLine().trim();
            if (input.equals("0")) { choice = null; return; }

            try {
                int index = Integer.parseInt(input) - 1;
                if (index < 0 || index >= choices.size()) { System.out.println("번호가 잘못되었습니다."); continue; }
                choice = choices.get(index);
                price = prices.getOrDefault(choice, 0);
                break;
            } catch (NumberFormatException e) {
                System.out.println("숫자만 입력해주세요.");
            }
        }
    }

    public String getChoice() { return choice; }
    public int getPrice() { return price; }
    
}//Temp class end

// -------------------- Topping --------------------
class Topping {
    private final List<String> toppings;
    private final Map<String, Integer> prices;
    private final Scanner scanner;

    private final Map<String, Integer> picked = new LinkedHashMap<>();
    private boolean backRequested = false;

    public Topping(List<String> toppings, Map<String, Integer> prices, Scanner scanner) {
        this.toppings = toppings;
        this.prices = prices;
        this.scanner = scanner;
    }
    // 토핑 선택
    public void pick() {
        if (toppings.isEmpty()) return;

        System.out.println("\n[추가 토핑 선택]");
        for (int i = 0; i < toppings.size(); i++) {
            String name = toppings.get(i);
            int price = prices.getOrDefault(name, 0);
            System.out.println((i + 1) + ". " + name + " (+" + price + "원)");
        }
        System.out.println((toppings.size() + 1) + ". 선택 안 함");
        System.out.println("0. 뒤로가기");
        System.out.println("쉼표로 중복 선택 가능합니다. (예: 1,2,2)");

        while (true) {
            System.out.print("토핑 번호를 입력해 주세요: ");
            String input = scanner.nextLine().trim();
            if (input.equals("0")) { picked.clear(); backRequested = true; return; }
            if (input.isEmpty() || input.equals(String.valueOf(toppings.size() + 1))) break;

            try {
                String[] selections = input.split(",");
                for (String sel : selections) {
                    int index = Integer.parseInt(sel.trim()) - 1;
                    if (index < 0 || index >= toppings.size()) throw new NumberFormatException();
                    String topping = toppings.get(index);
                    picked.put(topping, picked.getOrDefault(topping, 0) + 1);
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("잘못된 입력입니다. 숫자로 다시 입력해주세요.");
            }
        }
    }

    public Map<String, Integer> getPicked() { return picked; }
    public boolean isBackRequested() { return backRequested; }
}//Topping end
