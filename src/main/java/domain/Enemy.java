
package domain;

import java.util.*;

public class Enemy extends Person {
    Random rand = new Random();
    /**
     * Враждебность врага в клетках
     */
    int aggressive;
    /**
     * Возможные символы отображения мимика
     */
    public static ArrayList<String> typeOfMimic = new ArrayList<>(Arrays.asList("@", "&", "^", "§"));
    /**
     * Выбранный символ отображения мимика
     */
    public String mimic;

    /**
     * Символы отображения врагов
     * Z - Зомби
     * V - Вампир
     * G - Приведение
     * O - Огр
     * S - Змея
     * M - Мимик
     */
    public static ArrayList<String> typeOfEnemy = new ArrayList<>(Arrays.asList("Z", "V", "G", "O", "S", "M"));

    /**
     * Основное направление движения врага
     */
    String vector = "Up";
    /**
     * Ситуативное направление движения в одном векторе
     */
    String direction = "Right";

    /**
     * Флаг используется в некоторых паттернах передвижения
     * как сигнал о смене направления движения
     */
    boolean vectorTurn = false;

    /**
     * Флаг используется в паттерне передвижения змеи
     * как сигнал о смене стороны движения
     */
    boolean directionTurn = false;

    /**
     * Переменная используется в паттерне походки вампира.
     * При нечетных ее значениях меняется вектор движения, чтобы враг рисовал
     * походкой букву "V"
     */
    int vampireChangeSide = 0;

    /**
     * Флаг отвечает за первый промах по вампиру
     */
    private boolean missVampire = true;

    /**
     * Флаг отвечает за контратаку огра
     */
    private boolean ogrAttack = false;

    /**
     * Счетчик невидимых/видимых шагов
     */
    private int invisGhost = 10;

    /**
     * Флаг видимости призрака
     */
    private boolean isVisible = false;

    public static final Map<String, String> nameOfEnemy = Map.of(
            "Z", "Зомби",
            "V", "Вампир",
            "G", "Приведение",
            "O", "Огр",
            "S", "Змея",
            "M", "Мимик"
    );

    public Enemy(int x, int y) {
        super(x, y);
        HashMap<String, int[]> statsOfEnemies = new HashMap<>();
        statsOfEnemies.put("Z", new int[]{130, 7, 10, 3});
        statsOfEnemies.put("V", new int[]{130, 13, 10, 4});
        statsOfEnemies.put("G", new int[]{70, 13, 7, 2});
        statsOfEnemies.put("O", new int[]{160, 7, 16, 3});
        statsOfEnemies.put("S", new int[]{100, 16, 10, 4});
        statsOfEnemies.put("M", new int[]{130, 13, 7, 2});
        thisIs = typeOfEnemy.get(rand.nextInt(0, 6));

        int[] statsOfEnemy = statsOfEnemies.get(thisIs);
        health = statsOfEnemy[0];
        agility = statsOfEnemy[1];
        strength = statsOfEnemy[2];
        aggressive = statsOfEnemy[3];
        gold = health / 10 + agility + strength;
        turn = agility;
        prevCoordX = coordX;
        prevCoordY = coordY;
        mimic = typeOfMimic.get(rand.nextInt(0, 4));
    }

    /**
     * Общая функция передвижения для врагов. В зоне агрессии все ходят одинаково.
     * Если агрессии нет - по паттернам
     *
     * @param heroX - абсцисса координат героя
     * @param heroY - оордината координат героя
     */
    public void move(int heroX, int heroY) {
        int dx = heroX - coordX;
        int dy = heroY - coordY;
        int absDX = Math.abs(dx);
        int absDY = Math.abs(dy);

        prevCoordX = coordX;
        prevCoordY = coordY;

        if (absDX <= aggressive && absDY <= aggressive) {
            if (thisIs.equals("G")) {
                isVisible = true;
                invisGhost = 9;
            }

            if (absDX > absDY && isFree(coordX + Integer.signum(dx), coordY))
                coordX += Integer.signum(dx);
            else if (absDY > absDX && isFree(coordX, coordY + Integer.signum(dy)))
                coordY += Integer.signum(dy);
            else if (isFree(coordX + Integer.signum(dx), coordY))
                coordX += Integer.signum(dx);
            else
                coordY += Integer.signum(dy);
        } else {
            switch (thisIs) {
                case "V" -> moveVampire();
                case "O" -> moveOgr();
                case "S" -> moveSnake();
                case "G" -> moveGhost();
                case "Z" -> moveZombie();
            }
        }
    }


