package com.mphotool.whiteboard.view.menuviews.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mphotool.whiteboard.BuildConfig;
import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.activity.WhiteBoardApplication;
import com.mphotool.whiteboard.entity.Event;
import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.utils.PanelUtils;
import com.mphotool.whiteboard.utils.SharedPreferencesUtils;
import com.mphotool.whiteboard.utils.Utils;
import com.mphotool.whiteboard.view.PanelManager;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class SettingDialog extends Dialog {
    private static final String TAG = "SettingDialog";
    private Context mContext;
    private CheckBox mBtnAutoErase;
    private TextView mBtnCheckUpdate, mTvVersion;

    private RelativeLayout mEraseValueLayout;
//    private RelativeLayout mWriteModeRL;
//    private RelativeLayout mSetMultiRL;
    private RelativeLayout mAuteEraseRL;
    private EditText mEraseValueEdit;
    private TextView mBtnValueSet;
    private ImageView mBtnMulti;
    private ImageView mWriteMode;
    private ImageView m4KMode;
    boolean mEVLDisplay = false;

    public SettingDialog(@NonNull Context context)
    {
        super(context, R.style.XDialog);
        mContext = context;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_dialog);
        mBtnCheckUpdate = (TextView) findViewById(R.id.btn_check_update);
        mTvVersion = (TextView) findViewById(R.id.current_version);

        mBtnMulti = (ImageView) findViewById(R.id.im_set_multi);

        mBtnAutoErase = (CheckBox) findViewById(R.id.check_box);

        mEraseValueLayout = (RelativeLayout) findViewById(R.id.erase_value_layout);
/*        mWriteModeRL = (RelativeLayout) findViewById(R.id.write_mode_rl);
        mWriteModeRL.setVisibility(View.GONE);*/

/*        mSetMultiRL = (RelativeLayout) findViewById(R.id.set_multi_rl);
        mSetMultiRL.setVisibility(View.GONE);*/

        mAuteEraseRL = (RelativeLayout) findViewById(R.id.aute_erase_rl);


        m4KMode = (ImageView) findViewById(R.id.im_4k2k_mode);

        mWriteMode = (ImageView) findViewById(R.id.im_write_mode);
        int resId = PanelUtils.isWriteMode() ? R.drawable.checkbox_selected : R.drawable.checkbox_normal;
        mWriteMode.setImageResource(resId);

        mAuteEraseRL.setVisibility(View.VISIBLE);

        mEraseValueEdit = (EditText) findViewById(R.id.erase_value_edit);
        mBtnValueSet = (TextView) findViewById(R.id.btn_value_edit);

        mTvVersion.setText(" " + BuildConfig.VERSION_NAME);
        showMultiImage();

        resId = Constants.is4K ? R.drawable.checkbox_selected : R.drawable.checkbox_normal;
        m4KMode.setImageResource(resId);
        m4KMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean m4K = false;
                m4K = (Utils.getSystemProperties("persist.sys.ctv.4k2k", "false")).equals("true")?true:false;
                if(m4K) {
                    Utils.setSystemProperties("persist.sys.ctv.4k2k", "false");
                    m4KMode.setImageResource(R.drawable.checkbox_normal);
                }else {
                    Utils.setSystemProperties("persist.sys.ctv.4k2k", "true");
                    m4KMode.setImageResource(R.drawable.checkbox_selected);
                }
                Toast.makeText(mContext,mContext.getString(R.string.reboot_msg), Toast.LENGTH_SHORT).show();
                mHandler.removeMessages(SYSTEM_REBOOT_MSG);
                mHandler.sendEmptyMessageDelayed(SYSTEM_REBOOT_MSG,3000);
            }
        });

       /* mWriteMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean mWrite =  (boolean)SharedPreferencesUtils.getParam(Constants.WRITE_PEN, Constants.DEFAULT_ISWRITE);
                PanelUtils.setWriteSwitch(!mWrite);
                SharedPreferencesUtils.setParam(Constants.WRITE_PEN, PanelUtils.isWriteMode());

                int resId = PanelUtils.isWriteMode() ? R.drawable.checkbox_selected : R.drawable.checkbox_normal;
                mWriteMode.setImageResource(resId);
                if (PanelUtils.isWriteMode())
                {
                    mSetMultiRL.setVisibility(View.GONE);
                    mAuteEraseRL.setVisibility(View.GONE);
                    mEraseValueLayout.setVisibility(View.GONE);
                }
                else
                {
                    mSetMultiRL.setVisibility(View.VISIBLE);
                    mAuteEraseRL.setVisibility(View.VISIBLE);
                    boolean autoErase = (boolean) SharedPreferencesUtils.getParam("isAutoErase", true);
                    if (autoErase)
                    {
                        mEraseValueLayout.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        mEraseValueLayout.setVisibility(View.GONE);
                    }
                }
            }
        });*/

        mBtnAutoErase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                SharedPreferencesUtils.setParam(mContext, "isAutoErase", isChecked);
                BaseUtils.dbg(TAG,"--isChecked--" + (isChecked?"true":"false"));
                WhiteBoardApplication.isAutoEnterEraseMode = isChecked;
                if (isChecked) {
                    if(mEVLDisplay)
                        mEraseValueLayout.setVisibility(View.VISIBLE);
                    else
                        mEraseValueLayout.setVisibility(View.GONE);
                } else {
                    mEraseValueLayout.setVisibility(View.GONE);
                }
            }
        });
        String sensitivity = Utils.getSystemProperties(Constants.ERASE_WIDTH_ID, Constants.MIN_WIDTH_ACTIVATE_ERASER_DEFAULT);
        Constants.MIN_WIDTH_ACTIVATE_ERASER = Float.parseFloat(sensitivity);

        mEraseValueEdit.setText(sensitivity + "");

        mBtnValueSet.setText(mContext.getText(R.string.erase_value_edit));
        mEraseValueEdit.setEnabled(false);
        mEraseValueEdit.setTextColor(Color.GRAY);


        mBtnValueSet.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v)
            {
                if (mBtnValueSet.getText().toString().equals(mContext.getText(R.string.erase_value_edit)))
                {
                    mBtnValueSet.setText(mContext.getText(R.string.erase_value_set));
                    mEraseValueEdit.setEnabled(true);
                    mEraseValueEdit.setTextColor(Color.WHITE);
                }
                else if (mBtnValueSet.getText().toString().equals(mContext.getText(R.string.erase_value_set)))
                {
                    mBtnValueSet.setText(mContext.getText(R.string.erase_value_edit));
                    mEraseValueEdit.setEnabled(false);
                    mEraseValueEdit.setTextColor(Color.GRAY);

                    String valueStr = mEraseValueEdit.getText().toString();
                    if(TextUtils.isEmpty(valueStr)){
                        valueStr = Constants.MIN_WIDTH_ACTIVATE_ERASER + "";
                        mEraseValueEdit.setText(Constants.MIN_WIDTH_ACTIVATE_ERASER + "");
                    }
                    float value = Float.valueOf(valueStr);
                    if(value < 3.0f){
                        value = 3.0f;
                    }else if(value > 20.0f){
                        value = 20.0f;
                    }
                    Constants.MIN_WIDTH_ACTIVATE_ERASER = value;
                    mEraseValueEdit.setText(Constants.MIN_WIDTH_ACTIVATE_ERASER + "");
                    Utils.setSystemProperties(Constants.ERASE_WIDTH_ID, Float.toString(value));
//                    SharedPreferencesUtils.setParam(Constants.ERASE_WIDTH_SIZE, Constants.MIN_WIDTH_ACTIVATE_ERASER);
                }
            }
        });

        mBtnMulti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置多点触控模式
                boolean mMultiPen = (boolean) SharedPreferencesUtils.getParam(Constants.IS_MULTI_PEN, true);
                mMultiPen = !mMultiPen;
                SharedPreferencesUtils.setParam(Constants.IS_MULTI_PEN, mMultiPen);

                int resId = mMultiPen ? R.drawable.checkbox_selected : R.drawable.checkbox_normal;
                mBtnMulti.setImageResource(resId);

