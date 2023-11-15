package trainer.core.listeners.writer;

import com.sun.jna.platform.win32.WinNT.HANDLE;
import org.jnativehook.keyboard.NativeKeyEvent;
import trainer.core.DSJ2Controller;
import trainer.core.DSJ2FXInterfaceHelper;
import trainer.core.DSJ2TrainerV2;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DSJ2WriteTimer extends AbstractDSJ2WriteTimer {

    private static final Logger logger = Logger.getLogger(DSJ2WriteTimer.class.getName());
    private static final byte GAME_PAUSED_AT_HILL_BYTE_VALUE = 27;
    private byte gamePausedValue = Byte.MAX_VALUE;
    private byte hillPausedValue = Byte.MAX_VALUE;
    private byte hillLoadedCodeValue = Byte.MAX_VALUE;
    private byte replayModeValue = Byte.MAX_VALUE;

    static {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
    }

    public DSJ2WriteTimer(HANDLE dosBoxProcess, long dsj2CommentAddressValue, long dsj2LoadedAddressValue,
                          DSJ2Controller controller) {
        super(dosBoxProcess, dsj2CommentAddressValue, dsj2LoadedAddressValue, controller);
    }

    @Override
    public void run() {
        checkIfProcessShutdown();
        byte hillLoadedCodeByte = DSJ2TrainerV2.readSingleByteValue(dosBoxProcess,
                dsj2CommentAddressValue, DSJ2TrainerV2.HILL_CODE_OFFSET);
        byte gamePausedByte = DSJ2TrainerV2.readSingleByteValue(dosBoxProcess,
                dsj2CommentAddressValue, DSJ2TrainerV2.PAUSE_MODE_OFFSET);
        byte hillPausedByte = DSJ2TrainerV2.readSingleByteValue(dosBoxProcess,
                dsj2CommentAddressValue, DSJ2TrainerV2.JUMPING_PAUSED_OFFSET);
        byte replayModeByte = DSJ2TrainerV2.readSingleByteValue(dosBoxProcess,
                dsj2CommentAddressValue, DSJ2TrainerV2.REPLAY_LOADED_OFFSET);
        logger.log(Level.INFO, "values from game: gamePausedByte: " + gamePausedByte
                + "; hillPauseByte: " + hillPausedByte + "; hillLoadedCodeByte: "
                + hillLoadedCodeByte + "; replayModeByte: " + replayModeByte);
        if (gamePausedValue == Byte.MAX_VALUE && hillPausedValue == Byte.MAX_VALUE
                && hillLoadedCodeByte == Byte.MAX_VALUE && replayModeValue == Byte.MAX_VALUE) {
            gamePausedValue = gamePausedByte;
            hillPausedValue = hillPausedByte;
            hillLoadedCodeValue = hillLoadedCodeByte;
            replayModeValue = replayModeByte;
            logger.log(Level.FINE, "initial run");
            return;
        }
        checkAndWriteValues(hillLoadedCodeByte, gamePausedByte, hillPausedByte, replayModeByte);
        gamePausedValue = gamePausedByte;
        hillPausedValue = hillPausedByte;
        hillLoadedCodeValue = hillLoadedCodeByte;
        replayModeValue = replayModeByte;
        logger.log(Level.INFO, "new values: gamePausedValue: " + gamePausedValue
                + "; hillPausedValue: " + hillPausedValue + "; hillLoadedCodeValue: "
                + hillLoadedCodeValue + "; replayModeValue: " + replayModeValue);
    }

    private void checkAndWriteValues(byte hillLoadedCodeByte, byte gamePausedByte,
                                     byte hillPausedByte, byte replayModeByte) {
        if (gamePausedValue == (byte) 1 && hillPausedValue == (byte) 0) {
            if (gamePausedByte == (byte) 0 && hillPausedByte == (byte) 0) {
                logger.log(Level.INFO, "Transition menu -> game running (only on second and further hill loading");
                windStrengthValue = (float) dsj2Controller.windStrengthSlider.getValue();
                writeValues();
                return;
            }
        }
        if (gamePausedValue == (byte) 1 && hillPausedValue == GAME_PAUSED_AT_HILL_BYTE_VALUE) {
            if (gamePausedByte == (byte) 0 && hillPausedByte == (byte) 0) {
                logger.log(Level.INFO, "Transition game paused -> game running");
                writeValues();
                return;
            }
        }
        if (hillLoadedCodeValue != hillLoadedCodeByte && hillLoadedCodeByte != 0) {
            logger.log(Level.INFO, "Loaded hill; gamePausedValue: " + gamePausedValue
                    + "; hillPausedValue: " + hillPausedValue);
            try {
                logger.log(Level.INFO, "Waiting for hill to load completely");
                Thread.sleep(DSJ2FXInterfaceHelper.TIMER_DEFAULT_PERIOD);
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Error when trying to sleep");
                e.printStackTrace();
            }
            windStrengthValue = (float) dsj2Controller.windStrengthSlider.getValue();
            writeValues();
            return;
        }
        if (replayModeByte == 'N' || replayModeByte == 'D') {
            logger.log(Level.INFO, "Replay mode");
            writeValues();
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }
}
