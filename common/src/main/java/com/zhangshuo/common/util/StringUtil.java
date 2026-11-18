package com.zhangshuo.common.util;

import android.text.TextUtils;

import com.zhangshuo.common.AppUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtil {

    public static String getFillValue(int value) {
        if (value < 10) {
            return "0" + value;
        }
        return value + "";
    }

    public static String getString(int resId) {
        return AppUtil.get().getApplication().getString(resId);
    }

    public static String[] getStringArray(int resId){
        return AppUtil.get().getApplication().getResources().getStringArray(resId);
    }

    public static int stringToInt(String value) {
        try {
            if (TextUtils.isEmpty(value)) {
                return 0;
            }
            return Integer.parseInt(value);
        } catch (Exception exception) {
            return 0;
        }
    }

    public static long stringToLong(String value) {
        try {
            if (TextUtils.isEmpty(value)) {
                return 0;
            }
            return Long.parseLong(value);
        } catch (Exception exception) {
            return 0;
        }
    }

    public static double stringToDouble(String value) {
        try {
            if (TextUtils.isEmpty(value)) {
                return 0;
            }
            return Double.parseDouble(value);
        } catch (Exception exception) {
            return 0;
        }
    }

    public static ArrayList getList(Object[] objects) {
        return new ArrayList(Arrays.asList(objects));
    }

    public static boolean isNotEmpty(List list) {
        return !isEmpty(list);
    }

    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean isSameString(String str1, String ruleStr) {
        if (TextUtils.isEmpty(str1) || TextUtils.isEmpty(ruleStr)) return false;
        String targetName = ruleStr.replace("%", "");
        if (ruleStr.startsWith("%") && ruleStr.endsWith("%")) {
            return str1.contains(targetName);
        } else if (ruleStr.startsWith("%")) {
            return str1.endsWith(targetName);
        } else if (ruleStr.endsWith("%")) {
            return str1.startsWith(targetName);
        } else {
            return str1.equals(targetName);
        }
    }
}
