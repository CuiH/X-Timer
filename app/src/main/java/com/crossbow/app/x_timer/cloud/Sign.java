package com.crossbow.app.x_timer.cloud;

/**
 * Created by kinsang on 16-1-8.
 */
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xiaoyanhao on 16/1/6.
 */
public class Sign {
    private Handler handler;
    private int type;
    private String data;
    private String url = "http://114.215.159.197:3000";

    private static final int SIGNIN = 1;
    private static final int SIGNUP = 2;

    public Sign(Handler handler, int type, String username, String password) {
        this.handler = handler;
        this.type = type;
        this.data = "username=" + username + "&" + "password=" + password;
        if (type == SIGNIN) {
            url += "/signin";
        } else if (type == SIGNUP) {
            url += "/signup";
        }
        sendRequestWithHttpURLConnection();
    }


    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection)((new URL(url)).openConnection());

                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(40000);
                    connection.setReadTimeout(40000);

                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());

                    out.writeBytes(data);

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
