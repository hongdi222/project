package com.zhuoxin.phone.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.BatteryManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.ActionBarActivity;
import com.zhuoxin.phone.biz.MemoryManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PhoneStateActivity extends ActionBarActivity {
    //依赖注入 ButterKnife
    //2.通过注解来找到控件
    @InjectView(R.id.pb_state)
    ProgressBar pb_state;
    @InjectView(R.id.v_state)
    View v_state;
    @InjectView(R.id.tv_state)
    TextView tv_state;
    @InjectView(R.id.tv_brand)
    TextView tv_brand;
    @InjectView(R.id.tv_version)
    TextView tv_version;
    @InjectView(R.id.tv_cputype)
    TextView tv_cputype;
    @InjectView(R.id.tv_cpucore)
    TextView tv_cpucore;
    @InjectView(R.id.tv_totalram)
    TextView tv_totalram;
    @InjectView(R.id.tv_freeram)
    TextView tv_freeram;
    @InjectView(R.id.tv_screen)
    TextView tv_screen;
    @InjectView(R.id.tv_camera)
    TextView tv_camera;
    @InjectView(R.id.tv_base)
    TextView tv_base;
    @InjectView(R.id.tv_root)
    TextView tv_root;
    //广播接收者,接收电量
    BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_state);
        //1.在setContentView后面初始化
        ButterKnife.inject(this);
        initActionBar(true, "手机状态", false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initView() {
        //获取电量
        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
                int percent = (int) (100.0 * level / scale);
                if (percent == 100) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        v_state.setBackgroundColor(getResources().getColor(R.color.bluePrimaryColor, null));
                    }


                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        v_state.setBackgroundColor(getResources().getColor(R.color.piechartBackgroundColor, null));
                    }
                }
                pb_state.setProgress(percent);
                tv_state.setText(percent + "%");
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, filter);
        //设备名称，系统版本
        tv_brand.setText("设备名称：" + Build.BRAND);
        tv_version.setText("系统版本：Android" + Build.VERSION.RELEASE);
        //CPU型号和核数
        tv_cputype.setText("CPU型号：" + getCPUName());
        tv_cpucore.setText("CPU核心数：" + getCPUCore());
        //获得运行内存
        tv_totalram.setText("全部内存：" + MemoryManager.totalRAMString(this));
        tv_freeram.setText("剩余内存：" + MemoryManager.availableRAMString(this));
        //获得屏幕和相机分辨率
        tv_screen.setText("屏幕分辨率："+getScreen());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv_camera.setText("相机分辨率："+getCamera());
        }
        //基带版本，是否root
        tv_base.setText("基带版本："+Build.VERSION.INCREMENTAL);
        tv_root.setText("是否root："+isRoot());
    }

    /**
     * cpu名字
     *
     * @return
     */
    private String getCPUName() {
        FileReader fr;
        BufferedReader br;
        try {
            fr = new FileReader("/proc/cpuinfo");
            br = new BufferedReader(fr);
            //读取文件中的信息找到model的名字，切割后取右侧的名字
            String msg = "";
            while ((msg = br.readLine()) != null) {
                if (msg.contains("model name")) {
                    return msg.split(":")[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * cpu核数
     *
     * @return
     */
    private int getCPUCore() {
        FileReader fr;
        BufferedReader br;
        int core = 0;
        try {
            fr = new FileReader("/proc/cpuinfo");
            br = new BufferedReader(fr);
            //读取文件中的信息找到model的名字，切割后取右侧的名字
            String msg = "";
            while ((msg = br.readLine()) != null) {
                if (msg.contains("processor")) {
                    core++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return core;
    }

    /**
     * 获取屏幕分辨率
     *
     * @return
     */

    private String getScreen() {
        WindowManager manager = getWindowManager();
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point.y + "*" + point.x;
    }

    /**
     * 获取相机分辨率
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private String getCamera() {
        String s = null;
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //如果有权限，获取相机信息
            android.hardware.Camera camera = Camera.open();
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> size = parameters.getSupportedPictureSizes();
            //一般情况下，0的位置就是最大尺寸
            s = size.get(0).height + "*" + size.get(0).width;
            camera.release();
            return s;
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
        }
        return s;
    }
    private boolean isRoot(){
        if(new File("/system/bin/su").exists()||new File("/system/xbin/su").exists()){
            return true;
        }else{
            return false;
        }
    }
}
