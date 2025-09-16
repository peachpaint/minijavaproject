package kiosk.db;

import java.util.LinkedHashMap;
import java.util.Map;

public class Item {
    static Map<String, LinkedHashMap<String, Integer>> menu = new LinkedHashMap<>();
    static Map<String, LinkedHashMap<String, Integer>> option = new LinkedHashMap<>();

    static {
        menuItems();
        toppingOption();
    }

    static void menuItems() {
        LinkedHashMap<String, Integer> coffeMenu = new LinkedHashMap<>();
        coffeMenu.put("아메리카노", 3000);
        coffeMenu.put("에스프레소", 3000);
        coffeMenu.put("카페라떼", 4000);
        coffeMenu.put("카푸치노", 4000);

        LinkedHashMap<String, Integer> latteMenu = new LinkedHashMap<>();
        latteMenu.put("초코라떼", 4500);
        latteMenu.put("녹차라떼", 4500);
        latteMenu.put("딸기라떼", 5000);

        LinkedHashMap<String, Integer> smoothieMenu = new LinkedHashMap<>();
        smoothieMenu.put("딸기스무디", 5500);
        smoothieMenu.put("망고스무디", 5500);

        LinkedHashMap<String, Integer> teaMenu = new LinkedHashMap<>();
        teaMenu.put("캐모마일", 3000);
        teaMenu.put("페퍼민트", 3000);

        menu.put("커피", coffeMenu);
        menu.put("라떼", latteMenu);
        menu.put("스무디", smoothieMenu);
        menu.put("티", teaMenu);
    }

    static void toppingOption() {
        LinkedHashMap<String, Integer> coffeOption = new LinkedHashMap<>();
        coffeOption.put("ice", 0);
        coffeOption.put("hot", 0);
        coffeOption.put("헤이즐넛", 500);
        coffeOption.put("바닐라", 500);

        option.put("아메리카노", coffeOption);
        option.put("에스프레소", coffeOption);
        option.put("카페라떼", coffeOption);
        option.put("카푸치노", coffeOption);

        LinkedHashMap<String, Integer> latteOption = new LinkedHashMap<>();
        latteOption.put("ice", 0);
        latteOption.put("hot", 0);
        latteOption.put("우유", 0);
        latteOption.put("두유", 500);

        option.put("초코라떼", latteOption);
        option.put("녹차라떼", latteOption);
        option.put("딸기라떼", latteOption);

        LinkedHashMap<String, Integer> smoothieOption = new LinkedHashMap<>();
        option.put("딸기스무디", smoothieOption);
        option.put("망고스무디", smoothieOption);

        LinkedHashMap<String, Integer> teaOption = new LinkedHashMap<>();
        teaOption.put("ice", 0);
        teaOption.put("hot", 0);

        option.put("캐모마일", teaOption);
        option.put("페퍼민트", teaOption);
    }

    public static Map<String, LinkedHashMap<String, Integer>> getMenu() {
        return menu;
    }
    public static Map<String, LinkedHashMap<String, Integer>> getOption() {
        return option;
    }
}



