package trainer.core.listeners.textfields;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import trainer.core.DSJ2FXInterfaceHelper;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class WindDirectionFieldListener implements ChangeListener<String> {

    private static final Logger logger = Logger.getLogger(WindDirectionFieldListener.class.getName());
    private final TextField windDirectionField;
    private final Slider windDirectionSlider;

    public WindDirectionFieldListener(TextField windDirectionField, Slider windDirectionSlider) {
        this.windDirectionField = windDirectionField;
        this.windDirectionSlider = windDirectionSlider;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
        logger.log(Level.INFO, "changed: " + observable + "; newValue: " + newValue);
        if (Pattern.compile("\\d{1,3}?").matcher(newValue).matches()) {
            int intValue = Integer.parseInt(newValue);
            if (DSJ2FXInterfaceHelper.isAllowedWindDirectionValue(intValue)) {
                windDirectionSlider.setValue(intValue);
                windDirectionField.setText(newValue);
            } else {
                windDirectionSlider.setValue(DSJ2FXInterfaceHelper.MAX_WIND_DIRECTION_VALUE);
                windDirectionField.setText(Float.toString(DSJ2FXInterfaceHelper.MAX_WIND_DIRECTION_VALUE));
            }
        } else if (newValue.length() > 3) {
            windDirectionField.setText(newValue.substring(0, 3));
        }
    }
}
