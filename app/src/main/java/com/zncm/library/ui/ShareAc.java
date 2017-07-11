package com.zncm.library.ui;

import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zncm.library.R;
import com.zncm.library.data.BaseData;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Fields;
import com.zncm.library.data.Items;
import com.zncm.library.data.Lib;
import com.zncm.library.data.LocLib;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.ft.ItemsFt;
import com.zncm.library.ft.LocLibFt;
import com.zncm.library.utils.Dbutils;
import com.zncm.library.utils.XUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;


public class ShareAc extends BaseAc {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPreData();
    }

    @Override
    protected int setCV() {
        return R.layout.activity_search;
    }

    private void initPreData() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEND)) {
                Bundle extras = intent.getExtras();
                String content = String.valueOf(extras.getString(Intent.EXTRA_TEXT));
                if (!XUtil.notEmptyOrNull(content)) {
                    return;
                }
                XUtil.tShort("已保存~ " + content);
                initSave(Constant.SYS_SHARE, content, "");
                finish();

            }
        }
    }

    public static boolean initSave(String libName, String content, String title) {
        if (Dbutils.findSysLib(libName, Lib.libType.sys.value()) != null) {
            saveData(libName, content, title);
        } else {
            LocLibFt.mkLib(libName, Constant.SYS_SYS_MK, Lib.libType.sys.value());
            saveData(libName, content, title);
        }
        return false;
    }

    public static void initLibRss(String libName, String libDesc) {
        if (Dbutils.findSysLib(libName, Lib.libType.rss.value()) == null) {
            LocLibFt.mkLib(libName, libDesc, Constant.SYS_RSS_MK, Lib.libType.rss.value());
        }
    }

    public static void initLibNet(String libName, String libDesc) {
        LocLibFt.mkLib(libName, libDesc, libName + ":文本||" + libDesc + ":文本", Lib.libType.net.value());
    }

    public static void initLibApi(String libName, String libDesc) {
        LocLibFt.mkLib(libName, libDesc, libName + ":文本||" + libDesc + ":文本", Lib.libType.api.value());
    }

    public static boolean initSaveList(String libName, String mk, ArrayList<String> list) {
        return initSaveList(libName, mk, list, Lib.libType.net.value());
    }

    public static boolean initSaveList(String libName, String mk, ArrayList<String> list, int libType) {
        if (Dbutils.findSysLib(libName, libType) != null) {
            saveDataList(libName, list);
        } else {
            if (XUtil.isEmptyOrNull(mk)) {
                return false;
            }
            LocLibFt.mkLib(libName, mk, libType);
            saveDataList(libName, list);
        }
        return false;
    }


    public static void saveDataList(String libName, ArrayList<String> list) {
        if (!XUtil.listNotNull(list)) {
            return;
        }
        Lib lib = Dbutils.getLibSys(libName);
        saveInfo(list, lib);
    }

    public static void saveDataList(int libId, ArrayList<String> list) {
        saveDataList(libId, list, null);
    }

    public static void saveDataList(int libId, ArrayList<String> list, Items items) {
        if (!XUtil.listNotNull(list)) {
            return;
        }
        Lib lib = Dbutils.getLibById(libId);
        saveInfo(list, lib, items);
    }

    private static void saveInfo(ArrayList<String> list, Lib lib) {
        saveInfo(list, lib, null);
    }

    private static void saveInfo(ArrayList<String> list, Lib lib, Items items) {
        try {
            if (lib != null) {
                List<Fields> datas = Dbutils.getFields(lib.getLib_id());
                Map<String, Object> map = new HashMap<>();
                if (XUtil.listNotNull(datas)) {

                    Items tmp = new Items();
                    if (items != null) {
                        tmp = items;
                    }

                    int max = Math.min(datas.size(), list.size());
                    for (int i = 0; i < max; i++) {
                        map.put(datas.get(i).getFields_id() + "", list.get(i));
                    }
                    JSON json = new JSONObject(map);
                    tmp.setLib_id(lib.getLib_id());
                    tmp.setItem_json(json.toJSONString());
                    tmp.setItem_time(System.currentTimeMillis());
                    tmp.setItem_modify_time(System.currentTimeMillis());
                    Dbutils.addItems(tmp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void saveData(String libName, String content, String title) {
        try {
            Lib lib = Dbutils.getLibSys(libName);
            if (lib != null) {
                List<Fields> datas = Dbutils.getFields(lib.getLib_id());
                Map<String, Object> map = new HashMap<>();
                if (XUtil.listNotNull(datas)) {
                    for (Fields tmp : datas) {
                        if (tmp.getFields_name().equals("标题")) {
                            map.put(tmp.getFields_id() + "", title);
                        }
                        if (tmp.getFields_name().equals("内容")) {
                            map.put(tmp.getFields_id() + "", content);
                        }
                        if (tmp.getFields_name().equals("时间")) {
                            map.put(tmp.getFields_id() + "", XUtil.getDateFull());
                        }
                    }
                    JSON json = new JSONObject(map);
                    Dbutils.addItems(new Items(lib.getLib_id(), json.toJSONString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
