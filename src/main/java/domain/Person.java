package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Set;

/**
 * Игнорирование неизвестных свойств
 */
@JsonIgnoreProperties(ignoreUnknown = true)

/**
 * Эта аннотация добавляет тип в JSON для поддержки полиморфизма.
 * use = JsonTypeInfo.Id.NAME - Позволяет использовать строковые имена типов
 * include - Подключает свойство
 * property - задает названия свойства
 * JsonSubTypes - Если имя типа == "hero" то он сериализует объект как Hero
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Hero.class, name = "hero"),
        @JsonSubTypes.Type(value = Enemy.class, name = "enemy")
})

public abstract class Person {
    public Person(int x, int y) {
        coordX = x;
        coordY = y;
        prevCoordX = 0;
        prevCoordY = 0;
        agility = 10;
        strength = 10;
        health = 100;
        maxHealth = health;
        turn = agility;
    }

    public Person() {
    }

    protected int coordX;
    protected int coordY;
    /**
     * Прошлая координата
     */
    protected int prevCoordX = 0;
    /**
     * Прошлая координата
     */
    protected int prevCoordY = 0;
    protected int health;
    protected int maxHealth;
    protected int agility;
    protected int strength;
    int gold = -1;
    /**
     * Шкала хода (1 ход - 10 очков)
     */
    protected int turn;
    /**
     * Символ, на котором стоял герой
     */
    protected String curSymbol = ".";
    /**
     * Символ отображения на карте
     */
    protected String thisIs = "";

    public Set<Character> kindsOfPerson = Set.of('H', 'S', 'Z', 'V', 'O', 'G', 'M');

    /**
     * Метод передвижения героя
     *
     * @param deltaX Разница между координатами абсцисс
     * @param deltaY Разница между координатами оординат
     */
    public void move(int deltaX, int deltaY) {
        prevCoordX = coordX;
        prevCoordY = coordY;
        coordX += deltaX;
        coordY += deltaY;
    }

    public int getCoordX() {
        return coordX;
    }

    public void setCoordX(int x) {
        this.coordX = x;
    }

    public String getCurSymbol() {
        return curSymbol;
    }

    public void setCurSymbol(String symbol) {
        curSymbol = symbol;
    }

    public int getPrevCoordY() {
        return prevCoordY;
    }

    public int getPrevCoordX() {
        return prevCoordX;
    }

    public void setCoordY(int y) {
        this.coordY = y;
    }

    public int getCoordY() {
        return coordY;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }


    public boolean getNextTurn() {
        boolean nextTurn = true;

        if (turn >= 10)
            turn -= 10;
        else
            nextTurn = false;

        return nextTurn;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int agility) {
        turn += agility;
    }

    public String getThisIs() {
        return thisIs;
    }

    public void getHit(int damage) {
        health -= damage;
    }

    public int getDamage() {
        return strength;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getPointsTurn() {
        return turn;
    }

    public void setPointsTurn(int turn) {
        this.turn = turn;
    }
}
