package usi.justmove.gathering.user.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import usi.justmove.gathering.base.StateMachine;
import usi.justmove.gathering.user.Surveys;

/**
 * Created by usi on 07/02/17.
 */
public class ScheduleSystem implements Runnable {
    private Scheduler[] schedulers;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private Context context;


    public ScheduleSystem(Scheduler[] schedulers, Context context) {
        this.schedulers = schedulers;
    }

//    public void schedule() {
//        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//
//        // Set the alarm to start at 21:32 PM
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 21);
//        calendar.set(Calendar.MINUTE, 32);
//
//// setRepeating() lets you specify a precise custom interval--in this case,
//// 1 day
//        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, alarmIntent);
//    }
    @Override
    public void run() {
        Log.d("SURVEY SERVICE", "SCHEDULING...");
        for(int i = 0; i < schedulers.length; i++) {
            schedulers[i].schedule();
        }
    }
}
