package com.zhuoxin.phone.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhuoxin.phone.entity.TelClassInfo;
import com.zhuoxin.phone.entity.TelNumberInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hd on 2016/11/4.
 */

public class DBManager {
    public static void copyAssetsFileToFile(Context context, String assertsPath, File targetFile) {
        InputStream is = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        BufferedOutputStream bos = null;
        try {
            is = context.getAssets().open(assertsPath);
            bis = new BufferedInputStream(is);
            os = new FileOutputStream(targetFile);
            bos = new BufferedOutputStream(os);
            byte b[] = new byte[1024];
            int count = 0;
            while ((count = bis.read(b)) != -1) {
                bos.write(b, 0, count);
            }
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                os.close();
                bis.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static boolean isExistDB(File targetFile) {
        if (targetFile.exists() && targetFile.length() != 0) {
            return true;
        } else {

            return false;
        }
    }

    public static List<TelClassInfo> readTelClassList(Context context, File targetFile) {
        List<TelClassInfo> telClassInfoList = new ArrayList<TelClassInfo>();
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(targetFile, null);
        Cursor cursor = sqLiteDatabase.rawQuery("select * from classlist", null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int idx = cursor.getInt(cursor.getColumnIndex("idx"));
            TelClassInfo info = new TelClassInfo(name, idx);
            telClassInfoList.add(info);
        }
        return telClassInfoList;
    }

    public static List<TelNumberInfo> readTelNumberList(File targetFile, int idx) {
        List<TelNumberInfo> telNumberInfoList = new ArrayList<TelNumberInfo>();
        //创建数据库
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(targetFile, null);
        //查询数据库里的table中的信息
        Cursor cursor = sqLiteDatabase.rawQuery("select * from table" + idx, null);
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String number = cursor.getString(cursor.getColumnIndex("number"));
            TelNumberInfo info = new TelNumberInfo(_id, name, number);
            telNumberInfoList.add(info);
        }
        return telNumberInfoList;
    }

    //取出垃圾数据库中的APP路径
    public static List<String> getFilePath(File targetFile) {
        //创建数据来储存路径
        List<String> filePathList = new ArrayList<String>();
        //从数据库中读取
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(targetFile, null);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM softdetail", null);
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex("filepath"));
            filePathList.add(path);
        }
        return filePathList;
    }
}
