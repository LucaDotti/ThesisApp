package usi.justmove.gathering.user;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import usi.justmove.MainActivity;
import usi.justmove.R;
import usi.justmove.UI.SurveyEvent;
import usi.justmove.gathering.base.StateMachine;
import usi.justmove.gathering.strategies.timebased.TimeBasedSMState;
import usi.justmove.gathering.user.scheduler.PAMScheduler;
import usi.justmove.gathering.user.scheduler.PWBScheduler;
import usi.justmove.gathering.user.scheduler.ScheduleSystem;
import usi.justmove.gathering.user.scheduler.Scheduler;
import usi.justmove.gathering.user.strategies.schedulebased.ScheduleBasedInputProvider;
import usi.justmove.gathering.user.strategies.schedulebased.ScheduleBasedState;
import usi.justmove.gathering.user.strategies.schedulebased.ScheduleBasedStateMachineListener;
import usi.justmove.gathering.user.strategies.schedulebased.ScheduleBasedSymbol;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.local.database.tables.LocalDbUtility;
import usi.justmove.local.database.tables.LocalTables;
import usi.justmove.local.database.tables.PAMTable;
import usi.justmove.local.database.tables.PWBTable;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import static android.R.attr.id;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.os.Build.VERSION_CODES.N;

/**
 * Created by usi on 06/02/17.
 */

public class SurveysService extends Service {
    private final static String NOTIFICATION_INTENT = "notification";


    private ScheduleSystem scheduleSys;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private StateMachine<ScheduleBasedState, ScheduleBasedSymbol> pamStateMachine;
    private StateMachine<ScheduleBasedState, ScheduleBasedSymbol> pwbStateMachine;

    private Thread pamStateMachineThread;
    private Thread pwbStateMachineThread;

    public class SurveyBinder extends Binder {
        public SurveysService getService() {
            return SurveysService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("SURVEYS SERVICE", "STARTED");

        long stateMachineFreq = 1000;
        ScheduleBasedState[][] transitions = new ScheduleBasedState[3][3];
        transitions[ScheduleBasedState.WAIT.ordinal()][ScheduleBasedSymbol.WAIT.ordinal()] = ScheduleBasedState.WAIT;
        transitions[ScheduleBasedState.WAIT.ordinal()][ScheduleBasedSymbol.NOTIFY.ordinal()] = ScheduleBasedState.NOTIFY;
        transitions[ScheduleBasedState.NOTIFY.ordinal()][ScheduleBasedSymbol.NOTIFY.ordinal()] = ScheduleBasedState.NOTIFY;
        transitions[ScheduleBasedState.NOTIFY.ordinal()][ScheduleBasedSymbol.WAIT.ordinal()] = ScheduleBasedState.WAIT;

        PAMScheduler pamScheduler = new PAMScheduler(
                LocalTables.TABLE_PAM,
                new SQLiteController(getApplicationContext()),
                getApplicationContext().getString(R.string.PAM_start),
                getApplicationContext().getString(R.string.PAM_afternoon),
                getApplicationContext().getString(R.string.PAM_end),
                Long.parseLong(getApplicationContext().getString(R.string.PAM_min_elapse_time_between_surveys)),
                Long.parseLong(getApplicationContext().getString(R.string.PAM_max_elapse_time_for_completition)));
        PWBScheduler pwbScheduler = new PWBScheduler(
                LocalTables.TABLE_PWB,
                new SQLiteController(getApplicationContext()),
                getApplicationContext().getString(R.string.PWB_start),
                getApplicationContext().getString(R.string.PWB_end),
                Long.parseLong(getApplicationContext().getString(R.string.PWB_max_elapse_time_for_completition)),
                getApplicationContext().getString(R.string.PWB_week_period)
        );

        Scheduler[] schedulers = {pamScheduler, pwbScheduler};
        scheduleSys = new ScheduleSystem(schedulers, getApplicationContext());

        //the schedule might not be executed always in the morning! SHOULD BE FIXED
        scheduler.scheduleAtFixedRate(scheduleSys, 0, 24, TimeUnit.HOURS);

        pamStateMachine = new StateMachine<>(
                new PAMScheduleBasedInputProvider(LocalTables.TABLE_PAM, new SQLiteController(getApplicationContext()), Integer.parseInt(getApplicationContext().getString(R.string.PAM_notifications_count)), Long.parseLong(getApplicationContext().getString(R.string.PAM_max_elapse_time_for_completition))),
                transitions,
                ScheduleBasedState.WAIT,
                stateMachineFreq);
        pamStateMachine.addObserver(new PAMScheduleBasedStateMachineListener());
        pwbStateMachine = new StateMachine<>(new PWBScheduleBasedInputProvider(LocalTables.TABLE_PWB, new SQLiteController(getApplicationContext())), transitions, ScheduleBasedState.WAIT, stateMachineFreq);
        pwbStateMachine.addObserver(new PWBScheduleBasedStateMachineListener());

        pamStateMachineThread = new Thread(pamStateMachine);
        pwbStateMachineThread = new Thread(pwbStateMachine);

        pamStateMachineThread.start();
        pwbStateMachineThread.start();
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SurveyEvent event) {
        Log.d("EVENT", "GOT");
        createNotification();

    }

