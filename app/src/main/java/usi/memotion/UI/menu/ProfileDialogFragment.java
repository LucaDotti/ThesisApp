package usi.memotion.UI.menu;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.shawnlin.numberpicker.NumberPicker;


import usi.memotion.Mail;
import usi.memotion.R;
import usi.memotion.UI.fragments.SurveysFragment;
import usi.memotion.local.database.controllers.LocalStorageController;
import usi.memotion.local.database.controllers.SQLiteController;
import usi.memotion.local.database.tableHandlers.User;
import usi.memotion.local.database.tables.UserTable;

/**
 * Created by usi on 06/02/17.
 */

public class ProfileDialogFragment extends AppCompatDialogFragment {
    private OnEnrollStatusUpdate callback;
    private static String[] academicStatusValues = {"BSc", "MSc", "PhD", "Prof", "Other"};
    private LocalStorageController localcontroller;
    private NumberPicker agePicker;
    private RadioButton maleGenderRadioButton;
    private NumberPicker statusPicker;
    private Button updateButton;
    private CheckBox exitStudyCheckbox;
    private ViewGroup exitStudyWarning;
    private TextView noProfileMsg;
    private LinearLayout profileForm;
    private RadioButton currentSelectedRadioButton;
    private Dialog exitStudyDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        localcontroller = SQLiteController.getInstance(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View root = inflater.inflate(R.layout.profile_layout, null);
        noProfileMsg = (TextView) root.findViewById(R.id.profile_noProfileMsg);
        profileForm = (LinearLayout) root.findViewById(R.id.profile_profileForm);
        exitStudyCheckbox = (CheckBox) root.findViewById(R.id.profile_exitStudyCheckbox);
        initForm(root);

//        exitStudyInfo = (ViewGroup) root.findViewById(R.id.profile_exitStudyInfo);
        exitStudyWarning = (ViewGroup) root.findViewById(R.id.profile_exitStudyWarning);
        updateButton = (Button) root.findViewById(R.id.profile_updateProfileButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exitStudyCheckbox.isChecked()) {
                    showConfirmExitStudyDialog();
                } else {
                    updateUser();
                    User.saveAgreed(true);
                    callback.onEnrollStatusUpdate(false);
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

    private void dismissDialog() {
        dismiss();
    }

    private void showConfirmExitStudyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to exit the study?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        localcontroller.delete(UserTable.TABLE_USER, UserTable.KEY_USER_ID + " = " + 1);
                        sendEmail();
                        dismissDialog();
                        User.saveAgreed(false);
                        callback.onEnrollStatusUpdate(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        exitStudyDialog.dismiss();
                    }
                });

        exitStudyDialog = builder.create();
        exitStudyDialog.show();
    }

    private void sendEmail() {
        Mail m = new Mail(getContext().getString(R.string.staff_email), getContext().getString(R.string.staff_email_pass));

        String[] toArr = {getContext().getString(R.string.staff_email)};
        m.set_to(toArr);
        m.set_from(getContext().getString(R.string.staff_email));
        m.set_subject("User leaved the study.");
        m.setBody("The user " + Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID) + " leaved the study. Please contact him/her as soon as possible.");

        SendEmailAsynchTask task = new SendEmailAsynchTask(m);

        task.execute();
    }

    private void initForm(View root) {
        Cursor c = localcontroller.rawQuery("SELECT * FROM " + UserTable.TABLE_USER, null);
        if(c.getCount() > 0) {
            profileForm.setVisibility(View.VISIBLE);
            noProfileMsg.setVisibility(View.GONE);
            c.moveToFirst();
            agePicker = (NumberPicker) root.findViewById(R.id.age_picker);
            agePicker.setMinValue(18);
            agePicker.setMaxValue(99);
            agePicker.setValue(c.getInt(3));

            if(c.getInt(2) == 0) {
                exitStudyCheckbox.setChecked(true);
            } else {
                exitStudyCheckbox.setChecked(false);
            }

            maleGenderRadioButton = (RadioButton) root.findViewById(R.id.genderMaleRadioButton);
            RadioButton femaleGenderRadioButton = (RadioButton) root.findViewById(R.id.genderFemaleRadioButton);
            String a = c.getString(4);
            if(c.getString(4).equals("female")) {
                femaleGenderRadioButton.setChecked(true);
                currentSelectedRadioButton = femaleGenderRadioButton;
            } else {
                maleGenderRadioButton.setChecked(true);
                currentSelectedRadioButton = maleGenderRadioButton;
            }
            RadioButton maleGenderRadioButton = (RadioButton) root.findViewById(R.id.genderMaleRadioButton);
            maleGenderRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentSelectedRadioButton != null) {
                        currentSelectedRadioButton.setChecked(false);
                    }

                    currentSelectedRadioButton = (RadioButton) v;

                }
            });

            femaleGenderRadioButton = (RadioButton) root.findViewById(R.id.genderFemaleRadioButton);
            femaleGenderRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentSelectedRadioButton != null) {
                        currentSelectedRadioButton.setChecked(false);
                    }

                    currentSelectedRadioButton = (RadioButton) v;

                }
            });

            statusPicker = (NumberPicker) root.findViewById(R.id.status_picker);
            statusPicker.setMinValue(0);
            statusPicker.setMaxValue(4);


            String selectedStatus = c.getString(7);
            int i;
            for(i = 0; i < academicStatusValues.length; i++) {
                if(selectedStatus.equals(academicStatusValues[i])) {
                    break;
                }
            }
            statusPicker.setValue(i);
            statusPicker.setDisplayedValues(academicStatusValues);
        } else {
            profileForm.setVisibility(View.GONE);
            noProfileMsg.setVisibility(View.VISIBLE);
        }

        c.close();
    }

    private void updateUser() {
        ContentValues record = new ContentValues();
        record.put(UserTable.KEY_USER_AGE, agePicker.getValue());
        record.put(UserTable.KEY_USER_ACADEMIC_STATUS, academicStatusValues[statusPicker.getValue()]);
        record.put(UserTable.KEY_USER_UPDATE_TS, System.currentTimeMillis());
        record.put(UserTable.KEY_USER_GENDER, currentSelectedRadioButton.getId() == R.id.genderFemaleRadioButton ? "female" : "male");
        localcontroller.update(UserTable.TABLE_USER, record, UserTable.KEY_USER_ID + " = " + 1);
    }

    public interface OnEnrollStatusUpdate {
        void onEnrollStatusUpdate(boolean exit);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEnrollStatusUpdate) {
            callback = (OnEnrollStatusUpdate) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnExitStudy interface");
        }
    }
}

class SendEmailAsynchTask extends AsyncTask<Void, Void, Boolean> {
    private Mail mail;

    public SendEmailAsynchTask(Mail mail) {
        this.mail = mail;
    }

    @Override
    protected Boolean doInBackground(Void... params) {


        try {
            boolean status = mail.send();

            return status;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
