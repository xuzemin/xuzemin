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

static int uinp_fd = -1;
struct uinput_user_dev uinp; // uInput device structure
struct input_event event;

JNIEXPORT jintArray JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_gray(
        JNIEnv *env, jclass obj, jintArray buf, int w, int h);

JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_test
  (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_get
  (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_send
  (JNIEnv *, jclass);



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
        JNIEnv *env, jclass obj) {

            int fd;
            int version;
            fd = open("/dev/input/event3", O_RDWR);
            if(fd < 0) {
                close(fd);//fprintf(stderr, "could not open %s, %s\n", argv[optind], strerror(errno));
                return 2;
            }
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
        	    if(ret < (ssize_t) sizeof(event)) {
                     close(fd);
                     //fprintf(stderr, "write event failed, %s\n", strerror(errno));
                     return -2;
                }
                event.type = 0x0;
                event.code = 0x0;
                event.value = 0x0;
                ret = write(fd, &event, sizeof(event));
        	    if(ret < (ssize_t) sizeof(event)) {
                     close(fd);
                     //fprintf(stderr, "write event failed, %s\n", strerror(errno));
                     return -3;
                }        	    event.type = 0x04;
        	    event.code = 0x04;
        	    event.value = 0x90002;
        	    ret = write(fd, &event, sizeof(event));
        	    if(ret < (ssize_t) sizeof(event)) {
                     close(fd);
                     //fprintf(stderr, "write event failed, %s\n", strerror(errno));
                     return -4;
                }        	    event.type = 0x01;
                event.code = 0x0111;
                event.value = 0x0;
                ret = write(fd, &event, sizeof(event));
        	    if(ret < (ssize_t) sizeof(event)) {
                     close(fd);
                     //fprintf(stderr, "write event failed, %s\n", strerror(errno));
                     return -5;
                }        	    event.type = 0x0;
                event.code = 0x0;
                event.value = 0x0;
                ret = write(fd, &event, sizeof(event));
        	    if(ret < (ssize_t) sizeof(event)) {
                     close(fd);
                     //fprintf(stderr, "write event failed, %s\n", strerror(errno));
                     return -6;
                }
                close(fd);
            return 0;
}
}

JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_get(
        JNIEnv *env, jclass obj) {



           // Open the input device
           uinp_fd = open("/dev/uinput", O_WRONLY | O_NDELAY);
           if (uinp_fd < 0)
           {
              close(uinp_fd);
              printf("Unable to open /dev/uinput/n");
              return -2;
           }

           memset(&uinp,0,sizeof(uinp)); // Intialize the uInput device to NULL
           strncpy(uinp.name, "PolyVision Touch Screen", UINPUT_MAX_NAME_SIZE);
           uinp.id.version = 5;
           uinp.id.bustype = BUS_USB;

           // Setup the uinput device
           ioctl(uinp_fd, UI_SET_EVBIT, EV_KEY);
           ioctl(uinp_fd, UI_SET_EVBIT, EV_REL);
           ioctl(uinp_fd, UI_SET_RELBIT, REL_X);
           ioctl(uinp_fd, UI_SET_RELBIT, REL_Y);
           int i =0;
           for (i=0; i < 256; i++) {
              ioctl(uinp_fd, UI_SET_KEYBIT, i);
           }

           ioctl(uinp_fd, UI_SET_KEYBIT, BTN_MOUSE);
           ioctl(uinp_fd, UI_SET_KEYBIT, BTN_TOUCH);
           ioctl(uinp_fd, UI_SET_KEYBIT, BTN_MOUSE);
           ioctl(uinp_fd, UI_SET_KEYBIT, BTN_LEFT);
           ioctl(uinp_fd, UI_SET_KEYBIT, BTN_MIDDLE);
           ioctl(uinp_fd, UI_SET_KEYBIT, BTN_RIGHT);
           ioctl(uinp_fd, UI_SET_KEYBIT, BTN_FORWARD);
           ioctl(uinp_fd, UI_SET_KEYBIT, BTN_BACK);

           /* Create input device into input sub-system */
           write(uinp_fd, &uinp, sizeof(uinp));
           if (ioctl(uinp_fd, UI_DEV_CREATE))
           {
              printf("Unable to create UINPUT device.");
              return -1;
           }

          // send_click_events(); // Send mouse event
             memset(&event, 0, sizeof(event));
               event.type = EV_REL;
               event.code = REL_X;
               event.value = 100;
               write(uinp_fd, &event, sizeof(event));

               event.type = EV_REL;
               event.code = REL_Y;
               event.value = 100;
               write(uinp_fd, &event, sizeof(event));

               event.type = EV_SYN;
               event.code = SYN_REPORT;
               event.value = 0;
               write(uinp_fd, &event, sizeof(event));

               // Report BUTTON CLICK - PRESS event
               memset(&event, 0, sizeof(event));
               event.type = EV_KEY;
               event.code = BTN_LEFT;
               event.value = 1;
               write(uinp_fd, &event, sizeof(event));

               event.type = EV_SYN;
               event.code = SYN_REPORT;
               event.value = 0;
               write(uinp_fd, &event, sizeof(event));

               // Report BUTTON CLICK - RELEASE event
               memset(&event, 0, sizeof(event));
               event.type = EV_KEY;
               event.code = BTN_LEFT;
               event.value = 0;

               write(uinp_fd, &event, sizeof(event));
               event.type = EV_SYN;
               event.code = SYN_REPORT;
               event.value = 0;
               write(uinp_fd, &event, sizeof(event));
           /* Destroy the input device */
           //ioctl(uinp_fd, UI_DEV_DESTROY);

           /* Close the UINPUT device */
           //close(uinp_fd);
           return 0;
}

JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_send
  (JNIEnv *env, jclass obj){
        int fd;
                    int version;
                    fd = open("/dev/input/event4", O_RDWR);
                    if(fd < 0) {
                        close(fd);//fprintf(stderr, "could not open %s, %s\n", argv[optind], strerror(errno));
                        return 2;
                    }
                    if (ioctl(fd, EVIOCGVERSION, &version)) {
                        close(fd);//fprintf(stderr, "could not get driver version for %s, %s\n", argv[optind], strerror(errno));
                        return 3;
                    }
                	   memset(&event, 0, sizeof(event));
                                      event.type = EV_REL;
                                      event.code = REL_X;
                                      event.value = 100;
                                      write(fd, &event, sizeof(event));

                                      event.type = EV_REL;
                                      event.code = REL_Y;
                                      event.value = 100;
                                      write(fd, &event, sizeof(event));

                                      event.type = EV_SYN;
                                      event.code = SYN_REPORT;
                                      event.value = 0;
                                      write(fd, &event, sizeof(event));

                                      // Report BUTTON CLICK - PRESS event
                                      memset(&event, 0, sizeof(event));
                                      event.type = EV_KEY;
                                      event.code = BTN_LEFT;
                                      event.value = 1;
                                      write(fd, &event, sizeof(event));

                                      event.type = EV_SYN;
                                      event.code = SYN_REPORT;
                                      event.value = 0;
                                      write(fd, &event, sizeof(event));

                                      // Report BUTTON CLICK - RELEASE event
                                      memset(&event, 0, sizeof(event));
                                      event.type = EV_KEY;
                                      event.code = BTN_LEFT;
                                      event.value = 0;

                                      write(fd, &event, sizeof(event));
                                      event.type = EV_SYN;
                                      event.code = SYN_REPORT;
                                      event.value = 0;
                                      write(fd, &event, sizeof(event));
                    return 0;
  }

