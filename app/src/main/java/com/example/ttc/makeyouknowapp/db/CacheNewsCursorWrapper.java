package com.example.ttc.makeyouknowapp.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.ttc.makeyouknowapp.ZhihuDailyNewsContent;

/**
 * Created by ttc on 2017/3/14.
 */

public class CacheNewsCursorWrapper extends CursorWrapper {
    public CacheNewsCursorWrapper(Cursor cursor){

        super(cursor);

    }

    public ZhihuDailyNewsContent getCacheNews(){
        String body = getString(getColumnIndex(ZhihuNewsDbSchema.CacheNewsTable.Cols.BODY));
        String title = getString(getColumnIndex(ZhihuNewsDbSchema.CacheNewsTable.Cols.TITLE));
        String image = getString(getColumnIndex(ZhihuNewsDbSchema.CacheNewsTable.Cols.IMAGE));
        String shareUri = getString(getColumnIndex(ZhihuNewsDbSchema.CacheNewsTable.Cols.SHAREURI));
        String images = getString(getColumnIndex(ZhihuNewsDbSchema.CacheNewsTable.Cols.IMAGES));
        String id = getString(getColumnIndex(ZhihuNewsDbSchema.CacheNewsTable.Cols.ID));

        ZhihuDailyNewsContent content = new ZhihuDailyNewsContent();
        content.setBody(body);
        content.setTitle(title);
        content.setImage(image);
        content.setShareUri(shareUri);
        content.setImages(images);
        content.setId(Integer.valueOf(id));

        return content;
    }



}
