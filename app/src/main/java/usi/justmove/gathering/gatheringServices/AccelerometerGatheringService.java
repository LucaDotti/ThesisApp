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

import usi.justmove.database.base.DbController;
import usi.justmove.database.controllers.LocalDbController;
import usi.justmove.database.tables.AccelerometerTable;
import usi.justmove.utils.FrequencyHelper;

/**
 * Created by Luca Dotti on 03/01/17.
 */

public class AccelerometerGatheringService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(new AccelerometerEventListener(getApplicationContext(), 60), sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

class AccelerometerEventListener implements SensorEventListener {
    private Context context;
    private DbController dbController;
    private long elapseTime;
    private long lastFetchTime;

    public AccelerometerEventListener(Context context, double freq) {
        this.context = context;
        dbController = new LocalDbController(context, "JustMove");
        this.elapseTime = FrequencyHelper.getElapseTimeMillis(freq);
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
                dbController.insertRecords(AccelerometerTable.TABLE_ACCELEROMETER, records);
                Log.d("ACCELEROMETER SERVICE", "Added record: ts: " + record.get(AccelerometerTable.KEY_ACCELEROMETER_TIMESTAMP) + ", x: " + record.get(AccelerometerTable.KEY_ACCELEROMETER_X) + ", y: " + record.get(AccelerometerTable.KEY_ACCELEROMETER_Y)  + ", z: " + record.get(AccelerometerTable.KEY_ACCELEROMETER_Z));
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}