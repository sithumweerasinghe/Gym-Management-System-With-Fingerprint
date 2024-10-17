package fingerprint;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

public class FingerprintSDK {

    public interface HCNetSDK extends com.sun.jna.Library {
        HCNetSDK INSTANCE = Native.load("HCNetSDK", HCNetSDK.class);

        boolean NET_DVR_Init();
        int NET_DVR_Login_V30(String ip, short port, String username, String password, NET_DVR_DEVICEINFO_V30 deviceInfo);
        boolean NET_DVR_Logout(int userID);
        boolean NET_DVR_Cleanup();

        // These methods are included for future fingerprint operations
        boolean NET_DVR_CaptureFingerprint(int userID, byte[] fingerprintData);
        String NET_DVR_GetLastError();
        String NET_DVR_GetFingerData();
    }

    public static class NET_DVR_DEVICEINFO_V30 extends com.sun.jna.Structure {
        public byte[] sDeviceName = new byte[32]; // Device Name
        // Other fields as needed based on device documentation

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("sDeviceName");
        }
    }

    static {
        try {
            String dllPath = "dlls";
            NativeLibrary.addSearchPath("HCNetSDK", dllPath);

            System.out.println("Attempting to load HCNetSDK.dll...");
            HCNetSDK sdk = HCNetSDK.INSTANCE;
            System.out.println("HCNetSDK.dll loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load HCNetSDK.dll: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean attemptFingerprintLogin(String ip, short port) {
        NET_DVR_DEVICEINFO_V30 deviceInfo = new NET_DVR_DEVICEINFO_V30();
        // Update with provided username and password
        int userID = HCNetSDK.INSTANCE.NET_DVR_Login_V30(ip, port, "admin", "CCTV1234", deviceInfo);
        if (userID >= 0) {
            System.out.println("Connected to the device at IP: " + ip + " successfully. User ID: " + userID);
            // Perform fingerprint operations here if needed
            HCNetSDK.INSTANCE.NET_DVR_Logout(userID);
            return true;
        } else {
            System.err.println("Failed to connect to device at IP: " + ip);
            System.err.println("Error: " + HCNetSDK.INSTANCE.NET_DVR_GetLastError());
            return false;
        }
    }

    public static String scanForDevice(String subnet, short port) {
        // Loop through possible IP addresses in the subnet (e.g., 192.168.8.x)
        for (int i = 1; i < 255; i++) {
            String host = subnet + i;
            try {
                InetAddress address = InetAddress.getByName(host);
                if (address.isReachable(1000)) { // Ping the device
                    System.out.println("Host reachable: " + host);

                    // Attempt to connect to the fingerprint device at this IP
                    if (attemptFingerprintLogin(host, port)) {
                        return host; // Return the correct IP address once found
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null; // No device found
    }

    public static void main(String[] args) {
        try {
            // Initialize the SDK
            System.out.println("Initializing SDK...");
            boolean init = HCNetSDK.INSTANCE.NET_DVR_Init();
            if (init) {
                System.out.println("SDK Initialized successfully.");

                // Specify the subnet of the local network (e.g., 192.168.8.x)
                String subnet = "192.168.8."; // Adjusted for your network
                short port = 8000; // Default port for Hikvision devices

                String deviceIP = scanForDevice(subnet, port);
                if (deviceIP != null) {
                    System.out.println("Fingerprint device found at IP: " + deviceIP);
                } else {
                    System.err.println("No fingerprint device found on the network.");
                }

                // Clean up the SDK resources
                HCNetSDK.INSTANCE.NET_DVR_Cleanup();
            } else {
                System.err.println("SDK Initialization failed.");
            }
        } catch (Exception e) {
            System.err.println("Error in SDK initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
