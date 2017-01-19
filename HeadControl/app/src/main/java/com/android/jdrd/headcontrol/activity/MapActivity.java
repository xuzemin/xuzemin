package com.android.jdrd.headcontrol.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.android.jdrd.headcontrol.R;
import com.android.jdrd.headcontrol.view.MyView;

import java.io.InputStream;


public class MapActivity extends AppCompatActivity {
    private MyView surfaceview=null;
    private int bitmap_width = 0,bitmap_height= 0;
    private EditText bitmap_edit_x,bitmap_edit_y;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //无title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_map);

        surfaceview=(MyView)findViewById(R.id.surfaceview);

        bitmap_edit_x = (EditText) this.findViewById(R.id.bitmap_center_x);
        bitmap_edit_y = (EditText) this.findViewById(R.id.bitmap_center_y);

        findViewById(R.id.button_clearlast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(surfaceview !=null&&surfaceview.point_xs.size()>=1){
                    surfaceview.point_xs.remove(surfaceview.point_xs.size()-1);
                    surfaceview.point_ys.remove(surfaceview.point_ys.size()-1);
                }
            }
        });
        findViewById(R.id.button_clearall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(surfaceview !=null){
                    surfaceview.point_xs.removeAllElements();
                    surfaceview.point_ys.removeAllElements();
                }
            }
        });
        findViewById(R.id.button_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (surfaceview.bitmap != null) {
                    if (surfaceview.bitmap_x +bitmap_width/2  > 0) {
                        surfaceview.bitmap_x -= 10;
                    }
                }
            }
        });
        findViewById(R.id.button_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (surfaceview.bitmap != null) {
                    if (surfaceview.bitmap_x + bitmap_width/2 < 600) {
                        surfaceview.bitmap_x += 10;
                    }
                }
            }
        });
        findViewById(R.id.button_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (surfaceview.bitmap != null) {
                    if (surfaceview.bitmap_y + bitmap_height/2 > 0) {
                        surfaceview.bitmap_y -= 10;
                    }
                }
            }
        });
        findViewById(R.id.button_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (surfaceview.bitmap != null) {
                    if (surfaceview.bitmap_y + bitmap_height/2 <= 900) {
                        surfaceview.bitmap_y += 10;
                    }
                }
            }
        });
        findViewById(R.id.button_move).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bitmap_edit_x.getText().toString().trim().equals("")&&!bitmap_edit_y.getText().toString().trim().equals(""))
                {
                    surfaceview.bitmap_x = Integer.parseInt(bitmap_edit_x.getText().toString())-bitmap_width/2;
                    surfaceview.bitmap_y = Integer.parseInt(bitmap_edit_y.getText().toString())-bitmap_height/2;
                }
            }
        });
        findViewById(R.id.button_rote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (surfaceview.bitmap != null){
                    surfaceview.rote+=45;
                }
            }
        });
        surfaceview.bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.tuzi);

        InputStream is = getResources().openRawResource(R.mipmap.tuzi);
        surfaceview.gifMovie = Movie.decodeStream(is);
//        if (surfaceview.gifMovie != null) {
//            surfaceview.testbitmap= BitmapFactory.decodeStream(i s);
////            gifImageWidth = bitmap.getWidth();
////            gifImageHeight = bitmap.getHeight();
//            surfaceview.testbitmap.recycle();
//        }
        surfaceview.bitmap = Bitmap.createBitmap(surfaceview.bitmap,0,0,100,100);
        bitmap_width = surfaceview.bitmap.getWidth();
        bitmap_height= surfaceview.bitmap.getHeight();
        Log.e("MyView","bitmap_width = " +bitmap_width+" bitmap_height = "+bitmap_height);
        Log.e("MyView","surfaceview.gifMovie.width() = " +surfaceview.gifMovie.width()+" surfaceview.gifMovie.height() = "+surfaceview.gifMovie.height());
    }
}
