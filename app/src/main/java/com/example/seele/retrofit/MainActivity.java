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
import rx.functions.Action;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


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
        button3 = (Button) findViewById(R.id.bt4);
        button4 = (Button) findViewById(R.id.bt5);
        button5 = (Button) findViewById(R.id.bt3);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        tv = (TextView) findViewById(R.id.tv);

//        testMap(); //测试map
//        testFlatmap();//测试flatmap

    }

    private void testFlatmap() {
        Observable.just("a", "b", "c")
                .flatMap(
                        new Func1<String, Observable<String>>() {
                            @Override
                            public Observable<String> call(String s) {
                                Log.i("tag", "map--1----" + s);
                                return Observable.just(s + "!!!");
                            }
                        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i("tag", "map--2----" + s);
                        tv.setText(s);
                    }
                });
    }

    /**
     * debounce(超时时间,超时时间单位,线程)---拦截
     * 1秒后执行后面的逻辑;just是不断发射都不会执行，只有最后一个执行
     * tag: map--1----c
     * tag: map--2----c
     * <p>
     * filter 过滤 返回true执行下一步
     * <p>
     * take(num)取前num个进行操作
     *
     * doOnNext 可用于任何一个步骤，一般在订阅前使用，做数据保存，增删改等等
     *
     * skip(num) 跳过前几个操作
     */
    private void testMap() {
        Observable.just("a", "b", "c")
//                .debounce(1000, TimeUnit.MILLISECONDS,Schedulers.newThread())
//                .filter(new Func1<String, Boolean>() {
//                    @Override
//                    public Boolean call(String s) {
//                        if (s != null && "b".equals(s)) {
//                            return true;
//                        }
//                        return false;
//                    }
//                })
//                .take(2)
//                .skip(1)
                //使用map进行转换，参数1：转换前的类型，参数2：转换后的类型
//
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String i) {
                        String name = i;
                        Log.i("tag", "map--1----" + i);
                        return name;//返回name
                    }
                })
//                .doOnNext(new Action1<String>() {
//                    @Override
//                    public void call(String s) {
//                        Log.i("tag", "doOnNext--1----" + s);
//                    }
//                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i("tag", "map--2----" + s);
                        tv.setText(s);
                    }
                });
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
                //Rxjava配合操作符使用1
                sb1.setLength(0);
                str1 = "";
                RxMethod1();
                break;
            case R.id.bt4:
                //Rxjava配合操作符使用2
                RxMothod2().toList().subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> strings) {
                        tv.setText(strings.toString());
                    }
                });
                break;
            case R.id.bt5:
                //Rxjava配合Retrofit
                RxRetrofit();
                break;

            default:
                break;
        }
    }

    /**
     * Observable.from()/just方法，它接收一个集合/数组作为输入，然后每次输出一个元素给subscriber：返回的是Observable
     * <p>
     * func<param1,param2></> 一个函数，当应用于由源Observable发出的项时，返回一个Observable
     *
     * @param1,前面传过来Observable里面的结果集，这里可能说法不太标准，为了便于理解，注意是结果集
     * @param2,返回的Observable
     */
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
                                Log.i("tag", "RxMethod1-----22---::" + s);
                                subscriber.onNext(s);
                                subscriber.onCompleted();
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<String>() {
//                    @Override
//                    public void call(String s) {
//                        tv.setText(sb1.append(s).toString());
//                        Log.i("tag", "Action1-------" + s);
//                    }
//                });
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        Log.i("tag", "Observer----onCompleted-----" );
                        tv.setText(str1);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i("tag", "Observer---next----" + s);
                        str1 = sb1.append(s).toString();

                    }
                });
    }


    private Observable<String> RxMothod2() {
        return Observable.just(
                "http://www.baidu.com/",
                "http://www.sina.com/",
                "https://www.google.com/")
                //下面两步无实际意义，可以去掉，这里做扩展api练手
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
                /**
                 * 下面三行用于测试subscriber.onCompleted方法
                 */
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
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (subscribe != null && subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
    }
}
