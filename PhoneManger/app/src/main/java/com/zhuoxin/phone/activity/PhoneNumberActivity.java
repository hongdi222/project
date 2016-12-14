package com.zhuoxin.phone.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityManagerCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.adapter.TelNumberAdapter;
import com.zhuoxin.phone.base.ActionBarActivity;
import com.zhuoxin.phone.base.BaseActivity;
import com.zhuoxin.phone.db.DBManager;
import com.zhuoxin.phone.entity.TelNumberInfo;

import java.io.File;
import java.util.List;
import java.util.jar.Manifest;

public class PhoneNumberActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    List<TelNumberInfo> dataList;
    ListView ll_numberList;
    final int PERMISSION_REQUEST_CODE = 0;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        //取出标题数据
        initView();
        initData();

    }

    private void initData() {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        String title = bundle.getString("title");

        int idx = bundle.getInt("idx", 1);
        //从数据库取出数据
        File targetFile = new File(getFilesDir(), "commonnum.db");
        dataList = DBManager.readTelNumberList(targetFile, idx);
        //对listView设置Adapter就行了
        TelNumberAdapter adapter = new TelNumberAdapter(this, dataList);
        ll_numberList.setAdapter(adapter);
        ll_numberList.setOnItemClickListener(this);
        initActionBar(true, title, false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void initView() {
        ll_numberList = (ListView) findViewById(R.id.ll_numberlist);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //需要判断当前的版本号是否为6.0（23）版本
        number = dataList.get(i).number;
        if (Build.VERSION.SDK_INT >= 23) {
            //如果用到了敏感权限需要申请运行时running的权限
            //先检查自己需要的权限是否打开了
            int hasGot = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE);

            if (hasGot == PackageManager.PERMISSION_GRANTED) {
                //创建隐式Intent，启动拨号程序
                call(number);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            call(number);
        }
        //单机对应的条目时，获得对应的电话号码


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    call(number);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("选择权限是否打开").setMessage("你可以跳转到页面设置界面").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //确认按钮的单击事件，跳转到系统app界面
                            startActivity(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create();
                    alertDialog.show();
                    Toast.makeText(this, "权限申请失败，请您重新获取权限", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void call(final String number) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage("你是否要拨打电话").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //确认按钮的单击事件，跳转到拨号界面
                startActivity(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create();
        alertDialog.show();

    }
}
