package usi.justmove.gathering.gatheringServices;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import usi.justmove.database.base.DbController;
import usi.justmove.database.controllers.LocalDbController;
import usi.justmove.database.tables.CommunicationDirectionTable;
import usi.justmove.database.tables.PhoneCallLogTable;
import usi.justmove.database.tables.SMSTable;
import android.telephony.SmsMessage;
import android.util.Log;


/**
 * Created by Luca Dotti on 03/01/17.
 */
public class SMSGatheringService extends Service  {
    private BroadcastReceiver receiver;
    private ContentObserver outgoingSmSObserver;


    @Override
    public void onCreate() {
        super.onCreate();

        receiver = new IncomingSMSEventsReceiver(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);

        Uri smsUri = Uri.parse("content://sms");
        outgoingSmSObserver = new OutgoingSmsObserver(new Handler(), getApplicationContext(), smsUri);


        getApplicationContext().registerReceiver(receiver, filter);
        getApplicationContext().getContentResolver().registerContentObserver(smsUri, true, outgoingSmSObserver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

/**
 * http://androidexample.com/Incomming_SMS_Broadcast_Receiver_-_Android_Example/index.php?view=article_discription&aid=62
 */
class IncomingSMSEventsReceiver extends BroadcastReceiver {
    private DbController dbController;
    private String phoneNumber;

    public IncomingSMSEventsReceiver(Context context) {
        dbController = new LocalDbController(context, "JustMove");
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = tMgr.getLine1Number();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[])bundle.get("pdus");
                for (int i = 0; i < pdus.length; i++) {
                    String format = bundle.getString("format");
                    SmsMessage msg = SmsMessage.createFromPdu((byte[])pdus[i], format);
                    insertRecord(CommunicationDirectionTable.TYPE_COMMUNICATION_DIRECTION_INCOMING, phoneNumber, msg.getOriginatingAddress());
                }
            }
        }
    }

    private void insertRecord(String direction, String receiverNumber, String senderNumber) {
        List<Map<String, String>> records = new ArrayList<>();
        Map<String, String> record = new HashMap<>();

        record.put(SMSTable.KEY_SMS_ID, null);
        record.put(SMSTable.KEY_SMS_TS, Long.toString(System.currentTimeMillis()));
        record.put(SMSTable.KEY_SMS_DIRECTION, direction);
        record.put(SMSTable.KEY_SMS_RECEIVER_NUMBER, receiverNumber);
        record.put(SMSTable.KEY_SMS_SENDER_NUMBER, senderNumber);

        dbController.insertRecords(SMSTable.TABLE_SMS, records);
        Log.d("CALLS SERVICE", "Added record: ts: " + record.get(SMSTable.KEY_SMS_TS) + ", direction: " + record.get(SMSTable.KEY_SMS_DIRECTION) + ", receiver: " + record.get(SMSTable.KEY_SMS_RECEIVER_NUMBER) + ", sender: " + record.get(SMSTable.KEY_SMS_SENDER_NUMBER));
    }
}

/**
 * https://katharnavas.wordpress.com/2012/01/18/listening-for-outgoing-sms-or-send-sms-in-android/
 * https://github.com/scrack/gbandroid/blob/master/MobileSpy/src/org/ddth/android/monitor/observer/AndroidSmsWatcher.java
 */
class OutgoingSmsObserver extends ContentObserver {
    private DbController dbController;
    private Context context;
    private Uri smsUri;
    private String phoneNumber;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public OutgoingSmsObserver(Handler handler, Context context, Uri smsUri) {
        super(handler);
        this.context = context;
        this.smsUri = smsUri;
        dbController = new LocalDbController(context, "JustMove");
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = tMgr.getLine1Number();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Cursor smsSent = context.getContentResolver().query(smsUri, null, null, null, null);
        smsSent.moveToNext();
        String protocol = smsSent.getString(smsSent.getColumnIndex("protocol"));
        int type = smsSent.getInt(smsSent.getColumnIndex("type"));

        if(protocol == null && type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT) {
            insertRecord(CommunicationDirectionTable.TYPE_COMMUNICATION_DIRECTION_OUTGOING, smsSent.getString(smsSent.getColumnIndex("address")), phoneNumber);
        }
//        while(smsSent.moveToNext()) {
//            String protocol = smsSent.getString(smsSent.getColumnIndex("protocol"));
//            int type = smsSent.getInt(smsSent.getColumnIndex("type"));
//
//            if(protocol == null && type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT) {
//                insertRecord(CommunicationDirectionTable.TYPE_COMMUNICATION_DIRECTION_OUTGOING, smsSent.getString(smsSent.getColumnIndex("address")), phoneNumber);
//            }
//        }
    }

    private void insertRecord(String direction, String receiverNumber, String senderNumber) {
        List<Map<String, String>> records = new ArrayList<>();
        Map<String, String> record = new HashMap<>();

        record.put(SMSTable.KEY_SMS_ID, null);
        record.put(SMSTable.KEY_SMS_TS, Long.toString(System.currentTimeMillis()));
        record.put(SMSTable.KEY_SMS_DIRECTION, direction);
        record.put(SMSTable.KEY_SMS_RECEIVER_NUMBER, receiverNumber);
        record.put(SMSTable.KEY_SMS_SENDER_NUMBER, senderNumber);

        dbController.insertRecords(SMSTable.TABLE_SMS, records);
        Log.d("CALLS SERVICE", "Added record: ts: " + record.get(SMSTable.KEY_SMS_TS) + ", direction: " + record.get(SMSTable.KEY_SMS_DIRECTION) + ", receiver: " + record.get(SMSTable.KEY_SMS_RECEIVER_NUMBER) + ", sender: " + record.get(SMSTable.KEY_SMS_SENDER_NUMBER));
    }
}