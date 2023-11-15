package trainer.exceptions;

import trainer.core.DSJ2Controller;

public class DosBoxStartedAsAdminException extends Exception {

    private static final long serialVersionUID = 4946612373552784938L;

    public DosBoxStartedAsAdminException() {
        super(DSJ2Controller.resourceBundle.getString("adminDOSBox"));
    }

}
