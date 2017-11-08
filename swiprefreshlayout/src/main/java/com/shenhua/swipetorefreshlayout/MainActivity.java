package com.shenhua.swipetorefreshlayout;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shenhua.swipetorefreshlayout.widget.SwipeToRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SwipeToRefreshLayout.OnRefreshListener {

    private SwipeToRefreshLayout mSwipeToRefreshLayout;
    private RecyclerView mRecyclerView;
    private StringAdapter adapter;
    private List<String> mStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwipeToRefreshLayout = (SwipeToRefreshLayout) findViewById(R.id.swipe_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSwipeToRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StringAdapter(this, getMyStrings());
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_top:
                mSwipeToRefreshLayout.setType(SwipeToRefreshLayout.SwipeToRefreshLayoutType.TOP);
                break;
            case R.id.action_bottom:
                mSwipeToRefreshLayout.setType(SwipeToRefreshLayout.SwipeToRefreshLayoutType.BOTTOM);
                break;
            case R.id.action_top_bottom:
                mSwipeToRefreshLayout.setType(SwipeToRefreshLayout.SwipeToRefreshLayoutType.TOP_AND_BOTTOM);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh(SwipeToRefreshLayout.SwipeToRefreshLayoutType type) {
        switch (type) {
            case TOP:
                onItemsRefresh();
                break;
            case BOTTOM:
                onItemsLoadMore();
                break;
        }
    }

    private void onItemsLoadMore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    mStrings.add("on load more string " + i);
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeToRefreshLayout.setRefreshing(false);
                        adapter = new StringAdapter(MainActivity.this, mStrings);
                        mRecyclerView.setAdapter(adapter);
                        Toast.makeText(MainActivity.this, "上拉刷新成功！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private void onItemsRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                refreshDatas();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeToRefreshLayout.setRefreshing(false);
                        adapter = new StringAdapter(MainActivity.this, mStrings);
                        mRecyclerView.setAdapter(adapter);
                        Toast.makeText(MainActivity.this, "下拉刷新成功！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private class StringAdapter extends RecyclerView.Adapter<StringAdapter.ViewHolder> {

        private Context mContext;
        private List<String> mStrings;

        public StringAdapter(Context context, List<String> mStrings) {
            this.mContext = context;
            this.mStrings = mStrings;
        }

        @Override
        public StringAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(StringAdapter.ViewHolder holder, int position) {
            holder.mTextView.setText(mStrings.get(position));
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mTextView = (TextView) view.findViewById(R.id.text);
            }
        }

        @Override
        public int getItemCount() {
            return mStrings.size();
        }
    }

    /**
     * 模拟获取数据
     *
     * @return
     */
    public List<String> getMyStrings() {
        // 初始化数据
        List<String> Strings = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < r.nextInt(25) + 5; i++) {
            Strings.add("string in position " + i);
        }
        return Strings;
    }

    public void refreshDatas() {
        mStrings = getMyStrings();
    }

    public List<String> getDatas() {
        return mStrings;
    }
}
