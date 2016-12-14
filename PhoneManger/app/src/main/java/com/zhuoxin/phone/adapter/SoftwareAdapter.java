package com.zhuoxin.phone.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.MyBaseAdapter;
import com.zhuoxin.phone.entity.AppInfo;

import java.util.List;

/**
 * Created by hd on 2016/11/11.
 */

public class SoftwareAdapter extends MyBaseAdapter<AppInfo> {
    public SoftwareAdapter(Context context, List<AppInfo> dataList) {
        super(context,dataList);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_software, null);
            holder = new ViewHolder();
            holder.iv_appicon = (ImageView) view.findViewById(R.id.iv_appicon);
            holder.tv_appname = (TextView) view.findViewById(R.id.tv_appname);
            holder.tv_packageName = (TextView) view.findViewById(R.id.tv_packageName);
            holder.tv_appversion = (TextView) view.findViewById(R.id.tv_appversion);
            holder.cb_appdelete = (CheckBox) view.findViewById(R.id.cb_delete);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.cb_appdelete.setTag(i);
        holder.iv_appicon.setImageDrawable(getItem(i).appicon);
        holder.tv_appname.setText(getItem(i).appname);
        holder.tv_packageName.setText(getItem(i).packageName);
        holder.tv_appversion.setText(getItem(i).appversion);
        //如果是系统的app禁用checkbox
        if(getItem(i).isSystem){
            holder.cb_appdelete.setClickable(false);
        }
        holder.cb_appdelete.setChecked(getItem(i).isDelete);
        holder.cb_appdelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int index = (int) holder.cb_appdelete.getTag();
                getItem(index).isDelete = b;
            }
        });
        return view;
    }

    private static class ViewHolder {
        ImageView iv_appicon;
        TextView tv_appname;
        TextView tv_packageName;
        TextView tv_appversion;
        CheckBox cb_appdelete;
    }
}
