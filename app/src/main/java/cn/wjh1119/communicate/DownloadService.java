package cn.wjh1119.communicate;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * DownloadService
 * Created by Mr.King on 2017/5/15 0015.
 */

public class DownloadService extends Service {
    /**
     * 进度条的最大值
     */
    public static final int MAX_PROGRESS = 100;
    /**
     * 进度条的进度值
     */
    private int progress = 0;

    private Thread mThread;

    private volatile boolean isStopped = false;

    private ServiceActivity.OnProgressListener mOnProgressListenner;

    /**
     * 增加get()方法，供Activity调用
     *
     * @return 下载进度
     */
    public int getProgress() {
        return progress;
    }

    /**
     * 模拟下载任务，每秒钟更新一次
     */
    public void startDownLoad() {
        mThread = new Thread(new Runnable() {

            @Override
            public void run() {

                while (!isStopped && progress < MAX_PROGRESS) {
                    progress += 5;

                    //使用interface传递信息
                    if (mOnProgressListenner != null) {
                        mOnProgressListenner.setProgress(progress);
                    }

                    //使用广播传递信息
                    Intent intent = new Intent("cn.wjh1119.communicate.DOWNLOAD");
                    intent.putExtra("progress", progress);
                    sendBroadcast(intent);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                isStopped = false;
            }
        });
        mThread.start();
    }

    public void stopDownLoad() {
        isStopped = true;
    }

    public void setOnProgressListener(ServiceActivity.OnProgressListener onProgressListener){
        mOnProgressListenner = onProgressListener;
    }


    /**
     * 返回一个Binder对象
     */
    @Override
    public IBinder onBind(Intent intent) {
        return new MsgBinder();
    }

    public class MsgBinder extends Binder {
        /**
         * 获取当前Service的实例
         *
         * @return
         */
        public DownloadService getService() {
            return DownloadService.this;
        }
    }
}
