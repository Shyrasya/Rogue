package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Scroll extends Item implements UsingDropInfo, CheckItemAbility {

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
     * Конструктор класса Scroll
     *
     * @param name      Название свитка
     * @param agility   Количество единиц ловкости у данного свитка
     * @param strength  Количество единиц силы у данного свитка
     * @param maxHealth Количество единиц макимального здоровья у данного свитка
     */
    public Scroll(String name, int agility, int strength, int maxHealth) {
        super("Scroll", name);
        this.agility = agility;
        this.strength = strength;
        this.maxHealth = maxHealth;
    }

    public Scroll() {
        super();
    }

    /**
     * Конструктор копирования класса Scroll
     *
     * @param other Объект класса Scroll, с которого происходит копирование
     */
    public Scroll(Scroll other) {
        super(other.getType(), other.getName());
        this.agility = other.getAgility();
        this.strength = other.getStrength();
        this.maxHealth = other.getMaxHealth();
    }

    /**
     * Информация, которая пишется в информационной строке при использовании какого либо свитка
     *
     * @return Строка с информацией о прочитаном свитке
     */
    @Override
    public String usingInfo() {
        return "Герой прочитал " + getName() + getItemHaveMaxHealth(maxHealth) + getItemHaveStrength(strength) + getItemHaveAgility(agility);
    }

    /**
     * Информация, которая пишется в информационной строке при выбросе свитка
     *
     * @return Строка с информацией о выброшенном свитке
     */
    @Override
    public String dropInfo() {
        return "Герой выбросил " + getName();
    }

    /**
     * Метод для взятия информации о свитке для помещения её в область рюкзака
     *
     * @return Строка с именем свитка, его характеристиками
     */
    @Override
    public String getBackpackInfo() {
        String firstWord = getName().split(" ")[0];
        return firstWord + " (" + getItemBackpackHaveAgility(agility) + getItemBackpackHaveStrength(strength) + getItemBackpackHaveMaxHealth(maxHealth) + ")";
    }

    /**
     * Метод для взятия символа свитка для дальнейшей отрисовки на карте/экране
     *
     * @return Символ свитка, который отображается на экране
     */
    @Override
    public char getSymbol() {
        return '§';
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
