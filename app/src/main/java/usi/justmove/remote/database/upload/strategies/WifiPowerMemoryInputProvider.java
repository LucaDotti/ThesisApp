package usi.justmove.remote.database.upload.strategies;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.util.Log;

import java.io.File;

import usi.justmove.R;
import usi.justmove.gathering.base.StateMachineInputProvider;

/**
 * Created by usi on 17/01/17.
 */

public class WifiPowerMemoryInputProvider implements StateMachineInputProvider<WifiPowerMemorySMSymbol> {
    private ConnectivityManager mgr;
    private Context context;
    private String dbName;
    private long max_size;
    private long minBatteryLevel;

    public WifiPowerMemoryInputProvider(Context context, long max_size, long minBatteryLevel) {
        mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.context = context;
        this.dbName = context.getResources().getString(R.string.dbName);
        this.max_size = max_size;
        this.minBatteryLevel = minBatteryLevel;
    }
    @Override
    public WifiPowerMemorySMSymbol getInput() {
        if(checkWifi()) {
            if(checkPower()) {
                return WifiPowerMemorySMSymbol.UPLOAD;
            } else {
                if(checkSize()) {
                    return WifiPowerMemorySMSymbol.FORCE_UPLOAD;
                } else {
                    return WifiPowerMemorySMSymbol.WAIT;
                }
            }
        } else {
            return WifiPowerMemorySMSymbol.WAIT;
        }
    }

    private boolean checkSize() {
        File f = context.getDatabasePath(dbName);
        return f.length() > max_size;
    }

    /**
     * http://stackoverflow.com/questions/5283491/check-if-device-is-plugged-in
     * @return
     */
    private boolean checkPower() {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

        return plugged == BatteryManager.BATTERY_PLUGGED_AC
                || plugged == BatteryManager.BATTERY_PLUGGED_USB
                && level >= minBatteryLevel;
    }

    private boolean checkWifi() {
        Network[] networks = mgr.getAllNetworks();
        if (networks == null) {
            return false;
        } else {
            for (Network network : networks) {
                NetworkInfo info = mgr.getNetworkInfo(network);
                if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
                    if (info.isAvailable() && info.isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
