package com.crossbow.app.x_timer.Utils;

import android.content.Context;

import com.crossbow.app.x_timer.service.AppUsage;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by heath on 15-12-29.
 */
public class FileUtils {
    private Context context;

    public FileUtils(Context context) {
        this.context = context;
    }
    public void store(AppUsage appUsage) {
        try {
            FileOutputStream outputStream = context.openFileOutput(appUsage
                    .getPackageName(), Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter
                    (outputStream));
            writer.write(new Gson().toJson(appUsage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AppUsage load(String pkgName) {
        FileInputStream inputStream = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        //文件不存在，直接返回一个新的AppUsage
        if (!new File("/data/data/com.crossbow.app.x_timer/" + pkgName).exists
                ()) {
            return new AppUsage(pkgName);
        }
        //文件存在则读取文件，转换成AppUsage
        try {
            inputStream = context.openFileInput(pkgName);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            return parseJSOMWithGson(content.toString());
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
}