    /**
     * Паттерн передвижения огра. Огр обходит периметр комнаты по часовой стрелке.
     */
    public void moveOgr() {
        if (curSymbol.equals("+") || curSymbol.equals("D"))
            outFromCorridor();
        else if (direction.equals("Right") && !isFree(coordX + 1, coordY)) {
            direction = "Down";
            coordY++;
        } else if (direction.equals("Down") && !isFree(coordX, coordY + 1)) {
            direction = "Left";
            coordX--;
        } else if (direction.equals("Left") && !isFree(coordX - 1, coordY)) {
            direction = "Up";
            coordY--;
        } else if (direction.equals("Up") && !isFree(coordX, coordY - 1)) {
            direction = "Right";
            coordX++;
        } else if (direction.equals("Right"))
            coordX++;
        else if (direction.equals("Down"))
            coordY++;
        else if (direction.equals("Left"))
            coordX--;
        else if (direction.equals("Up"))
            coordY--;
    }

    /**
     * Паттерн передвижения змеи. Змея передвигается в одном векторе
     * в порядке: один шаг в сторону - один в направлении движения -
     * один в другую сторону (змейкой)
     */
    public void moveSnake() {
        changeDir();
        if (!isFree(coordX, coordY - 1)) {
            vector = "Up";
            directionTurn = true;
        }
        if (!isFree(coordX, coordY + 1)) {
            vector = "Down";
            directionTurn = true;
        }

        if (curSymbol.equals("+") || curSymbol.equals("D")) {
            outFromCorridor();
        } else if (directionTurn) {
            if (vector.equals("Down")) coordY--;
            else coordY++;

            if (direction.equals("Right"))
                direction = "Left";
            else direction = "Right";
            directionTurn = false;
        } else if (direction.equals("Right") && vector.equals("Up") || direction.equals("Left") && vector.equals("Down")) {
            if (isFree(coordX + 1, coordY))
                coordX++;
            else
                coordX--;
            directionTurn = true;
        } else if (direction.equals("Right") && vector.equals("Down") || direction.equals("Left") && vector.equals("Up")) {
            if (isFree(coordX - 1, coordY))
                coordX--;
            else
                coordX++;
            directionTurn = true;
        }
    }

    /**
     * Паттерн передвижения призрака. Призрак обходит площадь комнаты, меняя направление
     * обхода после его завершения.
     */
    public void moveGhost() {
        invisGhost++;
        if (invisGhost > 9) {
            invisGhost = 0;
            isVisible = !isVisible;
        }

        changeDir();
        boolean stack = !isFree(coordX + 1, coordY) && direction.equals("Right") || !isFree(coordX - 1, coordY) && direction.equals("Left");

        if (!isFree(coordX, coordY - 1) && stack)
            vector = "Up";
        if (!isFree(coordX, coordY + 1) && stack)
            vector = "Down";
        if (stack)
            vectorTurn = true;

        if (curSymbol.equals("+") || curSymbol.equals("D")) {
            outFromCorridor();
        } else if (vectorTurn) {
            if (vector.equals("Down")) coordY--;
            else coordY++;

            if (direction.equals("Right"))
                direction = "Left";
            else
                direction = "Right";
            vectorTurn = false;
        } else if (direction.equals("Right")) {
            coordX++;
        } else if (direction.equals("Left")) {
            coordX--;
        }
    }


    /**
     * Паттерн передвижения вампира. Вампир описывает букву "V" при ходьбе.
     */
    public void moveVampire() {
        changeDir();
        boolean stackY = !isFree(coordX, coordY - 1) && vector.equals("Up") ||
                !isFree(coordX, coordY + 1) && vector.equals("Down");
        boolean stackX = !isFree(coordX + 1, coordY) && direction.equals("Right") ||
                !isFree(coordX - 1, coordY) && direction.equals("Left");

        if (stackY || stackX) {
            if (stackX && direction.equals("Right"))
                direction = "Left";
            else if (stackX)
                direction = "Right";

            if (stackY && vector.equals("Up"))
                vector = "Down";
            else if (stackY)
                vector = "Up";
            if (stackY) {
                vampireChangeSide++;
                if (vampireChangeSide % 2 == 0) {
                    if (vector.equals("Up"))
                        vector = "Down";
                    else
                        vector = "Up";
                    vampireChangeSide = 0;
                }
            }
        }

        if (curSymbol.equals("+") || curSymbol.equals("D")) {
            outFromCorridor();
        } else if (vectorTurn) {
            if (vector.equals("Down")) coordY++;
            else coordY--;
            vectorTurn = false;
        } else if (direction.equals("Right")) {
            coordX++;
            vectorTurn = true;
        } else if (direction.equals("Left")) {
            coordX--;
            vectorTurn = true;
        }
    }

