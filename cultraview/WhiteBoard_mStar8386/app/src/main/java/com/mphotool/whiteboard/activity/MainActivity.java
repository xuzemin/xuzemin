package com.mphotool.whiteboard.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ctv.Jni.WbJniCall;
import com.ctv.utils.SDKinfor;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.mphotool.whiteboard.BuildConfig;
import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.utils.NoteAdapter;
import com.mphotool.whiteboard.utils.Noteinfo;
import com.mphotool.whiteboard.utils.PanelUtils;
import com.mphotool.whiteboard.utils.SharedPreferencesUtils;
import com.mphotool.whiteboard.utils.StatusEnum;
import com.mphotool.whiteboard.utils.ToofifiLog;
import com.mphotool.whiteboard.utils.UploadManager;
import com.mphotool.whiteboard.utils.Utils;
import com.mphotool.whiteboard.view.BaseGLSurfaceView;
import com.mphotool.whiteboard.view.DrawBoard;
import com.mphotool.whiteboard.view.Page;
import com.mphotool.whiteboard.view.PanelManager;
import com.mphotool.whiteboard.view.PanelManager.OnActionChangedListener;
import com.mphotool.whiteboard.view.PanelManager.OnScaleChangedListener;
import com.mphotool.whiteboard.view.imgedit.ImageEditView;
import com.mphotool.whiteboard.view.layerviews.PenWidth;
import com.mphotool.whiteboard.view.menuviews.ColorItem;
import com.mphotool.whiteboard.view.menuviews.ColorPickerAdapter;
import com.mphotool.whiteboard.view.menuviews.ColorPickerView;
import com.mphotool.whiteboard.view.menuviews.PreviewAdapter;
import com.mphotool.whiteboard.view.menuviews.SlideButton;
import com.mphotool.whiteboard.view.menuviews.SwipeRecyclerView;
import com.mphotool.whiteboard.view.menuviews.dialog.ExitDialog;
import com.mphotool.whiteboard.view.menuviews.dialog.FileBrowserPopu;
import com.mphotool.whiteboard.view.menuviews.dialog.FileCountDialog;
import com.mphotool.whiteboard.view.menuviews.dialog.FileCoverDialog;
import com.mphotool.whiteboard.view.menuviews.dialog.QrDialog;
import com.mphotool.whiteboard.view.menuviews.dialog.SaveNoteDialog;
import com.mphotool.whiteboard.view.menuviews.dialog.SavePanelDialog;
import com.mphotool.whiteboard.view.menuviews.dialog.SettingDialog;
import com.mphotool.whiteboard.view.menuviews.dialog.SpotsDialog;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter.OnItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private static final String APP_DIR = TextUtils.isEmpty(BuildConfig.floder_name) ? "whiteboard" : BuildConfig.floder_name + "/whiteboard";
    private static final String SD_DIR = BaseUtils.getSDPATH();
    public static final String ROOT_DIR = (SD_DIR + "/" + APP_DIR);
    public static final String MRC_DIR = ROOT_DIR + "/mrc";
    public static final String RECORD_TMP_FILE = (ROOT_DIR + "/temp/");

    public static final int MSG_NOTE_RETURN_PEN = 0x100;
    public static final int MSG_RETURN_PEN = 0x200;
    public static final int MSG_SHOW_TRANSPARENT_DLG = 0x400;
    public static final int MSG_HIDE_TRANSPARENT_DLG = 0x500;

    private static final int REQUEST_FOLDER = 1;
    private static final int REQUEST_IMAGE = 2;
    private static final int REQUEST_NOTE = 3;

    public static int mSurfaceHeight = Constants.SCREEN_HEIGHT;
    public static int mSurfaceWidth = Constants.SCREEN_WIDTH;

    private String lastFileName = "";
    private String fullName = "";
    private boolean hasFileSaved = false;

    private ColorPickerView colorPickerView,bgcolorView;
    private View dashLine2;
    private View mAddPage;
    private PopupWindow mBackgroundColorWindow;
    private View mBtnClean;
    private int mCleanMode = 1;
    private RecyclerView mColorPicker;
    private View mDeletePage;

    private BaseGLSurfaceView mBoardGLSurfaceView;

    private DrawBoard mDrawBoard;
    private ImageEditView mImageEditView;
    private View mEraseLayout;
    private View mEraseMaterialButton;
    private View mEraseSizeButton;
    private SlideButton mEraseSlider;
    private PopupWindow mEraseWindow;
    private ExitDialog mExitDialog = null;
    private SettingDialog mSettingDialog = null;
    private View mMoreFunction;
    private View mMoreLayout,mMathLayout,mBackgroundLayout;
    private LinearLayout color_right;
    private View color_bg_line;
    private ImageView mBgcolor[],bg_shape[];

//    // 数学形状
    private ImageView circleShape,ovalShape,rectShape,equalTriShape,
            rightTriShape,pentagramShape,arrowShape,lineShape,cylinder;
    private PopupWindow mMoreWindow, mNoteWindow,mMathWindow;
    private View mNextPage;
    private int mPageIndex = 0;
    private TextView mPageIndexTextView;
    private PanelManager mPanelManager = null;
    private View mPenSelector;
    private View mPenSelectorLayout;
    private PenWidth mPenWidthPreview;
    private SeekBar mPenWidthSeekBar;
    private View redoBoard, undoBoard,mPrevPage,mBg_bt,math_icon,pen_brush,pen_paint;

    private TextView pen_str;

    private PreviewAdapter mPreviewAdapter = null;
    private ViewGroup mPreviewLayout;
    private SwipeRecyclerView mPreviewList;
    private View mRoot;
    private View mSaveButton;
    private SavePanelDialog mSavePanelDialog = null;
    private ViewGroup mScaleHint;
    private AlphaAnimation mScaleHintHideAnimation;
    private TextView mScaleText;
    private View mPointCrl;
    private View mSelection;
    private boolean mShowing = true;
    private ConnectivityManager mConnectivityManager;
    private ObjectAnimator objectAnimator;
    private TextView textViewOneKeyClear;
    private String toSavePath;
    private View viewPenColor;
    private boolean mSetSize = false;

    private View mTrasparentView;
    private WindowManager.LayoutParams mTrasparentParams;
    private int tag_proc_upevent = 0;
    public static int isTouching = 0;
    public static boolean isActivityRunning = false;
    public static BlockingQueue<Byte> up_event_queue = new ArrayBlockingQueue<>(24);
    private ProcUpEventThread procUpEventThread;

    /**
     * 记录上次保存的页数和元素数，在打开源文件时提示当前文件未保存
     */
    private int lastSavedPage = 1;
    private int lastSavedMaterial = 0;

    String deviceFlag = "";
    int workId = 1;
    int i = 0;
    boolean isQrCancel = false; //未上传完时如果退出窗口，中断上传并通知服务端删除图片
    String toDir = "empty";

    private LayoutInflater layoutInflater;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_NOTE_RETURN_PEN:
                    if (!WhiteBoardApplication.isTurnBackStatus)
                    {
                        return;
                    }
                    /**被选中状态下不切回画笔状态*/
                    if (mPanelManager != null && !mPanelManager.hasSelected())
                    {
                            setPanelMode(0);
                    }
                    if (mMoreWindow != null)
                    {
                        mMoreWindow.dismiss();
                    }
                    if (mEraseWindow != null)
                    {
                        mEraseWindow.dismiss();
                    }
                    if(mNoteWindow != null)
                    {
                        mNoteWindow.dismiss();
                    }
                    if(mMathWindow != null)
                    {
                        mMathWindow.dismiss();
                    }
                    mPenSelectorLayout.setVisibility(View.GONE);
                    break;
                case MSG_RETURN_PEN:
                    if (!WhiteBoardApplication.isTurnBackStatus)
                    {
                        return;
                    }
                    /**被选中状态下不切回画笔状态*/
                    if (mPanelManager != null && !mPanelManager.hasSelected())
                    {
                            setPanelMode(0);
                    }
                    if (mMoreWindow != null)
                    {
                        mMoreWindow.dismiss();
                    }
                    if (mEraseWindow != null)
                    {
                        mEraseWindow.dismiss();
                    }
                    if(mNoteWindow != null)
                    {
                        mNoteWindow.dismiss();
                    }
                    mPenSelectorLayout.setVisibility(View.GONE);
                    break;
                case MSG_SHOW_TRANSPARENT_DLG:
//                    BaseUtils.dbg(TAG, "handleMessage MSG_SHOW_TRANSPARENT_DLG");
                    showTransparentDlg();
                    break;
                case MSG_HIDE_TRANSPARENT_DLG:
