package com.zhangshuo.magicnotice.led.factory;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.zhangshuo.magicnotice.led.LightView;
import com.zhangshuo.magicnotice.ui.model.behavior.LightModel;

public class SlideLight implements LightFactory {
    private LightView lightView;
    private Path drawPathLeft;
    private Path drawPathRight;
    private float xLeft;
    private float xRight;
    private float startY;
    private float endY;


    @Override
    public void onInit(LightView lightView) {
        this.lightView = lightView;
        initDrawPath();
        lightView.setShadow(lightView.getPaint(), lightView.getData().getStrokeWidth());
        lightView.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        lightView.getPaint().setAlpha(255);
    }

    @Override
    public void drawLight(Canvas canvas, float progress) {
        if (lightView == null) return;
        progress = Math.max(0, progress);
        progress = Math.min(progress, 1);
        int animType = lightView.getData().getAnimType();
        if (animType == LightModel.ANIM_FILL) {
            calculateDrawPath(progress);
            canvas.drawPath(drawPathLeft, lightView.getPaint());
            canvas.drawPath(drawPathRight, lightView.getPaint());
        } else if (animType == LightModel.ANIM_FADE) {
            int alpha = (int) (255 * progress);
            lightView.getPaint().setAlpha(alpha);
            calculateDrawPath(1);
            canvas.drawPath(drawPathLeft, lightView.getPaint());
            canvas.drawPath(drawPathRight, lightView.getPaint());
        } else if (animType == LightModel.ANIM_MOVE) {
            calculateMoveDrawPath(progress);
            canvas.drawPath(drawPathLeft, lightView.getPaint());
            canvas.drawPath(drawPathRight, lightView.getPaint());

        } else if (animType == LightModel.ANIM_BREATH) {
            int radius = (int) (lightView.getData().getStrokeWidth() * 4 * progress);
            lightView.setShadow(lightView.getPaint(), radius);
            calculateBreathDrawPath(progress);
            canvas.drawPath(drawPathLeft, lightView.getPaint());
            canvas.drawPath(drawPathRight, lightView.getPaint());
        }
    }

    @Override
    public long getAnimDuration(int speed) {
        if (speed == 0) {
            return 3000;
        } else if (speed == 1) {
            return 2000;
        } else if (speed == 2) {
            return 1000;
        }
        return 2000;
    }

    @Override
    public void onDestory() {
        lightView = null;
        drawPathLeft = null;
        drawPathRight = null;
    }

    private void initDrawPath() {
        if (lightView == null) return;
        drawPathLeft = new Path();
        drawPathRight = new Path();
        xLeft = lightView.getData().getStrokeWidth() / 2f;
        xRight = lightView.getWidth() - lightView.getData().getStrokeWidth() / 2f;
        startY = lightView.getHeight() / 4f;
        endY = lightView.getHeight() / 4f * 3;

    }

    private void calculateDrawPath(float progress) {
        drawPathLeft.reset();
        drawPathRight.reset();

        float diffY = (float) ((endY - startY) * (0.5 - progress * 0.5));

        drawPathLeft.moveTo(xLeft, startY + diffY);
        drawPathLeft.lineTo(xLeft, endY - diffY);

        drawPathRight.moveTo(xRight, startY + diffY);
        drawPathRight.lineTo(xRight, endY - diffY);
    }

    private void calculateMoveDrawPath(float progress) {
        drawPathLeft.reset();
        drawPathRight.reset();

        float midHeight = (endY + startY) / 2f;
        float newProgress = progress * 0.5f;
        float startDiffY = (float) ((endY - startY) * newProgress);
        float endDiffY = (float) ((endY - startY) * (newProgress + 0.1));
        endDiffY = Math.min(endDiffY, (endY - startY));

        drawPathLeft.moveTo(xLeft, midHeight - startDiffY);
        drawPathLeft.lineTo(xLeft, midHeight - endDiffY);

        drawPathLeft.moveTo(xLeft, midHeight + startDiffY);
        drawPathLeft.lineTo(xLeft, midHeight + endDiffY);

        drawPathRight.moveTo(xRight, midHeight - startDiffY);
        drawPathRight.lineTo(xRight, midHeight - endDiffY);

        drawPathRight.moveTo(xRight, midHeight + startDiffY);
        drawPathRight.lineTo(xRight, midHeight + endDiffY);
    }

    private void calculateBreathDrawPath(float progress) {
        drawPathLeft.reset();
        drawPathRight.reset();

        LightModel data = lightView.getData();
        int width = lightView.getWidth();
        int height = lightView.getHeight();

        float midHeight = height * 0.5f;
        float startHeight = 0;
        float endHeight = height;

        drawPathLeft.moveTo(0, startHeight);
        drawPathLeft.quadTo(data.getStrokeWidth() * 2 * progress, midHeight, 0, endHeight);
        drawPathLeft.lineTo(0, endHeight);
        drawPathLeft.close();

        drawPathRight.moveTo(width, startHeight);
        drawPathRight.quadTo(width - data.getStrokeWidth() * 2 * progress, midHeight, width, endHeight);
        drawPathRight.lineTo(width, endHeight);
        drawPathRight.close();

    }


}
