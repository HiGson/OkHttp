package com.net.markj.okhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp主要功能：
 * 1、get、post请求
 * 2、基于Http的文件上传和下载
 * 3、图片下载
 * 4、支持请求回调，直接返回对象，对象集合
 * 5、支持session的保持
 */
public class MainActivity extends AppCompatActivity {

    private OkHttpClient okHttpClient;
    private Request request;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String IMG_URL = "";
    private ArrayList<String> imgUrlList;
    private static final int SUCCESS = 1;
    private static final int FAIL = 2;
    private ImageView img;
    private Random random;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:
                    byte[] bytes = (byte[]) msg.obj;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    img.setImageBitmap(bitmap);

                    // 使用Picasso裁剪后展示
//                    img.setImageBitmap(new CropSqureTransform().transform(BitmapFactory.decodeByteArray(bytes,0,bytes.length)));
                    break;
                case FAIL:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.img);
        imgUrlList = new ArrayList();
        imgUrlList.add("https://t2.hddhhn.com/uploads/tu/201501/871/11.jpg");
        imgUrlList.add("http://www.fpwap.com/UploadFiles/article/bagua/2015/01/09/1420797277171580.png");
        imgUrlList.add("http://www.fpwap.com/UploadFiles/article/bagua/2015/01/09/1420797277699955.png");
        imgUrlList.add("http://www.fpwap.com/UploadFiles/article/bagua/2015/01/09/1420797277468777.png");
        imgUrlList.add("http://www.fpwap.com/UploadFiles/article/bagua/2015/01/09/1420797278902420.png");
        imgUrlList.add("http://www.fpwap.com/UploadFiles/article/bagua/2015/01/09/1420797278112407.png");
        okHttpClient = new OkHttpClient();

    }

    /**
     * 下载图片
     * @param view
     */
    public void downLoadPic(View view) {
        random = new Random();
        int index = random.nextInt(imgUrlList.size() - 1);

        request = new Request.Builder().get().url(imgUrlList.get(index)).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = handler.obtainMessage();
                if (response.isSuccessful()) {
                    byte[] bytes = response.body().bytes();
                    msg.what = SUCCESS;
                    msg.obj = bytes;
                    handler.sendMessage(msg);
                } else {
                    handler.sendEmptyMessage(FAIL);
                }
            }
        });
    }
}
