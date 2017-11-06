package com.shenhua.baidunav;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.shenhua.baidunav.overlayutil.BikingRouteOverlay;
import com.shenhua.baidunav.overlayutil.DrivingRouteOverlay;
import com.shenhua.baidunav.overlayutil.OverlayManager;
import com.shenhua.baidunav.overlayutil.TransitRouteOverlay;
import com.shenhua.baidunav.overlayutil.WalkingRouteOverlay;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用来展示如何进行地理编码搜索（用地址检索坐标）、反地理编码搜索（用坐标检索地址）
 */
public class GeoCodeActivity extends AppCompatActivity implements OnGetGeoCoderResultListener, BaiduMap.OnMapClickListener, OnGetRoutePlanResultListener {

    private static final String TAG = "GeoCodeActivity";
    private int flag = 0;// 标志，0为商家定位，1为路线规划
    private String mEndCity, mEndAddr, mStartCity = "杭州", loca;
    //    private SparseBooleanArray sb = new SparseBooleanArray();
    // 商家定位使用
    private GeoCoder mGeoCoderSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private BaiduMap mBaiduMap = null;
    // 路线规划使用
    private int nodeIndex = -1;// 节点索引，供浏览节点时使用
    private RouteLine route = null;// 路线
    private OverlayManager routeOverlay = null;
    private boolean useDefaultIcon = false;// 是否使用默认图标
    private RoutePlanSearch mRouteSearch = null;// 搜索模块，也可去掉地图模块独立使用
    private PlanNode stNode;// 起点
    private PlanNode enNode;// 终点

