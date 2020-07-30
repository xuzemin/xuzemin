LOCAL_PATH := $(my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := flashplayer
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_PATH := $(TARGET_OUT_DATA_APPS)
LOCAL_SRC_FILES := $(LOCAL_MODULE)$(COMMON_ANDROID_PACKAGE_SUFFIX)
LOCAL_CERTIFICATE := PRESIGNED
LOCAL_DEX_PREOPT := false
LOCAL_MULTILIB :=32
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)


LOCAL_PREBUILT_JNI_LIBS:= \
  lib/armeabi/libysshared.so \
  lib/armeabi/libstlport_shared.so \
  lib/armeabi/libstagefright_honeycomb.so \
  lib/armeabi/libstagefright_froyo.so \
  lib/armeabi/libCore.so 
include $(BUILD_PREBUILT)