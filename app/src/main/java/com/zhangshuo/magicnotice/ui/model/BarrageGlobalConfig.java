package com.zhangshuo.magicnotice.ui.model;

import com.zhangshuo.magicnotice.config.AppStatusConfig;

public class BarrageGlobalConfig {
    private int pTop;
    private int hTop;
    private int maxLines;
    private int clickType;

    public static BarrageGlobalConfig getDefault() {
        BarrageGlobalConfig barrageGlobalConfig = new BarrageGlobalConfig();
        barrageGlobalConfig.pTop = 100;
        barrageGlobalConfig.hTop = 100;
        barrageGlobalConfig.maxLines = 2;
        return barrageGlobalConfig;
    }

    public int getTop() {
        return AppStatusConfig.get().isPortrait() ? getPTop() : getHTop();
    }

    public int getPTop() {
        return pTop;
    }

    public void setPTop(int pTop) {
        this.pTop = pTop;
    }

    public int getHTop() {
        return hTop;
    }

    public void setHTop(int hTop) {
        this.hTop = hTop;
    }

    public int getMaxLines() {
        return maxLines;
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }
}
