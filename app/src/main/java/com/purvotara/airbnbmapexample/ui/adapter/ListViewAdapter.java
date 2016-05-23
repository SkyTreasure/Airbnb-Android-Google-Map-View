package com.purvotara.airbnbmapexample.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.purvotara.airbnbmapexample.R;
import com.purvotara.airbnbmapexample.constants.NetworkConstants;
import com.purvotara.airbnbmapexample.model.AddressModel;
import com.purvotara.airbnbmapexample.network.util.CustomVolleyRequestQueue;
import com.purvotara.airbnbmapexample.ui.widget.MyAnimationUtils;

import java.util.List;

/**
 * Created by akash on 15/5/16.
 */
public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ViewHolder>  {
    public static Context mContext;
    private static ImageLoader mImageLoader;
    private static AddressModel mPlaceModel;
    LayoutInflater layoutinflater;
    private List<AddressModel> mPlaceList;
    private Activity mActivity;
    private ImageView mImageView;
    private int lastPosition = -1;
    int previousPosition=0;

    public ListViewAdapter(List<AddressModel> places, Context context) {
        mContext = context;
        this.mPlaceList = places;
        layoutinflater = LayoutInflater.from(context);
        mActivity = (Activity) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.deal_card_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mPlaceModel = mPlaceList.get(position);
        mImageLoader = CustomVolleyRequestQueue.getInstance(mActivity).getImageLoader();

        holder.tvTitle.setText(mPlaceList.get(position).getLine1());
        holder.tvCategories.setText(mPlaceList.get(position).getLine2());
        holder.tvOffers.setText(mPlaceList.get(position).getCity());
        holder.tvDistanceInKm.setText(mPlaceList.get(position).getDistance());
        holder.tvRating.setText(mPlaceList.get(position).getRating());

        setImageViewFromUrl( mPlaceList.get(position).getImage(), holder.ivMain);

        if(position>previousPosition){
            new MyAnimationUtils().animate(holder,true);
        }else{
            new MyAnimationUtils().animate(holder,false);
        }

        previousPosition=position;
    }

    public void setImageViewFromUrl(String url, ImageView imageView) {
        mImageLoader.get(url, ImageLoader.getImageListener(imageView, 0, 0));
        imageView.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return mPlaceList == null ? 0 : mPlaceList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle, tvRating, tvDistanceInKm, tvOffers, tvLocation, tvCategories;
        public ImageView ivMain;
        public Button mBook, mPay;
        private LinearLayout mItemLayout;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            tvTitle = (TextView) itemLayoutView.findViewById(R.id.tv_title);
            tvLocation = (TextView) itemLayoutView.findViewById(R.id.tv_location);
            tvCategories = (TextView) itemLayoutView.findViewById(R.id.tv_categories);
            tvRating = (TextView) itemLayoutView.findViewById(R.id.tv_rating);
            tvDistanceInKm = (TextView) itemLayoutView.findViewById(R.id.tv_distanceinkm);
            tvOffers = (TextView) itemLayoutView.findViewById(R.id.tv_offers);
            ivMain = (ImageView) itemLayoutView.findViewById(R.id.iv_main);
            mBook = (Button) itemLayoutView.findViewById(R.id.btn_book);
            mPay = (Button) itemLayoutView.findViewById(R.id.btn_pay);



        }


    }
}
