package usi.justmove.UI.fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;

import usi.justmove.MainActivity;
import usi.justmove.R;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

/**
 * Created by usi on 11/02/17.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    public final static String OPEN_SURVEYS_ACTION = "open_surveys_action";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NOTI", "RECEIVED");
        NotificationManager mgr = (NotificationManager)  context.getSystemService(Context.NOTIFICATION_SERVICE);
        String action = intent.getStringExtra("action");
        int id = intent.getIntExtra("id", -1);

        if(id >= 0) {
            Intent i = new Intent(context, MainActivity.class);
            i.setAction(OPEN_SURVEYS_ACTION);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_LAUNCHER);

            mgr.cancel(1);
            context.startActivity(i);
        }
    }
}
