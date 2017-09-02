package com.example.ttc.makeyouknowapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2017/3/15.
 */

public class OfflineFragment extends Fragment {
    private RecyclerView mOfflineRecyclrView;
    private TextView mDeleteView;
    private TextView mSelectAll;
    private TextView mNotSelectAll;
    private String mNewsBody;
    private String mNewsTitle;

    private OfflineAdapter adapter;
    private Map<Integer,Boolean> map = new HashMap<>();
    List<ZhihuDailyNewsContent> zhihuDailyNewsContents;

    public static OfflineFragment newInstance(){
        return new OfflineFragment();
    }


    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
         setRetainInstance(true);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.offline_container,container,false);
        mOfflineRecyclrView = (RecyclerView) view.findViewById(R.id.offline_list);
        mOfflineRecyclrView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDeleteView = (TextView) view.findViewById(R.id.tv_delete);
        mSelectAll = (TextView) view.findViewById(R.id.tv_select_all);
        mNotSelectAll = (TextView) view.findViewById(R.id.tv_select_not_all);

        mDeleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = map.size();

                for (int i = 0; i < temp; i++) {
                    if (map.get(zhihuDailyNewsContents.get(i).getId())) {
                        ZhihuDailyNewsContentLab.get(getActivity()).deleteCacheNews(zhihuDailyNewsContents.get(i));
                        map.remove(zhihuDailyNewsContents.get(i).getId());
                    }
                }
                zhihuDailyNewsContents=ZhihuDailyNewsContentLab.get(getActivity()).getCacheNewsContent();


                Log.i("delete:","1");
                adapter.notifyDataSetChanged();
                updateUI();
            }
        });

        mSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int i = 0; i < zhihuDailyNewsContents.size(); i++) {
                    map.put(zhihuDailyNewsContents.get(i).getId(), true);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        mNotSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<Integer, Boolean> m = adapter.getMap();
                for (int i = 0; i < m.size(); i++) {
                    m.put(zhihuDailyNewsContents.get(i).getId(), false);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        updateUI();
        return view;
    }

    public void updateUI(){
        zhihuDailyNewsContents = ZhihuDailyNewsContentLab.get(getActivity()).getCacheNewsContent();
        for(int i=0;i<zhihuDailyNewsContents.size();i++){
            map.put(zhihuDailyNewsContents.get(i).getId(),false);
        }
        if(adapter == null) {
            adapter = new OfflineAdapter(zhihuDailyNewsContents,getContext());
            mOfflineRecyclrView.setAdapter(adapter);
            adapter.setRecyclerViewOnItemClickListener(new RecyclerViewOnItemClickListener() {
                @Override
                public void onItemClickListener(View view, int position) {
                    if(adapter.isshowBox) {
                        //点击事件
                        //设置选中的项
                        adapter.setSelectItem(position);
                    } else{
                        mNewsBody = adapter.mZhihuDailyNewsContents.get(position).getBody();
                        mNewsTitle = adapter.mZhihuDailyNewsContents.get(position).getTitle();
                        Intent intent = new Intent();
                        intent.setAction("Offline");
                        intent.putExtra(".title", mNewsTitle);
                        intent.putExtra(".body", mNewsBody);
                        startActivity(intent);
                    }
                }

                @Override
                public boolean onItemLongClickListener(View view, int position) {
                    //长按事件
                    adapter.setShowBox();
                    //设置选中的项
                    adapter.setSelectItem(position);
                    adapter.notifyDataSetChanged();
                    return true;
                }
            });

        }else{
            adapter.setZhihuDailyNewsContents(zhihuDailyNewsContents);
            adapter.notifyDataSetChanged();
        }

    }

    private class OfflineAdapter extends RecyclerView.Adapter<OfflineAdapter.OfflineHolder>
    implements View.OnClickListener,View.OnLongClickListener{
        private List<ZhihuDailyNewsContent> mZhihuDailyNewsContents;
        private ZhihuDailyNewsContent zhihuDailyNewsContent;
        private Context mContext;
        //是否显示单选框,默认false
        private boolean isshowBox = false;
        // 存储勾选框状态的map集合
       // private Map<Integer, Boolean> map = new HashMap<>();
        //接口实例
        private RecyclerViewOnItemClickListener onItemClickListener;

        public OfflineAdapter(List<ZhihuDailyNewsContent> zhihuDailyNewsContents , Context context){
            mZhihuDailyNewsContents = zhihuDailyNewsContents;
            mContext = context;
           // initMap();
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                //注意这里使用getTag方法获取数据
                onItemClickListener.onItemClickListener(view, (Integer) view.getTag());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            //不管显示隐藏，清空状态
           // initMap();
            return onItemClickListener != null && onItemClickListener.onItemLongClickListener(view, (Integer) view.getTag());
        }

        public class OfflineHolder extends RecyclerView.ViewHolder{
            private TextView mTitleTextView;
            private CheckBox mCheckBox;
            private ZhihuDailyNewsContent mNewsContent;
            private View itemView;

            public OfflineHolder(View itemView){
                super(itemView);
                this.itemView = itemView;
                mTitleTextView = (TextView) itemView.findViewById(R.id.offline_zhihu_title);
                mCheckBox = (CheckBox) itemView.findViewById(R.id.offline_news_checkbox);
            }
        }

        @Override
        public OfflineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.offline_news_list,parent,false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new OfflineHolder(view);
        }

        @Override
        public void onBindViewHolder(OfflineHolder holder,final int position) {
            zhihuDailyNewsContent = mZhihuDailyNewsContents.get(position);
            holder.mTitleTextView.setText(zhihuDailyNewsContent.getTitle());


            //长按显示/隐藏
            if (isshowBox) {
                holder.mCheckBox.setVisibility(View.VISIBLE);
            } else {
                holder.mCheckBox.setVisibility(View.INVISIBLE);
            }
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.list_anim);
            //设置checkBox显示的动画
            if (isshowBox)
                holder.mCheckBox.startAnimation(animation);
            //设置Tag
            holder.itemView.setTag(position);
            //设置checkBox改变监听
            holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //用map集合保存
                    map.put(zhihuDailyNewsContents.get(position).getId(), isChecked);
                }
            });
            // 设置CheckBox的状态
            if (map.get(zhihuDailyNewsContents.get(position).getId()) == null) {
                map.remove(zhihuDailyNewsContents.get(position).getId());
            }
            holder.mCheckBox.setChecked(map.get(zhihuDailyNewsContents.get(position).getId()));
            //holder.BindOffline(zhihuDailyNewsContent);
        }

        @Override
        public int getItemCount() {
            return mZhihuDailyNewsContents.size();
        }

        public void setZhihuDailyNewsContents(List<ZhihuDailyNewsContent> zhihuDailyNewsContents){
            mZhihuDailyNewsContents = zhihuDailyNewsContents;
        }

        //设置点击事件
        public void setRecyclerViewOnItemClickListener(RecyclerViewOnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        //设置是否显示CheckBox
        public void setShowBox() {
            //取反
            isshowBox = !isshowBox;
        }

        //点击item选中CheckBox
        public void setSelectItem(int position) {
            //对当前状态取反
            if (map.get(zhihuDailyNewsContents.get(position).getId())) {
                map.put(zhihuDailyNewsContents.get(position).getId(), false);
            } else {
                map.put(zhihuDailyNewsContents.get(position).getId(), true);
            }
            notifyItemChanged(position);
        }

        //返回集合给MainActivity
        public Map<Integer, Boolean> getMap() {
            return map;
        }
    }
}
