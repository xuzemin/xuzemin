package mstar.factorymenu.ui.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.cultraview.tv.CtvChannelManager;
import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvDatabaseManager;
import com.cultraview.tv.CtvExtraApiManager;
import com.cultraview.tv.CtvPictureManager;
import com.cultraview.tv.CtvPlayer;
import com.cultraview.tv.common.exception.CtvCommonException;
import com.cultraview.tv.common.vo.CtvEnumAvdVideoStandardType;
import com.cultraview.tv.common.vo.CtvEnumInputSource;

import mstar.factorymenu.ui.R;
import mstar.factorymenu.ui.utils.LogUtils;
import mstar.tvsetting.factory.desk.FactoryDB;
import mstar.tvsetting.factory.desk.FactoryDeskImpl;
import mstar.tvsetting.factory.desk.IFactoryDesk;
import mstar.tvsetting.factory.ui.designmenu.DesignMenuActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PictrueOverScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictrueOverScanFragment extends Fragment implements View.OnFocusChangeListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "PictrueOverScanFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    protected Handler handler = new Handler();

    //OVER SCAN
    private int hsizeval = 50;
    private int hpositionval = 50;
    private int vsizeval = 50;
    private int vpositionval = 50;
    private String[] sourcearrayOS;
    private boolean bIsScartRgb = false;

    //curve type
    private String[] curvetypearray = {
            "Volume", "Brightness", "Contrast", "Saturation", "Sharpness", "Hue", "BackLight"
    };
    private int curvetypeindex = 0;
    private int osd0val = 50;
    private int osd25val = 50;
    private int osd50val = 50;
    private int osd75val = 50;
    private int osd100val = 50;
    private int VolumeMaxVol = 100;
    private int BrightnessMaxVol = 255;
    private int ContrastMaxVol = 255;
    private int SaturationMaxVol = 255;
    private int SharpnessMaxVol = 63;
    private int HueMaxVol = 100;
    private int BackLightMaxVol = 255;
    private int BackLightStep = 256;
    private int BackLightVal = 0;
    private String[] sourcearrayNL;
    private int[] curvemaxarray = {
            VolumeMaxVol, BrightnessMaxVol, ContrastMaxVol, SaturationMaxVol, SharpnessMaxVol,
            HueMaxVol, BackLightMaxVol
    };
    private int[] sourceType;
    private int sourceTypeIndex = 0;
    private int currentTvInputSource = 0;
    private IFactoryDesk factoryManager;
    private Button btn_picture_over_scan_dtv, btn_picture_over_scan_atv, btn_picture_over_scan_av, btn_picture_over_scan_ypbpr, btn_picture_over_scan_hdmi, btn_picture_over_scan_vga;
    private SeekBar seekBar_picture_over_scan_hsize, seekBar_picture_over_scan_hposition, seekBar_picture_over_scan_vsize, seekBar_picture_over_scan_vposition;
    private TextView textView_picture_over_scan_hsize, textView_picture_over_scan_hposition, textView_picture_over_scan_vsize, textView_picture_over_scan_vposition;
    private LinearLayout ll_picture_over_scan_hsize;
    private LinearLayout ll_picture_over_scan_hposition;
    private LinearLayout ll_picture_over_scan_vsize;
    private LinearLayout ll_picture_over_scan_vposition;


    public PictrueOverScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PictrueModeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PictrueOverScanFragment newInstance(String param1, String param2) {
        PictrueOverScanFragment fragment = new PictrueOverScanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_over_scan, container, false);

        initViews(view);
        initOverScan();
        return view;
    }

    private void initViews(View view) {
        ll_picture_over_scan_hsize = view.findViewById(R.id.ll_picture_over_scan_hsize);
        ll_picture_over_scan_hposition = view.findViewById(R.id.ll_picture_over_scan_hposition);
        ll_picture_over_scan_vsize = view.findViewById(R.id.ll_picture_over_scan_vsize);
        ll_picture_over_scan_vposition = view.findViewById(R.id.ll_picture_over_scan_vposition);


        btn_picture_over_scan_dtv = view.findViewById(R.id.btn_picture_over_scan_dtv);
        btn_picture_over_scan_atv = view.findViewById(R.id.btn_picture_over_scan_atv);
        btn_picture_over_scan_av = view.findViewById(R.id.btn_picture_over_scan_av);
        btn_picture_over_scan_ypbpr = view.findViewById(R.id.btn_picture_over_scan_ypbpr);
        btn_picture_over_scan_hdmi = view.findViewById(R.id.btn_picture_over_scan_hdmi);
        btn_picture_over_scan_vga = view.findViewById(R.id.btn_picture_over_scan_vga);

        seekBar_picture_over_scan_hsize = view.findViewById(R.id.picture_over_scan_hsize);
        seekBar_picture_over_scan_hposition = view.findViewById(R.id.picture_over_scan_hposition);
        seekBar_picture_over_scan_vsize = view.findViewById(R.id.picture_over_scan_vsize);
        seekBar_picture_over_scan_vposition = view.findViewById(R.id.picture_over_scan_vposition);


        textView_picture_over_scan_hsize = view.findViewById(R.id.picture_over_scan_hsize_text);
        textView_picture_over_scan_hposition = view.findViewById(R.id.picture_over_scan_hposition_text);
        textView_picture_over_scan_vsize = view.findViewById(R.id.picture_over_scan_vsize_text);
        textView_picture_over_scan_vposition = view.findViewById(R.id.picture_over_scan_vposition_text);
        btn_picture_over_scan_dtv.setNextFocusLeftId(R.id.list_sub_title);
        btn_picture_over_scan_dtv.setOnFocusChangeListener(this);
        btn_picture_over_scan_dtv.setOnClickListener(this);
        btn_picture_over_scan_atv.setOnFocusChangeListener(this);
        btn_picture_over_scan_atv.setOnClickListener(this);
        btn_picture_over_scan_av.setOnFocusChangeListener(this);
        btn_picture_over_scan_av.setOnClickListener(this);
        btn_picture_over_scan_ypbpr.setOnFocusChangeListener(this);
        btn_picture_over_scan_ypbpr.setOnClickListener(this);
        btn_picture_over_scan_hdmi.setOnFocusChangeListener(this);
        btn_picture_over_scan_hdmi.setOnClickListener(this);
        btn_picture_over_scan_vga.setOnFocusChangeListener(this);
        btn_picture_over_scan_vga.setOnClickListener(this);



        seekBar_picture_over_scan_hsize.setOnFocusChangeListener(this);
        seekBar_picture_over_scan_hposition.setOnFocusChangeListener(this);
        seekBar_picture_over_scan_vsize.setOnFocusChangeListener(this);
        seekBar_picture_over_scan_vposition.setOnFocusChangeListener(this);

    }

    /**
     * Over Scan
     */
    private void initOverScan() {
        factoryManager = FactoryDeskImpl.getInstance(getActivity());
        currentTvInputSource = CtvCommonManager.getInstance().getCurrentTvInputSource();
        sourceType = getSupportSourcelist();
        sourceTypeIndex = getCurrentSourceTypeIndex(sourceType,
                currentTvInputSource);
        if ((currentTvInputSource == CtvCommonManager.INPUT_SOURCE_SCART)
                || (currentTvInputSource == CtvCommonManager.INPUT_SOURCE_SCART2)) {
            bIsScartRgb = CtvExtraApiManager.getInstance().getScartRGBMode();
        } else {
            bIsScartRgb = false;
        }
        if (bIsScartRgb) {
            int[] scartRgbOverscan = getScartRgbOverscan();
            hsizeval = scartRgbOverscan[0];
            hpositionval = scartRgbOverscan[1];
            vsizeval = scartRgbOverscan[2];
            vpositionval = scartRgbOverscan[3];
        } else {
            hsizeval = factoryManager.getOverScanHsize();
            hpositionval = factoryManager.getOverScanHposition();
            vsizeval = factoryManager.getOverScanVsize();
            vpositionval = factoryManager.getOverScanVposition();
        }
        clearOverScanBtnState();
        if (currentTvInputSource == 23 || currentTvInputSource == 24 || currentTvInputSource == 25 || currentTvInputSource == 26) {
            btn_picture_over_scan_hdmi.setBackgroundResource(R.color.colorFocus);
        } else if (currentTvInputSource == 28) {
            btn_picture_over_scan_dtv.setBackgroundResource(R.color.colorFocus);
        } else if (currentTvInputSource == 1) {
            btn_picture_over_scan_atv.setBackgroundResource(R.color.colorFocus);
        } else if (currentTvInputSource == 2) {
            btn_picture_over_scan_av.setBackgroundResource(R.color.colorFocus);
        } else if (currentTvInputSource == 16) {
            btn_picture_over_scan_ypbpr.setBackgroundResource(R.color.colorFocus);
        } else if (currentTvInputSource == 0) {
            btn_picture_over_scan_vga.setBackgroundResource(R.color.colorFocus);
        }
        factoryManager.setOverScanSourceType(CtvEnumInputSource.values()[currentTvInputSource]);
        Log.d(TAG, "hsizeval:" + hsizeval + "hpositionval:" + hpositionval + "vsizeval:" + vsizeval + "vpositionval:" + vpositionval);
        seekBar_picture_over_scan_hsize.setProgress(hsizeval);
        seekBar_picture_over_scan_hposition.setProgress(hpositionval);
        seekBar_picture_over_scan_vsize.setProgress(vsizeval);
        seekBar_picture_over_scan_vposition.setProgress(vpositionval);
        textView_picture_over_scan_hsize.setText(hsizeval + "");
        textView_picture_over_scan_hposition.setText(hpositionval + "");
        textView_picture_over_scan_vsize.setText(vsizeval + "");
        textView_picture_over_scan_vposition.setText(vpositionval + "");


        seekBar_picture_over_scan_hsize.setOnSeekBarChangeListener(this);
        seekBar_picture_over_scan_hposition.setOnSeekBarChangeListener(this);
        seekBar_picture_over_scan_vsize.setOnSeekBarChangeListener(this);
        seekBar_picture_over_scan_vposition.setOnSeekBarChangeListener(this);

    }

    private void clearOverScanBtnState() {
        btn_picture_over_scan_dtv.setBackgroundResource(R.color.colorUnFocus);
        btn_picture_over_scan_atv.setBackgroundResource(R.color.colorUnFocus);
        btn_picture_over_scan_av.setBackgroundResource(R.color.colorUnFocus);
        btn_picture_over_scan_ypbpr.setBackgroundResource(R.color.colorUnFocus);
        btn_picture_over_scan_hdmi.setBackgroundResource(R.color.colorUnFocus);
        btn_picture_over_scan_vga.setBackgroundResource(R.color.colorUnFocus);
    }

    public int[] getSupportSourcelist() {
        int j = 0;
        int[] sourceIndexList = null;
        int[] sourceListFlag = CtvCommonManager.getInstance().getSourceList();
        String[] sourcearrayDM = getActivity().getResources().getStringArray(R.array.str_array_source);
        if (sourceListFlag != null) {
            for (int i = 0; i < sourcearrayDM.length; i++) {
                if (sourceListFlag[i] > 0) {
                    j++;
                }
            }
            sourceIndexList = new int[j];
            j = 0;
            for (int i = 0; i < sourcearrayDM.length; i++) {
                if (sourceListFlag[i] > 0) {
                    sourceIndexList[j] = i;
                    j++;
                }
            }
        }
        return sourceIndexList;
    }

    public int getCurrentSourceTypeIndex(int[] sourceType, int currentTvInputSource) {
        int ret = 0;
        if (sourceType != null) {
            for (int i = 0; i < sourceType.length; i++) {
                if (sourceType[i] == currentTvInputSource) {
                    ret = i;
                    break;
                }
            }
        }
        return ret;
    }

    public IFactoryDesk.EN_VD_SIGNALTYPE vdStd_MapiTypeToVdType(CtvEnumAvdVideoStandardType vdStdMapiType) {
        IFactoryDesk.EN_VD_SIGNALTYPE eVDSinType;

        switch (vdStdMapiType) {
            case PAL_BGHI:
                eVDSinType = IFactoryDesk.EN_VD_SIGNALTYPE.SIG_PAL;
                break;
            case NTSC_M:
                eVDSinType = IFactoryDesk.EN_VD_SIGNALTYPE.SIG_NTSC;
                break;
            case SECAM:
                eVDSinType = IFactoryDesk.EN_VD_SIGNALTYPE.SIG_SECAM;
                break;
            case NTSC_44:
                eVDSinType = IFactoryDesk.EN_VD_SIGNALTYPE.SIG_NTSC_443;
                break;
            case PAL_M:
                eVDSinType = IFactoryDesk.EN_VD_SIGNALTYPE.SIG_PAL_M;
                break;
            case PAL_N:
                eVDSinType = IFactoryDesk.EN_VD_SIGNALTYPE.SIG_PAL_NC;
                break;
            case PAL_60:
                eVDSinType = IFactoryDesk.EN_VD_SIGNALTYPE.SIG_NTSC_443;
                break;
            default:
                eVDSinType = IFactoryDesk.EN_VD_SIGNALTYPE.SIG_PAL;
                break;
        }
        return eVDSinType;
    }

    public int[] getScartRgbOverscan() {
        int iHSize = 0;
        int iHPosition = 0;
        int iVSize = 0;
        int iVPosition = 0;
        SQLiteDatabase mSQLiteDatabase = SQLiteDatabase.openDatabase(
                "/tvdatabase/Database/factory.db", null, SQLiteDatabase.OPEN_READONLY);
        if (mSQLiteDatabase != null) {
            int arcMode = FactoryDB.getInstance(getActivity())
                    .queryArcMode(currentTvInputSource);
            int vdStd = 0;
            try {
                vdStd = vdStd_MapiTypeToVdType(CtvPlayer.getInstance().getVideoStandard())
                        .ordinal();
            } catch (CtvCommonException e) {
                Log.e(TAG, "getVideoStandard error");
            }
            String sql_cmd = "SELECT * FROM tbl_ScartRgbOverscanAdjust WHERE FactoryOverScanType="
                    + String.valueOf(vdStd) + " and _id=" + String.valueOf(arcMode);
            Log.d(TAG, "sql: " + sql_cmd);
            Cursor cursor = mSQLiteDatabase.rawQuery(sql_cmd, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    try {
                        // Exchange H Pos and Size as others.
                        iHPosition = cursor.getInt(cursor.getColumnIndexOrThrow("u8HCrop_Left"));
                        iHSize = cursor.getInt(cursor.getColumnIndexOrThrow("u8HCrop_Right"));
                        iVSize = cursor.getInt(cursor.getColumnIndexOrThrow("u8VCrop_Up"));
                        iVPosition = cursor.getInt(cursor.getColumnIndexOrThrow("u8VCrop_Down"));
                    } catch (IllegalArgumentException e) {
                        Log.e(TAG, "query scart rgb db fail when cursor.getInt");
                    }
                } else {
                    Log.e(TAG, "query scart rgb db fail when cursor.moveToFirst()");
                }
            } else {
                Log.e(TAG, "query scart rgb db fail");
            }
            mSQLiteDatabase.close();
        } else {
            Log.e(TAG, "open scart rgb db fail");
        }
        int[] ret = {
                iHSize, iHPosition, iVSize, iVPosition
        };
        return ret;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b) {
            switch (view.getId()) {
                case R.id.btn_picture_over_scan_dtv:
                    clearOverScanBtnState();
                    btn_picture_over_scan_dtv.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.btn_picture_over_scan_atv:
                    clearOverScanBtnState();
                    btn_picture_over_scan_atv.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.btn_picture_over_scan_av:
                    clearOverScanBtnState();
                    btn_picture_over_scan_av.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.btn_picture_over_scan_ypbpr:
                    clearOverScanBtnState();
                    btn_picture_over_scan_ypbpr.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.btn_picture_over_scan_hdmi:
                    clearOverScanBtnState();
                    btn_picture_over_scan_hdmi.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.btn_picture_over_scan_vga:
                    clearOverScanBtnState();
                    btn_picture_over_scan_vga.setBackgroundResource(R.color.colorFocus);
                    break;

                case R.id.picture_over_scan_hsize:
                    ll_picture_over_scan_hsize.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_over_scan_hposition:
                    ll_picture_over_scan_hposition.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_over_scan_vsize:
                    ll_picture_over_scan_vsize.setBackgroundResource(R.color.colorFocus);

                    break;
                case R.id.picture_over_scan_vposition:
                    ll_picture_over_scan_vposition.setBackgroundResource(R.color.colorFocus);
                    break;
            }
        } else {
            switch (view.getId()) {
                case R.id.picture_over_scan_hsize:
                case R.id.picture_over_scan_hposition:
                case R.id.picture_over_scan_vsize:
                case R.id.picture_over_scan_vposition:
                    clearLLstate();
                    break;
            }
        }

    }

    private void clearLLstate() {
        ll_picture_over_scan_hsize.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_over_scan_hposition.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_over_scan_vsize.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_over_scan_vposition.setBackgroundResource(R.color.colorUnFocus);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_picture_over_scan_dtv:
                clearOverScanBtnState();
                btn_picture_over_scan_dtv.setBackgroundResource(R.color.colorFocus);
                freshOverScanData(sourceType[8]);
                break;
            case R.id.btn_picture_over_scan_atv:
                clearOverScanBtnState();
                btn_picture_over_scan_atv.setBackgroundResource(R.color.colorFocus);
                freshOverScanData(sourceType[1]);
                break;
            case R.id.btn_picture_over_scan_av:
                clearOverScanBtnState();
                btn_picture_over_scan_av.setBackgroundResource(R.color.colorFocus);
                freshOverScanData(sourceType[2]);

                break;
            case R.id.btn_picture_over_scan_ypbpr:
                clearOverScanBtnState();
                btn_picture_over_scan_ypbpr.setBackgroundResource(R.color.colorFocus);
                freshOverScanData(sourceType[3]);
                break;
            case R.id.btn_picture_over_scan_hdmi:
                clearOverScanBtnState();
                btn_picture_over_scan_hdmi.setBackgroundResource(R.color.colorFocus);
                int tmpTvInputSource = CtvCommonManager.getInstance().getCurrentTvInputSource();
                if (tmpTvInputSource == 23 || tmpTvInputSource == 24 || tmpTvInputSource == 25 || tmpTvInputSource == 26) {
                    currentTvInputSource = CtvCommonManager.getInstance().getCurrentTvInputSource(); //HDMI1 HDMI2 HDMI3
                } else {
                    currentTvInputSource = 23; //// TODO: 2020/3/11 多个HDMI口问题
                }
                freshOverScanData(currentTvInputSource);
                break;
            case R.id.btn_picture_over_scan_vga:
                clearOverScanBtnState();
                btn_picture_over_scan_vga.setBackgroundResource(R.color.colorFocus);
                freshOverScanData(sourceType[0]);
                break;
        }

    }

    private void freshOverScanData(int i) {
        currentTvInputSource = i;
        changeSource();
    }

    public void changeSource() {
        handler.removeCallbacks(changesource);
        handler.postDelayed(changesource, 500);
    }

    Runnable changesource = new Runnable() {

        @Override
        public void run() {
            CtvCommonManager.getInstance().setInputSource(currentTvInputSource);
            factoryManager.setOverScanSourceType(CtvEnumInputSource.values()[currentTvInputSource]);
            freshOverScan();
            if (currentTvInputSource == CtvCommonManager.INPUT_SOURCE_ATV) {
                int curChannelNumber = CtvChannelManager.getInstance().getCurrentChannelNumber();
                if (curChannelNumber > 0xFF) {
                    curChannelNumber = 0;
                }
                CtvChannelManager.getInstance().setAtvChannel(curChannelNumber);
            } else if (currentTvInputSource == CtvCommonManager.INPUT_SOURCE_DTV) {
                CtvChannelManager.getInstance().playDtvCurrentProgram();
            }
            DesignMenuActivity activity = (DesignMenuActivity) getActivity();
            activity.RefreshList();
        }
    };

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (this != null && !hidden) {
            LogUtils.d("onHiddenChanged");
            clearOverScanBtnState();
            if (currentTvInputSource == 23 || currentTvInputSource == 24 || currentTvInputSource == 25 || currentTvInputSource == 26) {
                btn_picture_over_scan_hdmi.setBackgroundResource(R.color.colorFocus);
            } else if (currentTvInputSource == 28) {
                btn_picture_over_scan_dtv.setBackgroundResource(R.color.colorFocus);
            } else if (currentTvInputSource == 1) {
                btn_picture_over_scan_atv.setBackgroundResource(R.color.colorFocus);
            } else if (currentTvInputSource == 2) {
                btn_picture_over_scan_av.setBackgroundResource(R.color.colorFocus);
            } else if (currentTvInputSource == 16) {
                btn_picture_over_scan_ypbpr.setBackgroundResource(R.color.colorFocus);
            } else if (currentTvInputSource == 0) {
                btn_picture_over_scan_vga.setBackgroundResource(R.color.colorFocus);
            }
        }
    }
    public void freshOverScan() {
        if ((currentTvInputSource == CtvCommonManager.INPUT_SOURCE_SCART)
                || (currentTvInputSource == CtvCommonManager.INPUT_SOURCE_SCART2)) {
            bIsScartRgb = CtvExtraApiManager.getInstance().getScartRGBMode();
        } else {
            bIsScartRgb = false;
        }
        if (bIsScartRgb) {
            int[] scartRgbOverscan = getScartRgbOverscan();
            hsizeval = scartRgbOverscan[0];
            hpositionval = scartRgbOverscan[1];
            vsizeval = scartRgbOverscan[2];
            vpositionval = scartRgbOverscan[3];
        } else {
            hsizeval = factoryManager.getOverScanHsize();
            hpositionval = factoryManager.getOverScanHposition();
            vsizeval = factoryManager.getOverScanVsize();
            vpositionval = factoryManager.getOverScanVposition();
        }
        hsizeval = factoryManager.getOverScanHsize();
        hpositionval = factoryManager.getOverScanHposition();
        vsizeval = factoryManager.getOverScanVsize();
        vpositionval = factoryManager.getOverScanVposition();

        if (currentTvInputSource == 23 || currentTvInputSource == 24 || currentTvInputSource == 25 || currentTvInputSource == 26) {
            btn_picture_over_scan_hdmi.setBackgroundResource(R.color.colorFocus);
        } else if (currentTvInputSource == 28) {
            btn_picture_over_scan_dtv.setBackgroundResource(R.color.colorFocus);
        } else if (currentTvInputSource == 1) {
            btn_picture_over_scan_atv.setBackgroundResource(R.color.colorFocus);
        } else if (currentTvInputSource == 2) {
            btn_picture_over_scan_av.setBackgroundResource(R.color.colorFocus);
        } else if (currentTvInputSource == 16) {
            btn_picture_over_scan_ypbpr.setBackgroundResource(R.color.colorFocus);
        } else if (currentTvInputSource == 0) {
            btn_picture_over_scan_vga.setBackgroundResource(R.color.colorFocus);
        }
        Log.d(TAG, "hsizeval:" + hsizeval + "hpositionval:" + hpositionval + "vsizeval:" + vsizeval + "vpositionval:" + vpositionval);
        seekBar_picture_over_scan_hsize.setProgress(hsizeval);
        seekBar_picture_over_scan_hposition.setProgress(hpositionval);
        seekBar_picture_over_scan_vsize.setProgress(vsizeval);
        seekBar_picture_over_scan_vposition.setProgress(vpositionval);
        textView_picture_over_scan_hsize.setText(hsizeval + "");
        textView_picture_over_scan_hposition.setText(hpositionval + "");
        textView_picture_over_scan_vsize.setText(vsizeval + "");
        textView_picture_over_scan_vposition.setText(vpositionval + "");

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.picture_over_scan_hsize:
                Log.d(TAG, "picture_over_scan_hsize" + i + "bIsScartRgb:" + bIsScartRgb);
                hsizeval = i;
                if (bIsScartRgb) {
                    setScartRgbOverscan(hsizeval, hpositionval, vsizeval, vpositionval);
                } else {
                    factoryManager.setOverScanHsize((short) hsizeval);
                }
                textView_picture_over_scan_hsize.setText(Integer.toString(hsizeval));
                break;
            case R.id.picture_over_scan_hposition:
                Log.d(TAG, "picture_over_scan_hposition" + i + "bIsScartRgb:" + bIsScartRgb);
                hpositionval = i;
                if (bIsScartRgb) {
                    setScartRgbOverscan(hsizeval, hpositionval, vsizeval, vpositionval);
                } else {
                    factoryManager.setOverScanHposition((short) hpositionval);
                }
                textView_picture_over_scan_hposition.setText(Integer.toString(hpositionval));

                break;
            case R.id.picture_over_scan_vsize:
                vsizeval = i;
                if (bIsScartRgb) {
                    setScartRgbOverscan(hsizeval, hpositionval, vsizeval, vpositionval);
                } else {
                    factoryManager.setOverScanVsize((short) vsizeval);
                }
                textView_picture_over_scan_vsize.setText(Integer.toString(vsizeval));

                break;
            case R.id.picture_over_scan_vposition:
                vpositionval = i;
                if (bIsScartRgb) {
                    setScartRgbOverscan(hsizeval, hpositionval, vsizeval, vpositionval);
                } else {
                    factoryManager.setOverScanVposition((short) vpositionval);
                }
                textView_picture_over_scan_vposition.setText(Integer.toString(vpositionval));
                break;

        }
    }

    public void setScartRgbOverscan(int iHSize, int iHPosition, int iVSize, int iVPosition) {
        SQLiteDatabase mSQLiteDatabase = SQLiteDatabase.openDatabase(
                "/tvdatabase/Database/factory.db", null, SQLiteDatabase.OPEN_READWRITE);
        if (mSQLiteDatabase != null) {
            int arcMode = FactoryDB.getInstance(getActivity())
                    .queryArcMode(currentTvInputSource);
            int vdStd = 0;
            try {
                vdStd = vdStd_MapiTypeToVdType(CtvPlayer.getInstance().getVideoStandard())
                        .ordinal();
            } catch (CtvCommonException e) {
                Log.e(TAG, "getVideoStandard error");
            }
            ContentValues vals = new ContentValues();
            // Exchange H Pos and Size as others.
            vals.put("u8HCrop_Left", iHPosition);
            vals.put("u8HCrop_Right", iHSize);
            vals.put("u8VCrop_Up", iVSize);
            vals.put("u8VCrop_Down", iVPosition);
            mSQLiteDatabase.update("tbl_ScartRgbOverscanAdjust", vals,
                    "FactoryOverScanType=? and _id=?", new String[]{
                            String.valueOf(vdStd), String.valueOf(arcMode)
                    });
            mSQLiteDatabase.close();
            try {
                CtvDatabaseManager.getInstance().setDatabaseDirtyByApplication((short) 0x2B);// T_OverscanAdjust_IDX
                CtvPictureManager.getInstance().setOverscan(iHSize, iHPosition, iVSize, iVPosition);
            } catch (CtvCommonException e1) {
                e1.printStackTrace();
            }
        } else {
            Log.e(TAG, "open scart rgb db fail");
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
