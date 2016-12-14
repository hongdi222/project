package com.zhuoxin.phone.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.adapter.FileAdapter;
import com.zhuoxin.phone.base.ActionBarActivity;
import com.zhuoxin.phone.biz.FileManager;
import com.zhuoxin.phone.entity.FileInfo;
import com.zhuoxin.phone.utils.FileTypeUtil;

import java.util.ArrayList;
import java.util.List;

public class FileActivity extends ActionBarActivity {
    String fileType;
    ListView lv_file;
    FileAdapter fileAdapter;
    List<FileInfo> fileInfoList;
    Button btn_file;
    FileManager fm = FileManager.getFileManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        fileType = getIntent().getBundleExtra("bundle").getString("fileType");
        initActionBar(true, fileType, false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lv_file = (ListView) findViewById(R.id.lv_file);
        btn_file = (Button) findViewById(R.id.btn_file);
        getFileList();
        fileAdapter = new FileAdapter(this,fileInfoList);
        lv_file.setAdapter(fileAdapter);
        lv_file.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //获取到文件类型，根据文件类型的MIME进行跳转，跳转操作
                String mime = FileTypeUtil.getMIMEType(fileInfoList.get(i).getFile());
                //通过隐式跳转来打开对应的文件
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(fileInfoList.get(i).getFile()),mime);
                startActivity(intent);
            }
        });
        lv_file.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                switch (i){
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        fileAdapter.isScroll = false;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        fileAdapter.isScroll = true;
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
        btn_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除文件
                List<FileInfo> tempList = new ArrayList<FileInfo>();
                tempList.addAll(fileAdapter.getDataList());
                for(FileInfo f:tempList){
                    if(f.isSelect()){
                        //从所有文件列表中删除
                        fm.getAnyFileList().remove(f);
                        //从对应的的文件列表中删除
                        switch (f.getFileType()){
                            case FileTypeUtil.TYPE_TXT:
                                fm.getTxtFileList().remove(f);
                                break;
                            case FileTypeUtil.TYPE_VIDEO:
                                fm.getVideoFileList().remove(f);
                                break;
                            case FileTypeUtil.TYPE_AUDIO:
                                fm.getAudioFileList().remove(f);
                                break;
                            case FileTypeUtil.TYPE_IMAGE:
                                fm.getImageFileList().remove(f);
                                break;
                            case FileTypeUtil.TYPE_ZIP:
                                fm.getZipFileList().remove(f);
                                break;
                            case FileTypeUtil.TYPE_APK:
                                fm.getApkFileList().remove(f);
                                break;
                        }
                        //改变大小
                        long totalSize = fm.getAnyFileSize();
                        fm.setAnyFileSize(totalSize-=f.getFile().length());
                        f.getFile().delete();
                    }
                }
                fileAdapter.notifyDataSetChanged();
                Toast.makeText(FileActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void getFileList() {
        //根据传递过来的fileType，获取文件的数据
        switch (fileType){
            case "所有文件":
                fileInfoList = FileManager.getFileManager().getAnyFileList();
                break;
            case "文档文件":
                fileInfoList = FileManager.getFileManager().getTxtFileList();
                break;
            case "视频文件":
                fileInfoList = FileManager.getFileManager().getVideoFileList();
                break;
            case "音频文件":
                fileInfoList = FileManager.getFileManager().getAudioFileList();
                break;
            case "图像文件":
                fileInfoList = FileManager.getFileManager().getImageFileList();
                break;
            case "压缩文件":
                fileInfoList = FileManager.getFileManager().getZipFileList();
                break;
            case "apk文件":
                fileInfoList = FileManager.getFileManager().getApkFileList();
                break;
        }
    }
}
