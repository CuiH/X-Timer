package com.crossbow.app.x_timer.fragment.setting;

/**
 * Created by CuiH on 2016/1/1.
 */
public class SettingInfo {
    // 名称
    private String settingName;
    // 描述
    private String settingDescription;
    // 类别：1-点击，2-toggleButton
    private int settingStyle;
    // 描述类别：1-有，2-无
    private int descriptionStyle;

    public SettingInfo(String sName, String sDes, int sStyle, int dStyle) {
        settingName = sName;
        settingDescription = sDes;
        settingStyle = sStyle;
        descriptionStyle = dStyle;
    }

    public String getSettingName() {
        return settingName;
    }

    public String getSettingDescription() {
        return settingDescription;
    }

    public int getSettingStyle() {
        return settingStyle;
    }

    public boolean hasDescription() {
        if (descriptionStyle == 1) {
            return true;
        } else {
            return false;
        }
    }
}
