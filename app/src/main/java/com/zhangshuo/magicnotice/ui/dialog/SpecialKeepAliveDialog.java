package com.zhangshuo.magicnotice.ui.dialog;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;

import androidx.annotation.NonNull;

import com.zhangshuo.common.dialog.BaseBottomSheetDialog;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.DialogSpecialKeepAliveBinding;
import com.zhangshuo.magicnotice.notice.ListenerStatusDotMode;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import razerdp.basepopup.BasePopupWindow;

public class SpecialKeepAliveDialog extends BaseBottomSheetDialog<DialogSpecialKeepAliveBinding> {
    private static final String CONFIG = "config";
    private static final List<Integer> OPACITY_STEPS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

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
        int[] positions = {R.id.position_top_left, R.id.position_top_right,
                R.id.position_bottom_left, R.id.position_bottom_right};
        binding.positions.check(positions[corner]);
        binding.positions.setOnCheckedChangeListener((group, id) -> {
            for (int i = 0; i < positions.length; i++) {
                if (positions[i] == id) corner = i;
            }
        });
        showColor();
        showOpacity();
        binding.colorRow.setOnClickListener(v -> {
            if (activePopup != null) return;
            SelectColorPopup popup = new SelectColorPopup(getDialog(), colorHex());
            activePopup = popup;
            popup.setOnCallback(value -> {
                if (value == null || value.isEmpty()) return;
                try {
                    color = Color.parseColor(value) | 0xFF000000;
                    showColor();
                } catch (IllegalArgumentException ignored) { }
            }, new BasePopupWindow.OnDismissListener() {
                @Override public void onDismiss() { activePopup = null; }
            }).setPopupFadeEnable(true)
                    .setPopupGravity(Gravity.BOTTOM | Gravity.CENTER)
                    .showPopupWindow(v);
        });
        binding.opacityRow.setOnClickListener(v -> {
            if (activePopup != null) return;
            SelectStepPopup popup = new SelectStepPopup(getDialog(), OPACITY_STEPS, opacity);
            activePopup = popup;
            popup.setOnCallback(value -> {
                opacity = value;
                showOpacity();
                return null;
            }, new BasePopupWindow.OnDismissListener() {
                @Override public void onDismiss() { activePopup = null; }
            }).setPopupFadeEnable(true)
                    .setPopupGravityMode(BasePopupWindow.GravityMode.ALIGN_TO_ANCHOR_SIDE, BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR)
                    .setPopupGravity(Gravity.BOTTOM | Gravity.RIGHT)
                    .showPopupWindow(v);
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

    private void showColor() {
        binding.colorValue.setText(colorHex());
        GradientDrawable preview = new GradientDrawable();
        preview.setShape(GradientDrawable.OVAL);
        preview.setColor(color);
        preview.setStroke(1, 0xFF868A92);
        binding.colorPreview.setBackground(preview);
    }

    private void showOpacity() {
        binding.opacityValue.setText(NumberFormat.getPercentInstance().format(opacity / 10d));
    }

    public interface OnConfirm {
        void confirmSpecialKeepAlive(int packedConfig);
    }
}
