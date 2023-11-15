package trainer.core.listeners.textfields;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import trainer.core.DSJ2FXInterfaceHelper;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class DecreaseWindFieldListener implements ChangeListener<String> {

    private static final Logger logger = Logger.getLogger(DecreaseWindFieldListener.class.getName());
    private final TextField decreaseWindField;
    private final Slider decreaseWindSlider;

    public DecreaseWindFieldListener(TextField decreaseWindField, Slider decreaseWindSlider) {
        this.decreaseWindField = decreaseWindField;
        this.decreaseWindSlider = decreaseWindSlider;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
        logger.log(Level.INFO, "changed: " + observable + "; newValue: " + newValue);
        if (Pattern.compile("\\d([,.]\\d?)?").matcher(newValue).matches()) {
            float floatValue = Float.parseFloat(Pattern.compile(",").matcher(newValue).replaceAll("."));
            if (DSJ2FXInterfaceHelper.isAllowedDecreaseWindValue(floatValue)) {
                decreaseWindSlider.setValue(floatValue);
                decreaseWindField.setText(newValue);
            } else {
                decreaseWindSlider.setValue(DSJ2FXInterfaceHelper.MAX_DECREASE_WIND_VALUE);
                decreaseWindField.setText(Float.toString(DSJ2FXInterfaceHelper.MAX_DECREASE_WIND_VALUE));
            }
        } else if (newValue.length() > 3) {
            decreaseWindField.setText(newValue.substring(0, 3));
        } else if (Pattern.compile("\\d{2,3}").matcher(newValue).matches()) {
            decreaseWindField.setText(newValue.substring(0, 1));
        }
    }
}
