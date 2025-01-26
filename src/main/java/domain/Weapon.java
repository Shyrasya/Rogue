package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Weapon extends Item implements UsingDropInfo {

    /**
     * Сила (количество единиц повышения, для свитков, эликсиров и оружия)
     */
    private int strength;

    /**
     * Конструктор класса Weapon
     *
     * @param name     Название оружия
     * @param strength Количество единиц силы у данного оружия
     */
    public Weapon(String name, int strength) {
        super("Weapon", name);
        this.strength = strength;
    }

    public Weapon() {
    }

    /**
     * Конструктор копирования класса Weapon
     *
     * @param other Объект класса Weapon, с которого происходит копирование
     */
    public Weapon(Weapon other) {
        super(other.getType(), other.getName());
        this.strength = other.strength;
    }

    /**
     * Информация, которая пишется в информационной строке при использовании какого либо оружия
     *
     * @return Строка с информацией о взятом в руки оружии
     */
    @Override
    public String usingInfo() {
        return "Герой взял в руки " + getName() + ". Сила +" + this.strength;
    }

    /**
     * Информация, которая пишется в информационной строке при выбросе оружия
     *
     * @return Строка с информацией о выброшенном оружии
     */
    @Override
    public String dropInfo() {
        return "Герой выбросил " + getName();
    }

    /**
     * Информация, которая пишется в информационной строке при выбросе носимого в руке оружия
     *
     * @return Строка с информацией о выброшенном из руки оружии и понижении характристик героя
     */
    public String changeInfo() {
        return "Герой выбросил " + getName() + ". Сила -" + this.strength;
    }

    /**
     * Метод для взятия символа оружия для дальнейшей отрисовки на карте/экране
     *
     * @return Символ оружия, который отображается на экране
     */
    @Override
    public char getSymbol() {
        return '^';
    }

    /**
     * Метод для взятия информации об оружии для помещения её в область рюкзака
     *
     * @return Строка с именем оружия, его характеристикой и текущем статусе ношения
     */
    @Override
    public String getBackpackInfo() {
        return getName() + " (с +" + this.strength + ")" + wearingStatus();
    }

    /**
     * Метод для вычисления статуса ношения предмета, исходя из наличия оружия в руках героя
     *
     * @return Строка с информацией о ношении оружия - "в руке"
     */
    private String wearingStatus() {
        Weapon heroWeapon = Hero.getInstance().getCurWeapon();
        String wear = "";
        if (heroWeapon != null && getName().equals(heroWeapon.getName())) wear += " (в руке)";
        return wear;
    }

    public int getStrength() {
        return this.strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
}
