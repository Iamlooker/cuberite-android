package org.cuberite.android.helpers;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.cuberite.android.services.CuberiteService;

import static android.content.Context.WIFI_SERVICE;

public class CuberiteHelper {
    // Logging tag
    private static final String LOG = "Cuberite/CuberiteHelper";

    private static StringBuilder consoleOutput = new StringBuilder();

    private static final String EXECUTABLE_NAME = "Cuberite";

    public static void addConsoleOutput(Context context, String string) {
        consoleOutput.append(string).append("\n");

        Intent intent = new Intent("updateLog");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static String getConsoleOutput() {
        return consoleOutput.toString();
    }

    public static void resetConsoleOutput() {
        consoleOutput = new StringBuilder();
    }

    public static String getExecutableName() {
        return EXECUTABLE_NAME;
    }

    public static String getIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();

        if (ip == 0) {
            return "127.0.0.1";
        } else {
            return Formatter.formatIpAddress(ip);
        }
    }

    public static String getPreferredABI() {
        String abi;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abi = Build.SUPPORTED_ABIS[0];
        } else {
            abi = Build.CPU_ABI;
        }

        Log.d(LOG, "Getting preferred ABI: " + abi);

        return abi;
    }

    public static boolean isCuberiteRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (CuberiteService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void startCuberite(Context context) {
        Log.d(LOG, "Starting Cuberite");

        Intent serviceIntent = new Intent(context, CuberiteService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    public static void stopCuberite(Context context) {
        Log.d(LOG, "Stopping Cuberite");

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("stop"));
    }

    public static void killCuberite(Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("kill"));
    }
}
