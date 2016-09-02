package roommap.jdrd.android.com.roommap.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.palmaplus.nagrand.core.Engine;
import com.palmaplus.nagrand.data.DataList;
import com.palmaplus.nagrand.data.DataSource;
import com.palmaplus.nagrand.data.FloorModel;
import com.palmaplus.nagrand.data.LocationList;
import com.palmaplus.nagrand.data.MapModel;
import com.palmaplus.nagrand.data.PlanarGraph;
import com.palmaplus.nagrand.view.MapView;

import roommap.jdrd.android.com.roommap.util.Constant;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private DataSource dataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Engine engine = Engine.getInstance(); //初始化引擎
        engine.startWithLicense(Constant.APP_KEY, this);

        dataSource = new DataSource("http://api.ipalmap.com/");
        mapView = new MapView("default", this); //初始化MapView
        setContentView(mapView);
    }

    private void drawmap(){
        dataSource.requestMaps(new DataSource.OnRequestDataEventListener<DataList<MapModel>>() {
            @Override
            public void onRequestDataEvent(DataSource.ResourceState state, DataList<MapModel> data) {
                if (state != DataSource.ResourceState.ok)
                    return;
                if (data.getSize() == 0) //如果列表中的地图数量是0，请去开发者平台添加一些地图
                    return;
                //拿到第一个可用Map的POI，请求他的楼层数据
                dataSource.requestPOIChildren(MapModel.POI.get(data.getPOI(0)), new DataSource.OnRequestDataEventListener<LocationList>() {
                    @Override
                    public void onRequestDataEvent(DataSource.ResourceState state, LocationList data) {
                        if (state != DataSource.ResourceState.ok)
                            return;
                        if (data.getSize() == 0) //如果是0说明这套图没有楼层，请反馈给我们
                            return;
                        //获取楼层的id，展示地图
                        dataSource.requestPlanarGraph(
                                FloorModel.id.get(data.getPOI(0)), //获取这套图的默认楼层id
                                new DataSource.OnRequestDataEventListener<PlanarGraph>() { //发起获取一个平面图的请求
                                    @Override
                                    public void onRequestDataEvent(DataSource.ResourceState state, PlanarGraph data) {
                                        if (state == DataSource.ResourceState.ok) {
                                            mapView.drawPlanarGraph(data);  //加载平面图
                                            mapView.start(); //开始绘制地图
                                        } else {
                                            //error
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }
}
