#include "ImageProc.h"
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <linux/videodev2.h>
#include <sys/ioctl.h>

#include <jpeg/jpeglib.h>


int errnoexit(const char *s)
{
	LOGE("%s error %d, %s", s, errno, strerror (errno));
	return ERROR_LOCAL;
}


int xioctl(int fd, int request, void *arg)
{
	int r;

	do r = ioctl (fd, request, arg);
	while (-1 == r && EINTR == errno);

	return r;
}
int checkCamerabase(void){
	struct stat st;
	int i;
	int start_from_4 = 1;

	/* if /dev/video[0-3] exist, camerabase=4, otherwise, camrerabase = 0 */
	for(i=0 ; i<4 ; i++){
		sprintf(dev_name,"/dev/video%d",i);
		if (-1 == stat (dev_name, &st)) {
			start_from_4 &= 0;
		}else{
			start_from_4 &= 1;
		}
	}

	if(start_from_4){
		return 4;
	}else{
		return 0;
	}
}

int opendevice(int i)
{
	struct stat st;

	sprintf(dev_name,"/dev/video%d",i);

	if (-1 == stat (dev_name, &st)) {
		LOGE("Cannot identify '%s': %d, %s", dev_name, errno, strerror (errno));
		return ERROR_LOCAL;
	}

	if (!S_ISCHR (st.st_mode)) {
		LOGE("%s is no device", dev_name);
		return ERROR_LOCAL;
	}

	fd = open (dev_name, O_RDWR | O_NONBLOCK, 0);

	if (-1 == fd) {
		LOGE("Cannot open '%s': %d, %s", dev_name, errno, strerror (errno));
		return ERROR_LOCAL;
	}
	return SUCCESS_LOCAL;
}

int initdevice(void) 
{
	struct v4l2_capability cap;
	struct v4l2_cropcap cropcap;
	struct v4l2_crop crop;
	struct v4l2_format fmt;
	unsigned int min;

	if (-1 == xioctl (fd, VIDIOC_QUERYCAP, &cap)) {
		if (EINVAL == errno) {
			LOGE("%s is no V4L2 device", dev_name);
			return ERROR_LOCAL;
		} else {
			return errnoexit ("VIDIOC_QUERYCAP");
		}
	}

	if (!(cap.capabilities & V4L2_CAP_VIDEO_CAPTURE)) {
		LOGE("%s is no video capture device", dev_name);
		return ERROR_LOCAL;
	}

	if (!(cap.capabilities & V4L2_CAP_STREAMING)) {
		LOGE("%s does not support streaming i/o", dev_name);
		return ERROR_LOCAL;
	}

	CLEAR (cropcap);

	cropcap.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;

	if (0 == xioctl (fd, VIDIOC_CROPCAP, &cropcap)) {
		crop.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
		crop.c = cropcap.defrect; 

		if (-1 == xioctl (fd, VIDIOC_S_CROP, &crop)) {
			switch (errno) {
			case EINVAL:
				break;
			default:
				break;
			}
		}
	} else {
	}

	CLEAR (fmt);

	fmt.type                = V4L2_BUF_TYPE_VIDEO_CAPTURE;

	fmt.fmt.pix.width       = IMG_WIDTH; 
	fmt.fmt.pix.height      = IMG_HEIGHT;

	fmt.fmt.pix.pixelformat = V4L2_PIX_FMT_YUYV;
	fmt.fmt.pix.field       = V4L2_FIELD_INTERLACED;

	if (-1 == xioctl (fd, VIDIOC_S_FMT, &fmt))
		return errnoexit ("VIDIOC_S_FMT");

	min = fmt.fmt.pix.width * 2;
	if (fmt.fmt.pix.bytesperline < min)
		fmt.fmt.pix.bytesperline = min;
	min = fmt.fmt.pix.bytesperline * fmt.fmt.pix.height;
	if (fmt.fmt.pix.sizeimage < min)
		fmt.fmt.pix.sizeimage = min;

	return initmmap ();

}

