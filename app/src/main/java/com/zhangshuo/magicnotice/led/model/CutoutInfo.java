package com.zhangshuo.magicnotice.led.model;

public class CutoutInfo {
    private int left = 0;
    private int top = 0;
    private int right = 0;
    private int bottom = 0;
    private int radius = 0;


    public CutoutInfo() {
        super();
    }

    public CutoutInfo copy(float rate) {
        CutoutInfo cutoutInfo = new CutoutInfo();
        cutoutInfo.setLeft((int) (left * rate));
        cutoutInfo.setTop((int) (top * rate));
        cutoutInfo.setRight((int) (right * rate));
        cutoutInfo.setBottom((int) (bottom * rate));
        cutoutInfo.setRadius((int) (radius * rate));
        return cutoutInfo;
    }

    public int getWidth() {
        return right - left;
    }

    public int getHeight() {
        return bottom - top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

}