//                EventBus.getDefault().post(new Event.ChangeMultiPenEvent());
                PanelUtils.setMultiPen(mMultiPen);
            }
        });
    }

    public void setCheckUpdateListener(View.OnClickListener listener)
    {
        if (listener != null && mBtnCheckUpdate != null)
        {
            mBtnCheckUpdate.setOnClickListener(listener);
            mTvVersion.setOnClickListener(listener);
        }
    }

    private void showMultiImage()
    {
        if (mBtnMulti != null)
        {
            boolean mMultiPen = (boolean) SharedPreferencesUtils.getParam(Constants.IS_MULTI_PEN, true);
            int resId = mMultiPen ? R.drawable.checkbox_selected : R.drawable.checkbox_normal;
            mBtnMulti.setImageResource(resId);
        }

        mEVLDisplay = Utils.getSystemProperties(Constants.HAVE_ERASE, "false").equals("false") ? true : false;
        boolean autoErase = (boolean) SharedPreferencesUtils.getParam("isAutoErase", true);
        mBtnAutoErase.setChecked(autoErase);
        if (autoErase) {
            if(mEVLDisplay)
                mEraseValueLayout.setVisibility(View.VISIBLE);
            else
                mEraseValueLayout.setVisibility(View.GONE);
        } else {
            mEraseValueLayout.setVisibility(View.GONE);
        }
    }

    @Override public void show()
    {
        super.show();
        BaseUtils.hideNavigationBar(this);
        showMultiImage();
    }

    @Override public void dismiss()
    {
        super.dismiss();
    }

    private void RootSystem() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"reboot"});
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,mContext.getString(R.string.failed), Toast.LENGTH_SHORT).show();
        }
    }

    private static final int SYSTEM_REBOOT_MSG = 0x01;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SYSTEM_REBOOT_MSG:
                    RootSystem();
                    break;
                default:
                    break;
            }
        }
    };
}
