package usi.justmove.remote.database.controllers;


import android.os.AsyncTask;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import usi.justmove.remote.database.RemoteStorageController;
import java.util.Observable;
import java.util.concurrent.CountDownLatch;

/**
 * Created by usi on 17/01/17.
 */
public class SwitchDriveController implements RemoteStorageController {
    private String serverAddress;
    private String accessToken;
    private String password;
    private CountDownLatch doneSignal;
    private int httpResponse;

    public SwitchDriveController(String serverAddress, String accessToken, String password) {
        this.serverAddress = serverAddress;
        this.accessToken = accessToken;
        this.password = password;
        doneSignal = new CountDownLatch(1);
    }


    @Override
    public int upload(String fileName, String data) {
        new DataUploadTask(serverAddress, accessToken).execute(fileName, data);

        try {
            doneSignal.await();
        } catch (InterruptedException e) {
//            e.printStackTrace();
            httpResponse = -1;
        }
        doneSignal = new CountDownLatch(1);
        return httpResponse;
    }

    private class DataUploadTask extends AsyncTask<String, Void, Integer> {
        private String serverAddress;
        private String accessToken;

        public DataUploadTask(String serverAddress, String accessToken) {
            this.serverAddress = serverAddress;
            this.accessToken = accessToken;
        }

        @Override
        protected Integer doInBackground(String... params) {
            int httpStatus;

            try {
                String[] data = new String[2];

                int i = 0;
                for(String par: params) {
                    data[i] = par;
                    i++;
                }

                URL url = new URL(serverAddress + data[0]);

                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(accessToken, password.toCharArray());
                    }
                });
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Accept", "*/*");
                conn.getOutputStream().write(data[1].getBytes());
                conn.getOutputStream().flush();
                conn.getOutputStream().close();
                httpStatus = conn.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
                httpStatus = -1;
            }

            return httpStatus;
        }

        @Override
        protected void onPostExecute(Integer result) {
            httpResponse = result;
            doneSignal.countDown();
        }
    }
}




