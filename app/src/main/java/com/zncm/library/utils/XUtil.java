package com.zncm.library.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.ClipboardManager;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.zncm.library.R;
import com.zncm.library.data.Constant;
import com.zncm.library.data.Lib;
import com.zncm.library.data.MyApplication;
import com.zncm.library.ui.ItemsAc;
import com.zncm.library.ui.ShareAc;
import com.zncm.library.ui.ShowInfoActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XUtil {


    public static void sendToDesktop(Activity activity, Lib lib) {

        Intent intent;
        intent = new Intent();
        Intent shortIntent = new Intent(activity, ItemsAc.class);
        shortIntent.putExtra("lib_id", lib.getLib_id());
//        shortIntent.putExtra(Constant.KEY_PARAM_DATA, lib);
        shortIntent.setAction("android.intent.action.VIEW");
        shortIntent.putExtra("android.intent.extra.UID", 0);
        shortIntent.putExtra("random", new Random().nextLong());
        intent.putExtra("android.intent.extra.shortcut.INTENT", shortIntent);
        intent.putExtra("android.intent.extra.shortcut.NAME", makeShortcutIconTitle(lib.getLib_name()));

        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(activity,
                        R.drawable.ic_lib));
        intent.putExtra("duplicate", false);
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        activity.sendBroadcast(intent);


//        Intent addShortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//
//        // 不允许重复创建
//        addShortcutIntent.putExtra("duplicate", false);// 经测试不是根据快捷方式的名字判断重复的
//        // 应该是根据快链的Intent来判断是否重复的,即Intent.EXTRA_SHORTCUT_INTENT字段的value
//        // 但是名称不同时，虽然有的手机系统会显示Toast提示重复，仍然会建立快链
//        // 屏幕上没有空间时会提示
//        // 注意：重复创建的行为MIUI和三星手机上不太一样，小米上似乎不能重复创建快捷方式
//
//        // 名字
//        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, makeShortcutIconTitle(lib.getLib_name()));
//
//        // 图标
//        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
//                Intent.ShortcutIconResource.fromContext(activity,
//                        R.drawable.ic_lib));
//        // 设置关联程序
//        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
//        launcherIntent.setClass(activity, ItemsAc.class);
//        launcherIntent.putExtra("lib_id", lib.getLib_id());
//        launcherIntent.putExtra("random", new Random().nextLong());
//        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//
//        addShortcutIntent
//                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);
//        // 发送广播
//        activity.sendBroadcast(addShortcutIntent);
        tShort("已创建快捷方式");
    }

    // 创建快捷方式
    public static String makeShortcutIconTitle(String content) {
        final int SHORTCUT_ICON_TITLE_MAX_LEN = 10;
        final String TAG_CHECKED = String.valueOf('\u221A');
        final String TAG_UNCHECKED = String.valueOf('\u25A1');
        content = content.replace(TAG_CHECKED, "");
        content = content.replace(TAG_UNCHECKED, "");
        return content.length() > SHORTCUT_ICON_TITLE_MAX_LEN ? content.substring(0,
                SHORTCUT_ICON_TITLE_MAX_LEN) : content;
    }

    public static Integer getVersionCode(Activity ctx) {

        // 获取packagemanager的实例
        PackageManager packageManager = ctx.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(ctx.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Integer versionCode = packInfo.versionCode;
        return versionCode;
    }

    public static String getVersionName(Context ctx) {

        // 获取packagemanager的实例
        PackageManager packageManager = ctx.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(ctx.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
    }

    public static String replaceImageURLs(String content) {
        String URL_SPACE = "%20";
        Pattern IMG_PATTERN = Pattern.compile("<img\\s+[^>]*src=\\s*['\"]([^'\"]+)['\"][^>]*>", Pattern.CASE_INSENSITIVE);
        ArrayList<String> imgList = new ArrayList<>();
        if (!TextUtils.isEmpty(content)) {
            Matcher matcher = IMG_PATTERN.matcher(content);
            while (matcher.find()) {
                String imgPath = matcher.group(1).replace(" ", URL_SPACE);
                if (notEmptyOrNull(imgPath)) {
                    content = content.replace(matcher.group(), "");
                }
            }
        }
        return content;
    }

    public static void initRss() {
        Map<String, String> mRssMap = new LinkedHashMap<String, String>();
//        mRssMap.put("cnBeta", "http://rss.cnbeta.com/rss");
//        mRssMap.put("知乎日报", "http://www.zhihu.com/rss");
//        mRssMap.put("IT之家", "http://www.ithome.com/rss/");
        mRssMap.put("小众软件", "http://www.appinn.com/feed/");
//        mRssMap.put("凤凰网", "http://news.ifeng.com/rss/index.xml ");
//        mRssMap.put("人民网-国内新闻", "http://www.people.com.cn/rss/politics.xml");
//        mRssMap.put("人民网-国际新闻", "http://www.people.com.cn/rss/world.xml");
        List<Lib> netLib = Dbutils.getLibPage(null, 0, Lib.libType.rss.value());
        if (!XUtil.listNotNull(netLib)) {
            for (String key : mRssMap.keySet()) {
                System.out.println("key= " + key + " and value= " + mRssMap.get(key));
                if (notEmptyOrNull(key)) {
                    ShareAc.initLibRss(key, mRssMap.get(key));
                }
            }
        }
    }


    public static void initNetLib() {
        Map<String, String> mRssMap = new LinkedHashMap<String, String>();
//        mRssMap.put(Constant.SYS_WXHOT, Constant.SYS_WXHOT_MK);
//        mRssMap.put(Constant.SYS_NEWS, Constant.SYS_NEWS_MK);
//        mRssMap.put(Constant.SYS_MEIVI, Constant.SYS_MEIVI_MK);
//        mRssMap.put(Constant.SYS_XH, Constant.SYS_XH_MK);
//        mRssMap.put(Constant.SYS_XH_PIC, Constant.SYS_XH_PIC_MK);
        mRssMap.put(Constant.SYS_BAIDU_BK, Constant.SYS_BAIDU_BK_MK);
        List<Lib> netLib = Dbutils.getLibPage(null, 0, Lib.libType.net.value());
        if (!XUtil.listNotNull(netLib)) {
            for (String key : mRssMap.keySet()) {
                if (notEmptyOrNull(key)) {
                    ShareAc.initSaveList(key, mRssMap.get(key), null);
                }
            }
        }
    }


    // 从resources中的raw 文件夹中获取文件并读取数据
    public static String getFromRaw(Context ctx, int id) {
        String result = "";
        try {
            InputStream in = ctx.getResources().openRawResource(id);
            // 获取文件的字节数
            int lenght = in.available();
            // 创建byte数组
            byte[] buffer = new byte[lenght];
            // 将文件中的数据读到byte数组中
            in.read(buffer);
            result = new String(buffer, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static final WindowManager wm = (WindowManager) MyApplication.getInstance().ctx.getSystemService(Context.WINDOW_SERVICE);

    public static DisplayMetrics getDeviceMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static int getDeviceHeight() {
        return getDeviceMetrics().heightPixels;
    }

    // 刷新 图库，（及时显示图片）
    public static void refreshGallery(Context ctx, String filePath) {
        MediaScannerConnection.scanFile(ctx, new String[]{filePath}, null, null);
    }


    public static void shareImg(Context ctx, String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        File f = new File(imgPath);
        if (f != null && f.exists() && f.isFile()) {
            intent.setType("image/jpg");
            Uri u = Uri.fromFile(f);
            intent.putExtra(Intent.EXTRA_STREAM, u);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(Intent.createChooser(intent, "分享"));
    }

    //双击大段文本预览
    public static void doubleTextPre(final Context ctx, View tvText, final String texts) {
        if (tvText == null || isEmptyOrNull(texts)) {
            return;
        }
        DoubleClickImp.registerDoubleClickListener(tvText,
                new DoubleClickImp.OnDoubleClickListener() {
                    @Override
                    public void OnSingleClick(View v) {
                    }

                    @Override
                    public void OnDoubleClick(View v) {
                        if (notEmptyOrNull(texts)) {
                            Intent newIntent = new Intent(ctx, ShowInfoActivity.class);
                            newIntent.putExtra("show", texts);
                            ctx.startActivity(newIntent);
                        }
                    }
                });
    }

    public static int getDeviceWidth() {
        return getDeviceMetrics().widthPixels;
    }

    public static float getTextLength(final float textSize, String text) {
        Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // Define the string.
        mTextPaint.setTextSize(textSize);
        float textWidth = mTextPaint.measureText(text);
        return textWidth;
    }

    public static boolean isEmptyOrNull(String string) {
        if (string == null || string.trim().length() == 0 || string.equalsIgnoreCase("null")) {
            return true;
        } else {
            return false;
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void initIndicatorTheme(PagerSlidingTabStrip indicator) {
        Context ctx = MyApplication.getInstance().ctx;
        indicator.setTextSize(dip2px(16));
        indicator.setTextColor(ctx.getResources().getColor(R.color.white));
        indicator.setIndicatorColor(ctx.getResources().getColor(R.color.white));
//        indicator.setBackgroundColor(ctx.getResources().getColor(R.color.black));
    }

    public static Drawable initIconTheme(Iconify.IconValue iconValue) {
        Context ctx = MyApplication.getInstance().ctx;
        return new IconDrawable(MyApplication.getInstance().ctx, iconValue).color(ctx.getResources().getColor(R.color.primary)).sizeDp(30);
    }

    public static Drawable initIconUnSelTheme(Iconify.IconValue iconValue) {
        Context ctx = MyApplication.getInstance().ctx;
        return new IconDrawable(MyApplication.getInstance().ctx, iconValue).color(ctx.getResources().getColor(R.color.gray)).sizeDp(30);
    }

    public static Drawable initIconThemeSel(Iconify.IconValue iconValue) {
        Context ctx = MyApplication.getInstance().ctx;
        return new IconDrawable(MyApplication.getInstance().ctx, iconValue).color(ctx.getResources().getColor(R.color.primary)).sizeDp(30);
    }


    public static String delHTMLTag(String htmlStr) {
        try {
            String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
            String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
            String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式
            Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            Matcher m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); //过滤script标签
            Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            Matcher m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); //过滤style标签
            Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            Matcher m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); //过滤html标签
        } catch (Exception e) {
            e.printStackTrace();
        }
        return htmlStr.trim(); //返回文本字符串
    }

    /**
     * 去掉字符串里面的html代码。<br>
     * 要求数据要规范，比如大于小于号要配套,否则会被集体误杀。
     *
     * @param content 内容
     * @return 去掉后的内容
     */
    public static String stripHtml(String content) {
        if (isEmptyOrNull(content)) {
            return "";
        }
        // <p>段落替换为换行
        content = content.replaceAll("<p .*?>", "/r/n");
        // <br><br/>替换为换行
        content = content.replaceAll("<br//s*/?>", "/r/n");
        // 去掉其它的<>之间的东西 \u00A0
        content = content.replaceAll("//<.*?>", "");
        return content;
    }

    public static String getChinese(String paramValue) {
        String regex = "([\u4e00-\u9fa5]+)";
        String str = "";
        Matcher matcher = Pattern.compile(regex).matcher(paramValue);
        while (matcher.find()) {
            str += matcher.group(0);
        }
        return str;
    }

    public static Spanned fromHtml(String content) {
        if (isEmptyOrNull(content)) {
            return null;
        }
        content = content.replaceAll("\\\\u00A0", "");
        content = content.trim();
        Spanned contentHtml = Html.fromHtml(content);
        return contentHtml;
    }


    public static void stripUnderlinesEditText(EditText editText) {
        if (null != editText && editText.getText() instanceof Spannable) {
            Spannable s = (Spannable) editText.getText();
            URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
            if (spans != null && spans.length > 0) {
                for (URLSpan span : spans) {
                    int start = s.getSpanStart(span);
                    int end = s.getSpanEnd(span);
                    s.removeSpan(span);
                    span = new URLSpanUtils(span.getURL());
                    s.setSpan(span, start, end, 0);
                }
            }
        }
    }

    public static String getDateY_M_D() {
        return new SimpleDateFormat("yyyy_MM_dd").format(new Date());
    }

    public static String getDateY_M_D_H_M_S() {
        return new SimpleDateFormat("yyyy_MM_dd_H_M_S").format(new Date());
    }


    public static String getDateFull() {
        return new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date());
    }

    public static String getDateFullSec() {
        return new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date());
    }

    public static String getDateFull(Date date) {
        if (date == null) {
            date = new Date();
        }
        return new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(date);
    }

    public static String getDateFull(Long time) {
        Date date = new Date();
        if (time != null) {
            date = new Date(time);
        }
        return new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(date);
    }


    // 打开文件
    public static void openFile(Context ctx, String filePath) {
        Intent openFileIntent = FileMiniUtil.openFile(filePath);
        try {
            ctx.startActivity(openFileIntent);
        } catch (Exception e) {// 无对应打开方式,列出所有打开方式
            Intent openUnKnowFileIntent = FileMiniUtil.getUnKnowIntent(filePath);
            ctx.startActivity(openUnKnowFileIntent);
            e.printStackTrace();
        }
    }

    /**
     *打开链接
     */
    public static void openUrl(Context ctx, String url) {
        Intent intent = new Intent();
        try {
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            ctx.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isPic(String url) {
        if (notEmptyOrNull(url)) {
            if (url.startsWith("http") || url.contains("www")) {
                if (url.endsWith("jpg") || url.endsWith("gif") || url.endsWith("png")
                        || url.endsWith("jpeg") || url.endsWith("bmp") || url.endsWith("/640")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void sendTo(Context ctx, String sendWhat) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sendWhat);
        ctx.startActivity(shareIntent);
    }

    public static void copyText(Activity ctx, String text) {
        ClipboardManager cbm = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        cbm.setText(text);

    }

    public static String getVersionName(Activity ctx) {
        PackageManager packageManager = ctx.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(ctx.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
    }

    public static File createFolder(String path) {
        if (XUtil.notEmptyOrNull(path)) {
            File dir = new File(path);
            if (dir.exists()) {
                if (dir.isDirectory()) {
                    return dir;
                }
            }
            dir.mkdirs();
            return dir;
        } else {
            return null;
        }
    }

    public static File createFile(String path) throws IOException {
        if (XUtil.notEmptyOrNull(path)) {
            File file = new File(path);
            if (!file.exists()) {
                int lastIndex = path.lastIndexOf(File.separator);
                String dir = path.substring(0, lastIndex);
                if (createFolder(dir) != null) {
                    file.createNewFile();
                    return file;
                }
            } else {
                file.createNewFile();
                return file;
            }
        }
        return null;
    }

    public static boolean copyFileTo(File srcFile, File destFile)
            throws IOException {
        if (srcFile == null || destFile == null) {
            return false;
        }
        if (srcFile.isDirectory() || destFile.isDirectory())
            return false;
        if (!srcFile.exists()) {
            return false;
        }
        if (!destFile.exists()) {
            createFile(destFile.getAbsolutePath());
        }
        FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(destFile);
        int readLen = 0;
        byte[] buf = new byte[1024];
        while ((readLen = fis.read(buf)) != -1) {
            fos.write(buf, 0, readLen);
        }
        fos.flush();
        fos.close();
        fis.close();
        return true;
    }

    public static void contentToTxt(String filePath, String content) {
        String str = new String(); //原有txt内容
        String s1 = new String();//内容更新
        try {
            File f = new File(filePath);
//            if (f.exists()) {
//                System.out.print("文件存在");
//            } else {
//                System.out.print("文件不存在");
//                f.createNewFile();// 不存在则创建
//            }
            createFile(filePath);
            BufferedReader input = new BufferedReader(new FileReader(f));
            while ((str = input.readLine()) != null) {
                s1 += str + "\n";
            }
            System.out.println(s1);
            input.close();
            s1 += content;
            BufferedWriter output = new BufferedWriter(new FileWriter(f));
            output.write(s1);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    public static String getPathFromUri(Context context, Uri uri) {
        String path = "";
        if (uri == null || "content://media/external/file/-1".equals(uri.toString())) {
            XUtil.tShort("文件选取失败~");
            return null;
        }
        String[] projection = {"_data"};
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        path = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                    }
                    cursor.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }
        return path;
    }

    public static long getTimeLong(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }

    public static int dip2px(float dipValue) {
        final float scale = MyApplication.getInstance().ctx.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        final float scale = MyApplication.getInstance().ctx.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static Drawable initIconWhite(Iconify.IconValue iconValue) {
        return new IconDrawable(MyApplication.getInstance().ctx, iconValue).colorRes(R.color.white).sizeDp(24);
    }

    public static Drawable initIconWhite(Iconify.IconValue iconValue, int sizeDp) {
        return new IconDrawable(MyApplication.getInstance().ctx, iconValue).colorRes(R.color.white).sizeDp(sizeDp);
    }

    public static Drawable initIconDark(Iconify.IconValue iconValue) {
        return new IconDrawable(MyApplication.getInstance().ctx, iconValue).colorRes(R.color.icon_dark).sizeDp(24);
    }

    public static void tShort(String string) {
        Toast.makeText(MyApplication.getInstance().ctx, string, Toast.LENGTH_SHORT).show();
    }

    public static void tLong(String string) {
        Toast.makeText(MyApplication.getInstance().ctx, string, Toast.LENGTH_LONG).show();
    }

    public static String subStr(String input, int len) {
        int inpuLen = input.length();
        if (len >= inpuLen) {
            return input;
        } else {
            return input.substring(0, len);
        }

    }

    public static String subStrDot(String input, int len) {
        if (isEmptyOrNull(input)) {
            return null;
        }
        int inpuLen = input.length();
        if (len >= inpuLen) {
            return input;
        } else {
            return input.substring(0, len) + "...";
        }
    }

    public static void autoKeyBoardShow(final EditText editText) {
        new Timer().schedule(new TimerTask() {
                                 public void run() {
                                     InputMethodManager inputManager =
                                             (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                     inputManager.showSoftInput(editText, 0);
                                 }
                             },
                500);
    }

    public static void dismissShowDialog(DialogInterface dialog, boolean flag) {
//        try {
//            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
//            field.setAccessible(true);
//            field.set(dialog, flag);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        if (flag) {
            dialog.dismiss();
        }
    }

    public static void debug(Object string) {
        try {
            Log.i("[lib]", String.valueOf(string));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean notEmptyOrNull(String string) {
        if (string != null && !string.equalsIgnoreCase("null") && string.trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }


    public static <T> boolean listNotNull(List<T> t) {
        if (t != null && t.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
