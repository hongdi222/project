package com.zhuoxin.fragmentdemo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onClick(View view){
        int id = view.getId();
        //根据不同的按钮，替换不同的Fragment
        //1.获取FragmentManager，用来管理当前Activity的Fragment
        FragmentManager fm = getSupportFragmentManager();
        //2.开始FragmentManager的事物，保证替换Fragment的原子性
        FragmentTransaction ft = fm.beginTransaction();
        //3.通过事物进行Fragment的替换操作
        switch (id){
            case R.id.btn_1:
                MessageFragment mf = new MessageFragment();
                ft.replace(R.id.fl_main,mf);
                break;
            case R.id.btn_2:
                PersonFragment pf = new PersonFragment();
                ft.replace(R.id.fl_main,pf);
                break;
            case R.id.btn_3:
                DynamicFragment df = new DynamicFragment();
                ft.replace(R.id.fl_main,df);
                break;
        }
        //提交事务完成替换
        ft.commit();
    }
}
