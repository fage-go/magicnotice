package com.zhangshuo.magicnotice.ui.dialog;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.zhangshuo.common.dialog.BaseTipsDialog;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallUtils;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.common.util.UIUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.DialogSavePictureBinding;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.util.TimeUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

public class SavePictureDialog extends BaseTipsDialog<DialogSavePictureBinding> {
    private NotificationData notificationData;

    public static void showDialog(Context context, NotificationData notificationData) {
        SavePictureDialog savePictureDialog = new SavePictureDialog();
        savePictureDialog.setNotificationData(notificationData);
        savePictureDialog.show(context);
    }

    public void setNotificationData(NotificationData notificationData) {
        this.notificationData = notificationData;
    }


    @Override
    public void initView() {
        binding.tvShowTitle.getPaint().setFakeBoldText(true);
        String titleIcon = notificationData.getIconPath();
        if (TextUtils.isEmpty(titleIcon)) {
            titleIcon = notificationData.getLogoPath();
        }
        Glide.with(getContext())
                .load(notificationData.getLogoPath())
                .into(binding.itemIvAppLogo);
        binding.itemTvAppName.setText(notificationData.getAppName());
        binding.itemTvTime.setText(TimeUtil.formatYMDHMSDate(notificationData.getPostTime()));
        Glide.with(getContext())
                .load(titleIcon)
                .transform(new RoundedCorners(UIUtil.dp2Px(4)))
                .into(binding.ivLogo);
        binding.tvTitle.setText(notificationData.getTitle());
        binding.tvContent.setText(notificationData.getContent());
        binding.switchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeMode(isChecked);
            }
        });
        binding.mbSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    saveImage(binding.clNotice);
                } else {
                    PermissionUtils.permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .callback(new PermissionUtils.SingleCallback() {
                                @Override
                                public void callback(boolean isAllGranted, @NonNull List<String> granted, @NonNull List<String> deniedForever, @NonNull List<String> denied) {
                                    if (isAllGranted) {
                                        saveImage(binding.clNotice);
                                    }
                                }
                            })
                            .request();
                }
            }
        });
    }

    private void changeMode(boolean dark) {
        binding.clNotice.setBackgroundResource(dark ? R.drawable.bg_save_picture_dark : R.drawable.bg_save_picture_light);
        binding.tvTitle.setTextColor(dark ? Color.WHITE : Color.parseColor("#2E333B"));
        binding.tvContent.setTextColor(dark ? Color.parseColor("#AAFFFFFF") : Color.parseColor("#60646B"));
        binding.itemTvAppName.setTextColor(dark ? Color.parseColor("#9FFFFFFF") : Color.parseColor("#919499"));
        binding.itemTvTime.setTextColor(dark ? Color.parseColor("#9FFFFFFF") : Color.parseColor("#919499"));
    }

    private void saveImage(View view) {
        showLoading();
        AsyncCallUtils.asyncCall(new Callable<String>() {
            @Override
            public String call() throws Exception {
                int offsetSize = UIUtil.dp2Px(20);
                int width = view.getWidth() + offsetSize * 2;
                int height = view.getHeight() + offsetSize * 2;
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.parseColor("#EAEBED"));
                canvas.drawRect(0, 0, width, height, paint);
                canvas.save();
                canvas.translate(offsetSize, offsetSize);
                view.draw(canvas);
                canvas.restore();
                File saveFile = ImageUtils.save2Album(bitmap, StringUtil.getString(R.string.app_name), Bitmap.CompressFormat.PNG);
                try {
                    bitmap.recycle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (saveFile != null) {
                    return saveFile.getAbsolutePath();
                } else {
                    return "";
                }
            }
        }, new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                hideLoading();
                dismiss();
                if (TextUtils.isEmpty(result)) {
                    ToastManager.get().showShortToast(StringUtil.getString(R.string.save_picture_tip_fail));
                } else {
                    ToastManager.get().showShortToast(StringUtil.getString(R.string.save_picture_tip_suc));
                }
            }
        });
    }
}
