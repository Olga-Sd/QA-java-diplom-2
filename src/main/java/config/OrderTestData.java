package config;

import java.util.List;

public class OrderTestData {

    public static List<String> orderWithIngredientsData = List.of("61c0c5a71d1f82001bdaaa71","61c0c5a71d1f82001bdaaa72",
            "61c0c5a71d1f82001bdaaa76", "61c0c5a71d1f82001bdaaa77");
    public static String orderName = "Фалленианский spicy био-марсианский минеральный бургер";

    public static List<String> orderWithIngredientsData2 = List.of("61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa75",
            "61c0c5a71d1f82001bdaaa76", "61c0c5a71d1f82001bdaaa78");
    public static String orderName2 = "Альфа-сахаридный метеоритный антарианский минеральный бургер";

    public  static List<String> orderWithoutIngredientsData = List.of();

    public  static List<String> orderWithWrongIngredientsHashData = List.of("61c0c5a71d1f82001bdaa","61c0c5a71d1f82001bdaaa72",
            "61c0c5a71d1f82001bdaaa76", "61c0c5a71d1bdaaa77");
}
