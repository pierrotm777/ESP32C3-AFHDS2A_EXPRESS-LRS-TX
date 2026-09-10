package crazydude.com.telemetry.compat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * Android 12+ BLE compatibility used by BLE-v15.
 *
 * Important: GATT calls themselves stay native in BluetoothLeDataPoller.
 * This class only handles runtime permissions and guards getBondedDevices(),
 * which was the exact SecurityException seen on Android 14.
 */
public final class BleCompat {
    private static final String TAG = "Telemetry-BLE";
    private static final int REQ_BLE = 1709;
    private static final String PERM_BLUETOOTH_SCAN = "android.permission.BLUETOOTH_SCAN";
    private static final String PERM_BLUETOOTH_CONNECT = "android.permission.BLUETOOTH_CONNECT";
    private static final String PERM_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";

    private BleCompat() {}

    public static boolean ensurePermissions(Activity activity) {
        if (Build.VERSION.SDK_INT < 23) return true;

        ArrayList<String> missing = new ArrayList<String>();
        if (activity.checkSelfPermission(PERM_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            missing.add(PERM_FINE_LOCATION);
        }
        if (Build.VERSION.SDK_INT >= 31) {
            if (activity.checkSelfPermission(PERM_BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                missing.add(PERM_BLUETOOTH_SCAN);
            }
            if (activity.checkSelfPermission(PERM_BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                missing.add(PERM_BLUETOOTH_CONNECT);
            }
        }
        if (!missing.isEmpty()) {
            activity.requestPermissions(missing.toArray(new String[missing.size()]), REQ_BLE);
            return false;
        }
        return true;
    }

    public static Set<BluetoothDevice> getBondedDevices(BluetoothAdapter adapter) {
        try {
            return adapter == null ? Collections.<BluetoothDevice>emptySet() : adapter.getBondedDevices();
        } catch (RuntimeException e) {
            Log.e(TAG, "getBondedDevices failed", e);
            return Collections.emptySet();
        }
    }
}
