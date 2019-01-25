package wcfb.com.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button download;
    Button bt2;
    Button bt3;
    ImageView image;

    OkHttpClient client;
    final static int SUCCESS_STATUS = 1;
    final static int FAIL_STATUS = 0;
    //工具类
    private OkManager manager;

    static String TAG = MainActivity.class.getSimpleName();
    static String imagePath = "https://www.baidu.com/img/xinshouye_77c426fce3f7fd448db185a7975efae5.png";
    private String json_path = "http://api2.hichao.com/stars?category=%E5%85%A8%E9%83%A8&pin=&ga=%2Fstars&flag=&gv=63&access_token=&gi=862949022047018&gos=5.2.3&p=2013022&gc=xiaomi&gn=mxyc_adr&gs=720x1280&gf=android&page=2";
    //服务器ip地址
    private String login_path = "http://192.168.62.109:8080/webproject/LoginAction";

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS_STATUS:
                    byte[] res = (byte[]) msg.obj;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(res, 0, res.length);
                    image.setImageBitmap(bitmap);
                    break;
                case FAIL_STATUS:
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        request();
        request2();
        request3();
    }

    public void init(){
        download = findViewById(R.id.download);
        bt2 = findViewById(R.id.bt2);
        bt3 = findViewById(R.id.bt3);
        image = findViewById(R.id.imageView);
        client = new OkHttpClient();
    }

    public void request(){
        final Request request = new Request.Builder().get().url(imagePath).build();
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //失败的时候操作
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message message = handler.obtainMessage();
                        if(response.isSuccessful()){
                            message.what = SUCCESS_STATUS;
                            message.obj = response.body().bytes();
                            handler.sendMessage(message);
                        }
                        else{
                            handler.sendEmptyMessage(FAIL_STATUS);
                        }
                    }
                });
            }
        });
    }

    public void request2(){
        manager = OkManager.getInstance();
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.asyncJsonStringByURL(json_path, new OkManager.Func() {
                    @Override
                    public void onResponse(String result) {
                        Toast.makeText(MainActivity.this,
                                result,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void request3(){
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> map = new HashMap<String, String>();
                map.put("username","admin");
                map.put("password","12345");
                manager.sendComplexForm(login_path, map, new OkManager.Func4() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Toast.makeText(MainActivity.this,jsonObject.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
