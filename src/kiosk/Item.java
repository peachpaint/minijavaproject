package kiosk;


import java.util.LinkedHashMap;
import java.util.Map;

public class Item {
	
	
	public void runningItem() {//Item 클래서에서의 호출메서드
		menuItems();
		toppingOption();
		
	}
	public static Map<String, LinkedHashMap<String, Integer>> getMenu() {
        return menu;
    }
	public static Map<String, LinkedHashMap<String, Integer>> getOption() {
        return option;
    }
	
	//menu Map
	private static Map<String, LinkedHashMap<String, Integer>> menu = new LinkedHashMap<>();
	//sub menu(menuItems)
	static void menuItems() {
	
		//coffeMenu
		LinkedHashMap<String, Integer> coffeMenu = new LinkedHashMap<>();
		coffeMenu.put("아메리카노", 3000);
		coffeMenu.put("에스프레소", 3000);
		coffeMenu.put("카페라떼", 4000);
		coffeMenu.put("카푸치노", 4000);
		
		//latteMenu
		LinkedHashMap<String, Integer> latteMenu = new LinkedHashMap<>();
		latteMenu.put("초코라떼", 4500);
		latteMenu.put("녹차라뗴", 4500);
		latteMenu.put("딸기라떼", 5000);
		
		//smoothieMenu
		LinkedHashMap<String, Integer> smoothieMenu = new LinkedHashMap<>();
		smoothieMenu.put("딸기스무디", 5500);
		smoothieMenu.put("망고스무디", 5500);
		
		//teaMenu
		LinkedHashMap<String, Integer> teaMenu = new LinkedHashMap<>();
		teaMenu.put("캐모마일", 3000);
		teaMenu.put("페퍼민트", 3000);
	
		//mainMenu
		menu.put("커피", coffeMenu);
		menu.put("라떼", latteMenu);
		menu.put("스무디", smoothieMenu);
		menu.put("티", teaMenu);
		
	}
	
	//toppingOption(option)
	private static Map<String, LinkedHashMap<String, Integer>> option = new LinkedHashMap<>();//HashMap -> LinkedHashMap으로 변경
	static void toppingOption() {
		//coffeOption
		LinkedHashMap<String, Integer> coffeOption = new LinkedHashMap<>();
		coffeOption.put("Ice", 0);
		coffeOption.put("hot", 0);
		coffeOption.put("헤이즐넛", 500);
		coffeOption.put("바닐라", 500);
		
		option.put("아메리카노", coffeOption);
		option.put("에스프레소", coffeOption);
		option.put("카페라떼", coffeOption);
		option.put("카푸치노", coffeOption);
		
		//latteOption
		LinkedHashMap<String, Integer> latteOption = new LinkedHashMap<>();
		latteOption.put("Ice", 0);
		latteOption.put("hot", 0);
		latteOption.put("우유", 0);
		latteOption.put("두유", 500);
		
		option.put("초코라떼", latteOption);
		option.put("녹차라뗴", latteOption);
		option.put("딸기라떼", latteOption);
		
		//smoothieOption
		LinkedHashMap<String, Integer> smoothieOption = new LinkedHashMap<>();
		//스무디토핑 없음
		
		option.put("딸기스무디", smoothieOption);
		option.put("망고스무디", smoothieOption);
		
		//teaOption
		LinkedHashMap<String, Integer> teaOption = new LinkedHashMap<>();
		teaOption.put("Ice", 0);
		teaOption.put("hot", 0);
		
		option.put("캐모마일", teaOption);
		option.put("페퍼민트", teaOption);
	}
	//toppingOption(option) end
	
	
}