int initmmap(void)
{
	struct v4l2_requestbuffers req;

	CLEAR (req);

	req.count               = 4;
	req.type                = V4L2_BUF_TYPE_VIDEO_CAPTURE;
	req.memory              = V4L2_MEMORY_MMAP;

	if (-1 == xioctl (fd, VIDIOC_REQBUFS, &req)) {
		if (EINVAL == errno) {
			LOGE("%s does not support memory mapping", dev_name);
			return ERROR_LOCAL;
		} else {
			return errnoexit ("VIDIOC_REQBUFS");
		}
	}

	if (req.count < 2) {
		LOGE("Insufficient buffer memory on %s", dev_name);
		return ERROR_LOCAL;
	}

	buffers = calloc (req.count, sizeof (*buffers));

	if (!buffers) {
		LOGE("Out of memory");
		return ERROR_LOCAL;
	}

	for (n_buffers = 0; n_buffers < req.count; ++n_buffers) {
		struct v4l2_buffer buf;

		CLEAR (buf);

		buf.type        = V4L2_BUF_TYPE_VIDEO_CAPTURE;
		buf.memory      = V4L2_MEMORY_MMAP;
		buf.index       = n_buffers;

		if (-1 == xioctl (fd, VIDIOC_QUERYBUF, &buf))
			return errnoexit ("VIDIOC_QUERYBUF");

		buffers[n_buffers].length = buf.length;
		buffers[n_buffers].start =
				mmap (NULL ,
						buf.length,
						PROT_READ | PROT_WRITE,
						MAP_SHARED,
						fd, buf.m.offset);

		if (MAP_FAILED == buffers[n_buffers].start)
			return errnoexit ("mmap");
	}

	return SUCCESS_LOCAL;
}

int startcapturing(void)
{
	unsigned int i;
	enum v4l2_buf_type type;

	for (i = 0; i < n_buffers; ++i) {
		struct v4l2_buffer buf;

		CLEAR (buf);

		buf.type        = V4L2_BUF_TYPE_VIDEO_CAPTURE;
		buf.memory      = V4L2_MEMORY_MMAP;
		buf.index       = i;

		if (-1 == xioctl (fd, VIDIOC_QBUF, &buf))
			return errnoexit ("VIDIOC_QBUF");
	}

	type = V4L2_BUF_TYPE_VIDEO_CAPTURE;

	if (-1 == xioctl (fd, VIDIOC_STREAMON, &type))
		return errnoexit ("VIDIOC_STREAMON");

	return SUCCESS_LOCAL;
}

int readframeonce(void)
{
	for (;;) {
		fd_set fds;
		struct timeval tv;
		int r;

		FD_ZERO (&fds);
		FD_SET (fd, &fds);

		tv.tv_sec = 2;
		tv.tv_usec = 0;

		r = select (fd + 1, &fds, NULL, NULL, &tv);

		if (-1 == r) {
			if (EINTR == errno)
				continue;

			return errnoexit ("select");
		}

		if (0 == r) {
			LOGE("select timeout");
			return ERROR_LOCAL;

		}

		if (readframe ()==1)
			break;

	}

	return SUCCESS_LOCAL;

}


void processimage (const void *p)
{
	yuyv422toABGRY((unsigned char *)p);
}

int readframe(void)
{

	struct v4l2_buffer buf;
	unsigned int i;

	CLEAR (buf);

	buf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
	buf.memory = V4L2_MEMORY_MMAP;

	if (-1 == xioctl (fd, VIDIOC_DQBUF, &buf)) {
		switch (errno) {
		case EAGAIN:
			return 0;
		case EIO:
		default:
			return errnoexit ("VIDIOC_DQBUF");
		}
	}

	//assert (buf.index < n_buffers);

	processimage (buffers[buf.index].start);

	FILE * outfile;
	if ((outfile = fopen("data/data/com.camera.simplewebcam/frame.yuv420", "wb")) == NULL) {
			fprintf(stderr, "can't open %s\n", outfile);
			return -1;
		}

	if (outfile < 0) {
		return -1;
	}
	fwrite(buffers[buf.index].start, 1, buf.length, outfile);  //保存yuv数据进文件
	fclose(outfile);

	write_JPEG_file("data/data/com.camera.simplewebcam/test.jpg", buffers[buf.index].start, 100, 720, 625);

	if (-1 == xioctl (fd, VIDIOC_QBUF, &buf))
		return errnoexit ("VIDIOC_QBUF");

	return 1;
}

