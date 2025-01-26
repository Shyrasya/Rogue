package domain;

public class GameConstants {
    public static final int ROOMS_ON_LINE = 3;
    public static final int INIT = -1;
    public static final int MAX_ROOMS_NUMBER = ROOMS_ON_LINE * ROOMS_ON_LINE;
    public static final int MAX_CORRIDORS_NUMBER = 12;
    public static final int MAX_DOORS_NUMBER = 4;
    public static final double GENERATE_ROOM_CHANCE = 0.5;
    public static final int MAP_HEIGHT = 30;
    public static final int MAP_WIDTH = 90;
    public static final int SECTOR_HEIGHT = MAP_HEIGHT / ROOMS_ON_LINE;
    public static final int SECTOR_WIDTH = MAP_WIDTH / ROOMS_ON_LINE;
    public static final int CORNER_VERTIC_RANGE = (SECTOR_HEIGHT - 6) / 2;
    public static final int CORNER_HORIZ_RANGE = (SECTOR_WIDTH - 6) / 2;
    public static final int UP_POS = 0;
    public static final int RIGHT_POS = 1;
    public static final int DOWN_POS = 2;
    public static final int LEFT_POS = 3;
    public static final int MAX_CORRIDOR_NODES = 4;
    public static final int LEFT_TO_RIGHT_CORRIDOR = 0;
    public static final int LEFT_TURN_CORRIDOR = 1;
    public static final int RIGHT_TURN_CORRIDOR = 2;
    public static final int UP_TO_DOWN_CORRIDOR = 3;
    public static final char WALL_SYMBOL = '#';
    public static final char FLOOR_SYMBOL = '.';
    public static final char DOOR_SYMBOL = 'D';
    public static final String HERO_SYMBOL = "H";
    public static final char EXIT_SYMBOL = '|';
    public static final char CORRIDOR_SYMBOL = '+';
    public static final int MAX_NUMBER_EACH_TYPES = 9;
    public static final int MAX_GOLD_COST = 20;
    public static final int STEPS_EFFECT = 20;
    public static final double FOOD_LEVEL_SPAWN_PERCENT = 0.3;
}
