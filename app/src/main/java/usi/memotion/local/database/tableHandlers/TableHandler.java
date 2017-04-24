package usi.memotion.local.database.tableHandlers;

import android.content.ContentValues;
import android.database.Cursor;

import usi.memotion.MyApplication;
import usi.memotion.local.database.controllers.LocalStorageController;
import usi.memotion.local.database.controllers.SQLiteController;

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

    //should not be abstract
    public abstract String[] getColumns();

    public abstract String getTableName();

    public abstract void delete();
}
