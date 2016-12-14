package com.zhuoxin.phone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.zhuoxin.phone.adapter.SoftwareAdapter;
import com.zhuoxin.phone.service.MusicService;

public class RebootCompleteReceiver extends BroadcastReceiver {


    //接收到广播后执行的操作
    @Override
    public void onReceive(Context context, Intent intent) {
        //只有选中了开机启动项才会执行以下操作。
        boolean start = context.getSharedPreferences("config", context.MODE_PRIVATE).getBoolean("startWhenBootComplete", false);
        if (start) {
            Toast.makeText(context, "重启成功", Toast.LENGTH_SHORT).show();
            Intent musicIntent = new Intent();
            musicIntent.setClass(context, MusicService.class);
            context.startService(musicIntent);
        }
    }
}
