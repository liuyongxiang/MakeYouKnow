package com.example.ttc.makeyouknowapp.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.ttc.makeyouknowapp.ZhihuDailyNewsContent;

/**
 * Created by ttc on 2017/3/15.
 */

public class CollectNewsCursorWrapper extends CursorWrapper {
    public CollectNewsCursorWrapper(Cursor cursor){

        super(cursor);

    }

    public ZhihuDailyNewsContent getCollectNews(){

        String title = getString(getColumnIndex(ZhihuNewsDbSchema.CollectNewsTable.Cols.TITLE));
        String image = getString(getColumnIndex(ZhihuNewsDbSchema.CollectNewsTable.Cols.IMAGE));
        String shareUri = getString(getColumnIndex(ZhihuNewsDbSchema.CollectNewsTable.Cols.SHAREURI));
        String images = getString(getColumnIndex(ZhihuNewsDbSchema.CollectNewsTable.Cols.IMAGES));
        String id = getString(getColumnIndex(ZhihuNewsDbSchema.CollectNewsTable.Cols.ID));

        ZhihuDailyNewsContent content = new ZhihuDailyNewsContent();


        content.setTitle(title);
        content.setImage(image);
        content.setShareUri(shareUri);
        content.setImages(images);
        content.setId(Integer.valueOf(id));

        return content;
    }
}
