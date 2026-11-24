package com.zhangshuo.magicnotice.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

/** Experimental system binding only, with no events or interaction configured. */
public final class ExperimentalAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // No events are requested or processed.
    }

    @Override
    public void onInterrupt() {
        // There is no feedback to stop.
    }
}
