package com.zhuoxin.news;

import com.google.gson.Gson;
import com.zhuoxin.news.entity.NewsOfJuhe;
import com.zhuoxin.news.interfaces.GirlsInterface;
import com.zhuoxin.news.utils.HttpClientUtil;
import com.zhuoxin.news.utils.MD5Util;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getJson() {
        //2.创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gank.io/")
                .build();
        //3.获取接口对象
        GirlsInterface girlsInterface = retrofit.create(GirlsInterface.class);
        //4.加入队列进行数据解析
        Call<ResponseBody> call = girlsInterface.getGirls();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.print(response.body().toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.print(t.getMessage());
            }
        });

    }
    @Test
    public void testMD5(){
        System.out.print(MD5Util.getMD5("1"));
    }
}