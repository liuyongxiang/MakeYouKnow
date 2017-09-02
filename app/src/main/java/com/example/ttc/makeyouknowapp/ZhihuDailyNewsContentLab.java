package com.example.ttc.makeyouknowapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ttc.makeyouknowapp.db.CacheNewsBaseHelper;
import com.example.ttc.makeyouknowapp.db.CacheNewsCursorWrapper;
import com.example.ttc.makeyouknowapp.db.CollectNewsBaseHelper;
import com.example.ttc.makeyouknowapp.db.CollectNewsCursorWrapper;
import com.example.ttc.makeyouknowapp.db.ZhihuNewsDbSchema;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttc on 2017/3/14.
 */

public class ZhihuDailyNewsContentLab {

    private static  ZhihuDailyNewsContentLab mZhihuDailyNewsContentLab;

    private Context mContext;
    private SQLiteDatabase mDatebase;
    private SQLiteDatabase mCollectDatabase;


    public static ZhihuDailyNewsContentLab get(Context context){
        if(mZhihuDailyNewsContentLab == null){
            mZhihuDailyNewsContentLab = new ZhihuDailyNewsContentLab(context);
        }
        return mZhihuDailyNewsContentLab;
    }

    private ZhihuDailyNewsContentLab(Context context){
        mContext = context.getApplicationContext();
        mDatebase = new CacheNewsBaseHelper(mContext)
                          .getWritableDatabase();
        mCollectDatabase = new CollectNewsBaseHelper(mContext)
                .getWritableDatabase();

    }


