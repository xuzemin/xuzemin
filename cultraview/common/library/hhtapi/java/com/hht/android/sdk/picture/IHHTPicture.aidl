// IHHTPicture.aidl
package com.hht.android.sdk.picture;

// Declare any non-default types here with import statements

interface IHHTPicture {
    boolean	execAutoPc();
    boolean	freezeImage();
    int	getColorTempratureIdx();
    int	getPCClock();
    int	getPCHPos();
    int	getPCPhase();
    int	getPCVPos();
    int	getPictureMode();
    int	getVideoArcType();
    int	getVideoItem(int pictureItem);
    boolean	isImageFreezed();
    boolean	setColorTempratureIdx(int colorTempIdx);
    boolean	setPCClock(int clock);
    boolean	setPCHPos(int hpos);
    boolean	setPCPhase(int phase);
    boolean	setPCVPos(int vpos);
    boolean	setPictureMode(int pictureMode);
    boolean	setVideoArcType(int arcType);
    boolean	setVideoItem(int pictureItem, int value);
    boolean	unFreezeImage();
    boolean setBackLight(int value);
    int getBackLight();
}
