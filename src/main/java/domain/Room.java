package domain;

import java.util.Random;

public class Room implements Comparable<Room> {

    /**
     * Номер сектора комнаты (от 0 до 8)
     */
    private int curSector;

    /**
     * Вертикальная позиция комнаты в решетке (т.к 5*5 сетка, то 0:0 это 1:1)
     */
    private int verticGridPos;

    /**
     * Горизонтальная позиция комнаты в решетке (т.к 5*5 сетка, то 0:0 это 1:1)
     */
    private int horizGridPos;

    /**
     * Ширина комнаты
     */
    private int roomWidth;

    /**
     * Высота комнаты
     */
    private int roomHeight;

    /**
     * Исследована ли комната
     */
    private boolean explored;

    /**
     * Координата левого верхнего угла комнаты на карте
     */
    private Position upLeftCorner;

    /**
     * Координата правого нижнего угла комнаты на карте
     */
    private Position downRightCorner;

    /**
     * Массив с координатами дверей комнаты
     */
    private Position[] doors;

    /**
     * Массив с комнатами-соседями
     */
    private Room[] neighbors;

    /**
     * Конструктор класса Room
     */
    public Room() {
        this.curSector = GameConstants.INIT;
        doors = new Position[GameConstants.MAX_DOORS_NUMBER];
        neighbors = new Room[GameConstants.MAX_DOORS_NUMBER];
        upLeftCorner = new Position();
        downRightCorner = new Position();

        for (int i = 0; i < GameConstants.MAX_DOORS_NUMBER; i++) {
            doors[i] = new Position();
            neighbors[i] = null;
        }
    }

    /**
     * Сравнение комнат по сектору
     *
     * @param other Другая сравниваемая комната
     * @return 0 - сектора равны, иное - нет
     */
    public int compareTo(Room other) {
        return Integer.compare(this.curSector, other.curSector);
    }

    /**
     * Генерация углов и дверей в комнате
     *
     * @param rand Объект типа Random, который используется для генерации случайных чисел
     * @param i    Вертикальный сектор комнаты в решетке (идут с 1:1, т.к сетка 5*5)
     * @param j    Горизонтальный сектор комнаты в решетке (идут с 1:1, т.к сетка 5*5)
     */
    public void generateRoomCornDoor(Random rand, int i, int j) {
        if (curSector != GameConstants.INIT) {
            generateCorners(rand, (i - 1) * GameConstants.SECTOR_HEIGHT, (j - 1) * GameConstants.SECTOR_WIDTH);
            generateDoors(rand);
        }
    }

    /**
     * Генерация углов в комнате
     *
     * @param rand    Объект типа Random, который используется для генерации случайных чисел
     * @param offsetY Координата старта текущего сектора со смещением по Y
     * @param offsetX Координата старта текущего сектора со смещением по X
     */
    private void generateCorners(Random rand, int offsetY, int offsetX) {
        int luY = rand.nextInt(GameConstants.CORNER_VERTIC_RANGE) + offsetY + 1;
        upLeftCorner.setYPos(luY);
        int luX = rand.nextInt(GameConstants.CORNER_HORIZ_RANGE) + offsetX + 1;
        upLeftCorner.setXPos(luX);

        int rdY = GameConstants.SECTOR_HEIGHT - rand.nextInt(GameConstants.CORNER_VERTIC_RANGE) + offsetY - 1;
        downRightCorner.setYPos(rdY);
        int rdX = GameConstants.SECTOR_WIDTH - rand.nextInt(GameConstants.CORNER_HORIZ_RANGE) + offsetX - 1;
        downRightCorner.setXPos(rdX);
        roomHeight = rdY - luY + 1;
        roomWidth = rdX - luX + 1;
    }

    /**
     * Генерация дверей в комнате
     *
     * @param rand Объект типа Random, который используется для генерации случайных чисел
     */
    private void generateDoors(Random rand) {
        if (getNeighbor(GameConstants.UP_POS) != null) {
            doors[GameConstants.UP_POS].setYPos(upLeftCorner.getYPos());
            doors[GameConstants.UP_POS].setXPos(rand.nextInt(roomWidth - 2) + upLeftCorner.getXPos() + 1);
        }
        if (getNeighbor(GameConstants.RIGHT_POS) != null) {
            doors[GameConstants.RIGHT_POS].setYPos(rand.nextInt(roomHeight - 2) + upLeftCorner.getYPos() + 1);
            doors[GameConstants.RIGHT_POS].setXPos(downRightCorner.getXPos());
        }
        if (getNeighbor(GameConstants.DOWN_POS) != null) {
            doors[GameConstants.DOWN_POS].setYPos(downRightCorner.getYPos());
            doors[GameConstants.DOWN_POS].setXPos(rand.nextInt(roomWidth - 2) + upLeftCorner.getXPos() + 1);
        }
        if (getNeighbor(GameConstants.LEFT_POS) != null) {
            doors[GameConstants.LEFT_POS].setYPos(rand.nextInt(roomHeight - 2) + upLeftCorner.getYPos() + 1);
            doors[GameConstants.LEFT_POS].setXPos(upLeftCorner.getXPos());
        }
    }

    public int getCurSector() {
        return curSector;
    }

    public void setCurSector(int sector) {
        this.curSector = sector;
    }

    public int getVerticGridPos() {
        return verticGridPos;
    }

    public void setVerticGridPos(int verticGridPos) {
        this.verticGridPos = verticGridPos;
    }

    public int getHorizGridPos() {
        return horizGridPos;
    }

    public void setHorizGridPos(int horizGridPos) {
        this.horizGridPos = horizGridPos;
    }

    public Position getUpLeftCorner() {
        return upLeftCorner;
    }

    public void setUpLeftCorner(Position upLeftCorner) {
        this.upLeftCorner = upLeftCorner;
    }

    public Position getDownRightCorner() {
        return downRightCorner;
    }

    public void setDownRightCorner(Position downRightCorner) {
        this.downRightCorner = downRightCorner;
    }

    public Position[] getDoors() {
        return doors;
    }

    public void setDoors(Position[] doors) {
        this.doors = doors;
    }

    public Room getNeighbor(int number) {
        return neighbors[number];
    }

    public void setNeighbor(int number, Room neighbor) {
        this.neighbors[number] = neighbor;
    }

    public int getRoomWidth() {
        return roomWidth;
    }

    public void setRoomWidth(int roomWidth) {
        this.roomWidth = roomWidth;
    }

    public int getRoomHeight() {
        return roomHeight;
    }

    public void setRoomHeight(int roomHeight) {
        this.roomHeight = roomHeight;
    }

    public boolean isExplored() {
        return explored;
    }

    public void setExplored(boolean explored) {
        this.explored = explored;
    }
}
