package trainer.core.listeners.sliders;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import trainer.core.DSJ2FXInterfaceHelper;

import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SliderListener implements ChangeListener<Number> {

    private static final Logger logger = Logger.getLogger(SliderListener.class.getName());
    private final TextField sliderRelatedTextField;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.0");

    public SliderListener(TextField sliderRelatedTextField) {
        this.sliderRelatedTextField = sliderRelatedTextField;
    }

    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
        logger.log(Level.INFO, "changed: " + observable + "; newValue: " + newValue);
        decimalFormat.setMinimumIntegerDigits(1);
        sliderRelatedTextField.setText(decimalFormat.format(newValue));
    }
}
