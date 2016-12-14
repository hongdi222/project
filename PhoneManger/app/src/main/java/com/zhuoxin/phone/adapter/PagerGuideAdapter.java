package com.zhuoxin.phone.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.view.menu.MenuView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zhuoxin.phone.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hd on 2016/11/9.
 */

public class PagerGuideAdapter extends PagerAdapter {
    List<Integer> idList = new ArrayList<Integer>();
    LayoutInflater inflater;

    public PagerGuideAdapter(Context context, List<Integer> idList) {
        inflater = LayoutInflater.from(context);
        this.idList.addAll(idList);
    }

    //返回总数
    @Override
    public int getCount() {
        return idList.size();
    }

    //创建一个view并把它的key值返回
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //创建一个view
        View view = inflater.inflate(R.layout.layout_pager_guide, null);
        ImageView iv_guide = (ImageView) view.findViewById(R.id.iv_guide);
        iv_guide.setImageResource(idList.get(position));
        //添加容器到container中，负责对view的判断和显示
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
       View view = (View) object;
        container.removeView(view);
    }


}
