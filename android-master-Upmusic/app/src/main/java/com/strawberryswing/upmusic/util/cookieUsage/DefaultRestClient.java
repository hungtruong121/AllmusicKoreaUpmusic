package com.strawberryswing.upmusic.util.cookieUsage;

import com.strawberryswing.upmusic.activity.GlobalApplication;
import com.strawberryswing.upmusic.activity.UpmuicAPI;
import com.strawberryswing.upmusic.util.Constants;
import com.strawberryswing.upmusic.util.cookieUsage.AddCookiesInterceptor;
import com.strawberryswing.upmusic.util.cookieUsage.ReceivedCookiesInterceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DefaultRestClient<T> {

    private T service;

    public ReceivedCookiesInterceptor receivedCookiesInterceptor;
    public AddCookiesInterceptor addCookiesInterceptor;

    public T getClient(Class<? extends T> type) {
        if (service == null) {

            receivedCookiesInterceptor = new ReceivedCookiesInterceptor(GlobalApplication.getInstance());
            addCookiesInterceptor = new AddCookiesInterceptor(GlobalApplication.getInstance());

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
//                            .header("Accept-Charset", "UTF-8")
//                            .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            }).addInterceptor(interceptor)
                    .addInterceptor(receivedCookiesInterceptor)
                    .addInterceptor(addCookiesInterceptor)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(UpmuicAPI.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(type);
        }

        return service;
    }
}
