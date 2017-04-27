package usi.memotion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
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

import java.util.ArrayList;
import java.util.List;

import usi.memotion.UI.fragments.TabFragmentAdapter;
import usi.memotion.UI.fragments.HomeFragment;
import usi.memotion.gathering.gatheringServices.AccelerometerGatheringService;
import usi.memotion.surveys.config.SurveyType;
import usi.memotion.surveys.handle.NotificationBroadcastReceiver;
import usi.memotion.UI.fragments.SurveysFragment;
import usi.memotion.UI.fragments.SwipeChoiceViewPager;
import usi.memotion.UI.menu.ProfileDialogFragment;
import usi.memotion.UI.menu.StudyDialogFragment;
import usi.memotion.surveys.SurveysService;
import usi.memotion.surveys.handle.SurveyEvent;
import usi.memotion.local.database.controllers.SQLiteController;
import usi.memotion.gathering.GatheringSystem;
import usi.memotion.gathering.SensorType;
import usi.memotion.local.database.tableHandlers.Survey;
import usi.memotion.local.database.tableHandlers.SurveyConfig;
import usi.memotion.local.database.tables.UserTable;
import usi.memotion.remote.database.upload.DataUploadService;

public class MainActivity extends AppCompatActivity implements SurveysFragment.OnSurveyCompletedCallback, HomeFragment.OnRegistrationSurveyChoice, ProfileDialogFragment.OnEnrollStatusUpdate {
    private GatheringSystem gSys;
    private TabLayout tabLayout;
    private SwipeChoiceViewPager viewPager;
    private FragmentPagerAdapter tabFragmentAdapter;
    private android.support.v7.widget.Toolbar toolbar;
    public static boolean running;

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
        tabLayout.setupWithViewPager(viewPager, true);


        tabLayout.getTabAt(1).setCustomView(R.layout.surveys_tab_layout);

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
            boolean user = checkUserRegistered();
            if(user) {
                init(grantedPermissions());
            }
        }
    }

    private void init(List<String> grantedPermissions) {
        gSys = new GatheringSystem(getApplicationContext());

        for(SensorType type: SensorType.values()) {
            if(permissionAreGranted(grantedPermissions, type.getPermissions())) {
                gSys.addSensor(type);
                Log.d("MainActivity", "All permissions granted for sensor " + type);
            } else {
                Log.d("MainActivity", "Not all permissions granted for sensor " + type);
            }
        }

        gSys.start();

//        startService(new Intent(this, DataUploadService.class));
        startService(new Intent(this, SurveysService.class));
    }

    private boolean permissionAreGranted(List<String> grantedPermissions, String[] requiredPermissions) {
        for(String permission: requiredPermissions) {
            if(!grantedPermissions.contains(permission)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_REQUEST_STATUS) {
            if(checkUserRegistered()) {
                init(convertPermissionResultsToList(permissions, grantResults));
            }
        }
    }

    private List<String> convertPermissionResultsToList(String[] permissions, int[] grantResults) {
        List<String> grantedPermissions = new ArrayList<>();

        for(int i = 0; i < permissions.length; i++) {
            if(grantResults[i] >= 0) {
                grantedPermissions.add(permissions[i]);
            }
        }

        return grantedPermissions;
    }
    private List<String> grantedPermissions() {
        List<String> granted = new ArrayList<String>();
        try {
            PackageInfo pi = getPackageManager().getPackageInfo("usi.memotion", PackageManager.GET_PERMISSIONS);
            for (int i = 0; i < pi.requestedPermissions.length; i++) {
                if ((pi.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                    granted.add(pi.requestedPermissions[i]);
                }
            }
        } catch (Exception e) {
        }
        return granted;
    }
    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkUserRegistered() {
        Cursor c = SQLiteController.getInstance(this).rawQuery("SELECT * FROM " + UserTable.TABLE_USER, null);

        if(c.getCount() == 0) {
            return false;
        }

        c.moveToFirst();
        boolean agreed = c.getInt(2) == 0 ? false : true;
        return agreed;
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

        if(event.isScheduled()) {
            String surveyFragmentTag = makeFragmentName(viewPager.getId(), 1);
            SurveysFragment fragment = (SurveysFragment) getSupportFragmentManager().findFragmentByTag(surveyFragmentTag);
            fragment.resetSurvey(s.surveyType);
        }

        showSurveyNotification();
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
    }

    @Override
    public void onSurveyCompletedCallback() {
        showSurveyNotification();
    }

    @Override
    public void onRegistrationSurveyChoice(boolean now) {
        init(grantedPermissions());
        if(now) {
            viewPager.setCurrentItem(1);
        } else {
            SurveyConfig config = SurveyConfig.getConfig(SurveyType.GROUPED_SSPP);
            config.immediate = false;
            config.save();
        }

        showSurveyNotification();
    }

    private static String makeFragmentName(int viewPagerId, int index) {
        return "android:switcher:" + viewPagerId + ":" + index;
    }

    @Override
    public void onEnrollStatusUpdate(boolean exit) {
        tabFragmentAdapter.notifyDataSetChanged();
        if(exit) {
            gSys.stopServices();
            stopService(new Intent(this, DataUploadService.class));
            stopService(new Intent(this, SurveysService.class));
        } else {
            init(grantedPermissions());
        }
    }
}

