package com.zncm.library.utils;

import android.content.SharedPreferences;
import android.graphics.Color;

import com.alibaba.fastjson.JSON;
import com.zncm.library.data.BaseData;

public class MySp extends MySharedPreferences {
    private static final String STATE_PREFERENCE = "state_preference";
    private static SharedPreferences sharedPreferences;

    enum Key {
        app_version_code,
        default_tk,
        default_pj,
        clipboard_tk,
        last_tab,
        album_numcolumns,
        simple_model,
        theme,
        pwd,
        show_lunar,
        sys_tip,
        show_finish,
        clipboard_listen,
        outer_open_url,
        today_date,
        list_animation,
        vp_animation,
        font_size,
        template_version,
        api_wx,
        api_xh,
        api_page,
        api_xhpic,
        api_bdbk,
        api_news,
        api_mv,
    }


    public static <T> void put(String key, T value) {
        if (value == null) {
            return;
        }
        String realStr = "";
        if (value instanceof BaseData) {
            realStr = value.toString();
        } else {
            realStr = JSON.toJSONString(value);
        }
        putString(getSharedPreferences(), key, realStr);
    }

    public static <T> T get(String key, Class<T> cls, T defaultValue) {
        String stoStr = getString(getSharedPreferences(), key, "");
        if (!XUtil.notEmptyOrNull(stoStr)) {
            return defaultValue;
        }
        try {
            String realStr = new String(stoStr);
            T data = JSON.parseObject(realStr, cls);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }


    public static SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null)
            sharedPreferences = getPreferences(STATE_PREFERENCE);
        return sharedPreferences;
    }

    public static void setapi_wx(Integer api_wx) {
        putInt(getSharedPreferences(), Key.api_wx.toString(), api_wx);
    }

    public static Integer getapi_wx() {
        return getInt(getSharedPreferences(), Key.api_wx.toString(), 1);
    }

    public static void setapi_xh(Integer api_xh) {
        putInt(getSharedPreferences(), Key.api_xh.toString(), api_xh);
    }

    public static Integer getapi_xh() {
        return getInt(getSharedPreferences(), Key.api_xh.toString(), 1);
    }

    public static void setapi_page(Integer api_page) {
        putInt(getSharedPreferences(), Key.api_page.toString(), api_page);
    }

    public static Integer getapi_page() {
        return getInt(getSharedPreferences(), Key.api_page.toString(), 10);
    }

    public static void setapi_xhpic(Integer api_xhpic) {
        putInt(getSharedPreferences(), Key.api_xhpic.toString(), api_xhpic);
    }

    public static Integer getapi_xhpic() {
        return getInt(getSharedPreferences(), Key.api_xhpic.toString(), 1);
    }

    public static void setapi_bdbk(Integer api_bdbk) {
        putInt(getSharedPreferences(), Key.api_bdbk.toString(), api_bdbk);
    }

    public static Integer getapi_bdbk() {
        return getInt(getSharedPreferences(), Key.api_bdbk.toString(), 1);
    }

    public static void setapi_news(Integer api_news) {
        putInt(getSharedPreferences(), Key.api_news.toString(), api_news);
    }

    public static Integer getapi_news() {
        return getInt(getSharedPreferences(), Key.api_news.toString(), 1);
    }

    public static void setapi_mv(Integer api_mv) {
        putInt(getSharedPreferences(), Key.api_mv.toString(), api_mv);
    }

    public static Integer getapi_mv() {
        return getInt(getSharedPreferences(), Key.api_mv.toString(), 1);
    }

    public static void setLastTab(Integer last_tab) {
        putInt(getSharedPreferences(), Key.last_tab.toString(), last_tab);
    }

    public static Integer getLastTab() {
        return getInt(getSharedPreferences(), Key.last_tab.toString(), 0);
    }

    public static void setAlbumNumColumns(Integer album_numcolumns) {
        putInt(getSharedPreferences(), Key.album_numcolumns.toString(), album_numcolumns);
    }

    public static Integer getAlbumNumColumns() {
        return getInt(getSharedPreferences(), Key.album_numcolumns.toString(), 1);
    }

    public static void setDefaultTk(Integer default_tk) {
        putInt(getSharedPreferences(), Key.default_tk.toString(), default_tk);
    }

    public static Integer getDefaultTk() {
        return getInt(getSharedPreferences(), Key.default_tk.toString(), 0);
    }

    public static void setClipboardTk(Integer clipboard_tk) {
        putInt(getSharedPreferences(), Key.clipboard_tk.toString(), clipboard_tk);
    }

    public static Integer getClipboardTk() {
        return getInt(getSharedPreferences(), Key.clipboard_tk.toString(), 0);
    }

    public static void setDefaultPj(Integer default_pj) {
        putInt(getSharedPreferences(), Key.default_pj.toString(), default_pj);
    }

    public static Integer getDefaultPj() {
        return getInt(getSharedPreferences(), Key.default_pj.toString(), 0);
    }

    public static void setAppVersionCode(Integer app_version_code) {
        putInt(getSharedPreferences(), Key.app_version_code.toString(), app_version_code);
    }

    public static Integer getAppVersionCode() {
        return getInt(getSharedPreferences(), Key.app_version_code.toString(), 0);
    }

    public static void setViewpagerAnimation(Integer viewpager_animation) {
        putInt(getSharedPreferences(), Key.vp_animation.toString(), viewpager_animation);
    }

    public static Integer getViewpagerAnimation() {
        return getInt(getSharedPreferences(), Key.vp_animation.toString(), 0);
    }

    public static void setFontSize(Float font_size) {
        putFloat(getSharedPreferences(), Key.font_size.toString(), font_size);
    }

    public static Float getFontSize() {
        return getFloat(getSharedPreferences(), Key.font_size.toString(), 20f);
    }

    public static void setTheme(Integer theme) {
        putInt(getSharedPreferences(), Key.theme.toString(), theme);
    }

    public static Integer getTheme() {
        return getInt(getSharedPreferences(), Key.theme.toString(), Color.parseColor("#4E342E"));
    }

    public static void setTemplateVersion(Integer template_version) {
        putInt(getSharedPreferences(), Key.template_version.toString(), template_version);
    }

    public static Integer getTemplateVersion() {
        return getInt(getSharedPreferences(), Key.template_version.toString(), 71);
    }

    public static void setPwd(String pwd) {
        putString(getSharedPreferences(), Key.pwd.toString(), pwd);
    }

    public static String getPwd() {
        return getString(getSharedPreferences(), Key.pwd.toString(), "");
    }


    public static void setSimpleModel(Boolean simple_model) {
        putBoolean(getSharedPreferences(), Key.simple_model.toString(), simple_model);
    }

    public static Boolean getSimpleModel() {
        return getBoolean(getSharedPreferences(), Key.simple_model.toString(), false);
    }

    public static void setListAnimation(Boolean list_animation) {
        putBoolean(getSharedPreferences(), Key.list_animation.toString(), list_animation);
    }

    public static Boolean getListAnimation() {
        return getBoolean(getSharedPreferences(), Key.list_animation.toString(), true);
    }


    public static void setShowLunar(Boolean show_lunar) {
        putBoolean(getSharedPreferences(), Key.show_lunar.toString(), show_lunar);
    }

    public static Boolean getShowLunar() {
        return getBoolean(getSharedPreferences(), Key.show_lunar.toString(), true);
    }

    public static void setClipboardListen(Boolean clipboard_listen) {
        putBoolean(getSharedPreferences(), Key.clipboard_listen.toString(), clipboard_listen);
    }

    public static Boolean getClipboardListen() {
        return getBoolean(getSharedPreferences(), Key.clipboard_listen.toString(), true);
    }
    public static void setOuterOpenUrl(Boolean outer_open_url) {
        putBoolean(getSharedPreferences(), Key.outer_open_url.toString(), outer_open_url);
    }

    public static Boolean getOuterOpenUrl() {
        return getBoolean(getSharedPreferences(), Key.outer_open_url.toString(), true);
    }

    public static void setSysTip(Boolean sys_tip) {
        putBoolean(getSharedPreferences(), Key.sys_tip.toString(), sys_tip);
    }

    public static Boolean getSysTip() {
        return getBoolean(getSharedPreferences(), Key.sys_tip.toString(), false);
    }

    public static void setShowFinish(Boolean show_finish) {
        putBoolean(getSharedPreferences(), Key.show_finish.toString(), show_finish);
    }

    public static Boolean getShowFinish() {
        return getBoolean(getSharedPreferences(), Key.show_finish.toString(), true);
    }

    public static void setTodayDate(String today_date) {
        putString(getSharedPreferences(), Key.today_date.toString(), today_date);
    }

    public static String getTodayDate() {
        return getString(getSharedPreferences(), Key.today_date.toString(), "");
    }


}
