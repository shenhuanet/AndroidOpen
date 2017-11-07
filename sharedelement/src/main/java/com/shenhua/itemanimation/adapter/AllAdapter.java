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

public class AllAdapter extends RecyclerView.Adapter<AllAdapter.AllGridHolder> {

    private List<DataBean> beanList;
    private Context mContext;

    public AllAdapter(Context context, List<DataBean> beanList) {
        this.beanList = beanList;
        this.mContext = context;
    }

    @Override
    public AllGridHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_item, null);
        AllGridHolder ml = new AllGridHolder(v);
        return ml;
    }

    @Override
    public void onBindViewHolder(AllGridHolder allGridHolder, int i) {
        DataBean bean = beanList.get(i);
        allGridHolder.icon.setImageResource(bean.getIcon());
        allGridHolder.title.setText(bean.getTitle());
    }

    @Override
    public int getItemCount() {
        return (null != beanList ? beanList.size() : 0);
    }

    public class AllGridHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView icon;
        protected TextView title;

        public AllGridHolder(View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(R.id.all_icon);
            this.title = (TextView) view.findViewById(R.id.all_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, DetailActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.getInstance(), Pair.create((View) icon, "cover"), Pair.create((View) icon, "icon"));
            mContext.startActivity(intent, options.toBundle());
        }
    }
}

