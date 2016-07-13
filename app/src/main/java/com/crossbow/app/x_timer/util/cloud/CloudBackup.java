package com.crossbow.app.x_timer.util.cloud;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaoyanhao on 16/1/7.
 */
public class CloudBackup {
    private String URL = "http://114.215.159.197:3000/dataset/";
    private static CloudBackup cloudBackup;

    private Context context;

    private Handler handler;
    private int type;

    private CloudBackup() {
        cloudBackup = null;
    }

    public static CloudBackup getInstance() {
        if (cloudBackup == null) {
            cloudBackup = new CloudBackup();
        }
        return cloudBackup;
    }

    public void upload(Context context, ArrayList<String> fileNames, String userID,
                       Handler h, int t) {
        handler = h;
        type = t;

        Map<String, String> files = new HashMap<>();

        try {
            for (String name: fileNames) {
                FileInputStream fis = context.openFileInput(name);
                byte[] contents = new byte[fis.available()];

                fis.read(contents);
                fis.close();

                files.put(name, new String(contents));
            }

            uploadFile(files, userID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void download(Context c, Handler h, int t, String userID) {
        handler = h;
        type = t;
        context = c;
        downloadFile(userID);
    }

    private void uploadFile(final Map<String, String> files, final String userID) {
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
                    OutputStreamWriter outSW = new OutputStreamWriter(out, "UTF-8");
                    BufferedWriter bw = new BufferedWriter(outSW);

                    for (Map.Entry<String, String> entry : files.entrySet()) {
                        bw.write(prefix + boundary + end);
                        bw.write("Content-Disposition: form-data; name=\"dataset\"; " +
                                "filename=\"" + entry.getKey() + "\"" + end);
                        bw.write(end);

                        bw.write(entry.getValue());
                        bw.write(end);
                    }

                    bw.write(prefix + boundary + prefix + end);
                    bw.flush();

                    InputStream in = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    Log.e("response", response.toString());

                    out.close();

                    Message message = new Message();
                    message.what = type;
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

    private void downloadFile(final String userID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection)((new URL(URL+userID)).openConnection());

                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(40000);
                    connection.setReadTimeout(40000);

                    InputStream in = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder response = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    HttpURLConnection connection2 = null;

                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            String fileName = jsonArray.get(i).toString();
                            connection2 = (HttpURLConnection) ((new URL(URL + userID +
                                    "/" +fileName)).openConnection());

                            connection2.setRequestMethod("GET");
                            connection2.setConnectTimeout(40000);
                            connection2.setReadTimeout(40000);

                            InputStreamReader isr = new
                                    InputStreamReader(connection2.getInputStream(), "UTF-8");
                            BufferedReader reader2 = new BufferedReader(isr);

                            StringBuilder response2 = new StringBuilder();

                            String line2;
                            while ((line2 = reader2.readLine()) != null) {
                                response2.append(line2);
                            }

                            // System.out.println(fileName+"::::::"+response2.toString());

                            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                            outputStream.write(response2.toString().getBytes());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // show();

                    Message message = new Message();
                    message.what = type;
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

    private void show() {
        File root = new File("/data/data/com.crossbow.app.x_timer/files");
        File[] files = root.listFiles();
        for (File file : files) {
            System.out.println(file.getName());
            try {
                FileInputStream inputStream = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder content = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }

                System.out.println("im here!!!"+content.toString());
            }catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
