package kiosk;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Order {

	static Scanner scanner = new Scanner(System.in);//
	public void  runningOrder() {
		showMenu();
	}
	
	private static Map<String, LinkedHashMap<String, Integer>> menu= Item.getMenu();
	private static Map<String, LinkedHashMap<String, Integer>> option = Item.getOption();
	private static Map<String, Integer> orderMap = new LinkedHashMap<>();
	private static Map<String, Integer> priceMap = new LinkedHashMap<>();
	private static Map<String, Map<String, Integer>> toppingMap = new LinkedHashMap<>();
	
	//menu
	private static void showMenu() {
		
		String mainManu = """
						[ MAIN MENU ]
						1.커피 Coffe
						2.라떼 Latte
						3.스무디 Smoothie
						4.티 Tea
						""";
		System.out.println(mainManu);
		boolean ordering = true;
		boolean firstOrder = false;
		while(ordering) {
		//LinkedHashMap menu를 사용해 등록된 순서를 유지하며 ArrayList로 바꿔서 입력 번호 직접 연결 가능, 번호 선택을 구현하기 위해 index 사용필요 -> List 형태로 작동 정의를 내림 
		List<String> mainMenuItems = new ArrayList<>(menu.keySet());
		if(firstOrder) {	
			System.out.println("\n[ MAIN MENU ]");
			//메뉴목록 출력
			for (int i = 0; i <  mainMenuItems.size(); i++) {//items 리스트의 크기만큼 반복 (0번부터 마지막 인덱스까지)
				//(i + 1) -> 사용자에게는 1번부터 보여주기 위해 인덱스에 +1 / categories.get(i)-> i번째 카테고리 이름   
				System.out.println((i + 1) + ". " + mainMenuItems.get(i));
            }
			System.out.println((mainMenuItems.size() + 1) + ". 주문 완료");
		}
		System.out.println("주문하실 메뉴의 번호를 입력해 주세요 : ");
		int takeMenuNum = orderAmount();// 숫자 입력 받기, choiceNum() 숫자오류 제거 메소드
					
		if(takeMenuNum >=1 && takeMenuNum <=  mainMenuItems.size()) {
			//choice는 입력한 번호를 1부터 시작->List mainMenuItems에서 시작하기 때문에-1 작성
			String selectedMenu = mainMenuItems.get(takeMenuNum - 1);
			showSubMenu(selectedMenu);
			firstOrder = true; // 첫 주문 완료 후 true로 설정
		}else if(takeMenuNum == mainMenuItems.size() +1) {
			ordering =false;
			cart();
		}else {
			System.out.println("잘못된 숫자입지다. 다시 선택하여 주세요. \n");
		}
		}
			
	}

	//mainMenu end
	private static void showSubMenu(String subMenu) {
		//subMenu 에서 해당 목록을 꺼내 List 형태로 저장 
		//LinkedHashMap을 사용->메뉴등록순서를 유지한 상태, 입력받은 번호의 문자열(subMenu)을 menu에서 가져옴
		LinkedHashMap<String, Integer> items = menu.get(subMenu);
		// ArrayList로 만들면 정렬된 상태로 리스트에 담음 -> 번호 선택을 구현
		List<String> subMenuItem = new ArrayList<>(items.keySet());
		while (true) {
			System.out.println("\n["+ subMenu +" MENU ]:");
			for (int i = 0; i < subMenuItem.size(); i++) {
				 String itemName = subMenuItem.get(i);//List subMenuItem에서 알맞은 i번째의 것을 문자열 itemName으로 선언 
	             int price = items.get(itemName);//Map items 에서 itemName의 것을 가져와서 가격으로 선언 
	             System.out.println((i + 1) + "." + itemName + " : " + price + "원");
			}
			System.out.println((subMenuItem.size() + 1) + ".뒤로가기");
				 
			System.out.print("\n주문하실 메뉴의 번호를 입력해 주세요 : ");
			int choiceItem = orderAmount();// 숫자 입력 받기, choiceNum() 숫자오류 제거 메소드
			if (choiceItem >= 1 && choiceItem <= subMenuItem.size()) {
				//choiceItem는 입력한 번호를 1부터 시작->List subMenuItem에서 시작하기 때문에-1 작성
				String selectedItem = subMenuItem.get(choiceItem - 1);
				int basePrice = items.get(selectedItem);//기본 가격은 선택메뉴에 입력된 가격을 가져옴 
				int totalPrice = basePrice;//처음에는 기본 가격만 담음
				String finalItemName = selectedItem;//finalItemName에 저장
					 
				//ToppingsOption
				//토핑옵션이 존재하는 매뉴 확인
				if (option.containsKey(selectedItem)) {
					LinkedHashMap<String, Integer> existOption = option.get(selectedItem);
					List<String> toppingOptions = new ArrayList<>(existOption.keySet());
					System.out.println("\n[토핑 Option]");
					//토핑옵션 출력
					for (int i = 0; i < toppingOptions.size(); i++) {
						System.out.println((i + 1) + "." +toppingOptions.get(i)+ " (+" + existOption.get(toppingOptions.get(i)) + "원)");
					}
					//토핑옵션의 개수가 2개 초과일 경우에만 '선택 안 함' 옵션 출력 ->option이 ice와 hot만 있는 경우, ice선택 경우 얼은 선택사항 추가 필요
					if (toppingOptions.size() > 2) {
						System.out.println((toppingOptions.size() + 1) + ".선택 안 함");
						System.out.println("->쉼표로 중복 선택 가능합니다(예: 1,1,2)");
					}
					System.out.println("\nOption을 선택해 주세요. : ");
					
					String inNum = scanner.nextLine().trim();				
						 
					// 토핑 미선택
					if (inNum.equalsIgnoreCase("n") || inNum.equals(String.valueOf(toppingOptions.size() + 1))) {
						System.out.println("추가 옵션을 선택하지 않았습니다.");
					}else {
						// 중복 선택 허용을 위한 Map 사용
						Map<String, Integer> toppingCount = new LinkedHashMap<>();
						//여러 개의 번호를 입력했을 경우 , 를 사용 하여 분리함	
						String[] selections = inNum.split(",");	
						for (String sel : selections) {
							try {//오류가 발생할 수 있는 코드를 작성
						        int toppingIndex = Integer.parseInt(sel.trim()) - 1;//0부터 시작하는 인덱스의 경우
						        if (toppingIndex >= 0 && toppingIndex < toppingOptions.size()) {//유효한 범위검증
						            String topping = toppingOptions.get(toppingIndex);
						            toppingCount.put(topping, toppingCount.getOrDefault(topping, 0) + 1);//선택 옵션이 Map에 이미 있으면 그 값을 가져오고, 없다면 0을 반환합니다.
						            totalPrice += existOption.get(topping);
						        }
						    } catch (NumberFormatException e) {
						        // 잘못된 입력은 무시
						    }
						}
					}
				}else { // 해당 메뉴에는 토핑 없음
					System.out.println("이 메뉴는 추가 할 수 있는 토핑이 없습니다.");
				}
				// 수량 입력->1 이상 필수
				int choiceTopping ;
				do {
					System.out.print("메뉴의 수량 입력해주세요(1 이상): ");
					choiceTopping = orderAmount();// 숫자 입력 받기, choiceNum() 숫자오류 제거 메소드
				} while (choiceTopping <= 0);
				// 중복 선택 허용을 위한 Map 사용
				Map<String, Integer> toppingCount = new LinkedHashMap<>();
				// 토핑 선택 후 저장
				if (!toppingCount.isEmpty()) {
				    // 메뉴 이름이 이미 있을 경우, 기존 토핑에 누적 추가
				    Map<String, Integer> existingToppings = toppingMap.getOrDefault(finalItemName, new LinkedHashMap<>());
				    for (String top : toppingCount.keySet()) {
				        int newCount = toppingCount.get(top);
				        existingToppings.put(top, existingToppings.getOrDefault(top, 0) + newCount);
				    }
				    toppingMap.put(finalItemName, existingToppings);
				}

				//ToppingsOption end
				
				// 주문
				 orderMap.put(finalItemName, orderMap.getOrDefault(finalItemName, 0) + choiceTopping);
	             priceMap.put(finalItemName, totalPrice);
	             System.out.println( finalItemName + " " + + choiceTopping + "개 추가되었습니다.");
	             break;
			}else if (choiceItem ==  subMenuItem.size() + 1) {
                break; 
			}else {
                System.out.println("잘못된 입력입니다.");
			}
		}
	}
	//숫자 입력 헬퍼 ->비즈니스로직 
	private static int orderAmount() {
		while(true) {
			try {//오류가 발생할 수 있는 코드를 작성
				return Integer.parseInt(scanner.nextLine().trim());//Integer.parseInt()->문자열을 정수로 변환/scanner.nextLine()->입력한 문자열을 읽는다 /.trim()->문자열 공백을 제거 /
			} catch(NumberFormatException e) {//숫자가 아닌 값을 입력했을 경우 실행
				System.out.print("삼품에 알맞은 숫자로 입력해주세요: ");
			}
		}
	}
	//menu end
	
	// 주문 내역 요약(장바구니)
	private static void cart() {
		int total = 0;
		System.out.println("\n주문 내역 : ");
		for (String item : orderMap.keySet()) {
			Map<String, Integer> toppings = toppingMap.get(item);
			if (toppings != null && !toppings.isEmpty()) {
			    System.out.println("   └ 토핑:");
			    for (String toppingName : toppings.keySet()) {
			        int toppingCount = toppings.get(toppingName);
			        int toppingPrice = option.getOrDefault(item, new LinkedHashMap<>()).getOrDefault(toppingName, 0);
			        System.out.printf("     - %-20s x %2d = %5d원\n", toppingName, toppingCount, toppingCount * toppingPrice);
			    }
			}

		}
		System.out.println("총 결제예정 금액: " + total + "원");
	}

}
