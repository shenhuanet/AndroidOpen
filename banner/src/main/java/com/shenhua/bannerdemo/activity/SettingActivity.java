package com.shenhua.bannerdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.shenhua.bannerdemo.R;

/**
 * 展示设置界面的可阻尼拖动的activity
 * Created by shenhua on 8/29/2016.
 */
public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }
}
