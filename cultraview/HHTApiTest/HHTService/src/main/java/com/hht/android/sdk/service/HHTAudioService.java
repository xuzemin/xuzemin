package com.hht.android.sdk.service;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.hht.android.sdk.audio.HHTAudioManager;
import com.hht.android.sdk.audio.IHHTAudio;
import com.mstar.android.tv.TvAudioManager;

/**
 * 音频服务
 * @author wanghang
 *
 */
public class HHTAudioService extends IHHTAudio.Stub {
    private Context mContext;

    public HHTAudioService(Context context) {
        this.mContext = context;
        Log.i("gyx", "HHTAudioService init");
    }

    /**
     * Get sound mode.
     *
     * @return int ,sound mode.
     */
    @Override
    public int getAudioSoundMode() throws RemoteException {
        return TvAudioManager.getInstance().getAudioSoundMode();
    }

    /**
     * Get SpdifOut mode.
     *
     * @return
     */
    @Override
    public int getAudioSpdifOutMode() throws RemoteException {
        return TvAudioManager.getInstance().getAudioSpdifOutMode();
    }

    /**
     * Get Avc mode.
     *
     * @return isAvcEnable, boolean value, true if enable, false if disable
     */
    @Override
    public boolean getAvcMode() throws RemoteException {
        return TvAudioManager.getInstance().getAvcMode();
    }

    /**
     * Get balance.
     *
     * @return balance value 0~100
     */
    @Override
    public int getBalance() throws RemoteException {
        return TvAudioManager.getInstance().getBalance();
    }

    /**
     * Get Bass Value.
     *
     * @return Bass Value 0~100
     */
    @Override
    public int getBass() throws RemoteException {
        return TvAudioManager.getInstance().getBass();
    }

    /**
     * Get EarPhone volume.
     *
     * @return value of EarPhone volume
     */
    @Override
    public int getEarPhoneVolume() throws RemoteException {
        return TvAudioManager.getInstance().getEarPhoneVolume();
    }

    /**
     * Get Equilizer 10kHZ EQBand value
     *
     * @return 10kHZ EQBand value 0~100
     */
    @Override
    public int getEqBand10k() throws RemoteException {
        return TvAudioManager.getInstance().getEqBand10k();
    }

    /**
     * Get Equilizer 120HZ EQBand value
     *
     * @return
     */
    @Override
    public int getEqBand120() throws RemoteException {
        return TvAudioManager.getInstance().getEqBand120();
    }

    /**
     * Get Equilizer 1500HZ EQBand value
     *
     * @return
     */
    @Override
    public int getEqBand1500() throws RemoteException {
        return TvAudioManager.getInstance().getEqBand1500();
    }

    /**
     * Get Equilizer 500HZ EQBand value
     *
     * @return
     */
    @Override
    public int getEqBand500() throws RemoteException {
        return TvAudioManager.getInstance().getEqBand500();
    }

    /**
     * Get Equilizer 5kHZ EQBand value
     *
     * @return
     */
    @Override
    public int getEqBand5k() throws RemoteException {
        return TvAudioManager.getInstance().getEqBand5k();
    }

    /**
     * Get sound mode.
     * <p>
     * HHTAudioManager.EnumVolumeMode.VOLIME_NORMAL
     * HHTAudioManager.EnumVolumeMode.VOLIME_MUTE
     * HHTAudioManager.EnumVolumeMode.VOLIME_SOURCE_MUTE
     *
     * @return int ,volume mode.
     */
//    @Override
//    public int getSystemVolumeMode() throws RemoteException {
//        // todo ???
//        AudioManager audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
//        return audioManager.getMode();
//    }

    /**
     * Get treble value.
     *
     * @return treble value 0~100
     */
    @Override
    public int getTreble() throws RemoteException {
        return TvAudioManager.getInstance().getTreble();
    }

    /**
     * Set sound mode
     *
     * @param soundMode
     * @return true:Success, or false: failed
     */
    @Override
    public boolean setAudioSoundMode(int soundMode) throws RemoteException {
        return TvAudioManager.getInstance().setAudioSoundMode(soundMode);
    }

    /**
     * set SpdifOut mode.
     *
     * @param spdifMode
     * @return boolean value, true if enable, false if disable
     */
    @Override
    public boolean setAudioSpdifOutMode(int spdifMode) throws RemoteException {
        if (spdifMode >= HHTAudioManager.EnumSpdifMode.SPDIF_TYPE_PCM.ordinal()
                && spdifMode <= HHTAudioManager.EnumSpdifMode.SPDIF_TYPE_RAW.ordinal()) {
            return TvAudioManager.getInstance().setAudioSpdifOutMode(spdifMode);
        }

        return false;
    }

