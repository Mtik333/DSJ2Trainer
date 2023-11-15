package trainer.core.dosbox;

public class DosBox74 extends DosBoxVersion {
    //start from 0x00400000 hm?
    public static final long DSJ_2_LOADED_IN_DOS_BOX_OFFSET = 0x2257L;
    public static final long DSJ_21_P_LOADED_IN_DOS_BOX_OFFSET = 0x52CBL;
    public static final long INITIAL_POINTER = 0x0040074EL;
    public static final long INITIAL_POINTER_P = 0x004005EEL;
    public static final long DSJ_V_2_P_POINTER_P = 0x00400E40L;

    public DosBox74() {
        dsj2LoadedInDosBoxOffset = DSJ_2_LOADED_IN_DOS_BOX_OFFSET;
        dsj21PLoadedInDosBoxOffset = DSJ_21_P_LOADED_IN_DOS_BOX_OFFSET;
        initialPointer = INITIAL_POINTER;
        initialPointerP = INITIAL_POINTER_P;
        dsjV2PPointerP = DSJ_V_2_P_POINTER_P;
    }

    @Override
    public String toString() {
        return "0.74";
    }
}
