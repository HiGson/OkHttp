package com.net.markj.okhttp.okhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Kron Xu on 2019/3/4 1:25
 * Description:
 */
public class OkManager {
    private OkHttpClient client;
    private static volatile OkManager okManager;
    private static final String TAG = OkManager.class.getSimpleName();
    private static final long REQUEST_TIME = 30;
    private Handler handler;
    // 提交Json数据
    public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    //提交字符串
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown;charset=utf-8");

    private OkManager() {
        client = new OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIME, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIME, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIME, TimeUnit.SECONDS)
//                .addInterceptor(new LoggerInterceptor())
                .build();
        ;
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
     *
     * @param url
     * @param callback
     */
    public void asyncRequestJsonString(String url, Func1 callback) {
        Request request = new Request.Builder().get().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && callback != null) {
                    onSuccessJsonStringMethod(response.body().string(), callback);
                }
            }
        });
    }

    /**
     * 异步请求返回JSonObject对象
     *
     * @param url
     * @param callback
     */
    public void asyncRequestJsonObject(String url, Func2 callback) {
        Request request = new Request.Builder().url(url).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    onSuccessJsonObjectMethod(response.body().string(), callback);
                }
            }
        });
    }

    /**
     * 异步请求返回byte[]数组
     *
     * @param url
     * @param callback
     */
    public void asyncRequestByteArray(String url, Func3 callback) {
        Request request = new Request.Builder().url(url).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    onSuccessByteArrayMethod(response.body().bytes(), callback);
                }
            }
        });
    }

    /**
     * 异步请求返回Bitmap对象
     *
     * @param url
     * @param callback
     */
    public void asyncRequestBitmap(String url, Func4 callback) {
        Request request = new Request.Builder().url(url).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    callback.onResponce(BitmapFactory.decodeByteArray(response.body().bytes(), 0, response.body().bytes().length));
                }
            }
        });
    }

    /**
     * 异步提交键值对
     *
     * @param url
     * @param params
     * @param callback
     */
    public void asyncPostkeyMap(String url, Map<String, String> params, Func1 callback) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }

        RequestBody body = builder.build();
        Request request = new Request.Builder().post(body).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    callback.onResponce(response.body().string());
                }
            }
        });
    }

    /**
     * 异步上传Multipart文件
     * 我们在有些情况下既要上传文件还要上传其他类型字段。比如在个人中心我们可以修改名字，年龄，修改图像，这其实就是一个表单。
     * 这里我们用到MuiltipartBody ,它 是RequestBody 的一个子类,我们提交表单就是利用这个类来构建一个 RequestBody
     * @param url
     * @param filePath
     * @param fileName
     * @param params
     * @param callback
     */
    public void asyncPostForm(String url, String filePath, String fileName, Map params, Func1 callback) {
//上传的图片
        File file = new File(filePath, fileName);
        //2.通过new MultipartBody build() 创建requestBody对象，
        MultipartBody.Builder builder = new MultipartBody.Builder()
                //设置类型是表单
                .setType(MultipartBody.FORM)
                //添加数据
                .addFormDataPart("username", "zhangqilu")
                .addFormDataPart("age", "25")
                .addFormDataPart("image", "zhangqilu.png", RequestBody.create(MediaType.parse("image/png"), file));
        RequestBody requestBody = builder.build();
        //3.创建Request对象，设置URL地址，将RequestBody作为post方法的参数传入
        Request request = new Request.Builder().url(url).post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = client.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()){
                    callback.onResponce(response.body().string());
                }
            }
        });
    }

    /**
     * 异步提交字符串
     *
     * @param url
     * @param body
     * @param callBack
     */
    public void asyncPostString(String url, String body, Func1 callBack) {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, body);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    callBack.onResponce(response.body().string());
                }
            }
        });
    }


    /**
     * 异步上传文件
     *
     * @param url
     * @param filePath
     * @param fileName
     * @param callback
     */
    public void asyncPostFile(String url, String filePath, String fileName, Func1 callback) {
        //上传的图片
        File file = new File(filePath, fileName);
        //2.通过RequestBody.create 创建requestBody对象,application/octet-stream 表示文件是任意二进制数据流
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        //3.创建Request对象，设置URL地址，将RequestBody作为post方法的参数传入
        Request request = new Request.Builder().url(url).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    callback.onResponce(response.body().string());
                }
            }
        });
    }

    /**
     * 异步下载文件并存储到手机存储中，获奖下载的图片设置到ImageView中
     *
     * @param url
     * @param filePath
     * @param fileName
     */
    public void asyncDownloadFile(String url, String filePath, String fileName) {
        //2.创建Request对象，设置一个url地址（百度地址）,设置请求方式。
        Request request = new Request.Builder().url(url).get().build();
        //3.创建一个call对象,参数就是Request请求对象
        Call call = client.newCall(request);
        //4.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //拿到字节流
                InputStream is = response.body().byteStream();
                int len = 0;
                //设置下载图片存储路径和名称
                File file = new File(filePath, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buf = new byte[128];
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                fos.flush();
                fos.close();
                is.close();
            }

             /*@Override
             public void onResponse(Call call, Response response) throws IOException {
                 InputStream is = response.body().byteStream();
                 //使用 BitmapFactory 的 decodeStream 将图片的输入流直接转换为 Bitmap
                 final Bitmap bitmap = BitmapFactory.decodeStream(is);
                 //在主线程中操作UI
                 runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         //然后将Bitmap设置到 ImageView 中
                         imageView.setImageBitmap(bitmap);
                     }
                 });

                 is.close();
            }*/
        });
    }

    /**
     * 请求返回的结果是json字符串
     *
     * @param jsonValue
     * @param callback
     */
    private void onSuccessJsonStringMethod(String jsonValue, Func1 callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    try {
                        callback.onResponce(jsonValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 返回的是一个json对象
     *
     * @param jsonValue
     * @param callback
     */
    private void onSuccessJsonObjectMethod(String jsonValue, Func2 callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    try {
                        callback.onResponce(new JSONObject(jsonValue));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 返回的是一个字节数组
     *
     * @param bytes
     * @param callback
     */
    private void onSuccessByteArrayMethod(byte[] bytes, Func3 callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    try {
                        callback.onResponce(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 返回Json字符串回调接口
     */
    public interface Func1 {
        void onResponce(String result);
    }

    /**
     * 返回JsonObject对象回调接口
     */
    public interface Func2 {
        void onResponce(JSONObject jsonObject);
    }

    /**
     * 返回字节数组回调接口
     */
    public interface Func3 {
        void onResponce(byte[] bytes);
    }

    /**
     * 返回Bitmap对象回调接口
     */
    public interface Func4 {
        void onResponce(Bitmap bitmap);
    }


    /**
     * 关于取消请求和封装
     *         call.cancel();//取消请求，不能取消已经准备完成的请求
     *         okHttpClient.dispatcher().cancelAll();//取消所有请求
     *
     * 有时候网络条件不好的情况下，用户会主动关闭页面，这时候需要取消正在请求的http request, OkHttp提供了cancel方法，
     * 但是实际在使用过程中发现，如果调用cancel()方法，会回调到CallBack里面的 onFailure方法中，
     *
     *          void onFailure(Call call, IOException e);
     *
     * 可以看到注释，当取消一个请求，网络连接错误，或者超时都会回调到这个方法中来，但是我想对取消请求做一下单独处理，
     * 这个时候就需要区分不同的失败类型了
     *
     * 解决思路
     *
     * 测试发现不同的失败类型返回的IOException e不一样，所以可以通过e.toString 中的关键字来区分不同的错误类型
     *
     * 自己主动取消的错误的 java.net.SocketException:Socket closed
     * 超时的错误是 java.net.SocketTimeoutException
     * 网络出错的错误是java.net.ConnectException: Failed to connect to xxxxx
     *
     *代码

     all.enqueue(new  Callback() {
            @Override
            public void onFailure (Call call, IOException e){
                    if (e.toString().contains("closed")) {
                         //如果是主动取消的情况下
                    } else {
                          //其他情况下
                    }
    }

     */
}