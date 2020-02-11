// IHHTTime.aidl
package com.hht.android.sdk.time;
import com.hht.android.sdk.time.util.TimeUtil;
// Declare any non-default types here with import statements

interface IHHTTime {
    long	getChipRuntime();
    long	getSystemRuntime();
    boolean	isScheduleTimeBootEnable();
    boolean	isScheduleTimeShutdownEnable();
    boolean	setChipRuntime(long lChipTime);
//    boolean	setRtcTime(long lMillitime,String strTimezone);
    boolean	setScheduleTimeBootEnable(boolean enable);
    boolean	setScheduleTimeForBoot(inout TimeUtil bootTime);
    TimeUtil getScheduleTimeForBoot();
    boolean	setScheduleTimeShutdownEnable(boolean enable);
    boolean	setSystemRuntime(long lChipTime);
    boolean setScheduleTimeForShutdown(inout TimeUtil time);
    TimeUtil getScheduleTimeForShutdown();
}
