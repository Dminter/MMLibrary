package com.zncm.library.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.zncm.library.data.Constant;
import com.zncm.library.data.MyApplication;
import com.zncm.library.ui.ShareAc;

import java.util.ArrayList;

/**
 * Created by jmx on 1/19 0019.
 */
public class PhoneUtils {


    public static void getSMS() {
//        String where = "type='1'";
        try {
            ContentResolver cr = MyApplication.getInstance().ctx.getContentResolver();
            Cursor cursor = cr.query(Uri.parse("content://sms/inbox"), null, null, null, null);
            while (cursor.moveToNext()) {
                int phoneColumn = cursor.getColumnIndex("address");
                int smsColumn = cursor.getColumnIndex("body");
                String number = cursor.getString(phoneColumn);
                String content = cursor.getString(smsColumn);
                if (XUtil.notEmptyOrNull(content)) {
                    ShareAc.initSave(Constant.SYS_SMS, content, number);
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void readContect() {
        Cursor cursor = null;
        try {
            Context ctx = MyApplication.getInstance().ctx;
            cursor = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (XUtil.notEmptyOrNull(name)) {
                    ShareAc.initSave(Constant.SYS_CONTECT, number, name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }


    public static void loadStrequent() {
        String[] projection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.LOOKUP_KEY, ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                ContactsContract.Contacts.TIMES_CONTACTED, ContactsContract.Contacts.LAST_TIME_CONTACTED,
                ContactsContract.Contacts.STARRED, ContactsContract.Contacts.PHOTO_ID};
        ContentResolver resolver = MyApplication.getInstance().ctx
                .getContentResolver();
        // 显示最近联系人和收藏的联系人
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_STREQUENT_URI,
                projection, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(1);
            int times = cursor.getInt(4);
            long time = cursor.getLong(5);
            if (XUtil.notEmptyOrNull(name)) {
                ShareAc.initSave(Constant.SYS_CONTECT_NEAR, XUtil.getDateFull(time) + " (" + times + ")", name);
            }
        }
        cursor.close();
    }


    public static String getWifiInfo() {
        WifiManager wifi = (WifiManager) MyApplication.getInstance().ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String maxText = info.getMacAddress();
        String ipText = intToIp(info.getIpAddress());
        String status = "";

        String ssid = info.getSSID();
        int networkID = info.getNetworkId();
        int speed = info.getLinkSpeed();
        String infoStr = "mac：" + maxText + "  "
                + "ip：" + ipText + "  "
                + "ssid :" + ssid;

        ArrayList<ScanResult> list;                   //存放周围wifi热点对象的列表
        list = (ArrayList<ScanResult>) wifi.getScanResults();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).SSID).append(" ");
        }
        if (XUtil.notEmptyOrNull(infoStr)) {
            ShareAc.initSave(Constant.SYS_WIFI, infoStr + " \n其他wifi:" + sb.toString(), ssid);
        }
        return infoStr;
    }



    private static String intToIp(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 24) & 0xFF);
    }
}
