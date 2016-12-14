package com.zhuoxin.phone.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.adapter.PagerGuideAdapter;
import com.zhuoxin.phone.base.BaseActivity;
import com.zhuoxin.phone.db.DBManager;
import com.zhuoxin.phone.service.MusicService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseActivity {
    ViewPager vp_guide;
    ImageView iv_circle_red;
    float pixeWidth;
    TextView tv_skip;
    boolean isFromSettings = false;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        //获得Bundle中的数据
        Bundle bundle = getIntent().getBundleExtra("bundle");
        if(bundle!=null){
            isFromSettings = bundle.getBoolean("isFromSettings", false);
        }
        boolean isFirstRun = getSharedPreferences("config", Context.MODE_PRIVATE).getBoolean("isFirstRun", true);
        if(isFromSettings){
            initView();
            startService(MusicService.class);
        }else if(isFirstRun){
            initView();
            startService(MusicService.class);
        }else{
            finish();
            startActivity(HomeActivity.class);
        }
       /* if (isFirstRun) {
            initView();
            startService(MusicService.class);
        } else {
            startActivity(HomeActivity.class);
            finish();
        }*/


    }

    @Override
    public void finish() {
        Intent intent = new Intent(this, MusicService.class);
        stopService(intent);
        super.finish();
    }

    private void initView() {
        vp_guide = (ViewPager) findViewById(R.id.vp_guide);
        iv_circle_red = (ImageView) findViewById(R.id.iv_circle_red);
        tv_skip = (TextView) findViewById(R.id.tv_skip);
        pixeWidth = 40 * getDensity();
        final List<Integer> idList = new ArrayList<Integer>();
        idList.add(R.drawable.pager_guide1);
        idList.add(R.drawable.pager_guide2);
        idList.add(R.drawable.pager_guide3);
        PagerGuideAdapter pagerGuideAdapter = new PagerGuideAdapter(this, idList);
        vp_guide.setAdapter(pagerGuideAdapter);
        vp_guide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //滑动中的回调
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.v("test", "当前操作页面为：" + position + ",操作百分比为：" + positionOffset);
                //向后翻页，操作当前页面百分比为：0.0到0.999
                //向前翻页，操作当前页面百分比为：0.999到0.0
                iv_circle_red.setPadding((int) (0 + position * pixeWidth + positionOffset * pixeWidth), 0, 0, 0);
            }

            //页面被选中
            @Override
            public void onPageSelected(int position) {
                if (position == idList.size() - 1) {
                    tv_skip.setVisibility(View.VISIBLE);
                } else {
                    tv_skip.setVisibility(View.INVISIBLE);
                }
            }

            //滑动中的状态
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //保存是否第一次运行程序
                //sharedpreference
                getSharedPreferences("config", Context.MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
                copyAssets();
                if (isFromSettings) {
                    finish();
                } else {
                    startActivity(HomeActivity.class);
                    finish();
                }


            }
        });
    }

    private float getDensity() {
        //新建尺寸信息
        DisplayMetrics metrics = new DisplayMetrics();
        //获取当前手机的尺寸信息
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //获取密度并返回值
        return metrics.density;
    }

    private void copyAssets() {
        File file = this.getFilesDir();
        File targetFile = new File(file, "commonnum.db");
        if (!DBManager.isExistDB(targetFile)) {
            DBManager.copyAssetsFileToFile(this, "commonnum.db", targetFile);
        }

    }
}
