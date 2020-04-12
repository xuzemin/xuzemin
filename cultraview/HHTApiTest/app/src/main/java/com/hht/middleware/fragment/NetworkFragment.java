package com.hht.middleware.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hht.android.sdk.network.HHTNetworkManager;
import com.hht.android.sdk.network.LinkConfig;
import com.hht.middleware.R;
import com.hht.middleware.adapter.DetailsAdapter;
import com.hht.middleware.base.BaseFragment;
import com.hht.middleware.bean.DetailsBean;
import com.hht.middleware.bean.ModeBean;
import com.hht.middleware.dialog.IntroduceDialog;
import com.hht.middleware.listener.OnMiddleDialogListener;
import com.hht.middleware.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: chenhu
 * Time: 2019/12/13 10:06
 * Description do somethings
 */
public class NetworkFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private ListView mListView;
    private DetailsAdapter mDetailsAdapter;
    private HHTNetworkManager mHHTNetworkManager;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHHTNetworkManager = HHTNetworkManager.getInstance();//(HHTNetworkManager) mActivity.getSystemService(HHTNetworkManager.SERVICE);
    }

    public static NetworkFragment newInstance() {
        Bundle args = new Bundle();
        NetworkFragment fragment = new NetworkFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        mListView = findViewById(R.id.fragment_network_list_view);
        mDetailsAdapter = new DetailsAdapter(getActivity(), getDataList());
        mListView.setAdapter(mDetailsAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_network_layout;
    }

    @Override
    public void OnBackListener() {
        replaceFragment(MainFragment.newInstance());
    }

    @Override
    public void OnIntroduceListener() {
        IntroduceDialog introduceDialog = new IntroduceDialog(mActivity);
        introduceDialog.setIntroduceDialogHeight(180);
        introduceDialog.show(getString(R.string.fragment_network_introduce));
    }

    private List<DetailsBean> getDataList() {
        List<DetailsBean> mList = new ArrayList<>();
        mList.add(new DetailsBean("setWolEn(boolean bEnable)", "网络唤醒设置"));
        mList.add(new DetailsBean("getWolEn()", "获取网络唤醒状态"));

        mList.add(new DetailsBean("setEthernetEnabled(boolean enable)", "设置有线开关"));
        mList.add(new DetailsBean("getEthernetState()", "获取有线开关状态"));

        mList.add(new DetailsBean("setEthernetMode(String mode,DhcpInfo dhcpInfo)", "设置网络模式"));
        mList.add(new DetailsBean("getEthernetMode()", "获取网络模式状态"));

//        mList.add(new DetailsBean("getNetLinkStatus()", "Default Interface Physical Connection State"));
//        mList.add(new DetailsBean("getNetLinkStatus(String ifaceName)", "Get Physical Connection State"));

        mList.add(new DetailsBean("getEthernetMacAddress()", "Get Ethernet Hardware Address"));
        mList.add(new DetailsBean("getEthernetIpAddress()", "Get Ethernet IP Address"));
        mList.add(new DetailsBean("setNetworkconfig(MUSUAL)", "setNetworkconfig"));
        mList.add(new DetailsBean("setNetworkconfig(AUTO)", "setNetworkconfig"));

        return mList;
    }

    private List<ModeBean> getEthernetModeDataList() {
        List<ModeBean> mList = new ArrayList<>();

        mList.add(new ModeBean("CONNECT_MODE_DHCP",
                HHTNetworkManager.ETHERNET_CONNECT_MODE_DHCP));

        mList.add(new ModeBean("CONNECT_MODE_MANUAL",
                HHTNetworkManager.ETHERNET_CONNECT_MODE_MANUAL));

        mList.add(new ModeBean("CONNECT_MODE_PPPOE",
                HHTNetworkManager.ETHERNET_CONNECT_MODE_PPPOE));

        mList.add(new ModeBean("CONNECT_MODE_NONE",
                HHTNetworkManager.ETHERNET_CONNECT_MODE_NONE));

        return mList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                if (mHHTNetworkManager != null) {
                    boolean mNetInfo = mHHTNetworkManager.setWolEn(true);
                    ToastUtils.showShortToast("setWolEn是否成功==" + mNetInfo);
                }
                // HHTNetworkManager.getInstance().setWolEn(true);
                break;
            case 1:
                if (mHHTNetworkManager != null) {
                    boolean mNetInfo = mHHTNetworkManager.getWolEn();
                    ToastUtils.showShortToast("getWolEn==" + mNetInfo);
                }
                // HHTNetworkManager.getInstance().getWolEn();
                break;
            case 2:
                if (mHHTNetworkManager != null) {
                    mHHTNetworkManager.setEthernetEnabled(true);
                    ToastUtils.showShortToast("setEthernetEnabled未提供返回值");
                }
                // HHTNetworkManager.getInstance().setEthernetEnabled(true);
                break;
            case 3:
                if (mHHTNetworkManager != null) {
                    int mNetInfo = mHHTNetworkManager.getEthernetState();
                    ToastUtils.showShortToast("getEthernetState==" + mNetInfo);
                }
                // HHTNetworkManager.getInstance().getEthernetState();
                break;
            case 4:
                showDialog(getEthernetModeDataList(), "setEthernetMode", 2);
                break;
            case 5:
                if (mHHTNetworkManager != null) {
                    String mNetInfo = mHHTNetworkManager.getEthernetMode();
                    ToastUtils.showShortToast("getEthernetMode==" + mNetInfo);
                }
                // HHTNetworkManager.getInstance().getEthernetMode();
                break;
            case 6:
                if (mHHTNetworkManager != null) {
                    String mNetInfo = mHHTNetworkManager.getEthernetMacAddress();
                    ToastUtils.showShortToast("getEthernetMacAddress==" + mNetInfo);
                }
                // HHTNetworkManager.getInstance().getEthernetMacAddress();
                break;
            case 7:
                if (mHHTNetworkManager != null) {
                    String mNetInfo = mHHTNetworkManager.getEthernetIpAddress();
                    ToastUtils.showShortToast("getEthernetIpAddress==" + mNetInfo);
                }
                // HHTNetworkManager.getInstance().getEthernetIpAddress();
                break;
            case 8:
                if (mHHTNetworkManager != null) {
                    LinkConfig config = new LinkConfig();
                    config.isDhcp = false;
                    config.ipAddress = "192.168.5.125";
                    config.netmask = "255.255.255.0";
                    config.gateway = "192.168.5.255";
                    mHHTNetworkManager.setLinkConfig(config);
                    ToastUtils.showShortToast("getEthernetIpAddress==MUSUAL");
                }
                // HHTNetworkManager.getInstance().getEthernetIpAddress();
                break;
            case 9:
                if (mHHTNetworkManager != null) {
                    LinkConfig config = new LinkConfig();
                    config.isDhcp = true;
//                    config.ipAddress = "192.168.0.206";
//                    config.netmask = "255.255.255.0";
//                    config.gateway = "";
                    mHHTNetworkManager.setLinkConfig(config);
                    ToastUtils.showShortToast("getEthernetIpAddress==AUTO");
                }
                // HHTNetworkManager.getInstance().getEthernetIpAddress();
                break;
        }
    }

    private void showDialog(final List<ModeBean> mListData, String title, int numColumns) {
        showMiddleDialog(mListData, title, numColumns, new OnMiddleDialogListener() {
            @Override
            public void onItemClick(int position) {
//                ToastUtils.showShortToast(mListData.get(position).getModeName()
//                        + "    typeStr==" + mListData.get(position).getTypeStr()
//                        + "     typeInt==" + mListData.get(position).getTypeInt());

                if (mHHTNetworkManager != null) {
                    String typeString = mListData.get(position).getTypeStr();
                    mHHTNetworkManager.setEthernetMode(typeString);
                }
                // HHTNetworkManager.getInstance().setEthernetMode(mListData.get(position).getTypeStr());
            }
        });
    }
}
