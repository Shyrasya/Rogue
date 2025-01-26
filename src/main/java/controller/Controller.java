package controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import domain.*;
import mainpackage.MainConstants;
import presentation.Presentation;

import java.io.IOException;
import java.util.*;

@JsonIgnoreProperties({"presentation"})
public class Controller {

    /**
     * Объект класса Presentation, отвечающий за отрисовку игры на экране
     */
    private Presentation presentation;

    /**
     * Объект класса GameSession, отвечающий за логическую часть игры
     */
    GameSession session;

    /**
     * Базовый конструктор класса Controller
     */
    public Controller() {
    }

    /**
     * Параметризированный конструктор класса Controller
     *
     * @param presentation Объект класса Presentation - визуальная часть игры
     * @param session      Объект класса GameSession - логическая часть игры
     */
    public Controller(Presentation presentation, GameSession session) {
        this.presentation = presentation;
        this.session = session;
    }

    /**
     * Вызов метода refresh() класса Presentation
     *
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public void refresh() throws IOException {
        presentation.refresh();
    }

    /**
     * Вызов метода clear() класса Presentation
     *
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public void clear() throws IOException {
        presentation.clear();
    }

    /**
     * Вызов метода pressAnyButton() класса Presentation
     *
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public void pressAnyButton() throws IOException {
        presentation.pressAnyButton();
    }

    /**
     * Вызов метода clear() класса Presentation
     *
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public void close() throws IOException {
        presentation.close();
    }

    /**
     * Метод для начальной отрисовки игрового уровня на экране
     */
    public void updateStartMap() {
        presentation.drawGameMap(session.getFogMap());
        presentation.drawLabelBackpack();
    }

    /**
     * Печать информации слева внизу экрана о характеристиках героя
     */
    public void printInfo() {
        Hero hero = Hero.getInstance();
        presentation.strInfo(hero.getHealth(), hero.getMaxHealth(), hero.getStrength(), hero.getAgility(), session.getLvlNumber(), hero.getStatistic().get("gold"));
    }

    /**
     * Обработка нажатия клавиш с клавиатуры
     *
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public boolean input() throws IOException {
        boolean usingItem = true;
        boolean running = true;
        while (running && usingItem) {
            String key = presentation.inputKey();
            if (key.equals("exit")) running = false;
            else if (handleHeroMove(key)) usingItem = false;
            else if (key.equals("drop")) handleDropButton();
            else if (Set.of("Food", "Potion", "Scroll", "Weapon").contains(key))
                handleItemButton(key);
        }
        if (running) drawPerson(Hero.getInstance());
        refresh();
        return running;
    }

    /**
     * Обработка клавиш, отвечающих за движение главного героя
     *
     * @param key Нажатая пользователем клавиша передвижения
     * @return true - было нажатие клавиш передвижения, герой походил, false - нет
     */
    public boolean handleHeroMove(String key) {
        return switch (key) {
            case "up", "down", "left", "right" -> {
                session.inputMoveHero(key);
                List<String> endPotionInfo = Hero.getInstance().checkPotionsUsingTime();
                if (!endPotionInfo.isEmpty()) {
                    for (String s : endPotionInfo)
                        presentation.strMessage(s);
                    printInfo();
                }
                yield true;
            }
            default -> false;
        };
    }

