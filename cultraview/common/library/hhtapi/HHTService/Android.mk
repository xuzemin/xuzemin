
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := HHTService
LOCAL_MODULE_TAGS := optional

LOCAL_CERTIFICATE := platform
LOCAL_PROGUARD_FLAG_FILES := proguard.cfg

LOCAL_SRC_FILES := \
    $(call all-java-files-under, src)

LOCAL_JAVA_LIBRARIES := \
    com.hht.android.sdk \
	com.mstar.android \
	com.cultraview.tv

#LOCAL_STATIC_JAVA_LIBRARIES = \
#    SettingsLib

LOCAL_PROPRIETARY_MODULE := true

include frameworks/base/packages/SettingsLib/common.mk

include $(BUILD_PACKAGE)
