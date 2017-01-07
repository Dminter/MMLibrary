package com.zncm.library.utils;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.zncm.library.data.Constant;
import com.zncm.library.data.SpConstant;
import com.zncm.library.ui.ShareAc;

import java.util.List;

/**
 * Created by jmx on 12/24 0024.
 */
public class ActionService extends AccessibilityService {

    StringBuilder hbInfo = null;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (MySp.get(SpConstant.isHongbaoN, Boolean.class, true)) {
            openHongBao(event);
        }
        AccessibilityNodeInfo nodeInfo = event.getSource();
        if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(event.getClassName())) {
            hbInfo = new StringBuilder();
            traverseNode(nodeInfo);
            if (XUtil.notEmptyOrNull(hbInfo.toString())) {
                ShareAc.initSave(Constant.SYS_HONGBAO, hbInfo.toString(), "");
            }
        }
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            recordInfo(nodeInfo, Constant.SYS_INPUTTEXT);
        }
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            recordInfo(nodeInfo, Constant.SYS_CLICKED);
        }
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            try {
                StringBuffer stringBufferInfo = new StringBuffer();
                stringBufferInfo.append(event.getPackageName()).append(" ").append(event.getClassName());
                if (XUtil.notEmptyOrNull(stringBufferInfo.toString())) {
                    ShareAc.initSave(Constant.SYS_WINDOW, stringBufferInfo.toString(), "");
                }
            } catch (Exception e) {
            }
        }
    }

    private void recordInfo(AccessibilityNodeInfo nodeInfo, String sysClicked) {
        try {
            CharSequence text = nodeInfo.getText();
            if (null != text && text.length() > 0) {
                String str = text.toString();
                ShareAc.initSave(sysClicked, str, "");
            }
        } catch (Exception e) {

        }
    }

    private void openHongBao(AccessibilityEvent event) {
        if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(event.getClassName())) {
            checkKey1();
            MySp.put(SpConstant.isHongbaoN, false);
        } else if ("com.tencent.mm.ui.LauncherUI".equals(event.getClassName())) {
            checkKey2();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void checkKey1() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("拆红包");
        for (AccessibilityNodeInfo n : list) {
            n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void checkKey2() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");
        if (list.isEmpty()) {
            list = nodeInfo.findAccessibilityNodeInfosByText("[微信红包]");
            for (AccessibilityNodeInfo n : list) {
                n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
        } else {
            //最新的红包领起
            for (int i = list.size() - 1; i >= 0; i--) {
                AccessibilityNodeInfo parent = list.get(i).getParent();
                if (parent != null) {
                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
            }
        }
    }

    private void traverseNode(AccessibilityNodeInfo node) {
        if (null == node) return;
        final int count = node.getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                AccessibilityNodeInfo childNode = node.getChild(i);
                traverseNode(childNode);
            }
        } else {
            CharSequence text = node.getText();
            if (null != text && text.length() > 0) {
                String url = text.toString();
                hbInfo.append(url).append("\n");
            }
        }
    }

    @Override
    public void onInterrupt() {

    }
}
