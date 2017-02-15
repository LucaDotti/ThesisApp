package usi.justmove.UI.menu;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import usi.justmove.R;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.local.database.tables.UserTable;

/**
 * Created by usi on 06/02/17.
 */

public class ProfileDialogFragment extends AppCompatDialogFragment {
    private LocalStorageController localcontroller;
    private Spinner ageSpinner;
    private Spinner facultySpinner;
    private Spinner statusSpinner;
    private Button updateButton;
    private CheckBox exitStudyCheckbox;
    private ViewGroup exitStudyWarning;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        localcontroller = new SQLiteController(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View root = inflater.inflate(R.layout.profile_layout, null);

        initForm(root);

//        exitStudyInfo = (ViewGroup) root.findViewById(R.id.profile_exitStudyInfo);
        exitStudyWarning = (ViewGroup) root.findViewById(R.id.profile_exitStudyWarning);
        updateButton = (Button) root.findViewById(R.id.profile_updateProfileButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exitStudyCheckbox.isChecked()) {

                } else {
                    updateUser();
                    dismiss();
                    Toast.makeText(getContext(), "Profile saved", Toast.LENGTH_SHORT).show();
                }
            }
        });

        exitStudyCheckbox = (CheckBox) root.findViewById(R.id.profile_exitStudyCheckbox);
        exitStudyCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    exitStudyWarning.setVisibility(View.VISIBLE);
                } else {
                    exitStudyWarning.setVisibility(View.INVISIBLE);
                }
            }
        });

        builder.setView(root);

        return builder.create();
    }

    private void initForm(View root) {
        Cursor c = localcontroller.rawQuery("SELECT * FROM " + UserTable.TABLE_USER, null);
        if(c.getCount() > 0) {
            c.moveToFirst();

            ageSpinner = (Spinner) root.findViewById(R.id.profile_age_spinner);
            List<Integer> ages = new ArrayList<>();
            for(int i = 18; i < 60; i++) {
                ages.add(i);
            }

            ArrayAdapter<Integer> ageAdapter = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_spinner_dropdown_item, ages);
            ageSpinner.setAdapter(ageAdapter);
            ageSpinner.setSelection(getSpinnerItemPosition(ageSpinner, c.getString(3)));


            facultySpinner = (Spinner) root.findViewById(R.id.profile_faculty_spinner);
            List<String> faculties = new ArrayList<>();
            faculties.add("Communication sciences");
            faculties.add("Biomedical sciences");
            faculties.add("Architecture");
            faculties.add("Informatics");
            faculties.add("Economics");

            ArrayAdapter<String> facultyAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, faculties);
            facultySpinner.setAdapter(facultyAdapter);
            facultySpinner.setSelection(getSpinnerItemPosition(facultySpinner, c.getString(5)));

            statusSpinner = (Spinner) root.findViewById(R.id.profile_status_spinner);
            List<String> status = new ArrayList<>();
            status.add("Bachelor");
            status.add("Master");
            status.add("PhD");
            status.add("Professor");

            ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, status);
            statusSpinner.setAdapter(statusAdapter);
            statusSpinner.setSelection(getSpinnerItemPosition(statusSpinner, c.getString(6)));
        }

    }

    private int getSpinnerItemPosition(Spinner spinner, String item) {
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(item)){
                return i;
            }
        }
        return -1;
    }

    private void updateUser() {
        ContentValues record = new ContentValues();
        record.put(UserTable.KEY_USER_AGE, ageSpinner.getSelectedItem().toString());
        record.put(UserTable.KEY_USER_FACULTY, facultySpinner.getSelectedItem().toString());
        record.put(UserTable.KEY_USER_ACADEMIC_STATUS, statusSpinner.getSelectedItem().toString());
        record.put(UserTable.KEY_USER_UPDATE_TS, System.currentTimeMillis());
        localcontroller.update(UserTable.TABLE_USER, record, UserTable.KEY_USER_ID + " = " + 1);
    }
}
