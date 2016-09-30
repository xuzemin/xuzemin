package com.android.jdrd.opencv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private ImageView img;
    private String TAG = "ImageView";
    CascadeClassifier haarCascade;
    private File mCascadeFile;
    private Rect[] facesArray;
    double tempX = 0.0;
    double tempY = 0.0;
    public double moveX = 0.0;
    public double moveY = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(
                R.mipmap.picture)).getBitmap();
        int w = bitmap.getWidth(), h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int[] resultPixes = OpenCVHelper.gray(pix, w, h);
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        result.setPixels(resultPixes, 0, w, 0, 0, w, h);
        img = (ImageView) findViewById(R.id.img);
        img.setImageBitmap(result);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = OpenCVHelper.get();
                Log.e("MainAcitivity", "return" + i);
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = OpenCVHelper.keyDownPress();
                Log.e("MainAcitivity", "return" + i);
            }
        });
        findViewById(R.id.button10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = OpenCVHelper.keyDownInstitute();
                Log.e("MainAcitivity", "return" + i);
            }
        });
        findViewById(R.id.button11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = OpenCVHelper.keyUpPress();
                Log.e("MainAcitivity", "return" + i);
            }
        });
        findViewById(R.id.button12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = OpenCVHelper.keyUpInstitute();
                Log.e("MainAcitivity", "return" + i);
            }
        });
        findViewById(R.id.button13).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = OpenCVHelper.keyLeftPress();
                Log.e("MainAcitivity", "return" + i);
            }
        });
        findViewById(R.id.button14).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = OpenCVHelper.keyLeftInstitute();
                Log.e("MainAcitivity", "return" + i);
            }
        });
        findViewById(R.id.button15).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = OpenCVHelper.keyRightPress();
                Log.e("MainAcitivity", "return" + i);
            }
        });
        findViewById(R.id.button16).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = OpenCVHelper.keyRightInstitute();
                Log.e("MainAcitivity", "return" + i);
            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mat mat = new Mat();
                MatOfRect faces = new MatOfRect();
                int i = OpenCVHelper.getdata(mat.getNativeObjAddr());
                Log.e(TAG, "return" + i);
                if (!mat.empty()) {
                    if (haarCascade != null) {
                        haarCascade.detectMultiScale(mat, faces, 1.1, 2, 2, new Size(200, 200), new Size());
                        Log.e(TAG, "facesArray.length = " +  faces.toArray().length);
                    }
                    facesArray = faces.toArray();
                    Log.e(TAG, "facesArray.length = " + facesArray.length);
                    Log.e(TAG, "return" + mat.size());
                    Mat tmp = new Mat();
                    Imgproc.cvtColor(mat, tmp, Imgproc.COLOR_GRAY2RGBA, 4);
                    Bitmap result = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(tmp, result);
                    Bitmap mBitmap = Bitmap.createScaledBitmap(result, 1920, 1080, true);
                    img.setImageBitmap(mBitmap);
                    getdata();


                }
            }
        });
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = OpenCVHelper.stop();
                Log.e("MainAcitivity", "return" + i);
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                //OpenCV加载成功后，载入级联分类器
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    try {
                        //用InputStream和FileOutputStream把级联分类器文件从项目资源拷贝到应用中
                        //新建一个cascade文件夹，并把级联分类器文件中的内容拷贝到该文件夹下的新建文件中
                        //同时拷贝和保存的原因是，我们要把文件从项目目录中移动到手机文件系统中
                        InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt0);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "cascade.xml");
                        Log.i(TAG, "cascadeDir!!!!"+cascadeDir.exists());
                        FileOutputStream os = new FileOutputStream(mCascadeFile);
                        Log.i(TAG, "mCascadeFile!!!!"+mCascadeFile.exists());
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        //新建一个CascadeClassifier对象，稍后用于摄像头源中检测人脸
                        haarCascade = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        Log.i(TAG, "mCascadeFile.getAbsolutePath()!!!!:::::::"+haarCascade.load(mCascadeFile.getAbsolutePath()));
                        Log.i(TAG, "mCascadeFile.getAbsolutePath()!!!!"+mCascadeFile.getAbsolutePath());
                        if (haarCascade.empty()) {
                            Log.i(TAG, "级联分类器加载失败!!!!");
                            haarCascade = null;
                        }

                    } catch (Exception e) {
                        Log.i(TAG, "Cascase not found");
                    }
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    public void getdata() {
        for (int i = 0; i < facesArray.length; i++) {
            //Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), new Scalar(010), 3);
            Log.e(TAG, "facesArray[i].tl().x = " + facesArray[i].tl().x + "   ; facesArray[i].tl().y = " + facesArray[i].tl().y);
            Log.e(TAG, "facesArray[i].br().x = " + facesArray[i].br().x + "   ; facesArray[i].br().y = " + facesArray[i].br().y);

            double centerX = (facesArray[i].br().x - facesArray[i].tl().x) / 2 + facesArray[i].tl().x;
            double centerY = (facesArray[i].br().y - facesArray[i].tl().y) / 2 + facesArray[i].tl().y;
            Log.e(TAG, "centerX = " + centerX + "; centerY = " + centerY);
            //摄像头画面中心点坐标 X = 400，Y = 300
            if (tempX == 0 && tempY == 0) {
                tempX = 400;
                tempY = 300;
            } else {
                moveX = centerX - tempX;
                moveY = centerY - tempY;
            }

            double UpOffSet = -15;
            double DownOffSet = 15;
            double LeftOffSet = 20;
            double RightOffSet = -20;

            if (moveX >= LeftOffSet && moveY <= DownOffSet && moveY >= UpOffSet) {
                //Log.e(TAG, "向左移动了" + moveX);
            } else if (moveX <= RightOffSet && moveY <= DownOffSet && moveY >= UpOffSet) {
                //Log.e(TAG, "向右移动了" + moveX);
            } else if (moveY >= DownOffSet && moveX <= LeftOffSet && moveX >= RightOffSet) {
                //Log.e(TAG, "向下移动了" + moveY);
            } else if (moveY <= UpOffSet && moveX <= LeftOffSet && moveX >= RightOffSet) {
                //Log.e(TAG, "向上移动了" + moveY);
            } else if (moveX >= LeftOffSet && moveY >= DownOffSet) {
                if (Math.abs(moveX) >= Math.abs(moveY)) {
                    //Log.e(TAG, "向左移动了" + moveX);
                } else {
                    //Log.e(TAG, "向下移动了" + moveY);
                }
            } else if (moveX >= LeftOffSet && moveY <= UpOffSet) {
                if (Math.abs(moveX) >= Math.abs(moveY)) {
                    //Log.e(TAG, "向左移动了" + moveX);
                } else {
                    //Log.e(TAG, "向上移动了" + moveY);
                }
            } else if (moveX <= RightOffSet && moveY >= DownOffSet) {
                if (Math.abs(moveX) >= Math.abs(moveY)) {
                    //Log.e(TAG, "向右移动了" + moveX);
                } else {
                    //Log.e(TAG, "向下移动了" + moveY);
                }
            } else if (moveX <= RightOffSet && moveY <= UpOffSet) {
                if (Math.abs(moveX) >= Math.abs(moveY)) {
                    //Log.e(TAG, "向左移动了" + moveX);
                } else {
                    //Log.e(TAG, "向上移动了" + moveY);
                }
            } else {
                //Log.e(TAG, "移动距离小于阈值");
            }
        }
    }
}
