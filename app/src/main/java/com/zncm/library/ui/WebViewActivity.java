package com.zncm.library.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.malinskiy.materialicons.Iconify;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nanotasks.BackgroundWork;
import com.nanotasks.Completion;
import com.nanotasks.Tasks;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;
import com.zncm.library.R;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Lib;
import com.zncm.library.data.MyApplication;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.utils.DbHelper;
import com.zncm.library.utils.Dbutils;
import com.zncm.library.utils.FileMiniUtil;
import com.zncm.library.utils.MyPath;
import com.zncm.library.utils.NotiHelper;
import com.zncm.library.utils.XUtil;
import com.zncm.library.utils.htmlbot.contentextractor.ContentExtractor;
import com.zncm.library.view.MyWebView;

import de.greenrobot.event.EventBus;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class WebViewActivity extends BaseAc {
    public static String charset = "UTF-8";
    private MyWebView mWebView;
    private String url;
    private Activity ctx;
    static boolean isImport = false;
    MaterialSearchView searchView;

    Set<String> urlSet = new HashSet<>();
    ArrayList<String> urls = new ArrayList<>();
    private GestureDetector gestureDetector;
    private int downX, downY;


    String imgurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("");
        initView();
        initData();
    }

    @Override
    protected int setCV() {
        return R.layout.activity_webview;
    }

    private void initView() {
        ctx = this;
        url = getIntent().getExtras().getString("url");
        isImport = getIntent().getExtras().getBoolean("isImport", false);
        mWebView = (MyWebView) findViewById(R.id.mWebView);
//        mWebView.setWebViewClient(new WebViewClient() {
//            public boolean shouldOverrideUrlLoading(WebView view, final String url) {
//                if (!XUtil.notEmptyOrNull(url)) {
//                    return true;
//                }
//
//
//                Tasks.executeInBackground(ctx, new BackgroundWork<Boolean>() {
//                    @Override
//                    public Boolean doInBackground() throws Exception {
//                        try {
//                            Document doc;
//                            doc = Jsoup.connect(url).timeout(5000).get();
//                            Elements srcLinks = doc.select("img[src$=.jpg]");
//                            for (Element link : srcLinks) {
//                                String imagesPath = link.attr("src");
//                                ShareAc.initSave(Constant.SYS_PICS, url, imagesPath);
//                            }
//                            return true;
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        return false;
//                    }
//                }, new Completion<Boolean>() {
//                    @Override
//                    public void onSuccess(Context context, Boolean result) {
//
//                    }
//
//                    @Override
//                    public void onError(Context context, Exception e) {
//                    }
//                });
//
////                mWebView.loadUrl(url);
//
//                WebViewActivity.this.url = url;
//
//                initData();
//
//                return true;
//            }
//        });
//
//        mWebView.setWebChromeClient(new WebChromeClient() {
//            public void onProgressChanged(WebView view, int progress) {
//                getSupportActionBar().setTitle(view.getTitle());
//
//            }
//        });


        mWebView.setWebChromeClient(new WebChromeClient() {
            //android>5.0调用这个方法
            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                mUploadCallbackAboveL=filePathCallback;
                take();
                return true;
            }
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage=uploadMsg;
                take();
            }
            public void openFileChooser(ValueCallback<Uri> uploadMsg,String acceptType) {
                mUploadMessage=uploadMsg;
                take();
            }
            public void openFileChooser(ValueCallback<Uri> uploadMsg,String acceptType, String capture) {
                mUploadMessage=uploadMsg;
                take();
            }
        });





        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);


        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {

                new MaterialDialog.Builder(ctx)
                        .title("文件")
                        .content("确认下载" + url)
                        .positiveText("下载")
                        .negativeText("取消")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                XUtil.tShort("文件正在下载，请稍后...");
                                downloadFile(WebViewActivity.this, url, isImport);
                            }
                        }).show();


            }
        });


        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                downX = (int) e.getX();
                downY = (int) e.getY();
            }
        });
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = ((WebView) v).getHitTestResult();
                if (WebView.HitTestResult.IMAGE_TYPE == result.getType()) {
                    if (XUtil.notEmptyOrNull(result.getExtra())) {
                        Intent intent = new Intent(ctx, PhotoAc.class);
                        intent.putExtra("url", result.getExtra());
                        startActivity(intent);
                    }
                }
                return true;
            }
        });



        mWebView.setOnGetSelectTextListener(new MyWebView.OnGetSelectTextListener() {
            @Override
            public void getSelectText(String text) {
                Toast.makeText(ctx,text,Toast.LENGTH_SHORT).show();
            }
        });


