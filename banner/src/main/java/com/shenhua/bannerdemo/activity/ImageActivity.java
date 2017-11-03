package com.shenhua.bannerdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shenhua.bannerdemo.R;

/**
 * Created by shenhua on 12/28/2016.
 * Email shenhuanet@126.com
 */
public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Glide.with(this).load("http://1321321.jpg")
                .centerCrop()
                .into((ImageView) findViewById(R.id.iv_01));
    }
}
