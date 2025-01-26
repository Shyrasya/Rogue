package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Gold extends Item {

    /**
     * Стоимость сокровищ
     */
    private int coast;

    /**
     * Позиция золота на карте
     */
    private Position pos;

    /**
     * Контруктор класса  Gold
     *
     * @param name  Название предмета - "золото"
     * @param coast Стоимость золота
     */
    public Gold(String name, int coast) {
        super("Gold", name);
        this.coast = coast;
    }

    public Gold() {
        super();
    }

    /**
     * Информационная строка, уведомляющая о нахождении золота
     *
     * @return Строка о найденом золоте и его количестве
     */
    public String findInfo() {
        return "Герой нашел " + this.coast + " " + endGoldString();
    }

    /**
     * Метод, определяющий окончание информационной строки о нахождении золота
     *
     * @return Строка "золото" с окончанием с верным оконочанием для выбранного количества
     */
    private String endGoldString() {
        String goldEnd = "золота";
        if ((coast % 10 == 1) && coast != 11) goldEnd = "золото";
        return goldEnd;
    }

    /**
     * Метод для взятия символа золота для дальнейшей отрисовки на карте/экране
     *
     * @return Символ золота, который отображается на экране
     */
    @Override
    public char getSymbol() {
        return '$';
    }

    /**
     * Пустой метод-заглушка для золота (так как золото не кладется в рюкзак)
     *
     * @return null
     */
    @Override
    public String getBackpackInfo() {
        return null;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public int getCoast() {
        return coast;
    }
}
