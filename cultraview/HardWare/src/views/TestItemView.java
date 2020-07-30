package views;

import com.cultraview.hardware.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TestItemView extends RelativeLayout {

    private Context context;

    private View inflate;

    private TextView name;

    private ImageView icon;

    private TextView result;

    public TestItemView(Context context) {
        super(context);
        this.context = context;
        inflateView();
        initView();
    }

    public TestItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflateView();
        initView();
    }

    private void inflateView() {
        inflate = LayoutInflater.from(context).inflate(R.layout.test_item_view, this);
    }

    private void initView() {
        name = (TextView) inflate.findViewById(R.id.tv_device_name);
        icon = (ImageView) inflate.findViewById(R.id.iv_device);
        result = (TextView) inflate.findViewById(R.id.tv_result);
    }

    public void setName(int resid) {
        name.setText(resid);
    }

    public void setIcon(int id) {
        icon.setImageResource(id);
    }

    public void setResult(int resid) {
        result.setText(resid);
    }

}
