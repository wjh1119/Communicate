package cn.wjh1119.communicate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.thread_activity)
    Button threadButton;

    @BindView(R.id.service_activity)
    Button serviceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        threadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(getClass().getSimpleName(),"start ThreadActivity");
                Intent threadIntent = new Intent(MainActivity.this, ThreadActivity.class);
                startActivity(threadIntent);
            }
        });

        serviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(getClass().getSimpleName(),"start ServiceActivity");
                Intent serviceIntent = new Intent(MainActivity.this, ServiceActivity.class);
                startActivity(serviceIntent);
            }
        });
    }
}