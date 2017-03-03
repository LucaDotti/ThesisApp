package usi.justmove.gathering.gatheringServices;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import usi.justmove.R;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.local.database.tables.AccelerometerTable;
import usi.justmove.utils.FrequencyHelper;

/**
 * Created by Luca Dotti on 03/01/17.
 */

public class AccelerometerGatheringService extends Service {
    private SensorManager sensorManager;
    private SensorEventListener listener;

    @Override
    public void onCreate() {
        super.onCreate();

        long freq = Long.parseLong(getApplicationContext().getString(R.string.accelerometerSamplingFreq));
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        listener = new AccelerometerEventListener(SQLiteController.getInstance(getApplicationContext()), freq);
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(listener);
    }
}

class AccelerometerEventListener implements SensorEventListener {
    private Context context;
    private LocalStorageController localStorageController;
    private long elapseTime;
    private long lastFetchTime;

    public AccelerometerEventListener(LocalStorageController localStorageController, long freq) {
        this.localStorageController = localStorageController;
        this.elapseTime = freq;
        lastFetchTime = System.currentTimeMillis();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float x;
        float y;
        float z;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long now = System.currentTimeMillis();

            if(now - lastFetchTime >= elapseTime) {
                Log.d("ACCELEROMETER", Long.toString(now-lastFetchTime));
                lastFetchTime = now;

                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                List<Map<String, String>> records = new ArrayList<>();
                Map<String, String> record = new HashMap<>();

                record.put(AccelerometerTable.KEY_ACCELEROMETER_ID, null);
                record.put(AccelerometerTable.KEY_ACCELEROMETER_TIMESTAMP, Long.toString(lastFetchTime));
                record.put(AccelerometerTable.KEY_ACCELEROMETER_X, Float.toString(x));
                record.put(AccelerometerTable.KEY_ACCELEROMETER_Y, Float.toString(y));
                record.put(AccelerometerTable.KEY_ACCELEROMETER_Z, Float.toString(z));
                records.add(record);
                localStorageController.insertRecords(AccelerometerTable.TABLE_ACCELEROMETER, records);
                Log.d("ACCELEROMETER SERVICE", "Added record: ts: " + record.get(AccelerometerTable.KEY_ACCELEROMETER_TIMESTAMP) + ", x: " + record.get(AccelerometerTable.KEY_ACCELEROMETER_X) + ", y: " + record.get(AccelerometerTable.KEY_ACCELEROMETER_Y)  + ", z: " + record.get(AccelerometerTable.KEY_ACCELEROMETER_Z));
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}