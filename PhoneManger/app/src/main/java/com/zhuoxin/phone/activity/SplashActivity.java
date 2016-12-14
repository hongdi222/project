package com.zhuoxin.phone.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.BaseActivity;
import com.zhuoxin.phone.db.DBManager;

import java.io.File;

public class SplashActivity extends BaseActivity {

    ImageView iv_logo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        /*iv_logo = (ImageView) findViewById(R.id.iv_logo);
        iv_logo.setBackgroundResource(R.drawable.anim_frame);
        AnimationDrawable ad = (AnimationDrawable) iv_logo.getBackground();
        ad.start();*/

        startAnimation();
        iv_logo = (ImageView) findViewById(R.id.iv_logo);

        copyAssets();
    }
    private void startAnimation(){
        Animation anim = (Animation) AnimationUtils.loadAnimation(this, R.anim.anim_set);
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                startActivity(PhoneActivity.class);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        anim.setAnimationListener(animationListener);
        iv_logo.startAnimation(anim);
    }
    private void copyAssets() {
        File file = this.getFilesDir();
        File targetFile = new File(file, "commonnum.db");
        if (!DBManager.isExistDB(targetFile)) {
            DBManager.copyAssetsFileToFile(this, "commonnum.db", targetFile);
        }

    }
}
