
package com.shenhua.baidunav;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements BDLocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SDKReceiver mReceiver;
    private boolean isOk = false;
    public LocationClient mLocationClient = null;
    private String myLocation = "";
    @BindView(R.id.tv_locate)
    TextView tvLocation;

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        tvLocation.setText("我的位置：" + bdLocation.getAddrStr());
        mLocationClient.stop();
    }

    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            System.out.println(TAG + ":action: " + s);
            switch (s) {
                case SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR:
                    System.out.println("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
                    isOk = false;
                    break;
                case SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK:
                    System.out.println("key 验证成功! 功能可以正常使用");
                    isOk = true;
                    break;
                case SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR:
                    System.out.println("网络出错");
                    isOk = false;
                    break;
            }
        }
    }

    @OnClick({R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4})
    public void clicks(View v) {
        String city = "", addr = "";
        switch (v.getId()) {
            case R.id.tv1:
                city = "杭州";
                addr = "西湖区青芝坞";
                break;
            case R.id.tv2:
                city = "杭州";
                addr = "宋城";
                break;
            case R.id.tv3:
                city = "杭州";
                addr = "文三路";
                break;
            case R.id.tv4:
                city = "上海";
                addr = "东方明珠";
                break;
        }
        if (!isOk) {
            Toast.makeText(this, "百度地图AppKey认证失败", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(MainActivity.this, GeoCodeActivity.class).putExtra("mEndCity", city)
                .putExtra("mEndAddr", addr).putExtra("myLocation", "滨江区政府"));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(this);
        initLocation();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 默认高精度
        option.setCoorType("bd09ll");// 可选，默认gcj02。返回定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);// 可选，设置是否需要使用gps，默认为false
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消监听 SDK 广播
        unregisterReceiver(mReceiver);
    }
}