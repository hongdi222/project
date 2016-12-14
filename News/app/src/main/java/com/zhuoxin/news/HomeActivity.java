package com.zhuoxin.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuoxin.news.activity.LoginActivity;
import com.zhuoxin.news.fragment.ImageFragment;
import com.zhuoxin.news.fragment.NewsCollectFragment;
import com.zhuoxin.news.fragment.NewsPagerFragment;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //创建成员变量，用来保存当前的Fragment
    Fragment currentFragment;
    //找到布局
    ImageView iv_user_photo;
    TextView tv_user_name;
    TextView tv_user_other;
    Context context;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initSystemUI();
        context = this;
        showNewsFragment();
        ShareSDK.initSDK(this, "157fc72150700");

    }

    private void initSystemUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        iv_user_photo = (ImageView) headerView.findViewById(R.id.iv_user_photo);
        tv_user_name = (TextView) headerView.findViewById(R.id.tv_user_name);
        tv_user_other = (TextView) headerView.findViewById(R.id.tv_user_other);
        //单击事件，第三方登录
        iv_user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
                //Toast.makeText(HomeActivity.this, "头像不错", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void login(){
        //获取QQ平台消息
        final Platform qq = ShareSDK.getPlatform(QQ.NAME);
        //对平台的授权结果进行接口回调的监听
        qq.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                com.orhanobut.logger.Logger.d( platform.getDb().exportData());
                final String user_photo_url = platform.getDb().getUserIcon();
                final String user_name = platform.getDb().getUserName();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Picasso.with(context).load(user_photo_url).into(iv_user_photo);
                        ImageOptions imageOptions = new ImageOptions.Builder()
                                .setCircular(true)
                                .build();
                        x.image().bind(iv_user_photo,user_photo_url,imageOptions);
                        tv_user_name.setText(user_name);
                        qq.showUser(null);
                    }
                });



            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        qq.SSOSetting(false);
        qq.authorize();//单独授权
        qq.showUser(null);
        com.orhanobut.logger.Logger.d("获取用户信息");
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("每日新闻");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("这是一款基于Android5.0的新闻平台");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        //oks.setUrl("http://sharesdk.cn");
        oks.setImageUrl("http://a.hiphotos.baidu.com/image/pic/item/9825bc315c6034a86665d0f9cc134954082376c6.jpg");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        //oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        //oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final String TAG = "HomeActivity";

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_news) {
            showNewsFragment();
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

            showImageFragment();
        } else if (id == R.id.nav_news_collect) {
            showNewsCollcetFragment();
        } else if (id == R.id.nav_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            showShare();
        } else if (id == R.id.nav_send) {
            Toast.makeText(this, "发送消息成功", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showNewsFragment() {
        if (currentFragment instanceof NewsPagerFragment) {
            Toast.makeText(this, "数据未改变", Toast.LENGTH_SHORT).show();
        } else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            NewsPagerFragment newsPagerFragment = new NewsPagerFragment();
            ft.replace(R.id.fl_home, newsPagerFragment);
            ft.commit();
            currentFragment = newsPagerFragment;
        }

    }

    private void showImageFragment() {
        if (currentFragment instanceof ImageFragment) {
            Toast.makeText(this, "数据未改变", Toast.LENGTH_SHORT).show();
        } else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ImageFragment imageFragment = new ImageFragment();
            ft.replace(R.id.fl_home, imageFragment);
            ft.commit();
            currentFragment = imageFragment;
        }
    }

    private void showNewsCollcetFragment() {
        if (currentFragment instanceof NewsCollectFragment) {
            Toast.makeText(this, "数据未改变", Toast.LENGTH_SHORT).show();
        } else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            NewsCollectFragment newsCollectFragment = new NewsCollectFragment();
            ft.replace(R.id.fl_home, newsCollectFragment);
            ft.commit();
            currentFragment = newsCollectFragment;
        }

    }
}
