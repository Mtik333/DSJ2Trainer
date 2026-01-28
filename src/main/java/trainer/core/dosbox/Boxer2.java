package trainer.core.dosbox;

/**
 * Boxer-2 DOS emulator for macOS.
 * Boxer-2 is based on DOSBox 0.74, so memory offsets are similar.
 *
 * Note: Memory addresses on macOS differ significantly because:
 * - Boxer is a 64-bit Cocoa application
 * - The emulated DOS memory is allocated dynamically
 * - ASLR (Address Space Layout Randomization) is enabled
 *
 * We need to scan a wider memory range to find the DSJ2 signature.
 */
public class Boxer2 extends DosBoxVersion {

    // These offsets are relative within the DSJ2 game memory - same as DOSBox 0.74
    public static final long DSJ_2_LOADED_IN_DOS_BOX_OFFSET = 0x2257L;
    public static final long DSJ_21_P_LOADED_IN_DOS_BOX_OFFSET = 0x52CBL;

    // On macOS with Boxer, we need to scan from a low address
    // The emulated DOS memory (conventional + extended) is typically
    // allocated as a large contiguous block somewhere in the process space
    // Start from 0x1000 and scan upward
    public static final long INITIAL_POINTER = 0x00001000L;
    public static final long INITIAL_POINTER_P = 0x00001000L;
    public static final long DSJ_V_2_P_POINTER_P = 0x00001000L;

    public Boxer2() {
        dsj2LoadedInDosBoxOffset = DSJ_2_LOADED_IN_DOS_BOX_OFFSET;
        dsj21PLoadedInDosBoxOffset = DSJ_21_P_LOADED_IN_DOS_BOX_OFFSET;
        initialPointer = INITIAL_POINTER;
        initialPointerP = INITIAL_POINTER_P;
        dsjV2PPointerP = DSJ_V_2_P_POINTER_P;
    }

    @Override
    public String toString() {
        return "Boxer-2 (macOS)";
    }
}