//                    BaseUtils.dbg(TAG, "handleMessage MSG_HIDE_TRANSPARENT_DLG");
                    if(mTrasparentView != null)
                    {
                        mTrasparentParams.width = 1;
                        mTrasparentParams.height = 1;
                        getWindowManager().updateViewLayout(mTrasparentView, mTrasparentParams);
                    }
                    break;
            }

        }
    };

    public static void dbg(String msg)
    {
        BaseUtils.dbg(Constants.TAG_PAINT, msg);
    }

    private void error(String msg)
    {
        ToofifiLog.e(Constants.TAG_PAINT, msg);
    }

    private void setPenWidth(float width)
    {
        mPenWidthPreview.setPenWidth(0.7f * width);
        mPenWidthPreview.postInvalidate();
        mDrawBoard.setPenWidth(width);
    }

    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        WhiteBoardApplication.addActivity(this);

        setContentView(R.layout.activity_main);

        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        Point point = new Point();
        wm.getDefaultDisplay().getRealSize(point);
        int realw = point.x;
        int realh = point.y;
        boolean mFHD = false;
        if(android.os.Build.VERSION.SDK_INT >= 26) {
            String mHDConfig = Utils.getSystemProperties("persist.sys.ctv.4k2k","false");
            if (mHDConfig.equals("true"))
                mFHD = true;
            else
                mFHD = false;
        }
        if (mFHD) {
            Constants.SCREEN_WIDTH = 3840;
            Constants.SCREEN_HEIGHT = 2160;
        } else {
            Constants.SCREEN_WIDTH = realw;
            Constants.SCREEN_HEIGHT = realh;
        }

        BaseUtils.dbg(TAG, "realw=" + realw + " realh=" + realh);
        mRoot = findViewById(R.id.activity_main);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addDataScheme("file");

        // 初始化UI
        initView();
    }

    private void saveAsNote(final String mPath,final String fileName)
    {
        final AlertDialog dialog = new SpotsDialog(this, getResources().getString(R.string.saving));
        dialog.show();
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            public void call(Subscriber<? super Boolean> subscriber)
            {
                boolean success;
                File pFile = new File(mPath);
                if (pFile == null || !pFile.exists())
                {
                    pFile.mkdirs();
                }
                String path = mPath + "/" + fileName;

                try
                {
                    success = mPanelManager.saveNote(path);

                }
                catch (IOException e)
                {
                    error("save as note:" + BaseUtils.getStackTrace(e));
                    success = false;
                }
                StringBuilder sb = new StringBuilder();
                if(!path.substring(0,4).equals(Constants.PATH)) {
                    sb.append(getString(R.string.save_internal));
                    sb.append(path.substring(19,path.length()));
                }else {
                    if(path.length() >= 8 && path.substring(5,8).equals("usb")) {
                        sb.append(getString(R.string.save_external));
                        if(path.length() >= 18){
                            sb.append(path.substring(18, path.length()));
                        }else{
                            sb.append(path.substring(8, path.length()));
                        }
                    }else{
                        sb.append(getString(R.string.save_sd));
                        if(path.length() >= 27) {
                            sb.append(path.substring(27, path.length()));
                        }else{
                            sb.append(path.substring(17, path.length()));
                        }
                    }
                }
                toSavePath = sb.toString();
                subscriber.onStart();
                subscriber.onNext(Boolean.valueOf(success));
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            public void call(Boolean success)
            {
                dialog.dismiss();
                Context context = getApplicationContext();
                hasFileSaved = success;
                if (success)
                {
                    if (mPanelManager != null)
                    {
                        lastSavedPage = mPanelManager.getPageCount();
                        lastSavedMaterial = mPanelManager.getTotalMaterialsCount();
                        BaseUtils.dbg("save note success: lastSavedPage = " + lastSavedPage + ",  lastSavedMaterial = " + lastSavedMaterial);
                    }
                }
                String str = "%s %s: %s";
                Object[] objArr = new Object[3];
                objArr[0] = getString(R.string.save_board);
                objArr[1] = success.booleanValue() ? getString(R.string.success) : getString(R.string.failed);
                objArr[2] = toSavePath;
                Toast.makeText(context, String.format(str, objArr), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAsPicture(final String mPath,final String fileName)
    {
        final AlertDialog dialog = new SpotsDialog(this, getResources().getString(R.string.saving));
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override public void onDismiss(DialogInterface dialog)
            {
                BaseUtils.hideNavigationBar(MainActivity.this);
            }
        });
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            public void call(Subscriber<? super Boolean> subscriber)
            {
                String path = "";
                boolean success = true;
                int i = 0;
                while (i < mPanelManager.getPageCount())
                {
                    Bitmap bitmap = getPageBitmap(i);
                    if (bitmap == null)
                    {
                        success = false;
                        break;
                    }
                    try
                    {

                        path = mPath + "/" + fileName + "_"+ (i + 1) + ".png";
                        File pFile = new File(mPath);
                        if (pFile == null || !pFile.exists())
                        {
                            pFile.mkdirs();
                        }

                        File toFile = new File(path);
                        if (toFile == null || !toFile.exists())
                        {
                            toFile.createNewFile();
                        }
                        FileOutputStream out = new FileOutputStream(path);
//                        BaseUtils.dbg(TAG,"path=" + path);
                        bitmap.compress(CompressFormat.PNG, 100, out);
                        bitmap.recycle();
                    }
                    catch (FileNotFoundException e)
                    {
                        error("save to png:" + BaseUtils.getStackTrace(e));
                        success = false;
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        error("create png:" + BaseUtils.getStackTrace(e));
                    }
                    i++;
                }
                StringBuilder sb = new StringBuilder();
                if(!path.substring(0,4).equals(Constants.PATH)) {
                    sb.append(getString(R.string.save_internal));
                    sb.append(path.substring(19,path.length()));
                }else {
                    if(path.length() >= 8 && path.substring(5,8).equals("usb")) {
                        sb.append(getString(R.string.save_external));
                        if(path.length() >= 18){
                            sb.append(path.substring(18, path.length()));
                        }else{
                            sb.append(path.substring(8, path.length()));
                        }
                    }else{
                        sb.append(getString(R.string.save_sd));
                        if(path.length() >= 27) {
                            sb.append(path.substring(27, path.length()));
                        }else{
                            sb.append(path.substring(17, path.length()));
                        }
                    }
                }
                toSavePath = sb.toString();
                updateMediaContents(mPath);
                subscriber.onStart();
                subscriber.onNext(Boolean.valueOf(success));
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            public void call(Boolean success)
            {
                dialog.dismiss();
                Context context = getApplicationContext();
                hasFileSaved = success;
                String str = "%s %s: %s";
                Object[] objArr = new Object[3];
                objArr[0] = getString(R.string.save_board);
                objArr[1] = success.booleanValue() ? getString(R.string.success) : getString(R.string.failed);
                objArr[2] = toSavePath;
                error("save to png:" + toSavePath);
                Toast.makeText(context, String.format(str, objArr), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAllAsPdf(final String mPath,final String fileName)
    {
        final AlertDialog dialog = new SpotsDialog(this, getResources().getString(R.string.saving));
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override public void onDismiss(DialogInterface dialog)
            {
                BaseUtils.hideNavigationBar(MainActivity.this);
            }
        });
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            public void call(Subscriber<? super Boolean> subscriber)
            {
                List<String> files = new ArrayList();
                String path = "";
                File pFile = new File(mPath);
                if (pFile == null || !pFile.exists())
                {
                    pFile.mkdirs();
                }
                File rFile = new File(RECORD_TMP_FILE);
                if (rFile == null || !rFile.exists())
                {
                    rFile.mkdirs();
                }

                boolean success = true;
                int i = 0;
                while (i < mPanelManager.getPageCount())
                {
                    Bitmap bitmap = getPageBitmap(i);
                    if (bitmap == null)
                    {
                        success = false;
                        break;
                    }
                    try
                    {
                        path = RECORD_TMP_FILE + "/p" + i + ".png";
                        File jpgFile = new File(path);
                        jpgFile.getParentFile().mkdirs();
                        if (!jpgFile.exists())
                        {
                            jpgFile.createNewFile();
                        }
                        bitmap.compress(CompressFormat.PNG, 100, new FileOutputStream(path));
                        files.add(path);
                        bitmap.recycle();
                        i++;
                    }
                    catch (FileNotFoundException e)
                    {
                        error("save to png:" + BaseUtils.getStackTrace(e));
                        success = false;
                        i++;
                    }
                    catch (IOException e)
                    {
                        error("save to png:" + BaseUtils.getStackTrace(e));
                        success = false;
                        i++;
                    }
                }
                path = mPath + "/" + fileName;
                String toPdf = path;
                if (success)
                {
                    new File(toPdf).getParentFile().mkdirs();
                    try
                    {
                        success = saveAsPdf(files, toPdf);
                    }
                    catch (DocumentException e2)
                    {
                        success = false;
                        error("save as pdf:" + BaseUtils.getStackTrace(e2));
                    }
                    catch (IOException e3)
                    {
                        success = false;
                        error("save as pdf:" + BaseUtils.getStackTrace(e3));
                    }
                    BaseUtils.deleteFloder(RECORD_TMP_FILE);
                }

                StringBuilder sb = new StringBuilder();
                if(!path.substring(0,4).equals(Constants.PATH)) {
                    sb.append(getString(R.string.save_internal));
                    sb.append(path.substring(19,path.length()));
                }else {
                    if(path.length() >= 8 && path.substring(5,8).equals("usb")) {
                        sb.append(getString(R.string.save_external));
                        if(path.length() >= 18){
                            sb.append(path.substring(18, path.length()));
                        }else{
                            sb.append(path.substring(8, path.length()));
                        }
                    }else{
                        sb.append(getString(R.string.save_sd));
                        if(path.length() >= 27) {
                            sb.append(path.substring(27, path.length()));
                        }else{
                            sb.append(path.substring(17, path.length()));
                        }
                    }
                }
                toSavePath = sb.toString();
                updateMediaContents(toPdf);
                subscriber.onStart();
                subscriber.onNext(Boolean.valueOf(success));
                error("save to pdf:" + toSavePath);
                subscriber.onCompleted();

            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            public void call(Boolean success)
            {
                dialog.dismiss();
                Context context = getApplicationContext();
                hasFileSaved = success;
                String str = "%s %s: %s";
                Object[] objArr = new Object[3];
                objArr[0] = getString(R.string.save_board);
                objArr[1] = success.booleanValue() ? getString(R.string.success) : getString(R.string.failed);
                objArr[2] = toSavePath;
                Toast.makeText(context, String.format(str, objArr), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean saveAsPdf(List<String> files, String pdf) throws DocumentException, IOException
    {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdf));
        document.open();
        Paragraph blank = new Paragraph(" ");
        for (String path : files)
        {
            if (!new File(path).exists())
            {
                BaseUtils.dbg(TAG, "saveAsPdf get png failed, file not exist : " + path);
                document.close();
                return false;
            }
            Image img = Image.getInstance(path);
            if (img == null)
            {
                continue;
            }
            img.scalePercent(((((document.getPageSize().getWidth() - document.leftMargin()) - document.rightMargin()) - 0.0f) / img.getWidth()) * SensorManager.LIGHT_CLOUDY);
            img.setAlignment(5);
            document.add(img);
            document.add(blank);
        }
        document.close();
        return true;
    }

    protected void onResume()
    {
        BaseUtils.dbg(TAG,"onResume");
        BaseUtils.hideNavigationBar(this);
        super.onResume();
        if (mTrasparentView == null)
        {
            mTrasparentParams = new WindowManager.LayoutParams();
            mTrasparentParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            mTrasparentParams.format = PixelFormat.RGBA_8888;
            mTrasparentParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

            mTrasparentParams.gravity = Gravity.LEFT | Gravity.TOP;
            mTrasparentParams.x = 0;
            mTrasparentParams.y = 0;
            mTrasparentParams.width = 1;
            mTrasparentParams.height = 1;

            LayoutInflater inflater = LayoutInflater.from(getApplication());
            mTrasparentView = inflater.inflate(R.layout.transparent_dialog, null);
            getWindowManager().addView(mTrasparentView, mTrasparentParams);

            BaseUtils.dbg(TAG,"onResume mTrasparentView create and added !!");
        }else{
            BaseUtils.dbg(TAG,"onResume mTrasparentView is not null !");
        }

        isActivityRunning = true;
        mShowing = true;

    }

    @Override protected void onPause()
    {
        super.onPause();
        BaseUtils.dbg(TAG, "onPause");
        isActivityRunning = false;

        if (mTrasparentView != null)
        {
            getWindowManager().removeView(mTrasparentView);
            mTrasparentView = null;
        }
    }

    protected void onStop()
    {
        super.onStop();
        mShowing = false;

    }

    protected void onDestroy()
    {
        super.onDestroy();
        //        EventBus.getDefault().unregister(this);

        if (hasFileSaved)
        {
            int fileCount = (int) SharedPreferencesUtils.getParam("file_count", 1);
            fileCount++;
            SharedPreferencesUtils.setParam("file_count", fileCount);
        }
        mDrawBoard.mPanelView.destory();
        tag_proc_upevent = 0;
        if(procUpEventThread != null){
            procUpEventThread.interrupt();
            procUpEventThread = null;
        }

        while (up_event_queue.size() > 0){
            up_event_queue.clear();
        }

        if (mExitDialog != null)
        {
            mExitDialog.dismiss();
            mExitDialog = null;
        }

        if (BuildConfig.is_accelerate)
        {
            WbJniCall.fbStop();
        }

        BaseUtils.deleteFloder(RECORD_TMP_FILE);
        WhiteBoardApplication.removeActivity(this);
    }

    @Override public void onBackPressed()
    {
//        super.onBackPressed();
        showExitDialog();
    }

    private void setPanelMode(int mode)
    {
        mDrawBoard.mPanelView.mPanelManager.setMode(mode);
        mPenSelector.setActivated(false);
        mBtnClean.setActivated(false);
        mSelection.setActivated(false);
        math_icon.setActivated(false);
        switch (mode)
        {
            case 0:
            case 5:
                mPenSelector.setActivated(true);
                return;
            case 1:
            case 2:
                mBtnClean.setActivated(true);
                return;
            case 3:
                mSelection.setActivated(true);
                return;
            case 4: // 形状
                math_icon.setActivated(true);
                return;
            default:
                return;
        }
    }

    private void setPenSizeView(View[] mView, int position){
        final float[] PenSize={Constants.PEN_WIDTH_LITTLE,Constants.PEN_WIDTH_MIDDLE
        ,Constants.PEN_WIDTH_LARGE,Constants.PEN_WIDTH_OVERBIG};
        for(int i=0;i<mView.length;i++){
            if(position == i) {
                mView[i].setVisibility(View.VISIBLE);
                if(Constants.is4K) {
                    float tempWidth = PenSize[i];
                    mPanelManager.setPenWidth(tempWidth);
                }else
                    mPanelManager.setPenWidth(PenSize[i]);
                SharedPreferencesUtils.setParam(Constants.PEN_SIZE_ID, i);
            }else
                mView[i].setVisibility(View.GONE);
        }
    }

    private void initPenWidthOptions()
    {
        final View[] PenlenActive = new View[4];
        final int pen_sizeid = (int)SharedPreferencesUtils.getParam(Constants.PEN_SIZE_ID, Constants.PEN_WIDTH_DEFAULT);

        View penLittle = findViewById(R.id.pen_little);
        PenlenActive[0] = findViewById(R.id.pen_little_ative);
        View penMiddle = findViewById(R.id.pen_middle);
        PenlenActive[1] = findViewById(R.id.pen_middle_active);
        View penBig = findViewById(R.id.pen_big);
        PenlenActive[2] = findViewById(R.id.pen_big_active);
        View penOverBig = findViewById(R.id.pen_Overbig);
        PenlenActive[3] = findViewById(R.id.pen_Overbig_active);

        setPenSizeView(PenlenActive,pen_sizeid);
        penLittle.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                clearAndResetTimer();
                setPenSizeView(PenlenActive,0);
                mPenSelector.setBackgroundResource(R.drawable.pen_little_selector);
                viewPenColor.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.y10);
                mPenSelector.setActivated(true);
            }
        });
        penMiddle.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                clearAndResetTimer();
                setPenSizeView(PenlenActive,1);
                mPenSelector.setBackgroundResource(R.drawable.pen_middle_selector);
                viewPenColor.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.y15);
                mPenSelector.setActivated(true);
            }
        });
        penBig.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                clearAndResetTimer();
                setPenSizeView(PenlenActive,2);
                mPenSelector.setBackgroundResource(R.drawable.pen_large_selector);
                viewPenColor.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.y15);
                mPenSelector.setActivated(true);
            }
        });

        penOverBig.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                clearAndResetTimer();
                setPenSizeView(PenlenActive,3);
                mPenSelector.setBackgroundResource(R.drawable.pen_large_selector);
                viewPenColor.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.y15);
                mPenSelector.setActivated(true);
            }
        });
    }

    private void initPenColorPicker()
    {
        final int mColor = (int)SharedPreferencesUtils.getParam(Constants.PEN_COLOLR, Constants.PEN_COLOLR_DEFAULT);

        final int mPenType = Integer.valueOf(Utils.getSystemProperties(Constants.PEN_TYPE,Constants.CTV_PEN_TYPE));
        viewPenColor = findViewById(R.id.view_button_pen_color);
        viewPenColor.setBackgroundColor(mColor);
        mDrawBoard.setPenColor(mColor);
        mPenWidthPreview.setPenColor(mColor);

        pen_brush = findViewById(R.id.pen_brush);
        pen_paint = findViewById(R.id.pen_normal);
        pen_str = findViewById(R.id.pen_str);

        if(mPenType == 0){
            pen_brush.setActivated(false);
            pen_paint.setActivated(true);
            mPanelManager.setPenType(0);
            pen_str.setText(R.string.pen);
        }else{
            pen_brush.setActivated(true);
            pen_paint.setActivated(false);
            mPanelManager.setPenType(1);
            pen_str.setText(R.string.soft_pen);
        }

        pen_brush.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pen_brush.setActivated(true);
                pen_paint.setActivated(false);
                mPanelManager.setPenType(1);
                pen_str.setText(R.string.soft_pen);
            }
        });

        pen_paint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pen_brush.setActivated(false);
                pen_paint.setActivated(true);
                mPanelManager.setPenType(0);
                pen_str.setText(R.string.pen);
            }
        });
        dashLine2 = findViewById(R.id.pen_dash_line2);
        mColorPicker = (RecyclerView) findViewById(R.id.color_picker);
        mColorPicker.setLayoutManager(new GridLayoutManager(this, 3));
        int[] colors = getResources().getIntArray(R.array.pen_colors);
        final List<ColorItem> colorItemList = new ArrayList();
        for (int color : colors)
        {
            ColorItem colorItem = new ColorItem();
            colorItem.setType(0);
            colorItem.setColor(color);
            colorItemList.add(colorItem);
        }
        ColorItem colorItemCustom = new ColorItem();
        colorItemCustom.setType(1);
        colorItemList.add(colorItemCustom);
        final ColorPickerAdapter adapter = new ColorPickerAdapter(this, R.layout.pen_color_item, colorItemList);
        adapter.setCurrentColor(mColor);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, ViewHolder holder, int position)
            {
                if (position < 0 && position >= colorItemList.size())
                {
                    return;
                }
                clearAndResetTimer();
                if (position != adapter.getActivePosition())
                {
                    ColorItem colorItemClick = (ColorItem) colorItemList.get(position);
                    switch (colorItemClick.getType())
                    {
                        case 0:
                            dismissColorPickerView();
                            int color = colorItemClick.getColor();
                            adapter.setCurrentColor(color);
                            if (color == -1)
                            {
                                viewPenColor.setBackgroundColor(getResources().getColor(R.color.color1));
                            }
                            else
                            {
                                viewPenColor.setBackgroundColor(color);
                            }
                            SharedPreferencesUtils.setParam(Constants.PEN_COLOLR, color);
                            mDrawBoard.setPenColor(color);
                            mPenWidthPreview.setPenColor(color);
                            mPenWidthPreview.postInvalidate();
                            break;
                        case 1:
                            showColorPickerView(adapter.getCurrentColor());
                            break;
                    }
                    adapter.setActivePosition(position);
                    adapter.notifyDataSetChanged();
                }
            }

            public boolean onItemLongClick(View view, ViewHolder holder, int position)
            {
                return false;
            }
        });
        mColorPicker.setAdapter(adapter);
        colorPickerView = (ColorPickerView) findViewById(R.id.color_picker_view);
        colorPickerView.setColor(Color.MAGENTA);
        colorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            public void onColorChanged(int color)
            {
                clearAndResetTimer();
                adapter.setCurrentColor(color);
                if (color == -1)
                {
                    viewPenColor.setBackgroundColor(getResources().getColor(R.color.bgcolor1));
                }
                else
                {
                    viewPenColor.setBackgroundColor(color);
                }
                SharedPreferencesUtils.setParam(Constants.PEN_COLOLR, color);
                mDrawBoard.setPenColor(color);
                mPenWidthPreview.setPenColor(color);
                mPenWidthPreview.postInvalidate();
            }
        });
    }

    private void showColorPickerView(int color)
    {
        colorPickerView.setColor(color);
        dashLine2.setVisibility(View.VISIBLE);
        colorPickerView.setVisibility(View.VISIBLE);
    }

    private void showColorView(int color)
    {
        bgcolorView.setColor(color);
        color_bg_line.setVisibility(View.VISIBLE);
        color_right.setVisibility(View.VISIBLE);
        bgcolorView.setVisibility(View.VISIBLE);
    }

    private void dismissColorView()
    {
        if(color_right != null && color_right.getVisibility() == View.VISIBLE) {
            bgcolorView.setVisibility(View.GONE);
            color_right.setVisibility(View.GONE);
            color_bg_line.setVisibility(View.INVISIBLE);
        }
    }

    private void dismissColorPickerView()
    {
        dashLine2.setVisibility(View.GONE);
        colorPickerView.setVisibility(View.GONE);
    }

    private void initPage()
    {
        mAddPage = findViewById(R.id.add_panel);
        mDeletePage = findViewById(R.id.delete_panel);
        mPrevPage = findViewById(R.id.prev_panel);
        mNextPage = findViewById(R.id.next_panel);
        mPageIndexTextView = (TextView) findViewById(R.id.panel_index);
        setPageInfo(0, 1);
        mPrevPage.setEnabled(false);
        mNextPage.setEnabled(false);
        mDeletePage.setEnabled(false);
        mAddPage.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                hideAllWindows(0);
                addAndSetPage();
            }
        });
        mDeletePage.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                if (mPanelManager.getPageCount() <= 1)
                {
                    MainActivity.dbg("only one panel");
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.waring_delete_panel), Toast.LENGTH_SHORT).show();
                }
                if (mPreviewLayout.isShown())
                {
                    mPreviewLayout.setVisibility(View.GONE);
                }
                deletePage(mPageIndex);
            }
        });
        mPrevPage.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                hideAllWindows(0);
                goPrevPage();
            }
        });
        mNextPage.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                hideAllWindows(0);
                goNextPage();
            }
        });
    }

    public int getPageIndex()
    {
        return mDrawBoard.mPanelView.mPanelManager.getCurrentPageIndex();
    }

    public void setPage(int index)
    {
        mPanelManager.addPageSwitchAction(index);
        if (mPanelManager.setPage(index, false) == 0)
        {
            mPageIndex = index;
            setPageInfo(mPageIndex, mPanelManager.getPageCount());
            updatePageButtonState();
            mDrawBoard.mPanelView.updateSurface();
        }
    }

    private void initQrShare()
    {
        mMoreLayout.findViewById(R.id.share_qr).setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                mMoreWindow.dismiss();
                if (mConnectivityManager == null)
                {
                    mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                }
                if (BaseUtils.isNetworkAvailed(mConnectivityManager))
                {
                    showQr();
                    return;
                }
                Toast.makeText(getApplicationContext(), R.string.unaviled_network, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showQr()
    {
        isQrCancel = false;
        final SDKinfor sdkinfo = new SDKinfor(this);
        final QrDialog qr_dialog = new QrDialog(this);
        qr_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override public void onDismiss(DialogInterface dialog)
            {
                isQrCancel = true;
                BaseUtils.deleteFloder(RECORD_TMP_FILE);
                BaseUtils.hideNavigationBar(MainActivity.this);
            }
        });
        qr_dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override public void onCancel(DialogInterface dialog)
            {
                isQrCancel = true;
                BaseUtils.deleteFloder(RECORD_TMP_FILE);
                BaseUtils.hideNavigationBar(MainActivity.this);
            }
        });
        qr_dialog.show();
        qr_dialog.getWindow().setLayout(getResources().getDimensionPixelOffset(R.dimen.qr_dialog_width), getResources().getDimensionPixelOffset(R.dimen.qr_dialog_width));


        deviceFlag = (String) SharedPreferencesUtils.getParam("device_flag", "");
        if (TextUtils.isEmpty(deviceFlag))
        {
            String macAdress = BaseUtils.getMacAddress(0).trim().replace(":", "");
            Random random = new Random();
            deviceFlag = macAdress + "_" + (random.nextInt(100) + 1);
            SharedPreferencesUtils.setParam("device_flag", deviceFlag);
        }
        workId = (int) SharedPreferencesUtils.getParam("upload_workid", 1);
        if (workId >= 900)
        {
            workId = 1;
        }
        SharedPreferencesUtils.setParam("upload_workid", workId + 1);
        final int pageCount = mPanelManager.getPageCount();

        Observable.just(Integer.valueOf(0)).observeOn(Schedulers.newThread()).subscribe(new Action1<Integer>() {
            public void call(Integer integer)
            {
                i = 0;
                Bitmap bitmap = getPageBitmap(mPageIndex);
                String path = UploadManager.compressImage(bitmap).getAbsolutePath();
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                Date date = new Date(System.currentTimeMillis());

                StringBuilder sb = new StringBuilder();
                sb.append("whiteboard/");
                sb.append("whiteboad_");
                sb.append(format.format(date));
                sb.append(".png");
                Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                    public void call(Integer integer)
                    {
                        qr_dialog.refreshPageInfo(0, 1);
                    }
                });
//                BaseUtils.dbg(TAG,"uoloadBitmap path =  " + path);
                sdkinfo.upload(sb.toString(),path,new CosXmlResultListener(){
                    @Override
                    public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                        // todo Put Bucket Lifecycle success
//                                BaseUtils.dbg(TAG,"uoloadBitmap success url=" + result.accessUrl);
                        final String mUrl = result.accessUrl;
                        Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                            public void call(Integer integer)
                            {
                                qr_dialog.refreshPageInfo(1, 1);
                                qr_dialog.showQrView(createQRCode(mUrl, getResources().getDimensionPixelOffset(R.dimen.qr_width)));
                            }
                        });
                    }

                    @Override
                    public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException)  {
                        // todo Put Bucket Lifecycle failed because of CosXmlClientException or CosXmlServiceException...
                        BaseUtils.dbg(TAG,"uoloadBitmap onFail");
                        qr_dialog.dismiss();
                        Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                            public void call(Integer integer)
                            {
                                Toast.makeText(getApplicationContext(), R.string.upload_image_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }
/*
    private void showQr()
    {
        isQrCancel = false;

        final QrDialog qr_dialog = new QrDialog(this);
        qr_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override public void onDismiss(DialogInterface dialog)
            {
                isQrCancel = true;
                BaseUtils.deleteFloder(RECORD_TMP_FILE);
                BaseUtils.hideNavigationBar(MainActivity.this);
            }
        });
        qr_dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override public void onCancel(DialogInterface dialog)
            {
                isQrCancel = true;
                BaseUtils.deleteFloder(RECORD_TMP_FILE);
                BaseUtils.hideNavigationBar(MainActivity.this);
            }
        });
        qr_dialog.show();
        qr_dialog.getWindow().setLayout(getResources().getDimensionPixelOffset(R.dimen.qr_dialog_width), getResources().getDimensionPixelOffset(R.dimen.qr_dialog_width));

        deviceFlag = (String) SharedPreferencesUtils.getParam("device_flag", "");
        if (TextUtils.isEmpty(deviceFlag))
        {
            String macAdress = BaseUtils.getMacAddress(0).trim().replace(":", "");
            Random random = new Random();
            deviceFlag = macAdress + "_" + (random.nextInt(100) + 1);
            SharedPreferencesUtils.setParam("device_flag", deviceFlag);
        }
        workId = (int) SharedPreferencesUtils.getParam("upload_workid", 1);
        if (workId >= 900)
        {
            workId = 1;
        }
        SharedPreferencesUtils.setParam("upload_workid", workId + 1);
        final int pageCount = mPanelManager.getPageCount();

        Observable.just(Integer.valueOf(0)).observeOn(Schedulers.newThread()).subscribe(new Action1<Integer>() {
            public void call(Integer integer)
            {
                i = 0;
                while (i < pageCount)
                {
                    if (isQrCancel)
                    {
                        break;
                    }
                    int index = i;
                    Bitmap bitmap = getPageBitmap(index);
                    if (bitmap == null)
                    {
                        Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                            public void call(Integer integer)
                            {
                                Toast.makeText(getApplicationContext(), R.string.no_more_memory, Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    String result = null;
                    try
                    {
                        result = UploadManager.uoloadBitmap(deviceFlag, workId, pageCount, i + 1, bitmap);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                            public void call(Integer integer)
                            {
                                Toast.makeText(getApplicationContext(), String.format(getString(R.string.page_index_info), i) + " " + getString(R.string.upload_image_failed), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if (TextUtils.isEmpty(result))
                    {
                        Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                            public void call(Integer integer)
                            {
                                Toast.makeText(getApplicationContext(), String.format(getString(R.string.page_index_info), i) + " " + getString(R.string.upload_image_failed), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        try
                        {
                            BaseUtils.dbg(TAG,"uoloadBitmap result =  " + result);
                            JSONObject json = new JSONObject(result);
                            toDir = json.optString("tdir");
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                        public void call(Integer integer)
                        {
                            qr_dialog.refreshPageInfo(i, pageCount);
                        }
                    });
                    i++;
                }

                try
                {
                    if (isQrCancel)
                    {
                        UploadManager.getResponse(deviceFlag, workId, 0, toDir);
                        Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                            public void call(Integer integer)
                            {
                                qr_dialog.dismiss();
                                Toast.makeText(getApplicationContext(), R.string.upload_image_interrupt, Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    String responseResult = UploadManager.getResponse(deviceFlag, workId, 1, toDir);
                    BaseUtils.dbg(TAG," responseResult =  " + responseResult);
                    JSONObject json = new JSONObject(responseResult);
                    int code = json.optInt("code");
                    final String url = json.optString("url");
                    BaseUtils.dbg(TAG," url =  " + url);
                    if (TextUtils.isEmpty(url))
                    {
                        Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                            public void call(Integer integer)
                            {
                                qr_dialog.dismiss();
                                Toast.makeText(getApplicationContext(), R.string.upload_image_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    else
                    {
                        Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                            public void call(Integer integer)
                            {
                                qr_dialog.showQrView(createQRCode(url, getResources().getDimensionPixelOffset(R.dimen.qr_width)));
                            }
                        });
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                        public void call(Integer integer)
                        {
                            qr_dialog.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.upload_image_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

            }
        });
    }*/

    public static Bitmap createQRCode(String url, int widthAndHeight)
    {
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix matrix = null;
        try
        {
            BaseUtils.dbg(TAG,"createQRCode url=" + url);
            matrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
        }
        catch (com.google.zxing.WriterException e)
        {
            e.printStackTrace();
        }

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        //画黑点
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                if (matrix.get(x, y))
                {
                    pixels[y * width + x] = Color.BLACK; //0xff000000
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static void setPopupWindowTouchModal(PopupWindow popupWindow, boolean touchModal)
    {
        if (popupWindow != null)
        {
            try
            {
                Method method = PopupWindow.class.getDeclaredMethod("setTouchModal", new Class[]{Boolean.TYPE});
                method.setAccessible(true);
                method.invoke(popupWindow, new Object[]{Boolean.valueOf(touchModal)});
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void initEraseWindow()
    {
        ViewGroup viewGroup = (ViewGroup) ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.erase_layout, null);
        mEraseWindow = new PopupWindow(viewGroup, -2, -2, false);
        mEraseLayout = viewGroup;
        mEraseWindow.setOutsideTouchable(false);
        mEraseWindow.setFocusable(false);
        mEraseWindow.setTouchable(true);
        mEraseWindow.setBackgroundDrawable(new ColorDrawable(0));
        setPopupWindowTouchModal(mEraseWindow, false);
        textViewOneKeyClear = (TextView) viewGroup.findViewById(R.id.text_erase_on_key_clear);
        objectAnimator = ObjectAnimator.ofFloat(textViewOneKeyClear, "alpha", new float[]{1.0f, 0.0f, 1.0f});
        objectAnimator.setDuration(4000);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.setRepeatCount(-1);
        mEraseWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss()
            {
                objectAnimator.cancel();
            }
        });
        mBtnClean = findViewById(R.id.clean_board);
        mBtnClean.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                if (mEraseWindow != null && mEraseLayout != null && mEraseLayout.isShown())
                {
                    mEraseWindow.dismiss();
                    return;
                }
                BaseUtils.dbg(TAG,"mBtnClean");
                clearTimer();
                View view = findViewById(R.id.bottom_buttons);
                mEraseLayout.measure(0, 0);
                BaseUtils.hideNavigationBar(MainActivity.this);
                mEraseWindow.showAtLocation(mRoot, Gravity.BOTTOM | Gravity.LEFT, getResources().getDimensionPixelSize(R.dimen.x760), getWindowLocationY());
                objectAnimator.start();
                mPenSelectorLayout.setVisibility(View.GONE);
                hideAllWindows(2);
                setPanelMode(1);
                setTimer();
            }
        });
        mEraseSizeButton = mEraseLayout.findViewById(R.id.erase_size);
        mEraseSizeButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                mCleanMode = 1;
                setPanelMode(mCleanMode);
                mBtnClean.setBackgroundResource(R.drawable.erase_by_size);
                mEraseSizeButton.setBackgroundResource(R.drawable.erase_pressed);
                mEraseMaterialButton.setBackgroundResource(R.drawable.erase_material_normal);
                clearAndResetTimer();
            }
        });
        mEraseMaterialButton = mEraseLayout.findViewById(R.id.erase_material);
        mEraseMaterialButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                mCleanMode = 2;
                setPanelMode(mCleanMode);
                mBtnClean.setBackgroundResource(R.drawable.erase_material);
                mEraseSizeButton.setBackgroundResource(R.drawable.erase_normal);
                mEraseMaterialButton.setBackgroundResource(R.drawable.erase_material_pressed);
                clearAndResetTimer();
            }
        });
        mEraseSlider = (SlideButton) mEraseLayout.findViewById(R.id.clean_board_slider);
        mEraseSlider.setOnTriggerEventListener(new SlideButton.TriggerEventListener() {
            @Override public void onTrigger(boolean on)
            {
                if (on)
                {
                    mDrawBoard.cleanBoard();
                    mPanelManager.resetBoard();
                    /*清除Action记录*/
                    mPanelManager.cleanAction();
                    mEraseWindow.dismiss();
                    setPanelMode(0);
                    return;
                }
                setTimer();
            }
        });
    }
    private List<Noteinfo> getNoteList(){
        List<Noteinfo> mNote = new ArrayList<Noteinfo>();
        File[] files = new File(MRC_DIR).listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory() && file.getName().endsWith(".mrc")) {
                    Noteinfo note = new Noteinfo();
                    note.setFile(file);
                    note.setName(file.getName());
                    note.setPathName(file.getPath());
                    mNote.add(note);
                }
            }
        }
        if(mNote != null){
            Collections.sort(mNote, new Comparator<Noteinfo>() {
                @Override
                public int compare(Noteinfo o1, Noteinfo o2) {
                    if (o1.getmFile().lastModified() > o2.getmFile().lastModified()) {
                        return -1;
                    }
                    return 0;
                }
            });
        }
        return mNote;
    }

    int mPosition = -1;
    private void initNoteWindow(final List<Noteinfo> mNameList){
        mPosition = -1;
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.note_layout, null);
        mNoteWindow = new PopupWindow(viewGroup, getResources().getDimensionPixelSize(R.dimen.x680), getResources().getDimensionPixelSize(R.dimen.x300), false);
        mNoteWindow.setOutsideTouchable(false);
        mNoteWindow.setFocusable(false);
        mNoteWindow.setTouchable(true);
        mNoteWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mNoteWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                clearNoteTimer();
            }
        });
        setPopupWindowTouchModal(mNoteWindow, false);
        GridView note_list = (GridView) viewGroup.findViewById(R.id.note_list);
        final ImageView note_draw = (ImageView) viewGroup.findViewById(R.id.note_draw);

        final NoteAdapter noteAdapter = new NoteAdapter(MainActivity.this,mNameList);
        note_list.setSelector(new ColorDrawable(Color.TRANSPARENT));
        note_list.setAdapter(noteAdapter);
        note_list.setVerticalScrollBarEnabled(true);
        hideAllWindows(0);
        viewGroup.measure(0, 0);
        BaseUtils.hideNavigationBar(MainActivity.this);
        mNoteWindow.showAtLocation(mRoot, Gravity.LEFT | Gravity.BOTTOM, getResources().getDimensionPixelSize(R.dimen.icon_padding), getWindowLocationY());

        note_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             //   OpenNote(mNameList.get(position).getPathName());
                mPosition = position;
                Bitmap bitmap = PreviewBitmapNote(mNameList.get(position).getPathName());
                if(bitmap != null) {
                    note_draw.setImageBitmap(bitmap);
                    if (bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                }
                noteAdapter.setIdChanged(position);
            }
        });
        note_draw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPosition >= 0) {
                    mNoteWindow.dismiss();
                    OpenNote(mNameList.get(mPosition).getPathName());
                }else
                    Toast.makeText(MainActivity.this, R.string.please_select_file, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void OpenNote(String pathName)
    {
        File noteFile = new File(pathName);
//        BaseUtils.dbg(TAG, "OpenNote noteFile= " + noteFile.toString());
        try
        {
            mPanelManager.restoreNote(noteFile.getAbsolutePath());
            setPageInfo(mPanelManager.getCurrentPageIndex(), mPanelManager.getPageCount());
            updatePageButtonState();
            lastSavedPage = mPanelManager.getPageCount();
            lastSavedMaterial = mPanelManager.getTotalMaterialsCount();
        }
        catch (Exception e2)
        {
            Toast.makeText(this, R.string.open_file_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap PreviewBitmapNote(String pathName)
    {
        File noteFile = new File(pathName);
//        BaseUtils.dbg(TAG, "OpenNote noteFile= " + noteFile.toString());
        try
        {
            return mPanelManager.PreviewNote(noteFile.getAbsolutePath());
        }
        catch (Exception e2)
        {
            Toast.makeText(this, R.string.open_file_failed, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    /**
     * 初始化数学形状UI
     */
    private void initMathShapeWindow(){
        mMathLayout = layoutInflater.inflate(R.layout.math_shape_window, null);
        mMathWindow = new PopupWindow(mMathLayout, -2, getResources().getDimensionPixelSize(R.dimen.y160), false);
        mMathWindow.setOutsideTouchable(false);
        mMathWindow.setFocusable(false);
        mMathWindow.setTouchable(true);
        mMathWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mMathWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss()
            {
                clearNoteTimer();
            }
        });
        setPopupWindowTouchModal(mMathWindow, false);

        circleShape = (ImageView) mMathLayout.findViewById(R.id.iv_shape_circle);
        ovalShape = (ImageView) mMathLayout.findViewById(R.id.iv_shape_oval);
        rectShape = (ImageView) mMathLayout.findViewById(R.id.iv_shape_rect);
        equalTriShape = (ImageView) mMathLayout.findViewById(R.id.iv_shape_equal_tri);
        rightTriShape = (ImageView) mMathLayout.findViewById(R.id.iv_shape_right_tri);
        cylinder = (ImageView) mMathLayout.findViewById(R.id.iv_shape_cylinder);
//        pentagramShape = (ImageView) mMathLayout.findViewById(R.id.iv_shape_pentagram);
        arrowShape = (ImageView) mMathLayout.findViewById(R.id.iv_shape_arrow);
        lineShape = (ImageView) mMathLayout.findViewById(R.id.iv_shape_line);

        // 设置默认形状
        mDrawBoard.mPanelView.mPanelManager.setShapeType(StatusEnum.STATUS_SHAPE_CIRCLE);
        circleShape.setActivated(true);

        // 数学形状监听
        circleShape.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.dbg(TAG, "start --- iv_shape_circle.isShowing");
                setShapeSelected(StatusEnum.STATUS_SHAPE_CIRCLE);
            }
        });

        ovalShape.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.dbg(TAG, "start --- iv_shape_oval.isShowing");
                setShapeSelected(StatusEnum.STATUS_SHAPE_OVAL);
            }
        });

        rectShape.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.dbg(TAG, "start --- iv_shape_rect.isShowing");
                setShapeSelected(StatusEnum.STATUS_SHAPE_RECTTANGLE);
            }
        });

        equalTriShape.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.dbg(TAG, "start --- iv_shape_equal_tri.isShowing");
                setShapeSelected(StatusEnum.STATUS_SHAPE_TRIANGLE);
            }
        });

        rightTriShape.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.dbg(TAG, "start --- iv_shape_right_tri.isShowing");
                setShapeSelected(StatusEnum.STATUS_SHAPE_RIGHT_TRIANGLE);
            }
        });

   /*     pentagramShape.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.dbg(TAG, "start --- iv_shape_pentagram.isShowing");
                setShapeSelected(StatusEnum.STATUS_SHAPE_FIVE_POINT);
            }
        });*/

        arrowShape.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.dbg(TAG, "start --- iv_shape_arrow.isShowing");
                setShapeSelected(StatusEnum.STATUS_SHAPE_ARROW);
            }
        });

        lineShape.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.dbg(TAG, "start --- iv_shape_line.isShowing");
                setShapeSelected(StatusEnum.STATUS_SHAPE_LINE);
            }
        });

        cylinder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.dbg(TAG, "start --- iv_shape_cylinder.isShowing");
                setShapeSelected(StatusEnum.STATUS_SHAPE_CONE);
            }
        });
    }

    /**
     * 设置选择的形状
     * @param shapeType
     */
    private void setShapeSelected(int shapeType){
        circleShape.setActivated(false);
        ovalShape.setActivated(false);
        rectShape.setActivated(false);
        equalTriShape.setActivated(false);
        rightTriShape.setActivated(false);
//        pentagramShape.setActivated(false);
        arrowShape.setActivated(false);
        lineShape.setActivated(false);
        cylinder.setActivated(false);

        // 判断形状类型
        switch (shapeType){
            case StatusEnum.STATUS_SHAPE_CIRCLE:{ // 圆形
                circleShape.setActivated(true);
                math_icon.setBackgroundResource(R.drawable.circle_bt);
                break;
            }
            case StatusEnum.STATUS_SHAPE_OVAL:{ // 椭圆
                ovalShape.setActivated(true);
                math_icon.setBackgroundResource(R.drawable.oval_bt);
                break;
            }
            case StatusEnum.STATUS_SHAPE_RECTTANGLE:{ // 矩形
                rectShape.setActivated(true);
                math_icon.setBackgroundResource(R.drawable.square_bt);
                break;
            }
            case StatusEnum.STATUS_SHAPE_TRIANGLE:{ // 三角形
                equalTriShape.setActivated(true);
                math_icon.setBackgroundResource(R.drawable.equal_tri_bt);
                break;
            }
            case StatusEnum.STATUS_SHAPE_RIGHT_TRIANGLE:{ // 直角三角形
                rightTriShape.setActivated(true);
                math_icon.setBackgroundResource(R.drawable.right_tri_bt);
                break;
            }
/*            case StatusEnum.STATUS_SHAPE_FIVE_POINT:{ // 五角星
                pentagramShape.setActivated(true);
                break;
            }*/
            case StatusEnum.STATUS_SHAPE_ARROW:{ // 箭头
                arrowShape.setActivated(true);
                math_icon.setBackgroundResource(R.drawable.arrow_bt);
                break;
            }
            case StatusEnum.STATUS_SHAPE_LINE:{ // 直线
                lineShape.setActivated(true);
                math_icon.setBackgroundResource(R.drawable.line_bt);
                break;
            }
            case StatusEnum.STATUS_SHAPE_CONE:
                cylinder.setActivated(true);
                math_icon.setBackgroundResource(R.drawable.cylinder_bt);
                break;
            default:{
                rectShape.setActivated(true);
                break;
            }
        }

        mDrawBoard.mPanelView.mPanelManager.setShapeType(shapeType);
        hideAllWindows(0);
        math_icon.setActivated(true);
    }
	
    private void initMoreFunctionWindow()
    {
        final int bg_color = (int)SharedPreferencesUtils.getParam(Constants.BG_COLOLR, Constants.BG_COLOLR_DEFAULT);
        final int bg_style = (int)SharedPreferencesUtils.getParam(Constants.BG_STYLE, Constants.BG_STYLE_DEFAULT);

        final int[] colors = getResources().getIntArray(R.array.bg_colors);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.more_function_layout, null);
        mMoreWindow = new PopupWindow(viewGroup, -2, -2, false);
        mMoreLayout = viewGroup;
        initQrShare();
        mMoreWindow.setOutsideTouchable(false);
        mMoreWindow.setFocusable(false);
        mMoreWindow.setTouchable(true);
        mMoreWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mMoreWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss()
            {
                clearTimer();
            }
        });
        setPopupWindowTouchModal(mMoreWindow, false);


        viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.bg_change_layout, null);
        mBackgroundLayout = viewGroup;
        mBackgroundColorWindow = new PopupWindow(mBackgroundLayout, -2, getResources().getDimensionPixelSize(R.dimen.y160), false);
        mBackgroundColorWindow.setOutsideTouchable(true);
        mBackgroundColorWindow.setTouchable(true);
        setPopupWindowTouchModal(mBackgroundColorWindow, false);

        mBgcolor = new ImageView[5];
        bg_shape = new ImageView[5];

        mBgcolor[0] = (ImageView) mBackgroundLayout.findViewById(R.id.bgcolor1);
        mBgcolor[1] = (ImageView) mBackgroundLayout.findViewById(R.id.bgcolor2);
        mBgcolor[2] = (ImageView) mBackgroundLayout.findViewById(R.id.bgcolor3);
        mBgcolor[3] = (ImageView) mBackgroundLayout.findViewById(R.id.bgcolor4);
        mBgcolor[4] = (ImageView) mBackgroundLayout.findViewById(R.id.bgcolor5);

        bg_shape[0] = (ImageView) mBackgroundLayout.findViewById(R.id.bg_shape1);
        bg_shape[1] = (ImageView) mBackgroundLayout.findViewById(R.id.bg_shape2);
        bg_shape[2] = (ImageView) mBackgroundLayout.findViewById(R.id.bg_shape3);
        bg_shape[3] = (ImageView) mBackgroundLayout.findViewById(R.id.bg_shape4);
        bg_shape[4] = (ImageView) mBackgroundLayout.findViewById(R.id.bg_shape5);

        color_right = (LinearLayout) mBackgroundLayout.findViewById(R.id.color_right);
        color_bg_line = (View) mBackgroundLayout.findViewById(R.id.color_bg_line);

        setColorViewBackGround(bg_color);
        setStyleViewBackGround(bg_style);

        mBackgroundColorWindow.setBackgroundDrawable(new ColorDrawable(0));
        bgcolorView = (ColorPickerView) mBackgroundLayout.findViewById(R.id.color_view);
        bgcolorView.setColor(Color.MAGENTA);
        bgcolorView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            public void onColorChanged(int color)
            {
                clearAndResetTimer();
                if (color != -1){
                    SharedPreferencesUtils.setParam(Constants.BG_COLOLR, color);
                    mDrawBoard.mPanelView.mPanelManager.setBoardBgBackground(color);
                    mPanelManager.UpdateBoardBackground(false);
                    mDrawBoard.mPanelView.updateSurface();
                }
            }
        });

        mBgcolor[0].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissColorView();
                setColorViewBackGround(v);
                SharedPreferencesUtils.setParam(Constants.BG_COLOLR, colors[0]);
                mDrawBoard.mPanelView.mPanelManager.setBoardBgBackground(colors[0]);
                mPanelManager.UpdateBoardBackground(false);
                mDrawBoard.mPanelView.updateSurface();
            }
        });

        mBgcolor[1].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissColorView();
                setColorViewBackGround(v);
                SharedPreferencesUtils.setParam(Constants.BG_COLOLR, colors[1]);
                mDrawBoard.mPanelView.mPanelManager.setBoardBgBackground(colors[1]);
                mPanelManager.UpdateBoardBackground(false);
                mDrawBoard.mPanelView.updateSurface();
            }
        });

        mBgcolor[2].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissColorView();
                setColorViewBackGround(v);
                SharedPreferencesUtils.setParam(Constants.BG_COLOLR, colors[2]);
                mDrawBoard.mPanelView.mPanelManager.setBoardBgBackground(colors[2]);
                mPanelManager.UpdateBoardBackground(false);
                mDrawBoard.mPanelView.updateSurface();
            }
        });

        mBgcolor[3].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissColorView();
                setColorViewBackGround(v);
                SharedPreferencesUtils.setParam(Constants.BG_COLOLR, colors[3]);
                mDrawBoard.mPanelView.mPanelManager.setBoardBgBackground(colors[3]);
                mPanelManager.UpdateBoardBackground(false);
                mDrawBoard.mPanelView.updateSurface();
            }
        });

        mBgcolor[4].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setColorViewBackGround(v);
                if(color_right != null && color_right.getVisibility() == View.VISIBLE) {
                    dismissColorView();
                }else {
                    showColorView(mDrawBoard.mPanelView.mPanelManager.getBoardBackgroundColor());
                }
            }
        });

        bg_shape[0].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissColorView();
                setStyleViewBackGround(v);
                SharedPreferencesUtils.setParam(Constants.BG_STYLE, 0);
                mDrawBoard.mPanelView.mPanelManager.setStyleBackground(0);
                mPanelManager.UpdateBoardBackground(false);
                mDrawBoard.mPanelView.updateSurface();
            }
        });

        bg_shape[1].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissColorView();
                setStyleViewBackGround(v);
                SharedPreferencesUtils.setParam(Constants.BG_STYLE, 1);
                mDrawBoard.mPanelView.mPanelManager.setStyleBackground(1);
                mPanelManager.UpdateBoardBackground(false);
                mDrawBoard.mPanelView.updateSurface();
            }
        });

        bg_shape[2].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissColorView();
                setStyleViewBackGround(v);
                SharedPreferencesUtils.setParam(Constants.BG_STYLE, 2);
                mDrawBoard.mPanelView.mPanelManager.setStyleBackground(2);
                mPanelManager.UpdateBoardBackground(false);
                mDrawBoard.mPanelView.updateSurface();
            }
        });

        bg_shape[3].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissColorView();
                setStyleViewBackGround(v);
                SharedPreferencesUtils.setParam(Constants.BG_STYLE, 3);
                mDrawBoard.mPanelView.mPanelManager.setStyleBackground(3);
                mPanelManager.UpdateBoardBackground(false);
                mDrawBoard.mPanelView.updateSurface();
            }
        });

        bg_shape[4].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissColorView();
                setStyleViewBackGround(v);
                SharedPreferencesUtils.setParam(Constants.BG_STYLE, 4);
                mDrawBoard.mPanelView.mPanelManager.setStyleBackground(4);
                mPanelManager.UpdateBoardBackground(false);
                mDrawBoard.mPanelView.updateSurface();
            }
        });

        mMoreFunction = findViewById(R.id.more_function);
        final Resources resources = getResources();
        mMoreFunction.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                if (mMoreLayout != null && mMoreLayout.isShown() && mMoreWindow != null)
                {
                    mMoreWindow.dismiss();
                    return;
                }
                else
                {
                    hideAllWindows(0);
                    mMoreLayout.measure(0, 0);
                    BaseUtils.hideNavigationBar(MainActivity.this);
                    mMoreWindow.showAtLocation(mRoot, Gravity.LEFT | Gravity.BOTTOM, resources.getDimensionPixelSize(R.dimen.icon_padding), getWindowLocationY());
                }
