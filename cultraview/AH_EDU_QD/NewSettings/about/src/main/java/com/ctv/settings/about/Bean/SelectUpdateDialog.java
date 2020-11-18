package com.ctv.settings.about.Bean;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.ctv.settings.about.R;
import com.ctv.settings.about.activity.LocalUpdateActivity;

public class SelectUpdateDialog extends Dialog  implements View.OnFocusChangeListener, View.OnClickListener {
    private static final String TAG = "SelectUpdateDialog";
    private final LocalUpdateActivity localUpdateActivity;
    private Button local_update_sele_btn;
    private ListView local_update_sele_lv;

    public SelectUpdateDialog(LocalUpdateActivity localUpdateActivity) {
        super(localUpdateActivity);
        this.localUpdateActivity = localUpdateActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_update_select);
    }
    private void setWindowStyle() {
        Window w = getWindow();
        Resources res = localUpdateActivity.getResources();
        Drawable drab = res.getDrawable(R.drawable.transparency_bg);
        w.setBackgroundDrawable(drab);
        WindowManager.LayoutParams lp = w.getAttributes();
        final float scale = res.getDisplayMetrics().density;
        // In the mid-point to calculate the offset x and y
        lp.y = (int) (-36 * scale + 0.5f);
        lp.width = (int) (680 * scale + 0.5f);
        lp.height = (int) (408 * scale + 0.5f);
        w.setAttributes(lp);
    }
    private void findViews() {
        local_update_sele_btn = (Button) findViewById(R.id.local_update_sele_btn);
        local_update_sele_btn.setOnFocusChangeListener(this);
        local_update_sele_btn.setOnClickListener(this);
        local_update_sele_lv = (ListView) findViewById(R.id.local_update_sele_lv);
        SimpleAdapter simpleAdapter = new SimpleAdapter(localUpdateActivity,

                localUpdateActivity.mMountedVolumes, R.layout.local_update_select_item,
                new String[] {
                        "volume_lable"
                }, new int[] {
                R.id.updatezip_list_item
        });
        local_update_sele_lv.setAdapter(simpleAdapter);
        Log.i(TAG, "-----mMountedVolumes:" + localUpdateActivity.mMountedVolumes.toString());
        local_update_sele_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= localUpdateActivity.mScanFiles.size()) {
                    localUpdateActivity.mUpdateFile = localUpdateActivity.mScanFiles
                            .get(localUpdateActivity.mScanFiles.size() - 1);
                } else if (position > 0) {
                    localUpdateActivity.mUpdateFile = localUpdateActivity.mScanFiles
                            .get(position - 1);
                } else {
                    localUpdateActivity.mUpdateFile = localUpdateActivity.mScanFiles.get(position);
                }
                dismiss();
                localUpdateActivity.myHandler.sendEmptyMessage(5);
            }
        });
    }
    @Override
    public void onClick(View view) {

    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }
}