    private void createNotification() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.survey_notification_layout);
        remoteViews.setTextViewText(R.id.notificationTitle, "JustMove");
        remoteViews.setTextViewText(R.id.notificationContent, "New survey available");

        int id = (int) System.currentTimeMillis();
        Intent notificationNowButton = new Intent(NOTIFICATION_INTENT);
        notificationNowButton.putExtra("id", id);
        notificationNowButton.putExtra("action", "now");
        PendingIntent pButtonNowIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) System.currentTimeMillis(), notificationNowButton,0);
        remoteViews.setOnClickPendingIntent(R.id.notificationButtonNow, pButtonNowIntent);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setCustomBigContentView(remoteViews)
                .setContentTitle("JustMove")
                .setSubText("New survey available!");
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, mBuilder.build());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

class PAMScheduleBasedInputProvider extends ScheduleBasedInputProvider {

    private LocalTables pamTable;
    private LocalStorageController localController;
    private int maxNbNotifications;
    private long maxElapseTimeForCompletion;

    public PAMScheduleBasedInputProvider(LocalTables pamTable, LocalStorageController localController, int maxNbNotifications, long maxElapseTimeForCompletion) {
        this.pamTable = pamTable;
        this.localController = localController;
        this.maxNbNotifications = maxNbNotifications;
        this.maxElapseTimeForCompletion = maxElapseTimeForCompletion;
    }

    private Cursor getTodaySurveys() {
        String tableName = LocalDbUtility.getTableName(pamTable);
        String columnTS = LocalDbUtility.getTableColumns(pamTable)[2];
        String columnCompleted = LocalDbUtility.getTableColumns(pamTable)[3];
        String columnNotified = LocalDbUtility.getTableColumns(pamTable)[13];
        LocalDateTime startDateTime = new LocalDateTime().withTime(0, 0, 0, 0);
        LocalDateTime endDateTime = new LocalDateTime().withTime(23, 59, 59, 999);
        long startMillis = startDateTime.toDateTime().getMillis()/1000;
        long endMillis = endDateTime.toDateTime().getMillis()/1000;
        Cursor c = localController.rawQuery("SELECT * FROM " + tableName
                + " WHERE " + columnTS + " >= " + startMillis + " AND " + columnTS + " <= " + endMillis
                + " AND " + columnCompleted + " = " + 0 + " AND " + columnNotified + " <= " + maxNbNotifications, null);

        return c;
    }


    private boolean checkSurveys(Cursor surveys) {
        int completed;
        while(surveys.moveToNext()) {
            completed = surveys.getInt(3);

            if(completed == 0 && checkNeedNotify(surveys.getInt(2))) {
                incrementNotified(surveys.getInt(0), surveys.getInt(13));

                return true;
            }
        }
        return false;
    }

    private boolean checkNeedNotify(long scheduleTime) {
        long elapseTimeBetweenPersistentNotifications = maxElapseTimeForCompletion/maxNbNotifications;
        DateTime now = new DateTime().plusHours(1);
        long nowMillis = now.getMillis()/1000;

        for(int i = 0; i < maxNbNotifications; i++) {
            Log.d("PAM", "NOW " + nowMillis);
            Log.d("PAM", "SCHEDULE " + scheduleTime);
            Log.d("PAM", "Elapse " + maxElapseTimeForCompletion);
            if(Math.abs(((scheduleTime + i*elapseTimeBetweenPersistentNotifications) - nowMillis)*1000) <= 1000 && nowMillis >= scheduleTime + i*elapseTimeBetweenPersistentNotifications - 1000) {
                return true;
            }
        }

        return false;
    }

