package trainer.exceptions;

import trainer.core.DSJ2Controller;

public class DSJ2UnloadedException extends Exception {

    private static final long serialVersionUID = -5489740837709614162L;

    public DSJ2UnloadedException() {
        super(DSJ2Controller.resourceBundle.getString("dsj2Unloaded"));
    }
}
