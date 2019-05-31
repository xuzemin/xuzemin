#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_xuzemin_kotlin_utils_JniUtils_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT void JNICALL
Java_com_xuzemin_kotlin_utils_JniUtils_CheckValue(
        JNIEnv *env,
        jobject /* this */,
        jfloatArray jfloatarray) {

    jfloat* arr;
    jint length;
    arr = (env)->GetFloatArrayElements(jfloatarray,NULL);
    length = (env)->GetArrayLength(jfloatarray);
    std::sort(arr,arr+length);

    env->SetFloatArrayRegion(jfloatarray,0,length,arr);

    env->ReleaseFloatArrayElements(jfloatarray,arr,0);
}
