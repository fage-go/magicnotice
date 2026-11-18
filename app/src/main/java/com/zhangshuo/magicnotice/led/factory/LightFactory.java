package com.zhangshuo.magicnotice.led.factory;

import android.graphics.Canvas;

import com.zhangshuo.magicnotice.led.LightView;

public interface LightFactory {

    void onInit(LightView lightView);

    void drawLight(Canvas canvas, float progress);

    long getAnimDuration(int speed);

    void onDestory();

}
