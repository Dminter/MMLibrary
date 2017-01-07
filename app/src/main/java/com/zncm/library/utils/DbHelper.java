package com.zncm.library.utils;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Fields;
import com.zncm.library.data.Items;
import com.zncm.library.data.Lib;
import com.zncm.library.data.LocLib;
import com.zncm.library.data.RefreshEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class DbHelper {


    public static boolean importCsv(File file, String charset) {
        List<String> datas = CSVUtils.importCsv(file, charset);
        if (!XUtil.listNotNull(datas)) {
            return false;
        }
        int lib_id = Dbutils.addLib(new Lib(file.getName().replaceAll(".csv", "")));
        List<Integer> ids = new ArrayList<>();
        String headLine = datas.get(0);
        headLine = headLine.replace("\"", "");
        String[] fieldsInfo = headLine.split(",");
        for (int i = 0; i < fieldsInfo.length; i++) {
            Dbutils.addFields(new Fields(fieldsInfo[i], lib_id));
            int _id = Dbutils.getFieldsSaveId();
            ids.add(_id);
        }
        if (!XUtil.listNotNull(ids)) {
            return false;
        }
        if (datas.size() > 0) {
            for (int j = datas.size() - 1; j > 0; j--) {
                String tmp = datas.get(j);
                tmp = tmp.replace("\"", "");
                String[] tmpInfo = tmp.split(",");
                Map<String, Object> map = new HashMap<>();
                if (ids.size() <= tmpInfo.length) {
                    for (int i = 0; i < ids.size(); i++) {
                        map.put(String.valueOf(ids.get(i)), tmpInfo[i]);
                    }
                } else {
                    for (int i = 0; i < tmpInfo.length; i++) {
                        map.put(String.valueOf(ids.get(i)), tmpInfo[i]);
                    }
                }
                JSON json = new JSONObject(map);
                Dbutils.addItems(new Items(lib_id, json.toJSONString()));
            }
        }
        return false;
    }

    public static void importLocCsv(Context ctx, File file) {
        List<String> datas = CSVUtils.importLocCsv(ctx, file);
        try {
            if (XUtil.listNotNull(datas)) {
                for (int j = 0; j < datas.size(); j++) {
                    String tmp = datas.get(j);
                    tmp = tmp.replace("\"", "");
                    XUtil.debug("tmp:" + tmp);
                    String[] tmpInfo = tmp.split(",");
                    if (tmpInfo.length == 7) {
                        LocLib locLib = new LocLib(tmpInfo[0], tmpInfo[1], tmpInfo[2], tmpInfo[3], tmpInfo[4], tmpInfo[5], tmpInfo[6]);
                        Dbutils.addLocLib(locLib);
                    }
                }
                EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.LOCLIB.getValue()));
            }
        } catch (Exception e) {
        }
    }
}