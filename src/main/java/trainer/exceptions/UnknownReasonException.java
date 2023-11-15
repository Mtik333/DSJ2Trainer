package trainer.exceptions;

import trainer.core.DSJ2Controller;

public class UnknownReasonException extends Exception {

    private static final long serialVersionUID = 4946612373552788938L;

    public UnknownReasonException() {
        super(DSJ2Controller.resourceBundle.getString("unknownException"));
    }
}
