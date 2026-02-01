package trainer.core.listeners.writer;

import javafx.application.Platform;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import trainer.core.DSJ2Controller;
import trainer.core.DSJ2FXInterfaceHelper;
import trainer.core.DSJ2TrainerV2;
import trainer.core.platform.PlatformMemoryAccess.ProcessHandle;
import trainer.exceptions.DSJ2UnloadedException;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractDSJ2WriteTimer extends TimerTask implements NativeKeyListener {

    static final Logger logger = Logger.getLogger(AbstractDSJ2WriteTimer.class.getName());
    private static final float CONSTANT_180_FLOAT = 180f;
    final ProcessHandle dosBoxProcess;
    final long dsj2CommentAddressValue;
    final long dsj2LoadedAddressValue;
    final DSJ2Controller dsj2Controller;
    final float windDirectionValue;
    private final byte gamePausedValue = Byte.MAX_VALUE;
    private final byte hillPausedValue = Byte.MAX_VALUE;
    float windStrengthValue;
    float increaseWindValue;
    float decreaseWindValue;

    static {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
    }

    protected AbstractDSJ2WriteTimer(ProcessHandle dosBoxProcess, long dsj2CommentAddressValue,
                                     long dsj2LoadedAddressValue, DSJ2Controller controller) {
        this.dosBoxProcess = dosBoxProcess;
        this.dsj2CommentAddressValue = dsj2CommentAddressValue;
        this.dsj2LoadedAddressValue = dsj2LoadedAddressValue;
        windDirectionValue = (float) controller.windDirectionSlider.getValue();
        windStrengthValue = (float) controller.windStrengthSlider.getValue();
        increaseWindValue = (float) controller.increaseWindSlider.getValue();
        decreaseWindValue = (float) controller.decreaseWindSlider.getValue();
        dsj2Controller = controller;
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
        GlobalScreen.addNativeKeyListener(this);
    }

    void writeValues() {
        logger.log(Level.INFO, "writeValues - base address: 0x" + Long.toHexString(dsj2CommentAddressValue));
        //set wind strength variance to 0
        logger.log(Level.INFO, "Writing wind strength variance to 0x" +
            Long.toHexString(dsj2CommentAddressValue + DSJ2TrainerV2.WIND_STRENGTH_VARIANCE_OFFSET));
        DSJ2TrainerV2.writeSingleFloatValue(dosBoxProcess, dsj2CommentAddressValue,
                DSJ2TrainerV2.WIND_STRENGTH_VARIANCE_OFFSET, 0);
        //set wind direction variance to 0
        DSJ2TrainerV2.writeSingleFloatValue(dosBoxProcess, dsj2CommentAddressValue,
                DSJ2TrainerV2.WIND_DIRECTION_VARIANCE_OFFSET, 0);
        float windDirectionPi = (float) ((windDirectionValue / CONSTANT_180_FLOAT) * Math.PI);
        logger.log(Level.FINE, "windDirectionPi: " + windDirectionPi);
        float horizontalValuePart = (float) (Math.cos(Math.toRadians(windDirectionValue)) * windStrengthValue);
        float verticalValuePart = (float) (Math.sin(Math.toRadians(windDirectionValue)) * windStrengthValue);
        logger.log(Level.INFO, "Writing wind - horizontal: " + horizontalValuePart + ", vertical: " + verticalValuePart);
        DSJ2TrainerV2.writeSingleFloatValue(dosBoxProcess, dsj2CommentAddressValue,
                DSJ2TrainerV2.ROUGH_WIND_VALUE_OFFSET, windDirectionPi);
        DSJ2TrainerV2.writeSingleFloatValue(dosBoxProcess, dsj2CommentAddressValue,
                DSJ2TrainerV2.IDEAL_WIND_VALUE_OFFSET, windDirectionPi);
        DSJ2TrainerV2.writeSingleFloatValue(dosBoxProcess, dsj2CommentAddressValue,
                DSJ2TrainerV2.WIND_STRENGTH_HORIZONTAL_PART_OFFSET, horizontalValuePart);
        DSJ2TrainerV2.writeSingleFloatValue(dosBoxProcess, dsj2CommentAddressValue,
                DSJ2TrainerV2.WIND_STRENGTH_VERTICAL_PART_OFFSET, verticalValuePart);

        // Verify the write by reading back
        float readBack = DSJ2TrainerV2.readSingleValue(dosBoxProcess, dsj2CommentAddressValue,
                DSJ2TrainerV2.WIND_STRENGTH_HORIZONTAL_PART_OFFSET);
        logger.log(Level.INFO, "Read back horizontal wind value: " + readBack +
            " (expected: " + horizontalValuePart + ")");
    }

    boolean isDsj2StillLoaded() {
        logger.log(Level.INFO, "isDsj2StillLoaded: dsj2LoadedAddressValue: " + dsj2LoadedAddressValue);
        boolean stillLoaded = DSJ2TrainerV2.isStringAtAddress(dosBoxProcess, dsj2LoadedAddressValue,
                DSJ2TrainerV2.DSJ_V2_STRING);
        if (stillLoaded) {
            logger.log(Level.INFO, "DSJ2 still in memory");
            return true;
        }
        logger.log(Level.INFO, "DSJ2 no longer in memory");
        return false;
    }

    void checkIfProcessShutdown() {
        logger.log(Level.FINE, "run: gamePaused: " + gamePausedValue + "; hillPaused: " + hillPausedValue);
        if (!isDsj2StillLoaded()) {
            logger.log(Level.SEVERE, "DSJ2 unloaded, deactivating trainer");
            super.cancel();
            Platform.runLater(() -> {
                dsj2Controller.deactivateTrainer();
                dsj2Controller.deactivatePane();
                try {
                    logger.log(Level.FINE, "GlobalScreen.unregisterNativeHook() and removeNativeKeyListener(this)");
                    GlobalScreen.unregisterNativeHook();
                    GlobalScreen.removeNativeKeyListener(dsj2Controller);
                } catch (NativeHookException nhe) {
                    logger.log(Level.SEVERE, "Exception when unregistering native hook: " + nhe.getMessage());
                    nhe.printStackTrace();
                }
                DSJ2FXInterfaceHelper.showErrorAlert(new DSJ2UnloadedException());
                dsj2Controller.reEnableDetectButton();
            });
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        if (nativeKeyEvent.getKeyCode() ==
                DSJ2FXInterfaceHelper.KEY_STRING_TO_KEY_CODE.get(DSJ2FXInterfaceHelper.increaseWindKey)) {
            float targetValue = windStrengthValue + increaseWindValue;
            float horizontalValuePart = (float) (Math.cos(Math.toRadians(windDirectionValue))
                    * (targetValue));
            float verticalValuePart = (float) (Math.sin(Math.toRadians(windDirectionValue))
                    * (targetValue));
            DSJ2TrainerV2.writeSingleFloatValue(dosBoxProcess, dsj2CommentAddressValue,
                    DSJ2TrainerV2.WIND_STRENGTH_HORIZONTAL_PART_OFFSET, horizontalValuePart);
            DSJ2TrainerV2.writeSingleFloatValue(dosBoxProcess, dsj2CommentAddressValue,
                    DSJ2TrainerV2.WIND_STRENGTH_VERTICAL_PART_OFFSET, verticalValuePart);
            windStrengthValue = targetValue;
        }
        if (nativeKeyEvent.getKeyCode() ==
                DSJ2FXInterfaceHelper.KEY_STRING_TO_KEY_CODE.get(DSJ2FXInterfaceHelper.decreaseWindKey)) {
            float targetValue = windStrengthValue - decreaseWindValue;
            if (targetValue < 0) {
                logger.log(Level.INFO, "Decreasing below 0, so changing to 0");
                targetValue = 0;
            }
            float horizontalValuePart = (float) (Math.cos(Math.toRadians(windDirectionValue))
                    * (targetValue));
            float verticalValuePart = (float) (Math.sin(Math.toRadians(windDirectionValue))
                    * (targetValue));
            DSJ2TrainerV2.writeSingleFloatValue(dosBoxProcess, dsj2CommentAddressValue,
                    DSJ2TrainerV2.WIND_STRENGTH_HORIZONTAL_PART_OFFSET, horizontalValuePart);
            DSJ2TrainerV2.writeSingleFloatValue(dosBoxProcess, dsj2CommentAddressValue,
                    DSJ2TrainerV2.WIND_STRENGTH_VERTICAL_PART_OFFSET, verticalValuePart);
            windStrengthValue = targetValue;
        }
    }

    @Override
    public boolean cancel() {
        GlobalScreen.removeNativeKeyListener(this);
        return super.cancel();
    }

    @Override
    public String toString() {
        return "DSJ2WriteTimerV3{" +
                "dosBoxProcess=" + dosBoxProcess +
                ", dsj2CommentAddressValue=" + dsj2CommentAddressValue +
                ", dsj2LoadedAddressValue=" + dsj2LoadedAddressValue +
                ", windDirectionValue=" + windDirectionValue +
                ", windStrengthValue=" + windStrengthValue +
                ", dsj2Controller=" + dsj2Controller +
                ", gamePausedValue=" + gamePausedValue +
                ", hillPausedValue=" + hillPausedValue +
                '}';
    }

}