    @BindView(R.id.bmapView)
    MapView mMapView;
    @BindView(R.id.fabgo)
    FloatingActionButton mFabGo;
    //    @Bind(R.id.viewStub_mode)
    LinearLayout mViewStubMode;// 四种路线方式view
    //    @Bind(R.id.btn_drive)
    Button mDriveBtn;// 驾车
    //    @Bind(R.id.btn_transit)
    Button mTransitBtn;// 公交
    //    @Bind(R.id.btn_walk)
    Button mWalkBtn;// 步行
    //    @Bind(R.id.btn_bike)
    Button mBikeBtn;// 自行车
    LinearLayout mViewStubPanel;// 节点view
    Button mBtnPre; // 上一个节点
    Button mBtnNext; // 下一个节点

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geocode);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mEndCity = intent.getStringExtra("mEndCity");
        mEndAddr = intent.getStringExtra("mEndAddr");
        loca = intent.getStringExtra("myLocation");
        System.out.println(loca);

        stNode = PlanNode.withCityNameAndPlaceName(mStartCity, loca);
        enNode = PlanNode.withCityNameAndPlaceName(mEndCity, mEndAddr);

        initModeView();
        initMap();
    }

    private void initMap() {
        flag = 0;
        // 地图初始化
        mBaiduMap = mMapView.getMap();
        // 初始化搜索模块，注册事件监听
        mGeoCoderSearch = GeoCoder.newInstance();
        mGeoCoderSearch.setOnGetGeoCodeResultListener(this);
        // Geo搜索
        mGeoCoderSearch.geocode(new GeoCodeOption().city(mEndCity).address(mEndAddr));
        DialogUtils.showLoadDialog(this, "正在查找商家位置...");
    }

    private void initRoutePlan() {
        flag = 1;
        CharSequence title = "推荐路线";
        setTitle(title);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapClickListener(this);
        mRouteSearch = RoutePlanSearch.newInstance();
        mRouteSearch.setOnGetRoutePlanResultListener(this);
    }

    private void initModeView() {
        mViewStubMode = (LinearLayout) findViewById(R.id.viewStub_mode);
        mViewStubPanel = (LinearLayout) findViewById(R.id.viewStub_panel);
        mDriveBtn = (Button) findViewById(R.id.btn_drive);
        mTransitBtn = (Button) findViewById(R.id.btn_transit);
        mWalkBtn = (Button) findViewById(R.id.btn_walk);
        mBikeBtn = (Button) findViewById(R.id.btn_bike);
        mDriveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMap.clear();
                mRouteSearch.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(enNode));
                refreshModeDisplay(0);
            }
        });
        mTransitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMap.clear();
                mRouteSearch.transitSearch(new TransitRoutePlanOption().from(stNode).city(mStartCity).to(enNode));
                refreshModeDisplay(1);
            }
        });
        mWalkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMap.clear();
                mRouteSearch.walkingSearch(new WalkingRoutePlanOption().from(stNode).to(enNode));
                refreshModeDisplay(2);
            }
        });
        mBikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMap.clear();
                mRouteSearch.bikingSearch(new BikingRoutePlanOption().from(stNode).to(enNode));
                refreshModeDisplay(3);
            }
        });
        mBtnPre = (Button) findViewById(R.id.btn_pre);
        mBtnNext = (Button) findViewById(R.id.btn_next);
        mBtnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nodeIndex > 0) {
                    nodeIndex--;
                    doGetMode();
                }

            }
        });
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nodeIndex < route.getAllStep().size() - 1) {
                    nodeIndex++;
                    doGetMode();
                }
            }
        });
    }

    /**
     * 获取节结果信息
     */
    private void doGetMode() {
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = route.getAllStep().get(nodeIndex);
        if (step instanceof DrivingRouteLine.DrivingStep) {
            nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
            nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
        } else if (step instanceof WalkingRouteLine.WalkingStep) {
            nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
            nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
        } else if (step instanceof TransitRouteLine.TransitStep) {
            nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
            nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
        } else if (step instanceof BikingRouteLine.BikingStep) {
            nodeLocation = ((BikingRouteLine.BikingStep) step).getEntrance().getLocation();
            nodeTitle = ((BikingRouteLine.BikingStep) step).getInstructions();
        }
        if (nodeLocation == null || nodeTitle == null) {
            return;
        }
        // 移动节点至中心
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        TextView popupText = new TextView(this);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);
        mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));
    }

    @OnClick(R.id.fabgo)
    public void click(View view) {
        if (flag == 0) {
            mFabGo.setImageResource(R.drawable.ic_back);
            initRoutePlan();
            mBaiduMap.clear();
            mViewStubMode.setVisibility(View.VISIBLE);
            mViewStubPanel.setVisibility(View.VISIBLE);
            mRouteSearch.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(enNode));
            refreshModeDisplay(0);
        } else {
            mFabGo.setImageResource(R.drawable.ic_go);
            initMap();
            mViewStubMode.setVisibility(View.GONE);
            mViewStubPanel.setVisibility(View.GONE);
        }
    }

    private void refreshModeDisplay(int key) {
        mDriveBtn.setSelected(false);
        mTransitBtn.setSelected(false);
        mWalkBtn.setSelected(false);
        mBikeBtn.setSelected(false);
        switch (key) {
            case 0:
                mDriveBtn.setSelected(true);
                break;
            case 1:
                mTransitBtn.setSelected(true);
                break;
            case 2:
                mWalkBtn.setSelected(true);
                break;
            case 3:
                mBikeBtn.setSelected(true);
                break;
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            DialogUtils.dissmissLoadDialog();
            Toast.makeText(GeoCodeActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            return;
        }
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(15).build()));
        String strInfo = String.format("查找成功!\n纬度：%f 经度：%f", result.getLocation().latitude, result.getLocation().longitude);
        DialogUtils.dissmissLoadDialog();
        Toast.makeText(GeoCodeActivity.this, strInfo, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(GeoCodeActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            return;
        }
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(15).build()));
        Toast.makeText(GeoCodeActivity.this, result.getAddress(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    /**
     * 步行路线
     *
     * @param walkingRouteResult
     */
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
        if (walkingRouteResult == null || walkingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (walkingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // walkingRouteResult.getSuggestAddrInfo()
            return;
        }
        if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            route = walkingRouteResult.getRouteLines().get(0);
            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(walkingRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    /**
     * 公交路线
     *
     * @param transitRouteResult
     */
    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
        if (transitRouteResult == null || transitRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果！", Toast.LENGTH_SHORT).show();
        }
        if (transitRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // transitRouteResult.getSuggestAddrInfo()
            return;
        }
        if (transitRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            route = transitRouteResult.getRouteLines().get(0);
            TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(transitRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    /**
     * 驾车路线
     *
     * @param drivingRouteResult
     */
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未能找到结果！", Toast.LENGTH_SHORT).show();
        }
        if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // drivingRouteResult.getSuggestAddrInfo()
            return;
        }
        if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            route = drivingRouteResult.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
            routeOverlay = overlay;
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(drivingRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    /**
     * 骑车路线
     *
     * @param bikingRouteResult
     */
    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
        if (bikingRouteResult == null || bikingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (bikingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (bikingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            route = bikingRouteResult.getRouteLines().get(0);
            BikingRouteOverlay overlay = new MyBikingRouteOverlay(mBaiduMap);
            routeOverlay = overlay;
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(bikingRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        /**
         * 构造函数
         *
         * @param baiduMap 该DrivingRouteOvelray引用的 BaiduMap
         */
        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon)
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon)
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            return null;
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyBikingRouteOverlay extends BikingRouteOverlay {
        public MyBikingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        mGeoCoderSearch.destroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
