package com.crossbow.app.x_timer.fragment.history;

/**
 * Created by CuiH on 2016/1/6.
 */
public class HistoryAppInfo {
    private String realName;
    private String pkgName;

    private boolean selected;

    public HistoryAppInfo(String rName, String pName) {
        realName = rName;
        pkgName = pName;
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
}
