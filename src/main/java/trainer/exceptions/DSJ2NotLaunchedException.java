package trainer.exceptions;

import trainer.core.DSJ2Controller;

public class DSJ2NotLaunchedException extends Exception {

    private static final long serialVersionUID = 5035201146196350808L;

    public DSJ2NotLaunchedException() {
        super(DSJ2Controller.resourceBundle.getString("noDSJ2"));
    }
}