    private void incrementNotified(int surveyId, int notificationsCount) {
        ContentValues record = new ContentValues();
        record.put(PAMTable.KEY_PAM_NOTIFED, notificationsCount+1);

        localController.update(LocalDbUtility.getTableName(pamTable), record, PAMTable.KEY_PAM_ID + " = " + surveyId);
    }

    @Override
    protected boolean checkSchedule() {
        Cursor surveys = getTodaySurveys();
        if(surveys.getCount() == 0) {
            return false;
        }

        return checkSurveys(surveys);
    }
}

class PAMScheduleBasedStateMachineListener extends ScheduleBasedStateMachineListener {

    @Override
    protected void processWaitState() {
        //Do nothing
        Log.d("PAM", "WAIT");
    }

    @Override
    protected void processNotifyState() {
        Log.d("PAM", "NOTIFY");
        EventBus.getDefault().post(new SurveyEvent());
    }
}

class PWBScheduleBasedInputProvider extends ScheduleBasedInputProvider {

    private LocalTables pwbTable;
    private LocalStorageController localController;
    private int currentSurveyId;

    public PWBScheduleBasedInputProvider(LocalTables pwbTable, LocalStorageController localController) {
        this.pwbTable = pwbTable;
        this.localController = localController;
    }

    private Cursor getWeekSurveys() {
        String tableName = LocalDbUtility.getTableName(pwbTable);
        String columnTS = LocalDbUtility.getTableColumns(pwbTable)[1];
        String columnCompleted = LocalDbUtility.getTableColumns(pwbTable)[3];
        String columnNotified = LocalDbUtility.getTableColumns(pwbTable)[4];
        LocalDateTime startDateTime = new LocalDateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTime(0, 0, 0, 0);
        LocalDateTime endDateTime = new LocalDateTime().withDayOfWeek(DateTimeConstants.SUNDAY).withTime(23, 59, 59, 999);
        long startMillis = startDateTime.toDateTime().getMillis()/1000;
        long endMillis = endDateTime.toDateTime().getMillis()/1000;
        Cursor c = localController.rawQuery("SELECT * FROM " + tableName
                + " WHERE " + columnTS + " >= " + startMillis + " AND " + columnTS + " <= " + endMillis
                + " AND " + columnCompleted + " = " + 0 + " AND " + columnNotified + " = " + 0, null);

        return c;
    }

    private boolean checkSurveysCompleted(Cursor surveys) {
        int completed;
        while(surveys.moveToNext()) {
            completed = surveys.getInt(3);

            if(completed == 0 && checkNeedNotify(surveys.getInt(2))) {
                currentSurveyId = surveys.getInt(0);
                return true;
            }
        }
        return false;
    }

    private boolean checkNeedNotify(long scheduleTime) {
        DateTime now = new DateTime().plusHours(1);
        long nowMillis = now.getMillis()/1000;
        return Math.abs((scheduleTime-nowMillis)*1000) <= 1000;
    }

    private void updateNotified() {
        ContentValues record = new ContentValues();
        record.put(PWBTable.KEY_PWB_NOTIFIED, 1);

        localController.update(LocalDbUtility.getTableName(pwbTable), record, PWBTable.KEY_PWB_ID + " = " + currentSurveyId);
    }


    @Override
    protected boolean checkSchedule() {
        Cursor weekSurveys = getWeekSurveys();

        if(weekSurveys.getCount() == 0) {
            int a = 1;
            return false;
        }

        boolean notify = checkSurveysCompleted(weekSurveys);

        if(notify) {
            updateNotified();
            return true;
        } else {
            return false;
        }
    }
}

class PWBScheduleBasedStateMachineListener extends ScheduleBasedStateMachineListener {

    @Override
    protected void processWaitState() {
        Log.d("PWB", "WAIT");
    }

    @Override
    protected void processNotifyState() {
        Log.d("PWB", "NOTIFY");
        EventBus.getDefault().post(new SurveyEvent());
    }
}


