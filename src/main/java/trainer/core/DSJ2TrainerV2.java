package trainer.core;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.VerRsrc;
import com.sun.jna.platform.win32.Version;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import trainer.core.dosbox.DosBoxVersion;
import trainer.core.dosbox.DosBoxVersionFactory;
import trainer.exceptions.DSJ2NotLaunchedException;
import trainer.exceptions.DosBoxProcessNotFoundException;
import trainer.exceptions.DosBoxStartedAsAdminException;
import trainer.exceptions.UnknownReasonException;
import trainer.exceptions.UnsupportedDosBoxVersionException;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class DSJ2TrainerV2 {

    public static final long DECREASE_ADDRESS_CONSTANT = 0x00001000L;
    public static final long FLOAT_SIZE = 4L;
    public static final String DOSBOX_EXE = "DOSBOX.EXE";
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
    private static final int PROCESS_VM_READ = 0x0010;
    private static final int PROCESS_VM_WRITE = 0x0020;
    private static final int PROCESS_VM_OPERATION = 0x0008;
    private static final int PROCESS_QUERY_INFORMATION = 0x0400;
    //    private static final int PROCESS_ALL_ACCESS = 0x001F0FFF;
    private static final Kernel32 kernel = Kernel32.INSTANCE;
    private static final Logger logger = Logger.getLogger(DSJ2TrainerV2.class.getName());
    public static final long MAXIMUM_POINTER = 0x7FFEFFFFL;
    static DosBoxVersion dosBoxVersion;

    static {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
    }

    //https://stackoverflow.com/questions/62566741/slow-memory-signature-scanner
//https://stackoverflow.com/questions/53715834/readprocessmemory-across-module-boundaries

    public static WinNT.HANDLE findDosBoxProcess() throws DosBoxProcessNotFoundException,
            UnsupportedDosBoxVersionException, DosBoxStartedAsAdminException, UnknownReasonException {
        logger.log(Level.INFO, "findDosBoxProcess");
        WinNT.HANDLE dosBoxProcess = null;
        WinNT.HANDLE snapshot = kernel.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0));
        Tlhelp32.PROCESSENTRY32.ByReference processEntry = new Tlhelp32.PROCESSENTRY32.ByReference();
        while (kernel.Process32Next(snapshot, processEntry)) {
            if (Native.toString(processEntry.szExeFile).toUpperCase().contains(DOSBOX_EXE)) {
                logger.log(Level.SEVERE, "Found process of DOSBox!");
                dosBoxProcess = openProcess(PROCESS_VM_READ | PROCESS_VM_WRITE
                                | PROCESS_VM_OPERATION | PROCESS_QUERY_INFORMATION,
                        processEntry.th32ProcessID.intValue());
                logger.log(Level.INFO, "DOSBox Process: " + dosBoxProcess);
                break;
            }
        }
        if (dosBoxProcess == null) {
            logger.log(Level.SEVERE, "No DOSBox process found");
            throw new DosBoxProcessNotFoundException();
        }
        return dosBoxProcess;
    }

    public static WinNT.HANDLE openProcess(int permissions, int pid) throws UnsupportedDosBoxVersionException,
            DosBoxStartedAsAdminException, UnknownReasonException {
        logger.log(Level.INFO, "openProcess: permissions: " + permissions + "; pid: " + pid);
        WinNT.HANDLE process = Kernel32.INSTANCE.OpenProcess(permissions, true, pid);
        try {
            Objects.requireNonNull(process);
        } catch (RuntimeException exp) {
            logger.log(Level.SEVERE, "Process cannot be opened: " + exp.getMessage());
            throw new DosBoxStartedAsAdminException();
        }
        try {
            setDosBoxVersionInstance(process);
        } catch (RuntimeException exp) {
            logger.log(Level.SEVERE, "Cannot query process path: " + exp.getMessage());
            throw new UnknownReasonException();
        }
        logger.log(Level.INFO, "openProcess successful");
        return process;
    }

    public static long findDSJ2CommentAddress(WinNT.HANDLE process, long pointer) throws DSJ2NotLaunchedException {
        logger.log(Level.INFO, "findDSJ2commentAddress: process: " + process);
        long dsj2CommentAddressPointer = 0;
        int memorySize = DSJ_V2_STRING.length() * 2;
        long currentPointer = pointer;
        Memory stringMemory = new Memory(memorySize);
        IntByReference read = new IntByReference(0);
        while (currentPointer < MAXIMUM_POINTER) {
            boolean isReadMemorySuccess = Kernel32.INSTANCE.ReadProcessMemory(process,
                    Pointer.createConstant(currentPointer), stringMemory, memorySize, read);
            if (stringMemory.getString(0L).contentEquals(DSJ_V2_STRING)) {
                dsj2CommentAddressPointer = currentPointer;
                logger.log(Level.INFO, "Pointer of DSJ2 Comment address: "
                        + Long.toHexString(dsj2CommentAddressPointer));
                break;
            }
            currentPointer = currentPointer + DECREASE_ADDRESS_CONSTANT;
        }
        if (0L == dsj2CommentAddressPointer) {
            logger.log(Level.SEVERE, "No DSJ2 comment found in memory");
            throw new DSJ2NotLaunchedException();
        }
        return dsj2CommentAddressPointer;
    }

    public static long findDSJ2LoadedAddress(WinNT.HANDLE process, long dsj2CommentAddress,
                                             long versionOffset) throws DSJ2NotLaunchedException {
        logger.log(Level.INFO, "findDSJ2LoadedAddress: process: " + process
                + "; dsj2CommentAddress: " + dsj2CommentAddress);
        long dsj2LoadedAddress = dsj2CommentAddress + versionOffset;
        int memorySize = DSJ_V2_STRING.length() * 2;
        Memory stringMemory = new Memory(memorySize);
        IntByReference read = new IntByReference(0);
        boolean readMemorySuccess = Kernel32.INSTANCE.ReadProcessMemory(process,
                new Pointer(dsj2LoadedAddress), stringMemory, memorySize, read);
        logger.log(Level.FINE, "readMemorySuccess: " + readMemorySuccess);
        if (stringMemory.getString(0L).contentEquals(DSJ_V2_STRING)) {
            logger.log(Level.FINE, "found dsj2LoadedAddress: " + dsj2LoadedAddress);
            return dsj2LoadedAddress;
        }
        throw new DSJ2NotLaunchedException();
    }

    public static boolean checkIfDSJ2PVersion(WinNT.HANDLE process) {
        logger.log(Level.INFO, "checkIfDSJ2PVersion: process: " + process);
        int memorySize = DSJ_V2P_STRING.length() * 2;
        long currentPointer = dosBoxVersion.getDsjV2PPointerP();
        Memory stringMemory = new Memory(memorySize);
        while (currentPointer < MAXIMUM_POINTER) {
            boolean isReadMemorySuccess = Kernel32.INSTANCE.ReadProcessMemory(process,
                    Pointer.createConstant(currentPointer), stringMemory, memorySize, null);
            if (stringMemory.getString(0L).contentEquals(DSJ_V2P_STRING)) {
                logger.log(Level.SEVERE, "This is DSJ2.1P version");
                DSJ2FXInterfaceHelper.isDSJ21PLaunched = true;
                return true;
            }
            currentPointer = currentPointer + DECREASE_ADDRESS_CONSTANT;
        }
        logger.log(Level.SEVERE, "This is not DSJ 2.1P version");
        DSJ2FXInterfaceHelper.isDSJ21PLaunched = false;
        return false;
    }

    static void setDosBoxVersionInstance(WinNT.HANDLE process) throws UnsupportedDosBoxVersionException {
        String processPath = Kernel32Util.QueryFullProcessImageName(process, 0);
        logger.log(Level.INFO, "Path to exe: " + processPath);
        int infoSize =
                Version.INSTANCE.GetFileVersionInfoSize(processPath, null);
        Pointer buffer = Kernel32.INSTANCE.LocalAlloc(WinBase.LMEM_ZEROINIT, infoSize);
        Version.INSTANCE.GetFileVersionInfo(processPath, 0, infoSize, buffer);
        IntByReference outputSize = new IntByReference();
        PointerByReference pointer = new PointerByReference();
        Version.INSTANCE.VerQueryValue(buffer, "\\", pointer, outputSize);
        VerRsrc.VS_FIXEDFILEINFO fileInfoStructure = new VerRsrc.VS_FIXEDFILEINFO(pointer.getValue());
        logger.log(Level.INFO, "DOSBox version: " + fileInfoStructure.getFileVersionMinor());
        dosBoxVersion = DosBoxVersionFactory.getDosBoxVersion(fileInfoStructure.getFileVersionMinor());
        logger.log(Level.INFO, "dosBoxVersion: " + dosBoxVersion);
    }

    static float readSingleValue(WinNT.HANDLE process, long address, long offset) {
        logger.log(Level.INFO, "readSingleValue: process: " + process
                + "; address: " + address + "; offset: " + offset);
        Memory memory = new Memory(FLOAT_SIZE);
        boolean readMemorySuccess = Kernel32.INSTANCE.ReadProcessMemory(process,
                new Pointer(address + offset), memory, 4, null);
        float floatValue = memory.getFloat(0L);
        logger.log(Level.FINE, "readMemorySuccess: " + readMemorySuccess + "; floatValue: " + floatValue);
        return floatValue;
    }

    public static byte readSingleByteValue(WinNT.HANDLE process, long address, long offset) {
        logger.log(Level.INFO, "readSingleByteValue: process: " + process
                + "; address: " + address + "; offset: " + offset);
        Memory memory = new Memory(1L);
        boolean readMemorySuccess = Kernel32.INSTANCE.ReadProcessMemory(process,
                new Pointer(address + offset), memory, 1, null);
        byte byteValue = memory.getByte(0L);
        logger.log(Level.FINE, "readMemorySuccess: " + readMemorySuccess + "; byteValue: " + byteValue);
        return byteValue;
    }

    public static void writeSingleFloatValue(WinNT.HANDLE process, long address, long offset, float value) {
        logger.log(Level.INFO, "readSingleByteValue: process: " + process
                + "; address: " + address + "; offset: " + offset + "; value: " + value);
        Memory memory = new Memory(FLOAT_SIZE);
        memory.setFloat(0, value);
        boolean readMemorySuccess = Kernel32.INSTANCE.WriteProcessMemory(process,
                new Pointer(address + offset), memory, 4, null);
        logger.log(Level.FINE, "readMemorySuccess: " + readMemorySuccess);
    }

}
