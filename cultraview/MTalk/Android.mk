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
  lib/armeabi/libbspatch.so \
  lib/armeabi/libclnt_uvc.so \
  lib/armeabi/libDoGpio.so \
  lib/armeabi/libGpio.so \
  lib/armeabi/libjpeg-turbo1500.so \
  lib/armeabi/libmaizheSDKlib.so \
  lib/armeabi/libmysher_h264.so \
  lib/armeabi/libMysherFirmware.so \
  lib/armeabi/libmysherxmpp.so \
  lib/armeabi/libmz_camera.so \
  lib/armeabi/libMzAudioJni.so \
  lib/armeabi/libnativeegl.so \
  lib/armeabi/libScreenCapturerJni.so \
  lib/armeabi/libSndCtl.so \
  lib/armeabi/libturbojpeg.so \
  lib/armeabi/libturnuclient.so \
  lib/armeabi/libusb100.so \
  lib/armeabi/libUsbDisplay-sdk.so \
  lib/armeabi/libuvc.so \
  lib/armeabi/libUVCCamera.so \
  lib/armeabi/libwebrtc_unity_plugin.so \
  lib/armeabi/libwz265.so \
  lib/armeabi/libwzdecoder.so
include $(BUILD_PREBUILT)