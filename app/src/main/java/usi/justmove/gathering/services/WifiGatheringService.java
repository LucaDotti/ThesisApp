package usi.justmove.gathering.services;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Created by usi on 29/12/16.
 */

public class WifiGatheringService extends IntentService {
    private WifiManager wifiManager;
    private BroadcastReceiver wifiScanResultReceiver;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public WifiGatheringService(String name) {
        super(name);
    }

    public WifiGatheringService() {
        super("WifiGatheringService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        wifiScanResultReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction() == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                    Log.d("WIFI", "GOT");
                    synchronized(this) {
                        notify();
                    }
                }
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while(true) {
            wifiManager.startScan();
            synchronized(this) {
                try {
                    wait();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