//                BaseUtils.dbg(TAG, "end --- mMoreWindow.isShowing = " + mMoreWindow.isShowing());
            }
        });
        mMoreLayout.findViewById(R.id.mail_to).setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                if(mMoreWindow != null && mMoreWindow.isShowing()) {
                    mMoreWindow.dismiss();
                }
                final AlertDialog dialog = new SpotsDialog(MainActivity.this, getResources().getString(R.string.loading));
                dialog.show();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override public void onDismiss(DialogInterface dialog)
                    {
                        BaseUtils.hideNavigationBar(MainActivity.this);
                    }
                });
                Observable.just(Integer.valueOf(1)).observeOn(Schedulers.newThread()).subscribe(new Action1<Integer>() {
                    public void call(Integer integer)
                    {
                        Intent email = new Intent(Intent.ACTION_SEND);
                        String tmpDir = ROOT_DIR + "/mail/";
                        ArrayList imageUris = new ArrayList();
                        int i = 0;
                        while (i < mPanelManager.getPageCount())
                        {
                            try
                            {
                                String path = tmpDir + i + ".png";
                                new File(path).getParentFile().mkdirs();
                                FileOutputStream out = new FileOutputStream(path);
                                Bitmap bitmap = getPageBitmap(i);
                                if (bitmap == null)
                                {
                                    throw new FileNotFoundException();
                                }
                                bitmap.compress(CompressFormat.PNG, 90, out);
                                bitmap.recycle();
                                imageUris.add(Uri.parse("file://" + path));
                                i++;
                            }
                            catch (FileNotFoundException e)
                            {
                                error(BaseUtils.getStackTrace(e));
                                Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                                    public void call(Integer integer)
                                    {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.send_mail_fail), Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                                return;
                            }
                        }

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(getString(R.string.file_name_prefix));
//                        stringBuilder.append("-");
                        stringBuilder.append(simpleDateFormat.format(new Date()));
                        String stringBuilder2 = stringBuilder.toString();

                        String str = "android.intent.action.SEND_MULTIPLE";
                        Intent intent = new Intent(str);
//                        intent.setType("plain/text");
                        intent.setType("*/*");
                        List<ResolveInfo> queryIntentActivities = getPackageManager().queryIntentActivities(intent, 0);
                        Collection arrayList = new ArrayList();
                        for (ResolveInfo resolveInfo : queryIntentActivities) {
                            String str4 = resolveInfo.activityInfo.packageName;
                            BaseUtils.dbg(TAG, "str4 = " + str4);
                            if(AppOverCare(str4)) {
                                arrayList.add(resolveInfo);
                            }
                        }
                        queryIntentActivities.removeAll(arrayList);
                        if (queryIntentActivities.size() <= 0) {
                            BaseUtils.dbg(TAG, "no email client instgalled.");
                        }

                        str = "android.intent.extra.STREAM";
                        intent.putParcelableArrayListExtra(str, imageUris);
                        ArrayList arrayList3 = new ArrayList(queryIntentActivities.size());
                        PackageManager packageManager = getPackageManager();
                        for (ResolveInfo resolveInfo2 : queryIntentActivities) {
                            String str3 = resolveInfo2.activityInfo.packageName;
                            BaseUtils.dbg(TAG, "str3 = " + str3);
                            ArrayList<String> extra_text = new ArrayList<String>();

                            if(!AppOverCare(str3)) {
                                LabeledIntent labeledIntent;
                                String text = getString(R.string.email_body);
                                extra_text.clear();
                                extra_text.add(text);
                                if(str3.toLowerCase(Locale.US).contains("com.microsoft.office.outlook")) {
                                    labeledIntent = new LabeledIntent(str3, resolveInfo2.loadLabel(packageManager), resolveInfo2.getIconResource());
                                    labeledIntent.setAction("android.intent.action.SEND_MULTIPLE").setPackage(str3).setComponent(new ComponentName(str3, resolveInfo2.activityInfo.name)).setType("*/*").putExtra("android.intent.extra.SUBJECT", stringBuilder2).putExtra(Intent.EXTRA_TEXT, extra_text).putParcelableArrayListExtra(str, imageUris).setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                                }else {
                                    labeledIntent = new LabeledIntent(str3, resolveInfo2.loadLabel(packageManager), resolveInfo2.getIconResource());
                                    labeledIntent.setAction(str).setPackage(str3).setComponent(new ComponentName(str3, resolveInfo2.activityInfo.name)).setType("text/plain").putExtra("android.intent.extra.SUBJECT", stringBuilder2).putExtra(Intent.EXTRA_TEXT, extra_text).putParcelableArrayListExtra(str, imageUris).setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                                }
                                arrayList3.add(labeledIntent);
                            }
                        }

                        try
                        {
                            dialog.dismiss();
                            Intent createChooser = Intent.createChooser((Intent) arrayList3.remove(0), getString(R.string.send_email));
                            int size = arrayList3.size();
                            if (size > 0) {
                                createChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, (Parcelable[]) arrayList3.toArray(new Parcelable[size]));
                            }
                            startActivity(createChooser);
                        }
                        catch (Exception e2)
                        {
                            Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                                public void call(Integer integer)
                                {
                                    Toast.makeText(getApplicationContext(), R.string.please_install_email, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        });

        mMoreLayout.findViewById(R.id.insert_image).setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                showFileBrowserPopu(1,1);
            }
        });

        mMoreLayout.findViewById(R.id.note_open).setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                mMoreWindow.dismiss();
                int totalCount = mPanelManager.getTotalMaterialsCount();
                if (mPanelManager.getPageCount() != lastSavedPage || (totalCount > 0 && totalCount != lastSavedMaterial))
                {
                    showSaveNoteDlg();
                }
                else
                {
                    if (mNoteWindow != null && mNoteWindow.isShowing())
                    {
                        mNoteWindow.dismiss();
                        return;
                    }

                    NoteTask mTask = new NoteTask();
                    mTask.execute();
                }
            }
        });

        mMoreLayout.findViewById(R.id.save_picture).setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                mMoreWindow.dismiss();
                int fileCount = BaseUtils.getFileCount(new File(ROOT_DIR));
