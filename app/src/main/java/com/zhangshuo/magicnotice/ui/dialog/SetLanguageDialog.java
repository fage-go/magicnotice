package com.zhangshuo.magicnotice.ui.dialog;

import android.view.View;

import com.blankj.utilcode.util.LanguageUtils;
import com.zhangshuo.common.dialog.BaseBottomSheetDialog;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.LocalDataKeys;
import com.zhangshuo.magicnotice.databinding.DialogSetLanguageBinding;

import java.util.Locale;

public class SetLanguageDialog extends BaseBottomSheetDialog<DialogSetLanguageBinding> {
    @Override
    public void initView() {
        Locale appLocale = LanguageUtils.getAppliedLanguage();
        if (appLocale == null) {
            appLocale = LanguageUtils.getSystemLanguage();
        }
        if (appLocale == null || appLocale.equals(Locale.CHINA)) {
            binding.tvChineseTitle.setTextColor(getResources().getColor(R.color.base_color_blue));
            binding.tvChineseTitle.getPaint().setFakeBoldText(true);
        } else {
            binding.tvEnglishTitle.setTextColor(getResources().getColor(R.color.base_color_blue));
            binding.tvEnglishTip.getPaint().setFakeBoldText(true);
        }
        binding.clChinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LanguageUtils.applyLanguage(Locale.CHINA);
                LocalDataUtil.get().saveData(LocalDataKeys.IKEY_LANGUAGE, 1);
                close();
            }
        });
        binding.clEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LanguageUtils.applyLanguage(Locale.ENGLISH);
                LocalDataUtil.get().saveData(LocalDataKeys.IKEY_LANGUAGE, 2);
                close();
            }
        });
    }
}
