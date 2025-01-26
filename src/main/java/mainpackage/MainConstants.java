package mainpackage;

public class MainConstants {
    public enum Status {
        UNDEFINED(-1),
        RUNNING(0),
        RECORD(1),
        EXIT(2),
        MENU(3);

        Status(int value) {
        }
    }

    public static final int MAX_LVL = 22;
}
