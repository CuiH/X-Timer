package com.crossbow.app.x_timer.cloud;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xiaoyanhao on 16/1/7.
 */
public class CloudBackup {
    private String URL = "http://114.215.159.197:3000/dataset/";
    private String ext = ".json";
    private static CloudBackup cloudBackup;

    private CloudBackup() {
        cloudBackup = null;
    }

    public static CloudBackup getInstance() {
        if (cloudBackup == null) {
            cloudBackup = new CloudBackup();
        }
        return cloudBackup;
    }

    public void upload(Context context, String fileName, String userID) {
//        uploadFile(fileName, userID);

        try {
            FileInputStream fis = context.openFileInput(fileName);
            byte[] contents = new byte[fis.available()];

            fis.read(contents);
            fis.close();

            uploadFile(new String(contents), userID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void download(Handler handler, int type, String userID) {
        downloadFile(handler, type, URL + userID + ext);
    }

    private void uploadFile(final String file, final String userID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                String prefix = "--", end = "\r\n", boundary = "******";
                try {
                    connection = (HttpURLConnection)((new URL(URL + userID)).openConnection());

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Charset", "UTF-8");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setConnectTimeout(40000);
                    connection.setReadTimeout(40000);

                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());

                    out.writeBytes(prefix + boundary + end);
                    out.writeBytes("Content-Disposition: form-data; name=\"dataset\"; filename=\""+userID+ext+"\""+end);
                    out.writeBytes(end);
                    out.writeBytes(file);
                    out.writeBytes(end);
                    out.writeBytes(prefix + boundary + prefix + end);
                    out.flush();

                    InputStream in = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();

                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    Log.e("response", response.toString());
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void downloadFile(final Handler handler, final int type, final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection)((new URL(url)).openConnection());

                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(40000);
                    connection.setReadTimeout(40000);

                    InputStream in = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();

                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    Message message = new Message();
                    message.what = type;
                    message.obj = response.toString();
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
