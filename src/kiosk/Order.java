package kiosk;

import java.util.*;

public class Order {// 주문 전체 흐름을 담당하는 클래스
    private Scanner scanner = new Scanner(System.in);
    private Menu menu = new Menu(); // 메뉴 관리 객체
    private Cart cart = new Cart();// 장바구니 관리 객체
    
    
    public Order() {
        menu.menu(); //메뉴 데이터 초기화 (Item 클래스에서 불러오기)
    }
    
    
    public void run() {
    	// 첫 주문 여부 체크
    	boolean firstOrder = true;
    	
        while (true) {//// 키오스크 실행 루프
        	// 1. 메인 메뉴 보여주기
            menu.showMainMenu();
            
            // 2. 카테고리 선택
            String category = menu.pickMainCategory(scanner);
            if (category.equals("exit")) break;
            
            while (true) {// 카테고리 루프 시작

            	// 3. 해당 카테고리 안 메뉴 보여주기
            	menu.showItemsInCategory(category);
            
            	// 4. 음료 선택
            	Drink drink = menu.pickDrink(scanner, category);
            	// 0 누르면 카테고리 선택으로 돌아감
            	if (drink == null) continue;

            	// 5. 온도 옵션 선택 (Ice/Hot)
            	TempPicker tempPicker = new TempPicker(drink.getTempOptions(), drink.getOptions(), scanner);
            	tempPicker.pick();
            	//토핑에서 뒤로가기 → 카테고리로 이동
            	if (tempPicker.getChoice() == null) break; 

            	// 6. 추가 토핑 선택
            	ToppingPicker toppingPicker = new ToppingPicker(drink.getExtraToppings(), drink.getOptions(), scanner);
            	toppingPicker.pick();
            	//토핑에서 뒤로가기 → 카테고리로 이동
            	if (toppingPicker.isBackRequested())break; 

            	// 7. 수량 입력
            	int quantity = askQuantity();

            	// 8. 음료에 옵션 적용 (온도 + 토핑)
            	drink.applyTemp(tempPicker);
            	drink.applyToppings(toppingPicker);

            	// 9. 장바구니에 추가
            	cart.add(drink, quantity);
            
            	// 첫 주문 후 종료 옵션 활성화
            	if (firstOrder) {
            		firstOrder = false;
            		menu.enableExitOption(); 
            	}
            	// 주문 완료 후 메인 메뉴로 이동
            	break;
            } // 카테고리 while end
        }//키오스크 실행 while end
        
        // 10. 최종 장바구니 요약 출력
        cart.showSummary();
        
    }//run() end

    // 수량을 입력하는  메서드
    private int askQuantity() {
        System.out.print("수량을 입력해주세요: ");
        while (true) {
            try {
                int n = Integer.parseInt(scanner.nextLine());
                if (n > 0) return n;
                System.out.print("1 이상 입력해주세요: ");
            } catch (NumberFormatException e) {
                System.out.print("숫자로 입력해주세요: ");
            }
        }
    }//askQuantity() end
    
}//Order end


// 메뉴 데이터 관리 클래스
class Menu {
    // 카테고리별 메뉴이름, 가격
    private Map<String, LinkedHashMap<String, Integer>> menuData;
    // 아이템별 옵션, 가격
    private Map<String, LinkedHashMap<String, Integer>> optiondata;
    
    // 첫 번째 주문에서는 false
    private boolean showExitOption = false;

    // Item 클래스에서 메뉴와 옵션 데이터를 불러오기
    public void menu() {
    	 this.menuData = Item.getMenu();
    	 this.optiondata = Item.getOption();  	
    }
    
    // 두 번째 주문부터 종료 선택 가능
    public void enableExitOption() {
        showExitOption = true; 
    }
    
    // 메인 카테고리 메뉴 보여주기
    public void showMainMenu() {
        System.out.println("\n[메인 메뉴]");
        int i = 1;
        for (String category : menuData.keySet()) {
            System.out.println(i++ + ". " + category);
        }
        // 두 번째 주문부터 종료 선택 가능
        if (showExitOption) {
        	System.out.println(i + ". 주문완료");
        }
    }
    
    // 카테고리 선택 입력 받기
    public String pickMainCategory(Scanner scanner) {
        System.out.print("카테고리 번호를 입력하세요: ");
        String input = scanner.nextLine().trim();

        try {
            int idx = Integer.parseInt(input) - 1;
            List<String> categories = new ArrayList<>(menuData.keySet());
 
            // 주문완료 번호
            if (showExitOption && idx == categories.size()) {
                return "exit";
            }
            
            if (idx < 0 || idx >= menuData.size()) {
                System.out.println("잘못된 번호입니다.");
                return pickMainCategory(scanner);
            }
            return new ArrayList<>(menuData.keySet()).get(idx);
        } catch (NumberFormatException e) {
            System.out.println("숫자로 입력해주세요.");
            return pickMainCategory(scanner);
        }
    }
    
