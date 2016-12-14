package com.zhuoxin.phone.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.MyBaseAdapter;
import com.zhuoxin.phone.entity.TelClassInfo;

import java.util.List;

/**
 * Created by hd on 2016/11/4.
 */

public class TelClassListAdapter extends MyBaseAdapter<TelClassInfo> {


    public TelClassListAdapter(Context context, List<TelClassInfo> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_classlist, null);
            holder = new ViewHolder();
            holder.tv_telclassname = (TextView) convertView.findViewById(R.id.tv_telclassname);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TelClassInfo info = (TelClassInfo) getItem(position);
        holder.tv_telclassname.setText(info.name);
        return convertView;
    }

    public static class ViewHolder {
        TextView tv_telclassname;
    }
}
