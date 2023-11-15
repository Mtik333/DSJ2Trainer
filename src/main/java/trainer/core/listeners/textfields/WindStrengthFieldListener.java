package trainer.core.listeners.textfields;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import trainer.core.DSJ2FXInterfaceHelper;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class WindStrengthFieldListener implements ChangeListener<String> {

    private static final Logger logger = Logger.getLogger(WindStrengthFieldListener.class.getName());
    private final TextField windStrengthField;
    private final Slider windStrengthSlider;

    public WindStrengthFieldListener(TextField windStrengthField, Slider windStrengthSlider) {
        this.windStrengthField = windStrengthField;
        this.windStrengthSlider = windStrengthSlider;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
        logger.log(Level.INFO, "changed: " + observable + "; newValue: " + newValue);
        if (Pattern.compile("\\d([,.]\\d?)?").matcher(newValue).matches()) {
            float floatValue = Float.parseFloat(Pattern.compile(",").matcher(newValue).replaceAll("."));
            if (DSJ2FXInterfaceHelper.isAllowedWindStrengthValue(floatValue)) {
                windStrengthSlider.setValue(floatValue);
                windStrengthField.setText(newValue);
            } else {
                windStrengthSlider.setValue(DSJ2FXInterfaceHelper.MAX_WIND_STRENGTH_VALUE);
                windStrengthField.setText(Float.toString(DSJ2FXInterfaceHelper.MAX_WIND_STRENGTH_VALUE));
            }
        } else if (newValue.length() > 3) {
            windStrengthField.setText(newValue.substring(0, 3));
        } else if (Pattern.compile("\\d{2,3}").matcher(newValue).matches()) {
            windStrengthField.setText(newValue.substring(0, 1));
        }
    }
}
