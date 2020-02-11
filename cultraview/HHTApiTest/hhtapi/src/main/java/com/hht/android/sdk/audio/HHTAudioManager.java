package com.hht.android.sdk.audio;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

/**
 * HHTAudioManager 是音频接口管理类。26
 * 比如：
 * 声音模式，
 * spdif模式，
 * 高音，低音，
 * 均衡器，等
 */
public class HHTAudioManager {
    public static final String SERVICE = "sdk_AudioManager";

    private static IHHTAudio mService = null;


    public enum EnumSoundMode {
        SOUND_MODE_STANDARD,
        SOUND_MODE_MUSIC,
        SOUND_MODE_SPORTS,
        SOUND_MODE_USER
    }

    public enum EnumSpdifMode {
        SPDIF_TYPE_PCM,
        SPDIF_TYPE_OFF,
        SPDIF_TYPE_RAW
    }

    public enum EnumVolumeMode {
        VOLIME_NORMAL,
        VOLIME_MUTE,
        VOLIME_SOURCE_MUTE
    }

    private static HHTAudioManager INSTANCE;

    private HHTAudioManager() {
        IBinder service = ServiceManager.getService(HHTAudioManager.SERVICE);
        mService = IHHTAudio.Stub.asInterface(service);
    }

    public static HHTAudioManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HHTAudioManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HHTAudioManager();
                }
            }
        }
        return INSTANCE;
    }

//    /**
//     * Set system volume mode
//     *
//     * @param strMode
//     * @return true:Success, or false: failed
//     */
//    public boolean setSystemVolumeMode(String strMode) {
//        try {
//            return mService.setSystemVolumeMode(strMode);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    /**
//     * Get sound mode.
//     *
//     * @return
//     */
//    public int getSystemVolumeMode() {
//        try {
//            return mService.getSystemVolumeMode();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }

    /**
     * Set sound mode
     *
     * @param soundMode
     * @return true:Success, or false: failed
     */
    public boolean setAudioSoundMode(int soundMode) {
        try {
            return mService.setAudioSoundMode(soundMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get sound mode.
     *
     * @return int ,sound mode.
     */
    public int getAudioSoundMode() {
        try {
            return mService.getAudioSoundMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

/*    *
     * Set EarPhone Volume.
     *
     * @param volume
     * @return true:Success, or false: failed*/

    public boolean setEarPhoneVolume(int volume) {
        try {
            return mService.setEarPhoneVolume(volume);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }
/*
    *//**
     * Set EarPhone Volume,According to the saveDb determine whether to save the database.
     *
     * @param volume - of volume
     * @param saveDb - boolean value, true if save, false if not save
     * @return true:Success, or false: failed
     *//*
    public boolean setEarPhoneVolume(int volume, boolean saveDb) {
        TvAudioManager.getInstance().setEarPhoneVolume(volume, saveDb);
        return true;
    }*/

    /**
     * Get EarPhone volume.
     *
     * @return value of EarPhone volume
     */
    public int getEarPhoneVolume() {
        try {
            return mService.getEarPhoneVolume();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Set Bass volume.
     *
     * @param bassValue - 0~100
     * @return true if operation success or false if fail.
     */
    public boolean setBass(int bassValue) {
        try {
            return mService.setBass(bassValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get Bass Value.
     *
     * @return Bass Value 0~100
     */
    public int getBass() {
        try {
            return mService.getBass();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Set treble value.
     *
     * @param bassValue - value 0~100
     * @return true if operation success or false if fail.
     */
    public boolean setTreble(int bassValue) {
        try {
            return mService.setTreble(bassValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get treble value.
     *
     * @return treble value 0~100
     */
    public int getTreble() {
        try {
            return mService.getTreble();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Set balance.
     *
     * @param balanceValue
     * @return true if operation success or false if fail.
     */
    public boolean setBalance(int balanceValue) {
        try {
            return mService.setBalance(balanceValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get balance.
     *
     * @return balance value 0~100
     */
    public int getBalance() {
        try {
            return mService.getBalance();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Set Avc mode.
     *
     * @param isAvcEnable
     * @return true if operation success or false if fail.
     */
    public boolean setAvcMode(boolean isAvcEnable) {
        try {
            return mService.setAvcMode(isAvcEnable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get Avc mode.
     *
     * @return isAvcEnable, boolean value, true if enable, false if disable
     */
    public boolean getAvcMode() {
        try {
            return mService.getAvcMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * set SpdifOut mode.
     *
     * @param spdifMode
     * @return boolean value, true if enable, false if disable
     */
    public boolean setAudioSpdifOutMode(int spdifMode) {
        try {
            return mService.setAudioSpdifOutMode(spdifMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get SpdifOut mode.
     *
     * @return
     */
    public int getAudioSpdifOutMode() {
        try {
            return mService.getAudioSpdifOutMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Set Equilizer 120HZ EQBand value
     *
     * @param eqValue
     * @return boolean value, true if enable, false if disable
     */
    public boolean setEqBand120(int eqValue) {
        try {
            return mService.setEqBand120(eqValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get Equilizer 120HZ EQBand value
     *
     * @return
     */
    public int getEqBand120() {
        try {
            return mService.getEqBand120();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Set Equilizer 500HZ EQBand value
     *
     * @param eqValue
     * @return
     */
    public boolean setEqBand500(int eqValue) {
        try {
            return mService.setEqBand500(eqValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get Equilizer 500HZ EQBand value
     *
     * @return
     */
    public int getEqBand500() {
        try {
            return mService.getEqBand500();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Set Equilizer 1500HZ EQBand value
     *
     * @param eqValue
     * @return
     */
    public boolean setEqBand1500(int eqValue) {
        try {
            return mService.setEqBand1500(eqValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get Equilizer 1500HZ EQBand value
     *
     * @return
     */
    public int getEqBand1500() {
        try {
            return mService.getEqBand1500();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Set Equilizer 5kHZ EQBand value
     *
     * @param eqValue
     * @return
     */
    public boolean setEqBand5k(int eqValue) {
        try {
            return mService.setEqBand5k(eqValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get Equilizer 5kHZ EQBand value
     *
     * @return
     */
    public int getEqBand5k() {
        try {
            return mService.getEqBand5k();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Set Equilizer 10kHZ EQBand value
     *
     * @param eqValue
     * @return
     */
    public boolean setEqBand10k(int eqValue) {
        try {
            return mService.setEqBand10k(eqValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get Equilizer 10kHZ EQBand value
     *
     * @return 10kHZ EQBand value 0~100
     */
    public int getEqBand10k() {
        try {
            return mService.getEqBand10k();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean setSourceVolumeMute(boolean enable){
        try {
            return mService.setSourceVolumeMute(enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     *
     * @return
     */
    public boolean getSourceVolumeMute(){
        try {
            return mService.getSourceVolumeMute();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

}
