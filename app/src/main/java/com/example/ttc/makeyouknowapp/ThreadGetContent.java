package com.example.ttc.makeyouknowapp;

import android.os.AsyncTask;

/**
 * Created by ttc on 2017/3/14.
 */

public class ThreadGetContent extends AsyncTask<Void,Void,ZhihuDailyNewsContent>{

    ZhihuDailyNewsContent mZhihuDailyNewsContent;
    String url ;

    @Override
    protected ZhihuDailyNewsContent doInBackground(Void... voids) {
        mZhihuDailyNewsContent = new GetZhihuNewsContent().fetchContent(url);
        return mZhihuDailyNewsContent;
    }

    protected void onPostExecute (ZhihuDailyNewsContent items){
        mZhihuDailyNewsContent = items;
    }

    public ThreadGetContent(String url){
        this.url = url;
    }

    public ZhihuDailyNewsContent getZhihuDailyNewsContent() {
        return mZhihuDailyNewsContent;
    }
}

