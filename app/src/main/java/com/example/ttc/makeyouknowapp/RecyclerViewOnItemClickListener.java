package com.example.ttc.makeyouknowapp;

import android.view.View;

/**
 * Created by lenovo on 2017/3/15.
 */

//接口回调设置点击事件
public interface RecyclerViewOnItemClickListener {
    //点击事件
    void onItemClickListener(View view, int position);

    //长按事件
    boolean onItemLongClickListener(View view, int position);
}