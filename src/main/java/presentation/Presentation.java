package presentation;

import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import mainpackage.MainConstants;
import com.googlecode.lanterna.TerminalPosition;

import java.io.IOException;
import java.util.*;

import static com.googlecode.lanterna.input.KeyType.Enter;

public class Presentation {
    public static final String WALL_SYMBOL = "#";
    public static final String FLOOR_SYMBOL = ".";
    public static final String DOOR_SYMBOL = "D";
    public static final String HERO_SYMBOL = "H";
    public static final String EXIT_SYMBOL = "|";
    public static final String CORRIDOR_SYMBOL = "+";
    public static final String ZOMBIE_SYMBOL = "Z";
    public static final String VAMPIRE_SYMBOL = "V";
    public static final String GHOST_SYMBOL = "G";
    public static final String ORC_SYMBOL = "O";
    public static final String SNAKE_SYMBOL = "S";
    public static final String MIMIC_SYMBOL = "M";
    public static final String GOLD_SYMBOL = "$";
    public static final String FOOD_SYMBOL = "@";
    public static final String POTION_SYMBOL = "&";
    public static final String WEAPON_SYMBOL = "^";
    public static final String SCROLL_SYMBOL = "§";

    /**
     * Средняя информационная строка
     */
    static StringBuilder firstString = new StringBuilder(" ");

    /**
     * Верхняя информационная строка
     */
    static StringBuilder secondString = new StringBuilder(" ");

    /**
     * Очищающая строка
     */
    static StringBuilder emptyStr = new StringBuilder(" ".repeat(91));

    /**
     * Карта с отображаемым символом и соответствующим ему цветом
     */
    private static final Map<String, TextColor> symbolToColorMap = new HashMap<>();

    static {
        symbolToColorMap.put(WALL_SYMBOL, TextColor.ANSI.YELLOW);
        symbolToColorMap.put(FLOOR_SYMBOL, TextColor.ANSI.WHITE);
        symbolToColorMap.put(DOOR_SYMBOL, TextColor.ANSI.MAGENTA);
        symbolToColorMap.put(HERO_SYMBOL, TextColor.ANSI.GREEN);
        symbolToColorMap.put(EXIT_SYMBOL, TextColor.ANSI.GREEN);
        symbolToColorMap.put(CORRIDOR_SYMBOL, TextColor.ANSI.YELLOW);
        symbolToColorMap.put(ZOMBIE_SYMBOL, TextColor.ANSI.GREEN);
        symbolToColorMap.put(VAMPIRE_SYMBOL, TextColor.ANSI.RED_BRIGHT);
        symbolToColorMap.put(GHOST_SYMBOL, TextColor.ANSI.WHITE);
        symbolToColorMap.put(ORC_SYMBOL, TextColor.ANSI.YELLOW);
        symbolToColorMap.put(SNAKE_SYMBOL, TextColor.ANSI.YELLOW_BRIGHT);
        symbolToColorMap.put(MIMIC_SYMBOL, TextColor.ANSI.WHITE);
        symbolToColorMap.put(GOLD_SYMBOL, TextColor.ANSI.YELLOW_BRIGHT);
        symbolToColorMap.put(FOOD_SYMBOL, TextColor.ANSI.RED);
        symbolToColorMap.put(POTION_SYMBOL, TextColor.ANSI.CYAN);
        symbolToColorMap.put(WEAPON_SYMBOL, TextColor.ANSI.CYAN);
        symbolToColorMap.put(SCROLL_SYMBOL, TextColor.ANSI.CYAN);
    }

    /**
     * Перечисление цветов и их названий для рисования в терминале
     */
    public enum SymbolColor {
        BLACK("#000000"),
        YELLOW("#FFEB3B"),
        YELLOW_DARK("#FFC009"),
        YELLOW_HARD_DARK("#FF9900"),
        ORANGE("#E53934"),
        ORANGE_DARK("#C62928"),
        WHITE("#FFFFFF");

