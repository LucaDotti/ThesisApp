package usi.memotion.local.database.tableHandlers;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.SimpleDateFormat;

import usi.memotion.local.database.db.LocalTables;
import usi.memotion.local.database.tables.UserTable;

/**
 * Created by usi on 31/03/17.
 */

public class User extends TableHandler {
    private static LocalTables table = LocalTables.TABLE_USER;

    public User(boolean isNewRecord) {
        super(isNewRecord);
        id = -1;
    }

    @Override
    public void setAttributes(ContentValues attributes) {

    }

    @Override
    public ContentValues getAttributes() {
        return null;
    }

    @Override
    public ContentValues getAttributesFromCursor(Cursor cursor) {
        return null;
    }

    @Override
    public void save() {

    }

    @Override
    public String[] getColumns() {
        return new String[0];
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public void delete() {

    }

    public static String getCreationDate() {
        Cursor user = localController.rawQuery("SELECT * FROM " + table.getTableName(), null);

        if(user.getCount() > 0) {
            user.moveToFirst();

            int time = user.getInt(9);

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
//            DateTime d = new DateTime(time * 1000L);
//
//
//            DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd-MM-yyyy");
            return format.format(time * 1000L);
        }

        return null;
    }

    public static String getEndStudyDate() {
        Cursor user = localController.rawQuery("SELECT * FROM " + table.getTableName(), null);

        if(user.getCount() > 0) {
            user.moveToFirst();
            return user.getString(10);
        }

        return null;
    }

    public static boolean isEnrolled() {
        Cursor user = localController.rawQuery("SELECT * FROM " + table.getTableName(), null);

        if(user.getCount() > 0) {
            user.moveToFirst();
            return user.getInt(2) == 0 ? false : true;
        }

        return false;
    }

    public static void saveAgreed(boolean agreed) {
        ContentValues agr = new ContentValues();
        agr.put(UserTable.KEY_USER_AGREED, agreed);
        localController.update(table.getTableName(), agr, UserTable.KEY_USER_ID + " = " + 1);
    }
}
