package usi.justmove.remote.database.upload;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.tables.LocalDbUtility;
import usi.justmove.local.database.tables.LocalTables;
import usi.justmove.local.database.tables.UploaderUtilityTable;
import usi.justmove.remote.database.RemoteStorageController;
import java.util.*;
import java.text.SimpleDateFormat;


/**
 * Created by usi on 19/01/17.
 */

public class Uploader {
    private RemoteStorageController remoteController;
    private LocalStorageController localController;
    private LocalTables tableToClean;
    private long uploadThreshold;
    private String userId;

    public Uploader(String userId, RemoteStorageController remoteController, LocalStorageController localController, long uploadThreshold) {
        this.remoteController = remoteController;
        this.localController = localController;
        this.uploadThreshold  = uploadThreshold;
        //the start table to clean
        tableToClean = LocalTables.values()[0];
        //user id is the phone id
        this.userId = userId;

        Cursor c = localController.rawQuery("SELECT * FROM uploader_utility", null);
        c.moveToFirst();
//        Log.d("DATA UPLOAD INIT", c.getString(1) + " " + c.getString(2) + " " + c.getInt(3) + " " + c.getString(4));

    }

    /**
     * Upload
     */
    public void upload() {
        //upload only when the uploadThreshold is reached
        Log.d("DATA UPLOAD SERVICE", "Size " + Long.toString(localController.getDbSize()));
        if(localController.getDbSize() > uploadThreshold) {
            Log.d("DATA UPLOAD SERVICE", "START CLEANING...");
            //if a new day starts, we need to clean the file part array, so that it restart from 0
            String today = buildDate();
            if(!checkDate(today)) {
                cleanFileParts();
                updateDate(today);
            }

            //number of tables
            int nbTableToClean = LocalTables.values().length;
            int i = 0;
            Cursor c;
            //current table to clean
            LocalTables currTable;
            //id of the starting record that was uploaded
            int startId;
            //id of the ending record that was uploaded
            int endId;
            String fileName;

//            //-------------------------- TEST ------------------------------//
//            currTable = LocalTables.TABLE_ACCELEROMETER;
//            Log.d("DATA UPLOAD SERVICE", "CLEANING TABLE " + LocalDbUtility.getTableName(currTable));
//            //build name of file to upload
//            fileName = buildFileName(currTable);
//
//            //get all data currently in the table
//            c = getRecords(currTable);
//
//            if(c.getCount() > 0) {
//                c.moveToFirst();
//                //get the start and end index of the extracted data
////                    c.moveToNext();
//                //the starting index
//                startId = c.getInt(0);
//                c.moveToLast();
//                //the ending index
//                endId = c.getInt(0);
//                c.moveToFirst();
//
//                //upload the data to the server
//                int response = remoteController.upload(fileName, toCSV(c, currTable));
//
//                //if the file was put, delete records and update the arrays
//                if(response >= 200 && response <= 207) {
//                    //delete from the db the records where id > startId and id <= endId
//                    removeRecords(currTable, startId, endId);
//                    incrementFilePartId(currTable);
//                    updateRecordId(currTable, endId);
//                } else {
//                    Log.d("DATA UPLOAD SERVICE", "Owncould's response: " + Integer.toString(response));
//                }
//            }
//            //-------------------------- TEST ------------------------------//

            //clean all tables
            while(i < nbTableToClean) {
                //get current table to clean
                currTable = LocalTables.values()[(tableToClean.ordinal()+i) % LocalTables.values().length];
                Log.d("DATA UPLOAD SERVICE", "CLEANING TABLE " + LocalDbUtility.getTableName(currTable));
                //build name of file to upload
                fileName = buildFileName(currTable);

                //get all data currently in the table
                c = getRecords(currTable);

                if(c.getCount() > 0) {
                    c.moveToFirst();
                    //get the start and end index of the extracted data
//                    c.moveToNext();
                    //the starting index
                    startId = c.getInt(0);
                    c.moveToLast();
                    //the ending index
                    endId = c.getInt(0);
                    c.moveToFirst();

                    //upload the data to the server
                    int response = remoteController.upload(fileName, toCSV(c, currTable));

                    //if the file was put, delete records and update the arrays
                    if(response >= 200 && response <= 207) {
                        //delete from the db the records where id > startId and id <= endId
                        removeRecords(currTable, startId, endId);
                        incrementFilePartId(currTable);
                        updateRecordId(currTable, endId);
                    } else {
                        Log.d("DATA UPLOAD SERVICE", "Something went wrong, Owncould's response: " + Integer.toString(response));
                    }
                } else {
                    Log.d("DATA UPLOAD SERVICE", "Table is empty, nothing to upload" );
                }

                i++;
            }
        }
    }

    private boolean checkDate(String date) {
        Cursor c = localController.rawQuery("SELECT * FROM " + UploaderUtilityTable.TABLE_UPLOADER_UTILITY + " WHERE " + UploaderUtilityTable.KEY_UPLOADER_UTILITY_ID + " = 0", null);
        c.moveToNext();

        String d = c.getString(2);

        return date.equals(d);
    }

    /**
     * Utility function to increment the part id for the given table.
     *
     * @param table
     */
    private void incrementFilePartId(LocalTables table) {
        int part = getFilePartId(table);
        Log.d("DATA UPLOAD SERVICE", "INCREASING PART COUNT " + Integer.toString(part+1));

        ContentValues val = new ContentValues();
        val.put(UploaderUtilityTable.KEY_UPLOADER_UTILITY_FILE_PART, part+1);
        String clause = UploaderUtilityTable.KEY_UPLOADER_UTILITY_TABLE + " = \"" + LocalDbUtility.getTableName(table)  + "\"";

        localController.update(UploaderUtilityTable.TABLE_UPLOADER_UTILITY, val, clause);
    }