    //如要获取缓存数据库中的某个对象，调用此方法，传入id
    public ZhihuDailyNewsContent getCacheNew(int id){
        CacheNewsCursorWrapper cursor = queryNews(
                ZhihuNewsDbSchema.CacheNewsTable.Cols.ID+ " = ?",
                new String[]{String.valueOf(id)}
        );
        try{
            if(cursor.getCount()==0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCacheNews();
        }finally{
            cursor.close();
        }
    }

    //如要获取收藏数据库中的某个对象，调用此方法，传入id
    public ZhihuDailyNewsContent getCollectNew(int id){
        CollectNewsCursorWrapper cursor = queryCollectNews(
                ZhihuNewsDbSchema.CollectNewsTable.Cols.ID+"=?",
                new String[]{String.valueOf(id)}
        );
        try{
            if(cursor.getCount()==0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCollectNews();
        }finally{
            cursor.close();
        }
    }

    public void updateCacheNews(ZhihuDailyNewsContent mcontent){
        String mid = String.valueOf(mcontent.getId());
        ContentValues values = getContentValues(mcontent);
        mDatebase.update(ZhihuNewsDbSchema.CacheNewsTable.NAME,values,
                ZhihuNewsDbSchema.CacheNewsTable.Cols.ID+ "=?",
                new String[]{mid});
    }

    public void updateCollectNews(ZhihuDailyNewsContent mcontent){
        String mid = String.valueOf(mcontent.getId());
        ContentValues values = getCollectValues(mcontent);
        mCollectDatabase.update(ZhihuNewsDbSchema.CollectNewsTable.NAME,values,
                ZhihuNewsDbSchema.CollectNewsTable.Cols.ID+ "=?",
                new String[]{mid});
    }


    private static ContentValues getContentValues(ZhihuDailyNewsContent content){
        ContentValues values = new ContentValues();

        values.put(ZhihuNewsDbSchema.CacheNewsTable.Cols.BODY,content.getBody());
        values.put(ZhihuNewsDbSchema.CacheNewsTable.Cols.TITLE, content.getTitle());
        values.put(ZhihuNewsDbSchema.CacheNewsTable.Cols.IMAGE, content.getImage());
        values.put(ZhihuNewsDbSchema.CacheNewsTable.Cols.SHAREURI, content.getShareUri());
        values.put(ZhihuNewsDbSchema.CacheNewsTable.Cols.IMAGES, content.getImages());
        values.put(ZhihuNewsDbSchema.CacheNewsTable.Cols.ID, ""+content.getId());
        return values;
    }

    private static ContentValues getCollectValues(ZhihuDailyNewsContent content){
        ContentValues values = new ContentValues();

        values.put(ZhihuNewsDbSchema.CollectNewsTable.Cols.TITLE, content.getTitle());
        values.put(ZhihuNewsDbSchema.CollectNewsTable.Cols.IMAGE, content.getImage());
        values.put(ZhihuNewsDbSchema.CollectNewsTable.Cols.SHAREURI, content.getShareUri());
        values.put(ZhihuNewsDbSchema.CollectNewsTable.Cols.IMAGES, content.getImages());
        values.put(ZhihuNewsDbSchema.CollectNewsTable.Cols.ID, ""+content.getId());
        return values;
    }


    private CacheNewsCursorWrapper queryNews(String whereClause,String[] whereArgs){
        Cursor cursor = mDatebase.query(
              ZhihuNewsDbSchema.CacheNewsTable.NAME,
                  null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        if(cursor.moveToFirst()){
            Log.d("mdzz","日了狗了");
        }
        return new CacheNewsCursorWrapper(cursor);
    }
    private CollectNewsCursorWrapper queryCollectNews(String whereClause,String[] whereArgs){
        Cursor cursor = mCollectDatabase.query(
                ZhihuNewsDbSchema.CollectNewsTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CollectNewsCursorWrapper(cursor);
    }


    //如要向缓存数据库中添加记录，调用此方法
    public void addCacheNews(ZhihuDailyNewsContent c){
        ContentValues values = getContentValues(c);
        mDatebase.insert(ZhihuNewsDbSchema.CacheNewsTable.NAME,null,values);
        Log.d("insert Cache success!","MDZZ");
    }

    //如要向收藏数据库中添加记录，调用此方法
    public void addCollectNews(ZhihuDailyNewsContent c){
        ContentValues values = getCollectValues(c);
        mCollectDatabase.insert(ZhihuNewsDbSchema.CollectNewsTable.NAME,null,values);
        Log.d("insert Collect success!","MDZZ");
    }

    //如要删除缓存数据库中的某条记录，调用此方法，传入删除对象
    public void deleteCacheNews(ZhihuDailyNewsContent c){
        String mid = String.valueOf(c.getId());
        mDatebase.delete(ZhihuNewsDbSchema.CacheNewsTable.NAME,
                ZhihuNewsDbSchema.CacheNewsTable.Cols.ID+"=?",
                new String[]{mid});
        Log.d("delete cache success!","hahahahahahhahahaha");
    }

    //如要删除收藏数据库中的某条记录，调用此方法，传入删除对象
    public void deleteCollectNews(ZhihuDailyNewsContent c){
        String mid = String.valueOf(c.getId());
        mCollectDatabase.delete(ZhihuNewsDbSchema.CollectNewsTable.NAME,
                ZhihuNewsDbSchema.CollectNewsTable.Cols.ID+"=?",
                new String[]{mid});
        Log.d("delete collect success!","hahafhjaksdhfakjhfjhsakfhka");
    }
    //如何要获取整个缓存列表，调用该方法

    public List<ZhihuDailyNewsContent> getCacheNewsContent(){
        List<ZhihuDailyNewsContent> contents = new ArrayList<>();
        CacheNewsCursorWrapper cursor = queryNews(null,null);
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                contents.add(cursor.getCacheNews());
                cursor.moveToNext();
            }
        }finally{
            cursor.close();
        }
        return contents;
    }

    //如果要获取收藏消息列表，调用该方法
    public List<ZhihuDailyNewsContent> getCollectNewsContent(){
        List<ZhihuDailyNewsContent> contents = new ArrayList<>();
        CollectNewsCursorWrapper cursor = queryCollectNews(null,null);
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                contents.add(cursor.getCollectNews());
                cursor.moveToNext();
            }
        }finally{
            cursor.close();
        }
        return contents;
    }
}
