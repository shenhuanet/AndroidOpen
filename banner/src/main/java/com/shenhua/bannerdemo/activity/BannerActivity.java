package com.shenhua.bannerdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.shenhua.bannerdemo.R;
import com.shenhua.libs.bannerview.BannerView;

public class BannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        BannerView banner = (BannerView) findViewById(R.id.banner);
        String[] images = {
                "http://img1.imgtn.bdimg.com/it/u=750559018,929845401&fm=206&gp=0.jpg",
                "http://img2.3lian.com/2014/f6/102/d/51.jpg",
                "http://img4q.duitang.com/uploads/item/201504/08/20150408H0245_HTGh3.thumb.700_0.jpeg",
                "http://img2.3lian.com/2014/f5/64/d/83.jpg",
                "http://img5.imgtn.bdimg.com/it/u=2071825957,3967640546&fm=21&gp=0.jpg"};
        String[] titles = {
                "最美是牵着你的手一路狂奔",
                "总要笑到眼泪要掉下来，还越笑越大声、才懂成熟",
                "立正.. 稍息.. 趴开！",
                "",
                "幸福是快乐的起点 伸出一双手，搭起一座桥梁 绽放一个笑容，把春天留在身边..."
        };
        assert banner != null;
        banner.setBannerStyle(BannerView.BannerViewConfig.CIRCLE_INDICATOR_TITLE_HORIZONTAL);
        banner.setBannerTitleArray(titles);
        banner.setImageArray(images);
        banner.setOnBannerClickListener(new BannerView.OnBannerItemClickListener() {
            @Override
            public void OnBannerClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "你点击了：" + position, Toast.LENGTH_LONG).show();
            }
        });
    }

}
