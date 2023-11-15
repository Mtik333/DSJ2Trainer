package trainer.core;

import com.sun.jna.platform.win32.WinNT.HANDLE;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import trainer.DSJ2FXInterface;
import trainer.core.listeners.sliders.SliderListener;
import trainer.core.listeners.textfields.DecreaseWindFieldListener;
import trainer.core.listeners.textfields.ExtremeWindStrengthFieldListener;
import trainer.core.listeners.textfields.IncreaseWindFieldListener;
import trainer.core.listeners.textfields.LiftUpFieldListener;
import trainer.core.listeners.textfields.SpeedUpFieldListener;
import trainer.core.listeners.textfields.WindDirectionFieldListener;
import trainer.core.listeners.textfields.WindStrengthFieldListener;
import trainer.core.listeners.writer.AbstractDSJ2WriteTimer;
import trainer.core.listeners.writer.DSJ2WriteTimer;
import trainer.core.listeners.writer.DSJ2WriteTimerExtreme;
import trainer.exceptions.DSJ2DefaultValuesLoadingException;
import trainer.exceptions.DSJ2NotLaunchedException;
import trainer.exceptions.DosBoxProcessNotFoundException;
import trainer.exceptions.DosBoxStartedAsAdminException;
import trainer.exceptions.UnknownReasonException;
import trainer.exceptions.UnsupportedDosBoxVersionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DSJ2Controller implements Initializable, NativeKeyListener {

    private static final Logger globalScreenLogger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
    private static final Logger logger = Logger.getLogger(DSJ2Controller.class.getName());
    public static ResourceBundle resourceBundle;

    public Label topLabel;
    public Button findDosBoxButton;
    public Pane trainerPane;
    public Label windStrengthLabel;
    public Slider windStrengthSlider;
    public TextField windStrengthField;
    public Label windDirectionLabel;
    public Slider windDirectionSlider;
    public TextField windDirectionField;
    public Label speedUpLabel;
    public Slider speedUpSlider;
    public TextField speedUpField;
    public Label liftUpLabel;
    public Slider liftUpSlider;
    public TextField liftUpField;
    public Button activateButton;
    public Button deactivateButton;
    public Button exitButton;
    public Button helpButton;
    public Button saveButton;
    public Button loadButton;
    public CheckBox extremeModeCheckBox;
    public Slider increaseWindSlider;
    public Slider decreaseWindSlider;
    public Button hotkeysButton;
    public Label increaseWindLabel;
    public Label decreaseWindLabel;
    public TextField increaseWindField;
    public TextField decreaseWindField;

    private HANDLE dosBoxProcess;
    private long dsj2CommentAddressValue;
    private long dsj2LoadedAddressValue;
    private float speedUpValue;
    private float liftUpValue;
    private Timer timer;
    private AbstractDSJ2WriteTimer dsj2WriteTimer;

    private static String getPathToValuesFile(String runtimePath) throws IOException {
        logger.log(Level.INFO, "Running JAR");
        File valuesFile = new File(runtimePath.substring(1, runtimePath.lastIndexOf('/')) + '/' + "default.properties");
        if (valuesFile.createNewFile()) {
            logger.log(Level.INFO, "Created file with values");
        }
        return valuesFile.getPath();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
        globalScreenLogger.setLevel(Level.SEVERE);
        deactivatePane();
        localizeStrings();
    }

    public void localizeStrings() {
        Locale locale = Locale.getDefault();
        try {
            resourceBundle = ResourceBundle.getBundle("trainer.resources.StringsResourceRB", locale);
        } catch (MissingResourceException mre){
            logger.log(Level.WARNING, "no bundle with locale " + locale + " found, switching to english");
            resourceBundle = ResourceBundle.getBundle("trainer.resources.StringsResourceRB", Locale.ENGLISH);
        }
        topLabel.setText(resourceBundle.getString("topLabel"));
        findDosBoxButton.setText(resourceBundle.getString("detectButton"));
        windStrengthLabel.setText(resourceBundle.getString("windStrength"));
        windDirectionLabel.setText(resourceBundle.getString("windDirection"));
        speedUpLabel.setText(resourceBundle.getString("speedJumper"));
        liftUpLabel.setText(resourceBundle.getString("liftJumper"));
        activateButton.setText(resourceBundle.getString("activate"));
        deactivateButton.setText(resourceBundle.getString("deactivate"));
        helpButton.setText(resourceBundle.getString("help"));
        exitButton.setText(resourceBundle.getString("exit"));
        saveButton.setText(resourceBundle.getString("saveValues"));
        loadButton.setText(resourceBundle.getString("loadValues"));
        hotkeysButton.setText(resourceBundle.getString("changeHotkeys"));
        increaseWindLabel.setText(resourceBundle.getString("increaseWind"));
        decreaseWindLabel.setText(resourceBundle.getString("decreaseWind"));
        extremeModeCheckBox.setText(resourceBundle.getString("checkBoxLabel"));
        windStrengthLabel.setTooltip(new Tooltip(resourceBundle.getString("windStrength")));
        extremeModeCheckBox.setTooltip(new Tooltip(resourceBundle.getString("checkBoxLabel")));
        windDirectionLabel.setTooltip(new Tooltip(resourceBundle.getString("windDirection")));
        speedUpLabel.setTooltip(new Tooltip(resourceBundle.getString("speedJumper")));
        liftUpLabel.setTooltip(new Tooltip(resourceBundle.getString("liftJumper")));
        setupTextFields();
        setupSliders();
    }

    public void deactivatePane() {
        logger.log(Level.INFO, "deactivateAtStart");
        trainerPane.setEffect(new GaussianBlur());
        activateButton.setDisable(true);
        deactivateButton.setDisable(true);
        hotkeysButton.setDisable(true);
        saveButton.setDisable(true);
        loadButton.setDisable(true);
        slidersFieldsDisable(true);
    }

    public void reEnableDetectButton() {
        findDosBoxButton.setDisable(false);
    }

    public void detectDosBoxWithDSJ2() {
        logger.log(Level.INFO, "detectDOSBoxWithDSJ2");
        try {
            dosBoxProcess = DSJ2TrainerV2.findDosBoxProcess();
            dsj2CommentAddressValue = !DSJ2TrainerV2.checkIfDSJ2PVersion(dosBoxProcess) ?
                    DSJ2TrainerV2.findDSJ2CommentAddress(dosBoxProcess, DSJ2TrainerV2.dosBoxVersion.getInitialPointer()) :
                    DSJ2TrainerV2.findDSJ2CommentAddress(dosBoxProcess, DSJ2TrainerV2.dosBoxVersion.getInitialPointerP());
            dsj2LoadedAddressValue = !DSJ2FXInterfaceHelper.isDSJ21PLaunched ?
                    DSJ2TrainerV2.findDSJ2LoadedAddress(dosBoxProcess, dsj2CommentAddressValue, DSJ2TrainerV2.dosBoxVersion.getDsj2LoadedInDosBoxOffset()) :
                    DSJ2TrainerV2.findDSJ2LoadedAddress(dosBoxProcess, dsj2CommentAddressValue, DSJ2TrainerV2.dosBoxVersion.getDsj21PLoadedInDosBoxOffset());
            activatePane();
            findDosBoxButton.setDisable(true);
            logger.log(Level.SEVERE, "dosBoxProcess: " + dosBoxProcess + "; dsj2CommentAddressValue: " + dsj2CommentAddressValue +
                    "; dsj2LoadedAddressValue: " + dsj2LoadedAddressValue);
            if (!DSJ2FXInterfaceHelper.isDSJ21PLaunched) {
                DSJ2FXInterfaceHelper.showInformationAlert(DSJ2Controller.resourceBundle.getString("foundDSJ2")
                        + '\n' + DSJ2Controller.resourceBundle.getString("detectVersion") + DSJ2TrainerV2.dosBoxVersion);
            } else {
                DSJ2FXInterfaceHelper.showInformationAlert(DSJ2Controller.resourceBundle.getString("foundDSJ2P")
                        + '\n' + DSJ2Controller.resourceBundle.getString("detectVersion") + DSJ2TrainerV2.dosBoxVersion);
            }
            if (!GlobalScreen.isNativeHookRegistered()) {
                GlobalScreen.registerNativeHook();
                GlobalScreen.addNativeKeyListener(this);
            }
        } catch (DosBoxProcessNotFoundException | DSJ2NotLaunchedException | UnsupportedDosBoxVersionException
                | DosBoxStartedAsAdminException | UnknownReasonException exception) {
            globalScreenLogger.log(Level.SEVERE, exception.getClass().getSimpleName());
            globalScreenLogger.log(Level.SEVERE, exception.getMessage());
            DSJ2FXInterfaceHelper.showErrorAlert(exception);
        } catch (NativeHookException nhe) {
            logger.log(Level.SEVERE, "Exception when registering native hook: " + nhe.getMessage());
            nhe.printStackTrace();
        }
    }

    public void activatePane() {
        logger.log(Level.INFO, "activatePane");
        trainerPane.setEffect(null);
        activateButton.setDisable(false);
        deactivateButton.setDisable(true);
        hotkeysButton.setDisable(false);
        saveButton.setDisable(false);
        loadButton.setDisable(false);
        slidersFieldsDisable(false);
    }

    public void activateTrainer() {
        logger.log(Level.INFO, "activateTrainer");
        float windStrengthValue = (float) windStrengthSlider.getValue();
        float windDirectionValue = (float) windDirectionSlider.getValue();
        saveButton.setDisable(true);
        loadButton.setDisable(true);
        hotkeysButton.setDisable(true);
        activateButton.setDisable(true);
        deactivateButton.setDisable(false);
        speedUpValue = (float) speedUpSlider.getValue();
        liftUpValue = (float) liftUpSlider.getValue();
        slidersFieldsDisable(true);
        logger.log(Level.FINE, "windStrengthValue: " + windStrengthValue + "; windDirectionValue: " + windDirectionValue +
                "; speedUpValue: " + speedUpValue + "; liftUpValue: " + liftUpValue + "; checkbox selected? " + extremeModeCheckBox.isSelected());
        timer = new Timer();
        if (extremeModeCheckBox.isSelected()) {
            dsj2WriteTimer = new DSJ2WriteTimerExtreme(dosBoxProcess, dsj2CommentAddressValue, dsj2LoadedAddressValue,
                    this);
            timer.schedule(dsj2WriteTimer, 0, DSJ2FXInterfaceHelper.TIMER_EXTREME_PERIOD);
        } else {
            dsj2WriteTimer = new DSJ2WriteTimer(dosBoxProcess, dsj2CommentAddressValue, dsj2LoadedAddressValue,
                    this);
            timer.schedule(dsj2WriteTimer, 0, DSJ2FXInterfaceHelper.TIMER_DEFAULT_PERIOD);
        }
        logger.log(Level.FINE, "timer: " + timer);
        try {
            logger.log(Level.FINE, "GlobalScreen.registerNativeHook() and addNativeKeyListener(this)");
            if (!GlobalScreen.isNativeHookRegistered()) {
                GlobalScreen.registerNativeHook();
                GlobalScreen.addNativeKeyListener(this);
            }
        } catch (NativeHookException nhe) {
            logger.log(Level.SEVERE, "Exception when registering native hook: " + nhe.getMessage());
            nhe.printStackTrace();
        }
    }

    public void deactivateTrainer() {
        logger.log(Level.INFO, "deactivateTrainer");
        activateButton.setDisable(false);
        deactivateButton.setDisable(true);
        saveButton.setDisable(false);
        loadButton.setDisable(false);
        hotkeysButton.setDisable(false);
        slidersFieldsDisable(false);
        logger.log(Level.FINE, "Canceling timer");
        timer.cancel();
        dsj2WriteTimer.cancel();
    }

    public void showHotkeysWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DSJ2FXInterface.class.getResource("keys" + ".fxml"));
            Stage stage = new Stage();
            stage.setTitle(resourceBundle.getString("changeHotkeys"));
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        logger.log(Level.INFO, "nativeKeyPressed: nativeKeyEvent: " + NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode())
                + "activateButton isDisabled? " + activateButton.isDisabled());
        if (dosBoxProcess == null || dsj2CommentAddressValue == 0) {
            logger.log(Level.SEVERE, "No DOSBox process or no DSJ2 launched");
            return;
        }
        //avoiding problem with [de]activate key bind to both
        if (DSJ2FXInterfaceHelper.activateKey.contentEquals(DSJ2FXInterfaceHelper.deactivateKey)) {
            if (nativeKeyEvent.getKeyCode() ==
                    DSJ2FXInterfaceHelper.KEY_STRING_TO_KEY_CODE.get(DSJ2FXInterfaceHelper.activateKey)) {
                if (!activateButton.isDisabled() && deactivateButton.isDisabled()) {
                    activateTrainer();
                } else if (activateButton.isDisabled() && !deactivateButton.isDisabled()) {
                    deactivateTrainer();
                }
            }
        } else {
            if (nativeKeyEvent.getKeyCode() ==
                    DSJ2FXInterfaceHelper.KEY_STRING_TO_KEY_CODE.get(DSJ2FXInterfaceHelper.activateKey)) {
                if (!activateButton.isDisabled() && deactivateButton.isDisabled()) {
                    activateTrainer();
                }
            }
            if (nativeKeyEvent.getKeyCode() ==
                    DSJ2FXInterfaceHelper.KEY_STRING_TO_KEY_CODE.get(DSJ2FXInterfaceHelper.deactivateKey)) {
                if (activateButton.isDisabled() && !deactivateButton.isDisabled()) {
                    deactivateTrainer();
                }
            }
        }
        if (activateButton.isDisabled() && nativeKeyEvent.getKeyCode() ==
                DSJ2FXInterfaceHelper.KEY_STRING_TO_KEY_CODE.get(DSJ2FXInterfaceHelper.speedUpKey)) {
            float currentSpeed = DSJ2TrainerV2.readSingleValue(dosBoxProcess, dsj2CommentAddressValue, DSJ2TrainerV2.JUMPER_SPEED_OFFSET);
            logger.log(Level.INFO, DSJ2FXInterfaceHelper.speedUpKey + " key detected, current speed: " + currentSpeed);
            DSJ2TrainerV2.writeSingleFloatValue(dosBoxProcess, dsj2CommentAddressValue, DSJ2TrainerV2.JUMPER_SPEED_OFFSET, currentSpeed + speedUpValue);
        }
        if (activateButton.isDisabled() && nativeKeyEvent.getKeyCode() ==
                DSJ2FXInterfaceHelper.KEY_STRING_TO_KEY_CODE.get(DSJ2FXInterfaceHelper.liftUpKey)) {
            float currentLift = DSJ2TrainerV2.readSingleValue(dosBoxProcess, dsj2CommentAddressValue, DSJ2TrainerV2.JUMPER_LIFT_OFFSET);
            logger.log(Level.INFO, DSJ2FXInterfaceHelper.liftUpKey + " key detected, current lift: " + currentLift);
            DSJ2TrainerV2.writeSingleFloatValue(dosBoxProcess, dsj2CommentAddressValue, DSJ2TrainerV2.JUMPER_LIFT_OFFSET, currentLift - liftUpValue);
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
    }

    public void setupTextFields() {
        DSJ2FXInterfaceHelper.windStrengthListener = new WindStrengthFieldListener(windStrengthField, windStrengthSlider);
        windStrengthField.textProperty().addListener(DSJ2FXInterfaceHelper.windStrengthListener);
        windDirectionField.textProperty().addListener(new WindDirectionFieldListener(windDirectionField, windDirectionSlider));
        speedUpField.textProperty().addListener(new SpeedUpFieldListener(speedUpField, speedUpSlider));
        liftUpField.textProperty().addListener(new LiftUpFieldListener(liftUpField, liftUpSlider));
        increaseWindField.textProperty().addListener(new IncreaseWindFieldListener(increaseWindField, increaseWindSlider));
        decreaseWindField.textProperty().addListener(new DecreaseWindFieldListener(decreaseWindField, decreaseWindSlider));
    }

    public void setupSliders() {
        windStrengthSlider.valueProperty().addListener(new SliderListener(windStrengthField));
        windDirectionSlider.valueProperty().addListener(new SliderListener(windDirectionField));
        speedUpSlider.valueProperty().addListener(new SliderListener(speedUpField));
        liftUpSlider.valueProperty().addListener(new SliderListener(liftUpField));
        increaseWindSlider.valueProperty().addListener(new SliderListener(increaseWindField));
        decreaseWindSlider.valueProperty().addListener(new SliderListener(decreaseWindField));
    }

    public void slidersFieldsDisable(boolean disable) {
        windStrengthSlider.setDisable(disable);
        windDirectionSlider.setDisable(disable);
        speedUpSlider.setDisable(disable);
        liftUpSlider.setDisable(disable);
        increaseWindSlider.setDisable(disable);
        decreaseWindSlider.setDisable(disable);
        windStrengthField.setDisable(disable);
        windDirectionField.setDisable(disable);
        speedUpField.setDisable(disable);
        liftUpField.setDisable(disable);
        increaseWindField.setDisable(disable);
        decreaseWindField.setDisable(disable);
        extremeModeCheckBox.setDisable(disable);
    }

    public void showHelp() {
        DSJ2FXInterfaceHelper.showHelp();
    }

    public void exitApp() {
        DSJ2FXInterfaceHelper.exitApp();
    }

    public void saveValues2() {
        logger.log(Level.FINE, "Saving values to file");
        String runtimePath = DSJ2Controller.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String valuesPath;
        if (runtimePath.toLowerCase().contains(".jar")) {
            logger.log(Level.FINE, "Running in JAR");
            try {
                valuesPath = getPathToValuesFile(runtimePath);
            } catch (IOException exp) {
                logger.log(Level.SEVERE, "FileNotFoundException: Problem when getting path to values file: " + exp.getMessage());
                exp.printStackTrace();
                DSJ2FXInterfaceHelper.showErrorAlert(exp);
                return;
            }
            try (InputStream in = new FileInputStream(valuesPath)) {
                reallySaveValues(in, valuesPath);
            } catch (FileNotFoundException exp) {
                logger.log(Level.SEVERE, "FileNotFoundException: Problem when saving values file: " + exp.getMessage());
                exp.printStackTrace();
                DSJ2FXInterfaceHelper.showErrorAlert(exp);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "IOException: Problem when saving values file: " + e.getMessage());
                e.printStackTrace();
                DSJ2FXInterfaceHelper.showErrorAlert(e);
            }
        } else {
            logger.log(Level.INFO, "Running in IDE");
            URL defaultProps = DSJ2Controller.class.getResource("/trainer/default.properties");
            if (defaultProps != null) {
                valuesPath = defaultProps.getPath();
                try (InputStream in = DSJ2Controller.class.getResourceAsStream("/trainer/default.properties")) {
                    reallySaveValues(in, valuesPath);
                } catch (IOException exp) {
                    logger.log(Level.SEVERE, "IOException: Problem when saving values file: " + exp.getMessage());
                    exp.printStackTrace();
                    DSJ2FXInterfaceHelper.showErrorAlert(exp);
                }
            }
        }
    }

    private void reallySaveValues(InputStream in, String valuesPath) {
        try {
            Properties properties = new Properties();
            properties.load(in);
            float windStrengthValue = (float) windStrengthSlider.getValue();
            int windDirectionValue = (int) windDirectionSlider.getValue();
            float liftUpValue = (float) liftUpSlider.getValue();
            float speedUpValue = (float) speedUpSlider.getValue();
            properties.setProperty("windStrength", String.valueOf(windStrengthValue));
            properties.setProperty("windDirection", String.valueOf(windDirectionValue));
            properties.setProperty("speedUp", String.valueOf(speedUpValue));
            properties.setProperty("liftUp", String.valueOf(liftUpValue));
            properties.setProperty("activateKey", DSJ2FXInterfaceHelper.activateKey);
            properties.setProperty("deactivateKey", DSJ2FXInterfaceHelper.deactivateKey);
            properties.setProperty("speedUpKey", DSJ2FXInterfaceHelper.speedUpKey);
            properties.setProperty("liftUpKey", DSJ2FXInterfaceHelper.liftUpKey);
            properties.setProperty("increaseWindKey", DSJ2FXInterfaceHelper.increaseWindKey);
            properties.setProperty("decreaseWindKey", DSJ2FXInterfaceHelper.decreaseWindKey);
            properties.store(new FileOutputStream(valuesPath), null);
            logger.log(Level.INFO, "Saving current values of sliders to file successful");
            DSJ2FXInterfaceHelper.showInformationAlert(DSJ2Controller.resourceBundle.getString("savedValuesSuccess"));
            in.close();
        } catch (FileNotFoundException exp) {
            logger.log(Level.SEVERE, "FileNotFoundException: Problem when saving values file: " + exp.getMessage());
            exp.printStackTrace();
            DSJ2FXInterfaceHelper.showErrorAlert(exp);
        } catch (IOException exp) {
            logger.log(Level.SEVERE, "IOException: Problem when saving values file: " + exp.getMessage());
            exp.printStackTrace();
            DSJ2FXInterfaceHelper.showErrorAlert(exp);
        }
    }

    public void loadValues2() {
        logger.log(Level.INFO, "Loading values of sliders from file");
        String runtimePath = DSJ2Controller.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (runtimePath.toLowerCase().contains(".jar")) {
            String valuesPath = "";
            try {
                valuesPath = getPathToValuesFile(runtimePath);
            } catch (IOException exp) {
                logger.log(Level.SEVERE, "IOException: Problem when getting path to values file: " + exp.getMessage());
                exp.printStackTrace();
                DSJ2FXInterfaceHelper.showErrorAlert(exp);
            }
            try (InputStream in = new FileInputStream(valuesPath)) {
                checkInputStream(in);
                reallyLoadValues(in);
            } catch (FileNotFoundException exp) {
                logger.log(Level.SEVERE, "FileNotFoundException: Problem when getting path to values file: " + exp.getMessage());
                exp.printStackTrace();
                DSJ2FXInterfaceHelper.showErrorAlert(exp);
            } catch (IOException exp) {
                logger.log(Level.SEVERE, "IOException: Problem when getting path to values file: " + exp.getMessage());
                exp.printStackTrace();
                DSJ2FXInterfaceHelper.showErrorAlert(exp);
            }
        } else {
            try (InputStream in = DSJ2Controller.class.getResourceAsStream("/trainer/default.properties")) {
                reallyLoadValues(in);
            } catch (IOException exp) {
                logger.log(Level.SEVERE, "IOException: Problem when getting path to values file: " + exp.getMessage());
                exp.printStackTrace();
                DSJ2FXInterfaceHelper.showErrorAlert(exp);
            }
        }
    }

    private void reallyLoadValues(InputStream in) {
        try {
            Properties properties = new Properties();
            properties.load(in);
            float windStrengthFromProp = Float.parseFloat(properties.getProperty("windStrength"));
            int windDirectionFromProp = Integer.parseInt(properties.getProperty("windDirection"));
            float speedUpFromProp = Float.parseFloat(properties.getProperty("speedUp"));
            float liftUpFromProp = Float.parseFloat(properties.getProperty("liftUp"));
            String activateKeyProp = properties.getProperty("activateKey");
            String deactivateKeyProp = properties.getProperty("deactivateKey");
            String speedUpKeyProp = properties.getProperty("speedUpKey");
            String liftUpKeyProp = properties.getProperty("liftUpKey");
            String increaseWindKeyProp = properties.getProperty("increaseWindKey");
            String decreaseWindKeyProp = properties.getProperty("decreaseWindKey");
            setValuesFromProperties(windStrengthFromProp, windDirectionFromProp,
                    speedUpFromProp, liftUpFromProp, activateKeyProp, deactivateKeyProp,
                    speedUpKeyProp, liftUpKeyProp, increaseWindKeyProp, decreaseWindKeyProp);
            logger.log(Level.INFO, "Loading current values from file successful");
            DSJ2FXInterfaceHelper.showInformationAlert(DSJ2Controller.resourceBundle.getString("loadedValuesSuccess"));
            in.close();
        } catch (IOException | NumberFormatException exp) {
            logger.log(Level.SEVERE, "FileNotFoundException: Problem when loading values from file: " + exp.getMessage());
            exp.printStackTrace();
            Exception exception = new DSJ2DefaultValuesLoadingException();
            DSJ2FXInterfaceHelper.showErrorAlert(exception);
        }
    }

    private void setValuesFromProperties(float windStrengthFromProp, int windDirectionFromProp,
                                         float speedUpFromProp, float liftUpFromProp,
                                         String activateKeyProp, String deactivateKeyProp,
                                         String speedUpKeyProp, String liftUpKeyProp,
                                         String increaseWindKeyProp, String decreaseWindKeyProp) {
        if (DSJ2FXInterfaceHelper.isAllowedWindStrengthValue(windStrengthFromProp)) {
            windStrengthSlider.setValue(windStrengthFromProp);
        }
        if (DSJ2FXInterfaceHelper.isAllowedWindDirectionValue(windDirectionFromProp)) {
            windDirectionSlider.setValue(windDirectionFromProp);
        }
        if (DSJ2FXInterfaceHelper.isAllowedLiftUpValue(liftUpFromProp)) {
            liftUpSlider.setValue(liftUpFromProp);
        }
        if (DSJ2FXInterfaceHelper.isAllowedSpeedUpValue(speedUpFromProp)) {
            speedUpSlider.setValue(speedUpFromProp);
        }
        if (DSJ2FXInterfaceHelper.isAllowedKeyMapping(activateKeyProp)) {
            DSJ2FXInterfaceHelper.activateKey = activateKeyProp;
        }
        if (DSJ2FXInterfaceHelper.isAllowedKeyMapping(deactivateKeyProp)) {
            DSJ2FXInterfaceHelper.deactivateKey = deactivateKeyProp;
        }
        if (DSJ2FXInterfaceHelper.isAllowedKeyMapping(speedUpKeyProp)) {
            DSJ2FXInterfaceHelper.speedUpKey = speedUpKeyProp;
        }
        if (DSJ2FXInterfaceHelper.isAllowedKeyMapping(liftUpKeyProp)) {
            DSJ2FXInterfaceHelper.liftUpKey = liftUpKeyProp;
        }
        if (DSJ2FXInterfaceHelper.isAllowedKeyMapping(increaseWindKeyProp)) {
            DSJ2FXInterfaceHelper.increaseWindKey = increaseWindKeyProp;
        }
        if (DSJ2FXInterfaceHelper.isAllowedKeyMapping(decreaseWindKeyProp)) {
            DSJ2FXInterfaceHelper.decreaseWindKey = decreaseWindKeyProp;
        }
    }

    public void checkInputStream(InputStream in) throws IOException {
        assert Objects.requireNonNull(in, () -> resourceBundle.getString("loadingValuesError")).available() != 0;
    }

    @FXML
    private void checkboxValueChanged(ActionEvent event) {
        logger.log(Level.FINE, "checkboxValueChanged");
        CheckBox checkBox = (CheckBox) event.getSource();
        if (checkBox.isSelected()) {
            logger.log(Level.FINE, "checkbox is now selected");
            windStrengthField.textProperty().removeListener(DSJ2FXInterfaceHelper.windStrengthListener);
            DSJ2FXInterfaceHelper.windStrengthListener = new ExtremeWindStrengthFieldListener(windStrengthField);
            windStrengthField.textProperty().addListener(DSJ2FXInterfaceHelper.windStrengthListener);
        } else {
            logger.log(Level.FINE, "checkbox is now unselected");
            windStrengthField.textProperty().removeListener(DSJ2FXInterfaceHelper.windStrengthListener);
            DSJ2FXInterfaceHelper.windStrengthListener = new WindStrengthFieldListener(windStrengthField, windStrengthSlider);
            windStrengthField.textProperty().addListener(DSJ2FXInterfaceHelper.windStrengthListener);
            windStrengthField.setText(String.valueOf(windStrengthSlider.getValue()));
        }
    }
}