//                BaseUtils.dbg(ROOT_DIR + " , fileCount = " + fileCount);
                if (fileCount >= Constants.MAX_FILE_COUNT)
                {
                    showFileCountDlg();
                }
                else
                {
                    showFileBrowserPopu(1,0);
                }
            }
        });

        mMoreLayout.findViewById(R.id.save_pdf).setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                mMoreWindow.dismiss();
                int fileCount = BaseUtils.getFileCount(new File(ROOT_DIR));
                if (fileCount >= Constants.MAX_FILE_COUNT)
                {
                    showFileCountDlg();
                }
                else
                {
                    showFileBrowserPopu(0,0);
                }
            }
        });

        mMoreLayout.findViewById(R.id.setting).setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                mMoreWindow.dismiss();
                showSettingDialog();
            }
        });

        mMoreLayout.findViewById(R.id.exit_app).setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                mMoreWindow.dismiss();
                showExitDialog();
            }
        });
        initSaveButton();
    }

    private void showSaveNoteDlg()
    {
        final SaveNoteDialog dlg = new SaveNoteDialog(MainActivity.this);
        dlg.show();
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override public void onDismiss(DialogInterface dialog)
            {
                BaseUtils.hideNavigationBar(MainActivity.this);
            }
        });
        dlg.setMessage(getString(R.string.floder_name) + ROOT_DIR + "\n" + String.format(getString(R.string.file_already_full), Constants.MAX_FILE_COUNT));
        dlg.setLeftListener(new OnClickListener() {
            @Override public void onClick(View v)
            {
                dlg.dismiss();
                NoteTask mTask = new NoteTask();
                mTask.execute();
            }
        });
        dlg.setRightListener(new OnClickListener() {
            @Override public void onClick(View v)
            {
                dlg.dismiss();
                showFileBrowserPopu(2,0);
            }
        });
        dlg.getWindow().setLayout(getResources().getDimensionPixelSize(R.dimen.save_panel_dialog_width), getResources().getDimensionPixelSize(R.dimen.save_panel_dialog_height));

    }

    private void initSaveButton()
    {
        mSaveButton = mMoreLayout.findViewById(R.id.save_board);
        mSaveButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.getTotalMaterialsCount() == 0)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.nothing_to_save), Toast.LENGTH_SHORT).show();
                    return;
                }
                mMoreWindow.dismiss();

                int fileCount = BaseUtils.getFileCount(new File(ROOT_DIR));
