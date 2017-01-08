package usi.justmove.gathering.gatheringServices;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import usi.justmove.database.base.DbController;
import usi.justmove.database.controllers.LocalDbController;
import usi.justmove.database.tables.AccelerometerTable;
import usi.justmove.database.tables.BlueToothTable;
import usi.justmove.utils.FrequencyHelper;

import static android.R.attr.filter;
import static android.R.attr.x;
import static android.R.attr.y;
import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;

/**
 * Created by usi on 03/01/17.
 */

public class BluethootGatheringService extends Service {
    private BroadcastReceiver receiver;
    private Timer timer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);

        receiver = new BluetoothEventReceiver(getApplicationContext());
        getApplicationContext().registerReceiver(receiver, filter);
        timer = new Timer();
        timer.schedule(new BluetoothScanTask(), 0, 60*1000);
    }
}

class BluetoothEventReceiver extends BroadcastReceiver {
    private DbController dbController;

    public BluetoothEventReceiver(Context context) {
        dbController = new LocalDbController(context, "justMove");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(intent.getAction());
        if(intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            long now = System.currentTimeMillis();
            List<Map<String, String>> records = new ArrayList<>();
            Map<String, String> record = new HashMap<>();

            record.put(BlueToothTable.KEY_BLUETOOTH_ID, null);
            record.put(BlueToothTable.KEY_BLUETOOTH_TIMESTAMP, Long.toString(now));
            record.put(BlueToothTable.KEY_BLUETOOTH_MAC, device.getAddress());
            record.put(BlueToothTable.KEY_BLUETOOTH_LEVEL, null);
            records.add(record);
            dbController.insertRecords(BlueToothTable.TABLE_BLUETOOTH, records);
            Log.d("BLUETOOTH SERVICE", "Added record: ts" + record.get(BlueToothTable.KEY_BLUETOOTH_TIMESTAMP) + ", mac: " + record.get(BlueToothTable.KEY_BLUETOOTH_MAC) + ", level: " + record.get(BlueToothTable.KEY_BLUETOOTH_LEVEL));
        }
    }
}

class BluetoothScanTask extends TimerTask {
    private BluetoothAdapter adapter;

    public BluetoothScanTask() {
        adapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void run() {
        adapter.startDiscovery();
    }
}
