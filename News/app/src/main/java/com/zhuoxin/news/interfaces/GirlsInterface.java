package com.zhuoxin.news.interfaces;

import com.zhuoxin.news.entity.GirlsInfo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;

/**
 * Created by hd on 2016/12/7.
 */

public interface GirlsInterface {
    //指定要访问的网站地址
    @GET("api/data/福利/10/1")
    Call<GirlsInfo> getGirls();
}
