package com.zhangshuo.magicnotice.ui.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;

import androidx.annotation.NonNull;

import com.zhangshuo.common.dialog.BaseBottomSheetDialog;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.DialogSpecialKeepAliveBinding;
import com.zhangshuo.magicnotice.notice.ListenerStatusDotMode;
import com.zhangshuo.magicnotice.ui.model.OptionModel;
import com.zhangshuo.magicnotice.ui.view.ConfigRuleTipView;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import razerdp.basepopup.BasePopupWindow;

public class SpecialKeepAliveDialog extends BaseBottomSheetDialog<DialogSpecialKeepAliveBinding> {
    private static final String CONFIG = "config";
    private static final List<Integer> OPACITY_STEPS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    private static final int[] POSITION_LABELS = {R.string.special_keep_alive_top_left,
            R.string.special_keep_alive_top_right, R.string.special_keep_alive_bottom_left,
            R.string.special_keep_alive_bottom_right};

    private int corner;
    private int color;
    private int opacity;
    private BasePopupWindow activePopup;

    public static SpecialKeepAliveDialog create(int saved) {
        SpecialKeepAliveDialog dialog = new SpecialKeepAliveDialog();
        Bundle args = new Bundle();
        args.putInt(CONFIG, saved);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        int config = savedState == null ? requireArguments().getInt(CONFIG) : savedState.getInt(CONFIG);
        corner = ListenerStatusDotMode.corner(config);
        color = ListenerStatusDotMode.color(config);
        opacity = ListenerStatusDotMode.opacity(config);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CONFIG, ListenerStatusDotMode.configure(false, corner, color, opacity));
    }

    @Override
    public void initView() {
        showPosition();
        showColor();
        showOpacity();
        binding.positionCell.setOnClickListener(v -> {
            if (activePopup != null) return;
            List<OptionModel> positions = Arrays.asList(
                    new OptionModel(getString(POSITION_LABELS[0]), "0"),
                    new OptionModel(getString(POSITION_LABELS[1]), "1"),
                    new OptionModel(getString(POSITION_LABELS[2]), "2"),
                    new OptionModel(getString(POSITION_LABELS[3]), "3"));
            SelectOptionsPopup popup =
                    new SelectOptionsPopup(getDialog(), positions, Integer.toString(corner));
            popup.setOnCallback(value -> {
                if (value != null) {
                    corner = Integer.parseInt(value.getValue());
                    showPosition();
                }
            }, onDismiss(binding.positionCell));
            showPopup(popup, binding.positionCell, Gravity.TOP | Gravity.LEFT);
        });
        binding.colorCell.setOnClickListener(v -> {
            if (activePopup != null) return;
            SelectColorPopup popup = new SelectColorPopup(getDialog(), colorHex());
            popup.setOnCallback(value -> {
                if (value == null || value.isEmpty()) return;
                try {
                    color = Color.parseColor(value) | 0xFF000000;
                    showColor();
                } catch (IllegalArgumentException ignored) { }
            }, onDismiss(binding.colorCell));
            showPopup(popup, binding.colorCell, Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        });
        binding.opacityCell.setOnClickListener(v -> {
            if (activePopup != null) return;
            SelectStepPopup popup = new SelectStepPopup(getDialog(), OPACITY_STEPS, opacity);
            popup.setOnCallback(value -> {
                opacity = value;
                showOpacity();
                return null;
            }, onDismiss(binding.opacityCell));
            showPopup(popup, binding.opacityCell, Gravity.TOP | Gravity.RIGHT);
        });
        binding.confirm.setOnClickListener(v -> {
            if (getTargetFragment() instanceof OnConfirm) {
                ((OnConfirm) getTargetFragment()).confirmSpecialKeepAlive(
                        ListenerStatusDotMode.configure(true, corner, color, opacity));
            }
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        if (activePopup != null) activePopup.dismiss();
        activePopup = null;
        super.onDestroyView();
    }

    private String colorHex() {
        return String.format(Locale.ROOT, "#%06X", color & 0xFFFFFF);
    }

    private void showPopup(BasePopupWindow popup, ConfigRuleTipView cell, int gravity) {
        activePopup = popup;
        cell.setSelect(true);
        popup.setPopupFadeEnable(true)
                .setPopupGravityMode(BasePopupWindow.GravityMode.ALIGN_TO_ANCHOR_SIDE,
                        BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR)
                .setPopupGravity(gravity)
                .showPopupWindow(cell);
    }

    private BasePopupWindow.OnDismissListener onDismiss(ConfigRuleTipView cell) {
        return new BasePopupWindow.OnDismissListener() {
            @Override public void onDismiss() {
                cell.setSelect(false);
                activePopup = null;
            }
        };
    }

    private void showPosition() {
        binding.positionCell.setValue(getString(POSITION_LABELS[corner]));
    }

    private void showColor() {
        binding.colorCell.setValue(colorHex());
    }

    private void showOpacity() {
        binding.opacityCell.setValue(NumberFormat.getPercentInstance().format(opacity / 10d));
    }

    public interface OnConfirm {
        void confirmSpecialKeepAlive(int packedConfig);
    }
}