    /**
     * Паттерн передвижения Зомби. Зомби ходит случайно.
     */
    public void moveZombie() {
        int randomDirection = rand.nextInt(4);

        switch (randomDirection) {
            case 0 -> {
                coordX++;
                direction = "Right";
            }
            case 1 -> {
                coordX--;
                direction = "Left";
            }
            case 2 -> {
                coordY--;
                direction = "Up";
            }
            case 3 -> {
                coordY++;
                direction = "Down";
            }
        }
    }

    /**
     * Метод выхода из коридора. Идет по направлению пока может,
     * а потом меняет направление
     */
    public void outFromCorridor() {
        switch (direction) {
            case "Right":
                if (isFree(coordX + 1, coordY)) {
                    coordX++;
                    return;
                }
                break;
            case "Down":
                if (isFree(coordX, coordY + 1)) {
                    coordY++;
                    return;
                }
                break;
            case "Left":
                if (isFree(coordX - 1, coordY)) {
                    coordX--;
                    return;
                }
                break;
            case "Up":
                if (isFree(coordX, coordY - 1)) {
                    coordY--;
                    return;
                }
                break;
        }
        chooseRandDir();
    }

    /**
     * Метод для выбора случайного направления в коридоре. Удаляет текущее направление
     * из списка и выбирает свободное из перемешанного списка
     */
    public void chooseRandDir() {
        List<String> directions = new ArrayList<>(Arrays.asList("Right", "Down", "Left", "Up"));
        directions.remove(direction);
        Collections.shuffle(directions);

        for (String dir : directions) {
            switch (dir) {
                case "Right":
                    if (isFree(coordX + 1, coordY)) {
                        coordX++;
                        direction = "Right";
                        return;
                    }
                    break;
                case "Down":
                    if (isFree(coordX, coordY + 1)) {
                        coordY++;
                        direction = "Down";
                        return;
                    }
                    break;
                case "Left":
                    if (isFree(coordX - 1, coordY)) {
                        coordX--;
                        direction = "Left";
                        return;
                    }
                    break;
                case "Up":
                    if (isFree(coordX, coordY - 1)) {
                        coordY--;
                        direction = "Up";
                        return;
                    }
                    break;
            }
        }
    }

    /**
     * Метод для определения возможности хода врага на клетку
     *
     * @param x - абсцисса
     * @param y - ордината
     * @return true - клетка свободна для шага
     */
    public boolean isFree(int x, int y) {
        boolean isFree = false;
        GameSession session = GameSession.getSession();
        char[][] map = session.getMap();
        String allowChar = ".D+$H@&^§";
        char ceilMap = map[y][x];
        if (allowChar.indexOf(ceilMap) != -1) isFree = true;
        return isFree;
    }

    /**
     * Метод корректировки направления после выхода из коридора змеи, призрака или вампира.
     * У них нет направления вверх/вниз
     */
    private void changeDir() {
        if (direction.equals("Up")) {
            if (isFree(coordX + 1, coordY)) direction = "Right";
            else direction = "Left";
        } else if (direction.equals("Down")) {
            if (isFree(coordX - 1, coordY)) direction = "Right";
            else direction = "Left";
        }
    }

    public int gold4Dead() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getGold() {
        return gold;
    }

    public Enemy() {
    }

    public int getAggressive() {
        return aggressive;
    }

    public void setAggressive(int aggressive) {
        this.aggressive = aggressive;
    }

    public void setMissVampire(boolean miss) {
        missVampire = miss;
    }

    public boolean getMissVampire() {
        return missVampire;
    }

    public void setOgrAttack(boolean attack) {
        ogrAttack = attack;
    }

    public boolean getOgrAttack() {
        return ogrAttack;
    }

    public void setInvisGhost(int invisGhost) {
        this.invisGhost = invisGhost;
    }

    public int getInvisGhost() {
        return invisGhost;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}