        private final String colorCode;

        SymbolColor(String colorCode) {
            this.colorCode = colorCode;
        }

        public String getColorCode() {
            return colorCode;
        }
    }

    /**
     * Объект, предоставляющий методы для рисования текста и графики на Screen
     */
    private TextGraphics graphics;

    /**
     * Дисплей-окно, на котором отображается консольная графика игры
     */
    private Screen screen;

    /**
     * Конструктор класса Presentation
     *
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public Presentation() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        terminalFactory.setInitialTerminalSize(new TerminalSize(PresentConstants.TERMINAL_WIDTH, PresentConstants.TERMINAL_HEIGHT));
        Terminal terminal = terminalFactory.createTerminal();
        screen = new TerminalScreen(terminal);
        graphics = screen.newTextGraphics();
        screen.startScreen();
    }

    /**
     * Метод для обновления экрана, выводящее на консоль содержимое буфера
     *
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public void refresh() throws IOException {
        screen.refresh();
    }

    /**
     * Метод, очищающий содержимое экрана
     *
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public void clear() throws IOException {
        screen.clear();
    }

    /**
     * Метод для обработки нажатия любой клавиши
     *
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public void pressAnyButton() throws IOException {
        screen.readInput();
    }

    /**
     * Закрытие экрана, созданного с использованием библиотеки Lanterna
     *
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public void close() throws IOException {
        screen.stopScreen();
    }

    /**
     * Отрисовка игровой карты на экране
     *
     * @param map Карта для отрисовки
     */
    public void drawGameMap(char[][] map) {
        char symbol = ' ';
        for (int i = 0; i < PresentConstants.MAP_HEIGHT; i++) {
            for (int j = 0; j < PresentConstants.MAP_WIDTH; j++) {
                symbol = map[i][j];
                drawMapSymbol(j, i, Character.toString(symbol));
            }
        }
    }

    /**
     * Отрисовка одного символа на карте с сопоставлением соответствующего цвета
     *
     * @param drawX  Координата символа по X
     * @param drawY  Координата символа по Y
     * @param symbol Символ для отрисовки
     */
    public void drawMapSymbol(int drawX, int drawY, String symbol) {
        TextColor color = symbolToColorMap.get(symbol);
        drawSymbol(drawX + PresentConstants.HORIZONTAL_BORDERS, drawY + PresentConstants.VERTICAL_BORDERS, symbol, color);
    }

    /**
     * Отрисовка одного символа на карте
     *
     * @param drawX  Координата символа по X
     * @param drawY  Координата символа по Y
     * @param symbol Символ для отрисовки
     * @param color  Цвет рисуемого символа
     */
    public void drawSymbol(int drawX, int drawY, String symbol, TextColor color) {
        graphics.setForegroundColor(color);
        graphics.putString(drawX, drawY, String.valueOf(symbol));
    }

    /**
     * Вывод строки с параметрами персонажа на экран
     *
     * @param health    Здоровье героя
     * @param maxHealth Максимальное здоровье
     * @param str       Сила
     * @param agil      Ловкость
     * @param lvl       Номер уровня
     * @param gold      Количество полученного золота
     */
    public void strInfo(int health, int maxHealth, int str, int agil, int lvl, int gold) {
        drawSymbol(PresentConstants.HORIZONTAL_BORDERS, PresentConstants.MAP_HEIGHT + PresentConstants.VERTICAL_BORDERS + 1, String.valueOf(emptyStr), TextColor.ANSI.BLACK);
        String info = String.format("Здоровье: %d (%d) Сила: %d Ловкость: %d Уровень: %d Золото: %d", health, maxHealth, str, agil, lvl, gold);
        drawSymbol(PresentConstants.HORIZONTAL_BORDERS, PresentConstants.MAP_HEIGHT + PresentConstants.VERTICAL_BORDERS + 1, info, TextColor.ANSI.YELLOW_BRIGHT);
    }

