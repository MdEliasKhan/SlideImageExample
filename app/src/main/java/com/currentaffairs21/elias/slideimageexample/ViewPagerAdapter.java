package com.currentaffairs21.elias.slideimageexample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private List<HotOffer> hotOfferList;

    ViewPagerAdapter(Context context,List<HotOffer> hotOfferList) {
        this.context = context;
        this.hotOfferList = hotOfferList;
    }

    @Override
    public int getCount() {
        return hotOfferList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
       final String post_id = hotOfferList.get(position).hotOffer_id;
        ImageView imageView = new ImageView(context);
        Picasso.get()
                .load(hotOfferList.get(position).getHotPost_Image())
                .fit()
                .centerCrop()
                .into(imageView);
        container.addView(imageView);
        container.setScrollbarFadingEnabled(true);
        container.setScrollBarFadeDuration(1);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+post_id, Toast.LENGTH_SHORT).show();
            }
        });

        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
