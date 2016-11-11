package model;


import com.example.seele.retrofit.WeatherBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;


interface GithubAPIInterface {
    /**
     * @GET 指定请求方式，
     * @Query 表示请求参数，将会以key=value的方式拼接在url后面
     * 如requestUsers：完整地址https://api.github.com/users?per_page=1
     * @PATH 拼接
     * 如：requestUserDetails
     * https://api.github.com/users/mojombo
     * @QueryMap 在Query参数多的情况下，可以吧参数集成在一个map中传递
     */

    @GET("users/{username}")
    Call<GithubUserDetail> requestUserDetails(@Path("username") String username);

    @GET("users/{username}")
    Observable<GithubUserDetail> rxRequestUserDetails(@Path("username") String username);

    @GET("users")
    Call<List<GithubUser>> requestUsers(@Query("per_page") Integer perPage);

    @GET("users")
    List<GithubUser> requestUsers();

   /* //请求第一条
    @GET("users")
    Observable<List<GithubUser>> rxRequestUsers(@Query("per_page") Integer perPage);*/

    //所有数据
    @GET("users")
    Observable<List<GithubUser>> rxRequestUsers();


}
