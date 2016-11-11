package model;

import model.WeatherBean;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by SEELE on 2016/9/23.
 */
public interface WeatherApi {
    @GET("/microservice/weather?citypinyin=beijing")
    Call<WeatherBean> getWeather();

    //post
    /**
     * Stock是一个Bean类型，形式就下面
     * Call<WeatherBean> GetData(@Body Stock  stock) ;
     *
     * 使用：
     * Stock stockBean =newStock();
     * stockBean.stock_id="000002";
     * Call call = service.GetData(stockBean);
     * call.enqueue(new Callback() {
     *     //成功失败
     * }
     *
     * */
}
