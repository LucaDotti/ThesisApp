package usi.justmove.UI.fragments;

import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Layout;
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
import usi.justmove.UI.views.PHQ8SurveyView;
import usi.justmove.UI.views.PSSSurveyView;
import usi.justmove.UI.views.PWBSurveyView;
import usi.justmove.UI.views.SHSSurveyView;
import usi.justmove.UI.views.SWLSSurveyView;
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

public class SurveysFragment extends Fragment implements PAMSurveyView.OnPamSurveyCompletedCallback, PWBSurveyView.OnPwbCompletedCallback, SHSSurveyView.OnShsCompletedCallback, SWLSSurveyView.OnSwlsCompletedCallback, PHQ8SurveyView.OnPhq8CompletedCallback, PSSSurveyView.OnPssCompletedCallback {


    private OnSurveyCompletedCallback callback;
    private LocalStorageController localController;


    private ExpandableLayout pamLayout;
    private ExpandableLayout pwbLayout;
    private ExpandableLayout historyLayout;
    private ExpandableLayout swlsLayout;
    private ExpandableLayout pssLayout;
    private ExpandableLayout shsLayout;
    private ExpandableLayout phq8Layout;


    private PAMSurveyView pamSurvey;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Survey fragment", "onCreateView");
        localController = SQLiteController.getInstance(getContext());
        View root = inflater.inflate(R.layout.surveys_layout, container, false);

        initPam(root, inflater);
        initPwb(root, inflater);
        initPhq8(root, inflater);
        initPss(root, inflater);
        initSwls(root, inflater);
        initShs(root, inflater);
        initHistory(root, inflater);

