package cn.wjh1119.communicate;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ThreadActivity extends ActionBarActivity {

    private static final int MSG_HANDLER = 1;

    @BindView(R.id.button1)
    Button button1;

    @BindView(R.id.button2)
    Button button2;

    @BindView(R.id.button3)
    Button button3;

    @BindView(R.id.button4)
    Button button4;

    @BindView(R.id.main_text)
    TextView textView;

    private MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        ButterKnife.bind(this);

        //button1，点击创建工作者线程，并用Hanlder与UI线程通信
        myHandler = new MyHandler(this);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyThread handlerThread = new MyThread("handler");
                handlerThread.start();
            }
        });

        //button3，点击创建工作者线程，并用Eventbus与UI线程通信
        EventBus.getDefault().register(this);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyThread handlerThread = new MyThread("eventbus");
                handlerThread.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销EventBus
        EventBus.getDefault().unregister(this);
    }

    //接收eventbus消息，并在UI线程执行
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        textView.append("by eventbus msg: " + event.getMsg() + "\n");
    }


    //静态MyHandler类，使用若应用避免内存溢出
    private static class MyHandler extends Handler {

        private WeakReference<ThreadActivity> mActivityWR;

        MyHandler(ThreadActivity activity) {
            mActivityWR = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final ThreadActivity activity = mActivityWR.get();
            switch (msg.what) {
                case MSG_HANDLER:
                    activity.textView.append("by handler \n");
                    break;
            }
        }
    }

    private class MyThread extends Thread {

        private String mMode;

        MyThread(String mode) {
            mMode = mode;
        }

        @Override
        public void run() {
            switch (mMode) {
                case "handler":
                    myHandler.obtainMessage(MSG_HANDLER).sendToTarget();
                    break;
                case "eventbus":
                    EventBus.getDefault().post(new MessageEvent("eventbus"));
                    break;
            }
        }
    }

    private class MessageEvent {

        private String mMsg;

        MessageEvent(String msg) {
            mMsg = msg;
        }

        String getMsg() {
            return mMsg;
        }
    }
}