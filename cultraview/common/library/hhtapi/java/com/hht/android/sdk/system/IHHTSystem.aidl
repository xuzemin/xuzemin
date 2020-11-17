// IHHTSystem.aidl
package com.hht.android.sdk.system;
import android.graphics.Bitmap;

// Declare any non-default types here with import statements

interface IHHTSystem {
    boolean	installApkPackage(String strApkFilePath);
    boolean	updatePatchVersion(String strConfig);
    boolean	updateSystemMain();
//    boolean	startScreenShot();
    Bitmap screenshot(int width, int height);
    boolean	updateSystem(String strFilepath);
}
