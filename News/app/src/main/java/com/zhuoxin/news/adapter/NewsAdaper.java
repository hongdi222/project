package com.zhuoxin.news.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zhuoxin.news.R;
import com.zhuoxin.news.base.MyBaseAdapter;
import com.zhuoxin.news.entity.NewsInfo;
import com.zhuoxin.news.utils.BitmapUtil;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by hd on 2016/11/30.
 */

public class NewsAdaper extends MyBaseAdapter<NewsInfo> {
    public NewsAdaper(Context context) {
        super(context);
    }

    public boolean isFlying = false;

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_new, null);
            holder = new ViewHolder();
            holder.tv_news_title = (TextView) view.findViewById(R.id.tv_news_title);
            holder.tv_news_type = (TextView) view.findViewById(R.id.tv_news_type);
            holder.iv_news = (ImageView) view.findViewById(R.id.iv_news);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }
        URL url = null;
        try {
            url = new URL(getItem(i).getImageURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //holder.iv_news.setTag(getItem(i).getImageURL());
        //holder.iv_news.setImageResource(R.mipmap.ic_launcher);
        //BitmapUtil.setBitmap(context, getItem(i).getImageURL(), holder.iv_news);
        Picasso.with(context)
                .load(getItem(i).getImageURL())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.shape_button_red)
                .into(holder.iv_news);
        holder.tv_news_title.setText(getItem(i).getTitle());
        holder.tv_news_type.setText(getItem(i).getType());
        return view;
    }

    static class ViewHolder {
        ImageView iv_news;
        TextView tv_news_title;
        TextView tv_news_type;
    }
}
