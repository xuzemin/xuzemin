//
// Created by 徐泽民 on 16/9/8.
//
#include "com_android_jdrd_opencv_OpenCVHelper.h"
#include <opencv2/opencv.hpp>
#include <linux/uinput.h>
#include <linux/input.h>
#include <stdint.h>
#include <unistd.h>
#include <malloc.h>
#include <linux/fb.h>
#include <syslog.h>
#include <jpeg/jpeglib.h>
#include "ImageProc.h"
using namespace cv;

#ifdef ANDROID_ENV
#define LOG LOGV
#else
#define LOG printf
#endif
#define CAMERA_DEVICE "/dev/video0"
#define CAPTURE_FILE "data/data/com.android.jdrd.opencv/frame.yuv"
#define  LOG_TAG    "WebCam"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define CLEAR(x) memset (&(x), 0, sizeof (x))


struct fimc_buffer {
    int length;
    void *start;
} framebuf[BUFFER_COUNT],buffer[BUFFER_COUNT];
struct buffer {
    void *                  start;
    size_t                  length;
};
struct v4l2_requestbuffers reqbuf;
struct v4l2_buffer buf;
char            dev_name[16];
int              fd              = -1;
unsigned int     n_buffers       = 0;
int i, ret;
int uinp_fd = -1;
int camerabase = -1;
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

JNIEXPORT jint  JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_getdata
        (JNIEnv *, jclass, jlong);

JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_init
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_stop
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_keyDownPress
  (JNIEnv *, jclass);

