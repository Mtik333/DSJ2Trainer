package trainer.core.dosbox;

import trainer.core.DSJ2Controller;
import trainer.core.DSJ2FXInterfaceHelper;
import trainer.exceptions.UnsupportedDosBoxVersionException;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class DosBoxVersionFactory {

    static final int VER_73 = 73;
    static final int VER_74 = 74;
    private static final Logger logger = Logger.getLogger(DSJ2Controller.class.getName());

    static {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
    }

    public static DosBoxVersion getDosBoxVersion(int version) throws UnsupportedDosBoxVersionException {
        logger.log(Level.FINE, "Entering getDosBoxVersion with version: " + version);
        if (version == VER_73) {
            return new DosBox73();
        }
        if (version == VER_74) {
            return new DosBox74();
        }
        throw new UnsupportedDosBoxVersionException(version);
    }
}