    // 카테고리 안의 메뉴 출력
    public void showItemsInCategory(String category) {
        System.out.println("\n[" + category + " 메뉴]");
        LinkedHashMap<String, Integer> items = menuData.get(category);
        int i = 1;
        for (String name : items.keySet()) {
            System.out.println(i++ + ". " + name + " (" + items.get(name) + "원)");
        }
        //뒤로가기
        System.out.println("0. 뒤로가기");
    }
    
    // 음료 선택 후 Drink 객체 생성
    public Drink pickDrink(Scanner scanner, String category) {
        LinkedHashMap<String, Integer> items = menuData.get(category);
        List<String> itemList = new ArrayList<>(items.keySet());
        
        System.out.print("메뉴 번호 입력: ");
        System.out.print("메뉴 번호 입력 (0 입력 시 뒤로가기): ");
        
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            // 0 입력 시 뒤로가기
            if (idx == -1) return null; 
            if (idx < 0 || idx >= itemList.size()) return null;

            String itemName = itemList.get(idx);
            int basePrice = items.get(itemName);

            //옵션은 Item.option에서 가져오기
            Map<String, Integer> options = optiondata.getOrDefault(itemName, new LinkedHashMap<>());

            return new Drink(itemName, basePrice, options);
        } catch (NumberFormatException e) {
            System.out.println("숫자로 입력해주세요.");
            return null;
        }
    }
    
}//Menu end

//실제 주문되는 음료 클래스
class Drink {
    private String name;// 메뉴 이름
    private int basePrice;// 기본 가격
    private Map<String, Integer> options; // 전체 토핑 옵션 (온도 + 토핑)
    private Map<String, Integer> selectedToppings = new LinkedHashMap<>();// 사용자가 고른 옵션
    private String temperature = "";// 온도 (ice/hot)
    private int extraCost = 0;// 추가 금액

    public Drink(String name, int basePrice, Map<String, Integer> options) {
        this.name = name;
        this.basePrice = basePrice;
        this.options = options;
    }

    // 온도 옵션만 필터링해서 리스트 반환
    public List<String> getTempOptions() {
        List<String> temps = new ArrayList<>();
        if (options.containsKey("ice")) temps.add("ice");
        if (options.containsKey("hot")) temps.add("hot");
        return temps;
    }

    // 토핑 옵션만 필터링해서 리스트 반환
    public List<String> getExtraToppings() {
        List<String> toppings = new ArrayList<>();
        for (String key : options.keySet()) {
            if (!key.equals("ice") && !key.equals("hot")) {
                toppings.add(key);
            }
        }
        return toppings;
    }

    public Map<String, Integer> getOptions() {
        return options;
    }

    // 선택된 온도 적용
    public void applyTemp(TempPicker temp) {
        temperature = temp.getChoice();
        extraCost += temp.getPrice();
        selectedToppings.put(temperature, 1);
    }

    // 선택된 토핑 적용
    public void applyToppings(ToppingPicker picker) {
        Map<String, Integer> tops = picker.getPicked();
        for (String top : tops.keySet()) {
            int count = tops.get(top);
            selectedToppings.put(top, selectedToppings.getOrDefault(top, 0) + count);
            extraCost += count * options.get(top);
        }
    }

    // 최종 이름
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

    // 최종 가격 
    public int getTotalPrice() {
        return basePrice + extraCost;
    }
}//Drink end

//장바구니 관리 클래스
class Cart {
    private Map<String, Integer> itemCount = new LinkedHashMap<>();
    private Map<String, Integer> itemPrice = new LinkedHashMap<>();

    // 장바구니에 담기
    public void add(Drink drink, int quantity) {
        String name = drink.getFullName();
        int price = drink.getTotalPrice();

        itemCount.put(name, itemCount.getOrDefault(name, 0) + quantity);
        itemPrice.put(name, price);
    }

    // 장바구니 요약 출력
    public void showSummary() {
        System.out.println("\n[장바구니]");
        int total = 0;
        for (String name : itemCount.keySet()) {
            int count = itemCount.get(name);
            int price = itemPrice.get(name);
            int itemTotal = count * price;
            System.out.printf("- %s x%d = %d원\n", name, count, itemTotal);
            total += itemTotal;
        }
        System.out.println("총 결제 금액: " + total + "원");
    }
}//Cart end

