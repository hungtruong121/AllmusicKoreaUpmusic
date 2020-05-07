package com.strawberryswing.upmusic.activity.ws;

import com.strawberryswing.upmusic.activity.BaseActivity;
import com.strawberryswing.upmusic.activity.MainActivity;
import com.strawberryswing.upmusic.util.cookieUsage.AddCookiesInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Naik on 24.02.17.
 */
public class RestClient {

    public static final String ANDROID_EMULATOR_LOCALHOST = "10.0.2.2";
    public static final String SERVER_PORT = "8080";

    private static RestClient instance;
    private static final Object lock = new Object();

    public static RestClient getInstance() {
        RestClient instance = RestClient.instance;
        if (instance == null) {
            synchronized (lock) {
                instance = RestClient.instance;
                if (instance == null) {
                    RestClient.instance = instance = new RestClient();
                }
            }
        }
        return instance;
    }

    private final ExampleRepository mRepository;

    private RestClient() {

        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        client = builder.build();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        builder.addInterceptor(loggingInterceptor);

        Retrofit retrofit =
                new Retrofit.Builder().baseUrl("http://" + ANDROID_EMULATOR_LOCALHOST + ":" + SERVER_PORT + "/")
//        new Retrofit.Builder().baseUrl(WSS_URL + "")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client) // VERY VERY IMPORTANT
                .build();
        mRepository = retrofit.create(ExampleRepository.class);
    }

    private RestClient(AddCookiesInterceptor addCookiesInterceptor) {


        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(addCookiesInterceptor); // VERY VERY IMPORTANT
        client = builder.build();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        builder.addInterceptor(loggingInterceptor);


        // /upm-player-websocket
        // /topic/player
        // /topic/player

        Retrofit retrofit =
//                new Retrofit.Builder().baseUrl("http://" + ANDROID_EMULATOR_LOCALHOST + ":" + SERVER_PORT + "/")
        new Retrofit.Builder().baseUrl("wss://"+ "upmusic.azurewebsites.net" + "/upm-player-websocket")
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .client(client) // VERY VERY IMPORTANT
                        .build();
        mRepository = retrofit.create(ExampleRepository.class);

    }


    public ExampleRepository getExampleRepository() {
        return mRepository;
    }
}
