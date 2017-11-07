package com.shenhua.pulldownfilterdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private FilterView mFilterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mFilterView = (FilterView) findViewById(R.id.filterView);
        /**
         * 为filterView设置数据, 数据可以作为参数传递过去
         * 如 mFilterView.setFilterAdapter( <T> data );
         */
        mFilterView.setFilterAdapter();
        mFilterView.setOnFilterItemClickListener(new FilterView.OnFilterItemClickListener() {
            @Override
            public void onFilterItemClick(int pos) {
                Toast.makeText(MainActivity.this, "你点击了:" + pos, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mFilterView.isShowing())
            mFilterView.hide();
        else
            super.onBackPressed();
    }
}
