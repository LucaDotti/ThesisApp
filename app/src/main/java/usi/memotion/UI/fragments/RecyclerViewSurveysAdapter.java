package usi.memotion.UI.fragments;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import usi.memotion.R;

/**
 * Created by usi on 09/02/17.
 */

public class RecyclerViewSurveysAdapter extends RecyclerView.Adapter<SurveyViewHolder> {

    private List<String> surveys;
    private int mExpandedPosition;

    public RecyclerViewSurveysAdapter(List<String> surveys) {
        this.surveys = surveys;
        mExpandedPosition = -1;
    }

    @Override
    public SurveyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.surveys_pam_child_layout, parent, false);
        SurveyViewHolder surveyViewHolder = new SurveyViewHolder(view);
        return surveyViewHolder;
    }

    @Override
    public void onBindViewHolder(SurveyViewHolder holder, int position) {

//        final int pos = position;
//        final boolean isExpanded = position==mExpandedPosition;
//        TextView expandTextView = (TextView) holder.itemView.findViewById(R.id.pamSurveyExpandTextView);
//        final PamSurveyView pam = (PamSurveyView) holder.itemView.findViewById(R.id.pamSurveyView);
//        pam.setVisibility(isExpanded?View.VISIBLE:View.GONE);
//        pam.setActivated(isExpanded);
//        expandTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mExpandedPosition = isExpanded ? -1:pos;
//                TransitionManager.beginDelayedTransition(pam);
//                notifyDataSetChanged();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

class SurveyViewHolder extends RecyclerView.ViewHolder {
    private CardView pam;

    public SurveyViewHolder(View itemView) {
        super(itemView);
//        pam = (CardView) itemView.findViewById(R.id.pamSurveyCardView);
    }
}



