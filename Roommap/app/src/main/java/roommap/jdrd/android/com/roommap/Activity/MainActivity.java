package roommap.jdrd.android.com.roommap.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.palmaplus.nagrand.core.Types;
import com.palmaplus.nagrand.data.DataList;
import com.palmaplus.nagrand.data.DataSource;
import com.palmaplus.nagrand.data.Feature;
import com.palmaplus.nagrand.data.FeatureCollection;
import com.palmaplus.nagrand.data.FloorModel;
import com.palmaplus.nagrand.data.LocationList;
import com.palmaplus.nagrand.data.MapModel;
import com.palmaplus.nagrand.data.Param;
import com.palmaplus.nagrand.data.PlanarGraph;
import com.palmaplus.nagrand.geos.Coordinate;
import com.palmaplus.nagrand.navigate.NavigateManager;
import com.palmaplus.nagrand.view.MapOptions;
import com.palmaplus.nagrand.view.MapView;
import com.palmaplus.nagrand.view.gestures.OnSingleTapListener;
import com.palmaplus.nagrand.view.gestures.OnZoomListener;
import com.palmaplus.nagrand.view.layer.FeatureLayer;

import roommap.jdrd.android.com.roommap.R;
import roommap.jdrd.android.com.roommap.util.Mark;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "MainActivity";
    private MapView mapView;
    private DataSource dataSource;
    private MapOptions mMapOptions = new MapOptions();
    private LinearLayout mOverlayContainer;
    private NavigateManager navigateManager;
    private FeatureLayer navigateLayer;
    private long toId,startId=0;
    private double startX = 0,startY = 0,toX,toY;
    private boolean isNavigating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataSource = new DataSource("http://api.ipalmap.com/");
        mapView = (MapView)findViewById(R.id.mapview);
        navigateManager = new NavigateManager();
        navigateLayer = new FeatureLayer("navigate");
        mapView.addLayer(navigateLayer);
        mapView.setLayerOffset(navigateLayer);
        mOverlayContainer = (LinearLayout) findViewById(R.id.mapview_container);
        mapView.setOverlayContainer(mOverlayContainer);

        init();
    }
    public void init(){


        navigateManager.setOnNavigateComplete(new NavigateManager.OnNavigateComplete() {
            @Override
            public void onNavigateComplete(NavigateManager.NavigateState navigateState, FeatureCollection featureCollection) {
                Log.w(TAG, "回调"+navigateState+";");
                if(navigateState.equals("ok")){

                }else{

                }
                navigateLayer.clearFeatures();  //先把之前的导航线清理掉
                navigateLayer.addFeatures(featureCollection); //重新添加新的导航线
            }
        });

        //增加指南针
        mapView.setOnZoomListener(new OnZoomListener() {

            @Override
            public void preZoom(MapView mapView, float v, float v2) {
            }

            @Override
            public void onZoom(MapView mapView, boolean b) {
                //compass是一个带有指南针的ImageView，通过调用地图的getRotate方法来获取地图的旋转角度，
                //并且赋值给指南针的ImageView即可
//                compass.setRotation(-BigDecimal.valueOf(mapView.getRotate()).floatValue());
//                compass.invalidate();
            }

            @Override
            public void postZoom(MapView mapView, float v, float v2) {
            }
        });

        mapView.setOnZoomListener(new OnZoomListener() {

            @Override
            public void preZoom(MapView mapView, float v, float v2) {
                Log.w(TAG, "准备缩放");
            }

            @Override
            public void onZoom(MapView mapView, boolean b) {
                if (b) { // true：放大；false：缩小
                    Log.w(TAG, "放大地图");
                } else {
                    Log.w(TAG, "缩小地图");
                }
            }

            @Override
            public void postZoom(MapView mapView, float v, float v2) {
                Log.w(TAG, "缩放结束");
            }
        });

        mapView.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap(MapView mapView, float x, float y) {
                Types.Point point = mapView.converToWorldCoordinate(x, y); //通过手指点击将屏幕坐标转换世界坐标
                Log.w(TAG, "单击位置： x = " + x + "; y = " + y);
                Feature feature = mapView.selectFeature(x, y);
                if (feature == null){
                    Log.w(TAG, "当前位置在地图之外");
                    return;
                }
                Param<String> name = new Param<String>("name", String.class);
                Param<Long> id = new Param<Long>("id", Long.class);
                String n = name.get(feature);
                Log.w(TAG, "name = " + n + "; id = " + id.get(feature));
                if (startX == 0) {
                    startX = point.x;
                    startY = point.y;
                    startId = id.get(feature);
                    Log.w(TAG,"start");
                } else {
                    toX = point.x;
                    toY = point.y;
                    toId = id.get(feature);
                    isNavigating = true;
                    navigateManager.navigation(startX, startY, startId, toX, toY, toId); // TODO 请求导航线
                    Log.w(TAG,"end");
                    startX = 0;
                }
            }
        });

