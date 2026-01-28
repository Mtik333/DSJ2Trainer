package trainer.core.platform;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;

import java.util.Arrays;
import java.util.List;

/**
 * JNA interface for macOS mach kernel APIs for process memory access.
 */
public interface MachKernel extends Library {

    MachKernel INSTANCE = Native.load("c", MachKernel.class);

    // Mach error codes
    int KERN_SUCCESS = 0;
    int KERN_INVALID_ADDRESS = 1;

    // VM protection flags
    int VM_PROT_READ = 0x01;
    int VM_PROT_WRITE = 0x02;
    int VM_PROT_EXECUTE = 0x04;
    int VM_PROT_ALL = VM_PROT_READ | VM_PROT_WRITE | VM_PROT_EXECUTE;

    // VM region flavor
    int VM_REGION_BASIC_INFO_64 = 9;

    /**
     * Get the mach task port for a process
     */
    int task_for_pid(int target_tport, int pid, IntByReference task);

    /**
     * Read memory from a task
     */
    int mach_vm_read_overwrite(int target_task, long address, long size,
                                Pointer data, LongByReference outsize);

    /**
     * Write memory to a task
     */
    int mach_vm_write(int target_task, long address, Pointer data, int dataCnt);

    /**
     * Change memory protection for a region
     */
    int mach_vm_protect(int target_task, long address, long size, int set_maximum, int new_protection);

    /**
     * Get the current task port
     */
    int mach_task_self();

    /**
     * Deallocate a port
     */
    int mach_port_deallocate(int task, int name);

    /**
     * Get information about a memory region
     */
    int mach_vm_region(int target_task, LongByReference address, LongByReference size,
                       int flavor, Pointer info, IntByReference infoCnt, IntByReference object_name);
}