    /**
     * Utility function to update the record id in the given table.
     *
     * @param table
     * @param recordId
     */
    private void updateRecordId(LocalTables table, int recordId) {
        String clause = UploaderUtilityTable.KEY_UPLOADER_UTILITY_TABLE + " = \"" + LocalDbUtility.getTableName(table) + "\"";
        ContentValues val = new ContentValues();
        val.put(UploaderUtilityTable.KEY_UPLOADER_UTILITY_RECORD_ID, recordId);

        localController.update(UploaderUtilityTable.TABLE_UPLOADER_UTILITY, val, clause);
    }

    /**
     * Utility function to update the date of all tables.
     *
     * @param date
     */
    private void updateDate(String date) {
        String clause;
        ContentValues val;
        for(int i = 0; i < LocalTables.values().length; i++) {
            val = new ContentValues();
            val.put(UploaderUtilityTable.KEY_UPLOADER_UTILITY_DATE, date);
            clause = UploaderUtilityTable.KEY_UPLOADER_UTILITY_TABLE + " = \"" + LocalDbUtility.getTableName(LocalTables.values()[i]) + "\"";

            localController.update(UploaderUtilityTable.TABLE_UPLOADER_UTILITY, val, clause);
        }
    }

    /**
     * Utility function to get the file part of the given table.
     *
     * @param table
     * @return
     */
    private int getFilePartId(LocalTables table) {
        Cursor c = localController.rawQuery("SELECT * FROM " + UploaderUtilityTable.TABLE_UPLOADER_UTILITY + " WHERE " + UploaderUtilityTable.KEY_UPLOADER_UTILITY_TABLE + " = \"" + LocalDbUtility.getTableName(table) + "\"", null);
        c.moveToNext();
        return c.getInt(4);
    }

    private int getRecordId(LocalTables table) {
        Cursor c = localController.rawQuery("SELECT * FROM " + UploaderUtilityTable.TABLE_UPLOADER_UTILITY + " WHERE " + UploaderUtilityTable.KEY_UPLOADER_UTILITY_TABLE + " = \"" + LocalDbUtility.getTableName(table) + "\"", null);
        c.moveToNext();
        return c.getInt(3);
    }

    /**
     * Utility function to clean the file part of all tables.
     */
    private void cleanFileParts() {
        String clause;
        ContentValues val;
        for(int i = 0; i < LocalTables.values().length; i++) {
            clause = UploaderUtilityTable.KEY_UPLOADER_UTILITY_TABLE + " = \"" + LocalDbUtility.getTableName(LocalTables.values()[i]) + "\"";
            val = new ContentValues();
            val.put(UploaderUtilityTable.KEY_UPLOADER_UTILITY_FILE_PART, 0);
            localController.update(UploaderUtilityTable.TABLE_UPLOADER_UTILITY, val, clause);
        }
    }

    /**
     * Build the query to remove the records from the given table, where primary key id in ]start, end].
     *
     * @param table
     * @param start
     * @param end
     */
    private void removeRecords(LocalTables table, int start, int end) {
        Log.d("UPLOAD DATA SERVICE", "Removing from " + Integer.toString(start) + " to " + Integer.toString(end));

        String clause = LocalDbUtility.getTableColumns(table)[0] + " > " + Integer.toString(start) + " AND " +
                LocalDbUtility.getTableColumns(table)[0] + " <= " + Integer.toString(end);
        localController.delete(LocalDbUtility.getTableName(table), clause);
    }

    /**
     * Build the query to select all records from the given table.
     *
     * @param table
     * @return
     */
    private Cursor getRecords(LocalTables table) {
        String query = "SELECT * FROM " + LocalDbUtility.getTableName(table) +
                " WHERE " + LocalDbUtility.getTableColumns(table)[0] + " > " + Integer.toString(getRecordId(table));
        return localController.rawQuery(query, null);
    }

    /**
     * Build the file name.
     *
     * <subjectid>_<date>_<table>_part<nbPart>.csv
     *
     * @param table
     * @return
     */
    private String buildFileName(LocalTables table) {
        //get current date
        String today = buildDate();
        return userId + "_" + today + "_" + LocalDbUtility.getTableName(table) + "_" + "part" + Integer.toString(getFilePartId(table)) + ".csv";
    }

    /**
     * Utility function to get the string representation of the today date.
     *
     * @return
     */
    private String buildDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("MM-dd-yyyy");
        return mdformat.format(calendar.getTime());
    }

    /**
     * Generate the csv data from the given cursor.
     *
     * @param records
     * @param table
     * @return
     */
    private String toCSV(Cursor records, LocalTables table) {
        String csv = "";
        String[] columns = LocalDbUtility.getTableColumns(table);

        for(int i = 0; i < columns.length; i++) {
            csv += columns[i] + ",";
        }

        csv = csv.substring(0, csv.length()-1);
        csv += "\n";

        do {
            for(int i = 0; i < columns.length; i++) {
                csv += records.getString(i) + ",";
            }
            csv = csv.substring(0, csv.length()-1);
            csv += "\n";
        } while(records.moveToNext());
        csv = csv.substring(0, csv.length()-1);

        return csv;
    }
}