    /**
     * Обработка нажатия клавиш для выброса вещей из рюкзака
     *
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public void handleDropButton() throws IOException {
        if (!session.getBackpack().isEmpty()) {
            String whatTypeItemMsg = "Какой тип предмета вы хотите выбросить? Выберите соответствующую клавишу!";
            if (session.isTakenNeighborMapPoint()) whatTypeItemMsg = "Место для выброса на поле занято!";
            presentation.strMessage(whatTypeItemMsg);
            if (!Set.of("Предмета этого типа нет в рюкзаке :(", "Место для выброса на поле занято!").contains(whatTypeItemMsg)) {
                refresh();
                String dropTypeKey = presentation.inputKey();
                if (Set.of("Food", "Potion", "Scroll", "Weapon").contains(dropTypeKey))
                    dropItemByType(dropTypeKey);
                else presentation.strMessage("Предмета этого типа нет в рюкзаке :(");
            }
        } else presentation.strMessage("Рюкзак пуст!");
        refresh();
    }

    /**
     * Выброс предмета исходя из выбранного типа и проверка на корректный ввод номера, под которым находится предмет в рюкзаке
     *
     * @param dropTypeKey Тип выбрасываемого предмета
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    private void dropItemByType(String dropTypeKey) throws IOException {
        String dropItemInfo = session.getBackpack().checkIsEmptyInBackpack("drop", dropTypeKey);
        presentation.strMessage(dropItemInfo);
        refresh();
        String inputNumberInfo = session.dropItem(dropTypeKey, presentation.inputKey());
        presentation.strMessage(inputNumberInfo);
        if (!inputNumberInfo.equals("Неправильный ввод!")) {
            drawHeroRightSymbolBackpackInfo();
            if (dropTypeKey.equals("Weapon")) printInfo();
        }
    }

    /**
     * Обработка клавиш, отвечающих за взаимодействие персонажа с предметами
     *
     * @param key Нажатая пользователем клавиша для работы с предметом
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public void handleItemButton(String key) throws IOException {
        String useItemInfo = session.getBackpack().checkIsEmptyInBackpack("use", key);
        if (key.equals("Weapon") && session.isTakenNeighborMapPoint())
            useItemInfo = "Место для выброса на поле занято!";
        presentation.strMessage(useItemInfo);
        refresh();
        if (!useItemInfo.equals("Предмета этого типа нет в рюкзаке :(") && !useItemInfo.equals("Место для выброса на поле занято!")) {
            List<String> inputNumberInfo = session.useItem(key, presentation.inputKey());
            for (String s : inputNumberInfo)
                presentation.strMessage(s);
            if (inputNumberInfo.get(0).equals("Неправильный ввод!")) refresh();
            else drawHeroRightSymbolBackpackInfo();
        }
    }

    /**
     * Обновление символа справа от героя клетки - печать содержимого рюкзака и информации об характеристиках
     *
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    private void drawHeroRightSymbolBackpackInfo() throws IOException {
        Hero hero = Hero.getInstance();
        presentation.drawMapSymbol(hero.getCoordX() + 1, hero.getCoordY(), Character.toString(session.getMap()[hero.getCoordY()][hero.getCoordX() + 1]));
        printBackpackContent();
        printInfo();
        refresh();
    }

    /**
     * Метод для обработки хода персонажа и последующей отрисовки карты
     *
     * @param person Персонаж (главный герой или враг)
     */
    public void drawPerson(Person person) {
        char curSymbol = session.getMap()[person.getCoordY()][person.getCoordX()];
        Position curPos = new Position(person.getCoordX(), person.getCoordY());
        boolean isChange = session.drawPerson(person, curSymbol);
        if (isChange) {
            presentation.drawGameMap(session.getFogMap());
            if (person instanceof Hero && ((Hero) person).getPickUpItem() != null) {
                presentation.strMessage(((Hero) person).getPickUpItem().findInfo());
                if (((Hero) person).getPickUpItem() instanceof Gold)
                    printInfo();
                else printBackpackContent();
                ((Hero) person).setPickUpItem(null);
            } else if (person instanceof Hero && ((Hero) person).getPickUpItem() == null && session.getBackpack().isFullBackpackType()) {
                presentation.strMessage("Нет места в рюкзаке!");
                session.getBackpack().setFullBackpackType(false);
            }
        }
        String fightInfo = session.fight(person, curSymbol, curPos);
        if (!fightInfo.isEmpty()) presentation.strMessage(fightInfo);
    }

    /**
     * Метод для обработки смерти персонажа - отрисовка карты, изменение статистики и вывод информации
     *
     * @param person Умерший персонаж (главный герой или враг)
     */
    public void personDead(Person person) {
        presentation.drawMapSymbol(person.getCoordX(), person.getCoordY(), person.getCurSymbol());
        session.setSymbOnMap(person.getCoordY(), person.getCoordX(), person.getCurSymbol().charAt(0));
        String death;
        if (person instanceof Enemy enemy) {
            Hero.getInstance().addStatistic("gold", enemy.gold4Dead());
            death = String.format("Герой побеждает %s и получает %d золота", Enemy.nameOfEnemy.get(person.getThisIs()), enemy.gold4Dead());
        } else {
            death = "Игра окончена, " + Hero.getInstance().getName() + "!!!   o(>_<)o   Нажми любую клавишу для выхода!";
            person.setHealth(0);
        }
        presentation.drawGameMap(session.getFogMap());
        presentation.strMessage(death);
    }

    /**
     * Вызов метода showStartMenu() класса Presentation
     *
     * @return Статус игры (0 - запущена, 1 - показ рекордов, 2 - выход)
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public MainConstants.Status showStartMenu() throws IOException {
        return presentation.showStartMenu();
    }

    /**
     * Вызов метода inputUserName() класса Presentation для ввода и записи имени главного героя
     *
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public void inputUserName() throws IOException {
        Hero.getInstance().setName(presentation.inputUserName());
    }

    /**
     * Печать приветственной информации на экране
     */
    public void printHelloHero() {
        presentation.strMessage("Добро пожаловать " + Hero.getInstance().getName() + "! Пройди 21 уровень, чтобы выбраться из подземелья!");
    }

    /**
     * Вызов метода showStatistics() класса Presentation
     *
     * @param fromMenu Вызов поля с рекордами из главного меню (true - да, false - нет)
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public void showStatistics(boolean fromMenu) throws IOException {
        presentation.showStatistics(session.createStatistics(fromMenu));
    }

    /**
     * Вызов метода showWinScreen() класса Presentation
     *
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public void showWinScreen() throws IOException {
        presentation.showWinScreen(Hero.getInstance().getName());
    }

    /**
     * Вызов метода printBackpackContent() класса Presentation
     */
    public void printBackpackContent() {
        presentation.printBackpackContent(session.getBackpack().getBackpackContent());
    }
}


