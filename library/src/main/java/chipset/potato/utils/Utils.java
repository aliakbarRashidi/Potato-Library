package chipset.potato.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Developer: chipset
 * Package : chipset.potato
 * Project : Potato-Library
 * Date : 14/1/15
 */
public class Utils {

    private Context mContext;

    public Utils(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Method to get internet connection status
     *
     * @return true     if {@link Boolean} internet connection is established else false
     */
    public boolean isInternetConnected() {
        boolean isConnected;
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = (activeNetwork != null)
                && (activeNetwork.isConnectedOrConnecting());
        return isConnected;
    }

    /**
     * Method to hide keyboard
     *
     * @param view View of the activity to get Window Token
     */

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Method to get Bluetooth status
     *
     * @return {@link java.lang.Boolean} true if internet bluetooth available
     */
    public boolean isBluetoothAvailable() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(mContext, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }

    /**
     * Method to get Bluetooth status
     *
     * @return {@link java.lang.Boolean} true if internet bluetooth is enabled else false
     */
    public boolean isBluetoothOn() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(mContext, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            return false;
        }
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * Method to switch on bluetooth
     */
    public void setBluetoothOn() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (isBluetoothAvailable()) {
            if (!isBluetoothOn()) {
                mBluetoothAdapter.enable();
            }
        }
    }

    /**
     * Method to switch Off bluetooth
     */
    public void setBluetoothOff() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (isBluetoothAvailable()) {
            if (isBluetoothOn()) {
                mBluetoothAdapter.disable();
            }
        }
    }

    /**
     * Method to get switch on bluetooth and make it discoverable
     *
     * @param seconds Time in seconds for discoverable
     */
    public void setBluetoothOnAndDiscoverable(int seconds) {
        if (seconds > 3600 && seconds < 1) {
            Log.i("Potato.Utils.Bluetooth", "Seconds set to 120");
        }
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (isBluetoothAvailable()) {
            setBluetoothOn();
            if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                        .putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, seconds)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(discoverableIntent);
            }
        }
    }

    /**
     * Method to get battery level
     *
     * @return {@link java.lang.Integer} with battery level
     */
    public int getBatteryLevel() {
        final int[] level = new int[1];
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context mContext, Intent intent) {
                level[0] = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            }
        }, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return level[0];
    }

    /**
     * Method to open Directory in external storage
     * Directory will be created if it does not already exist
     *
     * @param folderName containing directory name
     * @return {@link java.io.File} object of opened/created directory
     */
    public File openDirectory(String folderName) throws IOException {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + folderName);
        if (!file.exists()) {
            file.mkdirs();
        }
        MediaScannerConnection.scanFile(mContext, new String[]{file.getAbsolutePath()}, null, null);
        return file;
    }

    /**
     * Method to get GPS status
     *
     * @return {@link java.lang.Boolean} true if GPS provider is enabled else false
     */
    public boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Method to get WiFi status
     *
     * @return {@link java.lang.Boolean} true if WiFi is enabled else false
     */

    public boolean isWiFiEnabled() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * Method to switch on WiFi
     *
     * @param mContext Context of the current activity
     */
    public void setWiFiOn(Context mContext) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    /**
     * Method to switch off WiFi
     */
    public void setWifiOff() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
    }

    /**
     * Method to get root View of an activity
     *
     * @param view Any view in an activity
     * @return {@link android.view.View} Root view of the activity
     */
    public View getRootView(View view) {
        return view.findViewById(android.R.id.content);
    }

    /**
     * Method to check type of internet connection
     *
     * @return 0 for not connected, 1 for WiFi, 2 for Mobile Data
     */
    public int connectionType() {
        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return 1;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return 2;
        }
        return 0;
    }

    /**
     * Method to check if Mobile Data is enabled
     *
     * @return {@link java.lang.Boolean} true if Mobile Data is enabled
     */
    public boolean isMobileDataEnabled() {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mobileDataEnabled;
    }

    /**
     * Method to enable Mobile Data
     */
    public void enableMobileData() {
        try {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            Method dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
            dataMtd.setAccessible(true);
            dataMtd.invoke(cm, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to disable Mobile Data
     */
    public void disableMobileData() {
        try {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            Method dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
            dataMtd.setAccessible(true);
            dataMtd.invoke(cm, false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}