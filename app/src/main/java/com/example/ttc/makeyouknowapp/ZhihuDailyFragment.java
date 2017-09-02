package com.example.ttc.makeyouknowapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;


/**
 * Created by ttc on 2017/3/12.
 */

public class ZhihuDailyFragment extends Fragment
{

    private static final String TAG = "ZhihuDailyFragment";

    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;

    private static String zhihuUri = "http://news-at.zhihu.com/api/4/news/latest";
    private RecyclerView mZhihuMessage;
    private FloatingActionButton fab;
    private SwipeRefreshLayout refresh;

    private Callbacks mCallbacks;

    public  void setZhihuUri(String uri){
        zhihuUri=uri;
    }

    private ZhihuDailyNews mItems = new ZhihuDailyNews() ;
    private ZhihuDailyNewsContent mZhihuDailyNewsContent;

    //回调接口
    public interface Callbacks{
        void onItemClick(ZhihuDailyNews.Question news);
    }


    private ThumbnailDownloader<ZhiDailyNewsHolder> mThumbnailDownloader;


    public static ZhihuDailyFragment newInstance(){
        return new ZhihuDailyFragment();
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


        Handler responseHandler = new Handler();

        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<ZhiDailyNewsHolder>() {
                    @Override
                    public void onThumbnailDownloaded(ZhiDailyNewsHolder photoHolder, Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        photoHolder.bindDrawable(drawable);
                    }
                }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG,"Background thread started");

    }

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
                zhihuUri = "http://news-at.zhihu.com/api/4/news/latest";
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
                        Date date = new DateFormatter().stringToDate(mItems.getDate());
                        zhihuUri="http://news.at.zhihu.com/api/4/news/before/"+new DateFormatter()
                                .ZhihuDailyDateFormat(date);
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
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog =  DatePickerFragment
                        .newInstance(
                                new DateFormatter().stringToDate(mItems.getDate()));
                dialog.setTargetFragment(ZhihuDailyFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);

           zhihuUri="http://news.at.zhihu.com/api/4/news/before/"+new DateFormatter()
                    .ZhihuDailyDateFormat(new DateFormatter().dateAddOne(date));
            //Log.d("The format date is :",zhihuUri);
            updateUI();

        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    public void onDestroy(){
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG,"Background thread destroyed");
    }

    private void setupAdapter(){
        if(isAdded()){
            mZhihuMessage.setAdapter(new ZhihuDailyFragment.ZhihuAdapter(mItems.getStories()));
        }
    }
    private class FetchItemsTask extends AsyncTask<Void,Void,ZhihuDailyNews> {
        @Override
        protected ZhihuDailyNews doInBackground(Void... params) {
           return  new ZhihuWeb().fetchItems(zhihuUri);

        }

        protected void onPostExecute (ZhihuDailyNews items){
            mItems = items;
            setupAdapter();
        }

    }

    private  class ZhiDailyNewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTitleTextView;
        private ImageView mImageView;

       private ZhihuDailyNews.Question question;

        public ZhiDailyNewsHolder (View itemView){
            super(itemView);
           // itemView.setOnClickListener(this);
            mTitleTextView = (TextView)
                    itemView.findViewById(R.id.zhihu_title);
            mImageView = (ImageView)
                    itemView.findViewById(R.id.zhihu_picture);

            itemView.setOnClickListener(this);
        }
        private void bindZhihuNews(ZhihuDailyNews.Question item){
            mTitleTextView.setText(item.getTitle());
            this.question = item;

        }
        public void bindDrawable(Drawable drawble){
            mImageView.setImageDrawable(drawble);
        }

       @Override
       public void onClick(View view) {
           mCallbacks.onItemClick(question);
       }
    }

    private class ZhihuAdapter extends RecyclerView.Adapter<ZhiDailyNewsHolder>{
        private List <ZhihuDailyNews.Question> mZhihuitems;

        public ZhihuAdapter(List<ZhihuDailyNews.Question> zhihuitems){
            mZhihuitems = zhihuitems;
        }

        public ZhiDailyNewsHolder onCreateViewHolder(ViewGroup viewGroup,int viewType){
           LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.zhihu_news_list, viewGroup, false);
            return new ZhiDailyNewsHolder(view);
        }

        public void onBindViewHolder(ZhiDailyNewsHolder zhiDailyNewsHolder,int position){
            ZhihuDailyNews.Question item = mZhihuitems.get(position);
            zhiDailyNewsHolder.bindZhihuNews(item);
            Drawable imageHolder = getResources().getDrawable(R.drawable.bill_up_close);
            zhiDailyNewsHolder.bindDrawable(imageHolder);
            mThumbnailDownloader.queueThumbnail(zhiDailyNewsHolder,item.getImages());
        }

        public int getItemCount(){
            return mZhihuitems.size();
        }
    }

    public void updateUI(){
        new FetchItemsTask().execute();
    }




}
