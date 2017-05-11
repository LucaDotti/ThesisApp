package usi.memotion.gathering.gatheringServices;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import android.app.Service;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import usi.memotion.local.database.controllers.LocalStorageController;
import usi.memotion.local.database.controllers.SQLiteController;
import usi.memotion.local.database.tables.PhoneCallLogTable;

/**
 * Created by Luca Dotti on 03/01/17.
 */
public class PhoneCallGatheringService extends Service  {
    private BroadcastReceiver receiver;


    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new PhoneCallEventsReceiver(getApplicationContext());

        IntentFilter filter = new IntentFilter();
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);

        getApplicationContext().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        getApplicationContext().unregisterReceiver(receiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

/**
 * https://gist.github.com/ftvs/e61ccb039f511eb288ee
 */
class PhoneCallEventsReceiver extends BroadcastReceiver {
    private LocalStorageController localStorageController;
    private int prevState;
    private String currentCallerNumber;
    private boolean currentCallIncoming;
    private DateTime currentCallStartTs;
    private String phoneNumber;

    public PhoneCallEventsReceiver(Context context) {
        localStorageController = SQLiteController.getInstance(context);
        currentCallIncoming = false;
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = tMgr.getLine1Number();
        prevState = TelephonyManager.CALL_STATE_IDLE;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        int state;

        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            currentCallerNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        } else {
            state = getState(extras.getString(TelephonyManager.EXTRA_STATE));
            currentCallerNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

            switch(state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    currentCallIncoming = true;
                    currentCallStartTs = new DateTime();
                    prevState = TelephonyManager.CALL_STATE_RINGING;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if(prevState == TelephonyManager.CALL_STATE_RINGING) {
                        //miss
                        DateTime now = new DateTime();
                        insertRecord("missed", new Interval(currentCallStartTs, now).toDurationMillis());
                    } else if(currentCallIncoming) {
                        //incoming ended
                        DateTime now = new DateTime();
                        insertRecord("incoming", new Interval(currentCallStartTs, now).toDurationMillis());
                    } else {
                        //outgoing ended
                        DateTime now = new DateTime();
                        insertRecord("outgoing", new Interval(currentCallStartTs, now).toDurationMillis());
                    }
                    prevState = TelephonyManager.CALL_STATE_RINGING;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if(prevState != TelephonyManager.CALL_STATE_RINGING) {
                        currentCallIncoming = false;
                        currentCallStartTs = new DateTime();
                    }
                    prevState = TelephonyManager.CALL_STATE_OFFHOOK;
                    break;
            }
        }
    }

    private void insertRecord(String direction, long duration) {
        ContentValues record = new ContentValues();

        record.put(PhoneCallLogTable.KEY_CALL_LOG_TS, Long.toString(System.currentTimeMillis()));
        record.put(PhoneCallLogTable.KEY_CALL_LOG_DIRECTION, direction);
        record.put(PhoneCallLogTable.KEY_CALL_LOG_DURATION, Long.toString(duration));

        localStorageController.insertRecord(PhoneCallLogTable.TABLE_CALL_LOG, record);
        Log.d("CALLS SERVICE", "Added record: ts: " + record.get(PhoneCallLogTable.KEY_CALL_LOG_TS) + ", direction: " + record.get(PhoneCallLogTable.KEY_CALL_LOG_DIRECTION) + ", duration: " + record.get(PhoneCallLogTable.KEY_CALL_LOG_DURATION));
    }

    private int getState(String state) {
        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            return TelephonyManager.CALL_STATE_RINGING;
        } else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            return TelephonyManager.CALL_STATE_IDLE;
        } else if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            return TelephonyManager.CALL_STATE_OFFHOOK;
        }

        return -1;
    }
}