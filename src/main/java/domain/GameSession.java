package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import datalayer.DataBase;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static domain.GameConstants.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameSession {

    /**
     * Номер текущего уровня
     */
    private int levelNumber;

    /**
     * Игровой уровень, в котором находится персонаж
     */
    private Level gameLevel;

    /**
     * Рюкзак главного героя
     */
    private Backpack backpack;

    /**
     * Список с врагми уровня
     */
    private List<Person> Enemies = new ArrayList<>();

    /**
     * Единственный экземпляр класса GameSession (реализация Singleton)
     */
    private static GameSession instance;

    /**
     * Основная игровая карта, в которой происходит игровая логика (не покрытая "туманом войны")
     */
    private char[][] map;

    /**
     * Игровая карта с учетом покрытия "туманом войны"
     */
    private char[][] fogMap;

    /**
     * Список символов возможных предметов в игре
     */
    private final Set<Character> itemSymbolSet = Set.of('@', '&', '^', '§');

    /**
     * Можно ли перейти на следующий уровень (true - да, false - нет)
     */
    private boolean nextLvl = false;

    /**
     * Базовый конструктор класса GameSession
     */
    public GameSession() {
    }

    /**
     * Параметризованный конструктор класса GameSession
     *
     * @param level     Текущий игровой уровень
     * @param lvlNumber Номер уровня
     * @param backpack  Рюкзак главного героя
     */
    public GameSession(Level level, int lvlNumber, Backpack backpack) {
        Random rand = new Random();
        levelNumber = lvlNumber;
        gameLevel = level;
        this.backpack = backpack;
        map = new char[MAP_HEIGHT][MAP_WIDTH];
        drawEmptyMap(map);
        setMap();

        fogMap = new char[MAP_HEIGHT][MAP_WIDTH];
        drawEmptyMap(fogMap);

        File saveFile = new File("save.json");
        if (!saveFile.exists())
            setHero();
        addPersonToMap(Hero.getInstance());
        setEnemy();
        setItem(rand);
        setGold(rand);
        instance = this;
        drawHeroRoomOnFogMap();
    }

    public static GameSession getSession() {
        return instance;
    }

    /**
     * Установка координат главного героя и помещение его в стартовую комнату в начале уровня
     */
    private void setHero() {
        int roomNumber = gameLevel.getStartIntRoom();
        Position pos = isRoomPointFree(roomNumber);
        Hero hero = Hero.getInstance(pos.getXPos(), pos.getYPos());
        hero.setCoordX(pos.getXPos());
        hero.setCoordY(pos.getYPos());
        hero.setHeroRoom(gameLevel.getStartRoom());
    }

    /**
     * Размещение врагов на карту в начале уровня
     */
    private void setEnemy() {
        Hero hero = Hero.getInstance();
        int startRoom = gameLevel.trueGetStartRoom(hero.coordX, hero.coordY);
        for (int i = 0, j = 0; i < gameLevel.getRoomsArray().length - 1; i++) {
            if (i != startRoom) {
                Position pos = gameLevel.trueRandPointRoom(i);
                Enemy enemy = new Enemy(pos.getXPos(), pos.getYPos());
                enemyImprove(enemy);
                Enemies.add(enemy);
                addPersonToMap(Enemies.get(j));
                j++;
            }
        }
    }

    /**
     * Задание характеристик врага исходя из номера уровня
     *
     * @param enemy Текущий враг
     */
    private void enemyImprove(Enemy enemy) {
        int[] improveEnemy = new int[]{5, levelNumber / 4, levelNumber / 2, levelNumber / 6};
        enemy.setHealth(enemy.getHealth() + improveEnemy[0]);
        enemy.setAgility(enemy.getAgility() + improveEnemy[1]);
        enemy.setStrength(enemy.getStrength() + improveEnemy[2]);
        enemy.setAggressive(enemy.getAggressive() + improveEnemy[3]);
    }

    /**
     * Расстановка предметов на карту, выбранных случайным образом, в зависимости от номера уровня
     *
     * @param rand Объект типа Random, который используется для генерации случайных чисел
     */
    private void setItem(Random rand) {
        gameLevel.setSpawnItems(new ArrayList<>());
        int countItems = itemsCountOnLevel("item");
        int levelFoodCount = (int) Math.ceil(countItems * FOOD_LEVEL_SPAWN_PERCENT);
        for (int i = 0; i < countItems - levelFoodCount; i++) {
            String type;
            do {
                type = ItemData.getRandomTypeItem();
            } while (type.equals("Food"));
            Item item = ItemData.getRandomItem(type);
            gameLevel.getSpawnItems().add(item);
        }
        for (int i = 0; i < levelFoodCount; i++) {
            Item item = ItemData.getRandomItem("Food");
            gameLevel.getSpawnItems().add(item);
        }
        addItemsToMap(rand);
    }

    /**
     * Расчет количества предметов определенного типа в зависимости от текущего уровня игры
     *
     * @param type Тип предмета (item/gold)
     * @return Количество предметов выбранного типа на уровне
     */
    private int itemsCountOnLevel(String type) {
        int countItems;
        if (levelNumber >= 1 && levelNumber <= 4)
            countItems = type.equals("item") ? 5 : 1;
        else if (levelNumber >= 5 && levelNumber <= 8)
            countItems = type.equals("item") ? 4 : 2;
        else if (levelNumber >= 9 && levelNumber <= 12)
            countItems = 3;
        else if (levelNumber >= 13 && levelNumber <= 16)
            countItems = type.equals("item") ? 2 : 4;
        else if (levelNumber >= 17 && levelNumber <= 20)
            countItems = type.equals("item") ? 1 : 5;
        else
            countItems = type.equals("item") ? 0 : 6;
        return countItems;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public Level getLevel() {
        return gameLevel;
    }

    public void setGameLevel(Level gameLevel) {
        this.gameLevel = gameLevel;
    }

    public int getLvlNumber() {
        return levelNumber;
    }

    public boolean getNextLvl() {
        return nextLvl;
    }

    public void setNextLvl(boolean lvl) {
        nextLvl = lvl;
    }

    public List<Person> getEnemies() {
        return Enemies;
    }

    public void setEnemies(List<Person> enemies) {
        Enemies = enemies;
    }

    /**
     * Поиск свободной точки в комнате
     *
     * @param curRoom Номер сектора комнаты в решетке 5*5 (от 0 до 8)
     * @return Координаты свободной точки
     */
    private Position isRoomPointFree(int curRoom) {
        Position pos;
        do {
            pos = gameLevel.chooseRandRoomPoint(curRoom);
        } while (map[pos.getYPos()][pos.getXPos()] != FLOOR_SYMBOL && map[pos.getYPos()][pos.getXPos()] != DOOR_SYMBOL && map[pos.getYPos()][pos.getXPos()] != WALL_SYMBOL);
        return pos;
    }

    /**
     * Проверка занятости соседней справа от героя клетки
     *
     * @return true - клетка занята, false - свободна
     */
    public boolean isTakenNeighborMapPoint() {
        Hero hero = Hero.getInstance();
        int neighborYCoord = hero.getCoordY();
        int neighborXCoord = hero.getCoordX() + 1;
        return (map[neighborYCoord][neighborXCoord] != FLOOR_SYMBOL && map[neighborYCoord][neighborXCoord] != CORRIDOR_SYMBOL);
    }

    /**
     * Установка на игровую карту комнат, выхода с уровня и коридоров
     */
    public void setMap() {
        setGeometryRooms(gameLevel);
        setExitDoor(gameLevel);
        setCorridors(gameLevel);
    }

    public char[][] getMap() {
        return this.map;
    }

    public void setMap(char[][] map) {
        this.map = map;
    }

    public char[][] getFogMap() {
        return fogMap;
    }

    public void setFogMap(char[][] fogMap) {
        this.fogMap = fogMap;
    }


    /**
     * Копирование с основной карты на туманную всей комнаты с героем
     */
    public void drawHeroRoomOnFogMap() {
        Room currentHeroRoom = gameLevel.findHeroRoom();
        if (currentHeroRoom != null) {
            for (int r = currentHeroRoom.getUpLeftCorner().getYPos(); r <= currentHeroRoom.getDownRightCorner().getYPos(); r++) {
                for (int c = currentHeroRoom.getUpLeftCorner().getXPos(); c <= currentHeroRoom.getDownRightCorner().getXPos(); c++) {
                    if (map[r][c] == 'M' || map[r][c] == 'G') {
                        findEnemyDrawFogMap(r, c);
                    } else fogMap[r][c] = map[r][c];
                }
            }
            Hero.getInstance().setHeroRoom(currentHeroRoom);
            currentHeroRoom.setExplored(true);
        }
    }

    /**
     * Установка на игровую карту пустых сгенерированных комнат и дверей
     *
     * @param level Текущий игровой уровень
     */
    private void setGeometryRooms(Level level) {
        Room[] array = level.getRoomsArray();
        for (Room room : array) {
            if (room != null) {
                setRoomsToMap(room);
                setDoorsToMap(room);
            }
        }
    }

    /**
     * Установка на игровую карту пустых сгенерированных комнат - стены и пол
     *
     * @param room Текущая комната
     */
    private void setRoomsToMap(Room room) {
        for (int i = 0; i < room.getRoomHeight(); i++) {
            for (int j = 0; j < room.getRoomWidth(); j++) {
                int curY = room.getUpLeftCorner().getYPos() + i;
                int curX = room.getUpLeftCorner().getXPos() + j;
                if ((i == 0 || i == room.getRoomHeight() - 1 || j == 0 || j == room.getRoomWidth() - 1))
                    map[curY][curX] = WALL_SYMBOL;
                else
                    map[curY][curX] = FLOOR_SYMBOL;
            }
        }
    }

    /**
     * Установка на игровую карту дверей для перехода между комнатами
     *
     * @param room Текущая комната
     */
    private void setDoorsToMap(Room room) {
        for (int i = 0; i < MAX_DOORS_NUMBER; i++) {
            if (room.getDoors()[i].getXPos() != INIT) {
                int doorY = room.getDoors()[i].getYPos();
                int doorX = room.getDoors()[i].getXPos();
                map[doorY][doorX] = DOOR_SYMBOL;
            }
        }
    }

    /**
     * Установка на игровую карту выхода с уровня
     *
     * @param level Текущий игровой уровень
     */
    private void setExitDoor(Level level) {
        Position exitDoor = level.getExitDoor();
        map[exitDoor.getYPos()][exitDoor.getXPos()] = EXIT_SYMBOL;
    }

    /**
     * Установка на игровую карту коридоров
     *
     * @param level Текущий игровой уровень
     */
    private void setCorridors(Level level) {
        for (int i = 0; i < level.getCorridorsCount(); i++) {
            Corridor curCor = level.getCorridorsArray()[i];
            if (curCor.getCorType() == LEFT_TO_RIGHT_CORRIDOR)
                setLeftRightCorridor(curCor);
            if (curCor.getCorType() == LEFT_TURN_CORRIDOR)
                setLeftTurnCorridor(curCor);
            if (curCor.getCorType() == RIGHT_TURN_CORRIDOR)
                setRightTurnCorridor(curCor);
            if (curCor.getCorType() == UP_TO_DOWN_CORRIDOR)
                setUpDownCorridor(curCor);
        }
    }

    /**
     * Метод для отображения коридора типа "слева-направо" символом '+' на выбранную карту
     *
     * @param curCor Коридор типа "слева-направо"
     */
    private void setLeftRightCorridor(Corridor curCor) {
        for (int c = curCor.getNode(0).getXPos() + 1; c < curCor.getNode(1).getXPos(); c++)
            map[curCor.getNode(0).getYPos()][c] = CORRIDOR_SYMBOL;

        int upNodeY = Math.min(curCor.getNode(1).getYPos(), curCor.getNode(2).getYPos());
        int downNodeY = Math.max(curCor.getNode(1).getYPos(), curCor.getNode(2).getYPos());

        for (int r = upNodeY; r <= downNodeY; r++)
            map[r][curCor.getNode(1).getXPos()] = CORRIDOR_SYMBOL;
        for (int c = curCor.getNode(2).getXPos() + 1; c < curCor.getNode(3).getXPos(); c++)
            map[curCor.getNode(2).getYPos()][c] = CORRIDOR_SYMBOL;
    }

    /**
     * Метод для отображения коридора типа "левый поворот" символом '+' на выбранную карту
     *
     * @param curCor Коридор типа "левый поворот"
     */
    private void setLeftTurnCorridor(Corridor curCor) {
        int minX = curCor.getNode(2).getXPos();
        int maxX = curCor.getNode(1).getXPos();
        for (int c = minX + 1; c <= maxX; c++)
            map[curCor.getNode(2).getYPos()][c] = CORRIDOR_SYMBOL;

        int minY = curCor.getNode(0).getYPos();
        int maxY = curCor.getNode(1).getYPos();

        for (int r = minY + 1; r < maxY; r++)
            map[r][curCor.getNode(1).getXPos()] = CORRIDOR_SYMBOL;
    }


    /**
     * Метод для отображения коридора типа "правый поворот" символом '+' на выбранную карту
     *
     * @param curCor Коридор типа "правый поворот"
     */
    private void setRightTurnCorridor(Corridor curCor) {
        int minX = curCor.getNode(1).getXPos();
        int maxX = curCor.getNode(2).getXPos();
        for (int c = minX; c < maxX; c++)
            map[curCor.getNode(1).getYPos()][c] = CORRIDOR_SYMBOL;

        int minY = curCor.getNode(0).getYPos();
        int maxY = curCor.getNode(1).getYPos();
        for (int r = minY + 1; r < maxY; r++)
            map[r][curCor.getNode(0).getXPos()] = CORRIDOR_SYMBOL;
    }


    /**
     * Метод для отображения коридора типа "сверху-вниз" символом '+' на выбранную карту
     *
     * @param curCor Коридор типа "сверху-вниз"
     */
    private void setUpDownCorridor(Corridor curCor) {
        for (int r = curCor.getNode(0).getYPos() + 1; r < curCor.getNode(1).getYPos(); r++)
            map[r][curCor.getNode(0).getXPos()] = CORRIDOR_SYMBOL;

        int leftNodeX = Math.min(curCor.getNode(1).getXPos(), curCor.getNode(2).getXPos());
        int rightNodeX = Math.max(curCor.getNode(1).getXPos(), curCor.getNode(2).getXPos());
        for (int c = leftNodeX; c <= rightNodeX; c++)
            map[curCor.getNode(1).getYPos()][c] = CORRIDOR_SYMBOL;
        for (int r = curCor.getNode(2).getYPos() + 1; r < curCor.getNode(3).getYPos(); r++)
            map[r][curCor.getNode(2).getXPos()] = CORRIDOR_SYMBOL;
    }

    /**
     * Метод для перезаписи изученных коридоров символом '+' на туманной карте
     */
    private void drawFogNoHeroCorridor() {
        for (Corridor corridor : gameLevel.getCorridorsArray()) {
            if (corridor.isExplored()) {
                if (corridor.getCorType() == LEFT_TO_RIGHT_CORRIDOR)
                    setFogMapCorridorSymbolLeftRightCorridor(corridor);
                else if (corridor.getCorType() == LEFT_TURN_CORRIDOR)
                    setFogMapCorridorSymbolLeftTurnCorridor(corridor);
                else if (corridor.getCorType() == RIGHT_TURN_CORRIDOR)
                    setFogMapCorridorSymbolRightTurnCorridor(corridor);
                else if (corridor.getCorType() == UP_TO_DOWN_CORRIDOR)
                    setFogMapCorridorSymbolUpDownCorridor(corridor);
            }
        }
    }

    /**
     * Метод для перезаписи символов коридора '+' типа "слева-направо" на туманную карту
     *
     * @param curCor Коридор типа "слева-направо"
     */
    private void setFogMapCorridorSymbolLeftRightCorridor(Corridor curCor) {
        for (int c = curCor.getNode(0).getXPos() + 1; c < curCor.getNode(1).getXPos(); c++)
            if (fogMap[curCor.getNode(0).getYPos()][c] != ' ') fogMap[curCor.getNode(0).getYPos()][c] = CORRIDOR_SYMBOL;

        int upNodeY = Math.min(curCor.getNode(1).getYPos(), curCor.getNode(2).getYPos());
        int downNodeY = Math.max(curCor.getNode(1).getYPos(), curCor.getNode(2).getYPos());

        for (int r = upNodeY; r <= downNodeY; r++)
            if (fogMap[r][curCor.getNode(1).getXPos()] != ' ') fogMap[r][curCor.getNode(1).getXPos()] = CORRIDOR_SYMBOL;
        for (int c = curCor.getNode(2).getXPos() + 1; c < curCor.getNode(3).getXPos(); c++)
            if (fogMap[curCor.getNode(2).getYPos()][c] != ' ') fogMap[curCor.getNode(2).getYPos()][c] = CORRIDOR_SYMBOL;
    }

    /**
     * Метод для перезаписи символов коридора типа "левый поворот" на туманную карту
     *
     * @param curCor Коридор типа "левый поворот"
     */

    private void setFogMapCorridorSymbolLeftTurnCorridor(Corridor curCor) {
        int minX = curCor.getNode(2).getXPos();
        int maxX = curCor.getNode(1).getXPos();
        for (int c = minX + 1; c <= maxX; c++)
            if (fogMap[curCor.getNode(2).getYPos()][c] != ' ') fogMap[curCor.getNode(2).getYPos()][c] = CORRIDOR_SYMBOL;

        int minY = curCor.getNode(0).getYPos();
        int maxY = curCor.getNode(1).getYPos();

        for (int r = minY + 1; r < maxY; r++)
            if (fogMap[r][curCor.getNode(1).getXPos()] != ' ') fogMap[r][curCor.getNode(1).getXPos()] = CORRIDOR_SYMBOL;
    }

    /**
     * Метод для перезаписи символов коридора типа "правый поворот" на туманную карту
     *
     * @param curCor Коридор типа "правый поворот"
     */
    private void setFogMapCorridorSymbolRightTurnCorridor(Corridor curCor) {
        int minX = curCor.getNode(1).getXPos();
        int maxX = curCor.getNode(2).getXPos();
        for (int c = minX; c < maxX; c++)
            if (fogMap[curCor.getNode(1).getYPos()][c] != ' ') fogMap[curCor.getNode(1).getYPos()][c] = CORRIDOR_SYMBOL;

        int minY = curCor.getNode(0).getYPos();
        int maxY = curCor.getNode(1).getYPos();
        for (int r = minY + 1; r < maxY; r++)
            if (fogMap[r][curCor.getNode(0).getXPos()] != ' ') fogMap[r][curCor.getNode(0).getXPos()] = CORRIDOR_SYMBOL;
    }

    /**
     * Метод для перезаписи символов коридора типа "сверху-вниз" на туманную карту
     *
     * @param curCor Коридор типа "сверху-вниз"
     */
    private void setFogMapCorridorSymbolUpDownCorridor(Corridor curCor) {
        for (int r = curCor.getNode(0).getYPos() + 1; r < curCor.getNode(1).getYPos(); r++)
            if (fogMap[r][curCor.getNode(0).getXPos()] != ' ') fogMap[r][curCor.getNode(0).getXPos()] = CORRIDOR_SYMBOL;

        int leftNodeX = Math.min(curCor.getNode(1).getXPos(), curCor.getNode(2).getXPos());
        int rightNodeX = Math.max(curCor.getNode(1).getXPos(), curCor.getNode(2).getXPos());
        for (int c = leftNodeX; c <= rightNodeX; c++)
            if (fogMap[curCor.getNode(1).getYPos()][c] != ' ') fogMap[curCor.getNode(1).getYPos()][c] = CORRIDOR_SYMBOL;
        for (int r = curCor.getNode(2).getYPos() + 1; r < curCor.getNode(3).getYPos(); r++)
            if (fogMap[r][curCor.getNode(2).getXPos()] != ' ') fogMap[r][curCor.getNode(2).getXPos()] = CORRIDOR_SYMBOL;
    }

    /**
     * Метод рисования коридоров с алгоритмами "ray-casting" и "bresenhem"
     */
    private void drawCorridorOnFogMap() {
        Hero hero = Hero.getInstance();
        Corridor currentCorridor;
        if (isHeroInDoor(hero))
            currentCorridor = gameLevel.findHeroCorridorInDoor(hero);
        else
            currentCorridor = findHeroCorridorInCorridor(hero);
        if (currentCorridor != null) {
            String[] directions = {"up", "upRight", "right", "rightDown", "down", "downLeft", "left", "leftUp"};
            for (String direction : directions) {
                Position edgePosition = rayCastingFindCoridorEdgePosition(hero, direction);
                if (edgePosition != null)
                    bresenhamLineOnFogMap(hero, edgePosition);
            }
        }
    }

    /**
     * Поиск коридора, в котором находится герой, из коридора
     *
     * @param hero Главный герой
     * @return Коридор с героем
     */
    private Corridor findHeroCorridorInCorridor(Hero hero) {
        Corridor heroCorridor = null;
        int heroY = hero.getCoordY();
        int heroX = hero.getCoordX();
        for (Corridor corridor : gameLevel.getCorridorsArray()) {
            if (corridor != null) {
                if (corridor.getCorType() == LEFT_TO_RIGHT_CORRIDOR && corridor.checkLeftToRightNodesExtraPoints(heroY, heroX)) {
                    if (isCurrentPointInHeroCorridorLeftToRight(heroY, heroX, corridor))
                        heroCorridor = corridor;
                } else if (corridor.getCorType() == LEFT_TURN_CORRIDOR && corridor.checkLeftTurnNodesExtraPoints(heroY, heroX)) {
                    if (isCurrentPointInHeroCorridorLeftTurn(heroY, heroX, corridor))
                        heroCorridor = corridor;
                } else if (corridor.getCorType() == RIGHT_TURN_CORRIDOR && corridor.checkRightTurnNodesExtraPoints(heroY, heroX)) {
                    if (isCurrentPointInHeroCorridorRightTurn(heroY, heroX, corridor))
                        heroCorridor = corridor;
                } else if (corridor.getCorType() == UP_TO_DOWN_CORRIDOR && corridor.checkUpToDownNodesExtraPoints(heroY, heroX)) {
                    if (isCurrentPointInHeroCorridorUpToDown(heroY, heroX, corridor))
                        heroCorridor = corridor;
                }
                if (heroCorridor != null) {
                    hero.setHeroCorridor(corridor);
                    corridor.setExplored(true);
                    break;
                }
            }
        }
        return heroCorridor;
    }

    /**
     * Проверка, находится ли выбранная точка в коридоре с героем  типа "слева-направо"
     *
     * @param pointY       Координата точки по оси Y
     * @param pointX       Координата точки по оси X
     * @param heroCorridor Коридор, в котором сейчас находится герой
     * @return true - да, точка в этом коридоре, false - нет
     */
    private boolean isCurrentPointInHeroCorridorLeftToRight(int pointY, int pointX, Corridor heroCorridor) {
        boolean pointIsInHeroCorridor = false;
        for (int c = heroCorridor.getNode(0).getXPos() + 1; c < heroCorridor.getNode(1).getXPos() && !pointIsInHeroCorridor; c++)
            if (map[heroCorridor.getNode(0).getYPos()][c] == map[pointY][pointX]) pointIsInHeroCorridor = true;

        int upNodeY = Math.min(heroCorridor.getNode(1).getYPos(), heroCorridor.getNode(2).getYPos());
        int downNodeY = Math.max(heroCorridor.getNode(1).getYPos(), heroCorridor.getNode(2).getYPos());

        for (int r = upNodeY; r <= downNodeY && !pointIsInHeroCorridor; r++)
            if (map[r][heroCorridor.getNode(1).getXPos()] == map[pointY][pointX]) pointIsInHeroCorridor = true;
        for (int c = heroCorridor.getNode(2).getXPos() + 1; c < heroCorridor.getNode(3).getXPos() && !pointIsInHeroCorridor; c++)
            if (map[heroCorridor.getNode(2).getYPos()][c] == map[pointY][pointX]) pointIsInHeroCorridor = true;
        return pointIsInHeroCorridor;
    }

    /**
     * Проверка, находится ли выбранная точка в коридоре с героем  типа "левый поворот"
     *
     * @param pointY       Координата точки по оси Y
     * @param pointX       Координата точки по оси X
     * @param heroCorridor Коридор, в котором сейчас находится герой
     * @return true - да, точка в этом коридоре, false - нет
     */
    private boolean isCurrentPointInHeroCorridorLeftTurn(int pointY, int pointX, Corridor heroCorridor) {
        boolean pointIsInHeroCorridor = false;
        int minX = heroCorridor.getNode(2).getXPos();
        int maxX = heroCorridor.getNode(1).getXPos();
        for (int c = minX + 1; c <= maxX && !pointIsInHeroCorridor; c++)
            if (map[heroCorridor.getNode(2).getYPos()][c] == map[pointY][pointX]) pointIsInHeroCorridor = true;

        int minY = heroCorridor.getNode(0).getYPos();
        int maxY = heroCorridor.getNode(1).getYPos();

        for (int r = minY + 1; r < maxY && !pointIsInHeroCorridor; r++)
            if (map[r][heroCorridor.getNode(1).getXPos()] == map[pointY][pointX]) pointIsInHeroCorridor = true;
        return pointIsInHeroCorridor;
    }

    /**
     * Проверка, находится ли выбранная точка в коридоре с героем  типа "правый поворот"
     *
     * @param pointY       Координата точки по оси Y
     * @param pointX       Координата точки по оси X
     * @param heroCorridor Коридор, в котором сейчас находится герой
     * @return true - да, точка в этом коридоре, false - нет
     */
    private boolean isCurrentPointInHeroCorridorRightTurn(int pointY, int pointX, Corridor heroCorridor) {
        boolean pointIsInHeroCorridor = false;
        int minX = heroCorridor.getNode(1).getXPos();
        int maxX = heroCorridor.getNode(2).getXPos();
        for (int c = minX; c < maxX && !pointIsInHeroCorridor; c++)
            if (map[heroCorridor.getNode(1).getYPos()][c] == map[pointY][pointX]) pointIsInHeroCorridor = true;

        int minY = heroCorridor.getNode(0).getYPos();
        int maxY = heroCorridor.getNode(1).getYPos();
        for (int r = minY + 1; r < maxY && !pointIsInHeroCorridor; r++)
            if (map[r][heroCorridor.getNode(0).getXPos()] == map[pointY][pointX]) pointIsInHeroCorridor = true;
        return pointIsInHeroCorridor;
    }

    /**
     * Проверка, находится ли выбранная точка в коридоре с героем  типа "сверху-вниз"
     *
     * @param pointY       Координата точки по оси Y
     * @param pointX       Координата точки по оси X
     * @param heroCorridor Коридор, в котором сейчас находится герой
     * @return true - да, точка в этом коридоре, false - нет
     */
    private boolean isCurrentPointInHeroCorridorUpToDown(int pointY, int pointX, Corridor heroCorridor) {
        boolean pointIsInHeroCorridor = false;
        for (int r = heroCorridor.getNode(0).getYPos() + 1; r < heroCorridor.getNode(1).getYPos() && !pointIsInHeroCorridor; r++)
            if (map[r][heroCorridor.getNode(0).getXPos()] == map[pointY][pointX]) pointIsInHeroCorridor = true;
        int leftNodeX = Math.min(heroCorridor.getNode(1).getXPos(), heroCorridor.getNode(2).getXPos());
        int rightNodeX = Math.max(heroCorridor.getNode(1).getXPos(), heroCorridor.getNode(2).getXPos());
        for (int c = leftNodeX; c <= rightNodeX && !pointIsInHeroCorridor; c++)
            if (map[heroCorridor.getNode(1).getYPos()][c] == map[pointY][pointX]) pointIsInHeroCorridor = true;
        for (int r = heroCorridor.getNode(2).getYPos() + 1; r < heroCorridor.getNode(3).getYPos() && !pointIsInHeroCorridor; r++)
            if (map[r][heroCorridor.getNode(2).getXPos()] == map[pointY][pointX]) pointIsInHeroCorridor = true;
        return pointIsInHeroCorridor;
    }

    /**
     * Метод для нахождения крайней координаты коридора
     *
     * @param hero      Главный герой
     * @param direction Направление, в котором ищется край коридора
     * @return Позиция, на которой заканчивается коридор
     */
    private Position rayCastingFindCoridorEdgePosition(Hero hero, String direction) {
        Position lastPosition = null;
        int findY = hero.getCoordY();
        int findX = hero.getCoordX();
        int prevFindY = findY;
        int prevFindX = findX;
        int length = 0;
        boolean exit = false;
        while (!exit) {
            switch (direction) {
                case "up" -> findY--;
                case "upRight" -> {
                    findY--;
                    findX++;
                    exit = true;
                }
                case "right" -> findX++;
                case "rightDown" -> {
                    findY++;
                    findX++;
                    exit = true;
                }
                case "down" -> findY++;
                case "downLeft" -> {
                    findY++;
                    findX--;
                    exit = true;
                }
                case "left" -> findX--;
                case "leftUp" -> {
                    findY--;
                    findX--;
                    exit = true;
                }
            }
            length++;
            if (findY < 0 || findY > MAP_HEIGHT - 1 || findX < 0 || findX > MAP_WIDTH - 1) exit = true;
            if (map[findY][findX] == DOOR_SYMBOL || fogMap[findY][findX] == DOOR_SYMBOL || map[findY][findX] == WALL_SYMBOL || map[findY][findX] == ' ' || map[findY][findX] == FLOOR_SYMBOL || isDiagonalDirection(direction))
                exit = true;
            if (!exit || map[findY][findX] == DOOR_SYMBOL || fogMap[findY][findX] == DOOR_SYMBOL || isDiagonalDirection(direction)) {
                prevFindY = findY;
                prevFindX = findX;
                if (map[findY][findX] == DOOR_SYMBOL || fogMap[findY][findX] == DOOR_SYMBOL) length++;
            }
        }
        if (length > 1 || (isDiagonalDirection(direction) && map[prevFindY][prevFindX] != WALL_SYMBOL))
            lastPosition = new Position(prevFindX, prevFindY);
        return lastPosition;
    }

    /**
     * Метод, определяющий, является ли выбранное направление рей-кастинга диагональным
     *
     * @param direction Направление рей-кастинга
     * @return true - да, false - нет
     */
    private boolean isDiagonalDirection(String direction) {
        return direction.equals("upRight") || direction.equals("rightDown") || direction.equals("downLeft") || direction.equals("leftUp");
    }

    /**
     * Метод для отображения коридоров из основной карты в туманную с использованием алгоритма Брезенхема
     *
     * @param hero          Главный герой
     * @param finalPosition Координаты точки, до которой отображается коридорная линия
     */
    private void bresenhamLineOnFogMap(Hero hero, Position finalPosition) {
        int x0 = hero.getCoordX();
        int y0 = hero.getCoordY();
        fogMap[y0][x0] = map[y0][x0];
        int x1 = finalPosition.getXPos();
        int y1 = finalPosition.getYPos();
        int deltaX = Math.abs(x1 - x0);
        int deltaY = Math.abs(y1 - y0);
        int stepX = (x0 < x1) ? 1 : -1;
        int stepY = (y0 < y1) ? 1 : -1;
        int error = deltaX - deltaY;
        while (x0 != x1 || y0 != y1) {
            int error2 = 2 * error;
            if (error2 > -deltaY) {
                error -= deltaY;
                x0 += stepX;
            }
            if (error2 < deltaX) {
                error += deltaX;
                y0 += stepY;
            }
            if (map[y0][x0] == 'M' || map[y0][x0] == 'G') {
                findEnemyDrawFogMap(y0, x0);
            } else fogMap[y0][x0] = map[y0][x0];
        }
        if (isPointADoor(y1, x1, fogMap))
            fogMap[y1][x1] = map[y1][x1];
    }


    /**
     * Ищем в списке врагов нужного нам врага и рисуем его вид предмета на туманную карту
     *
     * @param coordY Текущая координата врага по Y
     * @param coordX Текущая координата врага по X
     */
    private void findEnemyDrawFogMap(int coordY, int coordX) {
        for (Person person : Enemies) {
            if ((person.getCoordY() == coordY && person.getCoordX() == coordX) || (person.getCoordY() == Hero.getInstance().getCoordY() && person.getCoordX() == Hero.getInstance().getCoordX())) {
                if (isEnemyAggressive((Enemy) person, coordY, coordX)) {
                    fogMap[coordY][coordX] = person.getThisIs().charAt(0);
                } else {
                    if (map[coordY][coordX] == 'M') {
                        fogMap[coordY][coordX] = ((Enemy) person).mimic.charAt(0);
                    } else if (map[coordY][coordX] == 'G')
                        ghostDrawFogMap((Enemy) person, coordY, coordX);
                }
                break;
            }
        }
    }

    /**
     * Метод для проверки, агрессивный ли сейчас враг
     *
     * @param enemy  Враг
     * @param coordY Текущая координата врага по Y
     * @param coordX Текущая координата врага по X
     * @return true - враг агрессивный, false - нет
     */
    private boolean isEnemyAggressive(Enemy enemy, int coordY, int coordX) {
        Hero hero = Hero.getInstance();
        int heroX = hero.getCoordX();
        int heroY = hero.getCoordY();
        int dx = heroX - coordX;
        int dy = heroY - coordY;
        int absDX = Math.abs(dx);
        int absDY = Math.abs(dy);
        return absDX <= enemy.getAggressive() && absDY <= enemy.getAggressive();
    }

    /**
     * Отрисовка спокойного призрака на туманной карте
     *
     * @param enemy  Призрак
     * @param coordY Текущая координата врага по Y
     * @param coordX Текущая координата врага по X
     */
    private void ghostDrawFogMap(Enemy enemy, int coordY, int coordX) {
        if (enemy.isVisible())
            fogMap[coordY][coordX] = enemy.getThisIs().charAt(0);
        else
            fogMap[coordY][coordX] = enemy.getCurSymbol().charAt(0);
    }

    /**
     * Метод, который делает исследованные комнаты пустыми на туманной карте
     */
    private void drawFogRoomsWall() {
        Room heroRoom = Hero.getInstance().getHeroRoom();
        for (Room room : gameLevel.getRoomsArray()) {
            if (heroRoom != room && room.isExplored())
                drawFogRoom(room);
        }
    }

    /**
     * Заполняем комнату пустотой на туманной карте, обрисовываем стены и двери
     *
     * @param room Текущая комната
     */
    private void drawFogRoom(Room room) {
        for (int r = room.getUpLeftCorner().getYPos(); r <= room.getDownRightCorner().getYPos(); r++) {
            for (int c = room.getUpLeftCorner().getXPos(); c <= room.getDownRightCorner().getXPos(); c++) {
                if ((r == room.getUpLeftCorner().getYPos() || r == room.getDownRightCorner().getYPos()) || (c == room.getUpLeftCorner().getXPos() || c == room.getDownRightCorner().getXPos())) {
                    if (map[r][c] == WALL_SYMBOL)
                        fogMap[r][c] = WALL_SYMBOL;
                    else fogMap[r][c] = DOOR_SYMBOL;
                } else fogMap[r][c] = ' ';
            }
        }
    }


    /**
     * Метод, позволяющий выяснить, находится ли герой в данный момент в двери
     *
     * @param hero Главный герой
     * @return true - да, сейчас герой в двери. false - нет, герой не в двери
     */
    private boolean isHeroInDoor(Hero hero) {
        boolean isItDoor = false;
        int heroY = hero.getCoordY();
        int heroX = hero.getCoordX();
        if (isPointADoor(heroY, heroX, map))
            isItDoor = true;
        return isItDoor;
    }

    /**
     * Эта точка - дверь?
     *
     * @param coordY Координата точки по Y
     * @param coordX Координата точки по X
     * @param map    Карта, на которой проверяем точку
     * @return true - да, false - нет
     */
    private boolean isPointADoor(int coordY, int coordX, char[][] map) {
        boolean isFarWallToo = false;
        if (map[coordY - 1][coordX] == WALL_SYMBOL && map[coordY + 1][coordX] == WALL_SYMBOL) {
            if ((map[coordY - 2][coordX] == WALL_SYMBOL || map[coordY + 2][coordX] == WALL_SYMBOL) && isNearLessTwoWalls(coordY, coordX))
                isFarWallToo = true;
        } else if (coordX + 1 < MAP_WIDTH && map[coordY][coordX + 1] == WALL_SYMBOL && map[coordY][coordX - 1] == WALL_SYMBOL) {
            if ((map[coordY][coordX + 2] == WALL_SYMBOL || map[coordY][coordX - 2] == WALL_SYMBOL) && isNearLessTwoWalls(coordY, coordX))
                isFarWallToo = true;
        }
        return isFarWallToo;
    }

    /**
     * Возле выбранной точки меньше 2 дверей?
     *
     * @param coordY Координата точки по Y
     * @param coordX Координата токи по X
     * @return true - есть 2 стены и более по диагонали, false - нет
     */
    private boolean isNearLessTwoWalls(int coordY, int coordX) {
        int count = 0;
        if (map[coordY - 1][coordX + 1] == WALL_SYMBOL) count++;
        if (map[coordY + 1][coordX + 1] == WALL_SYMBOL) count++;
        if (map[coordY + 1][coordX - 1] == WALL_SYMBOL) count++;
        if (map[coordY - 1][coordX - 1] == WALL_SYMBOL) count++;
        return count < 2;
    }

    /**
     * Закрашивание выбранной карты пустыми символами
     *
     * @param map Выбранная карта
     */
    private void drawEmptyMap(char[][] map) {
        for (int i = 0; i < MAP_HEIGHT; i++)
            Arrays.fill(map[i], ' ');
    }

    public Backpack getBackpack() {
        return backpack;
    }

    /**
     * Добавление предметов на карту случайным образом
     *
     * @param rand Объект типа Random, который используется для генерации случайных чисел
     */
    public void addItemsToMap(Random rand) {
        for (int i = 0; i < gameLevel.getSpawnItems().size(); i++) {
            int randRoom = gameLevel.getRoomsArray()[rand.nextInt(gameLevel.getRoomsCount())].getCurSector();
            Position pos = isRoomPointFree(randRoom);
            Item item = gameLevel.getSpawnItems().get(i);
            item.setPos(pos);
            map[item.getPos().getYPos()][item.getPos().getXPos()] = item.getSymbol();
        }
    }

    /**
     * Удаление предмета с карты и при наличии свободного места в рюкзаке данного типа помещение его в рюкзак
     *
     * @param hero Главный герой
     * @return Удаленный предмет с карты
     */
    public Item popItemInList(Hero hero) {
        int heroY = hero.getCoordY();
        int heroX = hero.getCoordX();
        Position posItem = new Position();
        posItem.setPosition(heroX, heroY);
        Item item = gameLevel.popItemFromPosition(posItem);
        if (getBackpack().isItFreeBackpackType(item.getType()))
            backpack.add(item);
        else {
            gameLevel.returnItemBackToMap(item);
            setItemOnMapPlace(item);
            item = null;
        }
        if (gameLevel.findHeroRoom() != null)
            setPointOnMap(heroY, heroX);
        else
            setPointCoridorOnMap(heroY, heroX);
        return item;
    }


    /**
     * Применение предмета из рюкзака
     *
     * @param type             Тип предмета
     * @param numberInBackpack Строка с номером, под которым предмет находится в отображаемом списке содержимого рюкзака
     * @return Информамация об успешном/неуспешном применении предмета
     */
    public List<String> useItem(String type, String numberInBackpack) {
        List<String> message = new ArrayList<>();
        try {
            int intNumberInBackpack = Integer.parseInt(numberInBackpack);
            boolean isCorrectNumber = backpack.isCorrectBackpackNumber(type, intNumberInBackpack);
            if (isCorrectNumber) {
                List<Item> curTypeList = backpack.getBackpackTypeList(type);
                String usingItemName = backpack.findUseItemNameInBackpack(curTypeList, intNumberInBackpack);
                Item findItem = backpack.findLastItemInBackpack(type, usingItemName);
                if (Arrays.asList(backpack.getFoods(), backpack.getScrolls(), backpack.getPotions()).contains(curTypeList))
                    message.add(backpack.useFoodScrollPotion(curTypeList, findItem));
                else if (curTypeList == backpack.getWeapons()) {
                    Hero hero = Hero.getInstance();
                    String dropWeaponName = (hero.getCurWeapon() == null) ? "" : hero.getCurWeapon().getName();
                    if (hero.getCurWeapon() != null && (!dropWeaponName.equals(usingItemName))) {
                        message.add(hero.removeStrength());
                        Item dropWeapon = findDropItem("Weapon", dropWeaponName);
                        curTypeList.remove(backpack.findLastItemNumberInBackpack(dropWeapon));
                        hero.setCurWeapon(null);
                    }
                    if (hero.getCurWeapon() == null)
                        message.add(hero.addStrength((Weapon) findItem));
                    else message.add("Этот предмет уже есть в руке!");
                }
                if (curTypeList != backpack.getWeapons())
                    curTypeList.remove(backpack.findLastItemNumberInBackpack(findItem));
            } else message.add("Неправильный ввод!");
        } catch (NumberFormatException e) {
            message.add("Неправильный ввод!");
        }
        return message;
    }

    /**
     * Выброс предмета из рюкзака на игровое поле
     *
     * @param type             Тип выбрасываемого предмета
     * @param numberInBackpack Строка с номером, под которым предмет находится в отображаемом списке содержимого рюкзака
     * @return Информамация об успешном/неуспешном выбросе предмета
     */
    public String dropItem(String type, String numberInBackpack) {
        String message = "Неправильный ввод!";
        try {
            int intNumberInBackpack = Integer.parseInt(numberInBackpack);
            boolean isCorrectNumber = backpack.isCorrectBackpackNumber(type, intNumberInBackpack);
            if (isCorrectNumber) {
                List<Item> curTypeList = backpack.getBackpackTypeList(type);
                Hero hero = Hero.getInstance();
                String dropItemName = backpack.findUseItemNameInBackpack(curTypeList, intNumberInBackpack);
                Item dropItem = findDropItem(type, dropItemName);
                if (type.equals("Weapon") && backpack.isWeaponNameAloneInBackpack(dropItemName) && hero.getCurWeapon() != null && hero.getCurWeapon().getName().equals(dropItemName)) {
                    message = hero.removeStrength();
                    hero.setCurWeapon(null);
                } else message = dropItem.dropInfo();
                curTypeList.remove(backpack.findLastItemNumberInBackpack(dropItem));
            }
        } catch (NumberFormatException e) {
        }
        return message;
    }

    /**
     * Поиск выкидываемого предмета в рюкзаке
     *
     * @param type         Тип выкидываемого предмета
     * @param dropItemName Строка с названием выкидываемого предмета
     * @return Выкидываемый предмет
     */
    private Item findDropItem(String type, String dropItemName) {
        Hero hero = Hero.getInstance();
        Item dropItem = backpack.findLastItemInBackpack(type, dropItemName);
        Position newDropItemPosition = new Position(hero.getCoordX() + 1, hero.getCoordY());
        dropItem.setPos(newDropItemPosition);
        gameLevel.getSpawnItems().add(dropItem);
        setItemOnMapPlace(dropItem);
        return dropItem;
    }

    /**
     * Установка символа предмета ка карту
     *
     * @param item Устанавливаемый предмет
     */
    public void setItemOnMapPlace(Item item) {
        map[item.getPos().getYPos()][item.getPos().getXPos()] = item.getSymbol();
    }

    /**
     * Установка символа пола на карту
     *
     * @param y Координата точки по Y
     * @param x Координата точки по X
     */
    public void setPointOnMap(int y, int x) {
        map[y][x] = FLOOR_SYMBOL;
    }

    /**
     * Установка символа на карту
     *
     * @param y Координата точки по Y
     * @param x Координата точки по X
     */
    public void setSymbOnMap(int y, int x, char c) {
        map[y][x] = c;
    }

    /**
     * Установка символа коридора на карту
     *
     * @param y Координата точки по Y
     * @param x Координата точки по X
     */
    public void setPointCoridorOnMap(int y, int x) {
        map[y][x] = CORRIDOR_SYMBOL;
    }

    /**
     * Генерация количества золота в зависимости от номера уровня и его установка на карту
     *
     * @param rand Объект типа Random, который используется для генерации случайных чисел
     */
    public void setGold(Random rand) {
        int goldCount = itemsCountOnLevel("gold");
        gameLevel.setSpawnGold(new ArrayList<>());

        for (int i = 0; i < goldCount; i++) {
            int spawnGoldCost = rand.nextInt(GameConstants.MAX_GOLD_COST) + 1;
            Gold gold = new Gold("Золото", spawnGoldCost);
            gameLevel.getSpawnGold().add(gold);
        }
        addGoldToMap(rand);
    }

    /**
     * Установка сгенерированного золота случайным образом на карту
     *
     * @param rand Объект типа Random, который используется для генерации случайных чисел
     */
    private void addGoldToMap(Random rand) {
        for (int i = 0; i < gameLevel.getSpawnGold().size(); i++) {
            int randRoom = gameLevel.getRoomsArray()[rand.nextInt(gameLevel.getRoomsCount())].getCurSector();
            Position pos = isRoomPointFree(randRoom);
            Gold gold = gameLevel.getSpawnGold().get(i);
            gold.setPos(pos);
            map[gold.getPos().getYPos()][gold.getPos().getXPos()] = gold.getSymbol();
        }
    }

    /**
     * Логика взаимодействия с золотом при его нахождении героем
     */
    public void findGold() {
        Hero hero = Hero.getInstance();
        Position personPosition = new Position();
        personPosition.setPosition(hero.getCoordX(), hero.getCoordY());
        Gold gold = gameLevel.popGoldFromPosition(personPosition);
        setPointOnMap(hero.getCoordY(), hero.getCoordX());
        hero.setPickUpItem(gold);
        hero.addStatistic("gold", gold.getCoast());
    }

    public Set<Character> getItemSymbolSet() {
        return itemSymbolSet;
    }

    /**
     * Обработка строки-клавиши с ходом героя
     *
     * @param key Введенная клавиша передвижения
     */
    public void inputMoveHero(String key) {
        Hero hero = Hero.getInstance();
        switch (key) {
            case "up" -> hero.move(0, -1);
            case "down" -> hero.move(0, 1);
            case "left" -> hero.move(-1, 0);
            case "right" -> hero.move(1, 0);
        }
        gameLevel.findHeroCorridorInDoor(hero);
    }

    /**
     * Обработка хода персонажа и последующее обновление туманной карты
     *
     * @param person    Текущий персонаж
     * @param curSymbol Символ, на котором сейчас находится персонаж
     * @return Было ли изменение позиции персонажа
     */
    public boolean drawPerson(Person person, char curSymbol) {
        String allowChar = ".D+$";
        boolean itemDraw = false, isChange = false;
        if (allowChar.indexOf(curSymbol) != -1 || getItemSymbolSet().contains(curSymbol)) {
            if ((getItemSymbolSet().contains(curSymbol) || curSymbol == '$') && person instanceof Hero) {
                if (curSymbol == '$')
                    findGold();
                else
                    itemDraw = !(findItem((Hero) person));
            } else {
                itemDraw = true;
            }
            isChange = true;
            this.map[person.getPrevCoordY()][person.getPrevCoordX()] = person.getCurSymbol().charAt(0);
            person.setCurSymbol(String.valueOf(this.map[person.getCoordY()][person.getCoordX()]));
            if (person instanceof Hero) {
                if (curSymbol == CORRIDOR_SYMBOL)
                    ((Hero) person).setHeroRoom(null);
                else if (curSymbol == FLOOR_SYMBOL)
                    ((Hero) person).setHeroCorridor(null);
                else if (curSymbol == DOOR_SYMBOL) {
                    if (((Hero) person).getHeroCorridor() == null)
                        ((Hero) person).setHeroCorridor(gameLevel.findHeroCorridorInDoor((Hero) person));
                    if (((Hero) person).getHeroRoom() == null) ((Hero) person).setHeroRoom(gameLevel.findHeroRoom());
                }
                ((Hero) person).addStatistic("pathCells", 1);
            }
            this.map[person.getCoordY()][person.getCoordX()] = person.getThisIs().charAt(0);
        } else {
            if (person instanceof Hero && curSymbol == EXIT_SYMBOL)
                this.nextLvl = true;
            person.setCoordX(person.getPrevCoordX());
            person.setCoordY(person.getPrevCoordY());
        }
        updateFogMapFromHeroLocation();
        if (itemDraw)
            person.setCurSymbol(Character.toString(curSymbol));
        return isChange;
    }

    /**
     * Обновление туманной карты в зависимости от месторасположения героя
     */
    private void updateFogMapFromHeroLocation() {
        Hero hero = Hero.getInstance();
        Room heroRoom = hero.getHeroRoom();
        Corridor heroCorridor = hero.getHeroCorridor();
        drawFogNoHeroCorridor();
        if (heroRoom != null && heroCorridor != null) {
            drawFogRoomsWall();
            drawCorridorOnFogMap();
            drawHeroRoomOnFogMap();
        } else if (heroRoom != null) {
            drawFogRoomsWall();
            drawHeroRoomOnFogMap();
        } else if (heroCorridor != null) {
            drawFogRoomsWall();
            drawCorridorOnFogMap();
        }
    }

    /**
     * Проверка наличия состояния боя в текущий момент
     *
     * @param person    Текущий персонаж
     * @param curSymbol Символ, на который наступил персонаж
     * @param curPos    Текущая координата персонажа
     * @return Строка о состоянии боя
     */
    public String fight(Person person, char curSymbol, Position curPos) {
        boolean truePerson = person.kindsOfPerson.contains(curSymbol);
        boolean notSame = curSymbol != person.getThisIs().charAt(0);
        boolean notTwoEnemy = !(person instanceof Enemy && Enemy.typeOfEnemy.contains(String.valueOf(curSymbol)));
        if (truePerson && notSame && notTwoEnemy)
            return fightLogic(person, curPos);
        else
            return "";
    }

    /**
     * Функция отвечает за логику действия с предметом
     *
     * @param hero Главный герой, который наступил на предмет
     * @return Поднял (true) или нет предмет
     */
    public boolean findItem(Hero hero) {
        boolean getItem = false;
        Item pickUpItem = popItemInList(hero);
        if (pickUpItem != null) {
            hero.setPickUpItem(pickUpItem);
            getItem = true;
        } else backpack.setFullBackpackType(true);
        return getItem;
    }

    /**
     * Логика боя у текущего персонажа
     *
     * @param person Персонаж
     * @param pos    Позиция персонажа на карте
     * @return Информационная строка о состоянии боя
     */
    public String fightLogic(Person person, Position pos) {
        String resFight;
        Hero hero = Hero.getInstance();

        if (person instanceof Hero) {
            Enemy enemy = (Enemy) findEnemy(pos.getXPos(), pos.getYPos());
            assert enemy != null;
            if (chanceHit(hero, enemy) && !missVampire(enemy)) {
                enemy.getHit(hero.getDamage());
                resFight = String.format("Герой наносит %d урона по %s", hero.getDamage(), Enemy.nameOfEnemy.get(enemy.getThisIs()));
                hero.addStatistic("giveHit", 1);
            } else
                resFight = String.format("Герой промахивается по %s", Enemy.nameOfEnemy.get(enemy.getThisIs()));

            if (enemy.getHealth() <= 0) {
                this.map[enemy.getCoordY()][enemy.getCoordX()] = enemy.getCurSymbol().charAt(0);
                this.fogMap[enemy.getCoordY()][enemy.getCoordX()] = enemy.getCurSymbol().charAt(0);
            }
        } else {
            Person enemy = findEnemy(person.getCoordX(), person.getCoordY());
            if (enemy.getThisIs().equals("O")) ((Enemy) enemy).setOgrAttack(true);
            assert enemy != null;
            if (chanceHit(enemy, hero)) {
                hero.getHit(enemy.getDamage());
                hero.addStatistic("getHit", 1);
                Random random = new Random();
                String effect = "";
                if (random.nextInt(1, 11) <= 1) effect = enemyEffects((Enemy) enemy);
                resFight = String.format("%s наносит %d урона по герою %s", Enemy.nameOfEnemy.get(person.getThisIs()), enemy.getDamage(), effect);
            } else
                resFight = String.format("%s промахивается по герою", Enemy.nameOfEnemy.get(person.getThisIs()));

            if (hero.getHealth() <= 0) {
                this.map[hero.getCoordY()][hero.getCoordX()] = hero.getCurSymbol().charAt(0);
                this.fogMap[hero.getCoordY()][hero.getCoordX()] = hero.getCurSymbol().charAt(0);
            }
        }
        return resFight;
    }

    /**
     * Поиск врага из списка врагов по координате
     *
     * @param x Координата искомого врага по X
     * @param y Координата искомого врага по Y
     * @return Искомый враг
     */
    private Person findEnemy(int x, int y) {
        for (Person enemy : Enemies) {
            if (enemy.getCoordX() == x && enemy.getCoordY() == y)
                return enemy;
        }
        return null;
    }

    /**
     * Функция для особенностей вампира и змея
     *
     * @param enemy Враг
     * @return Примененный эффект для информации
     */
    String enemyEffects(Enemy enemy) {
        String result = "";
        Hero hero = Hero.getInstance();
        if (enemy.getThisIs().equals("S")) {
            hero.setTurn(-10);
            result = "и усыпляет его";
        } else if (enemy.getThisIs().equals("V")) {
            hero.setMaxHealth(hero.getMaxHealth() - (hero.getMaxHealth() / 10));
            result = "и калечит его";
        }

        return result;
    }

    /**
     * Функция для промаха первого удара по вампиру
     */
    boolean missVampire(Enemy enemy) {
        boolean res = enemy.getThisIs().equals("V") && enemy.getMissVampire();
        if (res)
            enemy.setMissVampire(false);
        return res;
    }

    /**
     * Функция, определяющая нанесение удара
     *
     * @param attacker Атакующий персонаж
     * @param defender Защищающийся персонаж
     * @return Будет ли произведен удар
     */
    public boolean chanceHit(Person attacker, Person defender) {
        int chanceToHit = 60 + (attacker.agility - defender.agility) * 3;
        Random random = new Random();
        return random.nextInt(1, 101) <= chanceToHit;
    }

    /**
     * Добавление символа персонажа на карту
     *
     * @param person Персонаж
     */
    private void addPersonToMap(Person person) {
        map[person.getCoordY()][person.getCoordX()] = person.getThisIs().charAt(0);
    }

    /**
     * Создание списка с рекордами
     *
     * @param fromMenu Вызов поля с рекордами из главного меню (true - да, false - нет)
     * @return Сформированный список с рекордами
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public ArrayList<ArrayList<String>> createStatistics(boolean fromMenu) throws IOException {
        DataBase dataBase = DataBase.loadStatistics("dataBase.json");

        if (!fromMenu) {
            ArrayList<String> fields = new ArrayList<>(Arrays.asList("gold", "level", "deadEnemies", "ateFood", "drunkPotion", "readScrolls", "giveHit", "getHit", "pathCells"));
            ArrayList<String> gameInfo = new ArrayList<>();
            ArrayList<ArrayList<String>> statistics = dataBase.getStatistics();
            deleteWorstRecord(dataBase);

            Hero hero = Hero.getInstance();
            HashMap<String, Integer> statistic = hero.getStatistic();
            gameInfo.add(hero.getName());
            for (String field : fields)
                gameInfo.add(statistic.get(field).toString());
            dataBase.addStatistic(gameInfo);
            statistics.sort(Comparator.comparing((ArrayList<String> list) -> Integer.parseInt(list.get(1))).reversed());
            DataBase.saveStatistics(dataBase, "dataBase.json");
        }
        return dataBase.getStatistics();
    }

    /**
     * Удаление наихудшего результата из рекордов и сохранение статистики в файл "dataBase.json"
     *
     * @param dataBase Хранилище рекордов
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public void deleteWorstRecord(DataBase dataBase) throws IOException {
        ArrayList<ArrayList<String>> statistics = dataBase.getStatistics();
        while (statistics.size() > 13) statistics.remove(statistics.size() - 1);
        DataBase.saveStatistics(dataBase, "dataBase.json");
    }
}
