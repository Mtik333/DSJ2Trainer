package trainer.core.listeners.textfields;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import trainer.core.DSJ2FXInterfaceHelper;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ExtremeWindStrengthFieldListener implements ChangeListener<String> {

    private static final Logger logger = Logger.getLogger(WindStrengthFieldListener.class.getName());
    private final TextField windStrengthField;

    public ExtremeWindStrengthFieldListener(TextField windStrengthField) {
        this.windStrengthField = windStrengthField;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
        logger.log(Level.INFO, "changed: " + observable + "; newValue: " + newValue);
        if (Pattern.compile("\\d{1,2}([,.]\\d?)?").matcher(newValue).matches()) {
            windStrengthField.setText(newValue);
        } else if (newValue.length() > 4) {
            windStrengthField.setText(newValue.substring(0, 4));
        } else if (Pattern.compile("\\d{3,4}").matcher(newValue).matches()) {
            windStrengthField.setText(newValue.substring(0, 2));
        }
    }
}
