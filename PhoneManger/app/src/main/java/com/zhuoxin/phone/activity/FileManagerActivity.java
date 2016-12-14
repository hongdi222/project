package com.zhuoxin.phone.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.ActionBarActivity;
import com.zhuoxin.phone.biz.FileManager;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

public class FileManagerActivity extends ActionBarActivity {
    @InjectView(R.id.tv_file_manager)
    TextView tv_file_manager;
    @InjectViews({R.id.pb_anyFile, R.id.pb_txtFile, R.id.pb_videoFile, R.id.pb_audioFile, R.id.pb_imageFile, R.id.pb_zipFile, R.id.pb_apkFile})
    List<ProgressBar> pbList;
    @InjectViews({R.id.iv_anyFile, R.id.iv_txtFile, R.id.iv_videoFile, R.id.iv_audioFile, R.id.iv_imageFile, R.id.iv_zipFile, R.id.iv_apkFile})
    List<ImageView> ivList;
    FileManager fileManager;
    Thread fileManagerThread;
    String fileType[] = {"所有文件", "文档文件", "视频文件", "音频文件", "图像文件", "压缩文件", "apk文件"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        ButterKnife.inject(this);
        initActionBar(true, "文件管理", false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //获取FileManager并设置侦听事件

        fileManager = FileManager.getFileManager();
        fileManager.setSearchListener(new FileManager.SearchListener() {
            @Override
            public void searching(long size) {
                final long l = size;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_file_manager.setText("已经找到：" + Formatter.formatFileSize(FileManagerActivity.this, l));
                    }
                });
            }

            @Override
            public void end(final boolean endFlag) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (endFlag) {
                            for (int i = 0; i < pbList.size(); i++) {
                                final int temp = i;
                                pbList.get(i).setVisibility(View.GONE);
                                ivList.get(i).setVisibility(View.VISIBLE);
                                ivList.get(i).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //设置每一个按钮的单击事件
                                        Bundle bundle = new Bundle();
                                        bundle.putString("fileType", fileType[temp]);
                                        startActivity(FileActivity.class, bundle);
                                    }
                                });
                            }
                            Toast.makeText(FileManagerActivity.this, "查找完毕", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
        requestPermissionAndShowMemory();
    }

    private void requestPermissionAndShowMemory() {
        int premissionState = 0;
        //动态申请权限
        if (Build.VERSION.SDK_INT >= 23) {
            premissionState = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (premissionState == PackageManager.PERMISSION_GRANTED) {
                
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        } else {
            asyncSearchSDCardFiles();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //获取完数据后显示信息
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

    @Override
    protected void onResume() {
        super.onResume();
        tv_file_manager.setText("已经找到：" + Formatter.formatFileSize(FileManagerActivity.this, FileManager.getFileManager().getAnyFileSize()));
        asyncSearchSDCardFiles();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //中断线程
        fileManagerThread.interrupt();
        //修改查询状态
        fileManager.isSearching = false;
    }

    private void asyncSearchSDCardFiles() {
        fileManagerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //查询SD卡中的文件
                fileManager.searchSDCardFile();
            }
        });
        fileManagerThread.start();
    }
}
