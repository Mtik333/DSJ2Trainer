package trainer.core.listeners.textfields;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import trainer.core.DSJ2FXInterfaceHelper;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class IncreaseWindFieldListener implements ChangeListener<String> {

    private static final Logger logger = Logger.getLogger(IncreaseWindFieldListener.class.getName());
    private final TextField increaseWindField;
    private final Slider increaseWindSlider;

    public IncreaseWindFieldListener(TextField increaseWindField, Slider increaseWindSlider) {
        this.increaseWindField = increaseWindField;
        this.increaseWindSlider = increaseWindSlider;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
        logger.log(Level.INFO, "changed: " + observable + "; newValue: " + newValue);
        if (Pattern.compile("\\d([,.]\\d?)?").matcher(newValue).matches()) {
            float floatValue = Float.parseFloat(Pattern.compile(",").matcher(newValue).replaceAll("."));
            if (DSJ2FXInterfaceHelper.isAllowedIncreaseWindValue(floatValue)) {
                increaseWindSlider.setValue(floatValue);
                increaseWindField.setText(newValue);
            } else {
                increaseWindSlider.setValue(DSJ2FXInterfaceHelper.MAX_INCREASE_WIND_VALUE);
                increaseWindField.setText(Float.toString(DSJ2FXInterfaceHelper.MAX_INCREASE_WIND_VALUE));
            }
        } else if (newValue.length() > 3) {
            increaseWindField.setText(newValue.substring(0, 3));
        } else if (Pattern.compile("\\d{2,3}").matcher(newValue).matches()) {
            increaseWindField.setText(newValue.substring(0, 1));
        }
    }
}
