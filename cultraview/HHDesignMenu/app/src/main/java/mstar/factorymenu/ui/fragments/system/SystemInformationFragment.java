package mstar.factorymenu.ui.fragments.system;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;

import mstar.factorymenu.ui.R;
import mstar.factorymenu.ui.holder.SystemViewHodler;
import mstar.tvsetting.factory.desk.FactoryDeskImpl;
import mstar.tvsetting.factory.desk.IFactoryDesk;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SystemInformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SystemInformationFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, View.OnFocusChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView imageView_uart_enable;
    private ImageView imageView_rs232_command;
    private IFactoryDesk factoryManager;
    private short watchDogMode;

    private String[] watchdogenable = {
            "Off", "On"
    };
    private ImageView imageViewWatchDog;
    private SystemViewHodler systemViewHodler;

    public SystemInformationFragment() {
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
    public static SystemInformationFragment newInstance(String param1, String param2) {
        SystemInformationFragment fragment = new SystemInformationFragment();
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
        View view = inflater.inflate(R.layout.system_information, container, false);
        factoryManager = FactoryDeskImpl.getInstance(getActivity());
        initViews(view);
        systemViewHodler = new SystemViewHodler(view, getActivity(), factoryManager);
        return view;
    }


    private void initViews(View view) {

    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

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
    public void onDestroy() {
        super.onDestroy();
        systemViewHodler.onDestroy();
    }

    public void clearOnFocus() {
        if (systemViewHodler != null) {
            systemViewHodler.clearOnFocus();
        }
    }
}
