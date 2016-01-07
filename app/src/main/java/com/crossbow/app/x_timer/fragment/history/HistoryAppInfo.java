package com.crossbow.app.x_timer.fragment.history;

import android.graphics.drawable.Drawable;

/**
 * Created by CuiH on 2016/1/6.
 */
public class HistoryAppInfo {
    private String realName;
    private String pkgName;

    private Drawable icon;

    private boolean selected;

    public HistoryAppInfo(String rName, String pName, Drawable i) {
        realName = rName;
        pkgName = pName;
        icon = i;
        selected = false;
    }

    public void setSelected(boolean s) {
        selected = s;
    }

    public boolean getSelected() {
        return selected;
    }

    public String getRealName() {
        return realName;
    }

    public String getPkgName() {
        return pkgName;
    }

    public Drawable getIcon() {
        return icon;
    }
}
