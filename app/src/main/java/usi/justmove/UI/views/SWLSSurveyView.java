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
import usi.justmove.local.database.tables.SHSTable;
import usi.justmove.local.database.tables.SWLSTable;

/**
 * Created by usi on 07/03/17.
 */

public class SWLSSurveyView extends LinearLayout{
    private OnSwlsCompletedCallback callback;
    private LocalStorageController localController;
    private int currentSurveyId;
    private LocalTables swlsTable;

    private DiscreteSeekBar q1Seekbar;
    private DiscreteSeekBar q2Seekbar;
    private DiscreteSeekBar q3Seekbar;
    private DiscreteSeekBar q4Seekbar;
    private DiscreteSeekBar q5Seekbar;
    private Button submiButton;

    public SWLSSurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        localController = SQLiteController.getInstance(context);
        swlsTable = LocalTables.TABLE_SHS;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.swls_layout, this, true);

        init();
    }

    private void init() {
        Cursor survey = getSwls();

        if(survey.getCount() > 0) {
            currentSurveyId = survey.getInt(0);
            q1Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysSwlsQ1SeekBar);
            q2Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysSwlsQ2SeekBar);
            q3Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysSwlsQ3SeekBar);
            q4Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysSwlsQ4SeekBar);
            q5Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysSwlsQ5SeekBar);

            submiButton = (Button) findViewById(R.id.swlsSubmitButton);

            submiButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    savePhq8Survey();
                    callback.onSwlsCompletedCallback();
                    Toast.makeText(getContext(), "Swls survey completed", Toast.LENGTH_SHORT).show();
                }
            });
        }

        survey.close();
    }

    private void savePhq8Survey() {
        long timestamp = System.currentTimeMillis();
        int completed = 1;
        ContentValues record = new ContentValues();
        record.put(SWLSTable.KEY_SWLS_TS, timestamp);
        record.put(SWLSTable.KEY_SWLS_COMPLETED, completed);
        record.put(SWLSTable.KEY_SWLS_Q1, q1Seekbar.getProgress());
        record.put(SWLSTable.KEY_SWLS_Q2, q2Seekbar.getProgress());
        record.put(SWLSTable.KEY_SWLS_Q3, q3Seekbar.getProgress());
        record.put(SWLSTable.KEY_SWLS_Q4, q4Seekbar.getProgress());
        record.put(SWLSTable.KEY_SWLS_Q5, q5Seekbar.getProgress());
        localController.update(LocalDbUtility.getTableName(swlsTable), record, LocalDbUtility.getTableColumns(swlsTable)[0] + " = " + currentSurveyId);
    }

    private Cursor getSwls() {
        String tableName = LocalDbUtility.getTableName(swlsTable);
        String indexColumn = LocalDbUtility.getTableColumns(swlsTable)[0];
        String columnSchedule = LocalDbUtility.getTableColumns(swlsTable)[2];
        String columnCompleted = LocalDbUtility.getTableColumns(swlsTable)[3];
        String columnNotified = LocalDbUtility.getTableColumns(swlsTable)[4];
        String columnExpired = LocalDbUtility.getTableColumns(swlsTable)[5];

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

    public interface OnSwlsCompletedCallback {
        void onSwlsCompletedCallback();
    }

    public void setCallback(OnSwlsCompletedCallback callback) {
        this.callback = callback;
    }
}
