package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Potion extends Item implements UsingDropInfo, CheckItemAbility {

    /**
     * Ловкость (количество единиц повышения, для свитков, эликсиров)
     */
    private int agility;

    /**
     * Сила (количество единиц повышения, для свитков, эликсиров и оружия)
     */
    private int strength;

    /**
     * Максимальное здоровье (количество единиц повышения, для свитков, эликсиров)
     */
    private int maxHealth;

    /**
     * Конструктор класса Potion
     *
     * @param name      Название зелья
     * @param agility   Количество единиц ловкости у данного зелья
     * @param strength  Количество единиц силы у данного зелья
     * @param maxHealth Количество единиц макимального здоровья у данного свитка
     */
    public Potion(String name, int agility, int strength, int maxHealth) {
        super("Potion", name);
        this.agility = agility;
        this.strength = strength;
        this.maxHealth = maxHealth;
    }

    public Potion() {
        super();
    }


    /**
     * Конструктор копирования класса Potion
     *
     * @param other Объект класса Potion, с которого происходит копирование
     */
    public Potion(Potion other) {
        super(other.getType(), other.getName());
        this.agility = other.getAgility();
        this.strength = other.getStrength();
        this.maxHealth = other.getMaxHealth();
    }

    /**
     * Информация, которая пишется в информационной строке при использовании какого либо зелья
     *
     * @return Строка с информацией о выпитом зелье
     */
    @Override
    public String usingInfo() {
        return "Герой выпил " + getName() + getItemHaveMaxHealth(maxHealth) + getItemHaveStrength(strength) + getItemHaveAgility(agility);
    }

    /**
     * Информация, которая пишется в информационной строке при выбросе зелья
     *
     * @return Строка с информацией о выброшенном зелье
     */
    @Override
    public String dropInfo() {
        return "Герой выбросил " + getName();
    }

    /**
     * Метод для взятия информации о зелье для помещения её в область рюкзака
     *
     * @return Строка с именем зелья, его характеристиками
     */
    @Override
    public String getBackpackInfo() {
        String firstWord = getName().split(" ")[0];
        return firstWord + " (" + getItemBackpackHaveAgility(agility) + getItemBackpackHaveStrength(strength) + getItemBackpackHaveMaxHealth(maxHealth) + ")";
    }

    /**
     * Информационная строка об окончании действия зелья
     *
     * @return Строка о конце действия зелья и снижении применяемых эффектов
     */
    public String endEffectInfo() {
        return "Действие зелья " + getName() + " закончилось" + finEffectAgility(getAgility()) + finEffectMaxHealth(getMaxHealth()) + finEffectStrength(getStrength());
    }

    /**
     * Информационная строка о снижении максимального здоровья у персонажа
     *
     * @param maxHealth Характеристика максимального здоровья данной единицы еды
     * @return Строка о снижении максимального здоровья
     */
    private String finEffectMaxHealth(int maxHealth) {
        String info = "";
        if (maxHealth > 0) info = ". Максимальное здоровье -" + maxHealth;
        return info;
    }

    /**
     * Информационная строка о снижении ловкости у персонажа
     *
     * @param agility Характеристика ловкости данной единицы еды
     * @return Строка о снижении ловкости
     */
    private String finEffectAgility(int agility) {
        String info = "";
        if (agility > 0) info = ". Ловкость -" + agility;
        return info;
    }

    /**
     * Информационная строка о снижении силы у персонажа
     *
     * @param strength Характеристика силы данной единицы еды
     * @return Строка о снижении силы
     */
    private String finEffectStrength(int strength) {
        String info = "";
        if (strength > 0) info = ". Сила -" + strength;
        return info;
    }

    /**
     * Метод для взятия символа зелья для дальнейшей отрисовки на карте/экране
     *
     * @return Символ зелья, который отображается на экране
     */
    @Override
    public char getSymbol() {
        return '&';
    }

    public int getAgility() {
        return agility;
    }

    public int getStrength() {
        return strength;
    }

    public int getMaxHealth() {
        return maxHealth;
    }
}
