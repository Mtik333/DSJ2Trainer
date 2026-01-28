package trainer.core.dosbox;

import trainer.core.DSJ2Controller;
import trainer.core.DSJ2FXInterfaceHelper;
import trainer.exceptions.UnsupportedDosBoxVersionException;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class DosBoxVersionFactory {

    static final int VER_73 = 73;
    static final int VER_74 = 74;
    static final int VER_BOXER = 74; // Boxer-2 is based on DOSBox 0.74
    private static final Logger logger = Logger.getLogger(DSJ2Controller.class.getName());

    private static final boolean IS_MACOS = System.getProperty("os.name").toLowerCase().contains("mac");

    static {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
    }

    public static DosBoxVersion getDosBoxVersion(int version) throws UnsupportedDosBoxVersionException {
        logger.log(Level.FINE, "Entering getDosBoxVersion with version: " + version);

        // On macOS, use Boxer2 which is based on DOSBox 0.74
        if (IS_MACOS) {
            logger.log(Level.INFO, "Running on macOS, using Boxer2 version");
            return new Boxer2();
        }

        if (version == VER_73) {
            return new DosBox73();
        }
        if (version == VER_74) {
            return new DosBox74();
        }
        throw new UnsupportedDosBoxVersionException(version);
    }

    /**
     * Get a DosBoxVersion specifically for Boxer-2 on macOS
     */
    public static DosBoxVersion getBoxer2Version() {
        return new Boxer2();
    }
}