        return root;
    }

    private void initPam(View root, LayoutInflater inflater) {
        pamLayout = (ExpandableLayout) root.findViewById(R.id.surveysFragmentPam);
        View pamTitleView = inflater.inflate(R.layout.expandable_layout_title, null);
        PAMSurveyView pamBody = new PAMSurveyView(getContext(), null);
        pamBody.setCallback(this);

        Cursor pamSurveys = getTodaySurvey(LocalTables.TABLE_PAM);
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
    }

    private void initPwb(View root, LayoutInflater inflater) {
        pwbLayout = (ExpandableLayout) root.findViewById(R.id.surveysFragmentPwb);

        View pwbTitleView = inflater.inflate(R.layout.expandable_layout_title, null);
        PWBSurveyView pwbBody = new PWBSurveyView(getContext(), null);
        pwbBody.setCallback(this);

        Cursor pwbSurvey = getTodaySurvey(LocalTables.TABLE_PWB);
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
    }

    private void initHistory(View root, LayoutInflater inflater) {
        historyLayout = (ExpandableLayout) root.findViewById(R.id.surveysFragmentHistory);
        View historyTitleView = inflater.inflate(R.layout.expandable_layout_title, null);
        historyLayout.setTitleView(historyTitleView);
        historyLayout.setTitleImage(R.id.surveysNotificationImage, 0);
        historyLayout.setTitleText(R.id.surveysTitle, "History");
        historyLayout.setNoContentMsg("Coming soon");
        historyLayout.showNoContentMsg();
    }

    private void initSwls(View root, LayoutInflater inflater) {
        swlsLayout = (ExpandableLayout) root.findViewById(R.id.surveysFragmentSwls);

        View titleView = inflater.inflate(R.layout.expandable_layout_title, null);
        SWLSSurveyView body = new SWLSSurveyView(getContext(), null);
        body.setCallback(this);

        Cursor survey = getTodaySurvey(LocalTables.TABLE_SWLS);
        swlsLayout.setTitleView(titleView);
        swlsLayout.setTitleText(R.id.surveysTitle, "SWLS");

        if(survey.getCount() > 0) {
            swlsLayout.setBodyView(body);
            swlsLayout.showBody();
        } else {
            swlsLayout.setTitleImage(R.id.surveysNotificationImage, 0);
            swlsLayout.setNoContentMsg("No SWLS survey available");
            swlsLayout.showNoContentMsg();
        }
    }

    private void initShs(View root, LayoutInflater inflater) {
        shsLayout = (ExpandableLayout) root.findViewById(R.id.surveysFragmentSHS);

        View titleView = inflater.inflate(R.layout.expandable_layout_title, null);
        SHSSurveyView body = new SHSSurveyView(getContext(), null);
        body.setCallback(this);

        Cursor survey = getTodaySurvey(LocalTables.TABLE_SHS);
        shsLayout.setTitleView(titleView);
        shsLayout.setTitleText(R.id.surveysTitle, "SHS");

        if(survey.getCount() > 0) {
            shsLayout.setBodyView(body);
            shsLayout.showBody();
        } else {
            shsLayout.setTitleImage(R.id.surveysNotificationImage, 0);
            shsLayout.setNoContentMsg("No SHS survey available");
            shsLayout.showNoContentMsg();
        }
    }

    private void initPhq8(View root, LayoutInflater inflater) {
        phq8Layout = (ExpandableLayout) root.findViewById(R.id.surveysFragmentPhq8);

        View titleView = inflater.inflate(R.layout.expandable_layout_title, null);
        PHQ8SurveyView body = new PHQ8SurveyView(getContext(), null);
        body.setCallback(this);

        Cursor survey = getTodaySurvey(LocalTables.TABLE_PHQ8);
        phq8Layout.setTitleView(titleView);
        phq8Layout.setTitleText(R.id.surveysTitle, "PHQ8");

        if(survey.getCount() > 0) {
            phq8Layout.setBodyView(body);
            phq8Layout.showBody();
        } else {
            phq8Layout.setTitleImage(R.id.surveysNotificationImage, 0);
            phq8Layout.setNoContentMsg("No PHQ8 survey available");
            phq8Layout.showNoContentMsg();
        }
    }

    private void initPss(View root, LayoutInflater inflater) {
        pssLayout = (ExpandableLayout) root.findViewById(R.id.surveysFragmentPss);

        View titleView = inflater.inflate(R.layout.expandable_layout_title, null);
        PSSSurveyView body = new PSSSurveyView(getContext(), null);
        body.setCallback(this);

        Cursor survey = getTodaySurvey(LocalTables.TABLE_PSS);
        pssLayout.setTitleView(titleView);
        pssLayout.setTitleText(R.id.surveysTitle, "PSS");

        if(survey.getCount() > 0) {
            pssLayout.setBodyView(body);
            pssLayout.showBody();
        } else {
            pssLayout.setTitleImage(R.id.surveysNotificationImage, 0);
            pssLayout.setNoContentMsg("No PSS survey available");
            pssLayout.showNoContentMsg();
        }
    }

    private Cursor getTodaySurvey(LocalTables table) {
        String tableName = LocalDbUtility.getTableName(table);
        String indexColumn = LocalDbUtility.getTableColumns(table)[0];
        String columnSchedule = LocalDbUtility.getTableColumns(table)[2];
        String columnCompleted = LocalDbUtility.getTableColumns(table)[3];
        String columnNotified = LocalDbUtility.getTableColumns(table)[4];
        String columnExpired = LocalDbUtility.getTableColumns(table)[5];

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

//    private Cursor getTodayPwb() {
//        LocalTables pwbTable = LocalTables.TABLE_PWB;
//        String tableName = LocalDbUtility.getTableName(pwbTable);
//        String indexColumn = LocalDbUtility.getTableColumns(pwbTable)[0];
//        String columnSchedule = LocalDbUtility.getTableColumns(pwbTable)[2];
//        String columnCompleted = LocalDbUtility.getTableColumns(pwbTable)[3];
//        String columnNotified = LocalDbUtility.getTableColumns(pwbTable)[4];
//        String columnExpired = LocalDbUtility.getTableColumns(pwbTable)[5];
//
//        LocalDateTime startDateTime = new LocalDateTime().withTime(0, 0, 0, 0);
//        LocalDateTime endDateTime = new LocalDateTime().withTime(23, 59, 59, 999);
//        long startMillis = startDateTime.toDateTime().getMillis()/1000;
//        long endMillis = endDateTime.toDateTime().getMillis()/1000;
//        Cursor c = localController.rawQuery("SELECT * FROM " + tableName
//                + " WHERE " + columnSchedule + " >= " + startMillis + " AND " + columnSchedule + " <= " + endMillis
//                + " AND " + columnCompleted + " = " + 0 + " AND " + columnNotified + " > " + 0 + " AND " + columnExpired + " = " + 0 +
//                " ORDER BY " + indexColumn + " ASC LIMIT 1", null);
//
//        return c;
//    }

//    private Cursor getTodayPams() {
//        LocalTables pamTable = LocalTables.TABLE_PAM;
//        String tableName = LocalDbUtility.getTableName(pamTable);
//        String indexColumn = LocalDbUtility.getTableColumns(pamTable)[0];
//        String columnSchedule = LocalDbUtility.getTableColumns(pamTable)[2];
//        String columnCompleted = LocalDbUtility.getTableColumns(pamTable)[3];
//        String columnNotified = LocalDbUtility.getTableColumns(pamTable)[4];
//        String columnExpired = LocalDbUtility.getTableColumns(pamTable)[5];
//
//        LocalDateTime startDateTime = new LocalDateTime().withTime(0, 0, 0, 0);
//        LocalDateTime endDateTime = new LocalDateTime().withTime(23, 59, 59, 999);
//        long startMillis = startDateTime.toDateTime().getMillis()/1000;
//        long endMillis = endDateTime.toDateTime().getMillis()/1000;
//        Cursor c = localController.rawQuery("SELECT * FROM " + tableName
//                + " WHERE " + columnSchedule + " >= " + startMillis + " AND " + columnSchedule + " <= " + endMillis
//                + " AND " + columnCompleted + " = " + 0 + " AND " + columnNotified + " > " + 0 + " AND " + columnExpired + " = " + 0 +
//                " ORDER BY " + indexColumn + " ASC LIMIT 1", null);
//
//        return c;
//    }





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

    @Override
    public void onShsCompletedCallback() {

    }

    @Override
    public void onPhq8CompletedCallback() {

    }

    @Override
    public void onPssCompletedCallback() {

    }

    @Override
    public void onSwlsCompletedCallback() {

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
