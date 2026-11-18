package com.zhangshuo.magicnotice.widget.model;

import java.io.Serializable;

public class WidgetInfo implements Serializable {
    private int appWidgetId;
    private int configId;
    private String configName;
    private boolean configToggle;

    public int getAppWidgetId() {
        return appWidgetId;
    }

    public void setAppWidgetId(int appWidgetId) {
        this.appWidgetId = appWidgetId;
    }

    public int getConfigId() {
        return configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public boolean isConfigToggle() {
        return configToggle;
    }

    public void setConfigToggle(boolean configToggle) {
        this.configToggle = configToggle;
    }

}