int write_JPEG_file (const char * filename, unsigned char* yuvData, int quality,int image_width,int image_height)
{

	struct jpeg_compress_struct cinfo;
	struct jpeg_error_mgr jerr;

	FILE * outfile;    // target file
	//     JSAMPROW row_pointer[1];  // pointer to JSAMPLE row[s]
	//     int row_stride;    // physical row width in image buffer
	JSAMPIMAGE  buffer;

	unsigned char *pSrc,*pDst;
	int band,i,buf_width[3],buf_height[3];
	cinfo.err = jpeg_std_error(&jerr);

	jpeg_create_compress(&cinfo);

	if ((outfile = fopen(filename, "wb")) == NULL) {
		fprintf(stderr, "can't open %s\n", filename);
		return -1;
	}

	LOG("write_JPEG_file 2 \n");

	jpeg_stdio_dest(&cinfo, outfile);

	cinfo.image_width = image_width;  // image width and height, in pixels
	cinfo.image_height = image_height;
	cinfo.input_components = 3;    // # of color components per pixel
	cinfo.in_color_space = JCS_RGB;  //colorspace of input image

	jpeg_set_defaults(&cinfo);
	jpeg_set_quality(&cinfo, quality, TRUE );
	//////////////////////////////
	cinfo.raw_data_in = TRUE;
	cinfo.jpeg_color_space = JCS_YCbCr;
	cinfo.comp_info[0].h_samp_factor = 2;
	cinfo.comp_info[0].v_samp_factor = 2;
	/////////////////////////
	jpeg_start_compress(&cinfo, TRUE);
	buffer = (JSAMPIMAGE) (*cinfo.mem->alloc_small) ((j_common_ptr) &cinfo,
			JPOOL_IMAGE, 3 * sizeof(JSAMPARRAY));
	for(band=0; band <3; band++)
	{
		buf_width[band] = cinfo.comp_info[band].width_in_blocks * DCTSIZE;
		buf_height[band] = cinfo.comp_info[band].v_samp_factor * DCTSIZE;
		buffer[band] = (*cinfo.mem->alloc_sarray) ((j_common_ptr) &cinfo,
				JPOOL_IMAGE, buf_width[band], buf_height[band]);
	}
	unsigned char *rawData[3];
	rawData[0]=yuvData;
	rawData[1]=yuvData+image_width*image_height;
	rawData[2]=yuvData+image_width*image_height*5/4;

	int src_width[3],src_height[3];
	for(i=0;i<3;i++)
	{
		src_width[i]=(i==0)?image_width:image_width/2;
		src_height[i]=(i==0)?image_height:image_height/2;
	}


	//max_line一般为16，外循环每次处理16行数据。
	int max_line = cinfo.max_v_samp_factor*DCTSIZE;
	int counter;
	for(counter=0; cinfo.next_scanline < cinfo.image_height; counter++)
	{
		//buffer image copy.
		for(band=0; band <3; band++)  //每个分量分别处理
		{
			int mem_size = src_width[band];//buf_width[band];
			pDst = (unsigned char *) buffer[band][0];
			pSrc = (unsigned char *) rawData[band] + counter*buf_height[band]
			                                                            * src_width[band];//buf_width[band];  //yuv.data[band]分别表示YUV起始地址

			for(i=0; i <buf_height[band]; i++)  //处理每行数据
			{
				memcpy(pDst, pSrc, mem_size);
				pSrc += src_width[band];//buf_width[band];
				pDst += buf_width[band];
			}
		}
		jpeg_write_raw_data(&cinfo, buffer, max_line);
	}

	jpeg_finish_compress(&cinfo);
	fclose(outfile);
	jpeg_destroy_compress(&cinfo);
	return 0;
}

int stopcapturing(void)
{
	enum v4l2_buf_type type;

	type = V4L2_BUF_TYPE_VIDEO_CAPTURE;

	if (-1 == xioctl (fd, VIDIOC_STREAMOFF, &type))
		return errnoexit ("VIDIOC_STREAMOFF");

	return SUCCESS_LOCAL;

}

int uninitdevice(void)
{
	unsigned int i;

	for (i = 0; i < n_buffers; ++i)
		if (-1 == munmap (buffers[i].start, buffers[i].length))
			return errnoexit ("munmap");

	free (buffers);

	return SUCCESS_LOCAL;
}

int closedevice(void)
{
	if (-1 == close (fd)){
		fd = -1;
		return errnoexit ("close");
	}

	fd = -1;
	return SUCCESS_LOCAL;
}


