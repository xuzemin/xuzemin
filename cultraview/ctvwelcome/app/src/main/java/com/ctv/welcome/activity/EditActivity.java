
package com.ctv.welcome.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctv.welcome.R;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter.OnRecyclerViewItemClickListener;
import com.ctv.welcome.adapter.TextColorAdapter;
import com.ctv.welcome.adapter.TypeFaceAdapter;
import com.ctv.welcome.constant.AddModule;
import com.ctv.welcome.constant.Config;
import com.ctv.welcome.dialog.SavingEditPictureDialog;
import com.ctv.welcome.task.ThreadManager;
import com.ctv.welcome.util.DBUtil;
import com.ctv.welcome.util.FileUtil;
import com.ctv.welcome.util.FileUtils;
import com.ctv.welcome.util.HockUtil;
import com.ctv.welcome.util.LogUtils;
import com.ctv.welcome.util.PopUtils.ClickListener;
import com.ctv.welcome.util.PopUtils.PopBuilder;
import com.ctv.welcome.util.UploadUtil;
import com.ctv.welcome.util.Utils;
import com.ctv.welcome.view.FontSettingContainer;
import com.ctv.welcome.view.RichEditor;
import com.ctv.welcome.view.RichEditor.OnLocationListener;
import com.ctv.welcome.view.RichEditor.OnTextChangeListener;
import com.ctv.welcome.view.customview.ColorProgress;
import com.ctv.welcome.view.customview.QRCode;
import com.ctv.welcome.view.customview.Signature;
import com.ctv.welcome.view.customview.SubsectionProgress;
import com.ctv.welcome.vo.LayoutGravity;
import com.ctv.welcome.vo.PicData;
import com.ctv.welcome.vo.PicDataDao.Properties;
import com.ctv.welcome.vo.RichEditorInfo;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.WhereCondition;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class EditActivity extends Activity implements OnClickListener, OnLongClickListener {
    private static final String TAG = "EditActivity";

    public static Bitmap mCustomSignatureBg;

    public static String mSaveFileName;

    private String URL = "http://120.77.69.2:8080/VIP/upload";

    private ImageView cancel;

    boolean change = false;

    private int[] colorArr = new int[] {
            R.color.Red, R.color.Orange, R.color.Green, R.color.Blue, R.color.Yellow,
            R.color.Blue_dark, R.color.Purple, R.color.Black, R.color.white
    };

    private boolean custom;

    private Drawable[] drawables;

    private ImageView edit;

    private String filePath;

    public int fontsize;

    private int iconFilePath;

    private int imageHeight;

    private int imageWidth;

    public boolean invoke = false;

    private boolean isAddTheme;

    public boolean isCheckedTC = false;

    public boolean isCheckedTP;

    public boolean isCheckedTS;

    private boolean isEdited = false;

    private boolean isFirstEdit = true;

    boolean isFirstLoad = false;

    public boolean isFontColorChange;

    public boolean isFontSizeChange;

    public boolean isFontTypeChange;

    boolean isSigned = false;

    boolean isSigning;

    private ImageView mClear;

    private LinearLayout mCodePicRlt;

    private TextView mCodeText;

    private RecyclerView mColorRecyclerView;

    private EditActivity mContext;

    private String mEditImageUrl;

    private ArrayList<RichEditorInfo> mEditorInfoList;

    private List<RichEditor> mEditorList;

    private ImageView mEraser;

    private final int mFontSetOffset = 20;

    private String mIndustryName;

    private LayoutGravity mLayoutGravity;

    private RelativeLayout mParent;

    private ImageView mPenProperty;

    private LinearLayout mPenPropertyCon;

    private PopupWindow mPoppupwindow;

    private ImageView mPostil;

    private LinearLayout mPostilCon;

    private ColorProgress mProgressColor;

    private SubsectionProgress mProgressText;

    private ImageView mQRCodeBig;

    private ImageView mQrCode;

    private LinearLayout mQrcodeCon;

    private PopupWindow mQrcodePopWindow;

    private LinearLayout mRadioGroupControl;

    private RecyclerView mRecyclerView;

    private RelativeLayout mRichEditorCon;

    private ImageView mSavePostil;

    private boolean mSaveSuccessBtn;

    private SavingEditPictureDialog mSavingEditPicDialog;

    private Signature mSignature;

    private ArrayList mSizeList;

    private ImageButton mTextColor;

    private TextColorAdapter mTextColorAdapter;

    public ImageView mTextSize;

    private FontSettingContainer mToolContainer;

    public ImageView mTypeFace;

    private int mWindowHeight;

    private int mWindowWidth;

    public ImageView mainImage;

    private PopupWindow popFontSetting;

    public String px;

    private ImageView save;

    private boolean saveSuccess = false;

    boolean signStatus = false;

    String[] strs = new String[] {
            "arial", "time_new_roman", "georgia", "song_ti", "hei_ti", "fang_song", "kai_ti"
    };

    final String[] textSizeStr = new String[] {
            "40", "60", "80", "100", "120", "140", "160", "180"
    };

    private TextView txtShowSignature;

    private TypeFaceAdapter typeFaceAdapter;

    private String[] typeFaceArr;

    private boolean type_string;

    class RichEditorLocationListener implements OnLocationListener {
        private int position;

        public RichEditorLocationListener(int position) {
            this.position = position;
        }

        public void onLocation(float x, float y) {
            ((RichEditorInfo) mEditorInfoList.get(this.position)).setX(x);
            ((RichEditorInfo) mEditorInfoList.get(this.position)).setY(y);
            showFontSetting(
                    (View) mEditorList.get(this.position), 0, 20);
        }
    }

    class RichEditorTextChangeListener implements OnTextChangeListener {
        private int position;

        private RichEditor richEditor;

        public RichEditorTextChangeListener(RichEditor richEditor, int position) {
            this.richEditor = richEditor;
            this.position = position;
        }

        public void onTextChange(String text) {
            isEdited = true;
            String htmlText = text;
            RichEditorInfo dstInfo = (RichEditorInfo) mEditorInfoList
                    .get(position);
            LogUtils.i(EditActivity.TAG, "onTextChange,position:" + position + ",isFirstEdit:"
                    + dstInfo.isFirstEdit());
            LogUtils.i(EditActivity.TAG, "onTextChange,lastFontType:" + dstInfo.getLastFontType()
                    + ",lastFontColor:" + dstInfo.getLastFontColor());
            LogUtils.i(EditActivity.TAG, "onTextChange,curFontType:" + dstInfo.getCurFontType()
                    + ",curFontColor:" + dstInfo.getCurFontColor());
            if (!dstInfo.getLastFontType().equals(dstInfo.getCurFontType())) {
                richEditor.setFontName(dstInfo.getCurFontType());
                dstInfo.setLastFontType(dstInfo.getCurFontType());
                dstInfo.setFirstEdit(false);
            }
            if (dstInfo.getLastFontColor() != dstInfo.getCurFontColor()) {
                richEditor.setTextColor(dstInfo.getCurFontColor());
                dstInfo.setLastFontColor(dstInfo.getCurFontColor());
                dstInfo.setFirstEdit(false);
            }
            ((RichEditorInfo) mEditorInfoList.get(position))
                    .setHtmlText(htmlText);
            LogUtils.i("EditActivity,richEditor pos:" + position + ",onTextChange:" + text);
            if (richEditor != null) {
                richEditor.evaluateJavascript("getWidth()", new ValueCallback<String>() {
                    public void onReceiveValue(String value) {
                    }
                });
            }
        }
    }

    private String uploadFileToServer(String str) {
        return "";
    }

    protected void initVariable() {
        mEditorList = new ArrayList();
        mEditorInfoList = new ArrayList();
        typeFaceArr = new String[] {
                "Arial", "Time New Roman", "Georgia",
                getResources().getString(R.string.song_typeface),
                getResources().getString(R.string.boldface),
                getResources().getString(R.string.imitation_song_dynasty_style_typeface),
                getResources().getString(R.string.regular_script)
        };
        String[] typeFace = new String[] {
                "A", "TNR", "G"
        };
        Drawable red = getResources().getDrawable(R.drawable.selector_font_red);
        Drawable orange = getResources().getDrawable(R.drawable.selector_font_orange);
        Drawable green = getResources().getDrawable(R.drawable.selector_font_green);
        Drawable blue = getResources().getDrawable(R.drawable.selector_font_blue);
        Drawable yellow = getResources().getDrawable(R.drawable.selector_font_yellow);
        Drawable blue_dark = getResources().getDrawable(R.drawable.selector_font_blue_dark);
        Drawable purple = getResources().getDrawable(R.drawable.selector_font_purple);
        Drawable black = getResources().getDrawable(R.drawable.selector_font_black);
        Drawable white = getResources().getDrawable(R.drawable.selector_font_white);
        drawables = new Drawable[] {
                red, orange, green, blue, yellow, blue_dark, purple, black, white
        };
        mLayoutGravity = new LayoutGravity(64);
        mContext = this;
    }

    private void initFontSetting() {
        mTypeFace = (ImageView) findViewById(R.id.typeface_iv);
        mTextSize = (ImageView) findViewById(R.id.textsize_iv);
        mTextColor = (ImageButton) findViewById(R.id.textcolor_ib);
        mToolContainer = (FontSettingContainer) findViewById(R.id.container_llt);
        mRecyclerView = (RecyclerView) findViewById(R.id.tool_rv);
        mColorRecyclerView = (RecyclerView) findViewById(R.id.tool_color_rv);
    }

    protected void initListener() {
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
        edit.setOnClickListener(this);
        txtShowSignature.setOnClickListener(this);
        mTypeFace.setOnClickListener(this);
        mTextSize.setOnClickListener(this);
        mTextColor.setOnClickListener(this);
        mPostilCon.setOnClickListener(this);
        mSavePostil.setOnClickListener(this);
        mPostil.setOnClickListener(this);
        cancel.setOnLongClickListener(this);
        save.setOnLongClickListener(this);
        edit.setOnLongClickListener(this);
        mTypeFace.setOnLongClickListener(this);
        mTextSize.setOnLongClickListener(this);
        mTextColor.setOnLongClickListener(this);
        mPostil.setOnLongClickListener(this);
        mPostilCon.setOnLongClickListener(this);
        mRichEditorCon.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                LogUtils.d("EditActivity,richEditorCon click");
                allExitEditStatus();
                setFontVisible(false);
            }
        });
    }

    private void allExitEditStatus() {
        for (RichEditor richEditor : mEditorList) {
            if (richEditor.change) {
                exitEditStatus(richEditor);
            }
        }
    }

    public void setFontSize(RichEditor richEditor, RichEditorInfo dstInfo, String curFontSize) {
        LogUtils.i(TAG, "setFontSize,lastFontSize:" + dstInfo.getLastFontSize());
        LogUtils.i(TAG, "setFontSize,curFontSize:" + curFontSize);
        dstInfo.setCurFontSize(curFontSize);
        if (dstInfo.getLastFontSize() != dstInfo.getCurFontSize()) {
            String htmlText = ManualSetFontSize(richEditor, dstInfo.getHtmlText(),
                    dstInfo.getCurFontSize());
            LogUtils.i("EditActivity,richEditor pos:,manualSize:" + htmlText);
            dstInfo.setLastFontSize(dstInfo.getCurFontSize());
            richEditor.setHtml(htmlText);
            dstInfo.setHtmlText(htmlText);
        }
    }

    private String ManualSetFontSize(boolean isFirst, RichEditorInfo info, String text,
            String curFontSize) {
        String htmlText = text;
        if (isFirst) {
            String str = htmlText.substring(htmlText.indexOf(">") + 1, htmlText.lastIndexOf("<"));
            LogUtils.d(TAG, "ManualSetFontSize,oldText:" + str);
            if (info.getText().equals(str)) {
                return htmlText;
            }
            String newText = str.substring(info.getText().length());
            LogUtils.d(TAG, "ManualSetFontSize,newText:" + newText);
            return htmlText
                    + ("<font style=\"font-size:" + curFontSize + ";\">" + newText + "</font>");
        }
        String fontSizeStr = htmlText.substring(htmlText.lastIndexOf("font-size:"),
                htmlText.lastIndexOf("px") + 2);
        LogUtils.d(TAG, "ManualSetFontSize,fontSizeStr:" + fontSizeStr);
        htmlText.replace(fontSizeStr, "font-size:" + curFontSize);
        return htmlText;
    }

    private String ManualSetFontSize(RichEditor editor, String text, String curFontSize) {
        String htmlText = text;
        Set<String> strList = new HashSet();
        int startIndex = htmlText.indexOf("font-size:");
        int lastIndex = htmlText.indexOf("px");
        LogUtils.d(TAG, "ManualSetFontSize,startIndex:" + startIndex + ",endIndex:" + lastIndex);
        while (startIndex != -1) {
            strList.add(htmlText.substring(startIndex + 10, lastIndex + 2));
            startIndex = htmlText.indexOf("font-size:", startIndex + 1);
            lastIndex = htmlText.indexOf("px", lastIndex + 1);
            LogUtils.d(TAG, "ManualSetFontSize,startIndex:" + startIndex + ",endIndex:" + lastIndex);
        }
        if (strList.size() == 0) {
            if (text.contains("<br>")) {
                return refreshCurSet(editor, text.substring(0, text.indexOf("<br>")), "kai_ti",
                        "#ffffff", "50px");
            }
            char[] strs = text.toCharArray();
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < strs.length) {
                sb.append(strs[i]);
                if (strs[i] == '<' && strs[i + 1] == 'f' && strs[i + 2] == 'o'
                        && strs[i + 3] == 'n' && strs[i + 4] == 't' && strs[i + 5] == ' ') {
                    sb.append(strs[i + 1]);
                    sb.append(strs[i + 2]);
                    sb.append(strs[i + 3]);
                    sb.append(strs[i + 4]);
                    sb.append(strs[i + 5]);
                    LogUtils.d(TAG, "ManualSetFontSize,find pos:" + i);
                    sb.append("style=\"font-size:" + curFontSize + ";\" ");
                    i += 5;
                }
                i++;
            }
            htmlText = sb.toString();
        } else {
            for (String str : strList) {
                LogUtils.d(TAG, "ManualSetFontSize,str:" + str);
                htmlText = htmlText.replace(str, curFontSize);
            }
        }
        return htmlText;
    }

    private String refreshCurSet(RichEditor editor, String text, String fontType, String fontColor,
            String fontSize) {
        return "<font face=\"" + fontType + "\" style=\"font-size:" + fontSize + ";\""
                + " color=\"" + fontColor + "\">" + text + "</font>";
    }

    private void initScreenSize() {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        mWindowWidth = mDisplayMetrics.widthPixels;
        mWindowHeight = mDisplayMetrics.heightPixels;
        imageWidth = mWindowWidth;
        imageHeight = mWindowHeight;
    }

    protected void changeWindowSize(boolean isWindowMode) {
        View child = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        LayoutParams params = (LayoutParams) child.getLayoutParams();
        if (isWindowMode) {
            imageWidth = (int) (((float) mWindowWidth) * 0.8f);
            imageHeight = (int) (((float) mWindowHeight) * 0.8f);
        } else {
            imageWidth = mWindowWidth;
            imageHeight = mWindowHeight;
        }
        params.width = imageWidth;
        params.height = imageHeight;
        params.gravity = 17;
        child.setLayoutParams(params);
    }

    private void changeComponentSize() {
        if (IndexActivity.mIsWindowMode) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cancel
                    .getLayoutParams();
            params.width = (int) getResources().getDimension(R.dimen.edit_cancel_width_window);
            params.height = (int) getResources().getDimension(R.dimen.edit_cancel_width_window);
            cancel.setLayoutParams(params);
            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) save
                    .getLayoutParams();
            params1.width = (int) getResources().getDimension(R.dimen.edit_cancel_width_window);
            params1.height = (int) getResources().getDimension(R.dimen.edit_cancel_width_window);
            save.setLayoutParams(params1);
            params1 = (LinearLayout.LayoutParams) edit.getLayoutParams();
            params1.width = (int) getResources().getDimension(R.dimen.edit_cancel_width_window);
            params1.height = (int) getResources().getDimension(R.dimen.edit_cancel_width_window);
            edit.setLayoutParams(params1);
            params = (RelativeLayout.LayoutParams) txtShowSignature.getLayoutParams();
            params.width = (int) getResources().getDimension(
                    R.dimen.edit_show_signature_width_window);
            params.height = (int) getResources().getDimension(R.dimen.edit_cancel_width_window);
            txtShowSignature.setLayoutParams(params);
            txtShowSignature.setTextSize(16.0f);
            return;
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cancel.getLayoutParams();
        params.width = -2;
        params.height = -2;
        cancel.setLayoutParams(params);
        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) save.getLayoutParams();
        params1.width = -2;
        params1.height = -2;
        save.setLayoutParams(params1);
        params1 = (LinearLayout.LayoutParams) edit.getLayoutParams();
        params1.width = -2;
        params1.height = -2;
        edit.setLayoutParams(params1);
        params = (RelativeLayout.LayoutParams) txtShowSignature.getLayoutParams();
        params.width = -2;
        params.height = -2;
        txtShowSignature.setLayoutParams(params);
        txtShowSignature.setTextSize(20.0f);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(TAG, "-----onCreate-----");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_edit_lib);
        initScreenSize();
        HockUtil.hookWebView();
        isFirstLoad = true;
        mRichEditorCon = new RelativeLayout(this);
        mRichEditorCon.setVisibility(View.GONE);
        initView();
        initVariable();
        initListener();
        getData();
        mParent.addView(mRichEditorCon, 1, new RelativeLayout.LayoutParams(-1, -1));
    }

    protected void onResume() {
        super.onResume();
        LogUtils.i(TAG, "-----onResume-----");
    }

    private void createSingleRichEditor() {
        mRichEditorCon.setVisibility(View.VISIBLE);
        RichEditorInfo info = new RichEditorInfo();
        RichEditor richEditor = new RichEditor(this);
        richEditor.setMinimumWidth(220);
        richEditor.setEditorBackgroundColor(0);
        int width = mParent.getWidth() / 2;
        int height = mParent.getHeight() / 2;
        richEditor.setX((float) (width - 200));
        richEditor.setY((float) (height - 50));
        info.setX((float) (width - 200));
        info.setY((float) (height - 50));
        richEditor.setFontName("song_ti");
        richEditor.setBaseFontSize("50px");
        richEditor.setTextColor(Color.parseColor("#ffffff"));
        richEditor.setHtml(getString(R.string.please_input));
        allExitEditStatus();
        showRichEditor(richEditor);
        info.setLastFontType("song_ti");
        info.setCurFontType("song_ti");
        info.setLastFontColor(getResources().getColor(R.color.white));
        info.setCurFontColor(getResources().getColor(R.color.white));
        info.setLastFontSize("50px");
        info.setCurFontSize("50px");
        info.setText(getString(R.string.please_input));
        mRichEditorCon.addView(richEditor, new RelativeLayout.LayoutParams(-2, -2));
        mEditorList.add(richEditor);
        mEditorInfoList.add(info);
        initRichEditorListener(richEditor, mEditorList.size() - 1);

    }

    private void createRichEditor(List<RichEditorInfo> infoList) {
        mRichEditorCon.setVisibility(View.VISIBLE);
        for (int i = 0; i < infoList.size(); i++) {
            RichEditorInfo info = (RichEditorInfo) infoList.get(i);
            RichEditor richEditor = new RichEditor(this);
            richEditor.setClickable(false);
            richEditor.setMinimumWidth(220);
            richEditor.setEditorBackgroundColor(0);
            richEditor.setVisibility(View.VISIBLE);
            richEditor.setFontName(info.getCurFontType());
            richEditor.setBaseFontSize(info.getCurFontSize());
            richEditor.setBaseTextColor(info.getCurFontColor());
            richEditor.setHtml(info.getText());
            LogUtils.d(
                    TAG,
                    "createRichEditor,name:" + info.getCurFontType() + ",size:"
                            + info.getCurFontSize() + ",color:" + info.getCurFontColor() + ",text:"
                            + info.getText());
            richEditor.setBorder("none");
            richEditor.setEditorEnable(false);
            float x = info.getX();
            float y = info.getY();
            if (((double) x) == 0.0d && ((double) y) == 0.0d) {
                int height = getWindowManager().getDefaultDisplay().getHeight() / 2;
                richEditor
                        .setX((float) ((getWindowManager().getDefaultDisplay().getWidth() / 2) - 200));
                richEditor.setY((float) (height - 50));
            } else {
                richEditor.setX(x);
                richEditor.setY(y);
            }
            initRichEditorListener(richEditor, i);
            mRichEditorCon.addView(richEditor, new RelativeLayout.LayoutParams(-2, -2));
            mEditorList.add(richEditor);
        }
    }

    private String refreshCurFontColor(String text, String fontColor) {
        return "<font color=\"" + fontColor + "\">" + text + "</font>";
    }

    private void initRichEditorListener(final RichEditor richEditor, final int position) {
        richEditor.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                LogUtils.d("EditActivity,richEditor click,change:" + change);
                if (richEditor.change) {
                    InputMethodManager imm = (InputMethodManager) EditActivity.this
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    exitEditStatus(richEditor);
                }
            }
        });
        richEditor.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View view) {
                return false;
            }
        });
        richEditor.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                        LogUtils.d("EditActivity,richEditor pos:" + position + " touch");
                        for (int i = 0; i < mEditorList.size(); i++) {
                            if (i == position) {
                                RichEditorInfo info = (RichEditorInfo) mEditorInfoList
                                        .get(i);
                                if (info.getText().equals(
                                        getString(R.string.please_input))) {
                                    richEditor.setHtml("");
                                    info.setHtmlText("");
                                    info.setText("");
                                }
                                showRichEditor(richEditor);
                            } else {
                                EditActivity.this
                                        .exitEditStatus((RichEditor) mEditorList
                                                .get(i));
                            }
                        }
                        break;
                }
                return false;
            }
        });
        richEditor.setOnTextChangeListener(new RichEditorTextChangeListener(richEditor, position));
        richEditor.setOnLocationListener(new RichEditorLocationListener(position));
    }

    private void initView() {
        mainImage = (ImageView) findViewById(R.id.main_image);
        cancel = (ImageView) findViewById(R.id.act_et_cancel_iv);
        save = (ImageView) findViewById(R.id.act_et_save_iv);
        edit = (ImageView) findViewById(R.id.act_et_edit_iv);
        txtShowSignature = (TextView) findViewById(R.id.txt_show_signature);
        mRadioGroupControl = (LinearLayout) findViewById(R.id.container_control_llt);
        mParent = (RelativeLayout) findViewById(R.id.act_et_container_rlt);
        mPostil = (ImageView) findViewById(R.id.act_et_postil_iv);
        mPenProperty = (ImageView) findViewById(R.id.layout_pen_postil_iv);
        mSavePostil = (ImageView) findViewById(R.id.layout_save_pro_iv);
        mPenPropertyCon = (LinearLayout) findViewById(R.id.pen_property_container);
        mPostilCon = (LinearLayout) findViewById(R.id.postil_container);
        mSignature = (Signature) findViewById(R.id.signaturePad);
        mProgressText = (SubsectionProgress) findViewById(R.id.progress_text);
        mProgressColor = (ColorProgress) findViewById(R.id.progress_color);
        mRichEditorCon.setClickable(false);
        mClear = (ImageView) findViewById(R.id.layout_clear_pro_iv);
        mEraser = (ImageView) findViewById(R.id.layout_eraser_iv);
        initFontSetting();
        initQRCodePopWindow();
    }

    private void getData() {
        int i;
        filePath = getIntent().getStringExtra("IMAGE_PATH");
        iconFilePath = getIntent().getIntExtra("IMAGE_PATH_ICON", -1);
        isAddTheme = getIntent().getBooleanExtra("isAddTheme", false);
        boolean show_qrocde = getIntent().getBooleanExtra("SHOW_QRCODE", false);
        custom = getIntent().getBooleanExtra("CUSTOM", false);
        mIndustryName = getIntent().getStringExtra("INDUSTRY_NAME");
        String filename = getIntent().getStringExtra("CUSTOM_NAME");
        String qrpath = getIntent().getStringExtra("QRPATH");
        boolean isAddModule = getIntent().getBooleanExtra("isAddModule", false);
        ArrayList<RichEditorInfo> editorInfoList = getIntent().getParcelableArrayListExtra(
                "RICHEDITOR_LIST");
        if (editorInfoList != null && editorInfoList.size() > 0) {
            for (i = 0; i < editorInfoList.size(); i++) {
                RichEditorInfo info = (RichEditorInfo) editorInfoList.get(i);
                parseHtmlText(info, info.getHtmlText());
                mEditorInfoList.add(info);
            }
        }
        mQrcodeCon.setVisibility(View.GONE);
        if (custom) {
            if (mEditorInfoList != null && mEditorInfoList.size() > 0) {
                createRichEditor(mEditorInfoList);
            }
        } else if (isAddModule) {
            for (i = 0; i < AddModule.TITLE_HTMLTEXT.length; i++) {
                RichEditorInfo info = new RichEditorInfo();
                info.setHtmlText(AddModule.TITLE_HTMLTEXT[i]);
                info.setX(AddModule.TITLE_X[i]);
                info.setY(AddModule.TITLE_Y[i]);
                parseHtmlText(info, AddModule.TITLE_HTMLTEXT[i]);
                mEditorInfoList.add(info);
            }
            createRichEditor(mEditorInfoList);
        } else if (mEditorInfoList != null && mEditorInfoList.size() > 0) {
            createRichEditor(mEditorInfoList);
        }
        changeEditBg();
        mSizeList = new ArrayList();
        Collections.addAll(mSizeList, strs);
        mSignature.setPenConGone(mPenPropertyCon);
    }

    private void changeEditBg() {
        if (!TextUtils.isEmpty(filePath)) {
            type_string = true;
            if (!TextUtils.isEmpty(filePath)) {
                loadImage(filePath);
            }
        }
        if (iconFilePath != -1) {
            loadImage(iconFilePath);
        }
    }

    private void parseHtmlText(RichEditorInfo info, String htmlText) {
        String[] fontSet = htmlText.split(" ");
        String fontType = "song_ti";
        String fontSize = "50px";
        String textColor = "#ffffff";
        int fontColor = getResources().getColor(R.color.white);
        info.setLastFontType(fontType);
        info.setCurFontType(fontType);
        info.setLastFontSize(fontSize);
        info.setCurFontSize(fontSize);
        info.setLastFontColor(fontColor);
        info.setCurFontColor(fontColor);
        info.setText("");
        if (fontSet.length > 0) {
            for (String set : fontSet) {
                if (set.contains("face")) {
                    fontType = set.substring(set.indexOf("\"") + 1, set.lastIndexOf("\""));
                }
                if (set.contains("font-size")) {
                    fontSize = set.substring(set.indexOf(":") + 1, set.indexOf(";"));
                }
                if (set.contains("color")) {
                    textColor = set.substring(set.indexOf("\"") + 1, set.lastIndexOf("\""));
                    fontColor = Color.parseColor(textColor);
                }
            }
            info.setLastFontType(fontType);
            info.setCurFontType(fontType);
            info.setLastFontSize(fontSize);
            info.setCurFontSize(fontSize);
            info.setLastFontColor(fontColor);
            info.setCurFontColor(fontColor);
            LogUtils.d(TAG, "parseHtmlText,type:" + fontType + ",size:" + fontSize + ",textColor:"
                    + textColor + ",color:" + fontColor);
            String text = htmlText.substring(htmlText.indexOf(">") + 1, htmlText.lastIndexOf("<"));
            info.setText(text);
            LogUtils.d(TAG, "parseHtmlText,text:" + text);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.act_et_edit_iv:
                enterEditStatus();
                return;
            case R.id.typeface_iv:
                changeTextType();
                return;
            case R.id.textsize_iv:
                changeTextSize();
                return;
            case R.id.textcolor_ib:
                changeTextColor();
                return;
            case R.id.act_et_cancel_iv:
                exitEdit();
                return;
            case R.id.act_et_save_iv:
                saveImage();
                return;
            case R.id.txt_show_signature:
                showSignatureActivity();
                return;
            default:
                return;
        }
    }

    private void showSignatureActivity() {
        LogUtils.i("EditActivity,showSignatureActivity");
        for (int i = 0; i < mEditorList.size(); i++) {
            RichEditor editor = (RichEditor) mEditorList.get(i);
            if (editor.change) {
                exitEditStatus(editor);
            }
        }
        cancel.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);
        mToolContainer.setVisibility(View.GONE);
        txtShowSignature.setVisibility(View.GONE);
        ThreadManager.getThreadPoolProxy().excute(new Runnable() {
            public void run() {
                if (!(EditActivity.mCustomSignatureBg == null || EditActivity.mCustomSignatureBg
                        .isRecycled())) {
                    EditActivity.mCustomSignatureBg.recycle();
                }
                EditActivity.mCustomSignatureBg = (Bitmap) FileUtil.takeScreenShot(
                        EditActivity.this).get();
                if (EditActivity.mCustomSignatureBg == null) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(EditActivity.this, R.string.get_screen_picture_fail, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            cancel.setVisibility(View.VISIBLE);
                            save.setVisibility(View.VISIBLE);
                            edit.setVisibility(View.VISIBLE);
                            txtShowSignature.setVisibility(View.VISIBLE);
                            startActivity(new Intent(EditActivity.this,
                                    SignatureActivity.class));
                        }
                    });
                }
            }
        });
    }

    private void exitEdit() {
        if (saveSuccess) {
            finish();
            EventBus.getDefault().post("refresh:" + mIndustryName);
        }
        if (isEdited) {
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_save_picture_hint, null);
            final AlertDialog dialog = new Builder(this, R.style.myDialog).create();
            TextView yes = (TextView) view.findViewById(R.id.txt_save_signature_yes);
            ((TextView) view.findViewById(R.id.txt_save_signature_no))
                    .setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
            yes.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    dialog.dismiss();
                    finish();
                    EventBus.getDefault().post("refresh:" + mIndustryName);
                }
            });
            dialog.show();
            dialog.setContentView(view);
            dialog.getWindow().clearFlags(131072);
        } else {
            finish();
        }
        clearSharedPreferences();
    }

    private void clearSharedPreferences() {
        Editor edit = getSharedPreferences("position", 0).edit();
        edit.clear();
        edit.apply();
    }

    private void hideOrShowQrCodeCon() {
        if (mQrCode.getVisibility() == View.GONE || mQrCode.getVisibility() == View.INVISIBLE) {
            mQrCode.setVisibility(View.VISIBLE);
            mCodePicRlt.setVisibility(View.GONE);
            return;
        }
        mQrCode.setVisibility(View.GONE);
        mCodePicRlt.setVisibility(View.VISIBLE);
    }

    private void hideOrShowQrCode() {
        if (mCodePicRlt.getVisibility() == View.GONE || mCodePicRlt.getVisibility() == View.INVISIBLE) {
            mCodePicRlt.setVisibility(View.VISIBLE);
            mQrCode.setVisibility(View.GONE);
            return;
        }
        mCodePicRlt.setVisibility(View.GONE);
        mQrCode.setVisibility(View.VISIBLE);
    }

    private void changeTextColor() {
        isCheckedTC = !isCheckedTC;
        if (isCheckedTP) {
            mTypeFace.setImageResource(R.drawable.typeface_normal);
            mRecyclerView.setVisibility(View.GONE);
            isCheckedTP = false;
        }
        if (isCheckedTS) {
            mTextSize.setImageResource(R.drawable.textsize_normal);
            mRecyclerView.setVisibility(View.GONE);
            isCheckedTS = false;
        }
        if (isCheckedTC) {
            mTextColor.setImageResource(R.drawable.color_selected);
            mColorRecyclerView.setVisibility(View.VISIBLE);
            mColorRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            if (mTextColorAdapter == null) {
                mTextColorAdapter = new TextColorAdapter(R.layout.item_textcolor,
                        Arrays.asList(drawables));
            }
            mTextColorAdapter
                    .setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
                        public void onItemClick(View view, int i) {
                            Log.i(EditActivity.TAG, "onItemClick");
                            mTextColorAdapter.setSelectItem(i);
                            mTextColorAdapter.notifyDataSetChanged();
                            isFontColorChange = true;
                            for (int j = 0; j < mEditorList.size(); j++) {
                                RichEditor editor = (RichEditor) mEditorList
                                        .get(j);
                                RichEditorInfo info = (RichEditorInfo) mEditorInfoList
                                        .get(j);
                                if (editor.change) {
                                    int color = getResources().getColor(
                                            colorArr[i]);
                                    LogUtils.i("EditActivity,changeTextColor,cur pos:" + j
                                            + ",mFontColor:" + color);
                                    editor.setTextColor(color);
                                    info.setCurFontColor(color);
                                }
                            }
                            mColorRecyclerView.setVisibility(View.GONE);
                            isCheckedTC = false;
                            mTextColor.setImageResource(R.drawable.color_normal);
                        }
                    });
            mColorRecyclerView.setAdapter(mTextColorAdapter);
            return;
        }
        mTextColor.setImageResource(R.drawable.color_normal);
        mColorRecyclerView.setVisibility(View.GONE);
    }

    private void changeTextSize() {
        boolean z;
        if (isCheckedTS) {
            z = false;
        } else {
            z = true;
        }
        isCheckedTS = z;
        if (isCheckedTP) {
            mTypeFace.setImageResource(R.drawable.typeface_normal);
            mRecyclerView.setVisibility(View.GONE);
            isCheckedTP = false;
        }
        if (isCheckedTC) {
            mTextColor.setImageResource(R.drawable.color_normal);
            mColorRecyclerView.setVisibility(View.GONE);
            isCheckedTC = false;
        }
        if (isCheckedTS) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextSize.setImageResource(R.drawable.textsiz_selected);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            typeFaceAdapter = new TypeFaceAdapter(Arrays.asList(textSizeStr), this,
                    false, mRecyclerView);
            for (int i = 0; i < mEditorList.size(); i++) {
                RichEditor editor = (RichEditor) mEditorList.get(i);
                if (editor.change) {
                    LogUtils.i("EditActivity,change textSize cur focus pos:" + i);
                    typeFaceAdapter.handleTextSizeClick(editor, mTextSize,
                            (RichEditorInfo) mEditorInfoList.get(i));
                }
            }
            mRecyclerView.setAdapter(typeFaceAdapter);
            return;
        }
        mTextSize.setImageResource(R.drawable.textsize_normal);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void changeTextType() {
        boolean z;
        if (isCheckedTP) {
            z = false;
        } else {
            z = true;
        }
        isCheckedTP = z;
        if (isCheckedTS) {
            mTextSize.setImageResource(R.drawable.textsize_normal);
            mRecyclerView.setVisibility(View.GONE);
            isCheckedTS = false;
        }
        if (isCheckedTC) {
            mTextColor.setImageResource(R.drawable.color_normal);
            mColorRecyclerView.setVisibility(View.GONE);
            isCheckedTC = false;
        }
        if (isCheckedTP) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTypeFace.setImageResource(R.drawable.typeface_selected);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            typeFaceAdapter = new TypeFaceAdapter(Arrays.asList(typeFaceArr), this, true,
                    mRecyclerView);
            for (int i = 0; i < mEditorList.size(); i++) {
                RichEditor editor = (RichEditor) mEditorList.get(i);
                if (editor.change) {
                    LogUtils.i("EditActivity,change textType cur pos:" + i);
                    typeFaceAdapter.handleTypeFaceClick(editor, mTypeFace,
                            (RichEditorInfo) mEditorInfoList.get(i));
                }
            }
            mRecyclerView.setAdapter(typeFaceAdapter);
            return;
        }
        mTypeFace.setImageResource(R.drawable.typeface_normal);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void enterEditStatus() {
        if (signStatus) {
            Toast.makeText(mContext, R.string.exit_sign_first, Toast.LENGTH_SHORT).show();
            return;
        }
        mQrCode.setVisibility(View.GONE);
        mQrcodeCon.setVisibility(View.GONE);
        createSingleRichEditor();
    }

    private void showRichEditor(RichEditor richEditor) {
        richEditor.change = true;
        richEditor.setEditorEnable(true);
        richEditor.setClickable(true);
        richEditor.setBorder("dashed #ccc 1px");
        richEditor.focusEditor();
        showFontSetting(richEditor, 0, 20);
        mQrcodeCon.setVisibility(View.GONE);
    }

    public void showFontSetting(View anchor, int xOffset, int yOffset) {
        mToolContainer.setVisibility(View.VISIBLE);
        int[] offset = mLayoutGravity.getOffset(anchor, mToolContainer, xOffset, yOffset);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
        params.addRule(10);
        params.leftMargin = offset[0];
        params.topMargin = offset[1];
        mToolContainer.setLayoutParams(params);
    }

    private void createFontSettingPopWindow() {
        View view = getLayoutInflater().inflate(R.layout.layout_font_setting, null);
        mTypeFace = (ImageView) view.findViewById(R.id.typeface_iv);
        mTextSize = (ImageView) view.findViewById(R.id.textsize_iv);
        mTextColor = (ImageButton) view.findViewById(R.id.textcolor_ib);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.tool_rv);
        mColorRecyclerView = (RecyclerView) view.findViewById(R.id.tool_color_rv);
        mTypeFace.setOnClickListener(this);
        mTextSize.setOnClickListener(this);
        mTextColor.setOnClickListener(this);
        popFontSetting = new PopupWindow(view, -2, -2);
        popFontSetting.setTouchable(true);
        popFontSetting.setOutsideTouchable(false);
        popFontSetting.update();
    }

    private void exitEditStatus(RichEditor richEditor) {
        richEditor.change = false;
        richEditor.clearFocusEditor();
        richEditor.setEditorEnable(false);
        richEditor.setBorder("none");
    }

    private void setFontVisible(boolean isVisible) {
        if (isVisible) {
            mToolContainer.setVisibility(View.VISIBLE);
            return;
        }
        mToolContainer.setVisibility(View.GONE);
        if (mRecyclerView != null && mRecyclerView.getVisibility() == View.VISIBLE) {
            mRecyclerView.setVisibility(View.GONE);
        }
        if (mColorRecyclerView != null && mColorRecyclerView.getVisibility() == View.VISIBLE) {
            mColorRecyclerView.setVisibility(View.GONE);
        }
    }

    private void saveImage() {
        if (signStatus) {
            Toast.makeText(mContext, R.string.exit_sign_first, Toast.LENGTH_SHORT).show();
            return;
        }
        int i;
        if (VERSION.SDK_INT >= 23) {
            requestReadsd();
        }
        for (i = 0; i < mEditorList.size(); i++) {
            RichEditor editor = (RichEditor) mEditorList.get(i);
            if (editor.change) {
                exitEditStatus(editor);
            }
        }
        EditText editText = new EditText(this);
        editText.setHint(R.string.save_input_text);
        editText.setImeOptions(33554432);
        for (i = 0; i < mEditorList.size(); i++) {
            ((RichEditor) mEditorList.get(i)).clearFocusEditor();
        }
        save.requestFocus();
        showSavePictureDialog();
    }

    private void showSavePictureDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_save_edit_picture, null);
        final AlertDialog dialog = new Builder(this, R.style.myDialog).create();
        final EditText etSaveName = (EditText) view.findViewById(R.id.et_input_save_name);
        TextView txtCancel = (TextView) view.findViewById(R.id.txt_save_edit_cancel);
        ((TextView) view.findViewById(R.id.txt_save_edit_confirm))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        EditActivity.mSaveFileName = etSaveName.getText().toString().trim();
                        dialog.dismiss();
                        if (TextUtils.isEmpty(EditActivity.mSaveFileName)) {
                            Toast.makeText(mContext, R.string.filename_not_null,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        hideUI();
                        String qrPath = EditActivity.mSaveFileName;
                        showSavingEditPicDialog();
                        String storeType = DBUtil.getPicStoreType();
                        LogUtils.d(EditActivity.TAG, "saveImage,storeType:" + storeType);
                        if (storeType.startsWith(Config.INNER_STOREAGE_PATH)) {
                            saveScreenContent(
                                    FileUtil.getBaseStorageDir(storeType), qrPath);
                        } else if (storeType.startsWith("/mnt/usb")) {
                            if (FileUtils.isUsbPurgeIn(storeType)) {
                                saveScreenContent(
                                        FileUtil.getBaseStorageDir(storeType), qrPath);
                                return;
                            }
                            Toast.makeText(mContext, R.string.usb_not_insert, Toast.LENGTH_LONG)
                                    .show();
                            showUI();
                            hideSavingEditPicDialog();
                        } else if (FileUtil.getSecondaryStorageState().equals("mounted")) {
                            saveScreenContent(
                                    FileUtil.getBaseStorageDir(storeType), qrPath);
                        } else {
                            Toast.makeText(mContext, R.string.no_sd_card, Toast.LENGTH_LONG)
                                    .show();
                            showUI();
                            hideSavingEditPicDialog();
                        }
                    }
                });
        txtCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setContentView(view);
        dialog.getWindow().clearFlags(131072);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = -2;
        params.height = -2;
        params.gravity = 17;
        window.setAttributes(params);
    }

    private void hideSavingEditPicDialog() {
        if (mSavingEditPicDialog != null && mSavingEditPicDialog.isShowing()) {
            mSavingEditPicDialog.dismiss();
            mSavingEditPicDialog.setFinished(true);
            mSavingEditPicDialog = null;
        }
    }

    private void showSavingEditPicDialog() {
        mSavingEditPicDialog = new SavingEditPictureDialog(this, R.style.myDialog);
        mSavingEditPicDialog.setCanceledOnTouchOutside(false);
        mSavingEditPicDialog.show();
    }

    private void hideUI() {
        cancel.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);
        txtShowSignature.setVisibility(View.GONE);
        mToolContainer.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mColorRecyclerView.setVisibility(View.GONE);
    }

    private void showUI() {
        cancel.setVisibility(View.VISIBLE);
        save.setVisibility(View.VISIBLE);
        edit.setVisibility(View.VISIBLE);
        txtShowSignature.setVisibility(View.VISIBLE);
        mToolContainer.setVisibility(View.GONE);
    }

    private void saveScreenContent(final String baseDir, final String qrPath) {
        ThreadManager.getThreadPoolProxy().excute(new Runnable() {
            public void run() {
                screenshotAndSaveImage(baseDir);
                runOnUiThread(new Runnable() {
                    public void run() {
                        hideSavingEditPicDialog();
                        if (mSaveSuccessBtn) {
                            handleAfterSave(baseDir);
                        } else {
                            showReplacePicDialog(baseDir);
                        }
                        showUI();
                    }

                    private void handleAfterSave(String baseDir) {
                        if (TextUtils.isEmpty(mEditImageUrl)) {
                            Toast makeText = Toast.makeText(mContext,
                                    getString(R.string.fail), Toast.LENGTH_LONG);
                            mCodePicRlt.setVisibility(View.GONE);
                            hideQrcodePopWindow();
                        } else {
                            generateQRCode(baseDir, EditActivity.mSaveFileName
                                    + FileUtil.JPEG, mEditImageUrl, true);
                            showQRCodePopWindow();
                        }
                        showPopPupWindow();
                        isEdited = false;
                        mToolContainer.setVisibility(View.GONE);
                        saveSuccess = true;
                    }

                    private void showReplacePicDialog(final String baseDir) {
                        Builder builder = new Builder(EditActivity.this);
                        builder.setMessage(R.string.save_failed);
                        builder.setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        builder.setPositiveButton(R.string.confirm,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        File file = new File(baseDir + "/" + Config.THEME_PICTURE
                                                + "/" + EditActivity.mSaveFileName + FileUtil.JPEG);
                                        if (file.exists()) {
                                            file.delete();
                                        }
                                        List<PicData> picDataList = DBUtil.mDaoSession
                                                .getPicDataDao()
                                                .queryBuilder()
                                                .where(Properties.FileName
                                                        .like(EditActivity.mSaveFileName),
                                                        new WhereCondition[0]).list();
                                        if (picDataList != null && picDataList.size() > 0) {
                                            for (PicData data : picDataList) {
                                                DBUtil.mDaoSession.getPicDataDao().deleteByKey(
                                                        data.getId());
                                            }
                                        }
                                        hideUI();
                                        showSavingEditPicDialog();
                                        ThreadManager.getThreadPoolProxy().excute(new Runnable() {
                                            public void run() { screenshotAndSaveImage(baseDir);
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        hideSavingEditPicDialog();
                                                        if (mSaveSuccessBtn) {handleAfterSave(baseDir);
                                                        }
                                                        showUI();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                        builder.show();
                    }
                });
            }

            private void screenshotAndSaveImage(String baseDir) {
                if (type_string) {
                    imageFromSDcard(baseDir);
                } else {
                    imageFromLocal(baseDir);
                }
            }

            private void imageFromSDcard(String baseDir) {
                Bitmap bitmap = null;
                try {
                    bitmap = (Bitmap) Glide.with(mContext)
                            .load(filePath).asBitmap().centerCrop()
                            .into(imageWidth, imageHeight)
                            .get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e2) {
                    e2.printStackTrace();
                }
                String name = new File(filePath).getName();
                if (bitmap != null) {
                    WeakReference<Bitmap> bitmapWeakReference = new WeakReference(bitmap);
                    String[] nameArr = name.split("\\.");
                    if (nameArr.length > 0) {
                        List<PicData> unique = DBUtil.mDaoSession
                                .getPicDataDao()
                                .queryBuilder()
                                .where(Properties.FileName.like(EditActivity.mSaveFileName),
                                        new WhereCondition[0]).list();
                        if (unique == null || unique.size() == 0) {
                            String savePath;
                            for (int i = 0; i < mEditorList.size(); i++) {
                                RichEditorInfo info = (RichEditorInfo) mEditorInfoList
                                        .get(i);
                                DBUtil.mDaoSession.getPicDataDao().insert(
                                        new PicData(null, EditActivity.mSaveFileName, true,
                                                nameArr[0], info.getHtmlText(), info.getX(), info
                                                        .getY(), qrPath));
                            }
                            if (isAddTheme) {
                                savePath = baseDir + "/" + Config.THEME_PICTURE + "/"
                                        + Config.CUSTOME + "/" + mIndustryName;
                            } else {
                                savePath = baseDir + "/" + Config.THEME_PICTURE + "/"
                                        + mIndustryName;
                            }
                            Log.d(EditActivity.TAG, "imageFromSDcard,savePath:" + savePath);
                            mSaveSuccessBtn = FileUtil.savePic(savePath,
                                    FileUtil.takeScreenShot(EditActivity.this),
                                    EditActivity.mSaveFileName);
                            mEditImageUrl = EditActivity.this
                                    .uploadFileToServer(savePath + "/" + EditActivity.mSaveFileName
                                            + FileUtil.JPEG);
                        }
                    }
                }
            }

            private void imageFromLocal(String baseDir) {
                Bitmap bitmap = null;
                try {
                    bitmap = Picasso.with(mContext)
                            .load(iconFilePath).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bitmap != null) {
                    WeakReference<Bitmap> bitmapWeakReference = new WeakReference(bitmap);
                    String[] split = getResources()
                            .getResourceName(iconFilePath).split("/");
                    if (split.length > 0) {
                        List<PicData> unique = DBUtil.mDaoSession
                                .getPicDataDao()
                                .queryBuilder()
                                .where(Properties.FileName.like(EditActivity.mSaveFileName),
                                        new WhereCondition[0]).list();
                        if (unique == null || unique.size() == 0) {
                            String savePath;
                            for (int i = 0; i < mEditorList.size(); i++) {
                                RichEditorInfo info = (RichEditorInfo) mEditorInfoList
                                        .get(i);
                                DBUtil.mDaoSession.getPicDataDao().insert(
                                        new PicData(null, EditActivity.mSaveFileName, false,
                                                split[1], info.getHtmlText(), info.getX(), info
                                                        .getY(), qrPath));
                            }
                            WeakReference<Bitmap> bitmapWeakReference1 = FileUtil
                                    .takeScreenShot(EditActivity.this);
                            if (isAddTheme) {
                                savePath = baseDir + "/" + Config.THEME_PICTURE + "/"
                                        + Config.CUSTOME + "/" + mIndustryName;
                            } else {
                                savePath = baseDir + "/" + Config.THEME_PICTURE + "/"
                                        + mIndustryName;
                            }
                            Log.d(EditActivity.TAG, "imageFromLocal,savePath:" + savePath);
                            mSaveSuccessBtn = FileUtil.savePic(savePath,
                                    bitmapWeakReference1, EditActivity.mSaveFileName);
                            mEditImageUrl = EditActivity.this
                                    .uploadFileToServer(savePath + "/" + EditActivity.mSaveFileName
                                            + FileUtil.JPEG);
                        }
                    }
                }
            }
        });
    }

    private void initQRCodePopWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_qrcode_pop, null);
        mQrcodeCon = (LinearLayout) view.findViewById(R.id.act_qrcode_cotainer_llt);
        mCodePicRlt = (LinearLayout) view.findViewById(R.id.act_code_pic_rlt);
        mQRCodeBig = (ImageView) view.findViewById(R.id.act_code_pic_iv);
        mCodeText = (TextView) view.findViewById(R.id.act_code_pic_tv);
        mQrCode = (ImageView) view.findViewById(R.id.act_ea_qrcode_iv);
        mQrcodePopWindow = new PopupWindow(view, -2, -2, true);
        mQrcodePopWindow.setFocusable(true);
        mQrcodePopWindow.setOutsideTouchable(true);
        mQrcodePopWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    private void showQRCodePopWindow() {
        if (mQrcodePopWindow != null && !mQrcodePopWindow.isShowing()) {
            mQrcodePopWindow.showAtLocation(mParent, 17, 0, 0);
        }
    }

    private void hideQrcodePopWindow() {
        if (mQrcodePopWindow != null && mQrcodePopWindow.isShowing()) {
            mQrcodePopWindow.dismiss();
        }
    }

    private boolean generateQRCode(String baseDir, String filename, String url, boolean saveQRcode) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        Bitmap qrCode = QRCode.createQRCode(url);
        if (mQrcodeCon.getVisibility() == View.GONE) {
            mQrcodeCon.setVisibility(View.VISIBLE);
        }
        mQRCodeBig.setImageBitmap(null);
        mQRCodeBig.setImageBitmap(qrCode);
        mCodePicRlt.setVisibility(View.VISIBLE);
        if (saveQRcode) {
            mCodeText.setText(R.string.scan_save_image);
        } else {
            mCodeText.setText(R.string.code_text);
        }
        return true;
    }

    private String upLoadImage(String path) {
        return UploadUtil.uploadFile(URL, new File(path));
    }

    private void showPopPupWindow() {
        View poppupView = LayoutInflater.from(mContext).inflate(R.layout.layout_poppup, null);
        mPoppupwindow = new PopupWindow(poppupView, FileUtil.dip2px(mContext, 200.0f),
                FileUtil.dip2px(mContext, 100.0f), false);
        mPoppupwindow.setOutsideTouchable(false);
        mPoppupwindow.setBackgroundDrawable(new BitmapDrawable());
        mPoppupwindow.showAtLocation(mParent, 17, 0, 0);
        poppupView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mPoppupwindow != null
                        && mPoppupwindow.isShowing()) {
                    mPoppupwindow.dismiss();
                }
                return false;
            }
        });
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (mPoppupwindow != null
                        && mPoppupwindow.isShowing()) {
                    mPoppupwindow.dismiss();
                }
            }
        }, 1000);
    }

    public void loadImage(String filepath) {
        Glide.with((Activity) this).load(filepath).into(mainImage);
    }

    public void loadImage(int resId) {
        Glide.with((Activity) this).load(Integer.valueOf(resId)).into(mainImage);
    }

    public static Dialog getLoadingDialog(Context context, String title, boolean canCancel) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(canCancel);
        dialog.setMessage(title);
        return dialog;
    }

    public void requestReadsd() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(this, new String[] {
                "android.permission.WRITE_EXTERNAL_STORAGE"
            }, 1);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            if (mPoppupwindow != null && mPoppupwindow.isShowing()) {
                return true;
            }
            if (saveSuccess) {
                EventBus.getDefault().post("refresh");
            }
            clearSharedPreferences();
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            NetworkInfo mNetworkInfo = ((ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.act_et_edit_iv:
                showPop(view, R.string.edit);
                break;
            case R.id.act_et_cancel_iv:
                showPop(view, R.string.exit);
                break;
            case R.id.act_et_save_iv:
                showPop(view, R.string.save_image);
                break;
            case R.id.act_et_postil_iv:
                showPop(view, R.string.sign);
                break;
            case R.id.layout_pen_postil_iv:
                showPop(view, R.string.pen_postil);
                break;
            case R.id.layout_clear_pro_iv:
                showPop(view, R.string.clear_signature);
                break;
            case R.id.layout_save_pro_iv:
                showPop(view, R.string.save_signature);
                break;
            case R.id.layout_eraser_iv:
                showPop(view, R.string.eraser);
                break;
        }
        return true;
    }

    private void showPop(View view, final int strRes) {
        int[] ints = new int[2];
        view.getLocationOnScreen(ints);
        int y = ints[1] - Utils.dp2px(this, 30);
        int x = ints[0];
        switch (view.getId()) {
            case R.id.layout_pen_postil_iv:
            case R.id.layout_clear_pro_iv:
            case R.id.layout_save_pro_iv:
            case R.id.layout_eraser_iv:
                int[] ints1 = new int[2];
                ((ViewGroup) view.getParent()).getLocationOnScreen(ints1);
                y = ints1[1] - Utils.dp2px(this, 30);
                break;
        }
        PopBuilder.createPopupWindow(this, R.layout.layout_tips, -2, -2, mParent, 0, x, y,
                new ClickListener() {
                    public void setUplistener(final PopBuilder builder) {
                        builder.setText(R.id.tips_tv,
                                getResources().getString(strRes));
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                builder.dismiss();
                            }
                        }, 2000);
                    }
                });
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mPoppupwindow != null && mPoppupwindow.isShowing()) {
            mPoppupwindow.dismiss();
            mPoppupwindow = null;
        }
        if (mQrcodePopWindow != null && mQrcodePopWindow.isShowing()) {
            mQrcodePopWindow.dismiss();
            mQrcodePopWindow = null;
        }
    }
}
