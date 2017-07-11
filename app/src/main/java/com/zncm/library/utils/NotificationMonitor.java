package com.zncm.library.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;


import com.zncm.library.data.Constant;
import com.zncm.library.data.Lib;
import com.zncm.library.data.MyApplication;
import com.zncm.library.data.SpConstant;
import com.zncm.library.ui.ShareAc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationMonitor extends NotificationListenerService {
    private static final String TAG = "SevenNLS";
    private static final String TAG_PRE = "[" + NotificationMonitor.class.getSimpleName() + "] ";
    private static final int EVENT_UPDATE_CURRENT_NOS = 0;
    public static final String ACTION_NLS_CONTROL = "com.seven.notificationlistenerdemo.NLSCONTROL";
    public static List<StatusBarNotification[]> mCurrentNotifications = new ArrayList<StatusBarNotification[]>();
    public static int mCurrentNotificationsCounts = 0;
    public static StatusBarNotification mPostedNotification;
    public static StatusBarNotification mRemovedNotification;
    private CancelNotificationReceiver mReceiver = new CancelNotificationReceiver();
    // String a;

    private Handler mMonitorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_UPDATE_CURRENT_NOS:
                    updateCurrentNotifications();
                    break;
                default:
                    break;
            }
        }
    };

    class CancelNotificationReceiver extends BroadcastReceiver {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action;
            if (intent != null && intent.getAction() != null) {
                action = intent.getAction();
                if (action.equals(ACTION_NLS_CONTROL)) {
                    String command = intent.getStringExtra("command");
                    if (TextUtils.equals(command, "cancel_last")) {
                        if (mCurrentNotifications != null && mCurrentNotificationsCounts >= 1) {
                            StatusBarNotification sbnn = getCurrentNotifications()[mCurrentNotificationsCounts - 1];
                            cancelNotification(sbnn.getPackageName(), sbnn.getTag(), sbnn.getId());
                        }
                    } else if (TextUtils.equals(command, "cancel_all")) {
                        cancelAllNotifications();
                    }
                }
            }
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NLS_CONTROL);
        registerReceiver(mReceiver, filter);
        mMonitorHandler.sendMessage(mMonitorHandler.obtainMessage(EVENT_UPDATE_CURRENT_NOS));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        updateCurrentNotifications();
        mPostedNotification = sbn;
        Bundle extras = sbn.getNotification().extras;
        String
                notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        CharSequence
                notificationText = extras.getCharSequence(Notification.EXTRA_TEXT);
//        CharSequence notificationSubText =
//                extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
//        String content = notificationTitle + " " + notificationText;
        try {

            boolean isListenNotify = MySp.get(SpConstant.isListenNotify, Boolean.class, true);
            String title = "";
            String content = "";
            if (notificationTitle != null) {
                title = notificationTitle.toString().trim();
            }
            if (notificationText != null) {
                content = notificationText.toString().trim();
            }
            if (isListenNotify && XUtil.notEmptyOrNull(content)) {
                boolean flag = false;
                String nKey = MySp.get(SpConstant.notifyKey, String.class, "");
                nKey = nKey.replaceAll("\\｜", "\\|");
                if (XUtil.notEmptyOrNull(nKey)) {
                    String arr[] = nKey.split("\\|");
                    if (arr.length > 0) {
                        for (int i = 0; i < arr.length; i++) {
                            String value = arr[i];
                            if (XUtil.notEmptyOrNull(value)) {
                                if (title.contains(value)) {
                                    flag = true;
                                    break;
                                }
                                if (content.contains(value)) {
                                    flag = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (flag) {
                    SoundPoolUtil.playSound(MyApplication.getInstance().ctx);
                    Notification notification = sbn.getNotification();
                    final PendingIntent pendingIntent = notification.contentIntent;
                    try {
                        if (MySp.get(SpConstant.isListenNotifyInto, Boolean.class, true)) {
                            MySp.put(SpConstant.isHongbaoN, true);
                            pendingIntent.send();
                        }
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
                ShareAc.initSave(Constant.SYS_NOTIFY, content, title);
            }
        } catch (Exception e) {

        }

//        Notification notification = sbn.getNotification();
//        if (null != notification) {
//            Bundle bundle = notification.extras;
//            if (null != bundle) {
//                List<String> textList = new ArrayList<String>();
//                String title = bundle.getString("android.title");
//                if (!TextUtils.isEmpty(title)) textList.add(title);
//                String detailText = bundle.getString("android.text");
//                if (!TextUtils.isEmpty(detailText)) textList.add(detailText);
//                if (textList.size() > 0) {
//                    for (String text : textList) {
//                        if (!TextUtils.isEmpty(text) && text.contains("红包")) {
//                            final PendingIntent pendingIntent = notification.contentIntent;
//                            try {
//                                pendingIntent.send();
//                            } catch (PendingIntent.CanceledException e) {
//                            }
//                            break;
//                        }
//                    }
//                }
//            }
//        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        updateCurrentNotifications();
        mRemovedNotification = sbn;
    }

    private void updateCurrentNotifications() {
        try {
            StatusBarNotification[] activeNos = getActiveNotifications();
            if (mCurrentNotifications.size() == 0) {
                mCurrentNotifications.add(null);
            }
            mCurrentNotifications.set(0, activeNos);
//            mCurrentNotificationsCounts = activeNos.length;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static StatusBarNotification[] getCurrentNotifications() {
        if (mCurrentNotifications.size() == 0) {
            return null;
        }
        return mCurrentNotifications.get(0);
    }

}