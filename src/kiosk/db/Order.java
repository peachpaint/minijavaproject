package kiosk.db;


import java.util.*;

public class Order {
    private Scanner scanner = new Scanner(System.in);
    private Menu menu = new Menu();
    private Cart cart = new Cart();

    // DB 연동으로 변경: Map<String, Cust> customerDB 제거
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
                processPayment();
                continue;
            }

            while (true) {
                menu.showItemsCategory(category);
                Drink drink = menu.pickDrink(scanner, category);
                if (drink == null) break;

                Temp temp = new Temp(drink.getTempOptions(), drink.getOptions(), scanner);
                if (!drink.getTempOptions().isEmpty()) {
                    temp.pick();
                    if (temp.getChoice() == null) break;
                }

                Topping topping = new Topping(drink.getToppings(), drink.getOptions(), scanner);
                if (!drink.getToppings().isEmpty()) {
                    topping.pick();
                    if (topping.isBackRequested()) break;
                }

                int quantity = askQuantity();

                if (!drink.getTempOptions().isEmpty()) drink.applyTemp(temp);
                if (!drink.getToppings().isEmpty()) drink.applyToppings(topping);

                cart.add(drink, quantity);

                if (firstOrder) {
                    firstOrder = false;
                    menu.enableExit();
                }
                int action = askNextAction();
                if (action == 1) {
                    break;
                } else if (action == 2) {
                    processPayment();
                    break;
                }
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

        Payment selectedPayment = choosePayment();
        int totalAmount = cart.getTotalAmount();

        while (true) {
            System.out.print("전화번호를 입력해주세요 (11자리): ");
            phone = scanner.nextLine().trim();
            if (phone.length() == 11 && phone.matches("\\d{11}")) break;
            System.out.println("전화번호는 11자리 숫자여야 합니다.");
        }

        // DB에서 고객 정보 자동 조회/생성
        cust = new Cust(phone);

        int earnedPoints = cust.earnPoints(totalAmount);

        cart.checkout(selectedPayment, earnedPoints, true);
        cart.printReceipt();
        System.out.println("이번 결제 적립 포인트: " + earnedPoints);
        System.out.println("현재 보유 포인트: " + cust.getPoints());

        System.out.println("대기번호 : " + Cart.getOrderCounter());
        Cart.incrementOrderCounter();

        firstOrder = true;
        menu.disableExit();
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
