package trainer.core;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DSJ2HotkeySettingsController implements Initializable {

    private static final Logger logger = Logger.getLogger(DSJ2HotkeySettingsController.class.getName());
    public static ResourceBundle resourceBundle;

    public Label activateHotkeyLabel;
    public ComboBox<String> activateHotkeyComboBox;
    public Label deactivateHotkeyLabel;
    public ComboBox<String> deactivateHotkeyComboBox;
    public Label speedUpHotkeyLabel;
    public ComboBox<String> speedUpHotkeyComboBox;
    public Label liftUpHotkeyLabel;
    public ComboBox<String> liftUpHotkeyComboBox;
    public Label increaseWindHotkeyLabel;
    public ComboBox<String> increaseWindHotkeyComboBox;
    public Label decreaseWindHotkeyLabel;
    public ComboBox<String> decreaseWindHotkeyComboBox;
    public Button cancelHotkeys;
    public Pane trainerPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
        localizeStrings();
    }

    public void localizeStrings() {
        Locale locale = Locale.getDefault();
        try{
            resourceBundle = ResourceBundle.getBundle("trainer.resources.StringsResourceRB", locale);
        } catch (MissingResourceException mre){
            logger.log(Level.WARNING, "no bundle with locale " + locale + " found, switching to english");
            resourceBundle = ResourceBundle.getBundle("trainer.resources.StringsResourceRB", Locale.ENGLISH);
        }
        activateHotkeyLabel.setText(resourceBundle.getString("activateHotkey"));
        deactivateHotkeyLabel.setText(resourceBundle.getString("deactivateHotkey"));
        speedUpHotkeyLabel.setText(resourceBundle.getString("speedUpHotkey"));
        liftUpHotkeyLabel.setText(resourceBundle.getString("liftUpHotkey"));
        increaseWindHotkeyLabel.setText(resourceBundle.getString("increaseWindHotkey"));
        decreaseWindHotkeyLabel.setText(resourceBundle.getString("decreaseWindHotkey"));
        cancelHotkeys.setText(resourceBundle.getString("returnButton"));
        setupChoiceBoxes();
    }

    public void setupChoiceBoxes() {
        Map<String, Integer> keysMap = DSJ2FXInterfaceHelper.KEY_STRING_TO_KEY_CODE;
        activateHotkeyComboBox.setItems(FXCollections.observableArrayList(keysMap.keySet()));
        activateHotkeyComboBox.getSelectionModel().select(DSJ2FXInterfaceHelper.activateKey);
        activateHotkeyComboBox.setVisibleRowCount(10);
        deactivateHotkeyComboBox.setItems(FXCollections.observableArrayList(keysMap.keySet()));
        deactivateHotkeyComboBox.getSelectionModel().select(DSJ2FXInterfaceHelper.deactivateKey);
        deactivateHotkeyComboBox.setVisibleRowCount(10);
        speedUpHotkeyComboBox.setItems(FXCollections.observableArrayList(keysMap.keySet()));
        speedUpHotkeyComboBox.getSelectionModel().select(DSJ2FXInterfaceHelper.speedUpKey);
        speedUpHotkeyComboBox.setVisibleRowCount(10);
        liftUpHotkeyComboBox.setItems(FXCollections.observableArrayList(keysMap.keySet()));
        liftUpHotkeyComboBox.getSelectionModel().select(DSJ2FXInterfaceHelper.liftUpKey);
        liftUpHotkeyComboBox.setVisibleRowCount(10);
        increaseWindHotkeyComboBox.setItems(FXCollections.observableArrayList(keysMap.keySet()));
        increaseWindHotkeyComboBox.getSelectionModel().select(DSJ2FXInterfaceHelper.increaseWindKey);
        increaseWindHotkeyComboBox.setVisibleRowCount(10);
        decreaseWindHotkeyComboBox.setItems(FXCollections.observableArrayList(keysMap.keySet()));
        decreaseWindHotkeyComboBox.getSelectionModel().select(DSJ2FXInterfaceHelper.decreaseWindKey);
        decreaseWindHotkeyComboBox.setVisibleRowCount(10);
    }

    @FXML
    public void closeWindow() {
        DSJ2FXInterfaceHelper.activateKey = activateHotkeyComboBox.getValue();
        DSJ2FXInterfaceHelper.deactivateKey = deactivateHotkeyComboBox.getValue();
        DSJ2FXInterfaceHelper.speedUpKey = speedUpHotkeyComboBox.getValue();
        DSJ2FXInterfaceHelper.liftUpKey = liftUpHotkeyComboBox.getValue();
        DSJ2FXInterfaceHelper.increaseWindKey = increaseWindHotkeyComboBox.getValue();
        DSJ2FXInterfaceHelper.decreaseWindKey = decreaseWindHotkeyComboBox.getValue();
        Stage stage = (Stage) cancelHotkeys.getScene().getWindow();
        stage.close();
    }
}
