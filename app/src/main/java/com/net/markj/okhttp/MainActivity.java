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
    private static final int SUCCESS = 1;
    private static final int FAIL = 2;
    private ImageView img;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:
                    byte[] bytes = (byte[]) msg.obj;
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//                    img.setImageBitmap(bitmap);

                    // 使用Picasso裁剪后展示
                    img.setImageBitmap(new CropSqureTransform().transform(BitmapFactory.decodeByteArray(bytes,0,bytes.length)));
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
        okHttpClient = new OkHttpClient();
        request = new Request.Builder().get().url("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1551636220656&di=d29851d1c1eacb6fb0f377005c2535c7&imgtype=0&src=http%3A%2F%2Fimg10.360buyimg.com%2Fimgzone%2Fjfs%2Ft2824%2F349%2F818203518%2F140577%2Fa8452f9c%2F5727572cN466b044e.jpg").build();

    }

    /**
     * 下载图片
     * @param view
     */
    public void downLoadPic(View view) {
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
