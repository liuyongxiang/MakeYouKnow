package com.example.ttc.makeyouknowapp;

import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,ZhihuCollectionFragment.Callbacks,ZhihuDailyFragment.Callbacks,ZhihuDailyThemeNewsFragment.Callbacks{
    private static final String DIALOG_DATE = "DialogDate";

    private ZhihuDailyFragment mZhihuDailyFragment;
    private DatePickerFragment mDatePickerFragment;
    private OfflineFragment mOfflineFragment;
    private DrawerLayout drawer;
    private ZhihuDailyThemeNewsFragment mZhihuDailyThemeNewsFragment;
    private ZhihuCollectionFragment mZhihuCollectionFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ZhihuDailyNewsContentLab.get(getApplicationContext());

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(isNetworkAvailableAndConnected()){
            mZhihuDailyFragment = ZhihuDailyFragment.newInstance();
            mZhihuDailyFragment.updateUI();
            if(!mZhihuDailyFragment.isAdded()) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.content_main, mZhihuDailyFragment, "ZhihuDailyFragment")
                        .commit();
            }

            mZhihuDailyThemeNewsFragment = ZhihuDailyThemeNewsFragment.newInstance();
            mZhihuDailyThemeNewsFragment.setZhihuUri("http://news-at.zhihu.com/api/4/theme/10");
            if(!mZhihuDailyThemeNewsFragment.isAdded()){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.content_main,mZhihuDailyThemeNewsFragment,"ZhihuDailyThemeNewsFragment")
                        .hide(mZhihuDailyThemeNewsFragment)
                        .commit();
            }

            mOfflineFragment = OfflineFragment.newInstance();
            if(!mOfflineFragment.isAdded()) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.content_main, mOfflineFragment, "OfflineFragment")
                        .hide(mOfflineFragment)
                        .commit();
            }

            mZhihuCollectionFragment = ZhihuCollectionFragment.newInstance();
            if(!mZhihuCollectionFragment.isAdded()){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.content_main,mZhihuCollectionFragment,"mZhihuCollectFragment")
                        .hide(mZhihuCollectionFragment)
                        .commit();

            }

        }else{
            Toast.makeText(this,"当前网络不可用！",Toast.LENGTH_LONG).show();
            mZhihuDailyFragment = ZhihuDailyFragment.newInstance();
            if(!mZhihuDailyFragment.isAdded()) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.content_main, mZhihuDailyFragment, "ZhihuDailyFragment")
                        .hide(mZhihuDailyFragment)
                        .commit();
            }

            mZhihuDailyThemeNewsFragment = ZhihuDailyThemeNewsFragment.newInstance();
            mZhihuDailyThemeNewsFragment.setZhihuUri("http://news-at.zhihu.com/api/4/theme/10");
            if(!mZhihuDailyThemeNewsFragment.isAdded()){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.content_main,mZhihuDailyThemeNewsFragment,"ZhihuDailyThemeNewsFragment")
                        .hide(mZhihuDailyThemeNewsFragment)
                        .commit();
            }
            mOfflineFragment = OfflineFragment.newInstance();
            if(!mOfflineFragment.isAdded()) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.content_main, mOfflineFragment, "OfflineFragment")
                        .commit();
            }

            mZhihuCollectionFragment = ZhihuCollectionFragment.newInstance();
            if(!mZhihuCollectionFragment.isAdded()){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.content_main,mZhihuCollectionFragment,"ZhihuCollectionFragment")
                        .hide(mZhihuCollectionFragment)
                        .commit();
            }
        }
    }


    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            try{
                Uri uri = Uri.parse("market://details?id="+getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }catch(ActivityNotFoundException e){
                Toast.makeText(this, "Couldn't launch the market !", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
            return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(isNetworkAvailableAndConnected()) {

            if (id == R.id.nav_home) {
                // Handle the camera action
                mZhihuDailyFragment.updateUI();

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(mZhihuDailyThemeNewsFragment);
                fragmentTransaction.hide(mOfflineFragment);
                fragmentTransaction.hide(mZhihuCollectionFragment);
                fragmentTransaction.show(mZhihuDailyFragment);
                fragmentTransaction.commit();


            } else if (id == R.id.nav_cache) {
                mOfflineFragment.updateUI();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(mZhihuDailyFragment);
                fragmentTransaction.hide(mZhihuDailyThemeNewsFragment);
                fragmentTransaction.hide(mZhihuCollectionFragment);
                fragmentTransaction.show(mOfflineFragment);
                fragmentTransaction.commit();


            } else if (id == R.id.nav_change) {
                drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        //检测当前主题模式
                        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                                == Configuration.UI_MODE_NIGHT_YES) {
                            //使用日间模式
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        } else {
                            //使用夜间模式
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        }
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.remove(mZhihuDailyThemeNewsFragment);
                        fragmentTransaction.remove(mOfflineFragment);
                        fragmentTransaction.remove(mZhihuCollectionFragment);
                        fragmentTransaction.remove(mZhihuDailyFragment);
                        fragmentTransaction.commit();
                        recreate();
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {

                    }
                });


            } else if (id == R.id.nav_networkSafety) {
                mZhihuDailyThemeNewsFragment.setZhihuUri("http://news-at.zhihu.com/api/4/theme/10");
                mZhihuDailyThemeNewsFragment.updateUI();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(mZhihuDailyFragment);
                fragmentTransaction.hide(mOfflineFragment);
                fragmentTransaction.hide(mZhihuCollectionFragment);
                fragmentTransaction.show(mZhihuDailyThemeNewsFragment);
                fragmentTransaction.commit();


            } else if (id == R.id.nav_about) {
                mZhihuDailyThemeNewsFragment.setZhihuUri("http://news-at.zhihu.com/api/4/theme/11");
                mZhihuDailyThemeNewsFragment.updateUI();

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(mZhihuDailyFragment);
                fragmentTransaction.hide(mOfflineFragment);
                fragmentTransaction.hide(mZhihuCollectionFragment);
                fragmentTransaction.show(mZhihuDailyThemeNewsFragment);
                fragmentTransaction.commit();

            } else if (id == R.id.nav_gym) {
                mZhihuDailyThemeNewsFragment.setZhihuUri("http://news-at.zhihu.com/api/4/theme/8");
                mZhihuDailyThemeNewsFragment.updateUI();

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(mZhihuDailyFragment);
                fragmentTransaction.hide(mOfflineFragment);
                fragmentTransaction.hide(mZhihuCollectionFragment);
                fragmentTransaction.show(mZhihuDailyThemeNewsFragment);
                fragmentTransaction.commit();


            } else if (id == R.id.nav_economy) {
                mZhihuDailyThemeNewsFragment.setZhihuUri("http://news-at.zhihu.com/api/4/theme/6");
                mZhihuDailyThemeNewsFragment.updateUI();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(mZhihuDailyFragment);
                fragmentTransaction.hide(mOfflineFragment);
                fragmentTransaction.hide(mZhihuCollectionFragment);
                fragmentTransaction.show(mZhihuDailyThemeNewsFragment);
                fragmentTransaction.commit();


            }else if(id == R.id.nav_collect){
                mZhihuCollectionFragment.updateUI();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(mZhihuDailyFragment);
                fragmentTransaction.hide(mOfflineFragment);
                fragmentTransaction.hide(mZhihuDailyThemeNewsFragment);
                fragmentTransaction.show(mZhihuCollectionFragment);
                fragmentTransaction.commit();
            }
        }else{


            if (id == R.id.nav_home) {

                showZhihuCollectionFragment();

            } else if (id == R.id.nav_collect) {

                showZhihuCollectionFragment();

            } else if (id == R.id.nav_change) {
                Toast.makeText(this,"当前网络不可用！",Toast.LENGTH_SHORT).show();
                drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        //检测当前主题模式
                        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                                == Configuration.UI_MODE_NIGHT_YES) {
                            //使用日间模式
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        } else {
                            //使用夜间模式
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        }
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.remove(mZhihuDailyThemeNewsFragment);
                        fragmentTransaction.remove(mZhihuDailyFragment);
                        fragmentTransaction.remove(mZhihuCollectionFragment);
                        fragmentTransaction.remove(mOfflineFragment);
                        fragmentTransaction.commit();
                        recreate();
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {

                    }
                });


            } else if (id == R.id.nav_networkSafety) {
                showZhihuCollectionFragment();
            } else if (id == R.id.nav_about) {
                showZhihuCollectionFragment();

            } else if (id == R.id.nav_gym) {
                showZhihuCollectionFragment();


            } else if (id == R.id.nav_economy) {
                showZhihuCollectionFragment();

            }
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemClick(ZhihuDailyNews.Question news) {
        Intent intent = DetailActivity.newIntent(this,news.getId(),news.getTitle(),news.getImages());

        startActivity(intent);
    }
    @Override
    public void onThemeItemClick(ZhihuDailyNews.Question news){
        Intent intent = DetailActivity.newIntent(this,news.getId(),news.getTitle(),news.getImages());

        startActivity(intent);
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo()!=null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }

    public void showZhihuCollectionFragment(){
        Toast.makeText(this,"当前网络不可用！",Toast.LENGTH_SHORT).show();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mOfflineFragment.updateUI();
        fragmentTransaction.hide(mZhihuCollectionFragment);
        fragmentTransaction.hide(mZhihuDailyFragment);
        fragmentTransaction.hide(mZhihuDailyThemeNewsFragment);
        fragmentTransaction.show(mOfflineFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onThemeItemClick(ZhihuDailyNewsContent news) {
        Intent intent = DetailActivity.newIntent(this,news.getId(),news.getTitle(),news.getImages());

        startActivity(intent);
    }
}