    /**
     * Вывод информационной строки в левом верхнем углу игрового поля
     *
     * @param message Последнее актуальное сообщение
     */
    public void strMessage(String message) {
        drawSymbol(0, 0, emptyStr.toString(), TextColor.ANSI.BLACK);
        drawSymbol(0, 1, emptyStr.toString(), TextColor.ANSI.BLACK);
        drawSymbol(0, 2, emptyStr.toString(), TextColor.ANSI.BLACK);

        drawSymbol(0, 0, secondString.toString(), TextColor.ANSI.YELLOW);
        drawSymbol(0, 1, firstString.toString(), TextColor.ANSI.YELLOW);
        drawSymbol(0, 2, message, TextColor.ANSI.RED_BRIGHT);

        secondString.setLength(0);
        secondString.append(firstString);

        firstString.setLength(0);
        firstString.append(message);
    }

    /**
     * Вывод содержимого рюкзака на экран в специальном поле
     *
     * @param contentBackpack Список строк с информацией о содержимом рюкзака
     */
    public void printBackpackContent(List<String> contentBackpack) {
        graphics.setForegroundColor(TextColor.ANSI.BLACK);
        int startX = PresentConstants.MAP_WIDTH + 2;
        int startY = PresentConstants.VERTICAL_BORDERS;
        int rectWidth = PresentConstants.TERMINAL_WIDTH - startX + 1;
        int rectHeight = PresentConstants.TERMINAL_HEIGHT - startY + 1;
        TerminalPosition position = new TerminalPosition(startX, startY);
        TerminalSize size = new TerminalSize(rectWidth, rectHeight);
        graphics.fillRectangle(position, size, ' ');

        for (int i = 0; i < contentBackpack.size(); i++)
            drawSymbol(startX, startY + i, contentBackpack.get(i), TextColor.ANSI.WHITE);
    }

    /**
     * Вывод на экран стартового меню
     *
     * @return Статус игры (0 - запущена, 1 - показ рекордов, 2 - выход)
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public MainConstants.Status showStartMenu() throws IOException {
        printGameNameAndCreators();
        MainConstants.Status status = MainConstants.Status.UNDEFINED;
        String input = "Старт";
        drawStartRecords(input);
        while (status == MainConstants.Status.UNDEFINED) {
            refresh();
            KeyStroke keyStroke = screen.readInput();
            if (keyStroke != null) {
                switch (keyStroke.getKeyType()) {
                    case Enter -> status = enterCheckInStartMenu(input);
                    case Escape -> status = MainConstants.Status.EXIT;
                    case Character -> {
                        char keyChar = keyStroke.getCharacter();
                        input = checkCharInput(keyChar, input);
                    }
                }
            }
        }
        return status;
    }

    private void printGameNameAndCreators() {
        String rogue = "РОГАЛИК";
        String creators = "ОТ LESLEYLE И LEMONGRB";
        drawSymbol(PresentConstants.TERMINAL_WIDTH / 2 - rogue.length() / 2, PresentConstants.TERMINAL_HEIGHT / 2 - 4, rogue, TextColor.ANSI.MAGENTA);
        drawSymbol(PresentConstants.TERMINAL_WIDTH / 2 - creators.length() / 2 + 1, PresentConstants.TERMINAL_HEIGHT / 2 - 2, creators, TextColor.ANSI.YELLOW);
    }

    private MainConstants.Status enterCheckInStartMenu(String input) {
        return input.equals("Старт") ? MainConstants.Status.RUNNING : MainConstants.Status.RECORD;
    }

    /**
     * Обработка нажатия клавиш 'w' и 's' для смены активного поля (страт/рекорды) в меню
     *
     * @param keyChar Введенный символ
     * @param input   Строка с активным полем ("старт" или "рекорды")
     * @return Измененная строка с активным полем
     */
    private String checkCharInput(char keyChar, String input) {
        switch (keyChar) {
            case 'w', 's' -> {
                if (input.equals("Старт"))
                    input = "Рекорды";
                else
                    input = "Старт";
                drawStartRecords(input);
            }
        }
        return input;
    }

