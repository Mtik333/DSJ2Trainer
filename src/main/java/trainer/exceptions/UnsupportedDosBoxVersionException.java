package trainer.exceptions;

import trainer.core.DSJ2Controller;

public class UnsupportedDosBoxVersionException extends Exception {

    private static final long serialVersionUID = -5489740837709614162L;

    public UnsupportedDosBoxVersionException(int version) {
        super(DSJ2Controller.resourceBundle.getString("unsupportedDOSBox") + version);
    }
}
