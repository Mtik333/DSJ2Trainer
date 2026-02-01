package trainer.core;

import trainer.core.dosbox.DosBoxVersion;
import trainer.core.dosbox.DosBoxVersionFactory;
import trainer.core.platform.PlatformMemoryAccess;
import trainer.core.platform.PlatformMemoryAccess.ProcessHandle;
import trainer.exceptions.DSJ2NotLaunchedException;
import trainer.exceptions.DosBoxProcessNotFoundException;
import trainer.exceptions.DosBoxStartedAsAdminException;
import trainer.exceptions.UnknownReasonException;
import trainer.exceptions.UnsupportedDosBoxVersionException;

import java.util.logging.Level;
import java.util.logging.Logger;


public final class DSJ2TrainerV2 {

    public static final long DECREASE_ADDRESS_CONSTANT = 0x00001000L;
    public static final long FLOAT_SIZE = 4L;
    public static final long WIND_STRENGTH_HORIZONTAL_PART_OFFSET = 0x82L;
    public static final long WIND_STRENGTH_VERTICAL_PART_OFFSET = 0x86L;
    public static final long WIND_STRENGTH_VARIANCE_OFFSET = 0x8AL;
    public static final long IDEAL_WIND_VALUE_OFFSET = 0x92L;
    public static final long ROUGH_WIND_VALUE_OFFSET = 0x96L;
    public static final long WIND_DIRECTION_VARIANCE_OFFSET = 0x9AL;
    public static final long REPLAY_LOADED_OFFSET = -0xB5DL;
    public static final long HILL_CODE_OFFSET = 0xEL;
    public static final long PAUSE_MODE_OFFSET = 0x10L;
    public static final long JUMPING_PAUSED_OFFSET = 0x11L;
    public static final String DSJ_V2_STRING = "//DSJ_v2.00//";
    public static final String DSJ_V2P_STRING = "//DSJ_v2.1P//";
    static final long JUMPER_SPEED_OFFSET = 0x616L;
    static final long JUMPER_LIFT_OFFSET = 0x61AL;
    private static final Logger logger = Logger.getLogger(DSJ2TrainerV2.class.getName());

    // Maximum pointer depends on platform
    // Windows 32-bit DOSBox: 0x7FFEFFFF (2GB limit)
    // macOS 64-bit: scan up to 0x7FFFFFFFF (higher limit for emulated memory)
    private static final boolean IS_MACOS = System.getProperty("os.name").toLowerCase().contains("mac");
    public static final long MAXIMUM_POINTER = IS_MACOS ? 0x7FFFFFFFFL : 0x7FFEFFFFL;

    public static DosBoxVersion dosBoxVersion;
    private static PlatformMemoryAccess platformAccess;
    private static ProcessHandle currentProcess;

    static {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
        platformAccess = PlatformMemoryAccess.getInstance();
    }

    /**
     * Find the DOSBox/Boxer emulator process
     */
    public static ProcessHandle findDosBoxProcess() throws DosBoxProcessNotFoundException,
            UnsupportedDosBoxVersionException, DosBoxStartedAsAdminException, UnknownReasonException {
        logger.log(Level.INFO, "findDosBoxProcess");
        ProcessHandle process = platformAccess.findEmulatorProcess();
        currentProcess = process;
        setDosBoxVersionInstance(process);
        return process;
    }

    /**
     * Find the DSJ2 comment address in memory
     */
    public static long findDSJ2CommentAddress(ProcessHandle process, long pointer) throws DSJ2NotLaunchedException {
        logger.log(Level.INFO, "findDSJ2commentAddress: process: " + process + ", starting from: 0x" + Long.toHexString(pointer));

        // Use the platform-specific string search which is more efficient
        long dsj2CommentAddressPointer = platformAccess.findStringInMemory(
                process, DSJ_V2_STRING, pointer, MAXIMUM_POINTER);

        if (dsj2CommentAddressPointer == -1) {
            logger.log(Level.SEVERE, "No DSJ2 comment found in memory");
            throw new DSJ2NotLaunchedException();
        }

        logger.log(Level.INFO, "Pointer of DSJ2 Comment address: 0x" + Long.toHexString(dsj2CommentAddressPointer));
        return dsj2CommentAddressPointer;
    }

