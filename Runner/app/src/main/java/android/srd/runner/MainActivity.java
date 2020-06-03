package android.srd.runner;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.srd.runner.util.Constant;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.skyworth.splicing.SerialPortUtil;

public class MainActivity extends AppCompatActivity {
    TextView TextView ;
    private String content = "";
    SerialPortUtil mSerialPortUtil;
    EditText editText;

    private final static int CONNECT_SUCCES = 1;
    private final static int GETDATA = 2;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONNECT_SUCCES:
                    break;
                case GETDATA:
                    TextView.setText(msg.obj.toString());
                    break;
                case Constant.COMMUNCITY_REEOR:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //单模式
        TextView = (TextView) this.findViewById(R.id.textView);
        editText = (EditText) this.findViewById(R.id.editText);
        mSerialPortUtil = SerialPortUtil.getInstance();
        //设置数据监听事件
        mSerialPortUtil.setOnDataReceiveListener(new SerialPortUtil.OnDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] buffer, int size) {
                // TODO Auto-generated method stub
//                TextView.setText(new String(buffer,0,size));
                content = new String(buffer, 0, size);
                Message msg =  Message.obtain(handler,GETDATA,content);
                msg.sendToTarget();
            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText.getText().toString().trim().equals("")){
                    Log.e("SerialActivity", "content"+editText.getText().toString().trim());
                    mSerialPortUtil.sendCmds(editText.getText().toString()+'\r'+'\n');
                    editText.setText("");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSerialPortUtil.closeSerialPort();
    }
}
