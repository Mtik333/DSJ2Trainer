package trainer.core;

public final class PrivilegeTest {

//    public static final String SE_DEBUG_NAME = "SeDebugPrivilege";

////////////////////////////////////////////////////////////////////////

//    public static final int SE_PRIVILEGE_ENABLED = 0x00000002;

    ////////////////////////////////////////////////////////////////////////
//    public static final int TOKEN_ASSIGN_PRIMARY = 0x00000001;
//    public static final int TOKEN_DUPLICATE = 0x00000002;
//    public static final int TOKEN_IMPERSONATE = 0x00000004;
//    public static final int TOKEN_QUERY = 0x00000008;
//    public static final int TOKEN_QUERY_SOURCE = 0x00000010;
//    public static final int TOKEN_ADJUST_PRIVILEGES = 0x00000020;
//    public static final int TOKEN_ADJUST_GROUPS = 0x00000040;
//    public static final int TOKEN_ADJUST_DEFAULT = 0x00000080;
//    public static final int TOKEN_ADJUST_SESSIONID = 0x00000100;
//    public static final int STANDARD_RIGHTS_READ = 0x00020000;
//    public static final int STANDARD_RIGHTS_REQUIRED = 0x000F0000;
//    public static final int TOKEN_READ = (STANDARD_RIGHTS_READ | TOKEN_QUERY);
//    public static final int TOKEN_ALL_ACCESS = (STANDARD_RIGHTS_REQUIRED | TOKEN_ASSIGN_PRIMARY |
//            TOKEN_DUPLICATE | TOKEN_IMPERSONATE | TOKEN_QUERY | TOKEN_QUERY_SOURCE |
//            TOKEN_ADJUST_PRIVILEGES | TOKEN_ADJUST_GROUPS | TOKEN_ADJUST_DEFAULT | TOKEN_ADJUST_SESSIONID);

    ////////////////////////////////////////////////////////////////////////
//    private static final Logger logger = Logger.getLogger(PrivilegeTest.class.getName());

//
//    public static void enableDebugPrivilege(WinNT.HANDLE hProcess) throws RuntimeException {
//        WinNT.HANDLEByReference hToken = new WinNT.HANDLEByReference();
//        boolean success = Advapi32.INSTANCE.OpenProcessToken(hProcess, TOKEN_QUERY | TOKEN_ADJUST_PRIVILEGES, hToken);
//        logger.log(Level.SEVERE, "OpenProcessToken success? " + success + "; hToken: " + hToken);
//        if (!success) {
//            int err = Native.getLastError();
//            throw new RuntimeException("OpenProcessToken failed. Error: " + err);
//        }
//        WinNT.LUID luid = new WinNT.LUID();
//        success = Advapi32.INSTANCE.LookupPrivilegeValue(null, WinNT.SE_DEBUG_NAME, luid);
//        logger.log(Level.SEVERE, "LookupPrivilegeValue success? " + success + "; luid: " + luid);
//        if (!success) {
//            int err = Native.getLastError();
//            throw new RuntimeException("LookupPrivilegeValueA failed. Error: " + err);
//        }
//        WinNT.TOKEN_PRIVILEGES tkp = new WinNT.TOKEN_PRIVILEGES(1);
//        tkp.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(luid, new WinDef.DWORD(WinNT.SE_PRIVILEGE_ENABLED));
//        success = Advapi32.INSTANCE.AdjustTokenPrivileges(hToken.getValue(), false, tkp, tkp.size(), null, null);
//        logger.log(Level.SEVERE, "AdjustTokenPrivileges success? " + success + "; tkp: " + tkp);
//        if (!success) {
//            int err = Native.getLastError();
//            throw new RuntimeException("AdjustTokenPrivileges failed. Error: " + err);
//        }
////        Kernel32.INSTANCE.CloseHandle(hToken.getValue());
//    }

}