//        mWebView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                WebView.HitTestResult result = ((WebView) v).getHitTestResult();
//                if (WebView.HitTestResult.IMAGE_TYPE==result.getType()){
//                    if (XUtil.notEmptyOrNull(result.getExtra())){
//                        Intent intent = new Intent(ctx, PhotoAc.class);
//                        intent.putExtra("url", result.getExtra());
//                        startActivity(intent);
//                    }
//                }
//
//            }
//        });
//        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                XUtil.debug("长按了webview");
//
//
//                WebView.HitTestResult result = ((WebView) v).getHitTestResult();
//
//
//                /**
//                 *WebView.HitTestResult.UNKNOWN_TYPE    未知类型
//                 WebView.HitTestResult.PHONE_TYPE    电话类型
//                 WebView.HitTestResult.EMAIL_TYPE    电子邮件类型
//                 WebView.HitTestResult.GEO_TYPE    地图类型
//                 WebView.HitTestResult.SRC_ANCHOR_TYPE    超链接类型
//                 WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE    带有链接的图片类型
//                 WebView.HitTestResult.IMAGE_TYPE    单纯的图片类型
//                 WebView.HitTestResult.EDIT_TEXT_TYPE    选中的文字类型
//                 */
//
//
//                if (null == result)
//                    return false;
//                int type = result.getType();
//                if (type == WebView.HitTestResult.UNKNOWN_TYPE)
//                    return false;
//                if (type == WebView.HitTestResult.EDIT_TEXT_TYPE) {
//                    //let TextViewhandles context menu return true;
//                }
//                final ItemLongClickedPopWindow itemLongClickedPopWindow = new ItemLongClickedPopWindow(WebViewActivity.this, ItemLongClickedPopWindow.IMAGE_VIEW_POPUPWINDOW, XUtil.dip2px(120), XUtil.dip2px(90));
//                // Setup custom handlingdepending on the type
//                switch (type) {
//                    case WebView.HitTestResult.PHONE_TYPE: // 处理拨号
//                        break;
//                    case WebView.HitTestResult.EMAIL_TYPE: // 处理Email
//                        break;
//                    case WebView.HitTestResult.GEO_TYPE: // TODO
//                        break;
//                    case WebView.HitTestResult.SRC_ANCHOR_TYPE: // 超链接
//                        // Log.d(DEG_TAG, "超链接");
//                        break;
//                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
//                        break;
//                    case WebView.HitTestResult.IMAGE_TYPE: // 处理长按图片的菜单项
//                        imgurl = result.getExtra();
//                        //通过GestureDetector获取按下的位置，来定位PopWindow显示的位置
////                        itemLongClickedPopWindow.showAtLocation(v, Gravity.TOP | Gravity.LEFT, (int) v.getX()+ 10, (int) v.getY() + 10);
//
//                        if (XUtil.notEmptyOrNull(imgurl)){
//                            Intent intent = new Intent(ctx, PhotoAc.class);
//                            intent.putExtra("url", imgurl);
//                            startActivity(intent);
//                        }
//                        break;
//                    default:
//                        break;
//                }
//                return true;
//            }
//        });


        searchView = (MaterialSearchView) ctx.findViewById(R.id.search_view);
        searchView.setHint("搜索");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (XUtil.notEmptyOrNull(query)) {
                    if (query.startsWith("http") || query.startsWith("www")) {
                        mWebView.loadUrl(query);
                    } else {
                        mWebView.loadUrl(Constant.NET_BAIDU + query);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
            }
        });


    }


    public static class MyTask extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... params) {
            try {
                String content = ContentExtractor.getContentByURL(params[0]);
                ShareAc.initSave(Constant.SYS_NET_TEXT, content, params[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }



    }


    public static void downloadFile(final Context ctx, final String url, final boolean isImport) {
        ThinDownloadManager downloadManager = new ThinDownloadManager(4);
        String fileEnd = "_";
        if (isImport) {
            fileEnd = ".csv";
        } else if (url.contains(".")) {
            fileEnd = url.substring(url.lastIndexOf("."));
            if (XUtil.notEmptyOrNull(fileEnd)) {
                if (fileEnd.contains("?")) {
                    fileEnd = fileEnd.substring(0, fileEnd.indexOf("?"));
                }
                if (fileEnd.length() > 10) {
                    fileEnd = fileEnd.substring(0, 10);
                }
            }
        }
        final String newFile = MyPath.getPathDownload() + "/" + System.currentTimeMillis() + fileEnd;
        Uri downloadUri = Uri.parse(url);
        Uri destinationUri = Uri.parse(newFile);
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadListener(new DownloadStatusListener() {
                    @Override
                    public void onDownloadComplete(int id) {
                        if (isImport) {
                            if (newFile.contains("csv") || newFile.contains("txt")) {
                                if (url.equals(Constant.LOCLIB_NET)) {
                                    DbHelper.importLocCsv(ctx, new File(newFile));
                                } else {
                                  getData(ctx, newFile);
                                }
                            } else {
                                XUtil.tShort("格式不支持");
                            }
                        }
                        XUtil.tLong("文件下载完毕：" + newFile);
                        Intent openUnKnowFileIntent = FileMiniUtil.getUnKnowIntent(newFile);
                        NotiHelper.noti("下载完成", newFile, "文件下载完毕~~~", openUnKnowFileIntent, false, new Random().nextInt());
                        if (XUtil.notEmptyOrNull(newFile)) {
                            ShareAc.initSave(Constant.SYS_FILE_DOWNLOAD, newFile, url);
                        }

                    }

                    @Override
                    public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                    }


                    @Override
                    public void onProgress(int id, long totalBytes, long downloadedBytes, int progress) {
                    }
                });
        downloadManager.add(downloadRequest);



    }


    private void initData() {
        if (!XUtil.notEmptyOrNull(url)) {
            return;
        }

        if (urlSet.add(url)) {
            urls.add(url);
        }
        mWebView.loadUrl(url);
        ShareAc.initSave(Constant.SYS_NET_HISTORY, url, url);
//        MyTask myTask = new MyTask();
//        myTask.execute(url, url);
    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item == null || item.getTitle() == null) {
            return false;
        }


        switch (item.getItemId()) {
            case 1:
                XUtil.sendTo(ctx, url);
                break;
            case 2:
                ShareAc.initSave(Constant.SYS_COLLECT, url, mWebView.getTitle());


                XUtil.tShort("已收藏网址");
                break;
            case 3:
                XUtil.copyText(ctx, url);
                XUtil.tShort("已复制");
                break;
            case 4:
                if (XUtil.notEmptyOrNull(url) && !url.startsWith("file:///")) {
                    if (item.getTitle().equals("在浏览器中打开")) {
                        try {
                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(i);
                        } catch (Exception e) {

                        }
                    }
                }
                break;
            case 5:
                mWebView.reload();
                break;
            case 6:
                ArrayList<String> strs = new ArrayList<>();
                if (XUtil.notEmptyOrNull(url)) {
                    strs.add(mWebView.getTitle());
                    strs.add(url);
                }
                ShareAc.initSaveList(Constant.SYS_SC, Constant.SYS_SC_MK, strs, Lib.libType.sys.value());
                XUtil.tShort("已添加收藏");
                break;

            case 7:
                searchView.showSearch(true);
                break;
            case 8:
                if (XUtil.listNotNull(urls)) {
                    url = urls.get(urls.size() - 1);
                }
                initData();
                break;

            case 9:
                Lib data = Dbutils.getLibSys(Constant.SYS_NET_HISTORY);
                if (data != null) {
                    Intent intent = new Intent(ctx, ItemsAc.class);
                    intent.putExtra(Constant.KEY_PARAM_DATA, data);
                    startActivity(intent);
                }
                break;


            case 10:
                ShareAc.initLibNet(url, "li");
                XUtil.tShort("已添加到网络库！");
                break;


            case 11:
                geUrlHtml(url);
                break;
            case 12:
                ShareAc.initLibRss(url, url);
                XUtil.tShort("已添加RSS源！");
                break;

            case 13:

                break;

            case 14:
                ShareAc.initLibApi(url, "results");
                XUtil.tShort("已添加到API库！");
                break;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void geUrlHtml(final String url) {
        try {

            //XUtil.tShort(Constant.SYS_BAIDU_BK + "-正在下载，第" + page + "页");
            Tasks.executeInBackground(MyApplication.getInstance().ctx, new BackgroundWork<String>() {
                @Override
                public String doInBackground() throws Exception {
                    String htmlStr = "";
                    try {
                        if (XUtil.isEmptyOrNull(url)) {
                            return htmlStr;
                        }
                        Document doc = Jsoup.connect(url).timeout(3000).get();
                        htmlStr = doc.html();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return htmlStr;
                }
            }, new Completion<String>() {
                @Override
                public void onSuccess(Context context, String result) {
                    if (XUtil.notEmptyOrNull(result)) {
                        Intent newIntent = new Intent(context, ShowInfoActivity.class);
                        newIntent.putExtra("show", result);
                        context.startActivity(newIntent);
                    }
                }

                @Override
                public void onError(Context context, Exception e) {
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu sub = menu.addSubMenu("");
        sub.setIcon(XUtil.initIconWhite(Iconify.IconValue.md_more_vert));
        sub.add(0, 1, 0, "分享");
        sub.add(0, 2, 0, "收藏网页");
        sub.add(0, 3, 0, "复制链接");
        sub.add(0, 4, 0, "在浏览器中打开");
        sub.add(0, 5, 0, "刷新");
        sub.add(0, 6, 0, "收藏");
        sub.add(0, 7, 0, "百度一下");
        sub.add(0, 8, 0, "上一页");
        sub.add(0, 9, 0, "历史记录");
        sub.add(0, 10, 0, "添加到网络库");
        sub.add(0, 11, 0, "html源码");
        sub.add(0, 12, 0, "添加RSS源");
        sub.add(0, 14, 0, "添加到API库");
        sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }



    private ValueCallback<Uri> mUploadMessage;// 表单的数据信息
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private final static int FILECHOOSER_RESULTCODE = 1;// 表单的结果回调
    private Uri imageUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==FILECHOOSER_RESULTCODE)
        {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            }
            else  if (mUploadMessage != null) {
                Log.e("result",result+"");
                if(result==null){
//                   mUploadMessage.onReceiveValue(imageUri);
                    mUploadMessage.onReceiveValue(imageUri);
                    mUploadMessage = null;

                    Log.e("imageUri",imageUri+"");
                }else {
                    mUploadMessage.onReceiveValue(result);
                    mUploadMessage = null;
                }


            }
        }
    }

    @SuppressWarnings("null")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE
                || mUploadCallbackAboveL == null) {
            return;
        }

        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                results = new Uri[]{imageUri};
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();

                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }

                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        if(results!=null){
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        }else{
            results = new Uri[]{imageUri};
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        }

        return;
    }

    private void take(){
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyApp");
        // Create the storage directory if it does not exist
        if (! imageStorageDir.exists()){
            imageStorageDir.mkdirs();
        }
        File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        imageUri = Uri.fromFile(file);

        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent i = new Intent(captureIntent);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            i.setPackage(packageName);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntents.add(i);

        }
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        Intent chooserIntent = Intent.createChooser(i,"Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        startActivityForResult(chooserIntent,  FILECHOOSER_RESULTCODE);
    }



    public static void getData(Context ctx, String path) {
//        dlg = new MaterialDialog.Builder(ctx)
//                .title("请稍后...")
//                .autoDismiss(false)
//                .show();
        GetData getData = new GetData();
        getData.execute(path);
    }

    static class GetData extends AsyncTask<String, Integer, Boolean> {

        protected Boolean doInBackground(String... params) {
            DbHelper.importCsv(new File(params[0]), charset);
            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            XUtil.tShort("values: " + values[0]);
        }

        protected void onPostExecute(Boolean canLoadMore) {
            super.onPostExecute(canLoadMore);
            XUtil.tShort("数据已成功导入 ~");
            EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.LIB.getValue()));
//            dlg.dismiss();
        }
    }

}
