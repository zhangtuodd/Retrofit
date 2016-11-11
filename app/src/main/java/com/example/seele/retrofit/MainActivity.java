package com.example.seele.retrofit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;
import model.GithubUserDetail;
import model.WeatherApi;
import model.WeatherBean;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.example.seele.retrofit.R.id.bt5;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button1, button2, button3, button4, button5;
    private TextView tv;
    private Subscription subscribe;
    private String str1;
    private StringBuilder sb1 = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        button1 = (Button) findViewById(R.id.bt1);
        button2 = (Button) findViewById(R.id.bt2);
        button3 = (Button) findViewById(R.id.bt3);
        button4 = (Button) findViewById(R.id.bt4);
        button5 = (Button) findViewById(bt5);


        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        tv = (TextView) findViewById(R.id.tv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt1:
                //Retrofit基本使用过程
                getData();
                break;
            case R.id.bt2:
                //Retrofit的响应过滤和错误拦截
                new GithubServiceManager().fetchUserDetails();
                break;
            case R.id.bt3:
                //Rxjava配合操作符使用2
                RxMothod2().toList().subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> strings) {
                        tv.setText(strings.toString());
                    }
                });
                break;
            case R.id.bt4:
                //Rxjava配合Retrofit
                RxRetrofit();
                break;
            case bt5:
                //Rxjava配合操作符使用1
                sb1.setLength(0);
                str1 = "";
                RxMethod1();
                break;
            default:
                break;
        }
    }

    private void RxMethod1() {
        List<Integer> integers = Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9, 10);
        Observable.from(integers)
                .flatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer integer) {
                        Log.i("tag", "RxMethod1-----11---::" + integer);
                        return Observable.just(integer * integer);
                    }
                })
                .flatMap(new Func1<Integer, Observable<String>>() {
                    @Override
                    public Observable<String> call(final Integer integer) {
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                String s = integer + "  /  ";
                                subscriber.onNext(s);
                                Log.i("tag", "RxMethod1-----22---::" + s);
                                subscriber.onCompleted();
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        tv.setText(str1);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(String s) {
                        str1 = sb1.append(s).toString();

                    }
                });
    }


    private Observable<String> RxMothod2() {
        return Observable.just(
                "http://www.baidu.com/",
                "http://www.sina.com/",
                "https://www.google.com/")
                .toList()
                .flatMap(new Func1<List<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<String> s) {
                        return Observable.from(s);
                    }
                })
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        Log.i("tag", "flatMap-----22---::" + s.toString());
                        return UpperObservable(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private Observable<String> UpperObservable(final String s) {
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String ss = s.toUpperCase();
                Log.i("tag", s.toString() + "Thread Name:" + Thread.currentThread().getName());
                subscriber.onNext(ss);

                Log.i("tag", "qian-------" + subscriber.isUnsubscribed());
                subscriber.onCompleted();//主动回调，自定解除订阅
                Log.i("tag", "hou-------" + subscriber.isUnsubscribed());
            }
        });
//                .subscribeOn(Schedulers.io());//内部开启执行多线程，提高效率，顺序乱了
        return observable;
    }

    private void RxRetrofit() {
        final Observer subscriber = new Observer<List<GithubUserDetail>>() {
            @Override
            public void onCompleted() {
                Log.i("tag", "onCompleted-----rx----");
            }

            @Override
            public void onError(Throwable e) {
                Log.i("tag", "onError---rx------");
            }

            @Override
            public void onNext(List<GithubUserDetail> githubUserDetails) {
                Log.i("tag", "onNext-----rx----");
                tv.setText(githubUserDetails.toString());
            }
        };
        subscribe = new GithubServiceManager().rxFetchUserDetails().subscribe(subscriber);
    }


    private void getData() {
        String baseUrl = "http://apistore.baidu.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //用Retrofit创建出接口的代理对象
        WeatherApi weatherApi = retrofit.create(WeatherApi.class);
        Call<WeatherBean> weatherCall = weatherApi.getWeather();
        weatherCall.enqueue(new Callback<WeatherBean>() {
            @Override
            public void onResponse(Call<WeatherBean> call, Response<WeatherBean> response) {
                String weather = response.body().retData.weather;
                tv.setText(weather);
            }

            @Override
            public void onFailure(Call<WeatherBean> call, Throwable t) {

            }
        });
    }

    public void onEvent(List<GithubUserDetail> githubUserDetails) {
        tv.setText("Retrofit----->>" + githubUserDetails.toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        if (subscribe != null && subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
    }
}
