package com.hht.android.sdk.service;

import android.content.Context;
import android.os.RemoteException;
import android.os.SystemProperties;

import com.hht.android.sdk.boardInfo.HHTConstant;
import com.hht.android.sdk.picture.IHHTPicture;
import com.hht.android.sdk.service.utils.L;
import com.hht.android.sdk.service.utils.Tools;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPictureManager;

import static com.hht.android.sdk.picture.HHTPictureManager.COLOR_TEMP_COOL;
import static com.hht.android.sdk.picture.HHTPictureManager.COLOR_TEMP_USER2;
import static com.hht.android.sdk.picture.HHTPictureManager.VIDEO_ARC_16x9;
import static com.hht.android.sdk.picture.HHTPictureManager.VIDEO_ARC_DOTBYDOT;

public class HHTPictureService extends IHHTPicture.Stub {
    private Context mContext;

    public HHTPictureService(Context context) {
        this.mContext = context;
        L.i("gyx", "HHTPictureService init");
    }

    /**
     * vga Auto Adjust VGA 自动调整
     *
     * @return true->成功， false -> 失败
     */
    @Override
    public boolean execAutoPc() throws RemoteException {
        int inputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        boolean flag = (inputSource == TvCommonManager.INPUT_SOURCE_VGA);
        if (!flag) {
            return false;
        }
        return TvPictureManager.getInstance().execAutoPc();
    }

    /**
     * To freeze image i
     *
     * @return boolean true if setting successful, otherwise return false
     */
    @Override
    public boolean freezeImage() throws RemoteException {
        if (Tools.isTVSource()) {
            TvPictureManager.getInstance().freezeImage();
            return true;
        }

        return false;
    }

    /**
     * Get current input source's color temperature
     *
     * @return int current input source's color temperature
     */
    @Override
    public int getColorTempratureIdx() throws RemoteException {
        return TvPictureManager.getInstance().getColorTemprature();
    }

    /**
     * get Clock
     *
     * @return 图像时钟数值 0-100
     */
    @Override
    public int getPCClock() throws RemoteException {
        return TvPictureManager.getInstance().getPCClock();
    }

    /**
     * get H Position
     *
     * @return
     */
    @Override
    public int getPCHPos() throws RemoteException {
        return TvPictureManager.getInstance().getPCHPos();
    }

    /**
     * get Phase
     *
     * @return
     */
    @Override
    public int getPCPhase() throws RemoteException {
        return TvPictureManager.getInstance().getPCPhase();
    }

    /**
     * get V Position
     *
     * @return
     */
    @Override
    public int getPCVPos() throws RemoteException {
        return TvPictureManager.getInstance().getPCVPos();
    }

    /**
     * 获取信号源的图像模式
     *
     * @return
     */
    @Override
    public int getPictureMode() throws RemoteException {
        return TvPictureManager.getInstance().getPictureMode();
    }

    /**
     *
     * Get video arc type.
     * @return int current video arc type
     */
    @Override
    public int getVideoArcType() throws RemoteException {
        return TvPictureManager.getInstance().getVideoArcType();
    }

    /**
     * 获取对比度、饱和度、锐度等对应数值
     *
     * @param iPictureItem - 对比度、饱和度等
     * @return
     */
    @Override
    public int getVideoItem(int iPictureItem) throws RemoteException {
        if (iPictureItem > TvPictureManager.PICTURE_BACKLIGHT
                || iPictureItem < TvPictureManager.PICTURE_BRIGHTNESS){
            return -1;
        }
        return TvPictureManager.getInstance().getVideoItem(iPictureItem);
    }

    /**
     * To get freeze-image setting status
     *
     * @return boolean true: freezed false: not freezed
     */
    @Override
    public boolean isImageFreezed() throws RemoteException {
        if (Tools.isTVSource()) {
            TvPictureManager.getInstance().isImageFreezed();
            return true;
        }

        return false;
    }

    /**
     * Setting the Ex color temperature by current input source
     *
     * @param colorTempIdx - indicated the color temperature
     * @return boolean true: success, false: fail
     */
    @Override
    public boolean setColorTempratureIdx(int colorTempIdx) throws RemoteException {
        if (colorTempIdx >= COLOR_TEMP_COOL && colorTempIdx <= COLOR_TEMP_USER2) {
            return TvPictureManager.getInstance().setColorTempratureIdx(colorTempIdx);
        }

        return false;
    }

    /**
     * set Clock
     *
     * @param iClock - 0-100
     * @return 0->成功， -1 -> 失败
     */
    @Override
    public boolean setPCClock(int iClock) throws RemoteException {
        return TvPictureManager.getInstance().setPCClock(iClock);
    }

    /**
     * set H Position
     *
     * @param iPosition
     * @return
     */
    @Override
    public boolean setPCHPos(int iPosition) throws RemoteException {
        return TvPictureManager.getInstance().setPCHPos(iPosition);
    }

    /**
     * set Phase
     *
     * @param iPhase
     * @return 0->成功， -1 -> 失败
     */
    @Override
    public boolean setPCPhase(int iPhase) throws RemoteException {
        return TvPictureManager.getInstance().setPCPhase(iPhase);
    }

    /**
     * set V Position
     *
     * @param iPosition
     * @return
     */
    @Override
    public boolean setPCVPos(int iPosition) throws RemoteException {
        return TvPictureManager.getInstance().setPCVPos(iPosition);
    }

    /**
     * 设置信号源的图像模式
     *
     * @param pictureMode
     * @return 0->成功， -1 -> 失败
     */
    @Override
    public boolean setPictureMode(int pictureMode) throws RemoteException {
        return TvPictureManager.getInstance().setPictureMode(pictureMode);
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
    @Override
    public boolean setVideoArcType(int arcType) throws RemoteException {
        int pos = arcType;
        if (arcType < VIDEO_ARC_16x9 || arcType > VIDEO_ARC_DOTBYDOT){
            return false;
        }

        if (arcType != VIDEO_ARC_DOTBYDOT){
            pos = arcType - 1;
        }
        return Tools.setVideoArcType(pos);
    }

    /**
     * set Video Item
     * @param iPictureItem
     * @param iValue
     * @return
     */
    @Override
    public boolean setVideoItem(int iPictureItem, int iValue) throws RemoteException {
        if (iPictureItem > TvPictureManager.PICTURE_BACKLIGHT
                || iPictureItem < TvPictureManager.PICTURE_BRIGHTNESS){
            return false;
        }

        return TvPictureManager.getInstance().setVideoItem(iPictureItem, iValue);
    }

    /**
     * To un-freeze image i
     *
     * @return boolean true if setting successful, otherwise return false
     */
    @Override
    public boolean unFreezeImage() throws RemoteException {
        if (Tools.isTVSource()) {
            TvPictureManager.getInstance().unFreezeImage();
            return true;
        }

        return false;
    }

    @Override
    public boolean setBackLight(int value) throws RemoteException {
        if (value >= 0 && value <= 100){
            SystemProperties.set(HHTConstant.SYS_BACKLIGHT, "" + value);
            TvPictureManager.getInstance().setBacklight(value);
            return true;
        }
        return false;
    }

    @Override
    public int getBackLight() throws RemoteException {
        return SystemProperties.getInt(HHTConstant.SYS_BACKLIGHT,50);
//        return TvPictureManager.getInstance().getBacklight();
    }
}
