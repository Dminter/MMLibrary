package com.zncm.library.data;

import com.zncm.library.utils.db.DatabaseHelper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by MX on 3/24 0024.
 */
public class Constant {
    public static int MAX_DB_QUERY = 100;
    public static int N_ID_XZ = 10000;
    public static String DB_PATH = "data/data/" + MyApplication.getInstance().ctx.getPackageName() + "/databases/" + DatabaseHelper.DATABASE_NAME;
    public static String KEY_PARAM_DATA = "KEY_PARAM_DATA";
    public static String KEY_PARAM_DATA2 = "KEY_PARAM_DATA2";
    public static String KEY_PARAM_LIST = "KEY_PARAM_LIST";
    public static String KEY_PARAM_BOOLEAN = "KEY_PARAM_BOOLEAN";
    public static String KEY_CURRENT_POSITION = "KEY_CURRENT_POSITION";
    public static String KEY_TITLE = "KEY_TITLE";
    public static String PATH_ROOT = "LIB/" + MyApplication.getInstance().ctx.getPackageName();
    //    public static String LIB_SHARE = "http://share.weiyun.com/fbd1f07b97d9890a8923032988c8905b";
    public static String LIB_SHARE = "http://pan.baidu.com/s/1mg1r620";
    public static String LOCLIB_NET = "http://pan.plyz.net/d.asp?u=456079979&p=loclib_net.csv";
    public static String LIB_SHARE_TMPLIB = "http://pan.plyz.net/d.asp?u=3356364438";
    public static String QQ_GROUP = "153039365";
    public static String QQ_GROUP_URL = "http://jq.qq.com/?_wv=1027&k=dV1lAA";


    public static String SHARE_URL = "http://fir.im/lib/";
    public static String QQ_LIB_SHARE = "1603638739";

    public static String SYS_SHARE = "分享";
    public static String SYS_CLIPBOARD = "剪贴板";
    public static String SYS_NOTIFY = "通知栏";
    public static String SYS_INPUTTEXT = "键盘输入";
    public static String SYS_CLICKED = "点击内容";
    public static String SYS_WINDOW = "窗体信息";
    public static String SYS_CONTECT = "手机联系人";
    public static String SYS_CONTECT_NEAR = "最近联系人";
    public static String SYS_WIFI = "WiFi信息";
    public static String SYS_SMS = "手机短信";
    public static String SYS_HONGBAO = "红包";
    public static String SYS_FILE_DOWNLOAD = "文件下载";
    public static String SYS_COLLECT = "网页收藏";
    public static String SYS_NET_HISTORY = "网页历史";
    public static String SYS_PICS = "网页图片";
    public static String SYS_SYS_MK = "标题:文本||内容:文本||时间:文本";


    public static String NET_BAIDU = "https://www.baidu.com/s?wd=";


    public static String SYS_WXHOT = "微信热门精选";
    public static String SYS_WXHOT_MK = "标题:文本||描述:文本||图片:文本||链接:文本||时间:文本";

    public static String SYS_NEWS = "新闻聚合";
    public static String SYS_NEWS_MK = "标题:文本||描述:文本||来源:文本||图片:文本||链接:文本||时间:文本";


    public static String SYS_MEIVI = "美女图片";
    public static String SYS_MEIVI_MK = "标题:文本||描述:文本||图片:文本||链接:文本||时间:文本";


    public static String SYS_XH = "笑话-文本";
    public static String SYS_XH_MK = "标题:文本||内容:文本";

    public static String SYS_XH_PIC = "笑话-图片";
    public static String SYS_XH_PIC_MK = "标题:文本||图片:网络图片";
    public static String SYS_BAIDU_BK = "百度百科";
    public static String SYS_BAIDU_BK_MK = "词条:文本||内容:文本||来源:文本";

    public static String SYS_SC = "收藏";
    public static String SYS_SC_MK = "1:文本||2:文本||3:文本||4:文本||5:文本||6:文本||7:文本||8:文本||9:文本||10:文本||11:文本||12:文本||13:文本||14:文本||15:文本||16:文本||17:文本||18:文本||19:文本||20:文本";


    public static String SYS_PRE_LIB = "SYS_PRE_LIB";
    public static String SYS_PRE_ITEM = "SYS_PRE_ITEM";

    public static String SYS_RSS_MK = "标题:文本||时间:文本||来源:文本";


}
