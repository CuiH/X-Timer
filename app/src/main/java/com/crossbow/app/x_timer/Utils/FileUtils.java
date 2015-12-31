package com.crossbow.app.x_timer.Utils;

import android.content.Context;

import com.crossbow.app.x_timer.service.AppUsage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by heath on 15-12-29.
 */
public class FileUtils {
    private Context context;

    public FileUtils(Context context) {
        this.context = context;
    }

    public ArrayList<String> getApplist() {
        if (!new File("/data/data/com.crossbow.app.x_timer/AppList").exists
                ()) {
            return new ArrayList<String>();
        }
        try {
            Gson gson = new Gson();
            return gson.fromJson(getContent("AppList"), new
                    TypeToken<ArrayList<String> >() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void storeAppList(ArrayList<String> appList) {
        try {
            getWriter("AppList").write(new Gson().toJson(appList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void store(AppUsage appUsage) {
        try {
            getWriter(appUsage.getPackageName()).write(new Gson().toJson
                    (appUsage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AppUsage load(String pkgName) {

        //文件不存在，直接返回一个新的AppUsage
        if (!new File("/data/data/com.crossbow.app.x_timer/" + pkgName).exists
                ()) {
            return new AppUsage(pkgName);
        }
        //文件存在则读取文件，转换成AppUsage
        try {
            return parseJSOMWithGson(getContent(pkgName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private AppUsage parseJSOMWithGson(String json) {
        Gson gson = new Gson();
        AppUsage appUsage = gson.fromJson(json, AppUsage.class);
        return appUsage;
    }

    private BufferedWriter getWriter(String fileName) throws IOException {
        FileOutputStream outputStream = context.openFileOutput(fileName, Context
                .MODE_PRIVATE);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter
                (outputStream));
        return writer;
    }

    private String getContent(String fileName) throws IOException {
        FileInputStream inputStream = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        inputStream = context.openFileInput(fileName);
        reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        return content.toString();
    }
}
