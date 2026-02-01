package trainer.core.platform;

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import trainer.core.DSJ2FXInterfaceHelper;
import trainer.exceptions.DosBoxProcessNotFoundException;
import trainer.exceptions.DosBoxStartedAsAdminException;
import trainer.exceptions.UnknownReasonException;
import trainer.exceptions.UnsupportedDosBoxVersionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * macOS implementation of memory access using mach kernel APIs.
 * This implementation supports Boxer-2 DOS emulator for macOS.
 */
public class MacOSMemoryAccess implements PlatformMemoryAccess {

    private static final String BOXER_PROCESS_NAME = "Boxer";
    private static final String DOSBOX_PROCESS_NAME = "DOSBox";
    private static final Logger logger = Logger.getLogger(MacOSMemoryAccess.class.getName());

    // Boxer-2 is based on DOSBox 0.74, so we use similar offsets
    public static final int BOXER_VERSION = 74;

    static {
        logger.setLevel(DSJ2FXInterfaceHelper.logLevel);
    }

    /**
     * macOS-specific process handle wrapper using mach task port
     */
    public static class MacOSProcessHandle implements ProcessHandle {
        private final int taskPort;
        private final int pid;
        private boolean valid;

        public MacOSProcessHandle(int taskPort, int pid) {
            this.taskPort = taskPort;
            this.pid = pid;
            this.valid = taskPort != 0;
        }

        public int getTaskPort() {
            return taskPort;
        }

        public int getPid() {
            return pid;
        }

        @Override
        public boolean isValid() {
            return valid;
        }

        @Override
        public void close() {
            if (valid && taskPort != 0) {
                MachKernel.INSTANCE.mach_port_deallocate(
                    MachKernel.INSTANCE.mach_task_self(), taskPort);
                valid = false;
            }
        }
    }

    @Override
    public ProcessHandle findEmulatorProcess() throws DosBoxProcessNotFoundException,
            UnsupportedDosBoxVersionException, DosBoxStartedAsAdminException, UnknownReasonException {
        logger.log(Level.INFO, "findEmulatorProcess (macOS)");

        // First try to find Boxer, then DOSBox
        int pid = findProcessByName(BOXER_PROCESS_NAME);
        if (pid == -1) {
            pid = findProcessByName(DOSBOX_PROCESS_NAME);
        }

        if (pid == -1) {
            logger.log(Level.SEVERE, "No Boxer or DOSBox process found");
            throw new DosBoxProcessNotFoundException();
        }

        logger.log(Level.INFO, "Found emulator process with PID: " + pid);

        // Get the mach task port for the process
        IntByReference taskPortRef = new IntByReference();
        int result = MachKernel.INSTANCE.task_for_pid(
            MachKernel.INSTANCE.mach_task_self(), pid, taskPortRef);

        if (result != MachKernel.KERN_SUCCESS) {
            logger.log(Level.SEVERE, "Failed to get task port for PID " + pid +
                ". Error code: " + result +
                ". Make sure the app is signed with com.apple.security.cs.debugger entitlement or run with sudo.");
            throw new DosBoxStartedAsAdminException();
        }

        int taskPort = taskPortRef.getValue();
        logger.log(Level.INFO, "Got task port: " + taskPort);

        return new MacOSProcessHandle(taskPort, pid);
    }

    /**
     * Find a process by name using pgrep
     */
    private int findProcessByName(String processName) {
        try {
            ProcessBuilder pb = new ProcessBuilder("pgrep", "-x", processName);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null && !line.isEmpty()) {
                    return Integer.parseInt(line.trim());
                }
            }

