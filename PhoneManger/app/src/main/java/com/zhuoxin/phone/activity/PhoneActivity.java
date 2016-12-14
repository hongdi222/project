package com.zhuoxin.phone.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.adapter.TelClassListAdapter;
import com.zhuoxin.phone.base.ActionBarActivity;
import com.zhuoxin.phone.base.BaseActivity;
import com.zhuoxin.phone.db.DBManager;
import com.zhuoxin.phone.entity.TelClassInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhoneActivity extends ActionBarActivity {
    ListView ll_classlist;
    TelClassListAdapter adapter;
    List<TelClassInfo> telClassInfoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        initData();
        initView();
        initActionBar(true, "电话大全", false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initData(){
        File file = this.getFilesDir();
        File targetFile = new File(file, "commonnum.db");
        telClassInfoList = DBManager.readTelClassList(this, targetFile);
        adapter = new TelClassListAdapter(this, telClassInfoList);
    }
    private void initView(){
        ll_classlist = (ListView) findViewById(R.id.ll_classlist);

        ll_classlist.setAdapter(adapter);
        ll_classlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                /*Intent intent = new Intent(PhoneActivity.this, PhoneNumberActivity.class);
                String title = telClassInfoList.get(position).name;
                int idx = telClassInfoList.get(position).idx;
                intent.putExtra("title", title);
                intent.putExtra("idx", idx);
                startActivity(intent);*/
                Bundle bundle = new Bundle();
                bundle.putInt("idx",telClassInfoList.get(position).idx);
                bundle.putString("title",telClassInfoList.get(position).name);
                startActivity(PhoneNumberActivity.class,bundle);
            }
        });
    }
}
