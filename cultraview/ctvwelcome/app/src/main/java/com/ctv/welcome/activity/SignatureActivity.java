
package com.ctv.welcome.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
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
import com.chad.library.adapter.base.BaseQuickAdapter.OnRecyclerViewItemClickListener;
import com.ctv.welcome.adapter.TextColorAdapter;
import com.ctv.welcome.adapter.TypeFaceAdapter;
import com.ctv.welcome.constant.Config;
import com.ctv.welcome.decorator.SpacesItemDecoration;
import com.ctv.welcome.task.ThreadManager;
import com.ctv.welcome.util.DBUtil;
import com.ctv.welcome.util.FileUtil;
import com.ctv.welcome.util.FileUtils;
import com.ctv.welcome.util.HockUtil;
import com.ctv.welcome.util.LogUtils;
import com.ctv.welcome.util.PopUtils.ClickListener;
import com.ctv.welcome.util.PopUtils.PopBuilder;
import com.ctv.welcome.util.ProgressDialogUtil;
import com.ctv.welcome.util.ToastUtils;
import com.ctv.welcome.util.UploadUtil;
import com.ctv.welcome.util.Utils;
import com.ctv.welcome.view.RichEditor;
import com.ctv.welcome.view.RichEditor.OnLocationListener;
import com.ctv.welcome.view.RichEditor.OnTextChangeListener;
import com.ctv.welcome.view.customview.ColorProgress;
import com.ctv.welcome.view.customview.QRCode;
import com.ctv.welcome.view.customview.Signature;
import com.ctv.welcome.view.customview.Signature.OnSigningListener;
import com.ctv.welcome.view.customview.SubsectionProgress;
import com.ctv.welcome.view.customview.SubsectionProgress.OnChangeListener;
import com.ctv.welcome.vo.RichEditorInfo;
import com.squareup.picasso.Picasso;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLDownloadTask;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.cos.xml.transfer.TransferState;
import com.tencent.cos.xml.transfer.TransferStateListener;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

public class SignatureActivity extends Activity implements OnClickListener, OnLongClickListener {
    private static final String TAG = "SignatureActivity";

    public static String mSaveFileName;

    private String QRPATH = "/QRCode/";

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

    private ImageView imgReturn;

    public boolean invoke = false;

    public boolean isCheckedTC = false;

    public boolean isCheckedTP;

    public boolean isCheckedTS;

    private boolean isEdited = false;

    private boolean isFirstEdit = true;

    boolean isFirstLoad = false;

    boolean isSigned = false;

    boolean isSigning;

    private ImageView mClear;

    private LinearLayout mCodePicRlt;

    private TextView mCodeText;

    private RecyclerView mColorRecyclerView;

    private SignatureActivity mContext;

    private String mEditImageUrl;

    private ArrayList<RichEditorInfo> mEditorInfoList;

    private List<RichEditor> mEditorList;

    private ImageView mEraser;

    private RelativeLayout mParent;

    private ImageView mPenProperty;

    private LinearLayout mPenPropertyCon;

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

    private Signature mSignature;

    private ArrayList mSizeList;

    private ImageButton mTextColor;

    private TextColorAdapter mTextColorAdapter;

    public ImageView mTextSize;

    private LinearLayout mToolContainer;

    public ImageView mTypeFace;

    private int mWindowHeight;

    private int mWindowWidth;

    public ImageView mainImage;

    public String px;

    private ImageView save;

    private boolean saveSuccess = false;

    private String serverURL;

    boolean signStatus = false;

    String[] strs = new String[] {
            "arial", "time_new_roman", "georgia", "song_ti", "hei_ti", "fang_song", "kai_ti"
    };

    final String[] textSizeStr = new String[] {
            "40", "60", "80", "100", "120", "140", "160", "180", "200", "220", "240", "260", "280",
            "300"
    };

    private TypeFaceAdapter typeFaceAdapter;

    private String[] typeFaceArr;

    private boolean type_string;
    private CosXmlService cosXmlService;
    private TransferManager transferManager;
    private ShortTimeCredentialProvider credentialProvider;
    private final static int ERROR_SAVE_FILE = 1001;
    private final static int ERROR_UPLOAD_FILE = 1002;
    private final static int SUCESS_SAVE_FILE = 1003;
    private final static int SUCESS_UPLOAD_FILE = 1004;
    private final static int SHOW_QRCODE_VIEW = 1005;
    private final static int SAVE_END = 1006;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ERROR_SAVE_FILE:
                    saveSuccess = false;
                    Toast.makeText(SignatureActivity.this, R.string.save_signature_fail, Toast.LENGTH_SHORT)
                            .show();
                    handler.sendEmptyMessage(SAVE_END);
                    break;
                case ERROR_UPLOAD_FILE:
                    Toast.makeText(SignatureActivity.this, R.string.signature_fail, Toast.LENGTH_SHORT)
                            .show();
                    mQrcodeCon.setVisibility(View.GONE);
                    Log.d("chen","ERROR_UPLOAD_FILE:mQrcodeCon:GONE");
                    hideQrcodePopWindow();
                    handler.sendEmptyMessage(SAVE_END);
                    break;
                case SUCESS_UPLOAD_FILE:
                    String mSignatureUrl = (String)msg.obj;
                    handler.sendEmptyMessage(SAVE_END);
                    if (SignatureActivity.this.generateQRCode(null,
                            null,
                            mSignatureUrl, false)) {
                        mQrcodeCon.setVisibility(View.VISIBLE);
                        Log.d("chen","SUCESS_UPLOAD_FILE:mQrcodeCon:VISIBLE");
                        showQRCodePopWindow();
                        Toast.makeText(SignatureActivity.this,
                                R.string.signature_success, Toast.LENGTH_SHORT).show();
                    }

