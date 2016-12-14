package com.zhuoxin.phone.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hd on 2016/11/7.
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter {
    //1、创建List和LayoutInflater
    List<T> dataList = new ArrayList<T>();
    public LayoutInflater inflater;
    public Context context;

    //2、构造函数初始化数据
    public MyBaseAdapter(Context context,List<T> dataList) {
        this.inflater = LayoutInflater.from(context);
        this.dataList = dataList;
        this.context = context;
    }

    //3、添加数据
    public void setDataList(List<T> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
    }
    public List<T> getDataList(){
        return dataList;
    }
    //4、重写四大方法

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public T getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public abstract View getView(int i, View view, ViewGroup viewGroup);
}
