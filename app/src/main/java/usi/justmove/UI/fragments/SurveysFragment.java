package usi.justmove.UI.fragments;

import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.Inflater;

import usi.justmove.R;
import usi.justmove.UI.ExpandableLayout;
import usi.justmove.UI.views.PAMSurveyView;
import usi.justmove.UI.views.PWBSurveyView;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.local.database.tables.LocalDbUtility;
import usi.justmove.local.database.tables.LocalTables;
import usi.justmove.local.database.tables.PAMTable;
import usi.justmove.local.database.tables.PWBTable;

import static usi.justmove.R.id.submitButton;
import static usi.justmove.R.id.surveysPamNotificationImage;

/**
 * Created by usi on 04/02/17.
 */

public class SurveysFragment extends Fragment implements PAMSurveyView.OnPamSurveyCompletedCallback, PWBSurveyView.OnPwbCompletedCallback {


    private OnSurveyCompletedCallback callback;
    private LocalStorageController localController;


    private ExpandableLayout pamLayout;
    private ExpandableLayout pwbLayout;
    private ExpandableLayout historyLayout;


    private PAMSurveyView pamSurvey;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Survey fragment", "onCreateView");
        localController = SQLiteController.getInstance(getContext());

        View root = inflater.inflate(R.layout.surveys_layout, container, false);

        pamLayout = (ExpandableLayout) root.findViewById(R.id.surveysFragmentPam);
        View pamTitleView = inflater.inflate(R.layout.expandable_layout_title, null);
        PAMSurveyView pamBody = new PAMSurveyView(getContext(), null);
        pamBody.setCallback(this);

        Cursor pamSurveys = getTodayPams();
        pamLayout.setTitleView(pamTitleView);
        pamLayout.setTitleText(R.id.surveysTitle, "PAM");

        if(pamSurveys.getCount() > 0) {
            pamLayout.setBodyView(pamBody);
            pamLayout.showBody();
        } else {
            pamLayout.setTitleImage(R.id.surveysNotificationImage, 0);
            pamLayout.setNoContentMsg("No PAM survey available");
            pamLayout.showNoContentMsg();
        }

        pwbLayout = (ExpandableLayout) root.findViewById(R.id.surveysFragmentPwb);

        View pwbTitleView = inflater.inflate(R.layout.expandable_layout_title, null);
        PWBSurveyView pwbBody = new PWBSurveyView(getContext(), null);
        pwbBody.setCallback(this);

        Cursor pwbSurvey = getTodayPwb();
        pwbLayout.setTitleView(pwbTitleView);
        pwbLayout.setTitleText(R.id.surveysTitle, "PWB");

        if(pwbSurvey.getCount() > 0) {
            pwbLayout.setBodyView(pwbBody);
            pwbLayout.showBody();
        } else {
            pwbLayout.setTitleImage(R.id.surveysNotificationImage, 0);
            pwbLayout.setNoContentMsg("No PWB survey available");
            pwbLayout.showNoContentMsg();
        }

        historyLayout = (ExpandableLayout) root.findViewById(R.id.surveysFragmentHistory);
        View historyTitleView = inflater.inflate(R.layout.expandable_layout_title, null);
        historyLayout.setTitleView(historyTitleView);
        historyLayout.setTitleImage(R.id.surveysNotificationImage, 0);
        historyLayout.setTitleText(R.id.surveysTitle, "History");
        historyLayout.setNoContentMsg("Coming soon");
        historyLayout.showNoContentMsg();
        return root;
    }

    private Cursor getTodayPwb() {
        LocalTables pwbTable = LocalTables.TABLE_PWB;
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
    private Cursor getTodayPams() {
        LocalTables pamTable = LocalTables.TABLE_PAM;
        String tableName = LocalDbUtility.getTableName(pamTable);
        String indexColumn = LocalDbUtility.getTableColumns(pamTable)[0];
        String columnSchedule = LocalDbUtility.getTableColumns(pamTable)[2];
        String columnCompleted = LocalDbUtility.getTableColumns(pamTable)[3];
        String columnNotified = LocalDbUtility.getTableColumns(pamTable)[4];
        String columnExpired = LocalDbUtility.getTableColumns(pamTable)[5];

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





//    private float parseTimePeriod(String period) {
//        if(period.equals("None")) {
//            return 0;
//        }
//
//        float hours = 0;
//        float mins = 0;
//
//        String[] split = period.split(" ");
//
//        if(split.length == 2) {
//            if(split[1].equals("hour") || split[1].equals("hours")) {
//                hours = Float.parseFloat(split[0]);
//            } else {
//                mins = Float.parseFloat(split[0]);
//            }
//        } else {
//            hours = Float.parseFloat(split[0]);
//            mins = Float.parseFloat(split[2]);
//        }
//        return hours + mins/60;
//    }



    @Override
    public void onPamSurveyCompletedCallback() {
        pamLayout.setTitleImage(R.id.surveysNotificationImage, 0);
        pamLayout.setNoContentMsg("No PAM survey available");
        pamLayout.showNoContentMsg();
        pamLayout.collapse();
        callback.onSurveyCompletedCallback();
    }

    @Override
    public void onPwbCompletedCallback() {
        pwbLayout.setTitleImage(R.id.surveysNotificationImage, 0);
        pwbLayout.setNoContentMsg("No PWB survey available");
        pwbLayout.showNoContentMsg();
        pwbLayout.collapse();
        callback.onSurveyCompletedCallback();
    }


    public interface OnSurveyCompletedCallback {
        void onSurveyCompletedCallback();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSurveyCompletedCallback) {
            callback = (SurveysFragment.OnSurveyCompletedCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUserRegisteredCallback");
        }
    }
}
