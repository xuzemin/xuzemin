package roommap.jdrd.android.com.roommap.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.palmaplus.nagrand.view.overlay.OverlayCell;

import roommap.jdrd.android.com.roommap.R;

/**
 * Created by Administrator on 2016/9/2.
 */
public class Mark extends LinearLayout implements OverlayCell {
    private ImageView mIconView;
    private TextView mPosX;
    private TextView mPosY;
    private TextView mPosId;

    private double[] mGeoCoordinate;
    private int mId;

    public Mark(Context context) {
        super(context);

        initView();
    }

    public Mark(Context context,int id) {
        super(context);

        this.mId = id;
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_item, this);
        mIconView = (ImageView) findViewById(R.id.mark_icon);
        mPosX = (TextView) findViewById(R.id.mark_x);
        mPosY = (TextView) findViewById(R.id.mark_y);
        mPosId = (TextView) findViewById(R.id.mark_id);
        mPosId.setText(String.valueOf(mId));
    }

    public void setMark(int id, double x, double y){
        mId = id;
        mPosId.setText(String.valueOf(id));
        mPosX.setText("x: " + x);
        mPosY.setText("y: " + y);
    }

    public void setMark(int id, double x, double y, int resId) {
        mId = id;
        mPosId.setText(String.valueOf(id));
        mPosX.setText("x: " + x);
        mPosY.setText("y: " + y);
        mIconView.setBackgroundResource(resId);
    }

    @Override
    public void init(double[] doubles) { // 用于接受一个世界坐标，必须要有
        mGeoCoordinate = doubles;
    }

    @Override
    public double[] getGeoCoordinate() { // 用于返回世界坐标，必须要有
        return mGeoCoordinate;
    }


    //用于定位覆盖物位置，这个接口会由SDK调用，
//最终参数是覆盖物添加世界坐标转换后的屏幕坐标，
//这个接口在做地图交互是会一直调用，如果你想自己控制覆盖物的显示位置，可以自己自定义这个接口
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void position(double[] doubles) {     setX((float) doubles[0] - getWidth() / 2);
        setY((float) doubles[1] - getHeight() / 2);
    }

}
