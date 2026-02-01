package trainer.core.platform;

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
import trainer.core.DSJ2FXInterfaceHelper;
import trainer.core.dosbox.DosBoxVersionFactory;
import trainer.exceptions.DosBoxProcessNotFoundException;
import trainer.exceptions.DosBoxStartedAsAdminException;
import trainer.exceptions.UnknownReasonException;
import trainer.exceptions.UnsupportedDosBoxVersionException;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Windows implementation of memory access using JNA and Win32 API.
 */
public class WindowsMemoryAccess implements PlatformMemoryAccess {

    private static final int PROCESS_VM_READ = 0x0010;
    private static final int PROCESS_VM_WRITE = 0x0020;
    private static final int PROCESS_VM_OPERATION = 0x0008;
    private static final int PROCESS_QUERY_INFORMATION = 0x0400;
    private static final String DOSBOX_EXE = "DOSBOX.EXE";
    private static final Kernel32 kernel = Kernel32.INSTANCE;
    private static final Logger logger = Logger.getLogger(WindowsMemoryAccess.class.getName());

    static {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
    }

    /**
     * Windows-specific process handle wrapper
     */
    public static class WindowsProcessHandle implements ProcessHandle {
        private final WinNT.HANDLE handle;

        public WindowsProcessHandle(WinNT.HANDLE handle) {
            this.handle = handle;
        }

        public WinNT.HANDLE getHandle() {
            return handle;
        }

        @Override
        public boolean isValid() {
            return handle != null;
        }

        @Override
        public void close() {
            if (handle != null) {
                kernel.CloseHandle(handle);
            }
        }
    }

    @Override
    public ProcessHandle findEmulatorProcess() throws DosBoxProcessNotFoundException,
            UnsupportedDosBoxVersionException, DosBoxStartedAsAdminException, UnknownReasonException {
        logger.log(Level.INFO, "findEmulatorProcess (Windows)");
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
        kernel.CloseHandle(snapshot);
        if (dosBoxProcess == null) {
            logger.log(Level.SEVERE, "No DOSBox process found");
            throw new DosBoxProcessNotFoundException();
        }
        return new WindowsProcessHandle(dosBoxProcess);
    }

    private WinNT.HANDLE openProcess(int permissions, int pid) throws DosBoxStartedAsAdminException {
        logger.log(Level.INFO, "openProcess: permissions: " + permissions + "; pid: " + pid);
        WinNT.HANDLE process = kernel.OpenProcess(permissions, true, pid);
        try {
            Objects.requireNonNull(process);
        } catch (RuntimeException exp) {
            logger.log(Level.SEVERE, "Process cannot be opened: " + exp.getMessage());
            throw new DosBoxStartedAsAdminException();
        }
        return process;
    }

    @Override
    public float readFloat(ProcessHandle process, long address) {
        WinNT.HANDLE handle = ((WindowsProcessHandle) process).getHandle();
        Memory memory = new Memory(4L);
        kernel.ReadProcessMemory(handle, new Pointer(address), memory, 4, null);
        return memory.getFloat(0L);
    }

    @Override
    public byte readByte(ProcessHandle process, long address) {
        WinNT.HANDLE handle = ((WindowsProcessHandle) process).getHandle();
        Memory memory = new Memory(1L);
        kernel.ReadProcessMemory(handle, new Pointer(address), memory, 1, null);
        return memory.getByte(0L);
    }

    @Override
    public String readString(ProcessHandle process, long address, int length) {
        WinNT.HANDLE handle = ((WindowsProcessHandle) process).getHandle();
        Memory memory = new Memory(length);
        kernel.ReadProcessMemory(handle, new Pointer(address), memory, length, null);
        return memory.getString(0L);
    }

    @Override
    public void writeFloat(ProcessHandle process, long address, float value) {
        WinNT.HANDLE handle = ((WindowsProcessHandle) process).getHandle();
        Memory memory = new Memory(4L);
        memory.setFloat(0, value);
        kernel.WriteProcessMemory(handle, new Pointer(address), memory, 4, null);
    }

    @Override
    public int getEmulatorVersion(ProcessHandle process) throws UnsupportedDosBoxVersionException {
        WinNT.HANDLE handle = ((WindowsProcessHandle) process).getHandle();
        String processPath = Kernel32Util.QueryFullProcessImageName(handle, 0);
        logger.log(Level.INFO, "Path to exe: " + processPath);
        int infoSize = Version.INSTANCE.GetFileVersionInfoSize(processPath, null);
        Pointer buffer = kernel.LocalAlloc(WinBase.LMEM_ZEROINIT, infoSize);
        Version.INSTANCE.GetFileVersionInfo(processPath, 0, infoSize, buffer);
        IntByReference outputSize = new IntByReference();
        PointerByReference pointer = new PointerByReference();
        Version.INSTANCE.VerQueryValue(buffer, "\\", pointer, outputSize);
        VerRsrc.VS_FIXEDFILEINFO fileInfoStructure = new VerRsrc.VS_FIXEDFILEINFO(pointer.getValue());
        int version = fileInfoStructure.getFileVersionMinor();
        logger.log(Level.INFO, "DOSBox version: " + version);
        kernel.LocalFree(buffer);
        return version;
    }

    @Override
    public String getEmulatorProcessName() {
        return DOSBOX_EXE;
    }
}

