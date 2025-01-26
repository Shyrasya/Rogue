package domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Hero extends Person {
    /**
     * Экземпляр объекта
     */
    private static Hero instance;
    /**
     * Статистика текущего прохождения
     */
    private HashMap<String, Integer> statistic = new HashMap<>();
    /**
     * Контейнер выпитых зелий
     */
    private HashMap<Integer, List<Potion>> usedPotions = new HashMap<>();
    /**
     * Выбранное оружие
     */
    private Weapon curWeapon;
    /**
     * Подобранный предмет
     */
    private Item pickUpItem;
    /**
     * Комната, где находится герой
     */
    private Room heroRoom;
    /**
     * Коридор, в котором находится герой
     */
    private Corridor heroCorridor;
    /**
     * Имя персонажа
     */
    private String name;

    private Hero(int x, int y) {
        super(x, y);
        this.thisIs = GameConstants.HERO_SYMBOL;
        statistic.put("gold", 0);
        statistic.put("level", 1);
        statistic.put("deadEnemies", 0);
        statistic.put("ateFood", 0);
        statistic.put("drunkPotion", 0);
        statistic.put("readScrolls", 0);
        statistic.put("giveHit", 0);
        statistic.put("getHit", 0);
        statistic.put("pathCells", 0);
    }

    /**
     * Получение экземпляра героя с стартовыми координатами
     *
     * @param x абсцисса появления персонажа
     * @param y оордината появления персонажа
     */
    @JsonCreator
    public static Hero getInstance(@JsonProperty("coordX") int x, @JsonProperty("coordY") int y) {
        if (instance == null) {
            instance = new Hero(x, y);
        }
        return instance;
    }

    /**
     * Функция-обертка для работы со статистикой
     *
     * @param field    Поле для увеличения
     * @param quantity Значение, на которое будет увеличено
     */
    public void addStatistic(String field, int quantity) {
        statistic.put(field, statistic.get(field) + quantity);
    }

    /**
     * Синглтон. Получение экземпляра героя
     *
     * @return Экземпляр героя
     */
    public static Hero getInstance() {
        return instance;
    }

    /**
     * Заменяет единственный экземпляр героя
     */
    public static void setHero(Hero hero) {
        instance = hero;
    }

    /**
     * Добавляет здоровье герою при использовании еды
     *
     * @return Оповещает об использовании предмета
     */
    public String addHealth(Food food) {
        int addNumber = food.getHealth();
        if (addNumber + health >= maxHealth) health = maxHealth;
        else health += addNumber;
        return food.usingInfo();
    }

    /**
     * Добавляет силу при использовании оружия
     *
     * @return Оповещает об использовании предмета
     */
    public String addStrength(Weapon newWeapon) {
        strength += newWeapon.getStrength();
        curWeapon = newWeapon;
        return newWeapon.usingInfo();
    }

    /**
     * Убирает силу при выкидывании оружия
     *
     * @return Оповещает о выкидывании предмета
     */
    public String removeStrength() {
        strength -= curWeapon.getStrength();
        return curWeapon.changeInfo();
    }

    /**
     * Добавляет атрибут после прочтения свитка
     *
     * @return Оповещает об использовании свитка
     */
    public String addScrollStats(Scroll scroll) {
        agility += scroll.getAgility();
        strength += scroll.getStrength();
        maxHealth += scroll.getMaxHealth();
        return scroll.usingInfo();
    }

    /**
     * Добавляет атрибуты при выпивании зелья
     *
     * @return Оповещает об использовании зелья
     */
    public String addPotionStats(Potion potion) {
        int stepsEffect = GameConstants.STEPS_EFFECT;
        int finalEffectSteps = stepsEffect + statistic.get("pathCells");
        agility += potion.getAgility();
        strength += potion.getStrength();
        maxHealth += potion.getMaxHealth();
        usedPotions.computeIfAbsent(finalEffectSteps, v -> new ArrayList<>()).add(potion);
        return potion.usingInfo();
    }

    /**
     * Проверяет, не кончилось ли зелье
     *
     * @return Контейнер законченных зелий
     */
    public List<String> checkPotionsUsingTime() {
        List<String> endPotionInfo = new ArrayList<>();
        int currentSteps = statistic.get("pathCells");
        List<Potion> curFinPotions = usedPotions.get(currentSteps);
        if (curFinPotions != null) {
            for (Potion curPotion : curFinPotions) {
                agility -= curPotion.getAgility();
                strength -= curPotion.getStrength();
                maxHealth -= curPotion.getMaxHealth();
                if (maxHealth < health) health = maxHealth;
                endPotionInfo.add(curPotion.endEffectInfo());
            }
            usedPotions.remove(currentSteps);
        }
        return endPotionInfo;
    }

    public HashMap<String, Integer> getStatistic() {
        return statistic;
    }

    public void setStatistic(HashMap<String, Integer> statistic) {
        this.statistic = statistic;
    }

    public Weapon getCurWeapon() {
        return curWeapon;
    }

    public void setCurWeapon(Weapon curWeapon) {
        this.curWeapon = curWeapon;
    }

    public HashMap<Integer, List<Potion>> getUsedPotions() {
        return usedPotions;
    }

    public void setUsedPotions(HashMap<Integer, List<Potion>> usedPotions) {
        this.usedPotions = usedPotions;
    }

    public Item getPickUpItem() {
        return pickUpItem;
    }

    public void setPickUpItem(Item pickUpItem) {
        this.pickUpItem = pickUpItem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Room getHeroRoom() {
        return heroRoom;
    }

    public void setHeroRoom(Room heroRoom) {
        this.heroRoom = heroRoom;
    }

    public Corridor getHeroCorridor() {
        return heroCorridor;
    }

    public void setHeroCorridor(Corridor heroCorridor) {
        this.heroCorridor = heroCorridor;
    }

    public void eraseHero() {
        instance = null;
    }
}