package com.android.cgcxy.autorobpacket;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class MyService extends AccessibilityService {

    private boolean isAuto = false;
    private int maxPacket = 0;
    private boolean listSize=false;

    public MyService() {
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        int evenTyp = accessibilityEvent.getEventType();

        if (evenTyp == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {

            List<CharSequence> text = accessibilityEvent.getText();
            if (text != null) {

                for (CharSequence titleText : text) {
                    if (titleText.length() > 31) {

                        return;
                    }
                    if (titleText.toString().contains("[微信红包]")) {
                        if (accessibilityEvent.getParcelableData() == null || !(accessibilityEvent.getParcelableData() instanceof Notification)) {
                            return;
                        }
                        Notification notification = (Notification) accessibilityEvent.getParcelableData();
                        PendingIntent pendingIntent = notification.contentIntent;
                        isAuto = true;
                        try {
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        } else if (evenTyp == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            CharSequence className = accessibilityEvent.getClassName();
            if ("com.tencent.mm.ui.LauncherUI".equals(className)) {
                if (isAuto) {
                    if (getRootInActiveWindow() == null) {
                        return;
                    }
                    List<AccessibilityNodeInfo> list = getRootInActiveWindow().findAccessibilityNodeInfosByText(getString(R.string.get_rad_packet));
                    maxPacket = list.size();
                    if (list.size() > 0) {
                        openRedPacked(list.get(list.size() - 1));
                    }
                }

                isAuto = false;
            } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(className)) {
                excreteRedPacked();
            } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(className)) {

            }
        } else if (evenTyp == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            CharSequence className = accessibilityEvent.getClassName();

            if ("android.widget.TextView".equals(className.toString()) && !isAuto) {

                if (getRootInActiveWindow() == null) {
                    return;
                }
                List<AccessibilityNodeInfo> list = getRootInActiveWindow().findAccessibilityNodeInfosByText(getString(R.string.get_rad_packet));
                System.out.println("-----maxPacket-----" + maxPacket + "----list-----" + list.size());
                if (list.size() > 0 && maxPacket < list.size()) {
                    maxPacket = list.size();
                    openRedPacked(list.get(list.size() - 1));
                }
                if (listSize){
                    maxPacket=0;
                }
                if (list.size()==1){
                    maxPacket=0;
                    listSize=true;
                }else if (list.size()!=0){
                    listSize=false;
                }

            }

        }

    }


    public void openRedPacked(AccessibilityNodeInfo info) {

        if (info.getChildCount() == 0) {
            openRedPacked(info.getParent());
        } else {

            if (info.isClickable()) {
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            } else {
                openRedPacked(info.getParent());
            }
        }

    }

    private void excreteRedPacked() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo info = nodeInfo.getChild(i);
            if ("android.widget.Button".equals(info.getClassName())) {
                nodeInfo.getChild(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }


    @Override
    public void onInterrupt() {

    }

}
