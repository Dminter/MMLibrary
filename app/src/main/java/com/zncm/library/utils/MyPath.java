package com.zncm.library.utils;

import android.os.Environment;


import com.zncm.library.data.Constant;

import java.io.File;

public class MyPath {
    public static final String PATH_IMG = "img";
    public static final String PATH_DATA = "data";
    public static final String PATH_DB = "db";
    public static final String PATH_DOWNLOAD = "download";


    public static String getFolder(String folderName) {
        if (folderName == null) {
            return null;
        }
        File dir = XUtil.createFolder(folderName);
        if (dir != null) {
            return dir.getAbsolutePath();
        } else {
            return null;
        }
    }


    private static String getPathFolder(String path) {
        File rootPath = Environment.getExternalStoragePublicDirectory(Constant.PATH_ROOT);
        return getFolder(rootPath + File.separator
                + path + File.separator);
    }


    public static String getPathImg() {
        return getPathFolder(PATH_IMG);
    }

    public static String getPathData() {
        return getPathFolder(PATH_DATA);
    }

    public static String getPathDb() {
        return getPathFolder(PATH_DB);
    }

    public static String getPathDownload() {
        return getPathFolder(PATH_DOWNLOAD);
    }

}
