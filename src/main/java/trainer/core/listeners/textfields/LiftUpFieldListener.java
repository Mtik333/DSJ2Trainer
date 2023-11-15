package trainer.core.listeners.textfields;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import trainer.core.DSJ2FXInterfaceHelper;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class LiftUpFieldListener implements ChangeListener<String> {

    private static final Logger logger = Logger.getLogger(LiftUpFieldListener.class.getName());
    private final TextField liftUpField;
    private final Slider liftUpSlider;

    public LiftUpFieldListener(TextField liftUpField, Slider liftUpSlider) {
        this.liftUpField = liftUpField;
        this.liftUpSlider = liftUpSlider;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
        logger.log(Level.INFO, "changed: " + observable + "; newValue: " + newValue);
        if (Pattern.compile("\\d([,.]\\d?)?").matcher(newValue).matches()) {
//        if (newValue.matches("\\d([,.]\\d?)?")) {
//            float floatValue = Float.parseFloat(newValue.replaceAll(",","."));
            float floatValue = Float.parseFloat(Pattern.compile(",").matcher(newValue).replaceAll("."));
            if (DSJ2FXInterfaceHelper.isAllowedLiftUpValue(floatValue)) {
                liftUpSlider.setValue(floatValue);
                liftUpField.setText(newValue);
            } else {
                liftUpSlider.setValue(DSJ2FXInterfaceHelper.MAX_LIFT_UP_VALUE);
                liftUpField.setText(Float.toString(DSJ2FXInterfaceHelper.MAX_LIFT_UP_VALUE));
            }
        } else if (newValue.length() > 3) {
            liftUpField.setText(newValue.substring(0, 3));
        } else if (Pattern.compile("\\d{2,3}").matcher(newValue).matches()) {
//        } else if (newValue.matches("\\d{2,3}")){
            liftUpField.setText(newValue.substring(0, 1));
        }
    }
}
