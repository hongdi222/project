package com.zhuoxin.news.fragment;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhuoxin.news.R;
import com.zhuoxin.news.activity.NewsActivity;
import com.zhuoxin.news.adapter.NewsAdaper;
import com.zhuoxin.news.entity.NewsInfo;
import com.zhuoxin.news.entity.NewsOfJuhe;
import com.zhuoxin.news.utils.DBUtil;
import com.zhuoxin.news.utils.HttpClientUtil;
import com.zhuoxin.news.utils.MyOpenHelper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by hd on 2016/12/1.
 */

public class NewsFragment extends Fragment {
    //建立变量，储存当前页面的新闻类型
    private String type = "top";
    public static final String TYPE_TOP = "top";
    public static final String TYPE_SHEHUI = "shehui";
    public static final String TYPE_GUOJI = "guoji";
    public static final String TYPE_KEJI = "keji";
    //当前Fragment的view
    View view = null;
    @InjectView(R.id.lv_home_news)
    ListView lv_home_news;
    @InjectView(R.id.srf_news)
    SwipeRefreshLayout srf_news;
    NewsAdaper newsAdaper;
    List<NewsInfo> newsInfoList = new ArrayList<NewsInfo>();
    //异步处理任务
    Context context;
    final int JSON_STATUS_ACCESS = 0;
    final int JSON_STATUS_FAIL = 1;
    final int JSON_STATUS_EXCEPTION = 2;
    Thread thread;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case JSON_STATUS_ACCESS:
                    newsAdaper.notifyDataSetChanged();
                    Toast.makeText(context, "刷新成功", Toast.LENGTH_SHORT).show();
                    srf_news.setRefreshing(false);
                    break;
                case JSON_STATUS_FAIL:
                    Toast.makeText(context, "请求失败,响应码为" + msg.arg1, Toast.LENGTH_SHORT).show();
                    srf_news.setRefreshing(false);
                    break;
                case JSON_STATUS_EXCEPTION:
                    Exception e = (Exception) msg.obj;
                    Toast.makeText(context, "请求异常,异常为：" + e, Toast.LENGTH_SHORT).show();
                    srf_news.setRefreshing(false);
                    break;
            }
        }
    };

    public NewsFragment() {
    }

    @SuppressLint("ValidFragment")
    public NewsFragment(String type) {
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();//从Fragment中获取到依附的Activity
        //执行初始化ListView的操作
        //先把空数据设置到listview中去

        newsAdaper = new NewsAdaper(context);
        newsAdaper.setDataList(newsInfoList);
        lv_home_news.setAdapter(newsAdaper);
        lv_home_news.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                switch (i) {
                    case SCROLL_STATE_FLING:
                        newsAdaper.isFlying = true;
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        newsAdaper.isFlying = false;
                        break;
                    case SCROLL_STATE_IDLE:
                        newsAdaper.isFlying = false;
                        newsAdaper.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
        //ListView的条目单击事件，跳转到newActivity
        lv_home_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                View iv_news = view.findViewById(R.id.iv_news);
                Intent intent = new Intent(context, NewsActivity.class);
                NewsInfo newInfo = newsAdaper.getItem(i);
                String url = newInfo.getUrl();
                String largeImageURL = newInfo.getLargeImageURL();
                String title = newInfo.getTitle();
                intent.putExtra("url", url);
                intent.putExtra("largeImageURL", largeImageURL);
                intent.putExtra("title", title);
                //startActivity(intent);
                if (Build.VERSION.SDK_INT >= 21) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(), iv_news, "news").toBundle());
                } else {
                    startActivity(intent);
                }

            }
        });
        lv_home_news.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                //创建一个PupWindow
                final PopupWindow popupWindow = new PopupWindow();
                //设置宽高和自定义iewV的布局
                final View popupView = LayoutInflater.from(context).inflate(R.layout.layout_popupwindow, null);
                popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setContentView(popupView);
                //设置点击外消失
                popupWindow.setBackgroundDrawable(new ColorDrawable(0xffffffff));
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(view, 360, -100);


                //对按钮进行单击事件的设置
                popupView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyOpenHelper myOpenHelper = new MyOpenHelper(context, "News.db", null, 1);
                        SQLiteDatabase database = myOpenHelper.getReadableDatabase();
                        if (!DBUtil.isNewExist(database, newsAdaper.getItem(i))) {
                            DBUtil.insert(database, newsAdaper.getItem(i));
                            Toast.makeText(context, "确定", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(context, "该新闻已经收藏", Toast.LENGTH_SHORT).show();
                        }


                        popupWindow.dismiss();
                    }
                });
                popupView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });
                return true;//如果return了一个false，代表了该单击事件还能在其他地方响应
            }
        });
        initListView();
        srf_news.setColorSchemeResources(R.color.colorPrimary,R.color.colorPrimaryDark);
        srf_news.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initListView();
            }
        });
    }

    private void initListView() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL targetURL = null;
                try {
                    targetURL = new URL("http://v.juhe.cn/toutiao/index?type=" + type + "&key=d728ab4e75e137c4f23aec12ed3ee6cd");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpClientUtil.getJson(targetURL, onJsonGetListener);
            }
        });
        thread.start();
    }

    private HttpClientUtil.OnJsonGetListener onJsonGetListener = new HttpClientUtil.OnJsonGetListener() {
        @Override
        public void jsonGetSuccess(String json) {
            //使用Gson解析数据
            Gson gson = new Gson();
            NewsOfJuhe newsData = gson.fromJson(json, NewsOfJuhe.class);
            List<NewsOfJuhe.Data> dataList = newsData.getResult().getData();
            newsInfoList.clear();
            for (NewsOfJuhe.Data data : dataList) {
                String url = data.getUrl();
                String title = data.getTitle();
                String newsType;
                if (type.equals(TYPE_TOP)) {
                    newsType = data.getRealtype();
                } else {
                    newsType = data.getCategory();
                }

                String largeImageURL = data.getThumbnail_pic_s03();
                String imageURL = data.getThumbnail_pic_s();
                NewsInfo info = new NewsInfo(url, imageURL, largeImageURL, title, newsType);
                newsInfoList.add(info);
            }
            Message msg = handler.obtainMessage();
            msg.what = JSON_STATUS_ACCESS;
            handler.sendMessage(msg);
        }

        @Override
        public void jsonGetFail(int responseCode) {
            Message msg = handler.obtainMessage();
            msg.what = JSON_STATUS_FAIL;
            msg.arg1 = responseCode;
            handler.sendMessage(msg);
        }


        @Override
        public void jsonGetException(Exception e) {
            Message msg = handler.obtainMessage();
            msg.what = JSON_STATUS_EXCEPTION;
            msg.obj = e;
            handler.sendMessage(msg);
        }
    };
}
