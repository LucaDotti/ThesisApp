package usi.justmove.UI.fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.List;

import usi.justmove.R;

/**
 * Created by usi on 09/02/17.
 */

public class ExpandableRecyclerViewSurveysAdapter extends ExpandableRecyclerAdapter<ParentLayout, ChildLayout, PamSurveyParentViewHolder, PamSurveyChildViewHolder> {


    /**
     * Primary constructor. Sets up {@link #mParentList} and {@link #mFlatItemList}.
     * <p>
     * Any changes to {@link #mParentList} should be made on the original instance, and notified via
     * {@link #notifyParentInserted(int)}
     * {@link #notifyParentRemoved(int)}
     * {@link #notifyParentChanged(int)}
     * {@link #notifyParentRangeInserted(int, int)}
     * {@link #notifyChildInserted(int, int)}
     * {@link #notifyChildRemoved(int, int)}
     * {@link #notifyChildChanged(int, int)}
     * methods and not the notify methods of RecyclerView.Adapter.
     *
     * @param parentList List of all parents to be displayed in the RecyclerView that this
     *                   adapter is linked to
     */
    public ExpandableRecyclerViewSurveysAdapter(@NonNull List<ParentLayout> parentList) {
        super(parentList);
    }

    @NonNull
    @Override
    public PamSurveyParentViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View view = LayoutInflater.from(parentViewGroup.getContext()).inflate(R.layout.surveys_pam_parent_layout, parentViewGroup, false);
        PamSurveyParentViewHolder surveyViewHolder = new PamSurveyParentViewHolder(view);
        return surveyViewHolder;
    }

    @NonNull
    @Override
    public PamSurveyChildViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View view = LayoutInflater.from(childViewGroup.getContext()).inflate(R.layout.surveys_pam_child_layout, childViewGroup, false);
        PamSurveyChildViewHolder surveyViewHolder = new PamSurveyChildViewHolder(view);
        return surveyViewHolder;
    }

    @Override
    public void onBindParentViewHolder(@NonNull PamSurveyParentViewHolder parentViewHolder, int parentPosition, @NonNull ParentLayout parent) {

    }

    @Override
    public void onBindChildViewHolder(@NonNull PamSurveyChildViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull ChildLayout child) {

    }
}
