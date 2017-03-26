package usi.justmove.gathering;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import usi.justmove.gathering.base.SensorType;
import usi.justmove.gathering.gatheringServices.AccelerometerGatheringService;
import usi.justmove.gathering.gatheringServices.BluethootGatheringService;
import usi.justmove.gathering.gatheringServices.LocationGatheringService;
import usi.justmove.gathering.gatheringServices.LockGatheringService;
import usi.justmove.gathering.gatheringServices.PhoneCallGatheringService;
import usi.justmove.gathering.gatheringServices.SMSGatheringService;
import usi.justmove.gathering.gatheringServices.UsedAppGatheringService;
import usi.justmove.gathering.gatheringServices.WifiGatheringService;

import android.util.Log;

/**
 * Created by usi on 03/01/17.
 */

public class GatheringSystem {
    private List<SensorType> sensors;
    private Context context;

    public GatheringSystem(Context context) {
        sensors = new ArrayList<>();
        this.context = context;
    }

    public void addSensor(SensorType sensor) {
        sensors.add(sensor);
    }

    public void start() {
        for (SensorType sensor: sensors) {
            switch(sensor) {
                case ACCELEROMETER:
                    Log.d("GATHERING SYSTEM", "Started accelerometer service");
                    context.startService(new Intent(context, AccelerometerGatheringService.class));
                    break;
                case BLUETOOTH:
                    Log.d("GATHERING SYSTEM", "Started bluetooth service");
                    context.startService(new Intent(context, BluethootGatheringService.class));
                    break;
                case LOCATION:
                    Log.d("GATHERING SYSTEM", "Started location service");
                    context.startService(new Intent(context, LocationGatheringService.class));
                    break;
                case LOCK:
                    Log.d("GATHERING SYSTEM", "Started lock service");
                    context.startService(new Intent(context, LockGatheringService.class));
                    break;
                case SMS:
                    Log.d("GATHERING SYSTEM", "Started sms service");
                    context.startService(new Intent(context, SMSGatheringService.class));
                    break;
                case USED_APPS:
                    Log.d("GATHERING SYSTEM", "Started used apps service");
                    context.startService(new Intent(context, UsedAppGatheringService.class));
                    break;
                case WIFI:
                    Log.d("GATHERING SYSTEM", "Started wifi service");
                    context.startService(new Intent(context, WifiGatheringService.class));
                    break;
                case PHONE_CALLS:
                    Log.d("GATHERING SYSTEM", "Started calls service");
                    context.startService(new Intent(context, PhoneCallGatheringService.class));
                    break;
                default:
                    break;
            }
        }
    }

//    public void stop() {
//        context.stopService(new Intent(context, LocalTables.values()[i]));
//    }
}
