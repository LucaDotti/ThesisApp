package usi.justmove.gathering.surveys.handle;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import usi.justmove.MainActivity;
import usi.justmove.R;
import usi.justmove.gathering.surveys.Surveys;
import usi.justmove.gathering.surveys.config.SurveyConfig;
import usi.justmove.gathering.surveys.config.SurveyConfigFactory;
import usi.justmove.gathering.surveys.inspection.SurveyEvent;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.local.database.tables.PAMTable;
import android.os.Vibrator;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by usi on 15/02/17.
 */

public class PAMSurveyHandler implements SurveyHandler {
    private Context context;
    private Notification notification;
    private int notificationID;
    private LocalStorageController localController;

    public PAMSurveyHandler(Context context) {
        this.context = context;
        notificationID = 0;
        localController = SQLiteController.getInstance(context);
    }

    @Override
    public void handle(SurveyEvent event) {
        cancelNotification();
        if(event.getStatus() == SurveyEvent.SCHEDULED) {
            createNotification(event.getRecordId());
        }

        EventBus.getDefault().post(event);
    }

    private void cancelNotification() {
        if(notificationID < 0) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(notificationID);
            notificationID = 0;
        }

    }

    private String getMissingTime(int surveyId) {
        SurveyConfig config = SurveyConfigFactory.getConfig(Surveys.PAM, context);
        Cursor survey = localController.rawQuery("SELECT * FROM " + PAMTable.TABLE_PAM + " WHERE " + PAMTable.KEY_PAM_ID + " = " + surveyId, null);
        survey.moveToNext();
        int notificationCount = survey.getInt(4);
        long elapseTimeBetweenPersistentNotifications = (config.maxElapseTimeForCompletion/config.notificationsCount);
        long missingTime = (config.notificationsCount - notificationCount)*elapseTimeBetweenPersistentNotifications;
        String split[] = formatMillis(missingTime).split("\\.");
        survey.close();
        return split[0];
    }

    /**
     * http://stackoverflow.com/questions/6710094/how-to-format-an-elapsed-time-interval-in-hhmmss-sss-format-in-java
     * @param val
     * @return
     */
    static public String formatMillis(long val) {
        StringBuilder buf = new StringBuilder(20);
        String sgn = "";

        if(val < 0) {
            sgn = "-";
            val = Math.abs(val);
        }

        append(buf, sgn, 0, ( val/3600000             ));
        append(buf, ":", 2, ((val%3600000)/60000      ));
        append(buf, ":", 2, ((val         %60000)/1000));
        append(buf, ".", 3, ( val                %1000));
        return buf.toString();
    }


    static private void append(StringBuilder tgt, String pfx, int dgt, long val) {
        tgt.append(pfx);
        if(dgt>1) {
            int pad=(dgt-1);
            for(long xa=val; xa>9 && pad>0; xa/=10) { pad--;           }
            for(int  xa=0;   xa<pad;        xa++  ) { tgt.append('0'); }
        }
        tgt.append(val);
    }

    private void createNotification(int surveyId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.survey_notification_layout);

        String missingTime = getMissingTime(surveyId);
        String[] splitTime = missingTime.split(":");
        String timeUnit;
        if(Integer.parseInt(splitTime[0]) != 0) {
            timeUnit = " hours";
        } else if(Integer.parseInt(splitTime[1]) != 0) {
            timeUnit = " minutes";
        } else {
            timeUnit = " seconds";
        }
        remoteViews.setTextViewText(R.id.notificationContent, "New daily PAM survey available");
        remoteViews.setTextViewText(R.id.notificationMissingTime, "Time left \t" + missingTime + timeUnit);

        if(notificationID >= 0) {
            notificationID = (int) System.currentTimeMillis();
        }

        Intent notificationNowButton = new Intent(SurveysHandleSystem.NOTIFICATION_INTENT);
        notificationNowButton.putExtra("id", notificationID);
        notificationNowButton.putExtra("action", "now");
        PendingIntent pButtonNowIntent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), notificationNowButton,0);
        remoteViews.setOnClickPendingIntent(R.id.notificationButtonNow, pButtonNowIntent);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setCustomBigContentView(remoteViews)
                        .setContentTitle("JustMove")
                        .setSubText("New survey available!");
        Intent resultIntent = new Intent();
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notificationID, mBuilder.build());
        Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
    }
}
