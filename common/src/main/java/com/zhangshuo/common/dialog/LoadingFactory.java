package com.zhangshuo.common.dialog;

import android.app.Activity;
import android.content.Context;

import com.zhangshuo.common.R;
import com.zhangshuo.common.util.StringUtil;

import java.util.ArrayList;


public class LoadingFactory {

    private static final ArrayList<LoadingDialog> mLoadingDialogList = new ArrayList<>();

    private LoadingFactory() {

    }

    public static void showLoadingDialog(Context context) {
        showLoadingDialog(context, StringUtil.getString(R.string.dialog_loading), true);
    }

    public static void showLoadingDialog(Context context, String message) {
        showLoadingDialog(context, message, true);
    }

    public static void showLoadingDialog(Context context, boolean cancelable) {
        showLoadingDialog(context,  StringUtil.getString(R.string.dialog_loading), cancelable);
    }

    public static void showLoadingDialog(Context context, String message, boolean cancelable) {
        try {
            if (!(context instanceof Activity)) {
                return;
            }
            if (((Activity) context).isFinishing()
                    || ((Activity) context).getWindow() == null
                    || ((Activity) context).getWindowManager() == null) {
                return;
            }
            closeOtherDlg();
            LoadingDialog progressDialog = LoadingDialog.show(context, message, cancelable);
            mLoadingDialogList.add(progressDialog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissLoadingDialog() {
        closeOtherDlg();
    }

    private static void closeOtherDlg() {
        try {
            if (mLoadingDialogList != null) {
                for (LoadingDialog dialog : mLoadingDialogList) {
                    // 部分机型存在not attach to window的问题
                    if (dialog.getWindow() != null) {
                        dialog.dismiss();
                    }
                }
                mLoadingDialogList.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
