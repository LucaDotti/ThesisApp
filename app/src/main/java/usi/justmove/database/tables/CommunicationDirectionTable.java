package usi.justmove.database.tables;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;

/**
 * Created by Luca Dotti on 03/01/17.
 */
public class CommunicationDirectionTable {
    public static final String TABLE_COMMUNICATION_DIRECTION = "communication_direction";
    public static final String KEY_COMMUNICATION_DIRECTION_ID = "id_communication_direction";
    public static final String KEY_COMMUNICATION_DIRECTION_DIRECTION = "direction";

    public static final String TYPE_COMMUNICATION_DIRECTION_INCOMING = "incoming";
    public static final String TYPE_COMMUNICATION_DIRECTION_OUTGOING = "outgoing";
    public static final String TYPE_COMMUNICATION_DIRECTION_MISSED = "missed";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_COMMUNICATION_DIRECTION +
                "(" +
                KEY_COMMUNICATION_DIRECTION_ID + " INTEGER PRIMARY KEY," +
                KEY_COMMUNICATION_DIRECTION_DIRECTION + " TEXT" +
                ")";
    }

    public static List<ContentValues> getRecords() {
        List<ContentValues> records = new ArrayList<>();
        ContentValues record = new ContentValues();

        record.put(KEY_COMMUNICATION_DIRECTION_ID, 0);
        record.put(KEY_COMMUNICATION_DIRECTION_DIRECTION, TYPE_COMMUNICATION_DIRECTION_INCOMING);
        records.add(record);

        record = new ContentValues();
        record.put(KEY_COMMUNICATION_DIRECTION_ID, 1);
        record.put(KEY_COMMUNICATION_DIRECTION_DIRECTION, TYPE_COMMUNICATION_DIRECTION_OUTGOING);
        records.add(record);

        record = new ContentValues();
        record.put(KEY_COMMUNICATION_DIRECTION_ID, 2);
        record.put(KEY_COMMUNICATION_DIRECTION_DIRECTION, TYPE_COMMUNICATION_DIRECTION_MISSED);
        records.add(record);

        return records;
    }
}