    /**
     * Set Avc mode.
     *
     * @param isAvcEnable
     * @return true if operation success or false if fail.
     */
    @Override
    public boolean setAvcMode(boolean isAvcEnable) throws RemoteException {
        return TvAudioManager.getInstance().setAvcMode(isAvcEnable);
    }

    /**
     * Set balance.
     *
     * @param balanceValue
     * @return true if operation success or false if fail.
     */
    @Override
    public boolean setBalance(int balanceValue) throws RemoteException {
        return TvAudioManager.getInstance().setBalance(balanceValue);
    }

    /**
     * Set Bass volume.
     *
     * @param bassValue - 0~100
     * @return true if operation success or false if fail.
     */
    @Override
    public boolean setBass(int bassValue) throws RemoteException {
        return TvAudioManager.getInstance().setBass(bassValue);
    }

    /**
     * Set EarPhone Volume.
     *
     * @param volume
     * @return true:Success, or false: failed
     */
    @Override
    public boolean setEarPhoneVolume(int volume) throws RemoteException {
        return TvAudioManager.getInstance().setEarPhoneVolume(volume);
    }

    /**
     * Set Equilizer 10kHZ EQBand value
     *
     * @param eqValue
     * @return
     */
    @Override
    public boolean setEqBand10k(int eqValue) throws RemoteException {
        return TvAudioManager.getInstance().setEqBand10k(eqValue);
    }


    /**
     * Set Equilizer 120HZ EQBand value
     *
     * @param eqValue
     * @return boolean value, true if enable, false if disable
     */
    @Override
    public boolean setEqBand120(int eqValue) throws RemoteException {
        TvAudioManager.getInstance().setEqBand120(eqValue);
        return true;
    }

    /**
     * Set Equilizer 1500HZ EQBand value
     *
     * @param eqValue
     * @return
     */
    @Override
    public boolean setEqBand1500(int eqValue) throws RemoteException {
        TvAudioManager.getInstance().setEqBand1500(eqValue);
        return true;
    }


    /**
     * Set Equilizer 500HZ EQBand value
     *
     * @param eqValue
     * @return
     */
    @Override
    public boolean setEqBand500(int eqValue) throws RemoteException {
        return TvAudioManager.getInstance().setEqBand500(eqValue);
    }

    /**
     * Set Equilizer 5kHZ EQBand value
     *
     * @param eqValue
     * @return
     */
    @Override
    public boolean setEqBand5k(int eqValue) throws RemoteException {
        return TvAudioManager.getInstance().setEqBand5k(eqValue);
    }

//    /**
//     * Set system volume mode
//     *
//     * @param strMode
//     * @return true:Success, or false: failed
//     */
//    @Override
//    public boolean setSystemVolumeMode(String strMode) throws RemoteException {
//        AudioManager audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
//
//        // todo ???
//        int mode = AudioManager.MODE_NORMAL;
//        if (HHTAudioManager.EnumVolumeMode.VOLIME_NORMAL.toString().equals(strMode)) {
//            mode = AudioManager.MODE_NORMAL;
//            audioManager.setMode(mode);
//        } else if (HHTAudioManager.EnumVolumeMode.VOLIME_MUTE.toString().equals(strMode)) {
//            mode = AudioManager.MODE_NORMAL;
//            boolean muteFlag = audioManager.isStreamMute(AudioManager.STREAM_MUSIC);
//            int direction = !muteFlag ? AudioManager.ADJUST_MUTE : AudioManager.ADJUST_UNMUTE;
//            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, 0);
//        } else if (HHTAudioManager.EnumVolumeMode.VOLIME_SOURCE_MUTE.toString().equals(strMode)) { // todo
//            mode = AudioManager.MODE_NORMAL;
//        }
//
//        return true;
//    }

    /**
     * Set treble value.
     *
     * @param bassValue - value 0~100
     * @return true if operation success or false if fail.
     */
    @Override
    public boolean setTreble(int bassValue) throws RemoteException {
        return TvAudioManager.getInstance().setTreble(bassValue);
    }

    @Override
    public boolean setSourceVolumeMute(boolean enable) throws RemoteException {
        return false;
    }

    @Override
    public boolean getSourceVolumeMute() throws RemoteException {
        return false;
    }
}
