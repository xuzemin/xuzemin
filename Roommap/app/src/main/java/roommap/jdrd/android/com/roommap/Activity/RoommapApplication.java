package roommap.jdrd.android.com.roommap.Activity;

import android.app.Application;
import android.widget.Toast;

import com.palmaplus.nagrand.core.Engine;

import roommap.jdrd.android.com.roommap.util.Constant;
import roommap.jdrd.android.com.roommap.util.FileUtilsTools;

/**
 * Created by Administrator on 2016/9/2.
 */
public class RoommapApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        if (FileUtilsTools.checkoutSDCard()) {
            FileUtilsTools.copyDirToSDCardFromAsserts(this, Constant.LUA_NAME, "font");
            FileUtilsTools.copyDirToSDCardFromAsserts(this, Constant.LUA_NAME, "Nagrand/lua");
        } else {
            Toast.makeText(this, "未找到SDCard", Toast.LENGTH_LONG).show();
        }
        Engine engine = Engine.getInstance(); //初始化引擎
        engine.startWithLicense(Constant.APP_KEY, this); //设置验证lincense，可以通过开发者平台去查找自己的lincense
    }






}
