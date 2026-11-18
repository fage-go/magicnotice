package com.zhangshuo.magicnotice.ui.dialog;

import android.view.View;

import com.zhangshuo.common.dialog.BaseBottomDialog;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.magicnotice.config.LocalDataKeys;
import com.zhangshuo.magicnotice.databinding.DialogRandomAvatarBinding;
import com.zhangshuo.magicnotice.util.ChineseToFirstCharUtil;

public class RandomAvatarDialog extends BaseBottomDialog<DialogRandomAvatarBinding> {
    private String RANDOM_AVATAR_URL = "https://ui-avatars.com/api/?bold=true&background=random&name=[NICKNAME]";
    private OnCallback onCallback;

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }

    @Override
    public void initView() {
        binding.tvTitleTip.getPaint().setFakeBoldText(true);
        binding.mbNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertTo(binding.etTitle.getText().toString());
            }
        });
    }

    private void convertTo(String chinese) {
        String engChar = ChineseToFirstCharUtil.convertAndClearAll(chinese);
        String avatar = RANDOM_AVATAR_URL.replace("[NICKNAME]", engChar);
        LocalDataUtil.get().saveData(LocalDataKeys.SKEY_NICK_NAME, chinese);
        LocalDataUtil.get().saveData(LocalDataKeys.SKEY_AVATAR, avatar);
        if (onCallback != null) {
            onCallback.onConfirm();
        }
    }

    public interface OnCallback {
        void onConfirm();
    }
}
