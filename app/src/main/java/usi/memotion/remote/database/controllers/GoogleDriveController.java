package usi.memotion.remote.database.controllers;

import android.content.Context;

import usi.memotion.remote.database.RemoteStorageController;

/**
 * Created by usi on 17/01/17.
 */

public class GoogleDriveController implements RemoteStorageController  {
    private Context context;

    public GoogleDriveController(Context context) {
        this.context = context;
//        googleApiClient = new GoogleApiClient.Builder(context)
//                .addApi(Drive.API)
//                .addScope(Drive.SCOPE_FILE)
//                .addConnectionCallbacks(this)
//                .build();
//
//        googleApiClient.connect();

    }
    @Override
    public int upload(String path, String data) {
//        googleApiClient.
        //append
        return -1;
    }

    public void createFile(String path) {
        String fileName = getFileName(path);
        String mimeType = "text/plain";

//        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
//                .setTitle(fileName)
//                .setMimeType(mimeType)
//                .setStarred(true).build();

    }

    private String getFileName(String path) {
        String[] split = path.split("/");
        return split[split.length-1];
    }

    public void createDirectory(String path) {

    }
}
