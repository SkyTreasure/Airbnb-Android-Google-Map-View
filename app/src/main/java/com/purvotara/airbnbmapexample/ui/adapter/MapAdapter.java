package com.purvotara.airbnbmapexample.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;

import com.purvotara.airbnbmapexample.model.AddressModel;
import com.purvotara.airbnbmapexample.ui.fragment.MapFragment;

import java.util.List;

/**
 * Created by skyrreasure on 14/3/16.
 */
public class MapAdapter extends FragmentPagerAdapter {
    LayoutInflater layoutInflater;
    List<AddressModel> deals;
    Context mContext;

    public MapAdapter(FragmentManager fm, List<AddressModel> deals, Context context) {
        super(fm);
        layoutInflater = LayoutInflater.from(context);
        this.deals = deals;
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return new MapFragment(position, deals.get(position));
    }

    @Override
    public int getCount() {
        return this.deals.size();
    }

    @Override
    public float getPageWidth(int position) {
        return 0.95f;
    }
}

