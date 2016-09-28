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
#define CAPTURE_FILE "data/data/com.android.jdrd.opencv/frame.yuv420"
#define VIDEO_WIDTH 720
#define VIDEO_HEIGHT 480
#define  LOG_TAG    "WebCam"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define CLEAR(x) memset (&(x), 0, sizeof (x))


struct fimc_buffer {
               int length;
               void *start;
               } framebuf[BUFFER_COUNT];

int displayyuv()
{
    int w = VIDEO_WIDTH;
    int h = VIDEO_HEIGHT;
    printf("yuv file w: %d, h: %d \n", w, h);
    FILE* pFileIn = fopen(CAPTURE_FILE, "rb+");
    if (pFileIn < 0) {
            		LOG("open frame data file failed\n");
            		return -1;
            	}
    int bufLen = w*h*3/2;
    unsigned char* pYuvBuf = new unsigned char[bufLen];
    int iCount = 0;
    for(int i=0; i<200; i++)
    {
        fread(pYuvBuf, bufLen*sizeof(unsigned char), 1, pFileIn);
        cv::Mat yuvImg;
        yuvImg.create(h*3/2, w, CV_8UC1);
        memcpy(yuvImg.data, pYuvBuf, bufLen*sizeof(unsigned char));
        cv::Mat rgbImg;
        cv::cvtColor(yuvImg, rgbImg, CV_YUV2BGR_I420);
        cv::imshow("img", yuvImg);
        cv::waitKey(1);
        printf("%d \n", iCount++);
    }
    delete[] pYuvBuf;
    fclose(pFileIn);
}
JNIEXPORT jintArray JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_gray(
        JNIEnv *env, jclass obj, jintArray buf, int w, int h);

JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_test
  (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_get
  (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_send
  (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_getdata
  (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_getyun
                       (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_getyun
 (JNIEnv *env, jclass obj){
 }

JNIEXPORT jint JNICALL Java_com_android_jdrd_opencv_OpenCVHelper_getdata(
        JNIEnv *env, jclass obj){
        int i, ret;
        	// Open Device
        	int fd;
        	fd = open(CAMERA_DEVICE, O_RDWR, 0);
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
        // Print capability infomations
        	LOG("Capability Informations:\n");
        	LOG(" driver: %s\n", cap.driver);
        	LOG(" card: %s\n", cap.card);
        	LOG(" bus_info: %s\n", cap.bus_info);
        	LOG(" version: %08X\n", cap.version);
        	LOG(" capabilities: %08X\n", cap.capabilities);

        	///////////////////////
        	struct v4l2_fmtdesc fmtdesc; fmtdesc.index=0; fmtdesc.type=V4L2_BUF_TYPE_VIDEO_CAPTURE; printf("Support format:\n");

        	while(ioctl(fd, VIDIOC_ENUM_FMT, &fmtdesc) != -1)
        	{
        		printf("\t%d.%s\n",fmtdesc.index+1,fmtdesc.description);
        		fmtdesc.index++;
        	}
        	/////////////////////
        // Set Stream Format
        	struct v4l2_format fmt;
        	memset(&fmt, 0, sizeof(fmt));
        	fmt.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
        	fmt.fmt.pix.width = VIDEO_WIDTH;
        	fmt.fmt.pix.height = VIDEO_HEIGHT;
        	fmt.fmt.pix.pixelformat = VIDEO_FORMAT;
        	fmt.fmt.pix.field = V4L2_FIELD_INTERLACED;
        	ret = ioctl(fd, VIDIOC_S_FMT, &fmt);
        	if (ret < 0) {
        		LOG("VIDIOC_S_FMT failed (%d)\n", ret);
        		return ret;
        	}
        // Get Stream Format
        	ret = ioctl(fd, VIDIOC_G_FMT, &fmt);
        	if (ret < 0) {
        		LOG("VIDIOC_G_FMT failed (%d)\n", ret);
        		return ret;
        	}
        // Print Stream Format
        	LOG("Stream Format Informations:\n");
        	LOG(" type: %d\n", fmt.type);
        	LOG(" width: %d\n", fmt.fmt.pix.width);
        	LOG(" height: %d\n", fmt.fmt.pix.height);
        	char fmtstr[8];
        	memset(fmtstr, 0, 8);
        	memcpy(fmtstr, &fmt.fmt.pix.pixelformat, 4);
        	LOG(" pixelformat: %s\n", fmtstr);
        	LOG(" field: %d\n", fmt.fmt.pix.field);
        	LOG(" bytesperline: %d\n", fmt.fmt.pix.bytesperline);
        	LOG(" sizeimage: %d\n", fmt.fmt.pix.sizeimage);
        	LOG(" colorspace: %d\n", fmt.fmt.pix.colorspace);
        	LOG(" priv: %d\n", fmt.fmt.pix.priv);
        	LOG(" raw_date: %s\n", fmt.fmt.raw_data);
        // Request buffers
        	struct v4l2_requestbuffers reqbuf;
        	reqbuf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
        	reqbuf.memory = V4L2_MEMORY_MMAP;
        	reqbuf.count = BUFFER_COUNT;
        	ret = ioctl(fd , VIDIOC_REQBUFS, &reqbuf);
        	if(ret < 0) {
        		LOG("VIDIOC_REQBUFS failed (%d)\n", ret);
        		return ret;
        	}
        // Queen buffers
        	struct v4l2_buffer buf;
        	for(i=0; i<BUFFER_COUNT; i++) {
        		// Query buffer
        		buf.index = i;
        		buf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
        		buf.memory = V4L2_MEMORY_MMAP;
        		ret = ioctl(fd , VIDIOC_QUERYBUF, &buf);
        		if(ret < 0) {
        			LOG("VIDIOC_QUERYBUF (%d) failed (%d)\n", i, ret);
        			return ret;
        		}
        		// mmap buffer
        		framebuf[i].length = buf.length;
        		framebuf[i].start = (char *) mmap(0, buf.length, PROT_READ|PROT_WRITE, MAP_SHARED, fd, buf.m.offset);
        		if (framebuf[i].start == MAP_FAILED) {
        			LOG("mmap (%d) failed: %s\n", i, strerror(errno));
        			return -1;
        		}
        		// Queen buffer
        		ret = ioctl(fd , VIDIOC_QBUF, &buf);
        		if (ret < 0) {
        			LOG("VIDIOC_QBUF (%d) failed (%d)\n", i, ret);
        			return -1;
        		}
        		LOG("Frame buffer %d: address=0x%x, length=%d\n", i, (unsigned int)framebuf[i].start, framebuf[i].length);
        	}
        // Stream On
        	enum v4l2_buf_type type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
        	ret = ioctl(fd, VIDIOC_STREAMON, &type);
        	if (ret < 0) {
        		LOG("VIDIOC_STREAMON failed (%d)\n", ret);
        		return ret;
        	}
        // Get frame
        	ret = ioctl(fd, VIDIOC_DQBUF, &buf);
        	if (ret < 0) {
        		LOG("	 (%d)\n", ret);
        		return ret;
        	}

        // Process the frame

        	FILE *fp = fopen(CAPTURE_FILE, "wb");
        	if (fp < 0) {
        		LOG("open frame data file failed\n");
        		return -1;
        	}
        	fwrite(framebuf[buf.index].start, 1, buf.length, fp);
        	fclose(fp);
        	//DisplayYUV();

        	LOG("Capture one frame saved in %s\n", CAPTURE_FILE);

        	//write_JPEG_file("test.jpg", framebuf[buf.index].start, 100, 720, 625);

        	LOG("Capture one frame saved in test.jpg\n");

        // Re-queen buffer
        	ret = ioctl(fd, VIDIOC_QBUF, &buf);
        	if (ret < 0) {
        		LOG("VIDIOC_QBUF failed (%d)\n", ret);
        		return ret;
        	}
        // Release the resource
        	for (i=0; i<BUFFER_COUNT; i++) {
        		munmap(framebuf[i].start, framebuf[i].length);
        	}
        	close(fd);
        	LOG("Camera test Done.\n");
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