    /**
     * Отрисовка надписей "старт" и "рекорды" в игровом меню
     *
     * @param activeString Активное поле с надписью
     */
    private void drawStartRecords(String activeString) {
        String start = "Старт";
        String records = "Рекорды";
        if (activeString.equals("Старт")) {
            drawSymbol(PresentConstants.TERMINAL_WIDTH / 2 - start.length() / 2, PresentConstants.TERMINAL_HEIGHT / 2 + 5, start, TextColor.ANSI.GREEN);
            drawSymbol(PresentConstants.TERMINAL_WIDTH / 2 - records.length() / 2, PresentConstants.TERMINAL_HEIGHT / 2 + 7, records, TextColor.ANSI.WHITE);
        } else {
            drawSymbol(PresentConstants.TERMINAL_WIDTH / 2 - start.length() / 2, PresentConstants.TERMINAL_HEIGHT / 2 + 5, start, TextColor.ANSI.WHITE);
            drawSymbol(PresentConstants.TERMINAL_WIDTH / 2 - records.length() / 2, PresentConstants.TERMINAL_HEIGHT / 2 + 7, records, TextColor.ANSI.GREEN);
        }
    }


    /**
     * Вывод таблицы с рекордами на экран
     *
     * @param statistics Информация с рекордами
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public void showStatistics(ArrayList<ArrayList<String>> statistics) throws IOException {
        screen.clear();
        String records = "Таблица рекордов";
        drawSymbol(PresentConstants.TERMINAL_WIDTH / 2 - records.length() / 2, 1, records, TextColor.ANSI.MAGENTA);
        int size = statistics.size();
        int sizeField;
        try {
            sizeField = statistics.get(0).size();
        } catch (Exception e) {
            sizeField = 0;
        }
        drawRectField();

        ArrayList<String> fields = new ArrayList<>(Arrays.asList("Имя", "Золото", "Уровень", "Враги", "Еда", "Зелья", "Свитки", "Исх. ударов", "Вх. ударов", "Шаги"));
        StringBuilder column = new StringBuilder();
        for (int i = 0; i < sizeField; i++) {
            column.append(fields.get(i));
            column.append(" ".repeat(PresentConstants.TABLE_CEIL_LENGTH - fields.get(i).length())).append("|");
        }

        drawSymbol(3, 3, column.toString(), TextColor.ANSI.MAGENTA);

        for (int i = 0; i < size; i++) {
            StringBuilder info = new StringBuilder();
            for (int j = 0; j < sizeField; j++) {
                info.append(statistics.get(i).get(j));
                info.append(" ".repeat(PresentConstants.TABLE_CEIL_LENGTH - statistics.get(i).get(j).length())).append("|");
            }
            drawSymbol(3, 5 + i * 2, info.toString(), TextColor.ANSI.MAGENTA);
        }
        String exit = "Для выхода из рекордов нажмите \"Enter\"";
        drawSymbol(PresentConstants.TERMINAL_WIDTH / 2 - exit.length() / 2, PresentConstants.TERMINAL_HEIGHT - 3, exit, TextColor.ANSI.MAGENTA);
        screen.refresh();

        KeyStroke keyStroke = screen.readInput();
        while (keyStroke.getKeyType() != Enter)
            keyStroke = screen.readInput();
    }

    /**
     * Метод для рисования разделительной полосы в поле с рекордами
     *
     * @param lUpCornerX   Координата левого верхнего угла по X - начала рисования разделительной строки
     * @param lUpCornerY   Координата левого верхнего угла по Y - начала рисования разделительной строки
     * @param rDownCornerX Координата правого нижнего угла по X - конца рисования разделительной строки
     * @param rDownCornerY Координата правого нижнего угла по Y - конца рисования разделительной строки
     */
    private void drawRectangle(int lUpCornerX, int lUpCornerY, int rDownCornerX, int rDownCornerY) {
        int width = rDownCornerX - lUpCornerX + 1;
        StringBuilder line = new StringBuilder("+" + "-".repeat(width - 2) + "+");
        drawSymbol(lUpCornerX, lUpCornerY, line.toString(), TextColor.ANSI.MAGENTA);

        for (int row = 1; row < (rDownCornerY - lUpCornerY); row++) {
            drawSymbol(lUpCornerX, lUpCornerY + row, "|", TextColor.ANSI.MAGENTA);
            drawSymbol(rDownCornerX, lUpCornerY + row, "|", TextColor.ANSI.MAGENTA);
        }
        drawSymbol(lUpCornerX, rDownCornerY, line.toString(), TextColor.ANSI.MAGENTA);
    }

