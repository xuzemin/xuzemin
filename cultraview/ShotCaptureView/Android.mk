LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_PACKAGE_NAME := ShotCaptureView
LOCAL_CERTIFICATE := platform
#LOCAL_PROGUARD_FLAG_FILES := proguard.flags

LOCAL_SRC_FILES := \
    $(call all-java-files-under, src) \
    $(call all-Iaidl-files-under, src)
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res

LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v4 \

#LOCAL_JAVA_LIBRARIES := \
#    com.mstar.android \
#    com.cultraview.tv
include $(BUILD_PACKAGE)
