package com.android.kevin.shuizu.utils;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.kevin.shuizu.R;
import com.android.kevin.shuizu.entities.YYZJInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.android.kevin.shuizu.entities.NetDataKt.ROOT_URL;

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/20/020.
 */
public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<YYZJInfo> mData;
    private float mBaseElevation;
    private OnCallDetails onCallDetails;

    public CardPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void setSource(ArrayList<YYZJInfo> data) {
        for (int i = 0; i < data.size(); i++) {
            mViews.add(null);
        }
        mData.addAll(data);
    }

    public OnCallDetails getOnCallDetails() {
        return onCallDetails;
    }

    public void setOnCallDetails(OnCallDetails onCallDetails) {
        this.onCallDetails = onCallDetails;
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.layout_yyzj_list_item, container, false);
        container.addView(view);
        bind(mData.get(position), view);
        CardView cardView = view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(final YYZJInfo item, final View view) {
        TextView name = view.findViewById(R.id.zjName);
        TextView content = view.findViewById(R.id.zjContent);
        ImageView zjImage = view.findViewById(R.id.zjImage);
        TextView zjDetails = view.findViewById(R.id.zjDetails);
        TextView zjCall = view.findViewById(R.id.zjCall);
        String[] bqs = item.getTags().split(",");
        name.setText(item.getTitle());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bqs.length; i++) {
            if (i != 0) {
                sb.append("\n");
            }
            sb.append(bqs[i]);
        }
        content.setText(sb.toString());
        Picasso.with(view.getContext()).load(getImageUrl(item.getFile_url())).into(zjImage);
        zjDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onCallDetails) {
                    onCallDetails.onCallDetails(item,view);
                }
            }
        });
        zjCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onCallDetails) {
                    onCallDetails.onCallZJ(item,view);
                }
            }
        });
    }

    private String getImageUrl(String url) {
        return url.contains("http") ? url : ROOT_URL + "/" + url;
    }

    public interface OnCallDetails {
        void onCallDetails(YYZJInfo item,View view);

        void onCallZJ(YYZJInfo item,View view);
    }
}

