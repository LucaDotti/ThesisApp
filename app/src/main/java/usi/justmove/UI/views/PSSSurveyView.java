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
import usi.justmove.local.database.tables.PSSTable;
import usi.justmove.local.database.tables.PWBTable;

/**
 * Created by usi on 07/03/17.
 */

public class PSSSurveyView extends LinearLayout {
    private OnPssCompletedCallback callback;
    private LocalStorageController localController;
    private int currentSurveyId;
    private LocalTables pssTable;

    private DiscreteSeekBar q1Seekbar;
    private DiscreteSeekBar q2Seekbar;
    private DiscreteSeekBar q3Seekbar;
    private DiscreteSeekBar q4Seekbar;
    private DiscreteSeekBar q5Seekbar;
    private DiscreteSeekBar q6Seekbar;
    private DiscreteSeekBar q7Seekbar;
    private DiscreteSeekBar q8Seekbar;
    private DiscreteSeekBar q9Seekbar;
    private DiscreteSeekBar q10Seekbar;
    private Button submiButton;

    public PSSSurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        localController = SQLiteController.getInstance(context);
        pssTable = LocalTables.TABLE_PSS;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pss_layout, this, true);
        init();
    }

    private void init() {
        Cursor survey = getPss();

        if(survey.getCount() > 0) {
            currentSurveyId = survey.getInt(0);
            q1Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPssQ1SeekBar);
            q2Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPssQ2SeekBar);
            q3Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPssQ3SeekBar);
            q4Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPssQ4SeekBar);
            q5Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPssQ5SeekBar);
            q6Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPssQ6SeekBar);
            q7Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPssQ7SeekBar);
            q8Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPssQ8SeekBar);
            q9Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPssQ9SeekBar);
            q10Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysPssQ10SeekBar);

            submiButton = (Button) findViewById(R.id.pssSubmitButton);

            submiButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    savePssSurvey();
                    callback.onPssCompletedCallback();
                    Toast.makeText(getContext(), "Pss survey completed", Toast.LENGTH_SHORT).show();
                }
            });
        }

        survey.close();
    }

    private void savePssSurvey() {
        long timestamp = System.currentTimeMillis();
        int completed = 1;
        ContentValues record = new ContentValues();
        record.put(PSSTable.KEY_PSS_TS, timestamp);
        record.put(PSSTable.KEY_PSS_COMPLETED, completed);
        record.put(PSSTable.KEY_PSS_Q1, q1Seekbar.getProgress());
        record.put(PSSTable.KEY_PSS_Q2, q2Seekbar.getProgress());
        record.put(PSSTable.KEY_PSS_Q3, q3Seekbar.getProgress());
        record.put(PSSTable.KEY_PSS_Q4, q4Seekbar.getProgress());
        record.put(PSSTable.KEY_PSS_Q5, q5Seekbar.getProgress());
        record.put(PSSTable.KEY_PSS_Q6, q6Seekbar.getProgress());
        record.put(PSSTable.KEY_PSS_Q7, q7Seekbar.getProgress());
        record.put(PSSTable.KEY_PSS_Q8, q8Seekbar.getProgress());
        record.put(PSSTable.KEY_PSS_Q9, q9Seekbar.getProgress());
        record.put(PSSTable.KEY_PSS_Q10, q10Seekbar.getProgress());
        localController.update(LocalDbUtility.getTableName(pssTable), record, LocalDbUtility.getTableColumns(pssTable)[0] + " = " + currentSurveyId);
    }

    private Cursor getPss() {
        String tableName = LocalDbUtility.getTableName(pssTable);
        String indexColumn = LocalDbUtility.getTableColumns(pssTable)[0];
        String columnSchedule = LocalDbUtility.getTableColumns(pssTable)[2];
        String columnCompleted = LocalDbUtility.getTableColumns(pssTable)[3];
        String columnNotified = LocalDbUtility.getTableColumns(pssTable)[4];
        String columnExpired = LocalDbUtility.getTableColumns(pssTable)[5];

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

    public interface OnPssCompletedCallback {
        void onPssCompletedCallback();
    }

    public void setCallback(OnPssCompletedCallback callback) {
        this.callback = callback;
    }
}
