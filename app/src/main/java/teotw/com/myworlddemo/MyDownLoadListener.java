package teotw.com.myworlddemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by xutingting on 2017/8/17.
 * 这个类的作用是监听到有需要下载的文件时弹出下载对话框，点击下载后调用下载的类的downloadApk。
 */

public class MyDownLoadListener implements DownloadListener {
    private Context context;

    private String name;

    public MyDownLoadListener(Context context) {
        this.context = context;
    }

    @Override
    public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

        //截取url的最后/后面的字符串作为每个下载app的名字
        String[] split = "http://ntimg.eyeoftheworld.cn/Uploads/zip/20190215135705.zip".split("/");
        if (split.length > 0) {
            name = split[split.length - 1];
        }

        /**
         * 弹出下载提示框
         */

        final AlertDialog dialog = new AlertDialog.Builder(context, R.style.Theme_AppCompat).create();
        View inflate = LayoutInflater.from(context).inflate(R.layout.xia_zai_dialog, null);
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.setView(inflate);
        dialog.show();
        //将设置好的属性set回去
        window.setAttributes(lp);


        LinearLayout mGroundLayout = (LinearLayout) inflate.findViewById(R.id.bei_jing_layout);

        TextView mCancleTextView = (TextView) inflate.findViewById(R.id.qu_xiao_textview); //取消按钮

        TextView mSureTextView = (TextView) inflate.findViewById(R.id.xia_zai_textView); //下载按钮

        mCancleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mSureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        // Looper.prepare();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                //检查权限，有去下载，没有去请求权限。
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                                } else {
                                    Downloader downloader = new Downloader(context);  //下载类
                                    Toast.makeText(context, "成功创建下载任务，正在下载", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    if (name.length() > 0) {
                                        downloader.downloadAPK(url, name);//DownLoader 需要在oncreate 中初始化
                                    } else {
                                        downloader.downloadAPK(url, "***.apk");//DownLoader 需要在oncreate 中初始化
                                    }

                                }

                            }
                        });
                    }
                }.start();

            }
        });

    }
}