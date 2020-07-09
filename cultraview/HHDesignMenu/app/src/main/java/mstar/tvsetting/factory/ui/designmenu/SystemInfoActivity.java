package mstar.tvsetting.factory.ui.designmenu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * action com.hht.action.FIRMWARE_VERSION 启动
 */
public class SystemInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this,DesignMenuActivity.class);
        intent.putExtra("data","swVersion");
        startActivity(intent);
        finish();
    }
}
