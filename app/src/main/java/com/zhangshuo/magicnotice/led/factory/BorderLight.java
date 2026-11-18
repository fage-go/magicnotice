package com.zhangshuo.magicnotice.led.factory;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;

import com.zhangshuo.magicnotice.led.LightView;
import com.zhangshuo.magicnotice.led.model.CutoutInfo;
import com.zhangshuo.magicnotice.ui.model.behavior.LightModel;

public class BorderLight implements LightFactory {
    private LightView lightView;
    private Path drawPath;
    private PathMeasure drawPathMeasure;
    private float drawLength;
    private Path cutoutPath;
    private PathMeasure cutoutPathMeasure;
    private float cutoutLength;
    private float cornerRadius = 3.5f;
    private Path drawProgressPath;
    private Path cutoutProgressPath;


    @Override
    public void onInit(LightView lightView) {
        this.lightView = lightView;
        initDrawPath();
        lightView.setShadow(lightView.getPaint(), lightView.getData().getStrokeWidth());
        lightView.getPaint().setStyle(Paint.Style.STROKE);
        lightView.getPaint().setAlpha(255);
    }

    @Override
    public void drawLight(Canvas canvas, float progress) {
        if (lightView == null || drawPath == null || drawPathMeasure == null || drawProgressPath == null || cutoutProgressPath == null)
            return;
        progress = Math.max(0, progress);
        progress = Math.min(progress, 1);

        int animType = lightView.getData().getAnimType();
        if (animType == LightModel.ANIM_FILL) {
            Path[] paths = calculateFillDrawPath(progress);
            for (Path path : paths) {
                if (path != null) {
                    canvas.drawPath(path, lightView.getPaint());
                }
            }
        } else if (animType == LightModel.ANIM_FADE) {
            int alpha = (int) (255 * progress);
            lightView.getPaint().setAlpha(alpha);
            canvas.drawPath(drawPath, lightView.getPaint());
            canvas.drawPath(cutoutPath, lightView.getPaint());

        } else if (animType == LightModel.ANIM_MOVE) {
            Path[] paths = calculateMoveDrawPath(progress);
            for (Path path : paths) {
                if (path != null) {
                    canvas.drawPath(path, lightView.getPaint());
                }
            }
        } else if (animType == LightModel.ANIM_BREATH) {
            int radius = (int) (lightView.getData().getStrokeWidth() * 4 * progress);
            lightView.setShadow(lightView.getPaint(), radius);
            canvas.drawPath(drawPath, lightView.getPaint());
            canvas.drawPath(cutoutPath, lightView.getPaint());
        }
    }

    @Override
    public long getAnimDuration(int speed) {
        if (speed == 0) {
            //慢
            return 3000;
        } else if (speed == 1) {
            //正常
            return 2000;
        } else if (speed == 2) {
            return 1000;
        }
        return 2000;
    }

    @Override
    public void onDestory() {
        lightView = null;
        drawPath = null;
        drawPathMeasure = null;
        drawProgressPath = null;
        cutoutProgressPath = null;
    }


    public Path[] calculateFillDrawPath(float progress) {
        Path[] paths = new Path[2];
        if (progress < 0.2) {
            cutoutProgressPath.reset();
            float newProgress = progress / 0.2f;
            float startPoint = 0;
            float endPoint = cutoutPathMeasure.getLength() * newProgress;
            cutoutPathMeasure.getSegment(startPoint, endPoint, cutoutProgressPath, true);
            paths[0] = cutoutProgressPath;
            paths[1] = null;
        } else if (progress < 0.9) {
            drawProgressPath.reset();
            float newProgress = (progress - 0.2f) / 0.7f;
            float startPoint = 0;
            float endPoint = drawPathMeasure.getLength() * newProgress;
            drawPathMeasure.getSegment(startPoint, endPoint, drawProgressPath, true);
            paths[0] = cutoutPath;
            paths[1] = drawProgressPath;
        } else {
            paths[0] = cutoutPath;
            paths[1] = drawPath;
        }
        return paths;
    }

    public Path[] calculateMoveDrawPath(float progress) {
        Path[] paths = new Path[2];
        cutoutProgressPath.reset();
        drawProgressPath.reset();
        if (progress < 0.3) {
            float newProgress = progress / 0.2f;
            float startPoint = cutoutLength * (newProgress - 0.4f);
            startPoint = Math.max(0, startPoint);
            float endPoint = cutoutLength * newProgress;
            endPoint = Math.min(cutoutLength, endPoint);
            cutoutPathMeasure.getSegment(startPoint, endPoint, cutoutProgressPath, true);
        }
        if (progress > 0.2) {
            float newProgress = (progress - 0.2f) / 0.6f;
            float startPoint = drawLength * (newProgress - 0.2f);
            startPoint = Math.max(0, startPoint);
            float endPoint = drawLength * newProgress;
            endPoint = Math.min(drawLength, endPoint);
            drawPathMeasure.getSegment(startPoint, endPoint, drawProgressPath, true);
        }
        paths[0] = cutoutProgressPath;
        paths[1] = drawProgressPath;
        return paths;
    }


    private void initDrawPath() {
        int radius = lightView.getData().getStrokeWidth() / 2;
        int left = radius;
        int top = radius;
        int right = lightView.getWidth() - radius;
        int bottom = lightView.getHeight() - radius;
        CutoutInfo cutoutInfo = lightView.getCutoutInfo();
        float diffRadius = radius * cornerRadius;
        if (drawPath == null) {
            drawPath = new Path();
        }
        drawPath.reset();
        //top line
        drawPath.moveTo(cutoutInfo.getLeft() + cutoutInfo.getRadius(), top);
        drawPath.lineTo(right - diffRadius, top);

        //top & right
        drawPath.quadTo(right, top, right, top + diffRadius);

        //right line
        drawPath.lineTo(right, bottom - diffRadius);

        //right & bottom
        drawPath.quadTo(right, bottom, right - diffRadius, bottom);

        //bottom
        drawPath.lineTo(left + diffRadius, bottom);

        //bottom & left
        drawPath.quadTo(left, bottom, left, bottom - diffRadius);

        //left
        drawPath.lineTo(left, top + diffRadius);

        //left & top
        drawPath.quadTo(left, top, left + diffRadius, top);

        //top
        drawPath.lineTo(cutoutInfo.getLeft() + cutoutInfo.getRadius(), top);

        drawPathMeasure = new PathMeasure(drawPath, false);
        drawLength = drawPathMeasure.getLength();

        //添加摄像头位置
        if (cutoutPath == null) {
            cutoutPath = new Path();
        }
        if (cutoutInfo.getWidth() > 0 && cutoutInfo.getHeight() > 0) {
            cutoutPath.reset();
            cutoutPath.addRoundRect(cutoutInfo.getLeft(), cutoutInfo.getTop(), cutoutInfo.getRight(), cutoutInfo.getBottom(), cutoutInfo.getRadius(), cutoutInfo.getRadius(), Path.Direction.CW);
        }
        cutoutPathMeasure = new PathMeasure(cutoutPath, false);
        cutoutLength = cutoutPathMeasure.getLength();
        drawProgressPath = new Path();
        cutoutProgressPath = new Path();
    }

}
