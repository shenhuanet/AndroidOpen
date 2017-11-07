package com.shenhua.itemanimation;

import android.app.SharedElementCallback;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shenhua.itemanimation.adapter.AllAdapter;
import com.shenhua.itemanimation.adapter.DataBean;

import java.util.ArrayList;
import java.util.List;

public class AllFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private AllAdapter mAdapter;
    private List<DataBean> beanList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rv, null);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_card);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.setHasFixedSize(true);
        setUpList();
        getActivity().setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
                int bitmapWidth = Math.round(screenBounds.width());
                int bitmapHeight = Math.round(screenBounds.height());
                Bitmap bitmap = null;
                if (bitmapWidth > 0 && bitmapHeight > 0) {
                    Matrix matrix = new Matrix();
                    matrix.set(viewToGlobalMatrix);
                    matrix.postTranslate(-screenBounds.left, -screenBounds.top);
                    bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.concat(matrix);
                    sharedElement.draw(canvas);
                }
                return bitmap;
            }
        });

        return v;
    }

    private void setUpList() {
        if (beanList == null) {
            beanList = new ArrayList<DataBean>();
        }
        for (int i = 0; i < 9; i++) {
            DataBean bean = new DataBean();
            bean.setTypeName("title name" + i);
            bean.setIcon(R.drawable.img_icon);
            beanList.add(bean);
        }
        mAdapter = new AllAdapter(getActivity(), beanList);
        mRecyclerView.setAdapter(mAdapter);
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
        }
    }

}

