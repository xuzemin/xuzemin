#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <fcntl.h>              /* low-level i/o */
#include <unistd.h>
#include <errno.h>
#include <malloc.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/time.h>
#include <sys/mman.h>
#include <sys/ioctl.h>
#include <asm/types.h>          /* for videodev2.h */
#include <linux/videodev2.h>
#include <linux/usbdevice_fs.h>

#define  LOG_TAG    "WebCam"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#define CLEAR(x) memset (&(x), 0, sizeof (x))


#define ERROR_LOCAL -1
#define SUCCESS_LOCAL 0
#ifdef ANDROID_ENV
#define LOG LOGV
#else
#define LOG printf
#endif

#define VIDEO_FORMAT V4L2_PIX_FMT_YUYV    //这里是输出的数据格式，要是摄像头支持jpeg的，可以直接把数据
//V4L2_PIX_FMT_MJPEG         //保存为jpg格式的图片，如果是yuv的则需要转换，转换在下篇介绍。
//V4L2_PIX_FMT_JPEG			   //这里主要是把yuv格式保存为文件
//V4L2_PIX_FMT_YUYV
//V4L2_PIX_FMT_YUV420
//V4L2_PIX_FMT_RGB32
#define BUFFER_COUNT 1

#define IMG_WIDTH 640
#define IMG_HEIGHT 480
#define VIDEO_WIDTH 720
#define VIDEO_HEIGHT 480
void  yuyv_2_rgb888( char * pointer,char * frame_buffer);
int displayyuv();
void yuyv422toABGRY(unsigned char *src);
