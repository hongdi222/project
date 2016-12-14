package com.zhuoxin.phone.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.format.Formatter;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.ActionBarActivity;
import com.zhuoxin.phone.view.PiecharView;

import java.io.File;

public class SoftManagerActivity extends ActionBarActivity implements View.OnClickListener{
    PiecharView pv_softmgr;
    ProgressBar pb_softmgr;
    TextView tv_softtmgr;
    RelativeLayout rl_allSoftware;
    RelativeLayout rl_systemSoftware;
    RelativeLayout rl_userSoftware;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soft_manager);
        initView();
        requestPermissionAndShowMemory();

        initActionBar(true, "软件信息", false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initView(){

        pv_softmgr = (PiecharView) findViewById(R.id.pv_softmgr);
        pb_softmgr = (ProgressBar) findViewById(R.id.pb_softmgr);
        tv_softtmgr = (TextView) findViewById(R.id.tv_softmgr);
        rl_allSoftware = (RelativeLayout) findViewById(R.id.rl_allSoftware);
        rl_systemSoftware = (RelativeLayout) findViewById(R.id.rl_systemSoftware);
        rl_userSoftware = (RelativeLayout) findViewById(R.id.rl_userSoftware);
        //设置单击事件
        rl_allSoftware.setOnClickListener(this);
        rl_systemSoftware.setOnClickListener(this);
        rl_userSoftware.setOnClickListener(this);

    }
    private void requestPermissionAndShowMemory(){
        //动态申请权限
        if (Build.VERSION.SDK_INT >= 23) {
            int premissionState = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (premissionState == PackageManager.PERMISSION_GRANTED) {
                //获取完数据后显示信息
                showMemory();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        } else {
            showMemory();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //获取完数据后显示信息
            showMemory();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage("请跳转到设置界面手动分配权限").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //没获取数据手动跳转一下
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            }).setNegativeButton("cancel", null).create();
            alertDialog.show();
        }
    }

    private void showMemory() {
        //获取手机中的SD卡
        File file = Environment.getExternalStorageDirectory();
        //获取总大小和已用大小
        long total = file.getTotalSpace();
        long used = total - file.getFreeSpace();
        //把数据展示出来
        int angle = (int) (360 * used / total);
        pv_softmgr.showPiechart(angle);
        pb_softmgr.setProgress((int) (100 * used / total));
        String totalStr = android.text.format.Formatter.formatFileSize(this, total);
        String freeStr = android.text.format.Formatter.formatFileSize(this, file.getFreeSpace());
        tv_softtmgr.setText("可用空间:" + freeStr + "/" + totalStr);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Bundle bundle = new Bundle();
        switch (id){
            case R.id.rl_allSoftware:
                bundle.putString("appType","all");
                startActivity(SoftwareActivity.class,bundle);
                break;
            case R.id.rl_systemSoftware:
                bundle.putString("appType","system");
                startActivity(SoftwareActivity.class,bundle);
                break;
            case R.id.rl_userSoftware:
                bundle.putString("appType","user");
                startActivity(SoftwareActivity.class,bundle);
                break;
        }
    }
}
