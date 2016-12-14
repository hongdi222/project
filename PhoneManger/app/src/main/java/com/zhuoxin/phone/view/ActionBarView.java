package com.zhuoxin.phone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhuoxin.phone.R;

/**
 * Created by hd on 2016/11/10.
 */

public class ActionBarView extends LinearLayout {
    ImageView iv_back;
    ImageView iv_menu;
    TextView tv_title;
    public ActionBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //需要先把Actionbar的布局引过来inflate
        inflate(context,R.layout.layout_actionbar,this);
        //找到对应的view
        this.setBackgroundResource(R.drawable.shape_shadow);
        this.setElevation(10);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_menu = (ImageView) findViewById(R.id.iv_menu);
        tv_title = (TextView) findViewById(R.id.tv_title);
    }
    public void initActionBar(boolean hasBack, String title, boolean hasMenu,OnClickListener listener){
        if(hasBack){
            iv_back.setOnClickListener(listener);
        }else {
            iv_back.setVisibility(View.INVISIBLE);
        }
        tv_title.setText(title);
        if(hasMenu){
            iv_menu.setOnClickListener(listener);
        }else{
            iv_menu.setVisibility(View.INVISIBLE);
        }
    }
}
