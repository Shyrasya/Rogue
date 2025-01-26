package datalayer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DataBase {
    private ArrayList<ArrayList<String>> statistics = new ArrayList<>();

    public ArrayList<ArrayList<String>> getStatistics() {
        return statistics;
    }

    public void setStatistics(ArrayList<ArrayList<String>> statistics) {
        this.statistics = statistics;
    }

    /**
     * Метод добавляет единичную статистику в список статистик
     *
     * @param statistic Текущая запись статистики
     */
    public void addStatistic(ArrayList<String> statistic) {
        statistics.add(statistic);
    }

    /**
     * Метод сериализует объект saveStatistics и записывает его в файл
     *
     * @param saveStatistics Объект статистики, который собираемся сериализовать
     * @param fileName       Имя файла, куда сохраним сериализованную статистику
     */
    public static void saveStatistics(DataBase saveStatistics, String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(fileName), saveStatistics);
    }

    /**
     * Метод десериализует файл и возвращает объект статистики.
     * Если файла не существует, возвращает пустой объект
     *
     * @param fileName Имя десериализуемого файла
     * @return Обект статистики
     */
    public static DataBase loadStatistics(String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File(fileName), DataBase.class);
        } catch (IOException e) {
            return new DataBase();
        }
    }
}