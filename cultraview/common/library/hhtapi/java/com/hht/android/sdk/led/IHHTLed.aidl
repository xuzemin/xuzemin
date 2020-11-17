// IHHTLed.aidl
package com.hht.android.sdk.led;

// Declare any non-default types here with import statements

interface IHHTLed {
   int	getLedMode();
   boolean	setLedMode(int iMode);
   boolean	setLedStatus(boolean bStatus);
}
