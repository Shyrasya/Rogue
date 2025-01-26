package domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Weapon.class, name = "weapon"),
        @JsonSubTypes.Type(value = Food.class, name = "food"),
        @JsonSubTypes.Type(value = Potion.class, name = "potion"),
        @JsonSubTypes.Type(value = Scroll.class, name = "scroll"),
        @JsonSubTypes.Type(value = Gold.class, name = "gold"),
})
public abstract class Item {

    /**
     * Тип предмета
     */
    private String type;

    /**
     * Название предмета
     */
    private String name;

    /**
     * Позиция предмета на карте
     */
    private Position pos;

    /**
     * Конструктор класса Item
     *
     * @param type Тип создаваемого предмета
     * @param name Имя создаваемого предмета
     */
    public Item(String type, String name) {
        this.type = type;
        this.name = name;
        this.pos = new Position();
    }

    public Item() {
    }

    /**
     * Информационная строка, уведомляющая о нахождении предмета
     *
     * @return Строка с именем предмета, который нашел герой
     */
    public String findInfo() {
        return "Герой нашел " + this.name;
    }

    /**
     * Информация, которая пишется в информационной строке при выбросе предмета
     *
     * @return Строка с информацией о выброшенном предмете
     */
    public String dropInfo() {
        return "Герой выбросил из рюкзака" + this.name;
    }

    /**
     * Метод, проверяющий равенство двух объектов (по имени и классу)
     *
     * @param obj Объект класса Object
     * @return true - объекты равны, false - нет
     */
    @Override
    public boolean equals(Object obj) {
        boolean equal;
        if (this == obj) equal = true;
        else if (obj == null || getClass() != obj.getClass()) equal = false;
        else {
            Item itemObj = (Item) obj;
            equal = Objects.equals(name, itemObj.name);
        }
        return equal;
    }

    /**
     * Вычисление хеш-кода на основе поля name объекта
     *
     * @return Целое число хеш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * Абстрактный метод для взятия информации о предмете для помещения её в область рюкзака
     *
     * @return Строка с именем предмета, его характеристиками
     */
    public abstract String getBackpackInfo();

    /**
     * Абстрактный метод взятия символа предмета для дальнейшей отрисовки на карте/экране
     *
     * @return Символ предмета, который отображается на экране
     */
    public abstract char getSymbol();

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
