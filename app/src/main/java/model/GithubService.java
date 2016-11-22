package model;

import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Path;
import rx.Observable;

public class GithubService {
    public static final int PER_PAGE = 1;
    public static String ENDPOINT = "https://api.github.com/";

    private GithubAPIInterface mGithubAPI;

    private GithubService() {
        /**
         * Retrofit封装了Okhttp，其实是Okhttp进行操作
         * Android4.4的源码中可以看到HttpURLConnection已经替换成OkHttp实现
         * 设置拦截器：BODY--- 请求/响应行 + 头 + 体
         * Logger()设置定向过滤，默认是okhttp
         * .addInterceptor(httpLoggingInterceptor)//应用拦截器
         */
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i("Rxjava", message);
            }
        });
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)//设置重连
                .connectTimeout(15, TimeUnit.SECONDS)
                .addNetworkInterceptor(new StethoInterceptor())//启用网络监视
//                .addInterceptor(httpLoggingInterceptor)//设置应用拦截器，主要用于设置公共参数，头信息，日志拦截等
                .build();

        /**
         * 1.基地址必须有（可以是全部，也可以是部分）
         * 2.添加拦截器(非必须)
         * 3.Converter是对于Call<T>中T的转换， Call<ResponseBody>--------->Call<Poju>
         *   Call<T>中的Call也是可以被替换的，而返回值的类型就决定你后续的处理程序逻辑(非必须)
         * 4.CallAdapter可以对Call转换，Retrofit提供了多个CallAdapter，这里以RxJava的为例，用Observable代替Call(非必须)
         *
         * 5.初始化Retrofit
         * 6.用Retrofit创建出接口的代理对象，用代理对象来操作其方法,返回Call<Poju>
         *     通过Call<Poju>来请求入队，execute/enqueue(同步/异步)
         *
         */
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mGithubAPI = retrofit.create(GithubAPIInterface.class);
    }


    /**
     * 单例
     */
    private static final GithubService INSTANCE = new GithubService();

    public static GithubService getInstance() {
        return INSTANCE;
    }

    public Call<GithubUserDetail> requestUserDetails(@Path("username") String username) {
        return mGithubAPI.requestUserDetails(username);
    }

    public Call<List<GithubUser>> requestUsers() {
        return mGithubAPI.requestUsers(PER_PAGE);
    }

    public Observable<GithubUserDetail> rxRequestUserDetails(@Path("username") String username) {
        Log.i("tag", "rxRequestUserDetails----------");
        return mGithubAPI.rxRequestUserDetails(username);
    }

    public Observable<List<GithubUser>> rxRequestUsers() {
        return mGithubAPI.rxRequestUsers(PER_PAGE);//第一条
    }
  /* public Observable<List<GithubUser>> rxRequestUsers() {
       return mGithubAPI.rxRequestUsers();//所有数据
   }*/

}
