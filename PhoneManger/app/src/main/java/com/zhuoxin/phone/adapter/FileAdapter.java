package com.zhuoxin.phone.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.MyBaseAdapter;
import com.zhuoxin.phone.entity.FileInfo;
import com.zhuoxin.phone.utils.FileTypeUtil;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hd on 2016/11/24.
 */

public class FileAdapter extends MyBaseAdapter<FileInfo> {
    public FileAdapter(Context context, List<FileInfo> dataList) {
        super(context, dataList);
    }
    public boolean isScroll = false;
    //1.创建软引用的键值对
    //HashMap<String,SoftReference<Bitmap>> bitmapSoftMap = new HashMap<String,SoftReference<Bitmap>>();
    //1.创建LruCache，并指定临界清理值
    int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
    private final int LRUSIZE = maxMemory/16;
    LruCache<String, Bitmap> bitmapLruCache = new LruCache<String, Bitmap>(LRUSIZE) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getHeight() * value.getRowBytes();
        }
    };

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_file, null);
            holder = new ViewHolder();
            holder.cb_file = (CheckBox) view.findViewById(R.id.cb_file);
            holder.iv_fileicon = (ImageView) view.findViewById(R.id.iv_fileicon);
            holder.tv_filename = (TextView) view.findViewById(R.id.tv_filename);
            holder.tv_filetype = (TextView) view.findViewById(R.id.tv_filetype);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //设置布局中的数据
        holder.cb_file.setTag(i);
        holder.cb_file.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //取出当前cb的位置
                int index = (int) holder.cb_file.getTag();
                //修改对应位置的数据
                getItem(index).setSelect(b);
            }
        });
        holder.cb_file.setChecked(getItem(i).isSelect());
        //图片,传统方式R.drawable.icon_image不可拆分和判断，建议用位图来判断

        Bitmap bitmap = null;
        /*SoftReference<Bitmap> bitmapSoftReference = bitmapSoftMap.get(getItem(i).getFile().getName());
        //如果软引用中没任何数据，==null
        if(bitmapSoftReference == null){
            //系统中重新加载图片
            bitmap = getBitmap(getItem(i));
            //把加载后的图片，设置到软引用中
            SoftReference<Bitmap> soft = new SoftReference<Bitmap>(bitmap);
            //用键值对关联软引用
            bitmapSoftMap.put(getItem(i).getFile().getName(),soft);
        }else{
            //如果引用不为空，则直接取出照片，不用从系统加载了
            bitmap = bitmapSoftReference.get();
        }*/
        if(!isScroll){
            bitmap = bitmapLruCache.get(getItem(i).getFile().getName());
            if(bitmap == null){
                //系统重新加载图片
                bitmap = getBitmap(getItem(i));
                //放入lru中
                bitmapLruCache.put(getItem(i).getFile().getName(),bitmap);
            }
            holder.iv_fileicon.setImageBitmap(bitmap);//添加获取位图的方法，用来根据不同的文件获取不同的位图
        }else{
            holder.iv_fileicon.setImageResource(R.drawable.item_arrow_right);
        }

        holder.tv_filename.setText(getItem(i).getFile().getName());
        holder.tv_filetype.setText(getItem(i).getFileType());
        return view;
    }

    static class ViewHolder {
        CheckBox cb_file;
        ImageView iv_fileicon;
        TextView tv_filename;
        TextView tv_filetype;
    }

    /**
     * @param fileInfo 传递进来文件类型，根据文件类型来取值
     * @return 解析后的位图资源
     */
    private Bitmap getBitmap(FileInfo fileInfo) {
        //定义位图
        Bitmap bitmap = null;
        //判断类型，image和非image
        //在decode图片资源之前，先获取和设置图片的缩放率
        //1.new 一个options
        BitmapFactory.Options options = new BitmapFactory.Options();


        if (fileInfo.getFileType().equals(FileTypeUtil.TYPE_IMAGE)) {
            //2.把图片参数取出来，放到options中
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fileInfo.getFile().getAbsolutePath(), options);
            //3.计算缩放率并设置
            int scaleUnit = 60;
            int scale = (options.outHeight > options.outWidth ? options.outHeight : options.outWidth) / scaleUnit;
            options.inSampleSize = scale;
            //4.根据设置好的options进行图片加载
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(fileInfo.getFile().getAbsolutePath(), options);
            return bitmap;
        } else {
            //其他情况，取drawable目录就可以了。
            //通过getIdentifier来把fileType转换为对应的R.id的形式
            int icon = context.getResources().getIdentifier(fileInfo.getIconName(), "drawable", context.getPackageName());
            //如果icon<=0说明数据没取出来，给个默认的值
            if (icon <= 0) {
                icon = R.drawable.item_arrow_right;
            }
            //把R.id的资源转换成bitmap
            //2.把图片参数取出来，放到options中
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(), icon, options);
            //3.计算缩放率并设置
            int scaleUnit = 60;
            int scale = (options.outHeight > options.outWidth ? options.outHeight : options.outWidth) / scaleUnit;
            options.inSampleSize = scale;
            //4.根据设置好的options进行图片加载
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeResource(context.getResources(), icon, options);
            return bitmap;
        }
    }
}
