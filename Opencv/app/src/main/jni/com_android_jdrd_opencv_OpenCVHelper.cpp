//
// Created by 徐泽民 on 16/9/8.
//
#include "com_android_jdrd_opencv_OpenCVHelper.h"
#include <stdio.h>
#include <stdlib.h>
#include <opencv2/opencv.hpp>
#include <linux/uinput.h>
#include <fcntl.h>
#include <string.h>
#include <linux/input.h>
#include <stdint.h>
#include <string.h>
#include <sys/ioctl.h>
#include <unistd.h>

using namespace cv;

extern "C" {

JNIEXPORT jintArray JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_gray(
        JNIEnv *env, jclass obj, jintArray buf, int w, int h);

JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_test
  (JNIEnv *, jclass, jint, jobjectArray);

JNIEXPORT jintArray JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_gray(
        JNIEnv *env, jclass obj, jintArray buf, int w, int h) {

    jint *cbuf;
    cbuf = env->GetIntArrayElements(buf, JNI_FALSE );
    if (cbuf == NULL) {
        return 0;
    }

    Mat imgData(h, w, CV_8UC4, (unsigned char *) cbuf);

    uchar* ptr = imgData.ptr(0);
    for(int i = 0; i < w*h; i ++){
        //计算公式：Y(亮度) = 0.299*R + 0.587*G + 0.114*B
        //对于一个int四字节，其彩色值存储方式为：BGRA
        int grayScale = (int)(ptr[4*i+2]*0.299 + ptr[4*i+1]*0.587 + ptr[4*i+0]*0.114);
        ptr[4*i+1] = grayScale;
        ptr[4*i+2] = grayScale;
        ptr[4*i+0] = grayScale;
    }

    int size = w * h;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, cbuf);
    env->ReleaseIntArrayElements(buf, cbuf, 0);
    return result;
}
JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_test(
        JNIEnv *env, jclass obj,jint argc,jobjectArray strArray) {

        struct uinput_user_dev device;
        memset(&device, 0, sizeof device);
        int uinputfd = open("/dev/uinput",O_WRONLY);
        strcpy(device.name, "Hillcrest Labs Virtual Pointer");
        device.id.bustype = BUS_USB;
        device.id.vendor = 0x1d5a; // Hillcrest Labs VID
        device.id.product = 0xc2b3; // FSM, but probably doesn't matter
        device.id.version = 1;
        int i = 0;
        for (i = 0; i < ABS_MAX; i++) {
            device.absmax[i] = -1;
            device.absmin[i] = -1;
            device.absfuzz[i] = -1;
            device.absflat[i] = -1;
        }
        if (write(uinputfd,&device,sizeof(device)) != sizeof(device)) {
            fprintf(stderr, "Error initializing the input device.\n");
            return 1;
        }
        if (ioctl(uinputfd, UI_SET_EVBIT, EV_REL) < 0 ||
            ioctl(uinputfd, UI_SET_RELBIT, REL_X) < 0 ||
            ioctl(uinputfd, UI_SET_RELBIT, REL_Y) < 0 ||
            ioctl(uinputfd, UI_SET_RELBIT, REL_WHEEL) < 0 ||
            ioctl(uinputfd, UI_SET_EVBIT, EV_KEY) < 0 ||
            ioctl(uinputfd, UI_SET_KEYBIT, BTN_LEFT) < 0 ||
            ioctl(uinputfd, UI_SET_KEYBIT, BTN_RIGHT) < 0 ||
            ioctl(uinputfd, UI_SET_KEYBIT, BTN_MIDDLE) < 0 ||
            ioctl(uinputfd,UI_DEV_CREATE) < 0) {
            fprintf(stderr, "Error configuring the input device.\n");
            return 1;
        }

        jstring jstr;
        	jsize len = argc;
        	char **argv = (char **) malloc(len*sizeof(char *));
        	    jsize i=0;
        	    int fd;
        	    fd = open("dev/input/event0", O_RDWR);
        	    if(fd < 0) {
        	    	close(fd);//fprintf(stderr, "could not open %s, %s\n", argv[optind], strerror(errno));
        	        return 2;
        	    }
        	    int version;
        	    if (ioctl(fd, EVIOCGVERSION, &version)) {
        	    	close(fd);//fprintf(stderr, "could not get driver version for %s, %s\n", argv[optind], strerror(errno));
        	        return 3;
        	    }
        	    struct input_event event;
        	    ssize_t ret;
        	    memset(&event, 0, sizeof(event));
        	    event.type = 0x04;
        	    event.code = 0x04;
        	    event.value = 0x90002;
        	    ret = write(fd, &event, sizeof(event));
        	    if(ret < (ssize_t) sizeof(event)) {
                     close(fd);
                     //fprintf(stderr, "write event failed, %s\n", strerror(errno));
                     return -1;
                }
        	    event.type = 0x01;
                event.code = 0x0111;
                event.value = 0x01;
                ret = write(fd, &event, sizeof(event));
                event.type = 0x0;
                event.code = 0x0;
                event.value = 0x0;
                ret = write(fd, &event, sizeof(event));
        	    event.type = 0x04;
        	    event.code = 0x04;
        	    event.value = 0x90002;
        	    ret = write(fd, &event, sizeof(event));
        	    event.type = 0x01;
                event.code = 0x0111;
                event.value = 0x0;
                ret = write(fd, &event, sizeof(event));
        	    event.type = 0x0;
                event.code = 0x0;
                event.value = 0x0;
                ret = write(fd, &event, sizeof(event));
        	    close(fd);
            return 0;
}
}
