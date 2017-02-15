package usi.justmove.UI.fragments;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

/**
 * Created by usi on 09/02/17.
 */

public class ParentLayout implements Parent<ChildLayout> {

    /* Create an instance variable for your list of children */
    private List<ChildLayout> children;

    public ParentLayout(List<ChildLayout> children) {
        this.children = children;
    }

    /**
     * Your constructor and any other accessor
     * methods should go here.
     */
    @Override
    public List<ChildLayout> getChildList() {
        return children;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return true;
    }
}