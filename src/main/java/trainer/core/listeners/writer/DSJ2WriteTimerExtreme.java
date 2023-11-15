package trainer.core.listeners.writer;

import com.sun.jna.platform.win32.WinNT;
import org.jnativehook.keyboard.NativeKeyEvent;
import trainer.core.DSJ2Controller;
import trainer.core.DSJ2FXInterfaceHelper;

import java.util.regex.Pattern;

public class DSJ2WriteTimerExtreme extends AbstractDSJ2WriteTimer {

    static {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
    }

    public DSJ2WriteTimerExtreme(WinNT.HANDLE dosBoxProcess, long dsj2CommentAddressValue,
                                 long dsj2LoadedAddressValue, DSJ2Controller controller) {
        super(dosBoxProcess, dsj2CommentAddressValue, dsj2LoadedAddressValue, controller);
        windStrengthValue = Float.parseFloat(Pattern.compile(",")
                .matcher(controller.windStrengthField.getText()).replaceAll("."));
    }

    private void checkAndWriteValues() {
        writeValues();
    }

    @Override
    public void run() {
        checkIfProcessShutdown();
        checkAndWriteValues();
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }
}
