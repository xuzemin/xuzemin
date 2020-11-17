package com.hht.android.sdk.picture;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

/**
 * 图片管理
 * @author wanghang
 */
public class HHTPictureManager {
    private static IHHTPicture mService=null;
    public static final int COLOR_TEMP_COOL = 0;
    public static final int COLOR_TEMP_NATURE = 1;
    public static final int COLOR_TEMP_WARM = 2;
    public static final int COLOR_TEMP_USER1 = 3;
    public static final int COLOR_TEMP_USER2 = 4;

    public static final int PICTURE_BRIGHTNESS = 0;
    public static final int PICTURE_CONTRAST = 1;
    public static final int PICTURE_SATURATION = 2;
    public static final int PICTURE_SHARPNESS = 3;
    public static final int PICTURE_HUE = 4;
    public static final int PICTURE_BACKLIGHT = 5;

    public static final int VIDEO_ARC_16x9 = 1;
    public static final int VIDEO_ARC_4x3 = 2;
    public static final int VIDEO_ARC_DOTBYDOT = 3;

    public static final String SERVICE = "sdk_PictureManager";

    public enum EnumPictureMode {
        PICTURE_DYNAMIC,
        PICTURE_NORMAL,
        PICTURE_SOFT,
        PICTURE_USER,
        PICTURE_GAME,
        PICTURE_AUTO,
        PICTURE_PC,
        PICTURE_VIVID,
        PICTURE_NATURAL,
        PICTURE_SPORTS,
        PICTURE_NUMS;
    }

    private static HHTPictureManager INSTANCE;

    private HHTPictureManager(){
        IBinder service = ServiceManager.getService(HHTPictureManager.SERVICE);
        mService = IHHTPicture.Stub.asInterface(service);
    }

    public static HHTPictureManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HHTPictureManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HHTPictureManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * To freeze image i
     *
     * @return boolean true if setting successful, otherwise return false
     */
    public boolean freezeImage(){
        try {
            return mService.freezeImage();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * To un-freeze image i
     *
     * @return boolean true if setting successful, otherwise return false
     */
    public boolean unFreezeImage(){
        try {
            return mService.unFreezeImage();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * To get freeze-image setting status
     *
     * @return boolean true: freezed false: not freezed
     */
    public boolean isImageFreezed(){
        try {
            return mService.isImageFreezed();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 设置信号源的图像模式
     *
     * @param iMode
     * @return 0->成功， -1 -> 失败
     */
    public boolean setPictureMode(int iMode) {
        try {
            return mService.setPictureMode(iMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取信号源的图像模式
     *
     * @return
     */
    public int getPictureMode() {
        try {
            return mService.getPictureMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * set Clock
     *
     * @param iClock - 0-100
     * @return 0->成功， -1 -> 失败
     */
    public boolean setPCClock(int iClock) {
        try {
            return mService.setPCClock(iClock);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * get Clock
     *
     * @return 图像时钟数值 0-100
     */
    public int getPCClock() {
        try {
            return mService.getPCClock();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Setting the Ex color temperature by current input source
     *
     * @param  colorTempIdx - indicated the color temperature
     * @return boolean true: success, false: fail
     */
    public boolean setColorTempratureIdx(int colorTempIdx){
        try {
            return mService.setColorTempratureIdx(colorTempIdx);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get current input source's color temperature
     *
     * @return int current input source's color temperature
     */
    public int getColorTempratureIdx(){
        try {
            return mService.getColorTempratureIdx();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * set H Position
     * @param iPosition
     * @return
     */
    public boolean setPCHPos(int iPosition) {
        try {
            return mService.setPCHPos(iPosition);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * get H Position
     * @return
     */
    public int getPCHPos() {
        try {
            return mService.getPCHPos();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * set Phase
     *
     * @param iPhase
     * @return 0->成功， -1 -> 失败
     */
    public boolean setPCPhase(int iPhase) {
        try {
            return mService.setPCPhase(iPhase);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * get Phase
     *
     * @return
     */
    public int getPCPhase() {
        try {
            return mService.getPCPhase();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * set V Position
     *
     * @param iPosition
     * @return
     */
    public boolean setPCVPos(int iPosition) {
        try {
            return mService.setPCVPos(iPosition);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * get V Position
     *
     * @return
     */
    public int getPCVPos() {
        try {
            return mService.getPCVPos();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * vga Auto Adjust VGA 自动调整
     *
     * @return true->成功， false -> 失败
     */
    public boolean execAutoPc() {
        try {
            return mService.execAutoPc();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Set video Arc
     *
     * @param arcType
     *
    VIDEO_ARC_16x9
    VIDEO_ARC_4x3
    VIDEO_ARC_DOTBYDOT
     * @return boolean TRUE:Success, or FALSE:failed.
     */
    public boolean setVideoArcType(int arcType){
        try {
            return mService.setVideoArcType(arcType);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * Get video arc type.
     * @return int current video arc type
     */
    public int getVideoArcType(){
        try {
            return mService.getVideoArcType();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }



    /**
     * set Video Item
     * @param iPictureItem
     * @param iValue
     * @return
     */
    public boolean setVideoItem(int iPictureItem, int iValue) {
        try {
            return mService.setVideoItem(iPictureItem,iValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取对比度、饱和度、锐度等对应数值
     *
     * @param iPictureItem - 对比度、饱和度等
     * @return
     */
    public int getVideoItem(int iPictureItem) {
        try {
            return mService.getVideoItem(iPictureItem);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 设置背光
     *
     * @param value 背光值 0~100
     * @return true:success,false:failed
     */
    public boolean setBackLight(int value){
        try {
            return mService.setBackLight(value);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取背光值
     *
     * @return 当前背光值
     */
    public int getBackLight(){
        try {
            return mService.getBackLight();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
