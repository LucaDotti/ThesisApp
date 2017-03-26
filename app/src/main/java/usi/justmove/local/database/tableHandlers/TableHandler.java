package usi.justmove.local.database.tableHandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import usi.justmove.MyApplication;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.controllers.SQLiteController;

/**
 * Created by usi on 10/03/17.
 */

public abstract class TableHandler {
    protected static LocalStorageController localController = SQLiteController.getInstance(MyApplication.getContext());

    public long id;
    protected boolean isNewRecord;
    protected String[] columns;

    public TableHandler(boolean isNewRecord) {
        this.isNewRecord = isNewRecord;
    }

    public abstract void setAttributes(ContentValues attributes);

    public abstract ContentValues getAttributes();

    public abstract ContentValues getAttributesFromCursor(Cursor cursor);

    public abstract void save();

    public void setAttribute(String attributeName, ContentValues attribute) {
        String name = null;
        for (int i = 0; i < columns.length; i++) {
            if(columns[i].equals(attributeName)) {
                name = columns[i];
                break;
            }
        }

        if(name == null) {
            throw new IllegalArgumentException("Column not found!");
        }
    }

    public abstract String[] getColumns();

    public abstract String getTableName();

    public abstract void delete();

}
