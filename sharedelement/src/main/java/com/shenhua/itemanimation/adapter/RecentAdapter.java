package com.shenhua.itemanimation.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shenhua.itemanimation.DetailActivity;
import com.shenhua.itemanimation.MainActivity;
import com.shenhua.itemanimation.R;

import java.util.List;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.RecentListRowHolder> {

    private List<DataBean> recentList;
    private Context mContext;

    public RecentAdapter(Context context, List<DataBean> recentList) {
        this.recentList = recentList;
        this.mContext = context;
    }

    @Override
    public RecentListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recent_item, null);
        RecentListRowHolder ml = new RecentListRowHolder(v);
        return ml;
    }

    @Override
    public void onBindViewHolder(RecentListRowHolder recentListRowHolder, int i) {
        DataBean recentItem = recentList.get(i);
        recentListRowHolder.cover.setImageResource(recentItem.getCover());
        recentListRowHolder.icon.setImageResource(recentItem.getIcon());
        recentListRowHolder.title.setText(recentItem.getTitle());
        recentListRowHolder.time.setText(recentItem.getTime());
    }

    @Override
    public int getItemCount() {
        return (null != recentList ? recentList.size() : 0);
    }

    public class RecentListRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView icon, cover;
        protected TextView title, time;

        public RecentListRowHolder(View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(R.id.icon);
            this.cover = (ImageView) view.findViewById(R.id.cover);
            this.title = (TextView) view.findViewById(R.id.recent_title);
            this.time = (TextView) view.findViewById(R.id.recent_time);

            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, DetailActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.getInstance(), Pair.create((View) cover, "cover"));
            mContext.startActivity(intent, options.toBundle());
        }

    }


}