//        mapView.setOnSingleTapListener(new OnSingleTapListener() {
//            @Override
//            public void onSingleTap(MapView mapView, float x, float y) {
//                Log.w(TAG, "单击位置： x = " + x + "; y = " + y);
//                Feature feature = mapView.selectFeature(x, y);
//                if (feature == null){
//                    Log.w(TAG, "当前位置在地图之外");
//                    return;
//                }
//                Param<String> name = new Param<String>("name", String.class);
//                Param<Long> id = new Param<Long>("id", Long.class);
//                String n = name.get(feature);
//                Log.w(TAG, "name = " + n + "; id = " + id.get(feature));
//                int mNum = 0;
//                Types.Point point = mapView.converToWorldCoordinate(x, y); //讲屏幕坐标转换为事件做坐标
//                Mark mark = new Mark(getApplicationContext()); //创建一个覆盖物
//                mark.setMark(++mNum, x, y);
//                mark.init(new double[]{point.x, point.y}); //把世界坐标传递给他
//                mapView.addOverlay(mark); //将这个覆盖物添加到MapView中
//            }
//        });


        MapOptions mMapOptions = new MapOptions();
        mMapOptions.setSkewEnabled(true);
        mapView.setMapOptions(mMapOptions);
        startload();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.drop();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
        }
    }
    public void startload(){

        dataSource.requestMaps(new DataSource.OnRequestDataEventListener<DataList<MapModel>>() {
            @Override
            public void onRequestDataEvent(DataSource.ResourceState state, DataList<MapModel> data) {
                if (state != DataSource.ResourceState.ok)
                    return;
                if (data.getSize() == 0) //如果列表中的地图数量是0，请去开发者平台添加一些地图
                    return;
                dataSource.requestPOIChildren(MapModel.POI.get(data.getPOI(0)), new DataSource.OnRequestDataEventListener<LocationList>() {
                    @Override
                    public void onRequestDataEvent(DataSource.ResourceState state, LocationList data) {
                        Log.w(TAG,"DataSource.ResourceState = "+state);
                        Log.w(TAG,"data.getSize() = "+data.getSize());
                        if (state != DataSource.ResourceState.ok)
                            return;
                        if (data.getSize() == 0) //如果是0说明这套图没有楼层，请反馈给我们
                            return;
                        dataSource.requestPlanarGraph(
                                297787,
                                new DataSource.OnRequestDataEventListener<PlanarGraph>() { //发起获取一个平面图的请求
                                    @Override
                                    public void onRequestDataEvent(DataSource.ResourceState state, PlanarGraph data) {
                                        Log.w(TAG,"DataSource.ResourceState = "+state);
                                        if (state == DataSource.ResourceState.ok) {

                                            mapView.drawPlanarGraph(data);  //加载平面图
                                            mapView.start(); //开始绘制地图

                                            //添加导航层
                                            navigateLayer = new FeatureLayer("navigate");
                                            mapView.setLayerOffset(navigateLayer);
                                            mapView.addLayer(navigateLayer);

                                            final NavigateManager nm = new NavigateManager();
                                            nm.setOnNavigateComplete(new NavigateManager.OnNavigateComplete() {
                                                @Override
                                                public void onNavigateComplete(NavigateManager.NavigateState state, FeatureCollection featureCollection) {
                                                    navigateLayer.addFeatures(featureCollection); //获取导航线
                                                    navigateLayer.addFeature(nm.getTransitFeature(1003497)); //获取经停点
                                                }
                                            });
                                            Coordinate[] coordinates = new Coordinate[]{new Coordinate(13526590.66, 3663426.49), new Coordinate(13526597.03, 3663427.70)};
                                            long[] ids = new long[]{1003497, 1003497};
                                            nm.navigation(new Coordinate(13526582.000000, 3663422.750000), 1003497, new Coordinate(13526605.000000, 3663424.250000), 1003497, coordinates, ids);
                                        } else {
                                            //error
                                        }
                                    }
                                });
                    }
                });
            }
        });


//        dataSource.requestMaps(new DataSource.OnRequestDataEventListener<DataList<MapModel>>() {
//            @Override
//            public void onRequestDataEvent(DataSource.ResourceState state, DataList<MapModel> data) {
//                if (state != DataSource.ResourceState.ok)
//                    return;
//                if (data.getSize() == 0) //如果列表中的地图数量是0，请去开发者平台添加一些地图
//                    return;
//                //拿到第一个可用Map的POI，请求他的楼层数据
//
//                dataSource.requestPOIChildren(MapModel.POI.get(data.getPOI(0)), new DataSource.OnRequestDataEventListener<LocationList>() {
//                    @Override
//                    public void onRequestDataEvent(DataSource.ResourceState state, LocationList data) {
//                        if (state != DataSource.ResourceState.ok)
//                            return;
//                        if (data.getSize() == 0) //如果是0说明这套图没有楼层，请反馈给我们
//                            return;
//                        //获取楼层的id，展示地图
//                        Log.w(TAG,"FloorModel.id = "+FloorModel.id);
//                        dataSource.requestPlanarGraph(
//                                FloorModel.id.get(data.getPOI(0)), //获取这套图的默认楼层id
//                                new DataSource.OnRequestDataEventListener<PlanarGraph>() { //发起获取一个平面图的请求
//                                    @Override
//                                    public void onRequestDataEvent(DataSource.ResourceState state, PlanarGraph data) {
//                                        if (state == DataSource.ResourceState.ok) {
//                                            mapView.drawPlanarGraph(data);  //加载平面图
//                                            mapView.start(); //开始绘制地图
//                                        } else {
//                                            //error
//                                        }
//                                    }
//                                });
//                    }
//                });
//            }
//        });
    }
}
