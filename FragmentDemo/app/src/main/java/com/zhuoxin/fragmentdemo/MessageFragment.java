package com.zhuoxin.fragmentdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hd on 2016/12/1.
 */

public class MessageFragment extends Fragment {
    //重写onCreateView

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //自定义view，通过布局填充器来填充
        View view = inflater.inflate(R.layout.fragment_message,container,false);
        return view;
    }
}
