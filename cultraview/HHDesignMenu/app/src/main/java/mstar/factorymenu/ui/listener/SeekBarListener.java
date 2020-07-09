package mstar.factorymenu.ui.listener;

import android.widget.SeekBar;

public interface SeekBarListener {

    void ItemOnProgressChanged(SeekBar seekBar, int i, boolean b);

    void ItemOnStartTrackingTouch(SeekBar seekBar);

    void ItemOnStopTrackingTouch(SeekBar seekBar);
}
