#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_jni_android_myjniproject_JniUtils_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "深圳市智宝机器人科技有限公司";
    return env->NewStringUTF(hello.c_str());
}
