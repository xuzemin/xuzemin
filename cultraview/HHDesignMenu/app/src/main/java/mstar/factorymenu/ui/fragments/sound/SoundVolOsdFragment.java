package mstar.factorymenu.ui.fragments.sound;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;

import com.cultraview.tv.CtvCommonManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mstar.factorymenu.ui.R;
import mstar.factorymenu.ui.adapter.SoundVolOsdAdapter;
import mstar.factorymenu.ui.bean.SoundVolOsdBean;
import mstar.factorymenu.ui.listener.SeekBarListener;
import mstar.factorymenu.ui.utils.LogUtils;
import mstar.tvsetting.factory.desk.FactoryDeskImpl;
import mstar.tvsetting.factory.desk.IFactoryDesk;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SoundVolOsdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoundVolOsdFragment extends Fragment implements
        View.OnClickListener, View.OnFocusChangeListener, SeekBarListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List list;
    private IFactoryDesk factoryManager;

    private static final int SOURCE_INX = 0;

    private static final int OSD0_INX = 1;
    private static final int OSD1_INX = 2;
    private static final int OSD2_INX = 3;
    private static final int OSD3_INX = 4;
    private static final int OSD4_INX = 5;
    private static final int OSD5_INX = 6;
    private static final int OSD6_INX = 7;
    private static final int OSD7_INX = 8;
    private static final int OSD8_INX = 9;
    private static final int OSD9_INX = 10;
    private static final int OSD10_INX = 11;
    private static final int OSD20_INX = 12;
    private static final int OSD30_INX = 13;
    private static final int OSD40_INX = 14;
    private static final int OSD50_INX = 15;
    private static final int OSD60_INX = 16;
    private static final int OSD70_INX = 17;
    private static final int OSD80_INX = 18;
    private static final int OSD90_INX = 19;
    private static final int OSD100_INX = 20;


    private int osd0val = 50;
    private int osd1val = 50;
    private int osd2val = 50;
    private int osd3val = 50;
    private int osd4val = 50;
    private int osd5val = 50;
    private int osd6val = 50;
    private int osd7val = 50;
    private int osd8val = 50;
    private int osd9val = 50;
    private int osd10val = 50;
    private int osd20val = 50;
    private int osd30val = 50;
    private int osd40val = 50;
    private int osd50val = 50;
    private int osd60val = 50;
    private int osd70val = 50;
    private int osd80val = 50;
    private int osd90val = 50;
    private int osd100val = 50;
    private int currentTvInputSource = 0;
    private String[] volume_nonlinear_list_default_value;

    private boolean switch0_9 = false;

    private Handler handler = new Handler();
    private int sourceTypeIndex;
    private int[] sourceType;
    private String[] sourcearrayVNL;
    protected List<Map<String, Object>> volume_nonlinear_view_adapter_data;


    private static final int[] text_volume_nonlinear_list_name = new int[]
            {R.string.str_volume_nonlinear_osd0, R.string.str_volume_nonlinear_osd1,
                    R.string.str_volume_nonlinear_osd2, R.string.str_volume_nonlinear_osd3,
                    R.string.str_volume_nonlinear_osd4, R.string.str_volume_nonlinear_osd5,
                    R.string.str_volume_nonlinear_osd6, R.string.str_volume_nonlinear_osd7,
                    R.string.str_volume_nonlinear_osd8, R.string.str_volume_nonlinear_osd9,
                    R.string.str_volume_nonlinear_osd10, R.string.str_volume_nonlinear_osd20,
                    R.string.str_volume_nonlinear_osd30, R.string.str_volume_nonlinear_osd40,
                    R.string.str_volume_nonlinear_osd50, R.string.str_volume_nonlinear_osd60,
                    R.string.str_volume_nonlinear_osd70, R.string.str_volume_nonlinear_osd80,
                    R.string.str_volume_nonlinear_osd90, R.string.str_volume_nonlinear_osd100};
    private ListView listView;
    private SoundVolOsdAdapter soundVolOsdAdapter;

    public SoundVolOsdFragment() {
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
    public static SoundVolOsdFragment newInstance(String param1, String param2) {
        SoundVolOsdFragment fragment = new SoundVolOsdFragment();
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
        View view = inflater.inflate(R.layout.sound_vol_osd, container, false);
        initViews(view);
        factoryManager = FactoryDeskImpl.getInstance(getActivity());

        setData();
        return view;
    }

    private void setData() {
        sourceType = getSupportSourcelist();
        sourcearrayVNL = getActivity().getResources().getStringArray(
                R.array.str_array_source);
        currentTvInputSource = CtvCommonManager.getInstance().getCurrentTvInputSource();
        sourceTypeIndex = getCurrentSourceTypeIndex(sourceType,
                currentTvInputSource);
        factoryManager.setCurveType(IFactoryDesk.MS_NLA_SET_INDEX.EN_NLA_VOLUME);
        //textview_volume_nonlinear_source_val.setText(sourcearrayVNL[currentTvInputSource]);
        osd0val = factoryManager.getOsdVolumeNonlinear(0);
        osd1val = factoryManager.getOsdVolumeNonlinear(1);
        osd2val = factoryManager.getOsdVolumeNonlinear(2);
        osd3val = factoryManager.getOsdVolumeNonlinear(3);
        osd4val = factoryManager.getOsdVolumeNonlinear(4);
        osd5val = factoryManager.getOsdVolumeNonlinear(5);
        osd6val = factoryManager.getOsdVolumeNonlinear(6);
        osd7val = factoryManager.getOsdVolumeNonlinear(7);
        osd8val = factoryManager.getOsdVolumeNonlinear(8);
        osd9val = factoryManager.getOsdVolumeNonlinear(9);
        osd0val = factoryManager.getOsdVolumeNonlinear(0);
        osd10val = factoryManager.getOsdVolumeNonlinear(10);
        osd20val = factoryManager.getOsdVolumeNonlinear(20);
        osd30val = factoryManager.getOsdVolumeNonlinear(30);
        osd40val = factoryManager.getOsdVolumeNonlinear(40);
        osd50val = factoryManager.getOsdVolumeNonlinear(50);
        osd60val = factoryManager.getOsdVolumeNonlinear(60);
        osd70val = factoryManager.getOsdVolumeNonlinear(70);
        osd80val = factoryManager.getOsdVolumeNonlinear(80);
        osd90val = factoryManager.getOsdVolumeNonlinear(90);
        osd100val = factoryManager.getOsdVolumeNonlinear(100);

        volume_nonlinear_list_default_value = new String[]{Integer.toString(osd0val),
                Integer.toString(osd1val), Integer.toString(osd2val),
                Integer.toString(osd3val), Integer.toString(osd4val),
                Integer.toString(osd5val), Integer.toString(osd6val),
                Integer.toString(osd7val), Integer.toString(osd8val),
                Integer.toString(osd9val), Integer.toString(osd10val),
                Integer.toString(osd20val), Integer.toString(osd30val),
                Integer.toString(osd40val), Integer.toString(osd50val),
                Integer.toString(osd60val), Integer.toString(osd70val),
                Integer.toString(osd80val), Integer.toString(osd90val),
                Integer.toString(osd100val)};

        checkSwitchValue();
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


    private void initViews(View view) {
        list = new ArrayList();
        //initData();

        listView = view.findViewById(R.id.list_sound_vol_osd);

    }

    public void checkSwitchValue() {
        LogUtils.d("checkSwitchValue-->");
        volume_nonlinear_view_adapter_data = getData();
        for (int i = 0; i < volume_nonlinear_view_adapter_data.size(); i++) {
            LogUtils.d( "============ switch false,volume_nonlinear_view_adapter_data.LIST_NAME ="
                    + volume_nonlinear_view_adapter_data.get(i).get("volume_nonlinear_list_name"));
        }
        for (int i = 1; i < 10; i++) {
            volume_nonlinear_view_adapter_data.remove(1);
        }//删除osd2到osd9.
        int size = volume_nonlinear_view_adapter_data.size();
        LogUtils.d("checkSwitchValue-->size" + size);

        soundVolOsdAdapter = new SoundVolOsdAdapter(getActivity(), R.layout.item_vol_osd, volume_nonlinear_view_adapter_data,
                this, factoryManager);
        listView.setAdapter(soundVolOsdAdapter);
        listView.setItemsCanFocus(true);

//            listview_volume_nonlinear.setAdapter(new volumenonLinearAdapter(volumenonLinearActivity));
//            textview_volume_nonlinear_switch_val
//                    .setText(R.string.str_volume_nonlinear_switch_0_100);

    }


    /**
     * ListView数据填充
     *
     * @return
     */
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        int length = text_volume_nonlinear_list_name.length;

        String[] listName = new String[length];
        listName = getListNameFromId(text_volume_nonlinear_list_name);

        for (int i = 0; i < length; i++) {
            map = new HashMap<String, Object>();
            map.put("volume_nonlinear_list_name", listName[i]);
            map.put("volume_nonlinear_list_value", volume_nonlinear_list_default_value[i]);
            list.add(map);
        }

        return list;
    }

    /**
     * @param str_id 列表名称的资源ID
     * @return
     */
    private String[] getListNameFromId(int[] str_id) {
        String[] strs = new String[str_id.length];
        for (int i = 0; i < str_id.length; i++) {
            strs[i] = getActivity().getString(str_id[i]);
        }
        return strs;

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {

        }
    }

    @Override
    public void ItemOnProgressChanged(SeekBar seekBar, int i, boolean b) {
        LogUtils.d("ItemOnProgressChanged--->");
        switch (seekBar.getId()) {
            case 100:
                break;
            case 101:
                break;
            case 102:
                break;
            case 103:
                break;
            case 104:
                break;
            case 105:
                break;
            case 106:
                break;
            case 107:
                break;
            case 108:
                break;
            case 109:
                break;
            case 110:
                break;
        }
    }

    @Override
    public void ItemOnStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void ItemOnStopTrackingTouch(SeekBar seekBar) {
    }
}
