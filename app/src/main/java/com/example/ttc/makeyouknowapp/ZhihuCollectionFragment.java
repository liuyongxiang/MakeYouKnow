package com.example.ttc.makeyouknowapp;

/**
 * Created by ttc on 2017/3/16.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ttc on 2017/3/15.
 */

public class ZhihuCollectionFragment extends Fragment
{

    private static final String TAG = "ZhihuCollectionFragment";

    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;

    private RecyclerView mZhihuMessage;
    private FloatingActionButton fab;
    private SwipeRefreshLayout refresh;

    private Callbacks mCallbacks;

    public static ZhihuCollectionFragment newInstance(){

        return new ZhihuCollectionFragment();

    }

    private List<ZhihuDailyNewsContent> mItems = new ArrayList<>();
    private ThumbnailDownloader<ZhihuCollectionHolder> mThumbnailDownloader;


    //回调接口
    public interface Callbacks{
        void onThemeItemClick(ZhihuDailyNewsContent news);
    }



    //fragment 附着
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }
    //fragment 解除
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        //获取数据库收藏列表

        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<ZhihuCollectionHolder>() {
                    @Override
                    public void onThumbnailDownloaded(ZhihuCollectionHolder photoHolder, Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        photoHolder.bindThemeDrawable(drawable);
                    }
                }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.zhihu_container,container,false);
        mZhihuMessage = (RecyclerView) v.
                findViewById(R.id.zhihu_list);
        mZhihuMessage.setLayoutManager(new LinearLayoutManager(getActivity()));
        //下拉刷新
        refresh = (SwipeRefreshLayout) v.findViewById(R.id.refreshLayout);
        //设置下拉刷新的按钮的颜色
        refresh.setColorSchemeResources(R.color.colorPrimary);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {

                updateUI();
                refresh.setRefreshing(false);
            }
        });
        //上拉加载
        mZhihuMessage.setOnScrollListener(new RecyclerView.OnScrollListener(){
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager manager = (LinearLayoutManager)recyclerView.getLayoutManager();
                //当不滚动时
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    //获取最后一个完全显示的Item position
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();

                    //判断是否滚动到底并且是向下滑动
                    if(lastVisibleItem == (totalItemCount-1) && isSlidingToLast){

                        updateUI();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSlidingToLast = dy > 0 ;

                //显示或隐藏fab
                if(dy >0 ){
                    fab.hide();
                }else{
                    fab.show();
                }
            }
        });
        fab = (FloatingActionButton)v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return v;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onDestroy(){
        super.onDestroy();
    }

    private void setupAdapter(){
        if(isAdded()){
            mZhihuMessage.setAdapter(new ZhihuCollectionAdapter(mItems));
        }
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<ZhihuDailyNewsContent>> {
        @Override
        protected List<ZhihuDailyNewsContent> doInBackground(Void... params) {
            return ZhihuDailyNewsContentLab.get(getActivity()).getCollectNewsContent();
        }

        protected void onPostExecute (List<ZhihuDailyNewsContent> items){
            mItems = items;
            setupAdapter();
        }

    }

    private  class ZhihuCollectionHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTitleTextView;
        private ImageView mImageView;

        private ZhihuDailyNewsContent question;

        public ZhihuCollectionHolder (View itemView){
            super(itemView);
            // itemView.setOnClickListener(this);
            mTitleTextView = (TextView)
                    itemView.findViewById(R.id.zhihu_title);
            mImageView = (ImageView)
                    itemView.findViewById(R.id.zhihu_picture);

            itemView.setOnClickListener(this);
        }
        private void bindZhihuCollectionNews(ZhihuDailyNewsContent item){
            mTitleTextView.setText(item.getTitle());
            this.question = item;

        }
        public void bindThemeDrawable(Drawable drawble){
            mImageView.setImageDrawable(drawble);
        }

        @Override
        public void onClick(View view) {
            mCallbacks.onThemeItemClick(question);
        }
    }

    private class ZhihuCollectionAdapter extends RecyclerView.Adapter<ZhihuCollectionHolder>{
        private List<ZhihuDailyNewsContent> mZhihuitems;

        public ZhihuCollectionAdapter(List<ZhihuDailyNewsContent> zhihuitems){
            mZhihuitems = zhihuitems;
        }

        public ZhihuCollectionHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.zhihu_news_list, viewGroup, false);
            return new ZhihuCollectionHolder(view);
        }

        @Override
        public void onBindViewHolder(ZhihuCollectionHolder holder, int position) {
            ZhihuDailyNewsContent item = mZhihuitems.get(position);
            holder.bindZhihuCollectionNews(item);
            Drawable imageHolder = getResources().getDrawable(R.drawable.bill_up_close);
            holder.bindThemeDrawable(imageHolder);
            mThumbnailDownloader.queueThumbnail(holder,item.getImages());

        }

        public int getItemCount(){
            return mZhihuitems.size();
        }
    }

    public void updateUI(){
        new FetchItemsTask().execute();
    }

}


