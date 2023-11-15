package trainer.core.listeners.textfields;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import trainer.core.DSJ2FXInterfaceHelper;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class SpeedUpFieldListener implements ChangeListener<String> {

    private static final Logger logger = Logger.getLogger(SpeedUpFieldListener.class.getName());
    private final TextField speedUpField;
    private final Slider speedUpSlider;

    public SpeedUpFieldListener(TextField speedUpField, Slider speedUpSlider) {
        this.speedUpField = speedUpField;
        this.speedUpSlider = speedUpSlider;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
        logger.log(Level.INFO, "changed: " + observable + "; newValue: " + newValue);
        if (Pattern.compile("\\d([,.]\\d?)?").matcher(newValue).matches()) {
            float floatValue = Float.parseFloat(Pattern.compile(",").matcher(newValue).replaceAll("."));
            if (DSJ2FXInterfaceHelper.isAllowedSpeedUpValue(floatValue)) {
                speedUpSlider.setValue(floatValue);
                speedUpField.setText(newValue);
            } else {
                speedUpSlider.setValue(DSJ2FXInterfaceHelper.MAX_SPEED_UP_VALUE);
                speedUpField.setText(Float.toString(DSJ2FXInterfaceHelper.MAX_SPEED_UP_VALUE));
            }
        } else if (newValue.length() > 3) {
            speedUpField.setText(newValue.substring(0, 3));
        } else if (Pattern.compile("\\d{2,3}").matcher(newValue).matches()) {
            speedUpField.setText(newValue.substring(0, 1));
        }
    }
}