            // Try with case-insensitive search
            pb = new ProcessBuilder("pgrep", "-i", processName);
            process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null && !line.isEmpty()) {
                    return Integer.parseInt(line.trim());
                }
            }
        } catch (IOException | NumberFormatException e) {
            logger.log(Level.WARNING, "Error finding process: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public float readFloat(ProcessHandle process, long address) {
        MacOSProcessHandle macHandle = (MacOSProcessHandle) process;
        Memory buffer = new Memory(4);
        LongByReference outSize = new LongByReference();

        int result = MachKernel.INSTANCE.mach_vm_read_overwrite(
            macHandle.getTaskPort(), address, 4, buffer, outSize);

        if (result != MachKernel.KERN_SUCCESS) {
            // Don't log every failed read during scanning
            return 0f;
        }

        return buffer.getFloat(0);
    }

    @Override
    public byte readByte(ProcessHandle process, long address) {
        MacOSProcessHandle macHandle = (MacOSProcessHandle) process;
        Memory buffer = new Memory(1);
        LongByReference outSize = new LongByReference();

        int result = MachKernel.INSTANCE.mach_vm_read_overwrite(
            macHandle.getTaskPort(), address, 1, buffer, outSize);

        if (result != MachKernel.KERN_SUCCESS) {
            return 0;
        }

        return buffer.getByte(0);
    }

    @Override
    public String readString(ProcessHandle process, long address, int length) {
        MacOSProcessHandle macHandle = (MacOSProcessHandle) process;
        Memory buffer = new Memory(length);
        LongByReference outSize = new LongByReference();

        int result = MachKernel.INSTANCE.mach_vm_read_overwrite(
            macHandle.getTaskPort(), address, length, buffer, outSize);

        if (result != MachKernel.KERN_SUCCESS) {
            return "";
        }

        return buffer.getString(0);
    }

    @Override
    public void writeFloat(ProcessHandle process, long address, float value) {
        MacOSProcessHandle macHandle = (MacOSProcessHandle) process;
        int taskPort = macHandle.getTaskPort();

        // First, try to make the memory writable
        // We protect a page-aligned region (4096 bytes is typical page size on macOS)
        long pageSize = 4096;
        long pageStart = (address / pageSize) * pageSize;

        int protectResult = MachKernel.INSTANCE.mach_vm_protect(
            taskPort, pageStart, pageSize, 0,
            MachKernel.VM_PROT_READ | MachKernel.VM_PROT_WRITE);

        if (protectResult != MachKernel.KERN_SUCCESS) {
            logger.log(Level.FINE, "mach_vm_protect failed for address 0x" +
                Long.toHexString(address) + ", error: " + protectResult +
                " (this may be normal, trying write anyway)");
        }

        // Now try to write
        Memory buffer = new Memory(4);
        buffer.setFloat(0, value);

        int result = MachKernel.INSTANCE.mach_vm_write(
            taskPort, address, buffer, 4);

        if (result != MachKernel.KERN_SUCCESS) {
            logger.log(Level.WARNING, "mach_vm_write failed at address 0x" +
                Long.toHexString(address) + ", value: " + value + ", error: " + result);
        } else {
            logger.log(Level.FINE, "Successfully wrote " + value + " to address 0x" +
                Long.toHexString(address));
        }
    }

    @Override
    public int getEmulatorVersion(ProcessHandle process) throws UnsupportedDosBoxVersionException {
        // Boxer-2 is based on DOSBox 0.74
        // For now, we return 74 as the version since Boxer-2 is compatible
        logger.log(Level.INFO, "Boxer/DOSBox on macOS - using version 74 (Boxer-2 compatible)");
        return BOXER_VERSION;
    }

    @Override
    public String getEmulatorProcessName() {
        return BOXER_PROCESS_NAME;
    }

    /**
     * Find all occurrences of a string in memory and return them as a list
     */
    public java.util.List<Long> findAllStringsInMemory(ProcessHandle process, String searchString,
            long startAddress, long maxAddress) {
        java.util.List<Long> results = new java.util.ArrayList<>();
        MacOSProcessHandle macHandle = (MacOSProcessHandle) process;
        int taskPort = macHandle.getTaskPort();

        byte[] searchBytes = searchString.getBytes();
        int searchLen = searchBytes.length;

        LongByReference addressRef = new LongByReference(startAddress);
        LongByReference sizeRef = new LongByReference(0);
        IntByReference infoCount = new IntByReference(9);
        IntByReference objectName = new IntByReference(0);
        Memory infoBuffer = new Memory(36);

        logger.log(Level.INFO, "Searching for ALL occurrences of string: " + searchString);

        while (addressRef.getValue() < maxAddress) {
            int result = MachKernel.INSTANCE.mach_vm_region(
                taskPort, addressRef, sizeRef,
                MachKernel.VM_REGION_BASIC_INFO_64,
                infoBuffer, infoCount, objectName);

            if (result != MachKernel.KERN_SUCCESS) {
                break;
            }

            long regionStart = addressRef.getValue();
            long regionSize = sizeRef.getValue();

            if (regionSize > 256 * 1024 * 1024) {
                addressRef.setValue(regionStart + regionSize);
                continue;
            }

            long chunkSize = Math.min(regionSize, 64 * 1024);
            Memory buffer = new Memory(chunkSize + searchLen);

            for (long offset = 0; offset < regionSize; offset += chunkSize) {
                long readAddr = regionStart + offset;
                long readSize = Math.min(chunkSize + searchLen - 1, regionSize - offset);

                LongByReference outSize = new LongByReference();
                int readResult = MachKernel.INSTANCE.mach_vm_read_overwrite(
                    taskPort, readAddr, readSize, buffer, outSize);

                if (readResult == MachKernel.KERN_SUCCESS && outSize.getValue() > 0) {
                    byte[] data = buffer.getByteArray(0, (int) outSize.getValue());
                    int searchStart = 0;
                    int foundIndex;
                    while ((foundIndex = indexOf(data, searchBytes, searchStart)) >= 0) {
                        long foundAddress = readAddr + foundIndex;
                        results.add(foundAddress);
                        logger.log(Level.INFO, "Found occurrence at 0x" + Long.toHexString(foundAddress));
                        searchStart = foundIndex + 1;
                    }
                }
            }

            addressRef.setValue(regionStart + regionSize);
            infoCount.setValue(9);
        }

        logger.log(Level.INFO, "Found " + results.size() + " total occurrences");
        return results;
    }

    /**
     * Scan memory regions to find a string pattern.
     * This is more efficient than linear scanning on macOS because we only
     * scan mapped memory regions.
     *
     * @param process The process handle
     * @param searchString The string to search for
     * @param startAddress Starting address for the search
     * @param maxAddress Maximum address to search up to
     * @return The address where the string was found, or -1 if not found
     */
    public long findStringInMemory(ProcessHandle process, String searchString, long startAddress, long maxAddress) {
        MacOSProcessHandle macHandle = (MacOSProcessHandle) process;
        int taskPort = macHandle.getTaskPort();

        byte[] searchBytes = searchString.getBytes();
        int searchLen = searchBytes.length;

        LongByReference addressRef = new LongByReference(startAddress);
        LongByReference sizeRef = new LongByReference(0);
        IntByReference infoCount = new IntByReference(9); // VM_REGION_BASIC_INFO_COUNT_64
        IntByReference objectName = new IntByReference(0);

        // Buffer for region info (we don't actually need the contents, just the region bounds)
        Memory infoBuffer = new Memory(36); // sizeof(vm_region_basic_info_data_64_t)

        logger.log(Level.INFO, "Starting memory scan from 0x" + Long.toHexString(startAddress) +
                " to 0x" + Long.toHexString(maxAddress) + " for string: " + searchString);

        int regionsScanned = 0;
        long bytesScanned = 0;

        // Collect all matches first
        java.util.List<Long> allMatches = new java.util.ArrayList<>();

        while (addressRef.getValue() < maxAddress) {
            // Get the next memory region
            int result = MachKernel.INSTANCE.mach_vm_region(
                taskPort, addressRef, sizeRef,
                MachKernel.VM_REGION_BASIC_INFO_64,
                infoBuffer, infoCount, objectName);

            if (result != MachKernel.KERN_SUCCESS) {
                // No more regions
                break;
            }

            long regionStart = addressRef.getValue();
            long regionSize = sizeRef.getValue();

            // Skip very large regions (likely mapped files or shared memory)
            if (regionSize > 256 * 1024 * 1024) { // Skip regions > 256MB
                addressRef.setValue(regionStart + regionSize);
                continue;
            }

            regionsScanned++;

            // Scan this region in chunks
            long chunkSize = Math.min(regionSize, 64 * 1024); // 64KB chunks
            Memory buffer = new Memory(chunkSize + searchLen);

            for (long offset = 0; offset < regionSize; offset += chunkSize) {
                long readAddr = regionStart + offset;
                long readSize = Math.min(chunkSize + searchLen - 1, regionSize - offset);

                LongByReference outSize = new LongByReference();
                int readResult = MachKernel.INSTANCE.mach_vm_read_overwrite(
                    taskPort, readAddr, readSize, buffer, outSize);

                if (readResult == MachKernel.KERN_SUCCESS && outSize.getValue() > 0) {
                    bytesScanned += outSize.getValue();

                    // Search for the string in this chunk
                    byte[] data = buffer.getByteArray(0, (int) outSize.getValue());
                    int searchStart = 0;
                    int foundIndex;
                    while ((foundIndex = indexOf(data, searchBytes, searchStart)) >= 0) {
                        long foundAddress = readAddr + foundIndex;
                        allMatches.add(foundAddress);
                        logger.log(Level.INFO, "Found string at address 0x" + Long.toHexString(foundAddress) +
                                " (low 3 hex digits: " + String.format("%03X", foundAddress & 0xFFF) + ")");
                        searchStart = foundIndex + 1;
                    }
                }
            }

            // Move to next region
            addressRef.setValue(regionStart + regionSize);
            infoCount.setValue(9); // Reset info count for next call
        }

        logger.log(Level.INFO, "Found " + allMatches.size() + " occurrences after scanning " +
                regionsScanned + " regions, " + (bytesScanned / 1024 / 1024) + " MB");

        // On DOSBox Windows, the correct address ends in 74E, and the offset 0x2257 leads to another occurrence
        // On Boxer macOS, we look for similar patterns

        // First, try to find the address where adding 0x2257 leads to another occurrence
        // This validates we have the correct game data structure
        long expectedOffset = 0x2257L;
        for (Long addr : allMatches) {
            long nextExpected = addr + expectedOffset;
            if (allMatches.contains(nextExpected)) {
                logger.log(Level.INFO, "Found validated address 0x" + Long.toHexString(addr) +
                        " (next occurrence at expected offset 0x2257)");
                return addr;
            }
        }

        // Try to find the one with the correct pattern (ending in 74E, 78E or similar)
        for (Long addr : allMatches) {
            long lowBits = addr & 0xFFF;
            // Check for addresses ending in patterns similar to DOSBox (74E, 78E, etc.)
            if ((lowBits & 0xFF) == 0x4E || (lowBits & 0xFF) == 0x8E) {
                logger.log(Level.INFO, "Selected address 0x" + Long.toHexString(addr) +
                        " (matches DOSBox-like pattern, ends in " + String.format("%03X", lowBits) + ")");
                return addr;
            }
        }

        // If no matching pattern found, return the first one (or try validation)
        if (!allMatches.isEmpty()) {
            // Return the first match - the caller should validate
            logger.log(Level.INFO, "No pattern match, returning first occurrence: 0x" +
                    Long.toHexString(allMatches.get(0)));
            return allMatches.get(0);
        }

        logger.log(Level.WARNING, "String not found after scanning " + regionsScanned +
                " regions, " + (bytesScanned / 1024 / 1024) + " MB");
        return -1;
    }

    /**
     * Find the index of a byte pattern in a byte array, starting from a given position
     */
    private int indexOf(byte[] data, byte[] pattern, int startFrom) {
        outer:
        for (int i = startFrom; i <= data.length - pattern.length; i++) {
            for (int j = 0; j < pattern.length; j++) {
                if (data[i + j] != pattern[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    /**
     * Find the index of a byte pattern in a byte array
     */
    private int indexOf(byte[] data, byte[] pattern) {
        return indexOf(data, pattern, 0);
    }
}

