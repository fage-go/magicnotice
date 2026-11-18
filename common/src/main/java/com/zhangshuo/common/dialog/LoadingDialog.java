package com.zhangshuo.common.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.zhangshuo.common.R;
import com.zhangshuo.common.util.StringUtil;

/**
 * @author: zhangshuo
 * Date:2020/12/31
 * @doc: 基类loadingDialog
 */
public class LoadingDialog extends AlertDialog {

    private String message = StringUtil.getString(R.string.dialog_loading);

    public LoadingDialog(Context context) {
        super(context, R.style.base_CommonDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_dialog_loading);
        ((TextView) findViewById(R.id.tv_loading)).setText(message);
    }

    public void setMessage(String message) {
        this.message = TextUtils.isEmpty(message) ? StringUtil.getString(R.string.dialog_loading) : message;
    }

    public void close() {
        try {
            dismiss();
        } catch (Exception e) {

        }
    }

    public static LoadingDialog show(Context context) {
        return show(context, StringUtil.getString(R.string.dialog_loading));
    }

    public static LoadingDialog show(Context context, boolean cancelable) {
        return show(context, StringUtil.getString(R.string.dialog_loading), cancelable);
    }

    public static LoadingDialog show(Context context, String message) {
        return show(context, message, true);
    }

    public static LoadingDialog show(Context context, String message, boolean cancelable) {
        LoadingDialog dialog = new LoadingDialog(context);
        dialog.setMessage(message);
        dialog.setCancelable(cancelable);
        if (dialog.isShowing()) return dialog;
        dialog.show();
        return dialog;
    }
}
