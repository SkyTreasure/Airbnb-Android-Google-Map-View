package com.purvotara.airbnbmapexample.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.purvotara.airbnbmapexample.R;
import com.purvotara.airbnbmapexample.model.AddressModel;
import com.purvotara.airbnbmapexample.network.util.CustomVolleyRequestQueue;


/**
 * Created by akash on 14/3/16.
 */
public class MapFragment extends Fragment {

    private static ImageLoader mImageLoader;
    int page_position;
    TextView tvTitle, tvLocation, tvCategory, tvRating, tvOffer, tvDistanceInKm, tvDescription, tvPhone;
    Button btnNavigate, btnBook;
    LinearLayout layoutItem;
    ImageView ivMain;
    private AddressModel dealModel;

    public MapFragment() {

    }

    @SuppressLint("ValidFragment")
    public MapFragment(int position, AddressModel placemodel) {
        this.page_position = position;
        this.dealModel = placemodel;
    }

    public AddressModel getDealModel() {
        return dealModel;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.deal_card_layout, container, false);
        try {
            mImageLoader = CustomVolleyRequestQueue.getInstance(getActivity()).getImageLoader();

            tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
            tvDescription = (TextView) rootView.findViewById(R.id.tv_description);

            tvTitle.setText(getDealModel().getAddressType());
            tvDescription.setText(getDealModel().getLine1() + getDealModel().getLine2());

            return rootView;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlacesMapFragment:" + e.getStackTrace());
        }

    }

}