//온도 선택 클래스
class TempPicker {
	// 선택 가능한 온도 옵션
    private final List<String> choices;
    private final Map<String, Integer> prices;
    private final Scanner scanner;

    private String choice = null;
    private int price = 0;

    // 온도 옵션 이름 리스트와 가격 맵을 같이 받음
    public TempPicker(List<String> choices, Map<String, Integer> prices, Scanner scanner) {
        this.choices = choices;
        this.prices = prices;
        this.scanner = scanner;
    }
    
    // ice/hot 둘 다 있을 때만 선택 필요
    public boolean needsPick() {
        return choices.contains("ice") && choices.contains("hot") && choices.size() == 2;
    }

    // 사용자에게 온도 선택 입력 받기
    public void pick() {
        if (!needsPick()) return;

        System.out.println("\n[온도 선택]");
        for (int i = 0; i < choices.size(); i++) {
            String name = choices.get(i);
            int cost = prices.getOrDefault(name, 0);
            System.out.println((i + 1) + ". " + name + (cost > 0 ? " (+" + cost + "원)" : ""));
        }
        System.out.println("→ 반드시 하나를 선택하세요. 중복 선택은 불가합니다.");
        // 뒤로가기
        System.out.println("0. 뒤로가기"); 

        while (true) {
            System.out.print("옵션 번호를 입력하세요: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("하나의 옵션을 반드시 선택해야 합니다.");
                continue;
            }
            // 0 입력 시 뒤로가기
            if (input.equals("0")) {
                choice = null;
                return;
            }

            try {
                String[] parts = input.split(",");
                if (parts.length > 1) {
                    System.out.println("하나만 선택해 주세요.");
                    continue;
                }

                int index = Integer.parseInt(parts[0].trim()) - 1;
                if (index < 0 || index >= choices.size()) {
                    System.out.println("번호가 잘못되었습니다.");
                    continue;
                }

                choice = choices.get(index);
                price = prices.getOrDefault(choice, 0);
                break;

            } catch (NumberFormatException e) {
                System.out.println("숫자만 입력해 주세요. 예: 1 또는 2");
            }
        }
    }//pick() end

    public String getChoice() {
        return choice;
    }

    public int getPrice() {
        return price;
    }
}//TempPicker end


//사용자에게 온도 선택 입력 받기
class ToppingPicker {
    private final List<String> toppings;
    private final Map<String, Integer> prices;
    private final Scanner scanner;

    private final Map<String, Integer> picked = new LinkedHashMap<>();
    private int cost = 0;
    // 뒤로가기 여부
    private boolean backRequested = false; 

    public ToppingPicker(List<String> toppings, Map<String, Integer> prices, Scanner scanner) {
        this.toppings = toppings;
        this.prices = prices;
        this.scanner = scanner;
    }

    public void pick() {
        if (toppings.isEmpty()) {
            return;
        }

        System.out.println("\n[추가 토핑 선택]");
        for (int i = 0; i < toppings.size(); i++) {
            String name = toppings.get(i);
            int price = prices.getOrDefault(name, 0);
            System.out.println((i + 1) + ". " + name + " (+" + price + "원)");
            
        }

        System.out.println((toppings.size() + 1) + ". 선택 안 함");
        // 0 = 뒤로가기 
        System.out.println("0. 뒤로가기");
        System.out.println("→ 쉼표로 중복 선택 가능합니다. (예: 1,2,2)");

        while (true) {
            System.out.print("토핑 번호를 입력해 주세요: ");
            String input = scanner.nextLine().trim();
            
            // 0 입력 시 뒤로가기
            if (input.equals("0")) {
                picked.clear();
                backRequested = true;
                return;
            }

            if (input.isEmpty() || input.equals(String.valueOf(toppings.size() + 1))) {
                System.out.println("선택 안 함을 선택했습니다.");
                break;
            }

            try {
                String[] selections = input.split(",");

                for (String sel : selections) {
                    int index = Integer.parseInt(sel.trim()) - 1;
                    if (index < 0 || index >= toppings.size()) {
                        throw new NumberFormatException();
                    }

                    String topping = toppings.get(index);
                    picked.put(topping, picked.getOrDefault(topping, 0) + 1);
                    cost += prices.getOrDefault(topping, 0);
                }
                break;

            } catch (NumberFormatException e) {
                System.out.println("잘못된 입력입니다. 숫자로만 다시 입력해 주세요.");
            }
        }
    }

    public Map<String, Integer> getPicked() {
        return picked;
    }
    //  뒤로가기 여부 확인
    public boolean isBackRequested() {
        return backRequested;
    }
}//ToppingPicker end
