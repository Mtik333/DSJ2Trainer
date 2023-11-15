package trainer.exceptions;

import trainer.core.DSJ2Controller;

public class DosBoxProcessNotFoundException extends Exception {

    private static final long serialVersionUID = 4946612373552784938L;

    public DosBoxProcessNotFoundException() {
        super(DSJ2Controller.resourceBundle.getString("noDOSBox"));
    }
}
