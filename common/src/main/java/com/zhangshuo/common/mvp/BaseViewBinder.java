package com.zhangshuo.common.mvp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.LogUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public class BaseViewBinder {

    public BaseViewBinder() {
    }


    @Nullable
    public static <D extends ViewBinding> Object createViewBinding(Activity activity, LayoutInflater layoutInflater) {
        try {
            ParameterizedType type = (ParameterizedType) activity.getClass().getGenericSuperclass();
            if (type != null) {
                Type[] actualTypeArguments = type.getActualTypeArguments();
                Class<D> tClass = (Class<D>) actualTypeArguments[0];
                Method inflate = tClass.getMethod("inflate", LayoutInflater.class);
                return inflate.invoke(tClass, layoutInflater);
            }
        } catch (Exception ignore) {
            LogUtils.e("BasePresent Activity createViewBinding > " + ignore);
            ignore.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static <D extends ViewBinding> Object createViewBinding(Fragment fragment, LayoutInflater layoutInflater, ViewGroup container) {
        D d = null;
        try {
            ParameterizedType type = (ParameterizedType) fragment.getClass().getGenericSuperclass();
            if (type != null) {
                Type[] actualTypeArguments = type.getActualTypeArguments();
                Class<D> tClass = (Class<D>) actualTypeArguments[0];
                Method inflate = tClass.getMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
                return inflate.invoke(tClass, layoutInflater, container, false);
            }
        } catch (Exception e) {
            LogUtils.e("BasePresent Fragment createViewBinding > " + e);
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static <D extends ViewBinding> Object createViewBindingObject(Object object, LayoutInflater layoutInflater, ViewGroup container) {
        D d = null;
        try {
            ParameterizedType type = findParentType(object.getClass());
            if (type != null) {
                Type[] actualTypeArguments = type.getActualTypeArguments();
                Class<D> tClass = (Class<D>) actualTypeArguments[0];
                Method inflate = tClass.getMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
                return inflate.invoke(tClass, layoutInflater, container, false);
            }
        } catch (Exception e) {
            LogUtils.e("BasePresent Object createViewBindingObject > " + e);
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static <D extends ViewBinding> Object createViewBindingObject(Object object, LayoutInflater layoutInflater) {
        D d = null;
        try {
            ParameterizedType type = findParentType(object.getClass());
            if (type != null) {
                Type[] actualTypeArguments = type.getActualTypeArguments();
                Class<D> tClass = (Class<D>) actualTypeArguments[0];
                Method inflate = tClass.getMethod("inflate", LayoutInflater.class);
                return inflate.invoke(tClass, layoutInflater);
            }
        } catch (Exception e) {
            LogUtils.e("BasePresent Object createViewBindingObject > " + e);
            e.printStackTrace();
        }
        return null;
    }

    private static ParameterizedType findParentType(Class clazz) {
        try {
            if (clazz == Object.class || clazz == Class.class) return null;
            Type genericSuperclass = clazz.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                return (ParameterizedType) genericSuperclass;
            } else {
                return findParentType(clazz.getSuperclass());
            }
        } catch (Exception e) {
            return null;
        }
    }
}
