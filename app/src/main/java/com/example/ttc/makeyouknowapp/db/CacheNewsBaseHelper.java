package com.example.ttc.makeyouknowapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ttc on 2017/3/14.
 */

public class CacheNewsBaseHelper extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    private static final String DATABASE_NAME ="newsBase.db";

    public CacheNewsBaseHelper(Context context){

        super(context,DATABASE_NAME,null,VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ ZhihuNewsDbSchema.CacheNewsTable.NAME+"("+
                " _id integer primary key autoincrement, " +
                ZhihuNewsDbSchema.CacheNewsTable.Cols.BODY + ", " +
                ZhihuNewsDbSchema.CacheNewsTable.Cols.TITLE + ", " +
                ZhihuNewsDbSchema.CacheNewsTable.Cols.IMAGE + ", " +
                ZhihuNewsDbSchema.CacheNewsTable.Cols.SHAREURI+", " +
                ZhihuNewsDbSchema.CacheNewsTable.Cols.IMAGES+", " +
                ZhihuNewsDbSchema.CacheNewsTable.Cols.ID +
                ")");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