void yuyv422toABGRY(unsigned char *src)
{

	int width=0;
	int height=0;

	width = IMG_WIDTH;
	height = IMG_HEIGHT;

	int frameSize =width*height*2;

	int i;

	if((!rgb || !ybuf)){
		return;
	}
	int *lrgb = NULL;
	int *lybuf = NULL;

	lrgb = &rgb[0];
	lybuf = &ybuf[0];

	if(yuv_tbl_ready==0){
		for(i=0 ; i<256 ; i++){
			y1192_tbl[i] = 1192*(i-16);
			if(y1192_tbl[i]<0){
				y1192_tbl[i]=0;
			}

			v1634_tbl[i] = 1634*(i-128);
			v833_tbl[i] = 833*(i-128);
			u400_tbl[i] = 400*(i-128);
			u2066_tbl[i] = 2066*(i-128);
		}
		yuv_tbl_ready=1;
	}

	for(i=0 ; i<frameSize ; i+=4){
		unsigned char y1, y2, u, v;
		y1 = src[i];
		u = src[i+1];
		y2 = src[i+2];
		v = src[i+3];

		int y1192_1=y1192_tbl[y1];
		int r1 = (y1192_1 + v1634_tbl[v])>>10;
		int g1 = (y1192_1 - v833_tbl[v] - u400_tbl[u])>>10;
		int b1 = (y1192_1 + u2066_tbl[u])>>10;

		int y1192_2=y1192_tbl[y2];
		int r2 = (y1192_2 + v1634_tbl[v])>>10;
		int g2 = (y1192_2 - v833_tbl[v] - u400_tbl[u])>>10;
		int b2 = (y1192_2 + u2066_tbl[u])>>10;

		r1 = r1>255 ? 255 : r1<0 ? 0 : r1;
		g1 = g1>255 ? 255 : g1<0 ? 0 : g1;
		b1 = b1>255 ? 255 : b1<0 ? 0 : b1;
		r2 = r2>255 ? 255 : r2<0 ? 0 : r2;
		g2 = g2>255 ? 255 : g2<0 ? 0 : g2;
		b2 = b2>255 ? 255 : b2<0 ? 0 : b2;

		*lrgb++ = 0xff000000 | b1<<16 | g1<<8 | r1;
		*lrgb++ = 0xff000000 | b2<<16 | g2<<8 | r2;

		if(lybuf!=NULL){
			*lybuf++ = y1;
			*lybuf++ = y2;
		}
	}

}


void 
Java_com_camera_simplewebcam_CameraPreview_pixeltobmp( JNIEnv* env,jobject thiz,jobject bitmap){

	jboolean bo;


	AndroidBitmapInfo  info;
	void*              pixels;
	int                ret;
	int i;
	int *colors;

	int width=0;
	int height=0;

	if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return;
	}

	width = info.width;
	height = info.height;

	if(!rgb || !ybuf) return;

	if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
		LOGE("Bitmap format is not RGBA_8888 !");
		return;
	}

	if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
	}

	colors = (int*)pixels;
	int *lrgb =NULL;
	lrgb = &rgb[0];

	for(i=0 ; i<width*height ; i++){
		*colors++ = *lrgb++;
	}

	AndroidBitmap_unlockPixels(env, bitmap);

}

jint 
Java_com_camera_simplewebcam_CameraPreview_prepareCamera( JNIEnv* env,jobject thiz, jint videoid){

	int ret;

	if(camerabase<0){
		camerabase = checkCamerabase();
	}

	ret = opendevice(camerabase + videoid);

	if(ret != ERROR_LOCAL){
		ret = initdevice();
	}
	if(ret != ERROR_LOCAL){
		ret = startcapturing();

		if(ret != SUCCESS_LOCAL){
			stopcapturing();
			uninitdevice ();
			closedevice ();
			LOGE("device resetted");	
		}

	}

	if(ret != ERROR_LOCAL){
		rgb = (int *)malloc(sizeof(int) * (IMG_WIDTH*IMG_HEIGHT));
		ybuf = (int *)malloc(sizeof(int) * (IMG_WIDTH*IMG_HEIGHT));
	}
	return ret;
}	



jint 
Java_com_camera_simplewebcam_CameraPreview_prepareCameraWithBase( JNIEnv* env,jobject thiz, jint videoid, jint videobase){

	int ret;

	camerabase = videobase;

	return Java_com_camera_simplewebcam_CameraPreview_prepareCamera(env,thiz,videoid);

}

void 
Java_com_camera_simplewebcam_CameraPreview_processCamera( JNIEnv* env,
		jobject thiz){

	readframeonce();
}

void 
Java_com_camera_simplewebcam_CameraPreview_stopCamera(JNIEnv* env,jobject thiz){

	stopcapturing ();

	uninitdevice ();

	closedevice ();

	if(rgb) free(rgb);
	if(ybuf) free(ybuf);

	fd = -1;

}

