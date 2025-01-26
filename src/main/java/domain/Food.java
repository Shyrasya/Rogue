package domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Food extends Item implements UsingDropInfo {

    /**
     * Здоровье (количество единиц повышения, для еды)
     */
    private int health;

    /**
     * Конструктор класса еда
     *
     * @param name   Название еды
     * @param health Количество единиц здоровья у данной еды
     */
    @JsonCreator
    public Food(@JsonProperty("name") String name, @JsonProperty("food") int health) {
        super("Food", name);
        this.health = health;
    }

    public Food() {
        super();
    }

    /**
     * Конструктор копирования класса Food
     *
     * @param other Объект класса Food, с которого происходит копирование
     */
    public Food(Food other) {
        super(other.getType(), other.getName());
        this.health = other.health;
    }

    /**
     * Информация, которая пишется в информационной строке при использовании какой либо еды
     *
     * @return Строка с информацией о съеденной еде
     */
    @Override
    public String usingInfo() {
        return "Герой съел " + getName() + ". Здоровье +" + this.health;
    }

    /**
     * Информация, которая пишется в информационной строке при выбросе еды
     *
     * @return Строка с информацией о выброшенной еде
     */
    @Override
    public String dropInfo() {
        return "Герой выбросил " + getName();
    }

    /**
     * Метод для взятия символа еды для дальнейшей отрисовки на карте/экране
     *
     * @return Символ еды, который отображается на экране
     */
    @Override
    public char getSymbol() {
        return '@';
    }


    /**
     * Метод для взятия информации об еде для помещения её в область рюкзака
     *
     * @return Строка с именем еды, ее характеристикой
     */
    @Override
    public String getBackpackInfo() {
        return getName() + "(з +" + this.health + ")";
    }

    public int getHealth() {
        return this.health;
    }
}