                    break;
                case SUCESS_SAVE_FILE:
                    SignatureActivity.this.saveSuccess = true;
                    SignatureActivity.this.isSigned = false;
                    Toast.makeText(SignatureActivity.this, R.string.save_signature_success,
                            Toast.LENGTH_SHORT).show();
                    break;
                case SHOW_QRCODE_VIEW:

                    break;
                case SAVE_END:
                    SignatureActivity.this.showSignatureUI();
                    ProgressDialogUtil.dimissProgressDialog();
                    break;
            }
        }
    };
    /* renamed from: com.ctv.welcome.activity.SignatureActivity$7 */
    class AnonymousClass7 implements OnClickListener {
        final/* synthetic */AlertDialog val$dialog;

        AnonymousClass7(AlertDialog alertDialog) {
            this.val$dialog = alertDialog;
        }

        public void onClick(View view) {
            this.val$dialog.dismiss();
            SignatureActivity.this.finish();
        }
    }

    /* renamed from: com.ctv.welcome.activity.SignatureActivity$8 */
    class AnonymousClass8 implements OnClickListener {
        final/* synthetic */AlertDialog val$dialog;

        AnonymousClass8(AlertDialog alertDialog) {
            this.val$dialog = alertDialog;
        }

        public void onClick(View view) {
            this.val$dialog.dismiss();
            SignatureActivity.this.exitSaveSignature();
        }
    }

    class RichEditorLocationListener implements OnLocationListener {
        private int position;

        public RichEditorLocationListener(int position) {
            this.position = position;
        }

        public void onLocation(float x, float y) {
            ((RichEditorInfo) SignatureActivity.this.mEditorInfoList.get(this.position)).setX(x);
            ((RichEditorInfo) SignatureActivity.this.mEditorInfoList.get(this.position)).setY(y);
            LogUtils.d("EditActivity,richEditor location pos:" + this.position + ",x:" + x + ",y:"
                    + y);
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
            SignatureActivity.this.isEdited = true;
            SignatureActivity.this.isFirstEdit = false;
            ((RichEditorInfo) SignatureActivity.this.mEditorInfoList.get(this.position))
                    .setHtmlText(text);
            LogUtils.i("EditActivity,richEditor pos:" + this.position + "onTextChange:" + text);
            if (this.richEditor != null) {
                this.richEditor.evaluateJavascript("getWidth()", new ValueCallback<String>() {
                    public void onReceiveValue(String value) {
                    }
                });
            }
            int width = SignatureActivity.this.mParent.getWidth();
        }
    }

    protected void initVariable() {
        this.mContext = this;
        this.mEditorList = new ArrayList();
        this.mEditorInfoList = new ArrayList();
        this.typeFaceArr = new String[] {
                "Arial", "Time New Roman", "Georgia",
                getResources().getString(R.string.song_typeface),
                getResources().getString(R.string.boldface),
                getResources().getString(R.string.imitation_song_dynasty_style_typeface),
                getResources().getString(R.string.regular_script)
        };
        Drawable red = getResources().getDrawable(R.drawable.red);
        Drawable orange = getResources().getDrawable(R.drawable.orange);
        Drawable green = getResources().getDrawable(R.drawable.green);
        Drawable blue = getResources().getDrawable(R.drawable.blue);
        Drawable yellow = getResources().getDrawable(R.drawable.yellow);
        Drawable blue_dark = getResources().getDrawable(R.drawable.blue_dark);
        Drawable purple = getResources().getDrawable(R.drawable.purple);
        Drawable black = getResources().getDrawable(R.drawable.black);
        Drawable white = getResources().getDrawable(R.drawable.white);
        this.drawables = new Drawable[] {
                red, orange, green, blue, yellow, blue_dark, purple, black, white
        };
    }

    protected void initListener() {
        this.cancel.setOnClickListener(this);
        this.save.setOnClickListener(this);
        this.edit.setOnClickListener(this);
        this.imgReturn.setOnClickListener(this);
        this.mTypeFace.setOnClickListener(this);
        this.mTextSize.setOnClickListener(this);
        this.mTextColor.setOnClickListener(this);
        this.mPostilCon.setOnClickListener(this);
        this.mPenProperty.setOnClickListener(this);
        this.mSavePostil.setOnClickListener(this);
        this.mPostil.setOnClickListener(this);
        this.mClear.setOnClickListener(this);
        this.mEraser.setOnClickListener(this);
        this.cancel.setOnLongClickListener(this);
        this.save.setOnLongClickListener(this);
        this.edit.setOnLongClickListener(this);
        this.mTypeFace.setOnLongClickListener(this);
        this.mTextSize.setOnLongClickListener(this);
        this.mTextColor.setOnLongClickListener(this);
        this.mSavePostil.setOnLongClickListener(this);
        this.mPostil.setOnLongClickListener(this);
        this.mPostilCon.setOnLongClickListener(this);
        this.mClear.setOnLongClickListener(this);
        this.mPenProperty.setOnLongClickListener(this);
        this.mEraser.setOnLongClickListener(this);
        this.mProgressText.setOnChangeListener(new OnChangeListener() {
            public void onChang(int textSize) {
                SignatureActivity.this.mSignature.setStrokeWidth(textSize);
            }
        });
        this.mProgressColor.setOnChangeListener(new ColorProgress.OnChangeListener() {
            public void onChang(String color) {
                SignatureActivity.this.mSignature.setColor(Color.parseColor(color));
            }
        });
        this.mSignature.setOnSigningListener(new OnSigningListener() {
            public void onSigning(boolean signing) {
                SignatureActivity.this.isSigned = signing;
            }
        });
        this.mRichEditorCon.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                for (int i = 0; i < SignatureActivity.this.mEditorList.size(); i++) {
                    SignatureActivity.this
                            .exitEditStatus((RichEditor) SignatureActivity.this.mEditorList.get(i));
                }
            }
        });
    }

    private void initScreenSize() {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        this.mWindowWidth = mDisplayMetrics.widthPixels;
        this.mWindowHeight = mDisplayMetrics.heightPixels;
    }

    protected void changeWindowSize(boolean isWindowMode) {
        int imgWidth;
        int imgHeight;
        View child = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        LayoutParams params = (LayoutParams) child.getLayoutParams();
        if (isWindowMode) {
            imgWidth = (int) (((float) this.mWindowWidth) * 0.8f);
            imgHeight = (int) (((float) this.mWindowHeight) * 0.8f);
        } else {
            imgWidth = this.mWindowWidth;
            imgHeight = this.mWindowHeight;
        }
        params.width = imgWidth;
        params.height = imgHeight;
        params.gravity = 17;
        child.setLayoutParams(params);
    }

    private void changeComponentSize() {
        if (IndexActivity.mIsWindowMode) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.imgReturn
                    .getLayoutParams();
            params.width = (int) getResources().getDimension(R.dimen.edit_cancel_width_window);
            params.height = (int) getResources().getDimension(R.dimen.edit_cancel_width_window);
            this.imgReturn.setLayoutParams(params);
            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) this.mPenProperty
                    .getLayoutParams();
            params1.width = (int) getResources().getDimension(R.dimen.edit_cancel_width_window);
            params1.height = (int) getResources().getDimension(R.dimen.edit_cancel_width_window);
            this.mPenProperty.setLayoutParams(params1);
            params1 = (LinearLayout.LayoutParams) this.mClear.getLayoutParams();
            params1.width = (int) getResources().getDimension(R.dimen.edit_cancel_width_window);
            params1.height = (int) getResources().getDimension(R.dimen.edit_cancel_width_window);
            this.mClear.setLayoutParams(params1);
            params1 = (LinearLayout.LayoutParams) this.mSavePostil.getLayoutParams();
            params1.width = (int) getResources().getDimension(R.dimen.edit_cancel_width_window);
            params1.height = (int) getResources().getDimension(R.dimen.edit_cancel_width_window);
            this.mSavePostil.setLayoutParams(params1);
            params1 = (LinearLayout.LayoutParams) this.mEraser.getLayoutParams();
            params1.width = (int) getResources().getDimension(R.dimen.edit_cancel_width_window);
            params1.height = (int) getResources().getDimension(R.dimen.edit_cancel_width_window);
            this.mEraser.setLayoutParams(params1);
            return;
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.imgReturn.getLayoutParams();
        params.width = -2;
        params.height = -2;
        this.imgReturn.setLayoutParams(params);
        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) this.mPenProperty.getLayoutParams();
        params1.width = -2;
        params1.height = -2;
        this.mPenProperty.setLayoutParams(params1);
        params1 = (LinearLayout.LayoutParams) this.mClear.getLayoutParams();
        params1.width = -2;
        params1.height = -2;
        this.mClear.setLayoutParams(params1);
        params1 = (LinearLayout.LayoutParams) this.mSavePostil.getLayoutParams();
        params1.width = -2;
        params1.height = -2;
        this.mSavePostil.setLayoutParams(params1);
        params1 = (LinearLayout.LayoutParams) this.mEraser.getLayoutParams();
        params1.width = -2;
        params1.height = -2;
        this.mEraser.setLayoutParams(params1);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(TAG, "-----onCreate-----");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_signature_lib);
        initScreenSize();
        HockUtil.hookWebView();
        this.mRichEditorCon = new RelativeLayout(this);
        this.mRichEditorCon.setVisibility(View.GONE);
        initView();
        initVariable();
        getData();
        initListener();
        this.isFirstLoad = true;
    }
    private void initCosService(String appid, String region, String signUrl) {
        credentialProvider = new ShortTimeCredentialProvider("AKIDEIIeu3WDbtAfzVg9680CEIE8sZlAGCvX",
                "qz62qfUPWVxGNmCzfAalkXXkqunVSFsu", 1000);
        CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(appid, region)
                .setDebuggable(true)
                .builder();

        cosXmlService = new CosXmlService(this, cosXmlServiceConfig, credentialProvider);

        TransferConfig transferConfig = new TransferConfig.Builder().build();
        transferManager = new TransferManager(cosXmlService, transferConfig);
    }
    public void onUploadClick(String file,String basepath) {

        String bucket = "upanddownpic-1300568896";
        String cosPath = file;
        String localPath = basepath + cosPath;
        upload(bucket, cosPath, localPath);
    }

    /**
     * 上传
     *
     * @params bucket  bucket 名称
     * @params cosPath 上传到 COS 的路径
     * @params localPath 本地文件路径
     */
    public void upload(String bucket, String cosPath, String localPath) {

        // 开始上传，并返回生成的 COSXMLUploadTask
        Log.d("chen","cosPath:"+ cosPath);
        Log.d("chen","localPath:"+ localPath);
        cosPath = "upload/" + cosPath;
        Log.d("chen","cosPath:"+ cosPath);
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, cosPath,
                localPath, null);

        // 设置上传状态监听
        cosxmlUploadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(final TransferState state) {
                // TODO: 2018/10/22
            }
        });

        // 设置上传进度监听
        cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(final long complete, final long target) {
                // TODO: 2018/10/22
            }
        });

        // 设置结果监听
        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                // TODO: 2018/10/22
                String url = cosXmlService.getAccessUrl(request);
                Log.d("chen","url:"+url);
                Message msg = new Message();
                msg.obj = url;
                msg.what = SUCESS_UPLOAD_FILE;
                handler.sendMessageDelayed(msg,1200);
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                // TODO: 2018/10/22
                //Log.d("chen","onFail:"+ serviceException.getMessage());
                handler.sendEmptyMessageDelayed(ERROR_UPLOAD_FILE,1200);
            }
        });
    }

    public void onDownloadClick(View view) {

        String bucket = "rickenwang-1252386093";
        String cosPath = "10Mfile.txt";
        String localDirPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        download(bucket, cosPath, localDirPath);
    }

    public void download(String bucket, String cosPath, String localDirPath) {

        // 开始下载，并返回生成的 COSXMLDownloadTask
        COSXMLDownloadTask cosxmlDownloadTask = transferManager.download(this, bucket, cosPath,
                localDirPath);

        // 设置下载状态监听
        cosxmlDownloadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(final TransferState state) {
                // TODO: 2018/10/22
            }
        });

        // 设置下载进度监听
        cosxmlDownloadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(final long complete, final long target) {
                // TODO: 2018/10/22
            }
        });

        // 设置下载结果监听
        cosxmlDownloadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                // TODO: 2018/10/22
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                // TODO: 2018/10/22
            }
        });
    }

    protected void onResume() {
        super.onResume();
        float touchAreaJudge = DBUtil.getTouchAreaJudge();
        LogUtils.i(TAG, "-----onResume,touchAreaJudge:-----" + touchAreaJudge);
        this.mSignature.setTouchAreaJudge(touchAreaJudge);
    }

    private void createSingleRichEditor() {
        this.mRichEditorCon.setVisibility(View.VISIBLE);
        RichEditorInfo info = new RichEditorInfo();
        RichEditor richEditor = new RichEditor(this);
        richEditor.setMinimumWidth(220);
        richEditor.setEditorBackgroundColor(0);
        showRichEditor(richEditor);
        richEditor.setX(100.0f);
        richEditor.setY(100.0f);
        info.setX(100.0f);
        info.setY(100.0f);
        this.mRichEditorCon.addView(richEditor, new RelativeLayout.LayoutParams(-2, -2));
        this.mEditorList.add(richEditor);
        this.mEditorInfoList.add(info);
        initRichEditorListener(richEditor, this.mEditorList.size() - 1);
    }

    private void createRichEditor(List<RichEditorInfo> infoList) {
        this.mRichEditorCon.setVisibility(View.VISIBLE);
        for (int i = 0; i < infoList.size(); i++) {
            RichEditorInfo info = (RichEditorInfo) infoList.get(i);
            RichEditor richEditor = new RichEditor(this);
            richEditor.setClickable(false);
            richEditor.setMinimumWidth(220);
            richEditor.setEditorBackgroundColor(0);
            richEditor.setVisibility(View.VISIBLE);
            richEditor.setHtml(((RichEditorInfo) infoList.get(i)).getHtmlText());
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
            this.mRichEditorCon.addView(richEditor, new RelativeLayout.LayoutParams(-2, -2));
            this.mEditorList.add(richEditor);
        }
    }

    private void initRichEditorListener(final RichEditor richEditor, final int position) {
        richEditor.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                LogUtils.d("EditActivity,richEditor click,change:" + SignatureActivity.this.change);
                if (richEditor.change) {
                    InputMethodManager imm = (InputMethodManager) SignatureActivity.this
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    SignatureActivity.this.exitEditStatus(richEditor);
                }
            }
        });
        richEditor.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                        LogUtils.d("EditActivity,richEditor pos:" + position + " touch");
                        for (int i = 0; i < SignatureActivity.this.mEditorList.size(); i++) {
                            if (i == position) {
                                SignatureActivity.this.showRichEditor(richEditor);
                            } else {
                                SignatureActivity.this
                                        .exitEditStatus((RichEditor) SignatureActivity.this.mEditorList
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
        this.mainImage = (ImageView) findViewById(R.id.main_image);
        this.cancel = (ImageView) findViewById(R.id.act_et_cancel_iv);
        this.save = (ImageView) findViewById(R.id.act_et_save_iv);
        this.edit = (ImageView) findViewById(R.id.act_et_edit_iv);
        this.imgReturn = (ImageView) findViewById(R.id.img_return);
        this.mTypeFace = (ImageView) findViewById(R.id.typeface_iv);
        this.mTextSize = (ImageView) findViewById(R.id.textsize_iv);
        this.mTextColor = (ImageButton) findViewById(R.id.textcolor_ib);
        this.mToolContainer = (LinearLayout) findViewById(R.id.container_llt);
        this.mRadioGroupControl = (LinearLayout) findViewById(R.id.container_control_llt);
        this.mRecyclerView = (RecyclerView) findViewById(R.id.tool_rv);
        this.mColorRecyclerView = (RecyclerView) findViewById(R.id.tool_color_rv);
        this.mParent = (RelativeLayout) findViewById(R.id.act_et_container_rlt);
        this.mPostil = (ImageView) findViewById(R.id.act_et_postil_iv);
        this.mPenProperty = (ImageView) findViewById(R.id.layout_pen_postil_iv);
        this.mSavePostil = (ImageView) findViewById(R.id.layout_save_pro_iv);
        this.mPenPropertyCon = (LinearLayout) findViewById(R.id.pen_property_container);
        this.mPostilCon = (LinearLayout) findViewById(R.id.postil_container);
        this.mSignature = (Signature) findViewById(R.id.signaturePad);
        this.mProgressText = (SubsectionProgress) findViewById(R.id.progress_text);
        this.mProgressColor = (ColorProgress) findViewById(R.id.progress_color);
        this.mRichEditorCon.setClickable(false);
        this.mClear = (ImageView) findViewById(R.id.layout_clear_pro_iv);
        this.mEraser = (ImageView) findViewById(R.id.layout_eraser_iv);
        initQRCodePopWindow();
    }

    private void getData() {
        this.filePath = getIntent().getStringExtra("IMAGE_PATH");
        this.iconFilePath = getIntent().getIntExtra("IMAGE_PATH_ICON", -1);
        this.custom = getIntent().getBooleanExtra("CUSTOM", false);
        String qrpath = getIntent().getStringExtra("QRPATH");
        this.mEditorInfoList = getIntent().getParcelableArrayListExtra("RICHEDITOR_LIST");
        this.mQrcodeCon.setVisibility(View.GONE);
        Log.d("chen","getData:mQrcodeCon:GONE");
        if (this.custom) {
            if (this.mEditorInfoList != null && this.mEditorInfoList.size() > 0) {
                createRichEditor(this.mEditorInfoList);
            }
        } else if (this.mEditorInfoList != null && this.mEditorInfoList.size() > 0) {
            createRichEditor(this.mEditorInfoList);
        }
        if (!TextUtils.isEmpty(this.filePath)) {
            this.type_string = true;
            if (!TextUtils.isEmpty(this.filePath)) {
                loadImage(this.filePath);
            }
        }
        if (this.iconFilePath != -1) {
            loadImage(this.iconFilePath);
        }
        this.mSizeList = new ArrayList();
        Collections.addAll(this.mSizeList, this.strs);
        this.mSignature.setPenConGone(this.mPenPropertyCon);
        this.mainImage.setBackground(new BitmapDrawable(EditActivity.mCustomSignatureBg));
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
            case R.id.act_et_postil_iv:
                enterSignatureStatus();
                return;
            case R.id.img_return:
                backEditActivity();
                return;
            case R.id.layout_pen_postil_iv:
                this.mSignature.setMode(0);
                hideOrShowPenProperty();
                return;
            case R.id.layout_clear_pro_iv:
                clearSignature();
                return;
            case R.id.layout_save_pro_iv:
                saveSignature();
                return;
            case R.id.layout_eraser_iv:
                this.mSignature.setMode(1);
                return;
            default:
                return;
        }
    }

    private void backEditActivity() {
        finish();
    }

    private void exitSaveSignature() {
        ProgressDialogUtil.show((Context) this, (int) R.string.saving_sign);
        hideSignatureUI();
        String storeType = DBUtil.getPicStoreType();
        LogUtils.d("saveSignature,storeType:" + storeType);
        if (storeType.startsWith(Config.INNER_STOREAGE_PATH)) {
            saveExitSignatureContent(FileUtil.getBaseStorageDir(storeType));
        } else if (storeType.startsWith("/mnt/usb")) {
            if (FileUtils.isUsbPurgeIn(storeType)) {
                saveExitSignatureContent(FileUtil.getBaseStorageDir(storeType));
                return;
            }
            Toast.makeText(this.mContext, R.string.usb_not_insert, Toast.LENGTH_LONG).show();
            showSignatureUI();
            ProgressDialogUtil.dimissProgressDialog();
            finish();
        } else if (FileUtil.getSecondaryStorageState().equals("mounted")) {
            saveSignatureContent(FileUtil.getBaseStorageDir(storeType));
        } else {
            Toast.makeText(this.mContext, R.string.no_sd_card, Toast.LENGTH_LONG).show();
            showSignatureUI();
            ProgressDialogUtil.dimissProgressDialog();
            finish();
        }
    }

    private void exitEdit() {
        if (this.saveSuccess) {
            finish();
            EventBus.getDefault().post("refresh");
        }
        if (this.isEdited) {
            Builder builder = new Builder(this);
            builder.setMessage(R.string.image_no_save);
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    SignatureActivity.this.finish();
                    EventBus.getDefault().post("refresh:signature");
                }
            });
            builder.show();
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

    private void clearSignature() {
        if (this.isSigned) {
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.dialog_save_signature_hint, null);
            final AlertDialog dialog = new Builder(this, R.style.myDialog).create();
            TextView no = (TextView) view.findViewById(R.id.txt_save_signature_no);
            TextView yes = (TextView) view.findViewById(R.id.txt_save_signature_yes);
            ((TextView) view.findViewById(R.id.txt_save_signature_message))
                    .setText(R.string.signature_no_save);
            no.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            yes.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    dialog.dismiss();
                    if (SignatureActivity.this.mSignature != null) {
                        SignatureActivity.this.mSignature.clearPath();
                        SignatureActivity.this.isSigned = false;
                    }
                }
            });
            dialog.show();
            dialog.setContentView(view);
            dialog.getWindow().clearFlags(131072);
        } else if (this.mSignature != null) {
            this.mSignature.clearPath();
            this.isSigned = false;
        }
    }

    private void saveSignature() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestReadsd();
        }
        if (this.isSigned) {
            ProgressDialogUtil.show((Context) this, (int) R.string.saving_sign);
            hideSignatureUI();
            String storeType = DBUtil.getPicStoreType();
            LogUtils.d("saveSignature,storeType:" + storeType);
            if (storeType.startsWith(Config.INNER_STOREAGE_PATH)) {
                saveSignatureContent(FileUtil.getBaseStorageDir(storeType));
                return;
            } else if (storeType.startsWith("/mnt/usb")) {
                if (FileUtils.isUsbPurgeIn(storeType)) {
                    saveSignatureContent(FileUtil.getBaseStorageDir(storeType));
                    return;
                }
                Toast.makeText(this.mContext, R.string.usb_not_insert, Toast.LENGTH_LONG).show();
                showSignatureUI();
                ProgressDialogUtil.dimissProgressDialog();
                return;
            } else if (FileUtil.getSecondaryStorageState().equals("mounted")) {
                saveSignatureContent(FileUtil.getBaseStorageDir(storeType));
                return;
            } else {
                Toast.makeText(this.mContext, R.string.no_sd_card, Toast.LENGTH_LONG).show();
                showSignatureUI();
                ProgressDialogUtil.dimissProgressDialog();
                return;
            }
        }
        ToastUtils.showToast(this.mContext, R.string.sign_first);
    }

    private void hideSignatureUI() {
        this.imgReturn.setVisibility(View.GONE);
        this.mSavePostil.setVisibility(View.GONE);
        this.mPenProperty.setVisibility(View.GONE);
        this.mPenPropertyCon.setVisibility(View.GONE);
        this.mClear.setVisibility(View.GONE);
        this.mEraser.setVisibility(View.GONE);
        if (this.mQrcodeCon.getVisibility() == View.VISIBLE) {
            this.mQrcodeCon.setVisibility(View.GONE);
            Log.d("chen","hideSignatureUI:mQrcodeCon:GONE");
        }
    }

    private void showSignatureUI() {
        this.imgReturn.setVisibility(View.VISIBLE);
        this.mSavePostil.setVisibility(View.VISIBLE);
        this.mPenProperty.setVisibility(View.VISIBLE);
        this.mClear.setVisibility(View.VISIBLE);
        this.mEraser.setVisibility(View.VISIBLE);
        this.mPenPropertyCon.setVisibility(View.GONE);
    }

    private void saveExitSignatureContent(final String baseDir) {
        ThreadManager.getThreadPoolProxy().excute(new Runnable() {
            private String fileName;

            private String mSignatureUrl;

            public void run() {
                this.fileName = FileUtil.saveSignature(baseDir,
                        FileUtil.takeScreenShot(SignatureActivity.this));
                if (!TextUtils.isEmpty(this.fileName)) {
                    this.mSignatureUrl = SignatureActivity.this.uploadFileToServer(baseDir
                            + Config.SIGNATURE_PICTURE + "/" + this.fileName);
                }else{

                }
                SignatureActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (TextUtils.isEmpty(fileName)) {
                            SignatureActivity.this.saveSuccess = false;
                        } else {
                            if (TextUtils.isEmpty(mSignatureUrl)) {
                              //  Toast.makeText(SignatureActivity.this, R.string.signature_fail, Toast.LENGTH_SHORT)
                              //          .show();
                                SignatureActivity.this.mQrcodeCon.setVisibility(View.GONE);
                                Log.d("chen","saveExitSignatureContent:mQrcodeCon:GONE");
                            } else {
                                if (SignatureActivity.this.generateQRCode(baseDir,
                                        fileName,
                                        mSignatureUrl, false)) {
                                    SignatureActivity.this.mQrcodeCon.setVisibility(View.VISIBLE);
                                    Log.d("chen","saveExitSignatureContent :mQrcodeCon:VISIBLE");
                                    Toast.makeText(SignatureActivity.this,
                                            R.string.signature_success, Toast.LENGTH_SHORT).show();
                                }
                                Toast.makeText(SignatureActivity.this,
                                        R.string.save_signature_success, Toast.LENGTH_SHORT).show();
                            }
                            SignatureActivity.this.saveSuccess = true;
                            SignatureActivity.this.isSigned = false;
                            Toast.makeText(SignatureActivity.this, R.string.save_signature_success,
                                    Toast.LENGTH_SHORT).show();
                        }
                        SignatureActivity.this.showSignatureUI();
                        ProgressDialogUtil.dimissProgressDialog();
                        SignatureActivity.this.finish();
                    }
                });
            }
        });
    }

    private void saveSignatureContent(final String baseDir) {
        ThreadManager.getThreadPoolProxy().excute(new Runnable() {
            private String fileName;

            private String mSignatureUrl;

            public void run() {
                fileName = FileUtil.saveSignature(baseDir,
                        FileUtil.takeScreenShot(SignatureActivity.this));
                if (!TextUtils.isEmpty(fileName)) {
                    initCosService("1300568896",
                            "ap-shenzhen-fsi", // bucket 的地域
                            "https://upanddownpic-1300568896.cos.ap-shenzhen-fsi.myqcloud.com/sign.json"); // 临时密钥服务地址
                    onUploadClick(fileName,baseDir
                            + Config.SIGNATURE_PICTURE + "/");
                    handler.sendEmptyMessage(SUCESS_SAVE_FILE);
                }else{
                    handler.sendEmptyMessage(ERROR_SAVE_FILE);
                }

              /*  runOnUiThread(new Runnable() {
                    public void run() {
                        if (TextUtils.isEmpty(fileName)) {

                        } else {
                            if (TextUtils.isEmpty(mSignatureUrl)) {

                            } else {
                                if (generateQRCode(baseDir,
                                        fileName,
                                        mSignatureUrl, true)) {
                                    SignatureActivity.this.showQRCodePopWindow();
                                    SignatureActivity.this.mQrcodeCon.setVisibility(View.VISIBLE);
                                    SignatureActivity.this.mCodeText.setText(R.string.code_text);
                                    Toast.makeText(SignatureActivity.this,
                                            R.string.signature_success, Toast.LENGTH_SHORT).show();
                                }
                                Toast.makeText(SignatureActivity.this,
                                        R.string.save_signature_success, Toast.LENGTH_SHORT).show();
                            }
                            SignatureActivity.this.saveSuccess = true;
                            SignatureActivity.this.isSigned = false;
                            Toast.makeText(SignatureActivity.this, R.string.save_signature_success,
                                    Toast.LENGTH_SHORT).show();
                        }
                        SignatureActivity.this.showSignatureUI();
                        ProgressDialogUtil.dimissProgressDialog();
                    }
                });*/
            }
        });
    }

    private void hideOrShowPenProperty() {
        if (this.mPenPropertyCon.getVisibility() == View.GONE) {
            this.mPenPropertyCon.setVisibility(View.VISIBLE);
        } else {
            this.mPenPropertyCon.setVisibility(View.GONE);
        }
    }

    private void enterSignatureStatus() {
        if (this.change) {
            Toast.makeText(this.mContext, R.string.exit_edit_first, Toast.LENGTH_SHORT).show();
            return;
        }
        this.signStatus = true;
        if (this.mRecyclerView.getVisibility() == View.VISIBLE) {
            this.mRecyclerView.setVisibility(View.GONE);
        }
        if (this.mColorRecyclerView.getVisibility() == View.VISIBLE) {
            this.mColorRecyclerView.setVisibility(View.GONE);
        }
        if (this.mPostilCon.getVisibility() == View.GONE) {
            this.mPostilCon.setVisibility(View.VISIBLE);
        } else {
            this.mPostilCon.setVisibility(View.GONE);
        }
        if (this.mSignature.getVisibility() == View.GONE) {
            setSignature(View.VISIBLE, R.drawable.postil_selected);
        } else {
            setSignature(View.GONE, R.drawable.postil);
            this.signStatus = false;
        }
        if (this.mToolContainer.getVisibility() == View.VISIBLE) {
            this.mToolContainer.setVisibility(View.GONE);
        }
        if (this.mPenPropertyCon.getVisibility() == View.VISIBLE) {
            this.mPenPropertyCon.setVisibility(View.GONE);
        }
    }


    private void setSignature(int visible, int postil_selected) {
        this.mSignature.setVisibility(visible);
        this.mPostil.setImageResource(postil_selected);
    }

    private void hideOrShowQrCodeCon() {
        if (this.mQrCode.getVisibility() == View.GONE || this.mQrCode.getVisibility() == View.INVISIBLE) {
            this.mQrCode.setVisibility(View.VISIBLE);
            this.mCodePicRlt.setVisibility(View.GONE);
            return;
        }
        this.mQrCode.setVisibility(View.GONE);
        this.mCodePicRlt.setVisibility(View.VISIBLE);
    }

    private void hideOrShowQrCode() {
        if (this.mCodePicRlt.getVisibility() == View.GONE || this.mCodePicRlt.getVisibility() == View.INVISIBLE) {
            this.mCodePicRlt.setVisibility(View.VISIBLE);
            this.mQrCode.setVisibility(View.GONE);
            return;
        }
        this.mCodePicRlt.setVisibility(View.GONE);
        this.mQrCode.setVisibility(View.VISIBLE);
    }

    private void changeTextColor() {
        this.isCheckedTC = !this.isCheckedTC;
        if (this.isCheckedTP) {
            this.mTypeFace.setImageResource(R.drawable.typeface_normal);
            this.mRecyclerView.setVisibility(View.GONE);
            this.isCheckedTP = false;
        }
        if (this.isCheckedTS) {
            this.mTextSize.setImageResource(R.drawable.textsize_normal);
            this.mRecyclerView.setVisibility(View.GONE);
            this.isCheckedTS = false;
        }
        if (this.isCheckedTC) {
            this.mColorRecyclerView.setVisibility(View.VISIBLE);
            this.mColorRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            if (this.mTextColorAdapter == null) {
                this.mTextColorAdapter = new TextColorAdapter(R.layout.item_textcolor,
                        Arrays.asList(this.drawables));
                this.mColorRecyclerView.addItemDecoration(new SpacesItemDecoration(5));
            }
            this.mTextColorAdapter
                    .setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
                        public void onItemClick(View view, int i) {
                            for (int j = 0; j < SignatureActivity.this.mEditorList.size(); j++) {
                                RichEditor editor = (RichEditor) SignatureActivity.this.mEditorList
                                        .get(j);
                                if (editor.change) {
                                    LogUtils.i("EditActivity,cur edit pos:" + j);
                                    editor.setTextColor(SignatureActivity.this.getResources()
                                            .getColor(SignatureActivity.this.colorArr[i]));
                                }
                            }
                            SignatureActivity.this.mColorRecyclerView.setVisibility(View.GONE);
                            SignatureActivity.this.mTextColor
                                    .setImageDrawable(SignatureActivity.this.drawables[i]);
                            SignatureActivity.this.mTextColor
                                    .setBackgroundResource(R.drawable.rb_normal_bg);
                        }
                    });
            this.mColorRecyclerView.setAdapter(this.mTextColorAdapter);
            return;
        }
        this.mColorRecyclerView.setVisibility(View.GONE);
    }

    private void changeTextSize() {
        boolean z;
        if (this.isCheckedTS) {
            z = false;
        } else {
            z = true;
        }
        this.isCheckedTS = z;
        if (this.isCheckedTP) {
            this.mTypeFace.setImageResource(R.drawable.typeface_normal);
            this.mRecyclerView.setVisibility(View.GONE);
            this.isCheckedTP = false;
        }
        if (this.isCheckedTC) {
            this.mColorRecyclerView.setVisibility(View.GONE);
            this.isCheckedTC = false;
        }
        if (this.isCheckedTS) {
            this.mRecyclerView.setVisibility(View.VISIBLE);
            this.mTextSize.setImageResource(R.drawable.textsiz_selected);
            this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            this.typeFaceAdapter = new TypeFaceAdapter(Arrays.asList(this.textSizeStr), this,
                    false, this.mRecyclerView);
            for (int i = 0; i < this.mEditorList.size(); i++) {
                RichEditor editor = (RichEditor) this.mEditorList.get(i);
                if (editor.change) {
                    LogUtils.i("EditActivity,change textSize cur focus pos:" + i);
                    this.typeFaceAdapter.handleTextSizeClick(editor, this.mTextSize,
                            (RichEditorInfo) this.mEditorInfoList.get(i));
                }
            }
            this.mRecyclerView.setAdapter(this.typeFaceAdapter);
            return;
        }
        this.mTextSize.setImageResource(R.drawable.textsize_normal);
        this.mRecyclerView.setVisibility(View.GONE);
    }

    private void changeTextType() {
        boolean z;
        if (this.isCheckedTP) {
            z = false;
        } else {
            z = true;
        }
        this.isCheckedTP = z;
        if (this.isCheckedTS) {
            this.mTextSize.setImageResource(R.drawable.textsize_normal);
            this.mRecyclerView.setVisibility(View.GONE);
            this.isCheckedTS = false;
        }
        if (this.isCheckedTC) {
            this.mColorRecyclerView.setVisibility(View.GONE);
            this.isCheckedTC = false;
        }
        if (this.isCheckedTP) {
            this.mRecyclerView.setVisibility(View.VISIBLE);
            this.mTypeFace.setImageResource(R.drawable.typeface_selected);
            this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            this.typeFaceAdapter = new TypeFaceAdapter(Arrays.asList(this.typeFaceArr), this, true,
                    this.mRecyclerView);
            for (int i = 0; i < this.mEditorList.size(); i++) {
                RichEditor editor = (RichEditor) this.mEditorList.get(i);
                if (editor.change) {
                    LogUtils.i("EditActivity,change textType cur focus pos:" + i);
                    this.typeFaceAdapter.handleTypeFaceClick(editor, this.mTypeFace,
                            (RichEditorInfo) this.mEditorInfoList.get(i));
                }
            }
            this.mRecyclerView.setAdapter(this.typeFaceAdapter);
            return;
        }
        this.mTypeFace.setImageResource(R.drawable.typeface_normal);
        this.mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void enterEditStatus() {
        if (this.signStatus) {
            Toast.makeText(this.mContext, R.string.exit_sign_first, Toast.LENGTH_SHORT).show();
            return;
        }
        this.mQrCode.setVisibility(View.GONE);
        this.mQrcodeCon.setVisibility(View.GONE);
        Log.d("chen","enterEditStatus:mQrcodeCon:GONE");
        createSingleRichEditor();
    }

    private void showRichEditor(RichEditor richEditor) {
        richEditor.change = true;
        richEditor.setEditorEnable(true);
        richEditor.setClickable(true);
        richEditor.setBorder("dashed #ccc 1px");
        this.mToolContainer.setVisibility(View.VISIBLE);
        this.mQrcodeCon.setVisibility(View.GONE);
        Log.d("chen","showRichEditor:mQrcodeCon:GONE");
    }

    private void exitEditStatus(RichEditor richEditor) {
        richEditor.change = false;
        richEditor.setEditorEnable(false);
        richEditor.setBorder("none");
        this.mToolContainer.setVisibility(View.GONE);
        if (this.mRecyclerView != null && this.mRecyclerView.getVisibility() == View.VISIBLE) {
            this.mRecyclerView.setVisibility(View.GONE);
        }
        if (this.mColorRecyclerView != null && this.mColorRecyclerView.getVisibility() == View.VISIBLE) {
            this.mColorRecyclerView.setVisibility(View.GONE);
        }
    }

    private String uploadFileToServer(String filePath) {
        if (isNetworkConnected(this)) {
            return upLoadImage(filePath);
        }
        runOnUiThread(new Runnable() {
            public void run() {
                Toast makeText = Toast.makeText(SignatureActivity.this.mContext,
                        R.string.network_not_connection, Toast.LENGTH_LONG);
            }
        });
        return null;
    }

    private void initQRCodePopWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_qrcode_pop, null);
        this.mQrcodeCon = (LinearLayout) view.findViewById(R.id.act_qrcode_cotainer_llt);
        this.mCodePicRlt = (LinearLayout) view.findViewById(R.id.act_code_pic_rlt);
        this.mQRCodeBig = (ImageView) view.findViewById(R.id.act_code_pic_iv);
        this.mCodeText = (TextView) view.findViewById(R.id.act_code_pic_tv);
        this.mQrCode = (ImageView) view.findViewById(R.id.act_ea_qrcode_iv);
        this.mQrcodePopWindow = new PopupWindow(view, -2, -2, true);
        this.mQrcodePopWindow.setFocusable(true);
        this.mQrcodePopWindow.setOutsideTouchable(true);
        this.mQrcodePopWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    private void showQRCodePopWindow() {
        if (this.mQrcodePopWindow != null && !this.mQrcodePopWindow.isShowing()) {
            this.mQrcodePopWindow.showAtLocation(this.mParent, 17, 0, 0);
        }
    }

    private void hideQrcodePopWindow() {
        if (this.mQrcodePopWindow != null && this.mQrcodePopWindow.isShowing()) {
            this.mQrcodePopWindow.dismiss();
        }
    }

    private boolean generateQRCode(String baseDir, String filename, String url, boolean saveQRcode) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        Log.d("chen","generateQRCode:"+url);
        Bitmap qrCode = QRCode.createQRCode(url);
        if (this.mQrcodeCon.getVisibility() == View.GONE) {
            this.mQrcodeCon.setVisibility(View.VISIBLE);
            Log.d("chen","generateQRCode:mQrcodeCon:VISIBLE");
        }
        this.mQRCodeBig.setImageBitmap(null);
        this.mQRCodeBig.setImageBitmap(qrCode);
        this.mCodePicRlt.setVisibility(View.VISIBLE);
        if (saveQRcode) {
            this.mCodeText.setText(R.string.scan_save_image);
        } else {
            this.mCodeText.setText(R.string.code_text);
        }
        return true;
    }

    private String upLoadImage(String path) {
        return UploadUtil.uploadFile(this.URL, new File(path));
    }

    public void loadImage(String filepath) {
        Picasso.with(this).load(new File(filepath)).fit().into(this.mainImage);
    }

    public void loadImage(int resId) {
        Picasso.with(this).load(resId).fit().into(this.mainImage);
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
            if (this.saveSuccess) {
                EventBus.getDefault().post("refresh:signature");
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
        PopBuilder.createPopupWindow(this, R.layout.layout_tips, -2, -2, this.mParent, 0, x, y,
                new ClickListener() {
                    public void setUplistener(final PopBuilder builder) {
                        builder.setText(R.id.tips_tv, SignatureActivity.this.getResources()
                                .getString(strRes));
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
        if (this.mQrcodePopWindow != null && this.mQrcodePopWindow.isShowing()) {
            this.mQrcodePopWindow.dismiss();
            this.mQrcodePopWindow = null;
        }
    }
}
