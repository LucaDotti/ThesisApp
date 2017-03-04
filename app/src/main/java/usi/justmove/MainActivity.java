package usi.justmove;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import net.danlew.android.joda.JodaTimeAndroid;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.LocalDateTime;

import usi.justmove.UI.TabFragmentAdapter;
import usi.justmove.UI.fragments.HomeFragment;
import usi.justmove.UI.fragments.NotificationBroadcastReceiver;
import usi.justmove.UI.fragments.SurveysFragment;
import usi.justmove.UI.fragments.SwipeChoiceViewPager;
import usi.justmove.UI.menu.ProfileDialogFragment;
import usi.justmove.UI.menu.StudyDialogFragment;
import usi.justmove.gathering.surveys.Surveys;
import usi.justmove.gathering.surveys.SurveysService;
import usi.justmove.gathering.surveys.inspection.SurveyEvent;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.gathering.GatheringSystem;
import usi.justmove.gathering.base.SensorType;
import usi.justmove.local.database.tables.LocalDbUtility;
import usi.justmove.local.database.tables.LocalTables;
import usi.justmove.remote.database.upload.DataUploadService;

public class MainActivity extends AppCompatActivity implements SurveysFragment.OnSurveyCompletedCallback {
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


        showSurveyNotification();


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

        if(action != null && action.equals(NotificationBroadcastReceiver.OPEN_SURVEYS_ACTION)) {
            viewPager.setCurrentItem(2);
        } else {
//            deleteDatabase("JustMove");
        }

        SQLiteController.getInstance(this);

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
//        deleteDatabase("JustMove");
        gSys = new GatheringSystem(getApplicationContext());
        gSys.addSensor(SensorType.LOCK);
        gSys.addSensor(SensorType.WIFI);
        gSys.addSensor(SensorType.LOCATION);
        gSys.addSensor(SensorType.BLUETOOTH);
        gSys.addSensor(SensorType.ACCELEROMETER);
        gSys.addSensor(SensorType.PHONE_CALLS);
        gSys.addSensor(SensorType.SMS);
//        gSys.addSensor(SensorType.USED_APPS);
        gSys.start();

        startService(new Intent(this, DataUploadService.class));
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
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SurveyEvent event) {
        tabFragmentAdapter.notifyDataSetChanged();
        tabLayout.getTabAt(2).setCustomView(R.layout.surveys_tab_layout);
        showSurveyNotification();
    }


    private int getAvailableSurveysCount() {
        int count = 0;
        Cursor surveys = null;
        for(Surveys survey: Surveys.values()) {
            switch(survey) {
                case PAM:
                    surveys = getTodaySurveys(LocalTables.TABLE_PAM);
                    count += surveys.getCount();
                    break;
                case PWB:
                    surveys = getTodaySurveys(LocalTables.TABLE_PWB);
                    count += surveys.getCount();
                    break;
            }
        }

        if(surveys != null) {
            surveys.close();
        }

        return count;
    }

    private Cursor getTodaySurveys(LocalTables table) {
        LocalStorageController controller = SQLiteController.getInstance(this);
        String tableName = LocalDbUtility.getTableName(table);
        String columnSchedule = LocalDbUtility.getTableColumns(table)[2];
        String columnCompleted = LocalDbUtility.getTableColumns(table)[3];
        String columnNotified = LocalDbUtility.getTableColumns(table)[4];
        String columnExpired = LocalDbUtility.getTableColumns(table)[5];

        LocalDateTime startDateTime = new LocalDateTime().withTime(0, 0, 0, 0);
        LocalDateTime endDateTime = new LocalDateTime().withTime(23, 59, 59, 999);
        long startMillis = startDateTime.toDateTime().getMillis()/1000;
        long endMillis = endDateTime.toDateTime().getMillis()/1000;

        Cursor c = controller.rawQuery("SELECT * FROM " + tableName
                + " WHERE " + columnSchedule + " >= " + startMillis + " AND " + columnSchedule + " <= " + endMillis
                + " AND " + columnCompleted + " = " + 0 + " AND " + columnNotified + " > " + 0 + " AND " + columnExpired + " = " + 0, null);
        return c;
    }

    private void showSurveyNotification() {
        int count = getAvailableSurveysCount();

        View notificationView = tabLayout.getTabAt(2).getCustomView();

        ImageView image = (ImageView) notificationView.findViewById(R.id.surveysPamNotificationImage);
        if(count == 0) {
            image.setVisibility(View.INVISIBLE);
        } else {
            image.setVisibility(View.VISIBLE);
            switch(count) {
                case 1:
                    image.setImageResource(R.drawable.notification_1);
                    break;
                case 2:
                    image.setImageResource(R.drawable.notification_2);
                    break;
                case 3:
                    image.setImageResource(R.drawable.notification_3);
                    break;
                default:
            }
        }
    }

    @Override
    public void onSurveyCompletedCallback() {
        Log.d("Activity", "Got finish survey event");
        showSurveyNotification();
    }
}

