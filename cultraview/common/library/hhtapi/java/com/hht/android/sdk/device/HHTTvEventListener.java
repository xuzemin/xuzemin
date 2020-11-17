package com.hht.android.sdk.device;

/**
 * 系统监听事件，包括信号源相关和传感器设置相关
 * @author wang
 * @date 0219
 */
public interface HHTTvEventListener {
    /**
     *  what                        arg1                            arg2            obj
     * ​	信号源切换      			target inputsource
     * ​	信号源插拔事件    			inputsource						1插入/0拔出
     * ​	信号源lock      			inputsource
     * ​	信号源unlock    			inputsource
     * ​	信号源unstable  	    	inputsource
     * ​
     * 	屏幕温度        			temperature（温度值，单位为摄氏度）
     * ​	屏幕亮度        			light（背光值0-100）
     * @param what
     * @param arg1
     * @param arg2
     * @param obj
     */
    //void onTvEvent(int what, int arg1, int arg2, Object obj);
    void onTvEvent(int what, int arg1, int arg2 ,Object obj);
}
