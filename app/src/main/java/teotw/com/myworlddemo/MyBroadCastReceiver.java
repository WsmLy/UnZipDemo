package teotw.com.myworlddemo;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;

/**
 * Created by xutingting on 2018/4/13.
 * 静态注册的MyBroadCastReceiver,收到下载完成的消息后进行安装
 */

public class MyBroadCastReceiver extends BroadcastReceiver {

    private long downloadId;

    private DownloadManager downloadManager;


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            //检查下载状态
            checkStatus(context);
        }
    }


    /**
     * 检查下载状态
     * 这里的contxt 直接用onReceive里的context，MyBroadCastReceiver不能有构造方法这里需要注意，需要参数的时候从onReceiver里的context和intent获得
     * @param context
     */
    private void checkStatus(Context context) {
        DownloadManager.Query query = new DownloadManager.Query();
        //通过下载的id查找
        query.setFilterById(downloadId);
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                //下载暂停
                case DownloadManager.STATUS_PAUSED:
                    break;
                //下载延迟
                case DownloadManager.STATUS_PENDING:
                    break;
                //正在下载
                case DownloadManager.STATUS_RUNNING:
                    break;
                //下载完成
                case DownloadManager.STATUS_SUCCESSFUL:
                    //下载完成安装APK
//                    installAPK(context);
                    String inPath = queryDownloadedPath();
                    String[] path = inPath.split("/");
                    String outPath = inPath.substring(0, inPath.length() - path[path.length-1].length());
                    ZipExtractorTask task = new ZipExtractorTask(inPath, outPath+"unzip", context, true, outPath+"unzip/index.html");
                    task.doInBackground();
                    break;
                //下载失败
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public String queryDownloadedPath() {
//        File targetApkFile = null;
        if (downloadId != -1) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
            Cursor cur = downloadManager.query(query);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (!uriString.isEmpty()) {
//                        targetApkFile = new File(Uri.parse(uriString).getPath());
                        return Uri.parse(uriString).getPath();
                    }
                }
                cur.close();
            }
        }
        return null;
    }

    /**
     * 下载到本地后执行安装
     * @param context
     */
    private void installAPK(Context context) {

        Intent intent = new Intent();
        File apkFile = queryDownloadedApk();
        String packageName = context.getPackageName();
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //7.0启动姿势<pre name="code" class="html">    //com.xxx.xxx.fileprovider为上述manifest中provider所配置相同；apkFile为问题1中的外部存储apk文件</pre>
            uri = FileProvider.getUriForFile(context, packageName + ".fileprovider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//7.0以后，系统要求授予临时uri读取权限，安装完毕以后，系统会自动收回权限，次过程没有用户交互
            intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
        } else {
            //7.0以下启动姿势
            uri = Uri.fromFile(apkFile);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


    public File queryDownloadedApk() {
        File targetApkFile = null;
        if (downloadId != -1) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
            Cursor cur = downloadManager.query(query);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (!uriString.isEmpty()) {
                        targetApkFile = new File(Uri.parse(uriString).getPath());
                    }
                }
                cur.close();
            }
        }
        return targetApkFile;
    }
}