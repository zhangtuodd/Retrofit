package filter;

import android.util.Log;

import com.google.gson.Gson;
import java.io.IOException;
import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by SEELE on 2016/11/8.
 */

public class ErrorHandler {

    public static BodyResponse handle(Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException error = (HttpException) throwable;
            try {
                return new Gson().fromJson(error.response().errorBody().string(),
                        BodyResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("tag","throwable.printStackTrac--------------------");
            throwable.printStackTrace();
        }
        return null;
    }
}
