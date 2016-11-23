package com.example.seele.retrofit;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import model.GithubService;
import model.GithubUser;
import model.GithubUserDetail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GithubServiceManager {

    private GithubService mService;

    Call<List<GithubUser>> call;

    public GithubServiceManager() {
        mService = GithubService.getInstance();
    }

    public void fetchUserDetails() {
        call = mService.requestUsers();
        call.enqueue(new Callback<List<GithubUser>>() {
            @Override
            public void onResponse(Call<List<GithubUser>> call, Response<List<GithubUser>> response) {
                final List<GithubUserDetail> githubUserDetails = new ArrayList<>();
                if (response.isSuccessful()) {
                    final List<GithubUser> githubUsers = response.body();
                    for (GithubUser user : githubUsers) {
                        mService.requestUserDetails(user.mLogin).enqueue(new Callback<GithubUserDetail>() {
                            @Override
                            public void onResponse(Call<GithubUserDetail> call, Response<GithubUserDetail> response) {
                                GithubUserDetail githubUserDetail = response.body();
                                githubUserDetails.add(githubUserDetail);
                                if (githubUserDetails.size() == 1) {
                                    EventBus.getDefault().post(githubUserDetails);
                                }
                            }

                            @Override
                            public void onFailure(Call<GithubUserDetail> call, Throwable t) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<List<GithubUser>> call, Throwable t) {
//                 ErrorHandler.handle(t);
            }
        });
    }

    private void CancleCall() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }

    /**
     * Observable.from()/just方法，它接收一个集合作为输入，然后每次输出一个元素给subscriber：返回的是Observable
     */
    public Observable<List<GithubUserDetail>> rxFetchUserDetails() {
        return mService.rxRequestUsers()
                .concatMap(new Func1<List<GithubUser>, Observable<? extends GithubUser>>() {
                    @Override
                    public Observable<? extends GithubUser> call(List<GithubUser> iterable) {
                        Log.i("tag", "---------------concatMap1");
                        return Observable.from(iterable);
                    }
                })
                .concatMap(new Func1<GithubUser, Observable<? extends GithubUserDetail>>() {
                               @Override
                               public Observable<? extends GithubUserDetail> call(GithubUser githubUser) {
                                   Log.i("tag", "---------------concatMap2---+"+githubUser.mLogin);
                                   return mService.rxRequestUserDetails(githubUser.mLogin);
                               }
                           }
                )
                .toList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

}

