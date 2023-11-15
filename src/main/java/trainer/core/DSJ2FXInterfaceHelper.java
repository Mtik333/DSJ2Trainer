package trainer.core;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Alert;
import org.jnativehook.keyboard.NativeKeyEvent;
import trainer.DSJ2FXInterface;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DSJ2FXInterfaceHelper {

    public static final float MAX_WIND_STRENGTH_VALUE = 4.0f;
    public static final int MAX_WIND_DIRECTION_VALUE = 360;
    public static final float MAX_LIFT_UP_VALUE = 5.0f;
    public static final float MAX_SPEED_UP_VALUE = 5.0f;
    public static final float MAX_INCREASE_WIND_VALUE = 1.0f;
    public static final float MAX_DECREASE_WIND_VALUE = 1.0f;
    public static final int TIMER_EXTREME_PERIOD = 100;
    public static final int TIMER_DEFAULT_PERIOD = 500;
    private static final Logger logger = Logger.getLogger(DSJ2FXInterfaceHelper.class.getName());
    public static Level logLevel = Level.SEVERE;
    public static boolean isDSJ21PLaunched;
    public static String activateKey = "F9";
    public static String deactivateKey = "F10";
    public static String speedUpKey = "F11";
    public static String liftUpKey = "F12";
    public static String increaseWindKey = "F6";
    public static String decreaseWindKey = "F7";
    public static ChangeListener<String> windStrengthListener;

    static {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
    }

    public static boolean isAllowedWindStrengthValue(Float value) {
        return value <= MAX_WIND_STRENGTH_VALUE && value >= 0;
    }

    public static boolean isAllowedWindDirectionValue(Integer value) {
        return value <= MAX_WIND_DIRECTION_VALUE && value >= 0;
    }

    public static boolean isAllowedSpeedUpValue(Float value) {
        return value <= MAX_LIFT_UP_VALUE && value >= 0;
    }

    public static boolean isAllowedIncreaseWindValue(Float value) {
        return value <= MAX_INCREASE_WIND_VALUE && value >= 0;
    }

    public static boolean isAllowedDecreaseWindValue(Float value) {
        return value <= MAX_DECREASE_WIND_VALUE && value >= 0;
    }

    public static boolean isAllowedLiftUpValue(Float value) {
        return value <= MAX_SPEED_UP_VALUE && value >= 0;
    }

    public static boolean isAllowedKeyMapping(String keyProp) {
        return KEY_STRING_TO_KEY_CODE.containsKey(keyProp);
    }

    public static void showHelp() {
        logger.log(Level.INFO, "showHelp");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(DSJ2FXInterface.owner);
        alert.setTitle(DSJ2Controller.resourceBundle.getString("message"));
        alert.setHeaderText(DSJ2Controller.resourceBundle.getString("help"));
        alert.setContentText(DSJ2Controller.resourceBundle.getString("helpText"));
        alert.showAndWait();
    }

    public static void exitApp() {
        logger.log(Level.INFO, "exitApp");
        Platform.exit();
        System.exit(0);
    }

    public static void showInformationAlert(String message) {
        logger.log(Level.INFO, "showConfirmationAlert, message: " + message);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(DSJ2FXInterface.owner);
        alert.setTitle(DSJ2Controller.resourceBundle.getString("message"));
        alert.setHeaderText(message);
        alert.setContentText(null);
        alert.showAndWait();
    }

    public static void showErrorAlert(Exception exp) {
        logger.log(Level.INFO, "showConfirmationAlert: exp: " + exp);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(DSJ2FXInterface.owner);
        alert.setTitle(exp.getClass().getSimpleName());
        alert.setHeaderText(exp.getMessage());
        alert.setContentText(null);
        alert.showAndWait();
    }

    public static final HashMap<String, Integer> KEY_STRING_TO_KEY_CODE = new HashMap<String, Integer>() {{
        put("F1", NativeKeyEvent.VC_F1);
        put("F2", NativeKeyEvent.VC_F2);
        put("F3", NativeKeyEvent.VC_F3);
        put("F4", NativeKeyEvent.VC_F4);
        put("F5", NativeKeyEvent.VC_F5);
        put("F6", NativeKeyEvent.VC_F6);
        put("F7", NativeKeyEvent.VC_F7);
        put("F8", NativeKeyEvent.VC_F8);
        put("F9", NativeKeyEvent.VC_F9);
        put("F10", NativeKeyEvent.VC_F10);
        put("F11", NativeKeyEvent.VC_F11);
        put("F12", NativeKeyEvent.VC_F12);
        put("A", NativeKeyEvent.VC_A);
        put("B", NativeKeyEvent.VC_B);
        put("C", NativeKeyEvent.VC_C);
        put("D", NativeKeyEvent.VC_D);
        put("E", NativeKeyEvent.VC_E);
        put("F", NativeKeyEvent.VC_F);
        put("G", NativeKeyEvent.VC_G);
        put("H", NativeKeyEvent.VC_H);
        put("I", NativeKeyEvent.VC_I);
        put("J", NativeKeyEvent.VC_J);
        put("K", NativeKeyEvent.VC_K);
        put("L", NativeKeyEvent.VC_L);
        put("M", NativeKeyEvent.VC_M);
        put("N", NativeKeyEvent.VC_N);
        put("O", NativeKeyEvent.VC_O);
        put("P", NativeKeyEvent.VC_P);
        put("Q", NativeKeyEvent.VC_Q);
        put("R", NativeKeyEvent.VC_R);
        put("S", NativeKeyEvent.VC_S);
        put("T", NativeKeyEvent.VC_T);
        put("U", NativeKeyEvent.VC_U);
        put("V", NativeKeyEvent.VC_V);
        put("W", NativeKeyEvent.VC_W);
        put("X", NativeKeyEvent.VC_X);
        put("Y", NativeKeyEvent.VC_Y);
        put("Z", NativeKeyEvent.VC_Z);
        put("`", NativeKeyEvent.VC_BACKQUOTE);
        put("1", NativeKeyEvent.VC_1);
        put("2", NativeKeyEvent.VC_2);
        put("3", NativeKeyEvent.VC_3);
        put("4", NativeKeyEvent.VC_4);
        put("5", NativeKeyEvent.VC_5);
        put("6", NativeKeyEvent.VC_6);
        put("7", NativeKeyEvent.VC_7);
        put("8", NativeKeyEvent.VC_8);
        put("9", NativeKeyEvent.VC_9);
        put("0", NativeKeyEvent.VC_0);
        put("-", NativeKeyEvent.VC_MINUS);
        put("=", NativeKeyEvent.VC_EQUALS);
        put("[", NativeKeyEvent.VC_OPEN_BRACKET);
        put("]", NativeKeyEvent.VC_CLOSE_BRACKET);
        put("\\", NativeKeyEvent.VC_BACK_SLASH);
        put(";", NativeKeyEvent.VC_SEMICOLON);
        put("'", NativeKeyEvent.VC_QUOTE);
        put(",", NativeKeyEvent.VC_COMMA);
        put(".", NativeKeyEvent.VC_PERIOD);
        put("/", NativeKeyEvent.VC_SLASH);
    }};
}
