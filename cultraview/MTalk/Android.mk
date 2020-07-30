LOCAL_PATH := $(my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := MTalk
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_PATH := $(TARGET_OUT)/app
LOCAL_SRC_FILES := $(LOCAL_MODULE)$(COMMON_ANDROID_PACKAGE_SUFFIX)
LOCAL_CERTIFICATE := PRESIGNED
LOCAL_DEX_PREOPT := false
LOCAL_MULTILIB :=32
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)


LOCAL_PREBUILT_JNI_LIBS:= \
  lib/armeabi/libBaiduSpeechSDK.so \
  lib/armeabi/libbd_easr_s1_merge_normal_20151216.dat.so \
  lib/armeabi/libbdEASRAndroid.so \
  lib/armeabi/libbdSpilWakeup.so \
  lib/armeabi/libbspatch.so \
  lib/armeabi/libcutils.so \
  lib/armeabi/libDoGpio.so \
  lib/armeabi/libGpio.so \
  lib/armeabi/libion.so \
  lib/armeabi/liblog.so \
  lib/armeabi/libmaizheSDKlib.so \
  lib/armeabi/libmysher_h264.so \
  lib/armeabi/libmysherxmpp.so \
  lib/armeabi/libnativeegl.so \
  lib/armeabi/librk_on2.so \
  lib/armeabi/libScreenCapturerJni.so \
  lib/armeabi/libSndCtl.so \
  lib/armeabi/libturbojpeg.so \
  lib/armeabi/libturnuclient.so \
  lib/armeabi/libUsbDisplay-sdk.so \
  lib/armeabi/libvad.dnn.so \
  lib/armeabi/libvpu.so \
  lib/armeabi/libwebrtc_unity_plugin.so
include $(BUILD_PREBUILT)