package trainer.core.platform;

import trainer.exceptions.DosBoxProcessNotFoundException;
import trainer.exceptions.DosBoxStartedAsAdminException;
import trainer.exceptions.UnknownReasonException;
import trainer.exceptions.UnsupportedDosBoxVersionException;

/**
 * Abstract interface for platform-specific memory access operations.
 * This allows the trainer to work on both Windows and macOS.
 */
public interface PlatformMemoryAccess {

    /**
     * Represents a handle to a process. Implementation is platform-specific.
     */
    interface ProcessHandle {
        boolean isValid();
        void close();
    }

    /**
     * Find the DOSBox/Boxer process
     */
    ProcessHandle findEmulatorProcess() throws DosBoxProcessNotFoundException,
            UnsupportedDosBoxVersionException, DosBoxStartedAsAdminException, UnknownReasonException;

    /**
     * Read a float value from process memory
     */
    float readFloat(ProcessHandle process, long address);

    /**
     * Read a byte value from process memory
     */
    byte readByte(ProcessHandle process, long address);

    /**
     * Read a string from process memory
     */
    String readString(ProcessHandle process, long address, int length);

    /**
     * Write a float value to process memory
     */
    void writeFloat(ProcessHandle process, long address, float value);

    /**
     * Get the emulator version (e.g., DOSBox 0.74 or Boxer 2)
     */
    int getEmulatorVersion(ProcessHandle process) throws UnsupportedDosBoxVersionException;

    /**
     * Get the name of the emulator process to look for
     */
    String getEmulatorProcessName();

    /**
     * Find a string in process memory by scanning memory regions.
     * This is more efficient than linear scanning on some platforms.
     *
     * @param process The process handle
     * @param searchString The string to search for
     * @param startAddress Starting address for the search
     * @param maxAddress Maximum address to search up to
     * @return The address where the string was found, or -1 if not found
     */
    default long findStringInMemory(ProcessHandle process, String searchString, long startAddress, long maxAddress) {
        // Default implementation uses linear scanning
        int memorySize = searchString.length() * 2;
        long currentPointer = startAddress;
        long step = 0x1000L; // 4KB step

        while (currentPointer < maxAddress) {
            String value = readString(process, currentPointer, memorySize);
            if (value.equals(searchString)) {
                return currentPointer;
            }
            currentPointer += step;
        }
        return -1;
    }

    /**
     * Check if the current platform is supported
     */
    static boolean isPlatformSupported() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") || os.contains("mac");
    }

    /**
     * Get the appropriate platform implementation
     */
    static PlatformMemoryAccess getInstance() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return new trainer.core.platform.WindowsMemoryAccess();
        } else if (os.contains("mac")) {
            return new trainer.core.platform.MacOSMemoryAccess();
        }
        throw new UnsupportedOperationException("Platform not supported: " + os);
    }
}