    /**
     * Find the DSJ2 loaded address in memory.
     * This verifies the game is still loaded by checking if the signature exists at the expected offset.
     *
     * On Windows with DOSBox, this looks for the string at comment + offset to verify.
     * On macOS, since we found the string via scanning, we verify by re-reading at the found address.
     */
    public static long findDSJ2LoadedAddress(ProcessHandle process, long dsj2CommentAddress,
                                             long versionOffset) throws DSJ2NotLaunchedException {
        logger.log(Level.INFO, "findDSJ2LoadedAddress: process: " + process
                + "; dsj2CommentAddress: 0x" + Long.toHexString(dsj2CommentAddress)
                + "; versionOffset: 0x" + Long.toHexString(versionOffset));

        int memorySize = DSJ_V2_STRING.length() * 2;

        // First try: verify at the found comment address directly (for macOS scanning approach)
        String stringValueAtComment = platformAccess.readString(process, dsj2CommentAddress, memorySize);
        if (stringValueAtComment.contentEquals(DSJ_V2_STRING)) {
            logger.log(Level.INFO, "Verified DSJ2 at comment address: 0x" + Long.toHexString(dsj2CommentAddress));
            return dsj2CommentAddress;
        }

        // Second try: check at comment + offset (original DOSBox approach)
        long dsj2LoadedAddress = dsj2CommentAddress + versionOffset;
        String stringValueAtOffset = platformAccess.readString(process, dsj2LoadedAddress, memorySize);
        if (stringValueAtOffset.contentEquals(DSJ_V2_STRING)) {
            logger.log(Level.INFO, "Verified DSJ2 at offset address: 0x" + Long.toHexString(dsj2LoadedAddress));
            return dsj2LoadedAddress;
        }

        logger.log(Level.SEVERE, "Could not verify DSJ2 at either address. " +
                "At comment: '" + stringValueAtComment + "', at offset: '" + stringValueAtOffset + "'");
        throw new DSJ2NotLaunchedException();
    }

    /**
     * Check if the running game is DSJ 2.1P version
     */
    public static boolean checkIfDSJ2PVersion(ProcessHandle process) {
        logger.log(Level.INFO, "checkIfDSJ2PVersion: process: " + process);

        long foundAddress = platformAccess.findStringInMemory(
                process, DSJ_V2P_STRING, dosBoxVersion.getDsjV2PPointerP(), MAXIMUM_POINTER);

        if (foundAddress != -1) {
            logger.log(Level.SEVERE, "This is DSJ2.1P version");
            DSJ2FXInterfaceHelper.isDSJ21PLaunched = true;
            return true;
        }

        logger.log(Level.SEVERE, "This is not DSJ 2.1P version");
        DSJ2FXInterfaceHelper.isDSJ21PLaunched = false;
        return false;
    }

    static void setDosBoxVersionInstance(ProcessHandle process) throws UnsupportedDosBoxVersionException {
        int version = platformAccess.getEmulatorVersion(process);
        logger.log(Level.INFO, "Emulator version: " + version);
        dosBoxVersion = DosBoxVersionFactory.getDosBoxVersion(version);
        logger.log(Level.INFO, "dosBoxVersion: " + dosBoxVersion);
    }

    public static float readSingleValue(ProcessHandle process, long address, long offset) {
        logger.log(Level.INFO, "readSingleValue: process: " + process
                + "; address: " + address + "; offset: " + offset);
        float floatValue = platformAccess.readFloat(process, address + offset);
        logger.log(Level.FINE, "floatValue: " + floatValue);
        return floatValue;
    }

    public static byte readSingleByteValue(ProcessHandle process, long address, long offset) {
        logger.log(Level.INFO, "readSingleByteValue: process: " + process
                + "; address: " + address + "; offset: " + offset);
        byte byteValue = platformAccess.readByte(process, address + offset);
        logger.log(Level.FINE, "byteValue: " + byteValue);
        return byteValue;
    }

    public static void writeSingleFloatValue(ProcessHandle process, long address, long offset, float value) {
        logger.log(Level.INFO, "writeSingleFloatValue: process: " + process
                + "; address: " + address + "; offset: " + offset + "; value: " + value);
        platformAccess.writeFloat(process, address + offset, value);
    }

    /**
     * Check if a string is still present at the given address
     */
    public static boolean isStringAtAddress(ProcessHandle process, long address, String expected) {
        int memorySize = expected.length() * 2;
        String stringValue = platformAccess.readString(process, address, memorySize);
        return stringValue.contentEquals(expected);
    }

    /**
     * Get the platform memory access instance
     */
    public static PlatformMemoryAccess getPlatformAccess() {
        return platformAccess;
    }
}
