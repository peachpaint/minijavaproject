package kiosk;

import java.util.*;

public class Order {
    private Scanner scanner = new Scanner(System.in);
    private Menu menu = new Menu();
    private Cart cart = new Cart();

    // 전화번호별 고객 관리
    private Map<String, Cust> customerDB = new HashMap<>();
    private boolean firstOrder = true;

    public void run() {
        menu.menu();

        while (true) {
            menu.showMainMenu();
            String category = menu.pickMainMenu(scanner);

            if (category.equals("exit")) {
                if (cart.isEmpty()) {
                    System.out.println("장바구니가 비어있습니다. 먼저 주문해주세요.");
                    continue;
                }
                processPayment(); // 주문완료 시 결제 진행
                continue;
            }

            while (true) {
                menu.showItemsCategory(category);
                Drink drink = menu.pickDrink(scanner, category);
                if (drink == null) break;

                // 온도 선택
                TempPicker tempPicker = new TempPicker(drink.getTempOptions(), drink.getOptions(), scanner);
                if (!drink.getTempOptions().isEmpty()) {
                    tempPicker.pick();
                    if (tempPicker.getChoice() == null) break;
                }

                // 토핑 선택
                ToppingPicker toppingPicker = new ToppingPicker(drink.getToppings(), drink.getOptions(), scanner);
                if (!drink.getToppings().isEmpty()) {
                    toppingPicker.pick();
                    if (toppingPicker.isBackRequested()) break;
                }

                // 수량 입력
                int quantity = askQuantity();

                // 선택 적용
                if (!drink.getTempOptions().isEmpty()) drink.applyTemp(tempPicker);
                if (!drink.getToppings().isEmpty()) drink.applyToppings(toppingPicker);

                cart.add(drink, quantity);

                if (firstOrder) {
                    firstOrder = false;
                    menu.enableExit();
                }
                break;
            }
        }
    }

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
    }

    private void processPayment() {
        String phone;
        Cust cust;
        while (true) {
            System.out.print("결제 전, 고객 전화번호를 입력해주세요 (11자리): ");
            phone = scanner.nextLine().trim();
            if (phone.length() == 11 && phone.matches("\\d{11}")) break;
            System.out.println("전화번호는 11자리 숫자여야 합니다.");
        }

        if (customerDB.containsKey(phone)) {
            cust = customerDB.get(phone);
        } else {
            cust = new Cust(phone);
            customerDB.put(phone, cust);
        }

        Payment selectedPayment = choosePayment();

        int totalAmount = cart.getTotalAmount();
        int earnedPoints = totalAmount / 100; // 1% 적립
        cust.addPoints(earnedPoints);

        cart.checkout(selectedPayment, earnedPoints, true);
        cart.printReceipt();
        System.out.println("현재 포인트: " + cust.getPoints());
    }

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
    }
}

// -------------------- Menu --------------------
class Menu {
    private Map<String, LinkedHashMap<String, Integer>> menuData;
    private Map<String, LinkedHashMap<String, Integer>> optionData;
    private boolean showExit = false;

    public void menu() {
        this.menuData = Item.getMenu();
        this.optionData = Item.getOption();
    }

    public void enableExit() {
        showExit = true;
    }

    public void showMainMenu() {
        System.out.println("\n[메인 메뉴]");
        int i = 1;
        for (String category : menuData.keySet()) {
            System.out.println(i++ + ". " + category);
        }
        if (showExit) System.out.println(i + ". 주문완료");
    }

    public String pickMainMenu(Scanner scanner) {
        System.out.print("메뉴 번호를 입력하세요: ");
        String input = scanner.nextLine().trim();

        try {
            int idx = Integer.parseInt(input) - 1;
            List<String> categories = new ArrayList<>(menuData.keySet());
            if (showExit && idx == categories.size()) return "exit";
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

    public void showItemsCategory(String category) {
        System.out.println("\n[" + category + " 메뉴]");
        LinkedHashMap<String, Integer> items = menuData.get(category);
        int i = 1;
        for (String name : items.keySet()) {
            System.out.println(i++ + ". " + name + " (" + items.get(name) + "원)");
        }
        System.out.println("0. 뒤로가기");
    }

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
}

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

    public List<String> getTempOptions() {
        List<String> temps = new ArrayList<>();
        if (options.containsKey("ice")) temps.add("ice");
        if (options.containsKey("hot")) temps.add("hot");
        return temps;
    }

    public List<String> getToppings() {
        List<String> toppings = new ArrayList<>();
        for (String key : options.keySet()) {
            if (!key.equals("ice") && !key.equals("hot")) toppings.add(key);
        }
        return toppings;
    }

    public Map<String, Integer> getOptions() { return options; }

    public void applyTemp(TempPicker temp) {
        temperature = temp.getChoice();
        extraCost += temp.getPrice();
        selectedToppings.put(temperature, 1);
    }

    public void applyToppings(ToppingPicker picker) {
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
}
//Order end


class Cart {
    private Map<String, Integer> itemCount = new LinkedHashMap<>();
    private Map<String, Integer> itemPrice = new LinkedHashMap<>();

    private Map<String, Integer> lastReceipt = new LinkedHashMap<>();
    private Map<String, Integer> lastPrices  = new LinkedHashMap<>();
    private int lastTotal = 0;
    private Payment lastPayment = null;
    private int lastEarnedPoints = 0;

    // 대기번호를 static으로 선언
    private static int orderCounter = 1;
    
    public void add(Drink drink, int quantity) {
        String name = drink.getFullName();
        int price = drink.getTotalPrice();
        itemCount.put(name, itemCount.getOrDefault(name, 0) + quantity);
        itemPrice.put(name, price);
    }

    public boolean isEmpty() { return itemCount.isEmpty(); }

    public int getTotalAmount() {
        int total = 0;
        for (String name : itemCount.keySet()) {
            total += itemCount.get(name) * itemPrice.get(name);
        }
        return total;
    }

    public void checkout(Payment payment, int earnedPoints, boolean clearCart) {
        lastReceipt.clear();
        lastPrices.clear();
        lastReceipt.putAll(itemCount);
        lastPrices.putAll(itemPrice);
        lastTotal = getTotalAmount();
        lastPayment = payment;
        lastEarnedPoints = earnedPoints;

        if (clearCart) {
            itemCount.clear();
            itemPrice.clear();
        }
    }

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
        // 주문번호 출력
        System.out.println("대기번호 : " + orderCounter);
    }
}
//cart end


class TempPicker {
    private final List<String> choices;
    private final Map<String, Integer> prices;
    private final Scanner scanner;

    private String choice = null;
    private int price = 0;

    public TempPicker(List<String> choices, Map<String, Integer> prices, Scanner scanner) {
        this.choices = choices;
        this.prices = prices;
        this.scanner = scanner;
    }

    public boolean needsPick() {
        return choices.contains("ice") && choices.contains("hot") && choices.size() == 2;
    }

    public void pick() {
        if (!needsPick()) return;

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
}

// -------------------- ToppingPicker --------------------
class ToppingPicker {
    private final List<String> toppings;
    private final Map<String, Integer> prices;
    private final Scanner scanner;

    private final Map<String, Integer> picked = new LinkedHashMap<>();
    private boolean backRequested = false;

    public ToppingPicker(List<String> toppings, Map<String, Integer> prices, Scanner scanner) {
        this.toppings = toppings;
        this.prices = prices;
        this.scanner = scanner;
    }

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
}//ToppingPicker end
