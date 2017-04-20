package usi.memotion.stateMachines.strategies.wifiPowerDbSize;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.BatteryManager;

import java.io.File;

import usi.memotion.MyApplication;
import usi.memotion.R;
import usi.memotion.stateMachines.base.ActiveStateMachine;

/**
 * State machine based on Wifi, power and database size.
 *
 * Created by usi on 20/04/17.
 */

public class WPMStateMachine extends ActiveStateMachine {
    private ConnectivityManager mgr;
    private Context context;
    private String dbName;
    private long max_size;
    private long minBatteryLevel;
    private BroadcastReceiver broadcastReceiver;

    public WPMStateMachine(final WPMSMState[][] transitions, WPMSMState startState, long max_size, long minBatteryLevel) {
        super(transitions, startState);

        this.context = MyApplication.getContext();
        mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        this.dbName = context.getResources().getString(R.string.dbName);
        this.max_size = max_size;
        this.minBatteryLevel = minBatteryLevel;

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                transition();
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);

        context.registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public WPMSMSymbol getSymbol() {
        if(checkWifi()) {
            if(checkPower()) {
                return WPMSMSymbol.UPLOAD;
            } else {
                if(checkSize()) {
                    return WPMSMSymbol.FORCE_UPLOAD;
                } else {
                    return WPMSMSymbol.WAIT;
                }
            }
        } else {
            return WPMSMSymbol.WAIT;
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
