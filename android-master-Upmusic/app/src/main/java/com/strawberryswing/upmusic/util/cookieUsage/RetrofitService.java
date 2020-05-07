package com.strawberryswing.upmusic.util.cookieUsage;

import com.strawberryswing.upmusic.util.Constants;
import com.strawberryswing.upmusic.util.vo.PushTokenVO;
import com.strawberryswing.upmusic.util.vo.SNSLoginVO;
import com.strawberryswing.upmusic.util.vo.SummaryVO;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitService {

    @GET(Constants.PUSHTOKEN_API + "{userId}/{token}")
    Call<PushTokenVO> pushToken(
            @Path("userId") String userId,
            @Path("token") String token
    );

    @GET(Constants.SUMMARY_API + "{userId}")
    Call<SummaryVO> summary(
            @Path("userId") String userId
    );

    @FormUrlEncoded
    @POST(Constants.SNSLOGIN_API)
    Call<SNSLoginVO> snsLogin(
            @Field("email") String email,
            @Field("name") String name,
            @Field("provider") String provider,
            @Field("provider_id") String provider_id
    );
}