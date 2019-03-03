package com.net.markj.okhttp.okhttp;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Kron Xu on 2019/3/4 1:25
 * Description:
 */
public class OkManager {
    private OkHttpClient client;
    private static volatile OkManager okManager;
    private static final String TAG = OkManager.class.getSimpleName();
    private Handler handler;
    // 提交Json数据
    public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    //提交字符串
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown;charset=utf-8");

    private OkManager() {
        client = new OkHttpClient();
        handler = new Handler(Looper.getMainLooper());
    }

    public static OkManager getInstance() {
        OkManager instance = null;
        if (okManager == null) {
            synchronized (OkManager.class) {
                if (instance == null) {
                    instance = new OkManager();
                    okManager = instance;
                }
            }
        }

        return okManager;
    }

    /**
     * 异步请求json字符串
     * @param url
     * @param callback
     */
    public void asyncRequestJsonString(String url,Func1 callback){
        Request request = new Request.Builder().get().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && callback!= null){
                    onSuccessJsonStringMethod(response.body().string(),callback);
                }
            }
        });
    }

    /**
     * 请求返回的结果是json字符串
     * @param jsonValue
     * @param callback
     */
    private void onSuccessJsonStringMethod(String jsonValue,Func1 callback){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null){
                    try {
                        callback.onResponce(jsonValue);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 返回的是一个json对象
     * @param jsonValue
     * @param callback
     */
    private void onSuccessJsonObjectMethod(String jsonValue,Func2 callback){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null){
                    try{
                        callback.onResponce(new JSONObject(jsonValue));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 返回的是一个字节数组
     * @param bytes
     * @param callback
     */
    private void onSuccessByteArrayMethod(byte[] bytes,Func3 callback){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null){
                    try {
                        callback.onResponce(bytes);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public interface Func1{
        void onResponce(String result);
    }

    public interface Func2{
        void onResponce(JSONObject jsonObject);
    }

    public interface Func3{
        void onResponce(byte[] bytes);
    }

    public  interface Func4{
        void onResponce(Bitmap bitmap);
    }
}
