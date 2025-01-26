package mainpackage;

import datalayer.*;
import controller.*;
import domain.*;
import presentation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            Presentation presentation = new Presentation();
            GameSession session = new GameSession();
            Controller controller = new Controller(presentation, session);
            Backpack backpack;
            Object[] gameData;
            int countLvl;

            MainConstants.Status status = MainConstants.Status.UNDEFINED;
            while (status != MainConstants.Status.EXIT) {
                status = controller.showStartMenu();
                if (status == MainConstants.Status.RUNNING) {
                    gameData = isLoad(presentation);
                    session = (GameSession) gameData[0];
                    controller = (Controller) gameData[1];
                    backpack = (Backpack) gameData[2];
                    countLvl = (int) gameData[3];
                    status = gameCycle(presentation, controller, session, backpack, countLvl);
                } else if (status == MainConstants.Status.RECORD) {
                    controller.showStatistics(true);
                } else if (status == MainConstants.Status.EXIT) {
                    controller.close();
                }
                controller.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object[] isLoad(Presentation presentation) throws IOException {
        File saveFile = new File("save.json");
        GameSession session;
        Controller controller;
        Backpack backpack;
        int countLvl;

        if (saveFile.exists()) {
            SaveGame saveGame = SaveGame.load("save.json");
            Hero hero = saveGame.getHero();
            Hero.setHero(hero);
            backpack = saveGame.getBackpack();
            countLvl = saveGame.getLevelNumber();
            session = new GameSession(new Level(), countLvl, backpack);
            controller = new Controller(presentation, session);
            loadGame(saveGame, session);
            if (!saveFile.delete()) {
                System.out.println("Не удалось удалить файл save.json");
            }
        } else {
            backpack = new Backpack();
            session = new GameSession(new Level(), 1, backpack);
            controller = new Controller(presentation, session);
            countLvl = 1;
        }

        return new Object[]{session, controller, backpack, countLvl};
    }

    /**
     * Основной цикл игры
     *
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public static MainConstants.Status gameCycle(Presentation presentation, Controller controller, GameSession session, Backpack backpack, int countLvl) throws IOException {
        boolean running = true;
        MainConstants.Status status = MainConstants.Status.RUNNING;
        Hero hero = Hero.getInstance();
        if (hero == null || hero.getName() == null) controller.inputUserName();
        controller.updateStartMap();
        controller.printInfo();
        controller.printHelloHero();
        controller.printBackpackContent();
        controller.refresh();
        hero.setTurn(hero.getAgility());

        while (running && hero.getHealth() > 0) {
            if (session.getNextLvl()) {
                Object[] object = (levelUp(presentation, backpack, countLvl));
                if ((boolean) object[4]) break;
                else {
                    session = (GameSession) object[0];
                    controller = (Controller) object[1];
                    backpack = (Backpack) object[2];
                    countLvl = (int) object[3];
                }
            }

            running = heroTurns(running, hero, session, controller);
            sleepTimer(100);
            if (running) enemiesTurns(session, controller, hero);

            if (hero.getHealth() <= 0) controller.personDead(hero);
            controller.printInfo();
            controller.refresh();
        }

        status = MainConstants.Status.MENU;
        endOfGame(running, session, countLvl, controller, hero);

        return status;
    }

    /**
     * Функция для обработки хода героя
     */
    public static boolean heroTurns(boolean running, Hero hero, GameSession session, Controller controller) throws IOException {
        while (hero.getNextTurn() && running && !session.getNextLvl()) {
            running = controller.input();
            for (int i = 0; i < session.getEnemies().size() && running; i++) {
                Person enemy = session.getEnemies().get(i);
                if (enemy.getHealth() <= 0) {
                    controller.personDead(enemy);
                    session.getEnemies().remove(i);
                    hero.addStatistic("deadEnemies", 1);
                }
            }
        }
        if (running) hero.setTurn(hero.getAgility());
        return running;
    }

    /**
     * Функция для обработки потоков хода врагов
     */
    private static void enemiesTurns(GameSession session, Controller controller, Hero hero) {
        List<Thread> threads = new ArrayList<>();
        for (Person enemy : session.getEnemies()) {
            Thread enemyTread = getThread(controller, enemy, hero);
            threads.add(enemyTread);
            enemyTread.start();
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }

    /**
     * Обработка выхода из игрового цикла. Либо сохранение игры при выходе, либо вызов поздравительного окошка (при победе)
     * и вывод статистики
     *
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    public static void endOfGame(boolean running, GameSession session, int countLvl, Controller controller, Hero hero) throws IOException {
        if (!running) {
            saveGame(session, countLvl);
        } else {
            if (countLvl == MainConstants.MAX_LVL) controller.showWinScreen();
            controller.pressAnyButton();
            sleepTimer(1000);
            controller.showStatistics(false);
        }
        if (hero.getHealth() <= 0) hero.eraseHero();
    }

    /**
     * Метод сохранения игры. Сеттерами устанавливает в один объект сущности для сохранения,
     * а потом сериализвует этот объект
     *
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    private static void saveGame(GameSession session, int countLvl) throws IOException {
        SaveGame saveGame = new SaveGame();
        saveGame.setLevelNumber(countLvl);
        saveGame.setGameLevel(session.getLevel());
        saveGame.setHero(Hero.getInstance());
        saveGame.setMap(session.getMap());
        saveGame.setFogMap(session.getFogMap());
        saveGame.setBackpack(session.getBackpack());
        saveGame.setEnemies(session.getEnemies());
        SaveGame.save(saveGame, "save.json");
    }

    /**
     * Метод получения потока для врагов
     * return поток для врага
     */
    private static Thread getThread(Controller controller, Person enemy, Hero hero) {
        Runnable enemyTask = () -> {
            if (enemy.getHealth() > 0) {
                while (enemy.getNextTurn()) {
                    enemy.move(hero.getCoordX(), hero.getCoordY());
                    synchronized (controller) {
                        controller.drawPerson(enemy);
                    }
                }

                if (enemy.getThisIs().equals("O")) {
                    ogrFitch(hero, enemy);
                } else {
                    enemy.setTurn(enemy.getAgility());
                }
            }
        };
        return new Thread(enemyTask);
    }

    private static void ogrFitch(Person hero, Person enemy) {
        int dx = hero.getCoordX() - enemy.getCoordX();
        int dy = hero.getCoordY() - enemy.getCoordY();
        int absDX = Math.abs(dx);
        int absDY = Math.abs(dy);
        if (!(absDX <= 2 && absDY <= 2)) {
            enemy.setTurn(20);
        } else {
            if (((Enemy) enemy).getOgrAttack()) {
                ((Enemy) enemy).setOgrAttack(false);
                enemy.setTurn(0);
            } else {
                enemy.setTurn(20);
            }
        }
    }

    /**
     * Метод Загрузки игры. Сеттерами устанавливает сохраненные сущности в новые объекты
     */
    private static void loadGame(SaveGame saveGame, GameSession session) {
        session.setGameLevel(saveGame.getGameLevel());
        session.setMap(saveGame.getMap());
        session.setFogMap(saveGame.getFogMap());
        session.setEnemies(saveGame.getEnemies());
    }

    /**
     * Установка задержки выполнения программы
     *
     * @param millis Количество милисекунд, на которое будет задержка
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    private static void sleepTimer(int millis) throws IOException {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.out.println("Поток был прерван");
        }
    }

    /**
     * Повышение номера игрового уровня, его генерация и установка и начальная отрисовка
     *
     * @param presentation Класс отрисовки
     * @param backpack     Рюкзак
     * @param countLvl     Номер уровня до перехода
     * @return Массив с объектами для установки рабочих верий в gameCycle
     * @throws IOException Исключение, возникающее при ошибке ввода/вывода при работе с терминалом
     */
    private static Object[] levelUp(Presentation presentation, Backpack backpack, int countLvl) throws IOException {
        boolean isMaxLevel = false;
        countLvl++;
        if (countLvl == MainConstants.MAX_LVL) isMaxLevel = true;
        GameSession session = new GameSession(new Level(), countLvl, backpack);
        Controller controller = new Controller(presentation, session);
        controller.updateStartMap();
        controller.printInfo();
        controller.refresh();
        Hero.getInstance().addStatistic("level", 1);
        return new Object[]{session, controller, backpack, countLvl, isMaxLevel};
    }
}
