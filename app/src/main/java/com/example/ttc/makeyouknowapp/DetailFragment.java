package com.example.ttc.makeyouknowapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class DetailFragment extends Fragment {
    private ImageView imageView;
    private WebView webView;
    private NestedScrollView scrollView;
    private CollapsingToolbarLayout toolbarLayout;
    private SwipeRefreshLayout refreshLayout;

    private int detailId;
    private String detailTitle;
    private String detailUrl;

    private Context context;
    private MenuItem item;


    private SharedPreferences sharedPreference;
    ZhihuDailyNewsContent content;
    ZhihuDailyNewsContent content1;
    public DetailFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        //获取activity传入参数
        detailId = getArguments().getInt("DETAIL-ID");
        detailTitle = getArguments().getString("DETAIL-TITLE");
        detailUrl = getArguments().getString("DETAIL-IMGURL");
        //用于打开浏览器连接
        sharedPreference = context.getSharedPreferences("user_settings", MODE_PRIVATE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_detail, container, false);
        initViews(view);
        //设置菜单栏
        setHasOptionsMenu(true);
        //初始化刷新
        refresh();

        view.findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0, 0);
            }
        });
        //刷新内容
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //presenter.requestData();跟新视图
                refresh();
            }
        });

        return view;
    }
    //detail上面图片
    public void showCover(String url) {
        String temp = new ZhihuImageUriFormat(url).CoverChange();
        Glide.with(getActivity())
                .load(temp)
                .asBitmap()
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .error(R.drawable.placeholder)
                .into(imageView);

    }

    //刷新界面
    public void refresh(){
        //刷新开始显示加载
        showLoading();
        //显示图片
        showCover(detailUrl);
        //设置title样式
        setCollapsingToolbarLayoutTitle(detailTitle);
        //刷新内容：给我一个Question对象获取其中share_url属性即可

        //获取url
        String newUrl = API.ZhiHuSpecialNews+detailId;
        ThreadGetContent thread = new ThreadGetContent(newUrl);

        thread.execute();
        //考虑回调函数实现,先这样子
        while (thread.getZhihuDailyNewsContent() == null){
            //Log.d("wait","等待获取内容");
        }
        content = thread.getZhihuDailyNewsContent();

        String url = content.getShareUri();
        if(content.getBody() == null){
            showResultWithBody(url);
        }else {
            showResultWithoutBody(convertZhiHuContent(content.getBody()));
        }
        insertCacheNewsSchema();
        //刷新结束关闭加载项
        stopLoading();
    }



    //显示加载项
    public void showLoading(){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
    }
    //结束加载
    public void  stopLoading(){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    //web下载URL
    public void showResultWithBody(String url) {
        webView.loadUrl(url);
    }
    //没有内容显示
    public void showResultWithoutBody(String url){
        webView.loadDataWithBaseURL("x-data://base",url,"text/html","utf-8",null);
    }

    //转化知乎内容显示格式
    private String convertZhiHuContent(String preResult){
        preResult = preResult.replace("<div class=\"img-place-holder\">", "");
        preResult = preResult.replace("<div class=\"headline\">", "");

        // 在api中，css的地址是以一个数组的形式给出，这里需要设置
        // in fact,in api,css addresses are given as an array
        // api中还有js的部分，这里不再解析js
        // javascript is included,but here I don't use it
        // 不再选择加载网络css，而是加载本地assets文件夹中的css
        // use the css file from local assets folder,not from network
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/zhihu_daily.css\" type=\"text/css\">";


        // 根据主题的不同确定不同的加载内容
        // load content judging by different theme
        String theme = "<body className=\"\" onload=\"onLoaded()\">";
        if ((context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES){
            theme = "<body className=\"\" onload=\"onLoaded()\" class=\"night\">";
        }

        return new StringBuilder()
                .append("<!DOCTYPE html>\n")
                .append("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n")
                .append("<head>\n")
                .append("\t<meta charset=\"utf-8\" />")
                .append(css)
                .append("\n</head>\n")
                .append(theme)
                .append(preResult)
                .append("</body></html>").toString();
    }

    //我去要改:初始化视图
    public void initViews(View view) {

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        //设置下拉刷新的按钮的颜色
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);

        webView = (WebView) view.findViewById(R.id.web_view);
        webView.setScrollbarFadingEnabled(true);

        DetailActivity activity = (DetailActivity) getActivity();
        activity.setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = (ImageView) view.findViewById(R.id.image_view);
        scrollView = (NestedScrollView) view.findViewById(R.id.scrollView);
        toolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.toolbar_layout);

        //能够和js交互
        webView.getSettings().setJavaScriptEnabled(true);
        //缩放,设置为不能缩放可以防止页面上出现放大和缩小的图标
        webView.getSettings().setBuiltInZoomControls(false);
        //缓存
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //开启DOM storage API功能
        webView.getSettings().setDomStorageEnabled(true);
        //开启application Cache功能
        webView.getSettings().setAppCacheEnabled(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //presenter.openUrl(view, url);
                openUrl(webView,url);
                return true;
            }

        });

    }

    //浏览器打开图文链接
    public void openUrl(WebView view,String url){
     /*   if(sharedPreference.getBoolean("in_app_browser",true)){

        }*/
        CustomTabsIntent.Builder cusTabInternt = new CustomTabsIntent.Builder()
                .setToolbarColor(context.getResources().getColor(R.color.colorAccent))
                .setShowTitle(true);
        CustomTabActivityHelper.openCustomTab(
                (Activity) context,
                cusTabInternt.build(),
                Uri.parse(url),
                new CustomFallback(){
                    @Override
                    public void openUri(Activity activity, Uri uri) {
                        super.openUri(activity, uri);
                    }
                }
        );


    }

    //字体根据视图大小变化
    private void setCollapsingToolbarLayoutTitle(String title) {
        toolbarLayout.setTitle(title);
        toolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        toolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPlus1);
        toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_more, menu);
        item = menu.getItem(0);
        setMarked(isMarked(detailId));
        super.onCreateOptionsMenu(menu, inflater);
    }

    //菜单栏选择回调函数
    @Override
            public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.isMarked :
                markNews(item);
                return true;
            case R.id.openInBrowser:
                openInLocalBrowser();
                return true;
            case R.id.share:
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, content.getShareUri());
                startActivity(sendIntent);
                return true;

            default:
                return  super.onOptionsItemSelected(item);
        }
    }

    //收藏有关
    public void markNews(MenuItem item){
        //首先查询数据库看是否收藏,在考虑是否每次都读写数据库(写回还是写通)
        //setMarked(isMarked(detailId));
        if(isMarked(detailId)){
            //设置为未收藏
            setMarked(false);
            //取消收藏
            ZhihuDailyNewsContentLab.get(getActivity()).deleteCollectNews(content);
            Snackbar.make(imageView, R.string.marked, Snackbar.LENGTH_SHORT).setAction("撤销", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isMarked(detailId);
                }
            }).show();
        }else {
            //设置为收藏
            setMarked(true);
            //加入数据库
            ZhihuDailyNewsContentLab.get(getActivity()).addCollectNews(content);
            Snackbar.make(imageView, R.string.unmark, Snackbar.LENGTH_SHORT).setAction("撤销", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //撤销标记操作
                    isMarked(detailId);
                }
            }).show();
        }
        return;
    }

    //查看数据库是否收藏
    public Boolean isMarked(int newId){
        if(ZhihuDailyNewsContentLab.get(getActivity()).getCollectNew(newId) == null){
            return false;
        }else {
            return true;
        }

    }

    public void setMarked(Boolean isMarked){
        if(isMarked){
            item.setTitle(R.string.marked)
                    .setIcon(R.drawable.ic_info_black_24dp);
        }else {
            item.setTitle(R.string.unmark)
                    .setIcon(R.drawable.ic_star_black_24dp);
        }

    }



    //在手机自带浏览器打开
    public void openInLocalBrowser(){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(content.getShareUri()));
            context.startActivity(intent);
        }catch (android.content.ActivityNotFoundException e){
            Snackbar.make(imageView, R.string.not_found_browser, Snackbar.LENGTH_SHORT).show();        }
    }

    public static DetailFragment newInstance(int detailId, String title, String imgUrl){
        Bundle args = new Bundle();
        args.putInt("DETAIL-ID",detailId);
        args.putString("DETAIL-TITLE",title);
        args.putString("DETAIL-IMGURL",imgUrl);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public void insertCacheNewsSchema(){

        content1=content;
        if(ZhihuDailyNewsContentLab.get(getActivity()).getCacheNew(content1.getId())==null) {

            ZhihuDailyNewsContentLab.get(getActivity()).addCacheNews(content1);

        }
        Log.d("haha","hehe");
    }

}
