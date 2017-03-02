package com.zncm.library.utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

@SuppressLint("DefaultLocale")
public class FileMiniUtil {

    public static String[] getFileNameInfo(String name) {
        String start = name;
        String end = "";
        if (name.contains(".")) {
            start = name.substring(0, name.lastIndexOf("."));
            end = name.substring(name.lastIndexOf(".") + 1, name.length())
                    .toLowerCase();
            if (XUtil.isEmptyOrNull(start)) {
                return new String[]{name, null};
            } else if (XUtil.isEmptyOrNull(end)) {
                return new String[]{name, null};
            } else {
                return new String[]{start, end};
            }
        } else {
            return new String[]{name, null};
        }

    }

    // 根据文件后缀选取合适的文件打开方式
    public static Intent openFile(String filePath) {

        File file = new File(filePath);
        if (!file.exists() && !filePath.contains(".")) {
            return null;
        }
        String end = file
                .getName()
                .substring(file.getName().lastIndexOf(".") + 1,
                        file.getName().length()).toLowerCase();
        /* 依扩展名的类型决定endType */
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            return getAudioFileIntent(filePath);
        } else if (end.equals("3gp") || end.equals("mp4")) {
            return getVideoFileIntent(filePath);
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            return getImageFileIntent(filePath);
        } else if (end.equals("apk")) {
            return getApkFileIntent(filePath);
        } else if (end.startsWith("ppt")) {
            return getPptFileIntent(filePath);
        } else if (end.startsWith("xls")) {
            return getExcelFileIntent(filePath);
        } else if (end.startsWith("doc")) {
            return getWordFileIntent(filePath);
        } else if (end.equals("pdf")) {
            return getPdfFileIntent(filePath);
        } else if (end.equals("chm")) {
            return getChmFileIntent(filePath);
        } else if (end.equals("txt")) {
            return getTextFileIntent(filePath, false);
        } else {
            return getAllIntent(filePath);
        }
    }

    public static String getFileName(String filePath) {
        File file = new File(filePath);
        if (!file.exists())
            return null;
        return file.getName();
    }


    // Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent(String param) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent(String param) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

    // Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    // Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    // Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent(String param) {

        Uri uri = Uri.parse(param).buildUpon()
                .encodedAuthority("com.android.htmlfileprovider")
                .scheme("content").encodedPath(param).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    // Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    // Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    // Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    // Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    public static String formatFileSize(String kbStr) {
        if (XUtil.notEmptyOrNull(kbStr)) {
            return "0KB";
        } else {
            long SIZE_KB = 1024L;
            long SIZE_MB = SIZE_KB * 1024L;
            long SIZE_GB = SIZE_MB * 1024L;
            float size = 0.0F;

            try {
                size = Float.parseFloat(kbStr);
            } catch (NumberFormatException var9) {
                var9.printStackTrace();
                return "";
            }

            size *= (float) SIZE_KB;
            return size < (float) SIZE_KB ? String.format("%d B", new Object[]{Integer.valueOf((int) size)}) : (size < (float) SIZE_MB ? String.format("%.2f KB", new Object[]{Float.valueOf(size / (float) SIZE_KB)}) : (size < (float) SIZE_GB ? String.format("%.2f MB", new Object[]{Float.valueOf(size / (float) SIZE_MB)}) : String.format("%.2f GB", new Object[]{Float.valueOf(size / (float) SIZE_GB)})));
        }
    }

    // Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    // Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(String param, boolean paramBoolean) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean) {
            Uri uri1 = Uri.parse(param);
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri2 = Uri.fromFile(new File(param));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }

    // Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    public static Intent getUnKnowIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/*");
        return intent;
    }


    /**
     * 判断是否在磁盘中
     *
     * @param path
     * @return
     */
    public static boolean isPathInDisk(String path) {
        if (XUtil.isEmptyOrNull(path)) {
            return false;
        }

        if (path.startsWith("/")) {
            return true;
        }

        if (path.contains("storage")) {
            return true;
        }

        if (path.contains("emulated")) {
            return true;
        }

        if (path.contains("sdcard")) {
            return true;
        }

        if (path.contains("/mnt")) {
            return true;
        }

        if (path.startsWith("content:")) {
            return true;
        }

        return false;
    }
}
