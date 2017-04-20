package usi.memotion.UI.menu;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import usi.memotion.R;

/**
 * Created by usi on 06/02/17.
 */

public class StudyDialogFragment extends AppCompatDialogFragment {
    private Button button;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.about_this_study_layout, null);

        button = (Button) root.findViewById(R.id.study_okButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(root);

        return builder.create();
    }
}
