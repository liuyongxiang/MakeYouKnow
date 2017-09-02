package com.example.ttc.makeyouknowapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ttc on 2017/3/15.
 */

public class CollectNewsBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME ="CollectNewsBaseHelper.db";

    public CollectNewsBaseHelper(Context context){

        super(context,DATABASE_NAME,null,VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table "+ ZhihuNewsDbSchema.CollectNewsTable.NAME+"("+
                " _id integer primary key autoincrement, " +
                ZhihuNewsDbSchema.CollectNewsTable.Cols.TITLE + ", " +
                ZhihuNewsDbSchema.CollectNewsTable.Cols.IMAGE + ", " +
                ZhihuNewsDbSchema.CollectNewsTable.Cols.SHAREURI+", " +
                ZhihuNewsDbSchema.CollectNewsTable.Cols.IMAGES+", " +
                ZhihuNewsDbSchema.CollectNewsTable.Cols.ID +
                ")");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
