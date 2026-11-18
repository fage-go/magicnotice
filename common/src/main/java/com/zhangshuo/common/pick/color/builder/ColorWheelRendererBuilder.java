package com.zhangshuo.common.pick.color.builder;


import com.zhangshuo.common.pick.color.ColorPickerView;
import com.zhangshuo.common.pick.color.renderer.ColorWheelRenderer;
import com.zhangshuo.common.pick.color.renderer.FlowerColorWheelRenderer;
import com.zhangshuo.common.pick.color.renderer.SimpleColorWheelRenderer;

public class ColorWheelRendererBuilder {
	public static ColorWheelRenderer getRenderer(ColorPickerView.WHEEL_TYPE wheelType) {
		switch (wheelType) {
			case CIRCLE:
				return new SimpleColorWheelRenderer();
			case FLOWER:
				return new FlowerColorWheelRenderer();
		}
		throw new IllegalArgumentException("wrong WHEEL_TYPE");
	}
}