package usi.justmove;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import net.danlew.android.joda.JodaTimeAndroid;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import usi.justmove.UI.SurveyEvent;
import usi.justmove.UI.TabFragmentAdapter;
import usi.justmove.UI.fragments.HomeFragment;
import usi.justmove.UI.fragments.NotificationBroadcastReceiver;
import usi.justmove.UI.fragments.SwipeChoiceViewPager;
import usi.justmove.UI.menu.ProfileDialogFragment;
import usi.justmove.UI.menu.StudyDialogFragment;
import usi.justmove.gathering.user.SurveysService;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.gathering.GatheringSystem;
import usi.justmove.gathering.base.SensorType;
import usi.justmove.remote.database.upload.DataUploadService;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnUserRegisteredCallback {
    private GatheringSystem gSys;
    private TabLayout tabLayout;
    private SwipeChoiceViewPager viewPager;
    private FragmentPagerAdapter tabFragmentAdapter;
    private android.support.v7.widget.Toolbar toolbar;

    private final int PERMISSION_REQUEST_STATUS = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JodaTimeAndroid.init(this);

        Intent i = getIntent();
        String action = i.getAction();

        viewPager = (SwipeChoiceViewPager) findViewById(R.id.viewPager);
        viewPager.setSwipeEnabled(false);
        tabFragmentAdapter = new TabFragmentAdapter(getSupportFragmentManager(), getApplicationContext());
        viewPager.setAdapter(tabFragmentAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(2).setCustomView(R.layout.surveys_tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

        if(action.equals(NotificationBroadcastReceiver.OPEN_SURVEYS_ACTION)) {
            viewPager.setCurrentItem(2);
        }

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(!checkPermissions()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.PROCESS_OUTGOING_CALLS,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.INTERNET},
                    PERMISSION_REQUEST_STATUS);
        } else {
            init();
        }
    }

    private void init() {
        getApplicationContext().deleteDatabase("JustMove");
        new SQLiteController(getApplication());
        gSys = new GatheringSystem(getApplicationContext());
        gSys.addSensor(SensorType.LOCK);
        gSys.addSensor(SensorType.WIFI);
        gSys.addSensor(SensorType.LOCATION);
        gSys.addSensor(SensorType.BLUETOOTH);
        gSys.addSensor(SensorType.ACCELEROMETER);
        gSys.addSensor(SensorType.PHONE_CALLS);
        gSys.addSensor(SensorType.SMS);
        gSys.addSensor(SensorType.USED_APPS);
//        gSys.start();

//        startService(new Intent(this, DataUploadService.class));
        startService(new Intent(this, SurveysService.class));
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
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onUserRegisteredCallback() {
        tabFragmentAdapter.notifyDataSetChanged();
//        viewPager.setCurrentItem(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        DialogFragment dialog;
        switch(id) {
            case R.id.actionBarItem_aboutThisStudy:
                dialog = new StudyDialogFragment();
                dialog.show(getSupportFragmentManager(), "aboutThisStudy");
                break;
            case R.id.actionBarItem_profile:
                dialog = new ProfileDialogFragment();
                dialog.show(getSupportFragmentManager(), "profile");
                break;
            default:
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        stopServices();
    }

//    private void stopServices() {
//        gSys.
//        stopService(new Intent(this, DataUploadService.class));
//    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SurveyEvent event) {

    }
}

