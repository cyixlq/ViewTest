package com.example.viewtest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.viewtest.core.ObserveData;
import com.example.viewtest.core.ValueChangeListener;
import com.example.viewtest.widget.RefreshLayout;

public class MainActivity extends AppCompatActivity {

    private int x = 0;
    private TextView textView;
    private Button button;
    private RefreshLayout refreshLayout;
    private ObserveData<String> text = new ObserveData<>("en");
    private TextView contentTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        text.setValueChangeListener(new ValueChangeListener<String>() {
            @Override
            public void changed(String oldValue, String newValue) {
                textView.setText(newValue);
            }
        });
    }

    private void initView() {
        textView = findViewById(R.id.txt);
        contentTv = findViewById(R.id.txt_content);
        contentTv.setText("a\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\n");
        button = findViewById(R.id.btn);
        refreshLayout = findViewById(R.id.refresh_layout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setValue("en" + x++);
            }
        });
        RefreshLayout.RefreshStateListender listender = new RefreshLayout.RefreshStateListender() {
            @Override
            public void onReadyRefresh(View headerView) {

            }

            @Override
            public void onCanRefresh(View headerView) {

            }

            @Override
            public void onRefresh(View headerView) {
                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (refreshLayout != null) {
                            text.setValue("en");
                            x = 0;
                            refreshLayout.refreshFinish(true);
                        }
                    }
                }.sendEmptyMessageDelayed(0, 2000);
            }

            @Override
            public void onFinish(View headerView, boolean isSuccess) {

            }
        };
        refreshLayout.setRefreshStateListener(listender);
        textView.setText(text.getValue());
    }
}
