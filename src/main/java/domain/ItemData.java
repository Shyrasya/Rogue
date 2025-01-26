package domain;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class ItemData {

    /**
     * Объект типа Random, который используется для генерации случайных чисел
     */
    private static final Random rand = new Random();
    /**
     * Список со всеми имеющимися в игре свитками
     */
    public static List<Item> scrolls = new ArrayList<>();

    /**
     * Список со всеми имеющимися в игре зельями
     */
    public static List<Item> potions = new ArrayList<>();

    /**
     * Список со всей имеющейся в игре едой
     */
    public static List<Item> foods = new ArrayList<>();

    /**
     * Список со всеми имеющимися в игре оружиями
     */
    public static List<Item> weapons = new ArrayList<>();

    static {
        scrolls.add(new Scroll("Древний свиток", 0, 0, 25));
        scrolls.add(new Scroll("Офер", 0, 1, 0));
        scrolls.add(new Scroll("Кровавый свиток", 3, 0, 0));
        scrolls.add(new Scroll("Грязный свиток", 1, 0, 0));
        scrolls.add(new Scroll("Золотой свиток", 0, 2, 0));

        potions.add(new Potion("Розовое зелье", 5, 0, 0));
        potions.add(new Potion("Квас", 10, 0, 0));
        potions.add(new Potion("Вино", 0, 15, 0));
        potions.add(new Potion("Зеленое зелье", 5, 5, 0));
        potions.add(new Potion("Пиво", 0, 9, 0));

        foods.add(new Food("Джекфрут", 40));
        foods.add(new Food("Груша", 30));
        foods.add(new Food("Сало", 60));
        foods.add(new Food("Эчпочмак", 50));
        foods.add(new Food("Чипсы", 20));

        weapons.add(new Weapon("Топор", 10));
        weapons.add(new Weapon("Меч", 12));
        weapons.add(new Weapon("Булава", 15));
        weapons.add(new Weapon("Катана", 18));
        weapons.add(new Weapon("Кинжал", 7));
    }

    /**
     * Метод для случайного получения одного из 4х существующих типов предметов в игре
     *
     * @return Строка с названием типа предмета
     */
    public static String getRandomTypeItem() {
        String[] types = {"Scroll", "Potion", "Weapon", "Food"};
        return types[rand.nextInt(types.length)];
    }

    /**
     * Метод для случайного получения предмета из заданного типа предметов
     *
     * @param type Тип получаемого предмета
     * @return Cлучайно выбранный объект класса Item
     */
    public static Item getRandomItem(String type) {
        List<Item> selectedList = null;
        Item newItem = null;
        switch (type) {
            case "Scroll" -> selectedList = scrolls;
            case "Food" -> selectedList = foods;
            case "Weapon" -> selectedList = weapons;
            case "Potion" -> selectedList = potions;
        }
        if (selectedList != null && !selectedList.isEmpty()) {
            int elementNumber = rand.nextInt(selectedList.size());
            Item selectedItem = selectedList.get(elementNumber);
            if (selectedItem instanceof Scroll) {
                newItem = new Scroll((Scroll) selectedItem);
            } else if (selectedItem instanceof Food) {
                newItem = new Food((Food) selectedItem);
            } else if (selectedItem instanceof Weapon) {
                newItem = new Weapon((Weapon) selectedItem);
            } else if (selectedItem instanceof Potion) {
                newItem = new Potion((Potion) selectedItem);
            }
        }
        return newItem;
    }
}
