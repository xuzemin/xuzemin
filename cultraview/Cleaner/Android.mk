LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := Cleaner
#LOCAL_MODULE_TAGS := tests
LOCAL_MODULE_TAGS := optional

#LOCAL_CERTIFICATE := platform
#LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_SRC_FILES := \
    $(call all-java-files-under, src) \
    $(call all-renderscript-files-under, src)

LOCAL_STATIC_JAVA_LIBRARIES :=  android-support-v4
LOCAL_JAVACFLAGS += -Xlint:all
LOCAL_PROPRIETARY_MODULE := true
#LOCAL_PROGUARD_FLAG_FILES := proguard.cfg
LOCAL_DEX_PREOPT := false
LOCAL_CERTIFICATE := platform
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_PATH := $(TARGET_OUT)/app
LOCAL_JAVA_LIBRARIES := com.mstar.android 
include $(BUILD_PACKAGE)

include $(CLEAR_VARS)
