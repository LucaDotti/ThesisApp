package usi.justmove.UI.views;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import org.joda.time.LocalDateTime;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import usi.justmove.R;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.local.database.tables.LocalDbUtility;
import usi.justmove.local.database.tables.LocalTables;
import usi.justmove.local.database.tables.PWBTable;

import static android.R.attr.button;

/**
 * Created by usi on 20/02/17.
 */

public class PWBSurveyView extends LinearLayout {
    private OnPwbCompletedCallback callback;
    private LocalStorageController localController;
    private LocalTables pwbTable;

    private DiscreteSeekBar q1Seekbar;
    private DiscreteSeekBar q2Seekbar;
    private DiscreteSeekBar q3Seekbar;
    private DiscreteSeekBar q4Seekbar;
    private DiscreteSeekBar q5Seekbar;
    private DiscreteSeekBar q6Seekbar;
    private DiscreteSeekBar q7Seekbar;
    private DiscreteSeekBar q8Seekbar;
    private Button submiButton;

    private int currentSurveyId;

    public PWBSurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        localController = SQLiteController.getInstance(context);
        pwbTable = LocalTables.TABLE_PWB;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pwb_layout, this, true);
        init();
    }

    private void init() {
        Cursor survey = getTodayPwb();

        if(survey.getCount() > 0) {
            survey.moveToFirst();
            currentSurveyId = survey.getInt(0);

            q1Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPwbQ1SeekBar);
            q2Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPwbQ2SeekBar);
            q3Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPwbQ3SeekBar);
            q4Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPwbQ4SeekBar);
            q5Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPwbQ5SeekBar);
            q6Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPwbQ6SeekBar);
            q7Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPwbQ7SeekBar);
            q8Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPwbQ8SeekBar);

            submiButton = (Button) findViewById(R.id.pwbSubmitButton);

            submiButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    savePwbSurvey();
                    callback.onPwbCompletedCallback();
                    Toast.makeText(getContext(), "Pwb survey completed", Toast.LENGTH_SHORT).show();
                }
            });

            survey.close();
        }
//        invalidate();
    }

    private Cursor getTodayPwb() {
        String tableName = LocalDbUtility.getTableName(pwbTable);
        String indexColumn = LocalDbUtility.getTableColumns(pwbTable)[0];
        String columnSchedule = LocalDbUtility.getTableColumns(pwbTable)[2];
        String columnCompleted = LocalDbUtility.getTableColumns(pwbTable)[3];
        String columnNotified = LocalDbUtility.getTableColumns(pwbTable)[4];
        String columnExpired = LocalDbUtility.getTableColumns(pwbTable)[5];

        LocalDateTime startDateTime = new LocalDateTime().withTime(0, 0, 0, 0);
        LocalDateTime endDateTime = new LocalDateTime().withTime(23, 59, 59, 999);
        long startMillis = startDateTime.toDateTime().getMillis()/1000;
        long endMillis = endDateTime.toDateTime().getMillis()/1000;
        Cursor c = localController.rawQuery("SELECT * FROM " + tableName
                + " WHERE " + columnSchedule + " >= " + startMillis + " AND " + columnSchedule + " <= " + endMillis
                + " AND " + columnCompleted + " = " + 0 + " AND " + columnNotified + " > " + 0 + " AND " + columnExpired + " = " + 0 +
                " ORDER BY " + indexColumn + " ASC LIMIT 1", null);

        return c;
    }

    private void savePwbSurvey() {
        long timestamp = System.currentTimeMillis();
        int completed = 1;
        ContentValues record = new ContentValues();
        record.put(PWBTable.KEY_PWB_TS, timestamp);
        record.put(PWBTable.KEY_PWB_COMPLETED, completed);
        record.put(PWBTable.KEY_PWB_Q1, q1Seekbar.getProgress());
        record.put(PWBTable.KEY_PWB_Q2, q2Seekbar.getProgress());
        record.put(PWBTable.KEY_PWB_Q3, q3Seekbar.getProgress());
        record.put(PWBTable.KEY_PWB_Q4, q4Seekbar.getProgress());
        record.put(PWBTable.KEY_PWB_Q5, q5Seekbar.getProgress());
        record.put(PWBTable.KEY_PWB_Q6, q6Seekbar.getProgress());
        record.put(PWBTable.KEY_PWB_Q7, q7Seekbar.getProgress());
        record.put(PWBTable.KEY_PWB_Q8, q8Seekbar.getProgress());
        localController.update(PWBTable.TABLE_PWB, record, PWBTable.KEY_PWB_ID + " = " + currentSurveyId);
    }

    public interface OnPwbCompletedCallback {
        void onPwbCompletedCallback();
    }

    public void setCallback(OnPwbCompletedCallback callback) {
        this.callback = callback;
    }
}
