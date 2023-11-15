package trainer.exceptions;

import trainer.core.DSJ2Controller;

public class DSJ2DefaultValuesLoadingException extends Exception {

    private static final long serialVersionUID = 4946612373552784938L;

    public DSJ2DefaultValuesLoadingException() {
        super(DSJ2Controller.resourceBundle.getString("loadingValuesError"));
    }
}
