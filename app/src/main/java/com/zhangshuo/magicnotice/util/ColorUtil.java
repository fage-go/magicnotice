package com.zhangshuo.magicnotice.util;

import android.graphics.Color;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorUtil {

    public static List<String> getColorList() {
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList("#2E333B", "#FFFFFF", "#8799A3", "#E54D42", "#F37B1D", "#FBBD08",
                "#8DC63F", "#39B54A", "#1CBBB4", "#0081FF", "#6739B6", "#E03997"));
        return list;
    }

    public static int[] getRainColorArray() {
        int[] color = new int[]{0xFF0000, 0xFFA500, 0xFFFF00, 0x00FF00,
                0x00FFFF, 0x0000FF, 0x0000FF, 0x8B00FF};
        return color;
    }

    public static List<String> getGradualColorList() {
        List<String> list = new ArrayList<>();
        list.add("#f43f3b,#ec008c");
        list.add("#ff9700,#ed1c24");
        list.add("#39b54a,#8dc63f");
        list.add("#0081ff,#1cbbb4");
        list.add("#6739b6,#e03997");
        list.add("#9000ff,#5e00ff");
        //红、橙、黄、绿、青、蓝、紫
        list.add("#FF0000,#FFA500,#FFFF00,#00FF00,#00FFFF,#0000FF,#8B00FF");
        return list;
    }

    public static String getHexString(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public static int getColor(String colorHex) {
        if (TextUtils.isEmpty(colorHex)) {
            return Color.parseColor("#FFFFFF");
        }
        return Color.parseColor(colorHex);
    }

    public static String getColorArray(int[] colorArray) {
        if (colorArray == null || colorArray.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < colorArray.length; i++) {
            //颜色int转Hex格式，并且补充完整
            String colorHex = String.format("#%06X", (0xFFFFFF & colorArray[i]));
            stringBuilder.append(colorHex);
            if (i != colorArray.length - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    public static int[] getColorArray(String colorArray) {
        if (TextUtils.isEmpty(colorArray)) {
            return null;
        }
        String[] colorHexArray = colorArray.split(",");
        int[] colorIntArray = new int[colorHexArray.length];
        for (int i = 0; i < colorHexArray.length; i++) {
            colorIntArray[i] = Color.parseColor(colorHexArray[i]);
        }
        return colorIntArray;
    }

}
