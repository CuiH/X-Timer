package com.crossbow.app.x_timer.Utils;

import android.content.Context;

import com.crossbow.app.x_timer.service.AppUsage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by heath on 15-12-29.
 */
public class FileUtils {
    // 记录上下文
    private Context mContext;

    public FileUtils(Context context) {
        this.mContext = context;
    }

    // 获取保存的监听列表
    public ArrayList<String> getAppList() {
        File file = new File("/data/data/com.crossbow.app.x_timer/files/appList");
        if (!file.exists()) {
            return new ArrayList<String>();
        }

        try {
            Gson gson = new Gson();
            return gson.fromJson(getContent(file), new
                    TypeToken<ArrayList<String> >() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 保存监听列表
    public void storeAppList(ArrayList<String> appList) {
        System.out.println(appList);
        try {
            getOutputStream("appList").write(new Gson().toJson(appList).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void storeAppInfo(AppUsage appUsage) {
        try {
            getOutputStream(appUsage.getPackageName())
                    .write(new Gson().toJson(appUsage).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AppUsage loadAppInfo(String pkgName) {
        //文件不存在，直接返回一个新的AppUsage
        File file = new File("/data/data/com.crossbow.app.x_timer/files/" + pkgName);
        if (!file.exists()) {
            return new AppUsage(pkgName);
        }

        //文件存在则读取文件，转换成AppUsage
        try {
            return parseJSONWithGSON(getContent(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 通过GSON读取json文件
    private AppUsage parseJSONWithGSON(String json) {
        Gson gson = new Gson();
        AppUsage appUsage = gson.fromJson(json, AppUsage.class);
        return appUsage;
    }

    // 获取file的OutputStream
    private FileOutputStream getOutputStream(String fileName) {
        try {
            FileOutputStream outputStream = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);

            return outputStream;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 获取file内容，返回String
    private String getContent(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder content = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 删除应用的信息
    private boolean deleteAppInfo(String pkgName) {
        File root = new File("/data/data/com.crossbow.app.x_timer/files");
        File[] files = root.listFiles();
        for (File file : files) {
            if (file.getName().equals(pkgName)) {
                file.delete();
                return true;
            }
        }

        return false;
    }
}
