package com.shenhua.pulldownfilterdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by shenhua on 16/8/28.
 */
public class FilterGridViewAdapter extends BaseAdapter {

    private Context context;

    public FilterGridViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 8;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_filter, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //在这里设置数据
        holder.tvTitle.setText("标题 " + Integer.toString(position));
        return convertView;
    }

    static class ViewHolder {
        ImageView ivImage;
        TextView tvTitle;

        ViewHolder(View view) {
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
        }
    }

}
