package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Backpack {

    /**
     * Список предметов типа "свитки" в рюкзаке
     */
    private List<Item> scrolls;

    /**
     * Список предметов типа "зелья" в рюкзаке
     */
    private List<Item> potions;

    /**
     * Список предметов типа "еда" в рюкзаке
     */
    private List<Item> foods;

    /**
     * Список предметов типа "оружие" в рюкзаке
     */
    private List<Item> weapons;

    /**
     * Флаг, наличие которого позволяет вывести информацию о заполенености рюкзака данного типа
     */
    private boolean fullBackpackType;

    /**
     * Констркутор класса Backpack
     */
    public Backpack() {
        scrolls = new ArrayList<>();
        potions = new ArrayList<>();
        foods = new ArrayList<>();
        weapons = new ArrayList<>();
        fullBackpackType = false;
    }

    /**
     * Метод для проверки пустоты рюкзака
     *
     * @return true - рюкзак пуст, false - в рюкзаке что-то есть
     */
    public boolean isEmpty() {
        return foods.isEmpty() && potions.isEmpty() && scrolls.isEmpty() && weapons.isEmpty();
    }

    /**
     * Метод для выведения ответной информационной строки на запрос к рюкзаку
     *
     * @param purpose Строка с целью обращения к рюкзаку
     * @param type    Выбранный тип предмета
     * @return Информационная строка для пользователя
     */
    public String checkIsEmptyInBackpack(String purpose, String type) {
        String message = "Предмета этого типа нет в рюкзаке :(";
        switch (type) {
            case "Food" -> {
                if (!(foods.isEmpty()))
                    if (purpose.equals("use"))
                        message = "Что вы хотите съесть? Выбери номер предмета (1 и т.д.) в рюкзаке";
                    else if (purpose.equals("drop")) message = "Какую еду вы хотите выбросить (1 и т.д.) из рюкзака?";
            }
            case "Potion" -> {
                if (!(potions.isEmpty())) {
                    if (purpose.equals("use"))
                        message = "Что вы хотите выпить? Выбери номер предмета (1 и т.д.) в рюкзаке";
                    else if (purpose.equals("drop")) message = "Какое зелье вы хотите выбросить (1 и т.д.) из рюкзака?";
                }
            }
            case "Scroll" -> {
                if (!(scrolls.isEmpty())) {
                    if (purpose.equals("use"))
                        message = "Что вы хотите прочитать? Выбери номер предмета (1 и т.д.) в рюкзаке";
                    else if (purpose.equals("drop"))
                        message = "Какой свиток вы хотите выбросить (1 и т.д.) из рюкзака?";
                }
            }
            case "Weapon" -> {
                if (!(weapons.isEmpty())) {
                    if (purpose.equals("use"))
                        message = "Что вы хотите взять в качестве основного оружия? Выбери номер предмета (1 и т.д.) в рюкзаке";
                    else if (purpose.equals("drop"))
                        message = "Какое оружие вы хотите выбросить (1 и т.д.) из рюкзака?";
                }
            }
        }
        return message;
    }

    /**
     * Проверка на наличие свободного места определенного типа в рюкзаке
     *
     * @param type Проверяемый тип предметов
     * @return true - место под данный тип имеется в рюкзаке, false - места нет
     */
    boolean isItFreeBackpackType(String type) {
        boolean free = false;
        switch (type) {
            case "Scroll" -> free = (scrolls.size() < GameConstants.MAX_NUMBER_EACH_TYPES);
            case "Food" -> free = (foods.size() < GameConstants.MAX_NUMBER_EACH_TYPES);
            case "Weapon" -> free = (weapons.size() < GameConstants.MAX_NUMBER_EACH_TYPES);
            case "Potion" -> free = (potions.size() < GameConstants.MAX_NUMBER_EACH_TYPES);
        }
        return free;
    }

    /**
     * Метод добавления нового предмета в рюкзак
     *
     * @param item Добавляемый предмет
     */
    public void add(Item item) {
        String curItemType = item.getType();
        switch (curItemType) {
            case "Scroll" -> scrolls.add(item);
            case "Food" -> foods.add(item);
            case "Weapon" -> weapons.add(item);
            case "Potion" -> potions.add(item);
        }
    }

    /**
     * Метод получения информации о всем содержимом рюкзака для печати её в специальном поле
     *
     * @return Список строк с содержимым рюкзака
     */
    public List<String> getBackpackContent() {
        List<String> content = new ArrayList<>();
        if (!foods.isEmpty()) {
            content.add("   Еда ('j' - применить)");
            content.addAll(createList(foods));
        }
        if (!weapons.isEmpty()) {
            content.add(" ");
            content.add("   Оружие ('h' - применить)");
            content.addAll(createList(weapons));
        }
        if (!potions.isEmpty()) {
            content.add(" ");
            content.add("   Зелья ('k' - применить)");
            content.addAll(createList(potions));
        }
        if (!scrolls.isEmpty()) {
            content.add(" ");
            content.add("   Свитки ('e' - применить)");
            content.addAll(createList(scrolls));
        }
        return content;
    }

    /**
     * Метод для создания списка с информацией о наличие предметов определенного типа в рюкзаке
     *
     * @param itemList Список выбранного типа предметов в рюкзаке
     * @return Список строк с содержимым рюкзака выбранного типа
     */
    private List<String> createList(List<Item> itemList) {
        List<String> finalList = new ArrayList<>();
        Map<Item, Integer> typeCountMap = new LinkedHashMap<>();
        for (Item item : itemList)
            typeCountMap.put(item, typeCountMap.getOrDefault(item, 0) + 1);

        int index = 1;
        for (Map.Entry<Item, Integer> entry : typeCountMap.entrySet()) {
            Item item = entry.getKey();
            int count = entry.getValue();
            if (count > 1)
                finalList.add(index++ + ") " + count + "x " + item.getBackpackInfo());
            else
                finalList.add(index++ + ") " + item.getBackpackInfo());
        }
        return finalList;
    }

    /**
     * Проверка на нахождения одного вида оружия с определенным именем в рюкзаке
     *
     * @param weaponName Имя оружия
     * @return true - оружие с этим именем одно в рюкзаке, false - нет
     */
    public boolean isWeaponNameAloneInBackpack(String weaponName) {
        int count = 0;
        for (Item weapon : weapons)
            if (weapon.getName().equals(weaponName)) count++;
        return count == 1;
    }

    /**
     * Метод для создания списка уникальных предметов (чтобы понять, какой предмет выбирает в рюкзаке пользователь - цифру)
     *
     * @param curTypeList Список предметов определенного типа
     * @return Карта с предметом и целым числом для дальнейшего выбора предмета из рюкзака с помощью клавиши для пользователя
     */
    private Map<Item, Integer> createUniqueHashMap(List<Item> curTypeList) {
        Map<Item, Integer> uniqueItems = new LinkedHashMap<>();
        int indexMap = 1;
        for (Item item : curTypeList)
            if (!uniqueItems.containsKey(item)) uniqueItems.put(item, indexMap++);
        return uniqueItems;
    }

    /**
     * Проверка на корректную цифру, которую нажал пользователь для взаимодействия с определенным типом из рюкзака
     *
     * @param type                Тип предмета
     * @param intNumberInBackpack Нажатая пользователем цифра, под которым выбирается предмет
     * @return true - пользователь правильно нажал на клавишу - такой предмет под этой цифрой существует, false - нет
     */
    public boolean isCorrectBackpackNumber(String type, int intNumberInBackpack) {
        return switch (type) {
            case "Food" -> (intNumberInBackpack <= createUniqueHashMap(foods).size());
            case "Potion" -> (intNumberInBackpack <= createUniqueHashMap(potions).size());
            case "Scroll" -> (intNumberInBackpack <= createUniqueHashMap(scrolls).size());
            case "Weapon" -> (intNumberInBackpack <= createUniqueHashMap(weapons).size());
            default -> false;
        };
    }

    /**
     * Метод для поиска списка искомого типа
     *
     * @param type Искомый тип предметов
     * @return Список с предметами выбранного типа
     */
    List<Item> getBackpackTypeList(String type) {
        return switch (type) {
            case "Food" -> foods;
            case "Scroll" -> scrolls;
            case "Weapon" -> weapons;
            default -> potions;
        };
    }

    /**
     * Метод для поиска имени предмета под искомым номером в рюкзаке
     *
     * @param curTypeList      Список предметов выбранного типа
     * @param numberInBackpack Выбранный номер, под которым находится предмет
     * @return Строка с именем искомого предмета
     */
    public String findUseItemNameInBackpack(List<Item> curTypeList, int numberInBackpack) {
        Map<Item, Integer> uniqueItems = createUniqueHashMap(curTypeList);
        String curItemName = "";
        for (Map.Entry<Item, Integer> entry : uniqueItems.entrySet()) {
            if (entry.getValue() == numberInBackpack) {
                curItemName = entry.getKey().getName();
                break;
            }
        }
        return curItemName;
    }

    /**
     * Метод для поиска последнего предмета определенного типа и названия в рюкзаке
     *
     * @param type     Тип предмета
     * @param nameItem Имя предмета
     * @return Последний предмет в рюкзаке
     */
    public Item findLastItemInBackpack(String type, String nameItem) {
        Item lastItem = null;
        boolean find = false;
        List<Item> curListType = getBackpackTypeList(type);
        for (int i = curListType.size() - 1; i >= 0 && !find; i--) {
            if (curListType.get(i).getName().equals(nameItem)) {
                find = true;
                lastItem = curListType.get(i);
            }
        }
        return lastItem;
    }

    /**
     * Метод для поиска номера элемента списка, под которым находится последний предмет с определенным именем в рюкзаке
     *
     * @param dropItem Выьранный предмет
     * @return Номер элемента в списке предметов
     */
    public int findLastItemNumberInBackpack(Item dropItem) {
        int numberItem = 0;
        boolean find = false;
        List<Item> curListType = getBackpackTypeList(dropItem.getType());
        for (int i = curListType.size() - 1; i >= 0 && !find; i--) {
            if (curListType.get(i).getName().equals(dropItem.getName())) {
                find = true;
                numberItem = i;
            }
        }
        return numberItem;
    }

    /**
     * Метод для формирования строки с используемым предметом для еды, зельев, свитков
     *
     * @param curTypeList Список предметов определенного типа
     * @param findItem    Выбранный предмет из рюкзака
     * @return Информационная строка об использовании предмета
     */
    public String useFoodScrollPotion(List<Item> curTypeList, Item findItem) {
        String message = "";
        Hero hero = Hero.getInstance();
        if (curTypeList == foods) {
            message = hero.addHealth((Food) findItem);
            hero.addStatistic("ateFood", 1);
        } else if (curTypeList == scrolls) {
            message = hero.addScrollStats((Scroll) findItem);
            hero.addStatistic("readScrolls", 1);
        } else if (curTypeList == potions) {
            message = hero.addPotionStats((Potion) findItem);
            hero.addStatistic("drunkPotion", 1);
        }
        return message;
    }

    public List<Item> getScrolls() {
        return scrolls;
    }

    public List<Item> getPotions() {
        return potions;
    }

    public List<Item> getFoods() {
        return foods;
    }

    public List<Item> getWeapons() {
        return weapons;
    }

    public boolean isFullBackpackType() {
        return fullBackpackType;
    }

    public void setFullBackpackType(boolean fullBackpackType) {
        this.fullBackpackType = fullBackpackType;
    }
}
