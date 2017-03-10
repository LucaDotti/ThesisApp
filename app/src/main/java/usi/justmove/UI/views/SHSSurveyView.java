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
import usi.justmove.local.database.tables.SHSTable;

/**
 * Created by usi on 07/03/17.
 */

public class SHSSurveyView extends LinearLayout {
    private OnShsCompletedCallback callback;
    private LocalStorageController localController;
    private int currentSurveyId;
    private LocalTables shsTable;

    private DiscreteSeekBar q1Seekbar;
    private DiscreteSeekBar q2Seekbar;
    private DiscreteSeekBar q3Seekbar;
    private DiscreteSeekBar q4Seekbar;
    private Button submiButton;

    public SHSSurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        localController = SQLiteController.getInstance(context);
        shsTable = LocalTables.TABLE_SHS;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.shs_layout, this, true);

        init();
    }

    private void init() {
        Cursor survey = getShs();

        if(survey.getCount() > 0) {
            currentSurveyId = survey.getInt(0);
            q1Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysShsQ1SeekBar);
            q2Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysShsQ2SeekBar);
            q3Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysShsQ3SeekBar);
            q4Seekbar = (DiscreteSeekBar) findViewById(R.id.surveysShsQ4SeekBar);

            submiButton = (Button) findViewById(R.id.shsSubmitButton);

            submiButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    savePhq8Survey();
                    callback.onShsCompletedCallback();
                    Toast.makeText(getContext(), "Shs survey completed", Toast.LENGTH_SHORT).show();
                }
            });
        }

        survey.close();
    }

    private void savePhq8Survey() {
        long timestamp = System.currentTimeMillis();
        int completed = 1;
        ContentValues record = new ContentValues();
        record.put(SHSTable.KEY_SHS_TS, timestamp);
        record.put(SHSTable.KEY_SHS_COMPLETED, completed);
        record.put(SHSTable.KEY_SHS_Q1, q1Seekbar.getProgress());
        record.put(SHSTable.KEY_SHS_Q2, q2Seekbar.getProgress());
        record.put(SHSTable.KEY_SHS_Q3, q3Seekbar.getProgress());
        record.put(SHSTable.KEY_SHS_Q4, q4Seekbar.getProgress());
        localController.update(LocalDbUtility.getTableName(shsTable), record, LocalDbUtility.getTableColumns(shsTable)[0] + " = " + currentSurveyId);
    }

    private Cursor getShs() {
        String tableName = LocalDbUtility.getTableName(shsTable);
        String indexColumn = LocalDbUtility.getTableColumns(shsTable)[0];
        String columnSchedule = LocalDbUtility.getTableColumns(shsTable)[2];
        String columnCompleted = LocalDbUtility.getTableColumns(shsTable)[3];
        String columnNotified = LocalDbUtility.getTableColumns(shsTable)[4];
        String columnExpired = LocalDbUtility.getTableColumns(shsTable)[5];

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

    public interface OnShsCompletedCallback {
        void onShsCompletedCallback();
    }

    public void setCallback(OnShsCompletedCallback callback) {
        this.callback = callback;
    }
}
