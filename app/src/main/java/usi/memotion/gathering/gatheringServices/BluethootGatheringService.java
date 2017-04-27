package usi.memotion.gathering.gatheringServices;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import usi.memotion.R;
import usi.memotion.local.database.controllers.LocalStorageController;
import usi.memotion.local.database.controllers.SQLiteController;
import usi.memotion.local.database.tables.BlueToothTable;

/**
 * Created by usi on 03/01/17.
 */

public class BluethootGatheringService extends Service {
    private BroadcastReceiver receiver;
    private Timer timer;
    private long samplingFreq;
    private boolean wasActive;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        long freq = Long.parseLong(getApplicationContext().getString(R.string.bluetoothSamplingFreq));
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);

        receiver = new BluetoothEventReceiver(SQLiteController.getInstance(getApplicationContext()));
        getApplicationContext().registerReceiver(receiver, filter);
        timer = new Timer();
        timer.schedule(new BluetoothScanTask(), 0, freq);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        getApplicationContext().unregisterReceiver(receiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private class BluetoothScanTask extends TimerTask {
        private BluetoothAdapter adapter;

        public BluetoothScanTask() {
            adapter = BluetoothAdapter.getDefaultAdapter();
        }

        @Override
        public void run() {
            if(adapter.isEnabled()) {
                wasActive = true;
            } else {
                wasActive = false;
                adapter.enable();
            }

            //need to add this, between the enable and the discovery. Not sure, but maybe, it takes a bit
            //to enable the bluetooth
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            adapter.startDiscovery();
        }
    }

    private class BluetoothEventReceiver extends BroadcastReceiver {
        private LocalStorageController localStorageController;

        public BluetoothEventReceiver(LocalStorageController localStorageController) {
            this.localStorageController = localStorageController;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                long now = System.currentTimeMillis();
                ContentValues record = new ContentValues();

                record.put(BlueToothTable.KEY_BLUETOOTH_TIMESTAMP, Long.toString(now));
                record.put(BlueToothTable.KEY_BLUETOOTH_MAC, device.getAddress());
                localStorageController.insertRecord(BlueToothTable.TABLE_BLUETOOTH, record);
                Log.d("BLUETOOTH SERVICE", "Added record: ts" + record.get(BlueToothTable.KEY_BLUETOOTH_TIMESTAMP) + ", mac: " + record.get(BlueToothTable.KEY_BLUETOOTH_MAC) + ", level: " + record.get(BlueToothTable.KEY_BLUETOOTH_LEVEL));
            }

            if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Log.d("BLUETOOTH", "Finished discovery");
            }

            if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Log.d("BLUETOOTH", "Started discovery");
            } else {
                if(!wasActive) {
                    BluetoothAdapter.getDefaultAdapter().disable();
                }
            }

        }
    }
}




