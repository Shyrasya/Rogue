package domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Position {

    /**
     * Координата по X
     */
    private int xPos;

    /**
     * Координата по Y
     */
    private int yPos;

    /**
     * Базовый конструктор класса Position
     */
    public Position() {
        this.xPos = GameConstants.INIT;
        this.yPos = GameConstants.INIT;
    }

    /**
     * Параметризированный конструктор
     *
     * @param xPos Передаваемая координата по X
     * @param yPos Передаваемая координата по Y
     */
    @JsonCreator
    public Position(@JsonProperty("xpos") int xPos, @JsonProperty("ypos") int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    public void setPosition(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public int[] getPosition() {
        return new int[]{xPos, yPos};
    }
}