//                BaseUtils.dbg(TAG,ROOT_DIR + " , fileCount = " + fileCount);
                if (fileCount >= Constants.MAX_FILE_COUNT)
                {
                    showFileCountDlg();
                }
                else
                {
                    showFileBrowserPopu(2,0);
                }
            }
        });
    }

    /**
     * 文件数量达到上限，提示用户
     */
    private void showFileCountDlg()
    {
        final FileCountDialog dlg = new FileCountDialog(MainActivity.this);
        dlg.show();
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override public void onDismiss(DialogInterface dialog)
            {
                BaseUtils.hideNavigationBar(MainActivity.this);
            }
        });
        dlg.setMessage(getString(R.string.floder_name) + ROOT_DIR + "\n" + String.format(getString(R.string.file_already_full), Constants.MAX_FILE_COUNT));
        dlg.setLeftListener(new OnClickListener() {
            @Override public void onClick(View v)
            {
                dlg.dismiss();
            }
        });
        dlg.setRightListener(new OnClickListener() {
            @Override public void onClick(View v)
            {
                dlg.dismiss();
                gotoFileBrower();
            }
        });
        dlg.getWindow().setLayout(getResources().getDimensionPixelSize(R.dimen.save_panel_dialog_width), getResources().getDimensionPixelSize(R.dimen.save_panel_dialog_height));
    }

    private void gotoFileBrower()
    {
        /**打开文件管理器*/
        startApplicationWithPackageName("com.droidlogic.FileBrower");
    }

    private void startApplicationWithPackageName(String packagename)
    {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try
        {
            packageinfo = getPackageManager().getPackageInfo(packagename, 0);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        if (packageinfo == null)
        {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null)
        {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            startActivity(intent);
        }
    }

    private void saveFile(String mPath,String fullName)
    {
        if (fullName.endsWith(".mrc"))
        {
            saveAsNote(mPath,fullName);
        }
        else if (fullName.endsWith(".pdf"))
        {
            saveAllAsPdf(mPath,fullName);
        }
        else
        {
            saveAsPicture(mPath,fullName);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case REQUEST_FOLDER:
                    File folder = (File) data.getSerializableExtra("file_path");
                    Toast.makeText(getApplicationContext(), "已选择：" + folder.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    break;
                case REQUEST_IMAGE:
                    File file = (File) data.getSerializableExtra("file_path");
                    Toast.makeText(getApplicationContext(), "已选择：" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    if (file.exists())
                    {
                        setPanelMode(3);
                        mPanelManager.setImageEditView(mImageEditView);
                        if (!mPanelManager.insertImage(file.getAbsolutePath(), true, true))
                        {
                            Toast.makeText(this, R.string.no_more_memory, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(this, R.string.get_image_path_failed, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case REQUEST_NOTE:
                    try
                    {
                        File noteFile = (File) data.getSerializableExtra("file_path");
                        mPanelManager.restoreNote(noteFile.getAbsolutePath());
                        setPageInfo(mPanelManager.getCurrentPageIndex(), mPanelManager.getPageCount());
                        updatePageButtonState();
                        lastSavedPage = mPanelManager.getPageCount();
                        lastSavedMaterial = mPanelManager.getTotalMaterialsCount();
                    }
                    catch (Exception e2)
                    {
                        Toast.makeText(this, R.string.open_file_failed, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        BaseUtils.dbg(TAG, "onNewIntent");

        if (mShowing && intent.getBooleanExtra("key", false))
        {
            intent.putExtra("key", false);
            moveTaskToBack(true);
        }
        setIntent(intent);
        insertNote(false);
    }

    @Override public void onWindowFocusChanged(boolean hasFocus)
    {
        //jean test
//        BaseUtils.dbg(TAG, "onWindowFocusChanged");
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
        {
            mSurfaceHeight = mDrawBoard.getHeight();
            mSurfaceWidth = mDrawBoard.getWidth();
//            BaseUtils.dbg(TAG, "onWindowFocusChanged surface size = " + mSurfaceWidth + " / " + mSurfaceHeight);

            while (up_event_queue != null && up_event_queue.size() > 0){
                up_event_queue.clear();
            }
            tag_proc_upevent = 1;
            procUpEventThread = new ProcUpEventThread();
            procUpEventThread.start();
        }
    }

    private int getWindowLocationY()
    {
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int screenHeight = size.y;

        int[] location = new int[2];
        mPenSelector.getLocationOnScreen(location);
        int mMenuViewLocationY = location[1];

        return screenHeight - mMenuViewLocationY + getResources().getDimensionPixelSize(R.dimen.pen_and_erase_margin_bottom);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
//        BaseUtils.dbg(TAG, "onConfigurationChanged");
    }

   /* @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {

        if (WhiteBoardApplication.isAutoEnterEraseMode)
        {
            float size;
            String string = "";
//            BaseUtils.dbg(TAG, "mSetSize=" + (mSetSize?"true":"false"));
            if (!mSetSize)
            {
                string = BaseUtils.getSystemProperty(TOUCH_SIZE_PROP, "");
                if (string.equals(""))
                {
                    size = ev.getSize();
                    if (((double) size) > 1.0E-4d)
                    {
                        BaseUtils.setSystemProperty(TOUCH_SIZE_PROP, "" + size);
                    }
                }
            }
            if (!(mPanelManager == null || mSetSize))
            {
                mSetSize = true;
                try
                {
                    size = Float.parseFloat(string);
                }
                catch (Exception e)
                {
                    size = ev.getSize();
                }
                if (((double) size) > 1.0E-4d)
                {
                    float s2 = ((0.0010071108f * 2.0f) * 1.3f) * 2.5f;
                    float[][] sizes = new float[3][];
                    sizes[0] = new float[]{s2, 140.0f, 200.0f};
                    sizes[1] = new float[]{s2 / 2, 70.0f, 100.0f};
                    sizes[2] = new float[]{s2 / 4, 35.0f, 50.0f};
                    BaseUtils.dbg(TAG, String.format("set size: %f, %f, %f", new Object[]{Float.valueOf(s2 / 4), Float.valueOf(s2 / 2), Float.valueOf(s2)}));
                    mPanelManager.setEraseSize(sizes);
                    mPanelManager.setStdEraseSize(sizes);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }*/

    private void insertNote(boolean addPage)
    {
        Intent intent = getIntent();
        String action = intent.getAction();
        if ("newskyer.intent.action.DRAW".equals(action))
        {
            String bg = intent.getStringExtra("bg_path");
            if (bg != null && new File(bg).exists())
            {
                if (addPage)
                {
                    try
                    {
                        mDrawBoard.mPanelView.mPanelManager.addPage();
                        mPanelManager.setPage(mPanelManager.getPageCount() - 1, false);
                        updatePageButtonState();
                    }
                    catch (Exception e)
                    {
                        error("open bg file: " + BaseUtils.getStackTrace(e));
                    }
                }
                mDrawBoard.mPanelView.mPanelManager.insertImage(bg, true, true);
                setPanelMode(3);
            }
        }
        intent.putExtra("bg_path", "");
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
//            BaseUtils.dbg(TAG, "event.getKeyCode() = " + event.getKeyCode());
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    if(mMoreWindow != null && mMoreWindow.isShowing()) {
                        mMoreWindow.dismiss();
                        return true;
                    }
                    if(mNoteWindow != null && mNoteWindow.isShowing()) {
                        mNoteWindow.dismiss();
                        return true;
                    }
                    if (mPanelManager != null && mPanelManager.isLocked)
                    {
                        return true;
                    }
                    showExitDialog();
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void initView()
    {
        mRoot.setBackgroundColor(getResources().getColor(R.color.root_bg));

        mBoardGLSurfaceView = (BaseGLSurfaceView) findViewById(R.id.board_glsurfaceview);
        mDrawBoard = (DrawBoard) findViewById(R.id.drawboard);

        mDrawBoard.setBackgroundColor(getResources().getColor(R.color.drawboard_bg));

        mImageEditView = (ImageEditView) findViewById(R.id.image_edit_view);
        mPanelManager = mDrawBoard.mPanelView.mPanelManager;
        mPenSelector = findViewById(R.id.pen_selector);
        mPenSelectorLayout = findViewById(R.id.pen_selector_layout);
        mPenWidthPreview = (PenWidth) findViewById(R.id.pen_width_preview);
        mPenWidthSeekBar = (SeekBar) findViewById(R.id.pen_width_seekbar);

        mDrawBoard.mPanelView.setOpenGLView(mBoardGLSurfaceView);

        mPenWidthSeekBar.setProgress(10);
        mPenWidthSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                setPenWidth(((float) (progress + 10)) / 10.0f);
            }

            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });
        initPenColorPicker();
        initPenWidthOptions();
        mPenSelector.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mNoteWindow != null && mNoteWindow.isShowing())
                {
                    mNoteWindow.dismiss();
                    return;
                }

                if (mMoreWindow != null && mMoreWindow.isShowing())
                {
                    mMoreWindow.dismiss();
                }
                hideAllWindows(4);
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }

                if (mPenSelectorLayout.isShown())
                {
                    mPenSelectorLayout.setVisibility(View.GONE);
                    clearTimer();
                }
                else
                {
                    mPenSelectorLayout.setVisibility(View.VISIBLE);
                    clearAndResetTimer();
                }
                setPanelMode(0);
            }
        });

        undoBoard = findViewById(R.id.undo_board);
        redoBoard = findViewById(R.id.redo_board);
        undoBoard.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                hideAllWindows(0);
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                if (mNoteWindow != null && mNoteWindow.isShowing())
                {
                    mNoteWindow.dismiss();
                    return;
                }
                mDrawBoard.undoBoard();
            }
        });
        redoBoard.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                hideAllWindows(0);
                mDrawBoard.redoBoard();
            }
        });

        mBg_bt = findViewById(R.id.change_bg);
        mBg_bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mNoteWindow != null && mNoteWindow.isShowing())
                {
                    mNoteWindow.dismiss();
                    return;
                }
                if(mMoreWindow != null && mMoreWindow.isShowing()){
                    mMoreWindow.dismiss();
                    return;
                }
                if(mMathWindow != null && mMathWindow.isShowing())
                {
                    mMathWindow.dismiss();
                    return;
                }

                hideAllWindows(0);
                mBackgroundLayout.measure(0, 0);
                BaseUtils.hideNavigationBar(MainActivity.this);
                mBackgroundColorWindow.showAtLocation(mRoot, Gravity.BOTTOM | Gravity.LEFT, getResources().getDimensionPixelSize(R.dimen.x760), getWindowLocationY());

            }
        });
        math_icon = findViewById(R.id.math_icon);
        math_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                if (mNoteWindow != null && mNoteWindow.isShowing())
                {
                    mNoteWindow.dismiss();
                    return;
                }
                if (mMoreLayout != null && mMoreLayout.isShown() && mMoreWindow != null)
                {
                    mMoreWindow.dismiss();
                    return;
                }
                BaseUtils.dbg(TAG, "start --- mMoreWindow.isShowing = " + mMoreWindow.isShowing());
                if (mMathLayout != null && mMathLayout.isShown() && mMathWindow != null)
                {
                    mMathWindow.dismiss();
                    return;
                }else {
                    hideAllWindows(0);
                    mMathLayout.measure(0, 0);
                    BaseUtils.hideNavigationBar(MainActivity.this);
                    mMathWindow.showAtLocation(mRoot, Gravity.BOTTOM | Gravity.CENTER, -2, getWindowLocationY());
                }

                setPanelMode(4);
            }
        });

        mPointCrl = findViewById(R.id.Point_control);
        mPointCrl.setActivated(PanelUtils.isMultiPen());
        mPointCrl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                // 设置多点触控模式
                mPointCrl.setActivated(!PanelUtils.isMultiPen());
                SharedPreferencesUtils.setParam(Constants.IS_MULTI_PEN, !PanelUtils.isMultiPen());

                PanelUtils.setMultiPen(!PanelUtils.isMultiPen());
            }
        });

        mSelection = findViewById(R.id.selection);
        mSelection.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }

                hideAllWindows(0);
                clearAndResetTimer();
                setPanelMode(3);
            }
        });
        initPage();
        initMoreFunctionWindow();
        initMathShapeWindow();
        initEraseWindow();
        initPreviewList();
        initScaleHint();
        mDrawBoard.mPanelView.mPanelManager.setOnActionChangedListener(new OnActionChangedListener() {
            @Override
            public void onActionChanged()
            {
                undoBoard.setEnabled(mPanelManager.canUndo());
                redoBoard.setEnabled(mPanelManager.canRedo());
            }

        });
        mDrawBoard.setTouchedListener(new DrawBoard.BoardTouchedListener() {
            @Override public void touched()
            {
                hideAllWindows(0);
            }
        });
        mPanelManager.setMinScale(Constants.MIN_SCALE_SIZE);
        mPanelManager.setMaxScale(Constants.MAX_SCALE_SIZE);
        undoBoard.setEnabled(false);
        redoBoard.setEnabled(false);
        setPanelMode(0);
        insertNote(false);

        mDrawBoard.mPanelView.mPanelManager.setOnModeChangedListener(new PanelManager.OnModeChangedListener() {
            public void onModeChanged(int mode)
            {
                setPanelMode(mode);
            }
        });
        mDrawBoard.mPanelView.mPanelManager.setOnPageChangedListener(new PanelManager.OnPageChangedListener() {
            public void onPageChanged(int index)
            {
                hideAllWindows(3);
                updatePageButtonState();
                setPageInfo(mPanelManager.getCurrentPageIndex(), mPanelManager.getPageCount());
            }
        });

        PanelManager.setSelectorLineColor(-1754827);
        PanelManager.setSelectorRectColor(-12532481);
        mPanelManager.setSelectedEdgeColor(-141259);
        PanelManager.setEraseLineColor(-1754827);
    }

    private void clearAndResetTimer()
    {
        clearTimer();
        setTimer();
    }

    private void clearTimer()
    {
        handler.removeMessages(MSG_RETURN_PEN);
    }

    private void clearNoteTimer()
    {
        handler.removeMessages(MSG_NOTE_RETURN_PEN);
    }

    private void setTimer()
    {
        handler.sendEmptyMessageDelayed(MSG_RETURN_PEN, 3500);
    }

    private void hideAllWindows(int index)
    {
//        BaseUtils.dbg(TAG, "hideAllWindows---");
        handler.removeMessages(MSG_SHOW_TRANSPARENT_DLG);
        if (mMoreWindow != null && mMoreWindow.isShowing() && index != 1)
        {
            mMoreWindow.dismiss();
        }
        if (mNoteWindow != null && mNoteWindow.isShowing() && index != 6)
        {
            mNoteWindow.dismiss();
        }
        if (mEraseWindow != null && mEraseWindow.isShowing() && index != 2)
        {
            mEraseWindow.dismiss();
        }
        if (mPreviewLayout.isShown() && index != 3)
        {
            mPreviewLayout.setVisibility(View.GONE);
        }
        if (mPenSelectorLayout.isShown() && index != 4)
        {
            mPenSelectorLayout.setVisibility(View.GONE);
        }
        if(mBackgroundColorWindow != null && index != 5) {
            dismissColorView();
            mBackgroundColorWindow.dismiss();
        }
        if (mMathWindow != null && mMathWindow.isShowing() && index != 7)
        {
            mMathWindow.dismiss();
        }
    }

    private void showScaleHint(final float scale)
    {
        mScaleHint.setVisibility(View.VISIBLE);
        mScaleHint.setAlpha(1.0f);
        mScaleText.setText(String.format("%3.0f%%", new Object[]{Float.valueOf(scale * SensorManager.LIGHT_CLOUDY)}));
        mScaleHintHideAnimation.cancel();
        mScaleHint.startAnimation(mScaleHintHideAnimation);
    }

    private void showExitDialog()
    {
        if (mExitDialog == null)
        {
            mExitDialog = new ExitDialog(MainActivity.this);
        }
        if (!mExitDialog.isShowing())
        {
            mExitDialog.show();
            int totalCount = mPanelManager.getTotalMaterialsCount();
            if (mPanelManager.getPageCount() != lastSavedPage || (totalCount > 0 && totalCount != lastSavedMaterial))
            {
                mExitDialog.setMessage(getString(R.string.warning_exit));
 /*               mExitDialog.setLeftClickListener(new OnClickListener() {
                    @Override public void onClick(View v)
                    {
                        mExitDialog.dismiss();
                        mSaveButton.performClick();
                    }
                });*/
            }
            else
            {
                mExitDialog.setMessage(getString(R.string.confirm_exit));
            }
            mExitDialog.getWindow().setLayout(getResources().getDimensionPixelSize(R.dimen.exit_dialog_width), getResources().getDimensionPixelSize(R.dimen.exit_dialog_height));
            mExitDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override public void onDismiss(DialogInterface dialog)
                {
                    BaseUtils.hideNavigationBar(MainActivity.this);
                }
            });
        }
    }

    private void showSettingDialog()
    {
        if (mSettingDialog == null)
        {
            mSettingDialog = new SettingDialog(MainActivity.this);
        }
        if (!mSettingDialog.isShowing())
        {
            mSettingDialog.show();
            mSettingDialog.getWindow().setLayout(getResources().getDimensionPixelSize(R.dimen.setting_dialog_width), getResources().getDimensionPixelSize(R.dimen.setting_dialog_height));
            mSettingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override public void onDismiss(DialogInterface dialog)
                {
                    BaseUtils.hideNavigationBar(MainActivity.this);
                }
            });
        }
        mSettingDialog.setCheckUpdateListener(new OnClickListener() {
            @Override public void onClick(View v)
            {
                mSettingDialog.dismiss();
            }
        });
    }

    boolean hasUndo = false;

    public boolean deletePage(int position)
    {
        if (mPanelManager.getPageCount() <= 1)
        {
            return false;
        }
        Page page = mPanelManager.deletePageAndSet(position, -1);


        undoBoard.setEnabled(false);
        redoBoard.setEnabled(false);

        hasUndo = false;
        Snackbar snackbar = Snackbar.make(findViewById(R.id.page_snackbar), getResources().getString(R.string.page_deleted), Snackbar.LENGTH_LONG);
        if (page == null)
        {
            return false;
        }

        snackbar.setAction(getResources().getString(R.string.undo), new OnClickListener() {
            public void onClick(View v)
            {
                synchronized (mDrawBoard)
                {
                    int update = mPanelManager.getCurrentPageIndex();
                    int pos = mPanelManager.restorePage(false);
                    mPageIndex = mPanelManager.getCurrentPageIndex();
                    setPageInfo(mPageIndex, mPanelManager.getPageCount());
                    updatePageButtonState();
                    mPreviewList.scrollToPosition(mPanelManager.getCurrentPageIndex());
                    mPreviewAdapter.notifyDataSetChanged();

                    hasUndo = true;
                }
            }
        });
        snackbar.show();
        snackbar.addCallback(new Snackbar.Callback() {
            public void onDismissed(Snackbar transientBottomBar, int event)
            {
                super.onDismissed(transientBottomBar, event);
//                if (!hasUndo)
//                {
                mPanelManager.cleanAction();
//                }
                undoBoard.setEnabled(mPanelManager.canUndo());
                redoBoard.setEnabled(mPanelManager.canRedo());
                hasUndo = false;
            }
        });

        mPageIndex = mPanelManager.getCurrentPageIndex();
        setPageInfo(mPageIndex, mPanelManager.getPageCount());
        updatePageButtonState();
        mPreviewList.scrollToPosition(mPanelManager.getCurrentPageIndex());
        mPreviewAdapter.notifyDataSetChanged();
        return true;
    }

    private void initPreviewList()
    {
        mPreviewList = (SwipeRecyclerView) findViewById(R.id.preview_list);
        mPreviewLayout = (ViewGroup) findViewById(R.id.preview_layout);
        mPreviewList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        PreviewAdapter adapter = new PreviewAdapter(this, mDrawBoard.mPanelView.mPanelManager);
        mPreviewAdapter = adapter;
        mPreviewList.setAdapter(adapter);
        mPreviewLayout.postDelayed(new Runnable() {
            public void run()
            {
                mPreviewLayout.setVisibility(View.GONE);
            }
        }, 100);
        mPreviewList.setItemAnimator(new DefaultItemAnimator());
        mPreviewList.setOnItemSwipeListener(new SwipeRecyclerView.OnItemSwipeListener() {
            public boolean onItemSwipe(int position)
            {
                return deletePage(position);
            }
        });
        mPreviewLayout.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                Animator animator = ViewAnimationUtils.createCircularReveal(mPreviewList, 0, mPreviewList.getHeight(), (float) Math.hypot((double) mPreviewList.getWidth(), (double) mPreviewList.getHeight()), 0.0f);
                animator.addListener(new Animator.AnimatorListener() {
                    public void onAnimationStart(Animator animation)
                    {
                    }

                    public void onAnimationEnd(Animator animation)
                    {
                        mPreviewLayout.setVisibility(View.GONE);
                    }

                    public void onAnimationCancel(Animator animation)
                    {
                    }

                    public void onAnimationRepeat(Animator animation)
                    {
                    }
                });
                animator.setInterpolator(new DecelerateInterpolator());
                animator.setDuration(300);
                animator.start();
            }
        });
        mPageIndexTextView.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) public void onClick(View v)
            {
                if (!WhiteBoardApplication.isWhiteBoardEnable)
                {
                    return;
                }
                if (mPanelManager != null && mPanelManager.isLocked)
                {
                    return;
                }
                if (mPreviewLayout != null && mPreviewLayout.isShown())
                {
                    mPreviewLayout.setVisibility(View.GONE);
                    return;
                }
                mPreviewLayout.setVisibility(View.VISIBLE);
                mPreviewList.scrollToPosition(mPanelManager.getCurrentPageIndex());
                int cy = mPreviewList.getHeight();
                Animator animator = ViewAnimationUtils.createCircularReveal(mPreviewList, 0, cy, 0.0f, (float) Math.hypot((double) mPreviewList.getWidth(), (double) cy));
                animator.addListener(new Animator.AnimatorListener() {
                    public void onAnimationStart(Animator animation)
                    {
                    }

                    public void onAnimationEnd(Animator animation)
                    {
                        mPreviewList.getAdapter().notifyDataSetChanged();
                    }

                    public void onAnimationCancel(Animator animation)
                    {
                    }

                    public void onAnimationRepeat(Animator animation)
                    {
                    }
                });
                animator.setInterpolator(new DecelerateInterpolator());
                animator.setDuration(300);
                animator.start();
            }
        });
    }

    private void setPageInfo(int index, int count)
    {
        if (index >= 0 && index < count)
        {
            mPageIndexTextView.setText("" + (index + 1) + "/" + count);
        }
    }

    public void updatePageButtonState()
    {
        boolean z;
        boolean z2 = true;
        mPageIndex = mDrawBoard.mPanelView.mPanelManager.getCurrentPageIndex();
        View view = mAddPage;
        if (mPanelManager.getPageCount() < Constants.MAX_PAGE_COUNT)
        {
            z = true;
        }
        else
        {
            z = false;
        }
        view.setEnabled(z);
        view = mDeletePage;
        if (mPanelManager.getPageCount() > 1)
        {
            z = true;
        }
        else
        {
            z = false;
        }
        view.setEnabled(z);
        view = mPrevPage;
        if (mPageIndex > 0)
        {
            z = true;
        }
        else
        {
            z = false;
        }
        view.setEnabled(z);
        View view2 = mNextPage;
        if (mPanelManager.getPageCount() <= 1 || mPageIndex >= mPanelManager.getPageCount() - 1)
        {
            z2 = false;
        }
        view2.setEnabled(z2);
    }

    private void goPrevPage()
    {
        mPageIndex--;
        if (mPageIndex < 0)
        {
            mPageIndex = 0;
        }
//        BaseUtils.dbg(TAG, "goPrevPage mPageIndex=" + mPageIndex);
        mPanelManager.addPageSwitchAction(mPageIndex);
        mPanelManager.setPage(mPageIndex,false);
        mPageIndex = mPanelManager.getCurrentPageIndex();
        setPageInfo(mPageIndex, mPanelManager.getPageCount());
        updatePageButtonState();
    }

    private void goNextPage()
    {
        mPageIndex++;
        if (mPageIndex >= mPanelManager.getPageCount())
        {
            mPageIndex = mPanelManager.getPageCount() - 1;
        }
        mPanelManager.addPageSwitchAction(mPageIndex);
        mPanelManager.setPage(mPageIndex, false);
        mPageIndex = mPanelManager.getCurrentPageIndex();
        setPageInfo(mPageIndex, mPanelManager.getPageCount());
//        BaseUtils.dbg(TAG, "goNextPage mPageIndex=" + mPageIndex);
    }
    private boolean addAndSetPage()
    {
        if (!mPanelManager.canAddPage())
        {
            Toast.makeText(getApplicationContext(), String.format(getResources().getString(R.string.waring_add_panel), Constants.MAX_PAGE_COUNT), Toast.LENGTH_SHORT).show();
            return false;
        }
/*
        int totalCount = mPanelManager.getTotalMaterialsCount();
        int lastPageMaterial = mPanelManager.getLastPagesMaterialsCount();
        BaseUtils.dbg(TAG, "totalCount=" + totalCount + " lastPageMaterial=" + lastPageMaterial);*/
        if (mPanelManager.getLastPagesMaterialsCount() > 0) {
            mPanelManager.addPageSwitchAction(mPanelManager.getPageCount());
            int ret = mDrawBoard.mPanelView.addPage();
            if (ret >= Constants.MAX_PAGE_COUNT - 1) {
                Toast.makeText(getApplicationContext(), String.format(getResources().getString(R.string.waring_add_panel), Constants.MAX_PAGE_COUNT), Toast.LENGTH_SHORT).show();
            }
            if (ret >= 0 && ret < Constants.MAX_PAGE_COUNT) {
                mPageIndex = ret;
                if (mPanelManager.setPage(mPageIndex, false) == 0) {
                    setPageInfo(mPanelManager.getCurrentPageIndex(), mPanelManager.getPageCount());
                }
                updatePageButtonState();
                return true;
            }
        }
        return false;
    }

    private void updateMediaContents(String file)
    {
        BaseUtils.dbg(TAG,"updateMediaContents:" + file);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(file)));
        sendBroadcast(intent);
        sendBroadcast(new Intent("action_media_scanner_start"));
    }

    private Bitmap getPageBitmap(int id)
    {
        return mDrawBoard.mPanelView.getSuitBitmap(id);
    }

    private void initScaleHint()
    {
        mScaleHint = (ViewGroup) findViewById(R.id.scale_info);
        mScaleText = (TextView) findViewById(R.id.board_scale_hint);
        findViewById(R.id.board_scale_reset).setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
//                mPanelManager.resetBoard();
            }
        });
        AlphaAnimation show = (AlphaAnimation) AnimationUtils.loadAnimation(this, R.anim.show);
        AlphaAnimation hide = (AlphaAnimation) AnimationUtils.loadAnimation(this, R.anim.hide);
        mScaleHintHideAnimation = hide;
        show.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation)
            {
            }

            public void onAnimationEnd(Animation animation)
            {
            }

            public void onAnimationRepeat(Animation animation)
            {
            }
        });
        hide.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation)
            {
            }

            public void onAnimationEnd(Animation animation)
            {
            }

            public void onAnimationRepeat(Animation animation)
            {
            }
        });
        mPanelManager.setOnScaleChangedListener(new OnScaleChangedListener() {
            public void onScaleChangedListener(float scale)
            {
                showScaleHint(scale);
            }
        });
    }

    private void showTransparentDlg()
    {
        if(mTrasparentView != null){
            mTrasparentParams.width = Constants.SCREEN_WIDTH;
            mTrasparentParams.height = Constants.SCREEN_HEIGHT;
            getWindowManager().updateViewLayout(mTrasparentView, mTrasparentParams);

            handler.sendEmptyMessageDelayed(MSG_HIDE_TRANSPARENT_DLG, 40);
        }else{
            BaseUtils.dbg(TAG,"showTransparentDlg mTrasparentView is null !!");
        }
    }

    class ProcUpEventThread extends Thread {
        @Override public void run()
        {
            byte up_event = 0;
            while (tag_proc_upevent == 1)
            {
                try
                {
                    up_event = up_event_queue.take();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                    continue;
                }

                if (up_event_queue.size() > 0)
                {
                    continue;
                }

                if (0 == up_event)
                {
                    continue;
                }

                if (!isActivityRunning)
                {
                    continue;
                }

                if (isTouching == 1)
                {
                    continue;
                }

                if (handler != null)
                {
                    handler.removeMessages(MSG_SHOW_TRANSPARENT_DLG);
                    handler.sendEmptyMessageDelayed(MSG_SHOW_TRANSPARENT_DLG,500);
                }

            }
        }
    }

    private class NoteTask extends AsyncTask<String, Integer, String> {
        List<Noteinfo> mPathNameList = new ArrayList<Noteinfo>();
        AlertDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new SpotsDialog(MainActivity.this, getResources().getString(R.string.loading));
            dialog.show();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    BaseUtils.hideNavigationBar(MainActivity.this);
                }
            });
        }

        @Override
        protected String doInBackground(String... params) {
            mPathNameList.clear();
            mPathNameList = getNoteList();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (mPathNameList != null && mPathNameList.size() > 0)
                initNoteWindow(mPathNameList);
            else
                Toast.makeText(MainActivity.this, R.string.not_file, Toast.LENGTH_SHORT)
                        .show();
        }
    }

    private void setColorViewBackGround(int mColor) {
        final int[] colors = getResources().getIntArray(R.array.bg_colors);
        boolean hasSelected =false;
        for(int i=0;i<colors.length;i++){
            if(mColor == colors[i]) {
                mBgcolor[i].setSelected(true);
                hasSelected = true;
            }else
                mBgcolor[i].setSelected(false);
        }
        if(!hasSelected)
            mBgcolor[4].setSelected(true);
    }

    private void setColorViewBackGround(View v) {
        mBgcolor[0].setSelected(false);
        mBgcolor[1].setSelected(false);
        mBgcolor[2].setSelected(false);
        mBgcolor[3].setSelected(false);
        mBgcolor[4].setSelected(false);

        ((ImageView) v).setSelected(true);
    }

    private void setStyleViewBackGround(int styid) {
        for(int i=0;i<bg_shape.length;i++){
            if(styid == i)
                bg_shape[i].setSelected(true);
            else
                bg_shape[i].setSelected(false);
        }
    }

    private void setStyleViewBackGround(View v) {
        bg_shape[0].setSelected(false);
        bg_shape[1].setSelected(false);
        bg_shape[2].setSelected(false);
        bg_shape[3].setSelected(false);
        bg_shape[4].setSelected(false);
        ((ImageView) v).setSelected(true);
    }

    private void showFileBrowserPopu(int type,final int function) {
        if (mMoreWindow != null && mMoreWindow.isShowing())
            mMoreWindow.dismiss();
        lastFileName = BaseUtils.getDefaultFileName(this);
        final FileBrowserPopu fileBrowser = new FileBrowserPopu(this, this.mRoot, type, lastFileName,function);
        fileBrowser.setChooseListenter(new FileBrowserPopu.OnChooseListenter() {
            public void onConfirm(final String path,final String name,int type) {
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(path)) {
                    if(path.equals(Constants.PATH) || path.equals(Constants.USB_PATH) || path.equals(Constants.SD_PATH)){
                        Toast.makeText(MainActivity.this, R.string.no_external, Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    switch (function) {
                        case 0:
                            if(type == 0) {
                                fullName = name + ".pdf";
                            }else if(type == 1) {
                                fullName = name;
                            }else {
                                fullName = name + ".mrc";
                            }
                            saveFile(path,fullName);
                            break;
                        case 1:
                            BaseUtils.dbg(TAG,"path=" + path + " name=" + name);
                            String mtemp = path + "/" +name;
                            File file = new File(mtemp);
                            Toast.makeText(getApplicationContext(), getString(R.string.select) + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                            if (file.exists())
                            {
                                setPanelMode(3);
                                mPanelManager.setImageEditView(mImageEditView);
                                if (!mPanelManager.insertImage(file.getAbsolutePath(), true, true))
                                {
                                    Toast.makeText(MainActivity.this, R.string.no_more_memory, Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, R.string.get_image_path_failed, Toast.LENGTH_SHORT).show();
                            }
                            break;
                        default:
                            break;
                    }
                }else{
                    Toast.makeText(MainActivity.this, R.string.file_not_empty, Toast.LENGTH_SHORT)
                            .show();
                }
                fileBrowser.dismiss();
            }
        });
    }
    private boolean AppOverCare(String str) {
        if(str.toLowerCase(Locale.US).contains("com.microsoft.office.outlook"))
            return false;
        return (str.toLowerCase(Locale.US).contains("office") || str.toLowerCase(Locale.US).contains("bluetooth"));
    }
}
