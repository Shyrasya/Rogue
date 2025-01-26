package domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Corridor {

    /**
     * Тип коридора
     */
    private int corType;

    /**
     * Массив с координатами узлов коридора
     */
    private Position[] nodes;

    /**
     * Количество узлов коридора
     */
    private int nodesCount;

    /**
     * Исследован ли коридора, был ли там ранее герой
     */
    private boolean explored;

    /**
     * Базовый конструктор класса Corridor
     */
    public Corridor() {
        this.corType = GameConstants.INIT;
        explored = false;
    }

    /**
     * Параметризованный конструктор класса Corridor
     *
     * @param corType    Тип коридора
     * @param nodes      Массив с координатами узлов коридора
     * @param nodesCount Количество узлов в коридоре
     * @param explored   Исследован ли ранее коридор
     */
    @JsonCreator
    public Corridor(@JsonProperty("corType") int corType,
                    @JsonProperty("nodes") Position[] nodes,
                    @JsonProperty("nodesCount") int nodesCount,
                    @JsonProperty("explored") boolean explored) {
        this.corType = corType;
        this.nodes = nodes;
        this.nodesCount = nodesCount;
        this.explored = explored;
    }

    /**
     * Проверка, находится ли выбранная точка в коридоре типа "лево-право"
     *
     * @param pointY Координата точки по Y
     * @param pointX Координата точки по X
     * @return true - да, точка в этом коридоре, false - нет.
     */
    public boolean checkLeftToRightNodesExtraPoints(int pointY, int pointX) {
        int node0Y = nodes[0].getYPos();
        int node0X = nodes[0].getXPos();
        int node3Y = nodes[3].getYPos();
        int node3X = nodes[3].getXPos();
        return (((pointY >= node0Y && pointY <= node3Y) || (pointY <= node0Y && pointY >= node3Y)) && node0X <= pointX && pointX <= node3X);
    }

    /**
     * Проверка, находится ли выбранная точка в коридоре типа "левый поворот"
     *
     * @param pointY Координата точки по Y
     * @param pointX Координата точки по X
     * @return true - да, точка в этом коридоре, false - нет.
     */
    public boolean checkLeftTurnNodesExtraPoints(int pointY, int pointX) {
        int node0Y = nodes[0].getYPos();
        int node0X = nodes[0].getXPos();
        int node2Y = nodes[2].getYPos();
        int node2X = nodes[2].getXPos();
        return (pointX >= node2X && pointX <= node0X && pointY >= node0Y && pointY <= node2Y);
    }

    /**
     * Проверка, находится ли выбранная точка в коридоре типа "правый поворот"
     *
     * @param pointY Координата точки по Y
     * @param pointX Координата точки по X
     * @return true - да, точка в этом коридоре, false - нет.
     */
    public boolean checkRightTurnNodesExtraPoints(int pointY, int pointX) {
        int node0Y = nodes[0].getYPos();
        int node0X = nodes[0].getXPos();
        int node2Y = nodes[2].getYPos();
        int node2X = nodes[2].getXPos();
        return (pointX <= node2X && pointX >= node0X && pointY >= node0Y && pointY <= node2Y);
    }

    /**
     * Проверка, находится ли выбранная точка в коридоре типа "сверх-вниз"
     *
     * @param pointY Координата точки по Y
     * @param pointX Координата точки по X
     * @return true - да, точка в этом коридоре, false - нет.
     */
    public boolean checkUpToDownNodesExtraPoints(int pointY, int pointX) {
        int node0Y = nodes[0].getYPos();
        int node0X = nodes[0].getXPos();
        int node3Y = nodes[3].getYPos();
        int node3X = nodes[3].getXPos();
        return (((pointX >= node0X && pointX <= node3X) || (pointX <= node0X && pointX >= node3X)) && node0Y <= pointY && pointY <= node3Y);
    }

    public int getCorType() {
        return corType;
    }

    public void setCorType(int corType) {
        this.corType = corType;
    }

    public int getNodesCount() {
        return nodesCount;
    }

    public void setNodesCount(int nodesCount) {
        this.nodesCount = nodesCount;
        nodes = new Position[nodesCount];
        for (int i = 0; i < nodesCount; i++)
            nodes[i] = new Position();
    }

    public Position getNode(int number) {
        return nodes[number];
    }

    public void setNode(int number, Position node) {
        this.nodes[number] = node;
    }

    public Position[] getNodes() {
        return nodes;
    }

    public void setNodes(Position[] nodes) {
        this.nodes = nodes;
    }

    public boolean isExplored() {
        return explored;
    }

    public void setExplored(boolean explored) {
        this.explored = explored;
    }
}
