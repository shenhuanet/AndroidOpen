# BannerDemo
[ ![jCenter](https://img.shields.io/badge/version-1.0.0-yellowgreen.svg) ](https://dl.bintray.com/shenhuanetos/maven/com/shenhua/libs/bannerview/1.0/)
[![Build Status](https://img.shields.io/travis/rust-lang/rust/master.svg)](https://bintray.com/shenhuanetos/maven/bannnerView)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

An android AD image banner. Supports unlimited cycle and a variety of topics, the flexibility to set the rotation style, time, location, image loading frame, etc.!
Text can be in the form of ticker
## Screenshot:
![](https://github.com/shenhuanet/AndroidDemo/blob/master/bannder/banner.gif)

### how to use:
```
dependencies {
    compile 'com.shenhua.libs:bannnerview:1.0'
}
```

``` xml
<com.shenhua.libs.bannerview.BannerView
        android:id="@+id/banner"
        android:layout_width="match_parent"
        android:layout_height="200dp" />
```

``` java
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
```

## 关于作者
博客：http://blog.csdn.net/klxh2009<br>
简书：http://www.jianshu.com/u/12a81897d5bc

## License

    Copyright 2017 ShenhuaNet

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.