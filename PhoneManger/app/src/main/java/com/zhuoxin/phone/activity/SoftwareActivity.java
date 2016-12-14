package com.zhuoxin.phone.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.adapter.SoftwareAdapter;
import com.zhuoxin.phone.base.ActionBarActivity;
import com.zhuoxin.phone.entity.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class SoftwareActivity extends ActionBarActivity {
    //ListView/List<AppInfo>/Adapter
    ListView ll_software;
    ProgressBar pb_softmgr_loading;
    List<AppInfo> appInfoList = new ArrayList<AppInfo>();
    SoftwareAdapter adapter;
    String appType;
    //定义删除选项和按钮
    CheckBox cb_deleteall;
    Button btn_delete;
    //广播接收者
    BroadcastReceiver receiver;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            //处理逻辑，根据不同的msg进行处理
            int flag = message.what;
            switch (flag){
                case 1:
                    pb_softmgr_loading.setVisibility(View.GONE);
                    ll_software.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                    break;
                case 2:
                    Toast.makeText(SoftwareActivity.this,"下载完成",Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;//如果不想让其他的handle处理,传true
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_software);
        appType = getIntent().getBundleExtra("bundle").getString("appType");
        initActionBar(true, appType, false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initView();
        //创建动态receiver,必须反注册
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //重新获取APP信息并保存
                saveAppInfo();
                adapter.notifyDataSetChanged();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");//协议类型 http tel package file
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void initView() {
        appType = getIntent().getBundleExtra("bundle").getString("appType","all");
        ll_software = (ListView) findViewById(R.id.ll_software);
        pb_softmgr_loading = (ProgressBar) findViewById(R.id.pb_softmgr_loading);
        cb_deleteall = (CheckBox) findViewById(R.id.cb_deleteall);
        /*switch (appType){
            case "all":
                title = "全部软件";
                break;
            case "system":
                title = "系统软件";
                break;
            case "user":
                title = "用户软件";
                break;
        }*/
        btn_delete = (Button) findViewById(R.id.btn_delete);
        //初始化adapter并和ll关联
        adapter = new SoftwareAdapter(this, appInfoList);
        ll_software.setAdapter(adapter);
        //获取手机应用信息，并存入到appinfolist中
        saveAppInfo();
        cb_deleteall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //通过for循环，把数据的状态修改掉
                for (int i = 0; i < appInfoList.size(); i++) {
                    if (appType.equals("all")) {
                        if(!appInfoList.get(i).isSystem){
                            appInfoList.get(i).isDelete = b;
                        }
                    } else if (appType.equals("system")) {
                        appInfoList.get(i).isDelete = false;
                    } else {
                        appInfoList.get(i).isDelete = b;
                    }
                }
                //把最新的数据给adapter然后刷新
                adapter.notifyDataSetChanged();
            }
        });
        btn_delete.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //循环取出app，如果是isDelete，请使用删除的方法，删除
                for(AppInfo info:appInfoList){
                    if(info.isDelete){
                        if(!info.packageName.equals(getPackageName())){
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_DELETE);
                            intent.setData(Uri.parse("package:"+info.packageName));
                            startActivity(intent);
                        }
                        //调用删除的方法

                    }
                }
            }
        });
    }

    private void saveAppInfo() {
        //因为访问数据（文件/网络）是耗时操作，开辟子线程，避免ANR现象产生
        pb_softmgr_loading.setVisibility(View.VISIBLE);
        ll_software.setVisibility(View.INVISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                appInfoList.clear();
                //获取几乎所有的安装包
                List<PackageInfo> packageInfoList = getPackageManager().getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);
                for (PackageInfo packageInfo : packageInfoList) {
                    ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                    boolean apptype;
                    if ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0 || (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        apptype = true;
                    } else {
                        apptype = false;
                    }
                    //创建appicon
                    Drawable appicon = getPackageManager().getApplicationIcon(applicationInfo);
                    //appname
                    String appname = (String) getPackageManager().getApplicationLabel(applicationInfo);
                    String packageName = packageInfo.packageName;
                    String appversion = packageInfo.versionName;
                    //把数据存到appInfoList
                    if (appType.equals("all")) {
                        AppInfo info = new AppInfo(appicon, appname, apptype, packageName, appversion, false);
                        appInfoList.add(info);
                    } else if (appType.equals("system")) {
                        if (apptype) {
                            AppInfo info = new AppInfo(appicon, appname, apptype, packageName, appversion, false);
                            appInfoList.add(info);
                        }
                    } else {
                        if (!apptype) {
                            AppInfo info = new AppInfo(appicon, appname, apptype, packageName, appversion, false);
                            appInfoList.add(info);
                        }
                        //判断当前页面要显示的数据，把数据存到appInfoList
                    }
                }

                //runOnUIThread   View.post();
                /*cb_deleteall.post(new Runnable() {
                    @Override
                    public void run() {
                         adapter.notifyDataSetChanged();
                    }
                });*/

                //2.handler机制
                Message msg = handler.obtainMessage();
                msg.what = 1;//对msg设置标记
                //msg.setData(Bundle);
                //msg.arg1;
                handler.sendMessage(msg);
                //3.AsyncTask
            }
        }).start();

    }
}