    /**
     * Метод для рисования разделительных полос в поле с рекордами
     */
    private void drawRectField() {
        for (int row = 2; row < PresentConstants.TERMINAL_HEIGHT; row += 2)
            drawRectangle(0, row - 2, PresentConstants.TERMINAL_WIDTH - 2, row);
    }


    /**
     * Обработка нажатия клавиш
     *
     * @return Введенная клавиша в строковом формате
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public String inputKey() throws IOException {
        while (screen.pollInput() != null) {
        }
        String input = "none";
        KeyStroke keyStroke = screen.readInput();
        if (keyStroke != null) {
            switch (keyStroke.getKeyType()) {
                case Escape -> input = "exit";
                case Character -> {
                    char keyChar = keyStroke.getCharacter();
                    switch (keyChar) {
                        case 'w' -> input = "up";
                        case 's' -> input = "down";
                        case 'a' -> input = "left";
                        case 'd' -> input = "right";
                        case 'j' -> input = "Food";
                        case 'k' -> input = "Potion";
                        case 'e' -> input = "Scroll";
                        case 'h' -> input = "Weapon";
                        case 'f' -> input = "drop";
                        case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> input = String.valueOf(keyChar);
                    }
                }
            }
        }
        return input;
    }

    /**
     * Отисовка надписи "рюкзак" и разделяющей линии на экране
     */
    public void drawLabelBackpack() {
        String label = "Рюкзак ('f' - выбросить)";
        drawSymbol(PresentConstants.MAP_WIDTH + (PresentConstants.TERMINAL_WIDTH - PresentConstants.MAP_WIDTH) / 5, 1, label, TextColor.ANSI.YELLOW);
        for (int i = 0; i < PresentConstants.TERMINAL_HEIGHT; i++)
            drawSymbol(PresentConstants.MAP_WIDTH + 1, i, "|", TextColor.ANSI.YELLOW);
    }

