package usi.memotion.surveys.handle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import usi.memotion.MainActivity;

/**
 * Created by usi on 11/02/17.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    public final static String OPEN_SURVEYS_ACTION = "open_surveys_action";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NOTI", "RECEIVED");
//        NotificationManager mgr = (NotificationManager)  context.getSystemService(Context.NOTIFICATION_SERVICE);
//        String action = intent.getStringExtra("action");
//        int id = intent.getIntExtra("id", 0);
//        mgr.cancel(id);
//        if(id < 0) {
            Intent i = new Intent(context, MainActivity.class);
            i.setAction(OPEN_SURVEYS_ACTION);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_LAUNCHER);

            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

            context.sendBroadcast(it);
            context.startActivity(i);
//        }
    }
}
