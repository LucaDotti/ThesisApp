package usi.justmove.gathering.surveys.handle;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import org.greenrobot.eventbus.EventBus;

import usi.justmove.MainActivity;
import usi.justmove.MyApplication;
import usi.justmove.R;
import usi.justmove.gathering.surveys.config.SurveyConfig;
import usi.justmove.gathering.surveys.config.SurveyConfigFactory;
import usi.justmove.gathering.surveys.schedulation.Scheduler;
import usi.justmove.local.database.tableHandlers.Survey;
import usi.justmove.local.database.tableHandlers.SurveyAlarmSurvey;
import usi.justmove.local.database.tableHandlers.SurveyAlarms;
import usi.justmove.local.database.tableHandlers.TableHandler;
import usi.justmove.local.database.tables.SurveyAlarmSurveyTable;
import usi.justmove.local.database.tables.SurveyTable;

import static android.R.attr.y;
import static usi.justmove.gathering.surveys.handle.SurveyEventReceiver.NOTIFICATION_INTENT;
import static usi.justmove.gathering.surveys.handle.SurveyEventReceiver.SURVEY_COMPLETED_INTENT;

/**
 * Created by usi on 14/03/17.
 */

public class SurveyNotifier {
    private Context context;
    private int notificationID;
    private Survey currSurvey;

    public SurveyNotifier() {
        this.context = MyApplication.getContext();
        notificationID = 0;
    }

    public void notify(long surveyId, String action) {
        Log.d("Notifier", "NOTIFYY");
        if(surveyId >= 0) {
            currSurvey = (Survey) Survey.findByPk(surveyId);
            if(currSurvey != null) {
                if(action.equals(SURVEY_COMPLETED_INTENT)) {
                    currSurvey.completed = true;
                    currSurvey.save();

                    cancelNotification((int) currSurvey.id);

                    notifyCancelAlarm();
                    EventBus.getDefault().post(new SurveyEvent(surveyId, false));
                } else {

                    SurveyConfig config = SurveyConfigFactory.getConfig(currSurvey.surveyType, context);

                    if(currSurvey.completed) {
                        return;
                    }

                    currSurvey.notified++;
                    if(currSurvey.notified >= config.notificationsCount) {
                        cancelNotification((int) currSurvey.id);

                        currSurvey.expired = true;
                        currSurvey.save();

                        notifyCancelAlarm();
                        EventBus.getDefault().post(new SurveyEvent(surveyId, true));
                        return;
                    }

                    createNotification(currSurvey, config);

                    currSurvey.save();

                    EventBus.getDefault().post(new SurveyEvent(surveyId, true));
                }
            }

        }
    }

    private void notifyCancelAlarm() {
        SurveyAlarms alarm = SurveyAlarmSurvey.getAlarm(currSurvey.id);
        Survey[] surveys = SurveyAlarmSurvey.getSurveys(alarm.id);

        TableHandler[] t = Survey.findAll("*", "");

        if(checkSurveysCompleted(surveys)) {
            Scheduler.getInstance().deleteAlarm((int) alarm.id);
        }

//        SurveyAlarms currentAlarm = SurveyAlarms.getCurrentAlarm(currSurvey.surveyType);

    }

    private boolean checkSurveysCompleted(Survey[] surveys) {
        for(Survey s: surveys) {
            if(s != null && !s.completed) {
                return false;
            }
        }

        return true;
    }

    private void cancelNotification(int id) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id);
    }

    private void createNotification(Survey survey, SurveyConfig config) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.survey_notification_layout);

        String missingTime = getMissingTime(survey, config);
        String[] splitTime = missingTime.split(":");
        String timeUnit;
        if(Integer.parseInt(splitTime[0]) != 0) {
            timeUnit = " hours";
            int hours = Integer.parseInt(splitTime[0]);
            if(hours > 24) {
                missingTime = "" + hours;

                if(hours > 1) {
                    timeUnit = " days";
                } else {
                    timeUnit = " day";
                }

            }
        } else if(Integer.parseInt(splitTime[1]) != 0) {
            timeUnit = " minutes";
        } else {
            timeUnit = " seconds";
        }
        remoteViews.setTextViewText(R.id.notificationContent, "New daily " + config.survey.getSurveyName() + " survey available");
        remoteViews.setTextViewText(R.id.notificationMissingTime, "Time left \t" + missingTime + timeUnit);

        notificationID = (int) survey.id;
//        if(notificationID >= 0) {
//            notificationID = (int) System.currentTimeMillis();
//        }

//        Intent notificationNowButton = new Intent(NOTIFICATION_INTENT);
//        notificationNowButton.putExtra("id", notificationID);
//        notificationNowButton.putExtra("action", "now");
//        PendingIntent pButtonNowIntent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), notificationNowButton,0);
//        remoteViews.setOnClickPendingIntent(R.id.notificationButtonNow, pButtonNowIntent);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.stat_sys_warning)
//                        .setCustomBigContentView(remoteViews)
                        .setContentTitle("JustMove")
                        .setContentText("New " + config.survey.getSurveyName() + " survey available!")
                        .setSubText("Time left \t" + missingTime + timeUnit);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);


        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notificationID, mBuilder.build());
    }

    private String getMissingTime(Survey survey, SurveyConfig config) {
        int notificationCount = survey.notified;
        long elapseTimeBetweenPersistentNotifications = (config.maxElapseTimeForCompletion/config.notificationsCount);
        long missingTime = (config.notificationsCount - notificationCount)*elapseTimeBetweenPersistentNotifications;
        String split[] = formatMillis(missingTime).split("\\.");
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
}