    /**
     * Обработка ввода имени главного героя
     *
     * @return Строка с именем главного героя
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public String inputUserName() throws IOException {
        screen.clear();
        drawInputHeroNameString();
        StringBuilder stringBuilder = new StringBuilder();
        boolean enterPress = false;
        while (!enterPress) {
            KeyStroke keyStroke = screen.readInput();
            if (keyStroke != null) {
                switch (keyStroke.getKeyType()) {
                    case Enter -> enterPress = true;
                    case Character -> handleCharacterInputInHeroName(keyStroke, stringBuilder);
                    case Backspace -> handleBackspaceInputInHeroName(stringBuilder);
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Вывод строки "Имя героя:" на экран
     *
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    private void drawInputHeroNameString() throws IOException {
        String inputLine = "Имя героя:";
        drawSymbol(PresentConstants.TERMINAL_WIDTH / 2 - 4, PresentConstants.TERMINAL_HEIGHT / 2 - 4, inputLine, TextColor.ANSI.MAGENTA);
        refresh();
    }

    /**
     * Метод для вывода введенного символа имени героя на экран
     *
     * @param keyStroke     Нажатая пользователем клавиша
     * @param stringBuilder Объект StringBuilder, используемый для накопления символов имени героя
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    private void handleCharacterInputInHeroName(KeyStroke keyStroke, StringBuilder stringBuilder) throws IOException {
        if (stringBuilder.length() < PresentConstants.HERO_NAME_LENGTH) {
            char keyChar = keyStroke.getCharacter();
            stringBuilder.append(keyChar);
            drawSymbol(PresentConstants.TERMINAL_WIDTH / 2 - 4 + (stringBuilder.length() - 1), PresentConstants.TERMINAL_HEIGHT / 2 - 2, Character.toString(keyChar), TextColor.ANSI.WHITE);
            refresh();
        }
    }

    /**
     * Метод для стирания последнего символа имени героя
     *
     * @param stringBuilder Объект StringBuilder, используемый для накопления символов имени героя
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    private void handleBackspaceInputInHeroName(StringBuilder stringBuilder) throws IOException {
        if (!stringBuilder.isEmpty()) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            drawSymbol(PresentConstants.TERMINAL_WIDTH / 2 - 4 + stringBuilder.length(), PresentConstants.TERMINAL_HEIGHT / 2 - 2, Character.toString(' '), TextColor.ANSI.WHITE);
            refresh();
        }
    }

    public TextGraphics getGraphics() {
        return graphics;
    }

    public void setGraphics(TextGraphics graphics) {
        this.graphics = graphics;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    /**
     * Отрисовка победного экрана
     *
     * @param heroName Имя героя
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public void showWinScreen(String heroName) throws IOException {
        clear();
        String winText = "Ты победил " + heroName + "!!!";
        int startX = PresentConstants.TERMINAL_WIDTH / 2 - winText.length() / 2 + 1;
        int startY = PresentConstants.TERMINAL_HEIGHT / 2 - 7;
        int offsetY = 3;
        int offsetX = -4;
        drawSymbol(startX, startY, winText, TextColor.ANSI.GREEN);

        String rectSymbol = "█";
        String[] duckMap = {
                "000000000032221",
                "0000000002111111",
                "0000000021111111",
                "00000000010601106",
                "000000001060000600",
                "000000001100044",
                "00000000111125553",
                "0000011131111223",
                "0000111113221222",
                "00011111111122223",
                "32211111211122222",
                "22100113111222222",
                "01111001112222222",
                "0011111122222223",
                "00000112222233"
        };

        for (int row = 0; row < duckMap.length; row++) {
            String line = duckMap[row];
            for (int column = 0; column < line.length(); column++) {
                char symbol = line.charAt(column);
                if (symbol == 0) continue;
                SymbolColor color = getColorFromSymbol(symbol);
                graphics.setForegroundColor(TextColor.Factory.fromString(color.getColorCode()));
                graphics.putString(startX + offsetX + column, startY + offsetY + row, rectSymbol);
            }
        }
        refresh();
        pressAnyButton();
    }

    /**
     * Поиск цвета по номеру из массива duckMap
     *
     * @param symbol Номер цвета в массиве duckMap
     * @return Соответсвующий цвет для отрисовки прямоугольника на экране
     */
    private SymbolColor getColorFromSymbol(char symbol) {
        return switch (symbol) {
            case '1' -> SymbolColor.YELLOW;
            case '2' -> SymbolColor.YELLOW_DARK;
            case '3' -> SymbolColor.YELLOW_HARD_DARK;
            case '4' -> SymbolColor.ORANGE;
            case '5' -> SymbolColor.ORANGE_DARK;
            case '6' -> SymbolColor.WHITE;
            default -> SymbolColor.BLACK;
        };
    }
}