/*
 * Class:     com_android_jdrd_opencv_OpenCVHelper
 * Method:    keydown_institute
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_keyDownInstitute
  (JNIEnv *, jclass);

/*
 * Class:     com_android_jdrd_opencv_OpenCVHelper
 * Method:    keyup_press
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_keyUpPress
  (JNIEnv *, jclass);

/*
 * Class:     com_android_jdrd_opencv_OpenCVHelper
 * Method:    keyup_institute
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_keyUpInstitute
  (JNIEnv *, jclass);

/*
 * Class:     com_android_jdrd_opencv_OpenCVHelper
 * Method:    keyleft_press
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_keyLeftPress
  (JNIEnv *, jclass);

/*
 * Class:     com_android_jdrd_opencv_OpenCVHelper
 * Method:    keyleft
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_keyLeftInstitute
  (JNIEnv *, jclass);

/*
 * Class:     com_android_jdrd_opencv_OpenCVHelper
 * Method:    keyright_press
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_keyRightPress
  (JNIEnv *, jclass);

/*
 * Class:     com_android_jdrd_opencv_OpenCVHelper
 * Method:    keyright_institute
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_keyRightInstitute
  (JNIEnv *, jclass);


JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_init
        (JNIEnv *env, jclass obj){
    // Open Device
    fd = open(CAMERA_DEVICE,  O_RDWR | O_NONBLOCK, 0);
    if (fd < 0) {
        LOG("Open %s failed\n", CAMERA_DEVICE);
        return -1;
    }
    // Query Capability
    struct v4l2_capability cap;
    ret = ioctl(fd, VIDIOC_QUERYCAP, &cap);
    if (ret < 0) {
        LOG("VIDIOC_QUERYCAP failed (%d)\n", ret);
        return ret;
    }
    struct v4l2_cropcap cropcap;
    struct v4l2_crop crop;
    CLEAR (cropcap);
    cropcap.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    ret = ioctl(fd, VIDIOC_QUERYCAP, &cropcap);
    if (ret == 0) {
            crop.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    		crop.c = cropcap.defrect;
            LOGI("VIDIOC_QUERYCAP failed (%d)\n", ret);
            ret = ioctl(fd, VIDIOC_QUERYCAP, &cap);
            if(-1 == ret)
            return ret;
    }

    // Print capability infomations
    LOG("Capability Informations:\n");
    LOG(" driver: %s\n", cap.driver);
    LOG(" card: %s\n", cap.card);
    LOG(" bus_info: %s\n", cap.bus_info);
    LOG(" version: %08X\n", cap.version);
    LOG(" capabilities: %08X\n", cap.capabilities);

    ///////////////////////
    struct v4l2_fmtdesc fmtdesc; fmtdesc.index=0; fmtdesc.type=V4L2_BUF_TYPE_VIDEO_CAPTURE;

    while(ioctl(fd, VIDIOC_ENUM_FMT, &fmtdesc) != -1)
    {
        printf("\t%d.%s\n",fmtdesc.index+1,fmtdesc.description);
        fmtdesc.index++;
    }
    /////////////////////
    // Set Stream Format
    struct v4l2_format fmt;
    CLEAR (fmt);
    memset(&fmt, 0, sizeof(fmt));
    fmt.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    fmt.fmt.pix.width = IMG_WIDTH;
    fmt.fmt.pix.height = IMG_HEIGHT;
    fmt.fmt.pix.pixelformat = VIDEO_FORMAT;
    fmt.fmt.pix.field = V4L2_FIELD_INTERLACED;
    ret = ioctl(fd, VIDIOC_S_FMT, &fmt);
    if (ret < 0) {
        LOGI("VIDIOC_S_FMT failed (%d)\n", ret);
        return ret;
    }
    unsigned int min;
    min = fmt.fmt.pix.width * 2;
    	if (fmt.fmt.pix.bytesperline < min)
    		fmt.fmt.pix.bytesperline = min;
    	min = fmt.fmt.pix.bytesperline * fmt.fmt.pix.height;
    	if (fmt.fmt.pix.sizeimage < min)
    		fmt.fmt.pix.sizeimage = min;

    // Get Stream Format
    ret = ioctl(fd, VIDIOC_G_FMT, &fmt);
    if (ret < 0) {
        LOGI("VIDIOC_G_FMT failed (%d)\n", ret);
        return ret;
    }
    // Print Stream Format
    LOGI("Stream Format Informations:\n");
    LOGI(" type: %d\n", fmt.type);
    LOGI(" width: %d\n", fmt.fmt.pix.width);
    LOGI(" height: %d\n", fmt.fmt.pix.height);
    LOGI(" pixelformat: %d %d\n", fmt.fmt.pix.pixelformat, VIDEO_FORMAT);
    char fmtstr[8];
    memset(fmtstr, 0, 8);
    memcpy(fmtstr, &fmt.fmt.pix.pixelformat, 4);
    LOGI(" pixelformat: %s\n", fmtstr);
    LOGI(" field: %d\n", fmt.fmt.pix.field);
    LOGI(" bytesperline: %d\n", fmt.fmt.pix.bytesperline);
    LOGI(" sizeimage: %d\n", fmt.fmt.pix.sizeimage);
    LOGI(" colorspace: %d\n", fmt.fmt.pix.colorspace);
    LOGI(" priv: %d\n", fmt.fmt.pix.priv);
    LOGI(" raw_date: %s\n", fmt.fmt.raw_data);
    // Request buffers

    CLEAR (reqbuf);
    reqbuf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    reqbuf.memory = V4L2_MEMORY_MMAP;
    reqbuf.count = BUFFER_COUNT;
    ret = ioctl(fd , VIDIOC_REQBUFS, &reqbuf);
    if(ret < 0) {
        LOGI("VIDIOC_REQBUFS failed (%d)\n", ret);
        return ret;
    }
    // Queen buffers
    for(i=0; i<BUFFER_COUNT; i++) {
        CLEAR (buf);
        // Query buffer
        buf.index = i;
        buf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
        buf.memory = V4L2_MEMORY_MMAP;
        ret = ioctl(fd , VIDIOC_QUERYBUF, &buf);
        if(ret < 0) {
            LOGI("VIDIOC_QUERYBUF (%d) failed (%d)\n", i, ret);
            return ret;
        }
        // mmap buffer
        framebuf[i].length = buf.length;
        framebuf[i].start = (char *) mmap(0, buf.length, PROT_READ|PROT_WRITE, MAP_SHARED, fd, buf.m.offset);
        if (framebuf[i].start == MAP_FAILED) {
            LOGI("mmap (%d) failed: %s\n", i, strerror(errno));
            return -1;
        }
        // Queen buffer
        ret = ioctl(fd , VIDIOC_QBUF, &buf);
        if (ret < 0) {
            LOGI("VIDIOC_QBUF (%d) failed (%d)\n", i, ret);
            return -1;
        }
        LOGI("Frame buffer %d: address=0x%x, length=%d\n", i, (unsigned int)framebuf[i].start, framebuf[i].length);
    }
    // Stream On
    enum v4l2_buf_type type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    ret = ioctl(fd, VIDIOC_STREAMON, &type);
    if (ret < 0) {
        LOGI("VIDIOC_STREAMON failed (%d)\n", ret);
        return ret;
    }
    return 0;
}

JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_stop
        (JNIEnv *env, jclass obj){
    // Release the resource
    for (i=0; i<BUFFER_COUNT; i++) {
        munmap(framebuf[i].start, framebuf[i].length);
    }
    close(fd);
    return 0;
}

JNIEXPORT jint  JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_getdata(
        JNIEnv *env, jclass obj,jlong rgba){
        cv::Mat* mat_rgba = (cv::Mat*) rgba;

         LOGI("getdata start %s\n", CAPTURE_FILE);
        // Queen buffers
    // Get frame
    ret = ioctl(fd, VIDIOC_DQBUF, &buf);
    if (ret < 0) {
        LOGI("	 (%d)\n", ret);
        return -12;
    }

    int w = IMG_WIDTH*2;
    int h = IMG_HEIGHT;
    int bufLen = w*h;
//    unsigned char* pYuvBuf = new unsigned char[bufLen];

        LOGI("Mat %s\n", CAPTURE_FILE);
        //cv::Mat yuvImg;
        //yuvImg.create(h*3/2, w, CV_8UC1);
        mat_rgba->create(h, w, CV_8UC1);
        LOGI("framebuf  buf.index %d\n", buf.index);
        memcpy(mat_rgba->data,framebuf[buf.index].start,mat_rgba->step * mat_rgba->rows);
        //yuyv_2_rgb888((char *)framebuf[0].start,(char *)mat_rgba->data);
        LOGI("Capture one frame saved in test.jpg\n");

    // Re-queen buffer
    ret = ioctl(fd, VIDIOC_QBUF, &buf);
    if (ret < 0) {
        LOGI("VIDIOC_QBUF failed (%d)\n", ret);
        return -13;
    }

    LOGI("Camera test Done.\n");
    return 0;

}
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

JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_keyDownPress
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
        event.type = 0x01;
        event.code = 0x04;
        event.value = 0x70051;
        write(fd, &event, sizeof(event));

        event.type = 0x01;
        event.code = 0x6c;
        event.value = 0x01;
        write(fd, &event, sizeof(event));

        event.type = 0x0;
        event.code = 0x0;
        event.value = 0x0;
        write(fd, &event, sizeof(event));
        close(fd);
        return fd;
}
JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_keyDownInstitute
  (JNIEnv *env, jclass obj)
{
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
                // Report BUTTON CLICK - PRESS event
                memset(&event, 0, sizeof(event));
                event.type = 0x04;
                event.code = 0x04;
                event.value = 0x70051;
                write(fd, &event, sizeof(event));

                event.type = 0x01;
                event.code = 0x6c;
                event.value = 0x0;
                write(fd, &event, sizeof(event));
                // Report BUTTON CLICK - RELEASE event
                memset(&event, 0, sizeof(event));
                event.type = 0x0;
                event.code = 0x0;
                event.value = 0x0;
                close(fd);
                return fd;
}
JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_keyUpPress
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
        event.type = 0x01;
        event.code = 0x04;
        event.value = 0x70052;
        write(fd, &event, sizeof(event));

        event.type = 0x01;
        event.code = 0x67;
        event.value = 0x01;
        write(fd, &event, sizeof(event));

        event.type = 0x0;
        event.code = 0x0;
        event.value = 0x0;
        write(fd, &event, sizeof(event));
        close(fd);
        return fd;
}
JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_keyUpInstitute
  (JNIEnv *env, jclass obj)
{
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
                // Report BUTTON CLICK - PRESS event
                memset(&event, 0, sizeof(event));
                event.type = 0x04;
                event.code = 0x04;
                event.value = 0x70052;
                write(fd, &event, sizeof(event));

                event.type = 0x01;
                event.code = 0x67;
                event.value = 0x0;
                write(fd, &event, sizeof(event));
                // Report BUTTON CLICK - RELEASE event
                memset(&event, 0, sizeof(event));
                event.type = 0x0;
                event.code = 0x0;
                event.value = 0x0;
                close(fd);
                return fd;
}
JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_keyRightPress
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
        event.type = 0x01;
        event.code = 0x04;
        event.value = 0x7004f;
        write(fd, &event, sizeof(event));

        event.type = 0x01;
        event.code = 0x6a;
        event.value = 0x01;
        write(fd, &event, sizeof(event));

        event.type = 0x0;
        event.code = 0x0;
        event.value = 0x0;
        write(fd, &event, sizeof(event));
        close(fd);
        return fd;
}
JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_keyRightInstitute
  (JNIEnv *env, jclass obj)
{
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
                // Report BUTTON CLICK - PRESS event
                memset(&event, 0, sizeof(event));
                event.type = 0x04;
                event.code = 0x04;
                event.value = 0x7004f;
                write(fd, &event, sizeof(event));

                event.type = 0x01;
                event.code = 0x6a;
                event.value = 0x0;
                write(fd, &event, sizeof(event));
                // Report BUTTON CLICK - RELEASE event
                memset(&event, 0, sizeof(event));
                event.type = 0x0;
                event.code = 0x0;
                event.value = 0x0;
                close(fd);
                return fd;
}
JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_keyLeftPress
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
        event.type = 0x01;
        event.code = 0x04;
        event.value = 0x70050;
        write(fd, &event, sizeof(event));

        event.type = 0x01;
        event.code = 0x52;
        event.value = 0x01;
        write(fd, &event, sizeof(event));

        event.type = 0x0;
        event.code = 0x0;
        event.value = 0x0;
        write(fd, &event, sizeof(event));
        close(fd);
        return fd;
}
JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_keyLeftInstitute
  (JNIEnv *env, jclass obj)
{
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
                // Report BUTTON CLICK - PRESS event
                memset(&event, 0, sizeof(event));
                event.type = 0x04;
                event.code = 0x04;
                event.value = 0x70050;
                write(fd, &event, sizeof(event));

                event.type = 0x01;
                event.code = 0x52;
                event.value = 0x0;
                write(fd, &event, sizeof(event));
                // Report BUTTON CLICK - RELEASE event
                memset(&event, 0, sizeof(event));
                event.type = 0x0;
                event.code = 0x0;
                event.value = 0x0;
                close(fd);
                return fd;
}
