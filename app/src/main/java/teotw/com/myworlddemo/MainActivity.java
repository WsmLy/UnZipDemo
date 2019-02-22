package teotw.com.myworlddemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static WebView worldView;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        WebView webView = findViewById(R.id.webview);
        worldView = findViewById(R.id.webview);

        loadWebView("http://nttw.eyeoftheworld.cn/Index/index/v/v1.0.76.html");

//        webView.loadUrl("file:///android_asset/test.html");
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//        webSettings.setAppCacheEnabled(true);
//        webView.setWebViewClient(new ViewClient());
//        webView.setWebChromeClient(new ChromeClient());
//
//        webView.addJavascriptInterface(new OnJsToAndroid(), "android");
    }

    String name;
    String url1;
    private void loadWebView(String url) {
//        if (AppDataUtils.getInstance().isLogin()) {
//            Map<String, String> map = new HashMap<>();
//            map.put("key", AppDataUtils.getInstance().getCurrentKey());
//            if (MainActivity.lnt != 0) {
//                map.put("longitude", MainActivity.lnt + "");
//            }
//            if (MainActivity.lat != 0) {
//                map.put("latitude", MainActivity.lat + "");
//            }
//            worldView.loadUrl(url, map);
//        } else {
            worldView.loadUrl(url);
//        }
        WebSettings webSettings = worldView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);//文件权限
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowContentAccess(true);
        //缓存相关
//        if(InternetUtil.checkInternet(context)){
            //有网络，则加载网络地址
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//设置缓存模式LOAD_CACHE_ELSE_NETWORK
//        }else{
//            //无网络，则加载缓存路径
//            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        }
        webSettings.setDomStorageEnabled(true);//开启DOM storage API功能
        webSettings.setDatabaseEnabled(true);//开启database storeage API功能
        String cacheDirPath = getFilesDir().getAbsolutePath()+ "/webcache";//缓存路径
        webSettings.setDatabasePath(cacheDirPath);//设置数据库缓存路径
        webSettings.setAppCachePath(cacheDirPath);//设置AppCaches缓存路径
        webSettings.setAppCacheEnabled(true);//开启AppCaches功能

        worldView.setWebViewClient(new ViewClient());
        worldView.setWebChromeClient(new ChromeClient());

        worldView.addJavascriptInterface(new OnJsToAndroid(), "android");

//        worldView.setDownloadListener(new MyDownLoadListener(this));
//        worldView.setInitialScale(0);

        //截取url的最后/后面的字符串作为每个下载app的名字
        url1 = "http://ntimg.eyeoftheworld.cn/Uploads/zip/20190218140743.zip";
        String[] split = url1.split("/");
        if (split.length > 0) {
            name = split[split.length - 1];
            name = name.replace("apk", "zip");
        }

        /**
         * 弹出下载提示框
         */

        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View inflate = LayoutInflater.from(this).inflate(R.layout.xia_zai_dialog, null);
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
//        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
//        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.setView(inflate);
//        dialog.show();
        //将设置好的属性set回去
//        window.setAttributes(lp);


        LinearLayout mGroundLayout = (LinearLayout) inflate.findViewById(R.id.bei_jing_layout);

        TextView mCancleTextView = (TextView) inflate.findViewById(R.id.qu_xiao_textview); //取消按钮

        TextView mSureTextView = (TextView) inflate.findViewById(R.id.xia_zai_textView); //下载按钮

        mCancleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

//        mSureTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        // Looper.prepare();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                //检查权限，有去下载，没有去请求权限。
                                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions((Activity) MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                                } else {
                                    Downloader downloader = new Downloader(MainActivity.this);  //下载类
                                    Toast.makeText(MainActivity.this, "成功创建下载任务，正在下载", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    if (name.length() > 0) {
                                        downloader.downloadAPK(url1, name);//DownLoader 需要在oncreate 中初始化
                                    } else {
                                        downloader.downloadAPK(url1, "app-release.apk");//DownLoader 需要在oncreate 中初始化
                                    }

                                }

                            }
                        });
                    }
                }.start();

//            }
//        });
    }

    public static void loadGame(String url) {
        worldView.loadUrl(url);
    }

    /**
     * 清除WebView缓存
     */
    public void clearWebViewCache(){
        //清理Webview缓存数据库
        try {
            deleteDatabase("webview.db");
            deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //WebView 缓存文件
        File appCacheDir = new File(getFilesDir().getAbsolutePath()+"/webcache");
//        Log.e(TAG, "appCacheDir path="+appCacheDir.getAbsolutePath());

        File webviewCacheDir = new File(getCacheDir().getAbsolutePath()+"/webviewCache");
//        Log.e(TAG, "webviewCacheDir path="+webviewCacheDir.getAbsolutePath());

        //删除webview 缓存目录
        if(webviewCacheDir.exists()){
            deleteFile(webviewCacheDir.getName());
        }
        //删除webview 缓存 缓存目录
        if(appCacheDir.exists()){
            deleteFile(appCacheDir.getName());
        }
    }

    private class ViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
//            webView.loadUrl("javascript:function loadinfoname(){document.getElementsByTagName('input')[0].value='1234';}loadinfoname();");
//            webView.loadUrl("javascript:function loadpassword(){document.getElementsByTagName('input')[1].value='1234';}loadpassword();");
//            worldView.loadUrl("javascript:receiveMobile ("+mData.getMobile()+")");
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            if ("ios://backController".equals(url)) {
//                finish();
//            }
        }
    }

    private class ChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            result.confirm();
            return true;
        }
    }

    private class OnJsToAndroid {
        @JavascriptInterface
        public void goLogin() {
        }

        @JavascriptInterface
        public void goFootprintView(String bid) {
        }

        @JavascriptInterface
        public void goShop() {

        }

        @JavascriptInterface
        public void buildInfo(int id) {
        }
    }
}
