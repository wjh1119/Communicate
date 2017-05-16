package cn.wjh1119.communicate;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServiceActivity extends AppCompatActivity {

    boolean mBound = false;
    private DownloadService mService;
    private int progress = 0;
    private DownloadReceiver mDownloadReceiver;
    private String mMode;

    @BindView(R.id.progressBar_service_download)
    ProgressBar mProgressBar;

    @BindView(R.id.button_service_binder)
    Button mBinderButton;

    @BindView(R.id.button_service_interface)
    Button mInterfaceButton;

    @BindView(R.id.button_service_broadcast)
    Button mBroadcastButton;

    @BindView(R.id.button_service_start)
    Button mStartButton;

    @BindView(R.id.button_service_stop)
    Button mStopButton;

    @BindView(R.id.text_service_show)
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        ButterKnife.bind(this);

        mMode = "binder";
        mTextView.append("mode is binder \n");

        mBinderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMode = "binder";
                mTextView.append("mode changes to binder \n");
            }
        });

        mInterfaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMode = "interface";
                mTextView.append("mode changes to interface \n");
            }
        });

        mBroadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMode = "broadcast";
                mTextView.append("mode changes to broadcast \n");
            }
        });

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceActivity.this, DownloadService.class);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

                if (mMode.equals("broadcast")) {
                    mDownloadReceiver = new DownloadReceiver();
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction("cn.wjh1119.communicate.DOWNLOAD");
                    registerReceiver(mDownloadReceiver, intentFilter);
                }
            }
        });
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound) {
                    mService.stopDownLoad();
                    unbindService(mConnection);
                    mProgressBar.setProgress(0);
                    mBound = false;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDownloadReceiver != null) {
            unregisterReceiver(mDownloadReceiver);
        }
    }

    /**
     * 监听进度，每秒钟获取调用DownloadService的getProgress()方法来获取进度，更新UI
     */
    public void listenProgress() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (progress < DownloadService.MAX_PROGRESS && mBound) {
                    Log.d(getClass().getSimpleName(), "listenprogrogress");
                    progress = mService.getProgress();
                    mProgressBar.setProgress(progress);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownloadService.MsgBinder msgBinder = (DownloadService.MsgBinder) service;
            mService = msgBinder.getService();
            mTextView.append("start download \n");
            mBound = true;

            //通过接口传输数据
            if (mMode.equals("interface")) {
                mService.setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void setProgress(int progress) {
                        mProgressBar.setProgress(progress);
                    }
                });
            }


            //通过调用service的公共方法获取service的属性。
            if (mMode.equals("binder")) {
                listenProgress();
            }

            mService.startDownLoad();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    interface OnProgressListener {
        void setProgress(int progress);
    }

    public class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("progress", 0);
            mProgressBar.setProgress(progress);
        }
    }
}
