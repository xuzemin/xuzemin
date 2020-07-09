package mstar.factorymenu.ui.fragments.sound;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import mstar.factorymenu.ui.R;
import mstar.factorymenu.ui.utils.LogUtils;
import mstar.tvsetting.factory.desk.FactoryDeskImpl;
import mstar.tvsetting.factory.desk.IFactoryDesk;

import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SoundAdvancesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoundAdvancesFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int audioprescaleval = 0;
    private IFactoryDesk factoryManager;
    private TextView text_advanced;
    private ImageView img_remove;
    private ImageView img_add;
    private RelativeLayout relativeLayout;

    public SoundAdvancesFragment() {
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
    public static SoundAdvancesFragment newInstance(String param1, String param2) {
        SoundAdvancesFragment fragment = new SoundAdvancesFragment();
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
        View view = inflater.inflate(R.layout.sound_advanced, container, false);
        initViews(view);
        factoryManager = FactoryDeskImpl.getInstance(getActivity());
        initData();

        return view;
    }

    private void initData() {
        audioprescaleval = factoryManager.getAudioPrescale();
        LogUtils.d("audioprescaleval-->" + audioprescaleval);
        text_advanced.setText("0x" + Integer.toHexString(audioprescaleval));
    }


    private void initViews(View view) {
        relativeLayout = view.findViewById(R.id.rl_item);
        text_advanced = view.findViewById(R.id.sound_advanced_text);
        img_remove = view.findViewById(R.id.img_sound_remove);
        img_add = view.findViewById(R.id.img_sound_add);
        img_remove.setOnClickListener(this);
        img_add.setOnClickListener(this);
        relativeLayout.setNextFocusLeftId(R.id.list_sub_title);
        relativeLayout.setOnFocusChangeListener(this);

        relativeLayout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        img_add.performClick();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        img_remove.performClick();
                        return true;
                    }
                }
                return false;
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_sound_remove:
                if (audioprescaleval < 255) {
                    audioprescaleval++;
                } else {
                    audioprescaleval = 0;
                }
                text_advanced.setText("0x" + Integer.toHexString(audioprescaleval));
                factoryManager.setAudioPrescale(audioprescaleval);
                break;

            case R.id.img_sound_add:
                if (audioprescaleval > 0) {
                    audioprescaleval--;
                } else {
                    audioprescaleval = 255;
                }
                text_advanced.setText("0x" + Integer.toHexString(audioprescaleval));
                factoryManager.setAudioPrescale(audioprescaleval);

                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b) {
            relativeLayout.setBackgroundResource(R.color.colorFocus);
        } else {
            relativeLayout.setBackgroundResource(R.color.colorUnFocus);
        }
    }
}
