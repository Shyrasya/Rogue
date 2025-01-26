package domain;

public interface CheckItemAbility {

    /**
     * Метод для выявления наличия у предмета параметра максимального здоровья и составления информационной строки о нем
     *
     * @param maxHealth Количество единиц максимального здоровья у предмета
     * @return Строка для информационного поля с параметром максимального здоровья предмета
     */
    default String getItemHaveMaxHealth(int maxHealth) {
        String info = "";
        if (maxHealth > 0) info = ". Максимальное здоровье +" + maxHealth;
        return info;
    }

    /**
     * Метод для выявления наличия у предмета параметра ловкости и составления информационной строки о нем
     *
     * @param agility Количество единиц ловкости у предмета
     * @return Строка для информационного поля с параметром ловкости предмета
     */
    default String getItemHaveAgility(int agility) {
        String info = "";
        if (agility > 0) info = ". Ловкость +" + agility;
        return info;
    }

    /**
     * Метод для выявления наличия у предмета параметра силы и составления информационной строки о нем
     *
     * @param strength Количество единиц силы у предмета
     * @return Строка для информационного поля с параметром силы предмета
     */
    default String getItemHaveStrength(int strength) {
        String info = "";
        if (strength > 0) info = ". Сила +" + strength;
        return info;
    }

    /**
     * Метод для выявления наличия у предмета параметра максимального здоровья и составления строки для поля с рюкзаком о нем
     *
     * @param maxHealth Количество единиц максимального здоровья у предмета
     * @return Строка для поля с содержимым рюкзака с параметром максимального здоровья предмета
     */
    default String getItemBackpackHaveMaxHealth(int maxHealth) {
        String info = "";
        if (maxHealth > 0) info = "мз +" + maxHealth;
        return info;
    }

    /**
     * Метод для выявления наличия у предмета параметра ловкости и составления строки для поля с рюкзаком о нем
     *
     * @param agility Количество единиц ловкости у предмета
     * @return Строка для поля с содержимым рюкзака с параметром ловкости предмета
     */
    default String getItemBackpackHaveAgility(int agility) {
        String info = "";
        if (agility > 0) info = "л +" + agility;
        return info;
    }

    /**
     * Метод для выявления наличия у предмета параметра силы и составления строки для поля с рюкзаком о нем
     *
     * @param strength Количество единиц силы у предмета
     * @return Строка для поля с содержимым рюкзака с параметром силы предмета
     */
    default String getItemBackpackHaveStrength(int strength) {
        String info = "";
        if (strength > 0) info = "с +" + strength;
        return info;
    }
}
