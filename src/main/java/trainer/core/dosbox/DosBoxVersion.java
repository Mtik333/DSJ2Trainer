package trainer.core.dosbox;

public abstract class DosBoxVersion {

    long dsj2LoadedInDosBoxOffset;
    long dsj21PLoadedInDosBoxOffset;
    long initialPointer;
    long initialPointerP;
    long dsjV2PPointerP;

    public long getDsj2LoadedInDosBoxOffset() {
        return dsj2LoadedInDosBoxOffset;
    }

    public long getDsj21PLoadedInDosBoxOffset() {
        return dsj21PLoadedInDosBoxOffset;
    }

    public long getInitialPointer() {
        return initialPointer;
    }

    public long getInitialPointerP() {
        return initialPointerP;
    }

    public long getDsjV2PPointerP() {
        return dsjV2PPointerP;
    }
}
