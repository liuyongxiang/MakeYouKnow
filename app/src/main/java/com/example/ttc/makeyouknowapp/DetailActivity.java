package com.example.ttc.makeyouknowapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

//day13:存在问题,文中链接不可用，没有回退操作，没有menuitems

public class DetailActivity extends AppCompatActivity {

    private DetailFragment mDetailFragment;
    private static final String EXTRA_DETAIL_ID = "MAKEYOUKNOWN-DETAIL-ID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //获取intent
        Intent intent = getIntent();

        if (savedInstanceState != null) {
            mDetailFragment = (DetailFragment) getSupportFragmentManager().getFragment(savedInstanceState,"detailFragment");
        } else {
            mDetailFragment = DetailFragment.newInstance(intent.getIntExtra("id",0),intent.getStringExtra("title"),intent.getStringExtra("coverUrl"));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mDetailFragment)
                    .commit();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mDetailFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "detailFragment", mDetailFragment);
        }
    }

    @Override
    public void onPause(){
        super.onPause();

}

    public static Intent newIntent(Context packageContext,int detailId,String title,String imgUrl){
        Intent intent = new Intent(packageContext,DetailActivity.class)
        //.putExtra("type", BeanType.TYPE_ZHIHU)    设置为知乎类型新闻
        .putExtra("id", detailId).putExtra("title", title).putExtra("coverUrl", imgUrl);
        return intent;
    }

}
