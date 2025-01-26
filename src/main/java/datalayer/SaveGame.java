package datalayer;

import domain.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SaveGame {
    /**
     * Номер уровня
     */
    private int levelNumber;
    /**
     * Текущий левел в сессии
     */
    Level gameLevel;
    /**
     * Текущее состояние героя
     */
    private Hero hero;
    /**
     * Карта уровня
     */
    private char[][] map;
    /**
     * Карта уровня с туманом войны
     */
    private char[][] fogMap;
    /**
     * Вещи в рюкзаке
     */
    private Backpack backpack;
    /**
     * Список врагов на уровне
     */
    private List<Person> enemies;

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public Level getGameLevel() {
        return gameLevel;
    }

    public void setGameLevel(Level gameLevel) {
        this.gameLevel = gameLevel;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public char[][] getMap() {
        return this.map;
    }

    public void setMap(char[][] map) {
        this.map = map;
    }

    public char[][] getFogMap() {
        return this.fogMap;
    }

    public void setFogMap(char[][] fogMap) {
        this.fogMap = fogMap;
    }

    public Backpack getBackpack() {
        return backpack;
    }

    public void setBackpack(Backpack backpack) {
        this.backpack = backpack;
    }

    public List<Person> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Person> enemies) {
        this.enemies = enemies;
    }

    /**
     * Метод сериализует объект saveGame и записывает его в файл
     * Сущности для записи в объект попадают через геттеры
     *
     * @param saveGame Объект c сохраненными сущностями игры, который собираемся сериализовать
     * @param fileName Имя файла, куда сохраним сериализованный объект
     */
    public static void save(SaveGame saveGame, String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(fileName), saveGame);
    }

    /**
     * Метод десериализует файл и возвращает объект сохраненный игры.
     * Каждую сущность можно получить через геттер
     *
     * @param fileName Имя десериализуемого файла
     * @return Объект сохраненной игры
     */
    public static SaveGame load(String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(fileName), SaveGame.class);
    }
}


