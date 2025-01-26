package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Level {

    /**
     * Количество комнат на уровне, отсчет от 1
     */
    private int roomsCount;

    /**
     * Количество коридоров на уровне
     */
    private int corridorsCount;

    /**
     * Двумерный массив комнат на уровне наподобие решетки 5*5 для буфферной зоны (сами комнаты располагаются в решетке 3*3)
     */
    private Room[][] roomsGrid;

    /**
     * Массив комнат на уровне, в котором идет отсчет от 0 до возможно макисмального числа комнат - 8
     */
    private Room[] roomsArray;

    /**
     * Массив коридоров на уровне
     */
    private Corridor[] corridorsArray;

    /**
     * Число, обозначающее сектор стартовой комнаты в решетке (не в списке, от 0 и до макс кол сгенерированных комнат - 1)
     */
    private int startIntRoom;

    /**
     * Число, обозначающее сектор финальной комнаты в решетке (не в списке, от 0 и до макс кол сгенерированных комнат - 1)
     */
    private int endIntRoom;

    /**
     * Координаты двери для выхода с уровня
     */
    private Position exitDoor;

    /**
     * Список с предметами, которые присутствуют на уровне, в данный момент лежат на карте, игрок их еще не подобрал
     */
    private List<Item> spawnItems;

    /**
     * Список с золотом, которое присутствует на уровне, в данный момент лежит на карте, игрок его еще не подобрал
     */
    private List<Gold> spawnGold;

    /**
     * Конструктор класса Level
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public Level() {
        Random rand = new Random();
        initLevel(rand);
        setNeighbors();
        generateGeometryRooms(rand);
        generateExitDoor();
        generateCorridors(rand);
    }

    /**
     * Метод применяемый для инициализции нового уровня
     *
     * @param rand Объект типа Random, который используется для генерации случайных чисел
     */
    private void initLevel(Random rand) {
        this.roomsCount = 0;
        this.corridorsCount = 0;
        roomsGrid = new Room[GameConstants.ROOMS_ON_LINE + 2][GameConstants.ROOMS_ON_LINE + 2];
        roomsArray = new Room[GameConstants.MAX_ROOMS_NUMBER];
        corridorsArray = new Corridor[GameConstants.MAX_CORRIDORS_NUMBER];
        exitDoor = new Position();
        for (int i = 0; i < roomsGrid.length; i++) {
            for (int j = 0; j < roomsGrid[i].length; j++)
                roomsGrid[i][j] = new Room();
        }
        for (int r = 0; r < GameConstants.MAX_ROOMS_NUMBER; r++)
            roomsArray[r] = null;
        for (int i = 0; i < GameConstants.MAX_CORRIDORS_NUMBER; i++)
            corridorsArray[i] = new Corridor();
        generateGrid(rand);
        chooseStartEndRooms(rand);
    }


    /**
     * Метод для генерации новой решетки с комнатами 5*5
     *
     * @param rand Объект типа Random, который используется для генерации случайных чисел
     */
    private void generateGrid(Random rand) {
        while (roomsCount < 3) {
            int curSector = 0;
            for (int i = 1; i < GameConstants.ROOMS_ON_LINE + 1; i++) {
                for (int j = 1; j < GameConstants.ROOMS_ON_LINE + 1; j++, curSector++) {
                    if (rand.nextDouble() < GameConstants.GENERATE_ROOM_CHANCE && roomsGrid[i][j].getCurSector() == GameConstants.INIT) {
                        roomsGrid[i][j].setCurSector(curSector);
                        roomsGrid[i][j].setVerticGridPos(i);
                        roomsGrid[i][j].setHorizGridPos(j);
                        roomsArray[roomsCount++] = roomsGrid[i][j];
                    }
                }
            }
        }
        roomsArray = Arrays.stream(roomsArray)
                .filter(Objects::nonNull)
                .toArray(Room[]::new);
        Arrays.sort(roomsArray);
    }

    /**
     * Метод для проверки существования сгенерированной комнаты и последующей генерации у нее дверей и углов
     *
     * @param rand Объект типа Random, который используется для генерации случайных чисел
     */
    private void generateGeometryRooms(Random rand) {
        for (int i = 1; i < GameConstants.ROOMS_ON_LINE + 1; i++) {
            for (int j = 1; j < GameConstants.ROOMS_ON_LINE + 1; j++) {
                if (roomsGrid[i][j] != null)
                    roomsGrid[i][j].generateRoomCornDoor(rand, i, j);
            }
        }
    }

    /**
     * Метод для случайного выбора стартовой и финальной комнаты
     *
     * @param rand Объект типа Random, который используется для генерации случайных чисел
     */
    private void chooseStartEndRooms(Random rand) {
        startIntRoom = roomsArray[rand.nextInt(roomsCount)].getCurSector();
        do {
            endIntRoom = roomsArray[rand.nextInt(roomsCount)].getCurSector();
        } while (startIntRoom == endIntRoom);
    }

    /**
     * Метод, позволяющий установить ближайщих и дальних соседей у комнаты
     */
    private void setNeighbors() {
        setNearNeighbors();
        setDistantNeighbors();
    }

    /**
     * Метод, позволяющий установить ближайщих соседей у комнаты
     */
    private void setNearNeighbors() {
        for (int i = 1; i < GameConstants.ROOMS_ON_LINE + 1; i++) {
            for (int j = 1; j < GameConstants.ROOMS_ON_LINE + 1; j++) {
                if (roomsGrid[i][j].getCurSector() != GameConstants.INIT) {
                    if (roomsGrid[i - 1][j].getCurSector() != GameConstants.INIT)
                        roomsGrid[i][j].setNeighbor(GameConstants.UP_POS, roomsGrid[i - 1][j]);
                    if (roomsGrid[i][j + 1].getCurSector() != GameConstants.INIT)
                        roomsGrid[i][j].setNeighbor(GameConstants.RIGHT_POS, roomsGrid[i][j + 1]);
                    if (roomsGrid[i + 1][j].getCurSector() != GameConstants.INIT)
                        roomsGrid[i][j].setNeighbor(GameConstants.DOWN_POS, roomsGrid[i + 1][j]);
                    if (roomsGrid[i][j - 1].getCurSector() != GameConstants.INIT)
                        roomsGrid[i][j].setNeighbor(GameConstants.LEFT_POS, roomsGrid[i][j - 1]);
                }
            }
        }
    }

    /**
     * Метод, позволяющий установить дальних соседей у комнаты
     */
    private void setDistantNeighbors() {
        for (int i = 0; i < roomsCount - 1; i++) {
            Room cur = roomsArray[i], next = roomsArray[i + 1];
            if (cur.getVerticGridPos() == next.getVerticGridPos() && next.getNeighbor(GameConstants.LEFT_POS) == null) {
                cur.setNeighbor(GameConstants.RIGHT_POS, next);
                next.setNeighbor(GameConstants.LEFT_POS, cur);
            } else if (cur.getVerticGridPos() - next.getVerticGridPos() == -1 && cur.getNeighbor(GameConstants.DOWN_POS) == null) {
                if (cur.getHorizGridPos() < next.getHorizGridPos() && next.getNeighbor(GameConstants.LEFT_POS) == null) {
                    cur.setNeighbor(GameConstants.DOWN_POS, next);
                    next.setNeighbor(GameConstants.LEFT_POS, cur);
                } else if (cur.getHorizGridPos() > next.getHorizGridPos() && next.getNeighbor(GameConstants.RIGHT_POS) == null) {
                    cur.setNeighbor(GameConstants.DOWN_POS, next);
                    next.setNeighbor(GameConstants.RIGHT_POS, cur);
                } else if (cur.getHorizGridPos() > next.getHorizGridPos() && cur.getNeighbor(GameConstants.DOWN_POS) == null && i < roomsCount - 2) {
                    cur.setNeighbor(GameConstants.DOWN_POS, roomsArray[i + 2]);
                    roomsArray[i + 2].setNeighbor(GameConstants.RIGHT_POS, cur);
                }
            } else if (cur.getVerticGridPos() - next.getVerticGridPos() == -2 && next.getNeighbor(GameConstants.UP_POS) == null) {
                cur.setNeighbor(GameConstants.DOWN_POS, next);
                next.setNeighbor(GameConstants.UP_POS, cur);
            }
        }
    }

    /**
     * Метод для установки координат финальной двери с уровня
     */
    private void generateExitDoor() {
        exitDoor = chooseRandRoomPoint(endIntRoom);
    }

    /**
     * Метод для выбора рандомной точки в комнате
     *
     * @param curRoom число, обозначающее сектор в решетке 5*5
     * @return Координата рандодмной точки
     */
    public Position chooseRandRoomPoint(int curRoom) {
        Position pos = new Position();
        int indexRoomY = curRoom / 3 + 1;
        int indexRoomX = curRoom % 3 + 1;
        Random rand = new Random();
        Room roomGrid = roomsGrid[indexRoomY][indexRoomX];

        int coordY, coordX;
        do {
            int randY = rand.nextInt(roomGrid.getRoomHeight() - 2);
            int randX = rand.nextInt(roomGrid.getRoomWidth() - 2);
            coordY = roomGrid.getUpLeftCorner().getYPos() + 1 + randY;
            coordX = roomGrid.getUpLeftCorner().getXPos() + 1 + randX;
        } while (isRoomPointNearDoor(coordY, coordX, roomGrid));

        pos.setPosition(coordX, coordY);
        return pos;
    }

    /**
     * Дополнительный метод для поиска рандомной точки в комнате
     *
     * @param curRoom Номер комнаты в массиве комнат
     * @return Координата рандомной точки
     */
    public Position trueRandPointRoom(int curRoom) {
        Random rand = new Random();
        Room room = roomsArray[curRoom];
        int height = room.getUpLeftCorner().getYPos() + room.getRoomHeight() - 2;
        int width = room.getUpLeftCorner().getXPos() + room.getRoomWidth() - 2;
        int randY = rand.nextInt(room.getUpLeftCorner().getYPos() + 1, height);
        int randX = rand.nextInt(room.getUpLeftCorner().getXPos() + 1, width);
        return new Position(randX, randY);
    }

    /**
     * Проверка, находится для выбранная точка рядом с дверью комнаты
     *
     * @param pointY Координата точки по Y
     * @param pointX Координата точки по X
     * @param room   Комната, в которой происходит поиск
     * @return true - да, точка рядом с выходом, false - нет
     */
    private boolean isRoomPointNearDoor(int pointY, int pointX, Room room) {
        boolean isNear = false;
        Position[] doors = room.getDoors();
        int[][] directions = {{1, 0}, {0, -1}, {-1, 0}, {0, 1}};
        for (int i = 0; i < doors.length && !isNear; i++) {
            Position door = doors[i];
            if (door.getYPos() != GameConstants.INIT) {
                int doorY = door.getYPos();
                int doorX = door.getXPos();
                if (doorY + directions[i][0] == pointY && doorX + directions[i][1] == pointX)
                    isNear = true;
            }
        }
        return isNear;
    }

    /**
     * Метод для нахождения финальной комнаты
     *
     * @return Комната с уровня
     */
    private Room getEndRoom() {
        int indexRoomY = endIntRoom / GameConstants.ROOMS_ON_LINE + 1;
        int indexRoomX = endIntRoom % GameConstants.ROOMS_ON_LINE + 1;
        return roomsGrid[indexRoomY][indexRoomX];
    }

    /**
     * Метод для нахождения стартовой комнаты на уровне
     *
     * @return Стартовая комната
     */
    public Room getStartRoom() {
        int indexRoomY = startIntRoom / GameConstants.ROOMS_ON_LINE + 1;
        int indexRoomX = startIntRoom % GameConstants.ROOMS_ON_LINE + 1;
        return roomsGrid[indexRoomY][indexRoomX];
    }

    /**
     * Метод для генерации коридоров уровня
     *
     * @param rand Объект типа Random, который используется для генерации случайных чисел
     */
    private void generateCorridors(Random rand) {
        for (int i = 1; i < GameConstants.ROOMS_ON_LINE + 1; i++) {
            for (int j = 1; j < GameConstants.ROOMS_ON_LINE + 1; j++) {
                Room curRoom = roomsGrid[i][j];
                if (curRoom.getNeighbor(GameConstants.RIGHT_POS) != null && curRoom.getNeighbor(GameConstants.RIGHT_POS).getNeighbor(GameConstants.LEFT_POS) == curRoom)
                    genLeftToRightCorridor(rand, i, j);
                if (curRoom.getNeighbor(GameConstants.DOWN_POS) != null) {
                    int verticalDifferent = curRoom.getVerticGridPos() - curRoom.getNeighbor(GameConstants.DOWN_POS).getVerticGridPos();
                    int horizontalDifferent = curRoom.getHorizGridPos() - curRoom.getNeighbor(GameConstants.DOWN_POS).getHorizGridPos();

                    if (verticalDifferent == -1 && horizontalDifferent > 0)
                        genLeftTurnCorridor(i, j);
                    else if (verticalDifferent == -1 && horizontalDifferent < 0)
                        genRightTurnCorridor(i, j);
                    else
                        genUpToDownCorridor(rand, i, j);
                }
            }
        }
    }

    /**
     * Генерация коридора типа "слева-направо"
     *
     * @param rand Объект типа Random, который используется для генерации случайных чисел
     * @param i    Вертикальная позиция сектора в решетке комнат 5*5
     * @param j    Горизонтальная позиция сектора в решетке комнат 5*5
     */
    private void genLeftToRightCorridor(Random rand, int i, int j) {
        Corridor cor = corridorsArray[corridorsCount];
        cor.setCorType(GameConstants.LEFT_TO_RIGHT_CORRIDOR);
        cor.setNodesCount(GameConstants.MAX_CORRIDOR_NODES);

        Room leftRoom = roomsGrid[i][j];
        Room rightRoom = leftRoom.getNeighbor(GameConstants.RIGHT_POS);

        Position firstNode = leftRoom.getDoors()[GameConstants.RIGHT_POS];
        Position finNode = rightRoom.getDoors()[GameConstants.LEFT_POS];
        cor.setNode(0, firstNode);
        cor.setNode(3, finNode);

        int xMin = firstNode.getXPos();
        int xMax = finNode.getXPos();

        for (int r = 1; r < GameConstants.ROOMS_ON_LINE + 1; r++) {
            Room neighbor = roomsGrid[r][leftRoom.getHorizGridPos()];
            if (neighbor.getCurSector() != GameConstants.INIT && r != leftRoom.getVerticGridPos())
                xMin = Math.max(neighbor.getDownRightCorner().getXPos(), xMin);
        }
        for (int r = 1; r < GameConstants.ROOMS_ON_LINE + 1; r++) {
            Room neighbor = roomsGrid[r][rightRoom.getHorizGridPos()];
            if (neighbor.getCurSector() != GameConstants.INIT && r != rightRoom.getVerticGridPos())
                xMax = Math.min(roomsGrid[r][rightRoom.getHorizGridPos()].getUpLeftCorner().getXPos(), xMax);
        }

        int randCenterX = rand.nextInt(xMax - xMin - 1) + 1 + xMin;

        Position secondNode = new Position();
        secondNode.setPosition(randCenterX, leftRoom.getDoors()[GameConstants.RIGHT_POS].getYPos());
        Position thirdNode = new Position();
        thirdNode.setPosition(randCenterX, rightRoom.getDoors()[GameConstants.LEFT_POS].getYPos());
        cor.setNode(1, secondNode);
        cor.setNode(2, thirdNode);
        corridorsCount++;
    }

    /**
     * Генерация коридора типа "левый поворот"
     *
     * @param i Вертикальная позиция сектора в решетке комнат 5*5
     * @param j Горизонтальная позиция сектора в решетке комнат 5*5
     */
    private void genLeftTurnCorridor(int i, int j) {
        Corridor cor = corridorsArray[corridorsCount];
        cor.setCorType(GameConstants.LEFT_TURN_CORRIDOR);
        cor.setNodesCount(GameConstants.MAX_CORRIDOR_NODES - 1);
        Position firstNode = roomsGrid[i][j].getDoors()[GameConstants.DOWN_POS];
        Position finNode = roomsGrid[i][j].getNeighbor(GameConstants.DOWN_POS).getDoors()[GameConstants.RIGHT_POS];
        cor.setNode(0, firstNode);
        cor.setNode(2, finNode);

        Position middleNode = new Position();
        middleNode.setPosition(firstNode.getXPos(), finNode.getYPos());
        cor.setNode(1, middleNode);
        corridorsCount++;
    }

    /**
     * Генерация коридора типа "правый поворот"
     *
     * @param i Вертикальная позиция сектора в решетке комнат 5*5
     * @param j Горизонтальная позиция сектора в решетке комнат 5*5
     */
    private void genRightTurnCorridor(int i, int j) {
        Corridor cor = corridorsArray[corridorsCount];
        cor.setCorType(GameConstants.RIGHT_TURN_CORRIDOR);
        cor.setNodesCount(GameConstants.MAX_CORRIDOR_NODES - 1);
        Position firstNode = roomsGrid[i][j].getDoors()[GameConstants.DOWN_POS];
        Position finNode = roomsGrid[i][j].getNeighbor(GameConstants.DOWN_POS).getDoors()[GameConstants.LEFT_POS];
        cor.setNode(0, firstNode);
        cor.setNode(2, finNode);

        Position middleNode = new Position();
        middleNode.setPosition(firstNode.getXPos(), finNode.getYPos());
        cor.setNode(1, middleNode);
        corridorsCount++;
    }

    /**
     * Генерация коридора типа "сверху-вниз"
     *
     * @param rand Объект типа Random, который используется для генерации случайных чисел
     * @param i    Вертикальная позиция сектора в решетке комнат 5*5
     * @param j    Горизонтальная позиция сектора в решетке комнат 5*5
     */
    private void genUpToDownCorridor(Random rand, int i, int j) {
        Corridor cor = corridorsArray[corridorsCount];
        cor.setCorType(GameConstants.UP_TO_DOWN_CORRIDOR);
        cor.setNodesCount(GameConstants.MAX_CORRIDOR_NODES);
        Room upRoom = roomsGrid[i][j];
        Room downRoom = upRoom.getNeighbor(GameConstants.DOWN_POS);
        Position firstNode = upRoom.getDoors()[GameConstants.DOWN_POS];
        Position finNode = downRoom.getDoors()[GameConstants.UP_POS];
        cor.setNode(0, firstNode);
        cor.setNode(3, finNode);

        int yMin = firstNode.getYPos();
        int yMax = finNode.getYPos();

        for (int c = 1; c < GameConstants.ROOMS_ON_LINE; c++) {
            Room neighbor = roomsGrid[upRoom.getVerticGridPos()][c];
            if (neighbor.getCurSector() != GameConstants.INIT)
                yMin = Math.max(neighbor.getDownRightCorner().getYPos(), yMin);
        }

        for (int c = 1; c < GameConstants.ROOMS_ON_LINE; c++) {
            Room neighbor = roomsGrid[downRoom.getVerticGridPos()][c];
            if (neighbor.getCurSector() != GameConstants.INIT)
                yMax = Math.min(neighbor.getUpLeftCorner().getYPos(), yMax);
        }

        int randCenterY = rand.nextInt(yMax - yMin - 1) + 1 + yMin;
        Position secondNode = new Position();
        secondNode.setPosition(upRoom.getDoors()[GameConstants.DOWN_POS].getXPos(), randCenterY);
        Position thirdNode = new Position();
        thirdNode.setPosition(downRoom.getDoors()[GameConstants.UP_POS].getXPos(), randCenterY);
        cor.setNode(1, secondNode);
        cor.setNode(2, thirdNode);
        corridorsCount++;
    }


    /**
     * Метод для удаления предмета из массива предметов уровня
     *
     * @param posPerson Координата предмета, который удаляется из массива
     * @return Удаляемый предмет
     */
    public Item popItemFromPosition(Position posPerson) {
        boolean find = false;
        Item findItem = null;
        for (int i = 0; i < spawnItems.size() && !find; i++) {
            Item itemList = spawnItems.get(i);
            Position posList = itemList.getPos();
            if (posList.getYPos() == posPerson.getYPos() && posList.getXPos() == posPerson.getXPos()) {
                find = true;
                findItem = spawnItems.get(i);
                spawnItems.remove(i);
            }
        }
        return findItem;
    }

    /**
     * Метод для удаления золота из массива золота уровня
     *
     * @param personPosition Координата героя
     * @return Удаляемое золото
     */
    public Gold popGoldFromPosition(Position personPosition) {
        boolean find = false;
        Gold findGold = null;
        for (int i = 0; i < spawnGold.size() && !find; i++) {
            Gold goldInList = spawnGold.get(i);
            Position goldInListPosition = goldInList.getPos();
            if (goldInListPosition.getYPos() == personPosition.getYPos() && goldInListPosition.getXPos() == personPosition.getXPos()) {
                find = true;
                findGold = spawnGold.get(i);
                spawnGold.remove(i);
            }
        }
        return findGold;
    }

    /**
     * Метод для возвращения предмета в список предметов уровня
     *
     * @param backItem Возвращаемый предмет
     */
    public void returnItemBackToMap(Item backItem) {
        spawnItems.add(backItem);
    }

    /**
     * Дополнительный метод для поиска начальной комнаты на уровне
     *
     * @param x Координата героя по оси X
     * @param y Координата героя по оси Y
     * @return Индекс стартовой комнаты в массиве комнат уровня
     */
    public int trueGetStartRoom(int x, int y) {
        int length = roomsArray.length;
        for (int i = 0; i < length; ++i) {
            int beginRoomX = roomsArray[i].getUpLeftCorner().getXPos() + 1;
            int endRoomX = roomsArray[i].getUpLeftCorner().getXPos() + roomsArray[i].getRoomWidth() - 1;
            int beginRoomY = roomsArray[i].getUpLeftCorner().getYPos() + 1;
            int endRoomY = roomsArray[i].getUpLeftCorner().getYPos() + roomsArray[i].getRoomHeight() - 1;
            if (beginRoomX <= x && x <= endRoomX && beginRoomY <= y && y <= endRoomY)
                return i;
        }
        return -1;
    }

    /**
     * Поиск комнаты с героем в массиве комнат по координатам героя
     *
     * @return Room Комната с героем
     */
    public Room findHeroRoom() {
        Room heroRoom = null;
        Hero hero = Hero.getInstance();
        int heroY = hero.coordY;
        int heroX = hero.coordX;
        for (Room room : roomsArray) {
            if (room != null) {
                if (isPointInRoom(heroY, heroX, room)) {
                    heroRoom = room;
                    break;
                }
            }
        }
        return heroRoom;
    }

    /**
     * Поиск коридора с героем, когда герой находится в двери
     *
     * @param hero Главный герой
     * @return Коридор, в котором находится главный герой
     */
    public Corridor findHeroCorridorInDoor(Hero hero) {
        Corridor heroCorridor = null;
        int heroY = hero.getCoordY();
        int heroX = hero.getCoordX();
        for (Corridor corridor : corridorsArray) {
            if (corridor != null) {
                if (corridor.getCorType() == GameConstants.LEFT_TO_RIGHT_CORRIDOR) {
                    int firstNodeY = corridor.getNode(0).getYPos();
                    int firstNodeX = corridor.getNode(0).getXPos();
                    int finNodeY = corridor.getNode(3).getYPos();
                    int finNodeX = corridor.getNode(3).getXPos();
                    if (heroY == firstNodeY && heroX == firstNodeX) heroCorridor = corridor;
                    else if (heroY == finNodeY && heroX == finNodeX) heroCorridor = corridor;
                } else if (corridor.getCorType() == GameConstants.LEFT_TURN_CORRIDOR) {
                    int firstNodeY = corridor.getNode(0).getYPos();
                    int firstNodeX = corridor.getNode(0).getXPos();
                    int finNodeY = corridor.getNode(2).getYPos();
                    int finNodeX = corridor.getNode(2).getXPos();
                    if (heroY == firstNodeY && heroX == firstNodeX) heroCorridor = corridor;
                    else if (heroY == finNodeY && heroX == finNodeX) heroCorridor = corridor;
                } else if (corridor.getCorType() == GameConstants.RIGHT_TURN_CORRIDOR) {
                    int firstNodeY = corridor.getNode(0).getYPos();
                    int firstNodeX = corridor.getNode(0).getXPos();
                    int finNodeY = corridor.getNode(2).getYPos();
                    int finNodeX = corridor.getNode(2).getXPos();
                    if (heroY == firstNodeY && heroX == firstNodeX) heroCorridor = corridor;
                    else if (heroY == finNodeY && heroX == finNodeX) heroCorridor = corridor;
                } else if (corridor.getCorType() == GameConstants.UP_TO_DOWN_CORRIDOR) {
                    int firstNodeY = corridor.getNode(0).getYPos();
                    int firstNodeX = corridor.getNode(0).getXPos();
                    int finNodeY = corridor.getNode(3).getYPos();
                    int finNodeX = corridor.getNode(3).getXPos();
                    if (heroY == firstNodeY && heroX == firstNodeX) heroCorridor = corridor;
                    else if (heroY == finNodeY && heroX == finNodeX) heroCorridor = corridor;
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
     * Проверка на нахождении точки в комнате
     *
     * @param pointYCoord Координата точки по Y
     * @param pointXCoord Координата точки по X
     * @param room        Проверяемая комната
     * @return true - да, точка в комнате, false - нет
     */
    public boolean isPointInRoom(int pointYCoord, int pointXCoord, Room room) {
        boolean isYFit = pointYCoord >= room.getUpLeftCorner().getYPos() && pointYCoord <= room.getDownRightCorner().getYPos();
        boolean isXFit = pointXCoord >= room.getUpLeftCorner().getXPos() && pointXCoord <= room.getDownRightCorner().getXPos();
        return (isYFit && isXFit);
    }

    public int getRoomsCount() {
        return roomsCount;
    }

    public void setRoomsCount(int roomsCount) {
        this.roomsCount = roomsCount;
    }

    public int getCorridorsCount() {
        return corridorsCount;
    }

    public void setCorridorsCount(int corridorsCount) {
        this.corridorsCount = corridorsCount;
    }

    public Room[][] getRoomsGrid() {
        return roomsGrid;
    }

    public Room[] getRoomsArray() {
        return roomsArray;
    }

    public int getStartIntRoom() {
        return startIntRoom;
    }

    public void setStartIntRoom(int startIntRoom) {
        this.startIntRoom = startIntRoom;
    }

    public int getEndIntRoom() {
        return endIntRoom;
    }

    public void setEndIntRoom(int endIntRoom) {
        this.endIntRoom = endIntRoom;
    }

    public Position getExitDoor() {
        return exitDoor;
    }

    public Corridor[] getCorridorsArray() {
        return corridorsArray;
    }

    public List<Item> getSpawnItems() {
        return spawnItems;
    }

    @JsonProperty("spawnItems")
    public void setSpawnItems(List<Item> spawnItems) {
        this.spawnItems = spawnItems;
    }

    public List<Gold> getSpawnGold() {
        return spawnGold;
    }

    @JsonProperty("spawnGold")
    public void setSpawnGold(List<Gold> spawnGold) {
        this.spawnGold = spawnGold;
    }
}
