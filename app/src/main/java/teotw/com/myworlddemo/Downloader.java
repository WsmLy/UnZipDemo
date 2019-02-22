package teotw.com.myworlddemo;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

/**
 * Created by xutingting on 2018/2/28.
 * 用DownLoadManager去下载
 */

public class Downloader {

    //下载器
    private DownloadManager downloadManager;
    //上下文
    private Context mContext;
    //下载的ID
    private long downloadId;

    public Downloader(Context context) {
        this.mContext = context;
    }
    
    //下载apk
    public void downloadAPK(String url, String name) {

        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //移动网络情况下是否允许漫游
        request.setAllowedOverRoaming(false);

        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle(name);
        request.setDescription("正在下载...");
        request.setVisibleInDownloadsUi(true);

        //设置下载的路径
        //  request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory().getAbsolutePath(), name);
        request.setDestinationInExternalPublicDir("xianjinfenqixt", name);

        //获取DownloadManager
        downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
        downloadId = downloadManager.enqueue(request);
    }
}