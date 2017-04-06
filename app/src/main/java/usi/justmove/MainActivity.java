package usi.justmove;

import android.Manifest;
import android.content.BroadcastReceiver;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import net.danlew.android.joda.JodaTimeAndroid;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import usi.justmove.UI.TabFragmentAdapter;
import usi.justmove.UI.fragments.HomeFragment;
import usi.justmove.gathering.surveys.config.SurveyType;
import usi.justmove.gathering.surveys.handle.NotificationBroadcastReceiver;
import usi.justmove.UI.fragments.SurveysFragment;
import usi.justmove.UI.fragments.SwipeChoiceViewPager;
import usi.justmove.UI.menu.ProfileDialogFragment;
import usi.justmove.UI.menu.StudyDialogFragment;
import usi.justmove.gathering.surveys.SurveysService;
import usi.justmove.gathering.surveys.handle.SurveyEvent;
import usi.justmove.local.database.LocalSQLiteDBHelper;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.gathering.GatheringSystem;
import usi.justmove.gathering.base.SensorType;
import usi.justmove.local.database.tableHandlers.Survey;
import usi.justmove.local.database.tables.UserTable;
import usi.justmove.remote.database.upload.DataUploadService;

import static android.R.attr.fragment;

public class MainActivity extends AppCompatActivity implements SurveysFragment.OnSurveyCompletedCallback, HomeFragment.OnRegistrationSurveyChoice {
    private GatheringSystem gSys;
    private TabLayout tabLayout;
    private SwipeChoiceViewPager viewPager;
    private FragmentPagerAdapter tabFragmentAdapter;
    private android.support.v7.widget.Toolbar toolbar;

    private final int PERMISSION_REQUEST_STATUS = 0;

    private BroadcastReceiver surveyEventReveicer;

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
        tabLayout.setupWithViewPager(viewPager, true);


        tabLayout.getTabAt(1).setCustomView(R.layout.surveys_tab_layout);

//        deleteDatabase("JustMove");
//        LocalSQLiteDBHelper dbHelper = new LocalSQLiteDBHelper(this);
//        dbHelper.getWritableDatabase();
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
            viewPager.setCurrentItem(1);
        } 

        SQLiteController.getInstance(this);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        startService(new Intent(this, SurveysService.class));
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

            if(checkUserRegistered()) {
                init();

            }
//            init();
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
        gSys.addSensor(SensorType.USED_APPS);
        gSys.start();

        startService(new Intent(this, DataUploadService.class));
        startService(new Intent(this, SurveysService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_REQUEST_STATUS) {
            if(checkUserRegistered()) {
                init();
            }

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

    private boolean checkUserRegistered() {
        Cursor c = SQLiteController.getInstance(this).rawQuery("SELECT * FROM " + UserTable.TABLE_USER, null);

        if(c.getCount() == 0) {
            return false;
        } else  {
            c.moveToFirst();
            return c.getInt(2) == 0 ? true : false;
        }
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
        Survey s = (Survey) Survey.findByPk(event.getRecordId());



        String surveyFragmentTag = makeFragmentName(viewPager.getId(), 1);
        SurveysFragment fragment = (SurveysFragment) getSupportFragmentManager().findFragmentByTag(surveyFragmentTag);
        fragment.resetSurvey(s.surveyType);
//        tabLayout.getTabAt(1).setCustomView(R.layout.surveys_tab_layout);
        showSurveyNotification();
//        tabLayout.setViewPa
//        tabFragmentAdapter.notifyDataSetChanged();
    }

    private void showSurveyNotification() {
        int count = Survey.getAllAvailableSurveysCount();

        View notificationView = tabLayout.getTabAt(1).getCustomView();

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
                case 4:
                    image.setImageResource(R.drawable.notification_4);
                    break;
                case 5:
                    image.setImageResource(R.drawable.notification_5);
                    break;
                case 6:
                    image.setImageResource(R.drawable.notification_6);
                    break;
                default:
            }
        }

//        notificationView.invalidate();
    }

    @Override
    public void onSurveyCompletedCallback() {
        showSurveyNotification();
    }

    @Override
    public void onRegistrationSurveyChoice(boolean now) {
        init();
        if(now) {
//            tabLayout.getTabAt(1).setCustomView(R.layout.surveys_tab_layout);
            viewPager.setCurrentItem(1);
        }

        showSurveyNotification();
//        tabFragmentAdapter.notifyDataSetChanged();
    }

    private static String makeFragmentName(int viewPagerId, int index) {
        return "android:switcher:" + viewPagerId + ":" + index;
    }
}

