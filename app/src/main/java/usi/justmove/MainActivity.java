package usi.justmove;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import net.danlew.android.joda.JodaTimeAndroid;

import usi.justmove.database.controllers.LocalDbController;
import usi.justmove.gathering.GatheringSystem;
import usi.justmove.gathering.base.SensorType;

public class MainActivity extends AppCompatActivity {
    private GatheringSystem gSys;
    private int PERMISSION_REQUEST_STATUS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JodaTimeAndroid.init(this);

        if(!checkPermissions()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.PROCESS_OUTGOING_CALLS,
                            Manifest.permission.READ_PHONE_STATE},
                    PERMISSION_REQUEST_STATUS);
        } else {
            init();
        }
    }

    private void init() {
        //        getApplicationContext().deleteDatabase("JustMove");
        new LocalDbController(getApplication(), "JustMove");
        gSys = new GatheringSystem(getApplicationContext());
        gSys.addSensor(SensorType.LOCK);
        gSys.addSensor(SensorType.WIFI);
        gSys.addSensor(SensorType.LOCATION);
        gSys.addSensor(SensorType.BLUETOOTH);
        gSys.addSensor(SensorType.ACCELEROMETER);
        gSys.addSensor(SensorType.PHONE_CALLS);
        gSys.addSensor(SensorType.SMS);
        gSys.addSensor(SensorType.USED_APPS);
        gSys.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_REQUEST_STATUS) {
            init();
        }
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED;
    }
}
