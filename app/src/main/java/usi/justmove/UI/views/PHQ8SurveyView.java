package usi.justmove.UI.views;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.joda.time.LocalDateTime;

import usi.justmove.R;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.local.database.tables.LocalDbUtility;
import usi.justmove.local.database.tables.LocalTables;
import usi.justmove.local.database.tables.PHQ8Table;

/**
 * Created by usi on 07/03/17.
 */

public class PHQ8SurveyView extends LinearLayout {
    private OnPhq8CompletedCallback callback;
    private LocalStorageController localController;
    private int currentSurveyId;
    private LocalTables phq8Table;

    private DiscreteSeekBar q1Seekbar;
    private DiscreteSeekBar q2Seekbar;
    private DiscreteSeekBar q3Seekbar;
    private DiscreteSeekBar q4Seekbar;
    private DiscreteSeekBar q5Seekbar;
    private DiscreteSeekBar q6Seekbar;
    private DiscreteSeekBar q7Seekbar;
    private DiscreteSeekBar q8Seekbar;
    private Button submiButton;

    public PHQ8SurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        localController = SQLiteController.getInstance(context);
        phq8Table = LocalTables.TABLE_PHQ8;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.phq8_layout, this, true);

        init();
    }

    private void init() {
        Cursor survey = getPhq8();

        if(survey.getCount() > 0) {
            currentSurveyId = survey.getInt(0);
            q1Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPhq8Q1SeekBar);
            q2Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPhq8Q2SeekBar);
            q3Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPhq8Q3SeekBar);
            q4Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPhq8Q4SeekBar);
            q5Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPhq8Q5SeekBar);
            q6Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPhq8Q6SeekBar);
            q7Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPhq8Q7SeekBar);
            q8Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPhq8Q8SeekBar);

            submiButton = (Button) findViewById(R.id.phq8SubmitButton);

            submiButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    savePhq8Survey();
                    callback.onPhq8CompletedCallback();
                    Toast.makeText(getContext(), "Phq8 survey completed", Toast.LENGTH_SHORT).show();
                }
            });
        }

        survey.close();
    }

    private void savePhq8Survey() {
        long timestamp = System.currentTimeMillis();
        int completed = 1;
        ContentValues record = new ContentValues();
        record.put(PHQ8Table.KEY_PHQ8_TS, timestamp);
        record.put(PHQ8Table.KEY_PHQ8_COMPLETED, completed);
        record.put(PHQ8Table.KEY_PHQ8_Q1, q1Seekbar.getProgress());
        record.put(PHQ8Table.KEY_PHQ8_Q2, q2Seekbar.getProgress());
        record.put(PHQ8Table.KEY_PHQ8_Q3, q3Seekbar.getProgress());
        record.put(PHQ8Table.KEY_PHQ8_Q4, q4Seekbar.getProgress());
        record.put(PHQ8Table.KEY_PHQ8_Q5, q5Seekbar.getProgress());
        record.put(PHQ8Table.KEY_PHQ8_Q6, q6Seekbar.getProgress());
        record.put(PHQ8Table.KEY_PHQ8_Q7, q7Seekbar.getProgress());
        record.put(PHQ8Table.KEY_PHQ8_Q8, q8Seekbar.getProgress());
        localController.update(LocalDbUtility.getTableName(phq8Table), record, LocalDbUtility.getTableColumns(phq8Table)[0] + " = " + currentSurveyId);
    }

    private Cursor getPhq8() {
        String tableName = LocalDbUtility.getTableName(phq8Table);
        String indexColumn = LocalDbUtility.getTableColumns(phq8Table)[0];
        String columnSchedule = LocalDbUtility.getTableColumns(phq8Table)[2];
        String columnCompleted = LocalDbUtility.getTableColumns(phq8Table)[3];
        String columnNotified = LocalDbUtility.getTableColumns(phq8Table)[4];
        String columnExpired = LocalDbUtility.getTableColumns(phq8Table)[5];

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

    public interface OnPhq8CompletedCallback {
        void onPhq8CompletedCallback();
    }

    public void setCallback(OnPhq8CompletedCallback callback) {
        this.callback = callback;
    }
}
