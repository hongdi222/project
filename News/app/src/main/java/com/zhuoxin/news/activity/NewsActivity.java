package com.zhuoxin.news.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhuoxin.news.R;
import com.zhuoxin.news.utils.BitmapUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class NewsActivity extends AppCompatActivity {
    @InjectView(R.id.wv_news)
    WebView wv_news;
    @InjectView(R.id.iv_news)
    ImageView iv_news;
    String url;
    String largeImageURL;
    String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.inject(this);
        ShareSDK.initSDK(this,"157fc72150700");
        initSystemUI();
        //设置WebVewi
        //wv_news.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wv_news.setWebViewClient(new WebViewClient());
        wv_news.setWebChromeClient(new WebChromeClient());
        wv_news.getSettings().setJavaScriptEnabled(true);
        wv_news.loadUrl(url);

    }
    private void initSystemUI(){
        //取出Intent里的数据
        url = getIntent().getStringExtra("url");
        largeImageURL = getIntent().getStringExtra("largeImageURL");
        title = getIntent().getStringExtra("title");
        //设置Actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        //设置悬浮按钮
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShare();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //加载图片
        iv_news.setTag(largeImageURL);
        BitmapUtil.setBitmap(this,largeImageURL,iv_news);
    }
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(title);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        //oks.setUrl("http://sharesdk.cn");
        oks.setImageUrl(largeImageURL);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        //oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        //oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }
}
