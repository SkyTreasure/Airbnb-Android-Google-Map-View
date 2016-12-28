package com.purvotara.airbnbmapexample.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.purvotara.airbnbmapexample.R;
import com.purvotara.airbnbmapexample.model.AddressModel;
import com.purvotara.airbnbmapexample.network.util.CustomVolleyRequestQueue;
import com.purvotara.airbnbmapexample.ui.widget.MyAnimationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akash on 28/12/16.
 */

public class RecyclerViewLiteModeAdapter extends RecyclerView.Adapter<RecyclerViewLiteModeAdapter.ViewHolder>  {

    public static Context mContext;
    LayoutInflater layoutinflater;
    private List<AddressModel> addressModels=new ArrayList<>();
    private Activity mActivity;
    private int lastPosition = -1;
    private AddressModel mAddressModel;
    private static ViewHolder lastSelectedViewHolder;
    int previousPosition=0;

    public RecyclerViewLiteModeAdapter(List<AddressModel> addressModelList, Context context) {
        mContext = context;
        this.addressModels = addressModelList;
        layoutinflater = LayoutInflater.from(context);
        mActivity = (Activity) context;
    }

    @Override
    public RecyclerViewLiteModeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lite_mode_layout, parent, false);
        RecyclerViewLiteModeAdapter.ViewHolder viewHolder = new RecyclerViewLiteModeAdapter.ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.initializeMapView(position,addressModels.get(position));

        holder.tvTitle.setText(addressModels.get(position).getLine1());
        holder.tvCategories.setText(addressModels.get(position).getLine2());
        holder.tvOffers.setText(addressModels.get(position).getCity());
        holder.tvDistanceInKm.setText(addressModels.get(position).getDistance());
        holder.tvRating.setText(addressModels.get(position).getRating());

        if(position>previousPosition){
            new MyAnimationUtils().animate(holder,true);
        }else{
            new MyAnimationUtils().animate(holder,false);
        }

        previousPosition=position;
    }



    @Override
    public int getItemCount() {
        return addressModels == null ? 0 : addressModels.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder  implements OnMapReadyCallback

    {

        public TextView tvTitle, tvRating, tvDistanceInKm, tvOffers, tvLocation, tvCategories;
        public ImageView ivMain;
        public Button mBook, mPay;
        private LinearLayout mItemLayout;

        GoogleMap map;
        MapView mapView;
        boolean isMapReady=false;
        ViewHolder.MapInterface mapInterface;
        int position;
        AddressModel addressModel;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            mapView=(MapView) itemLayoutView.findViewById(R.id.lite_map);
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

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(mContext);
            map = googleMap;
            isMapReady=true;
            map.getUiSettings().setMapToolbarEnabled(false);

            if (mapView.getViewTreeObserver().isAlive()) {
                mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation") // We use the new method when supported
                    @SuppressLint("NewApi") // We check which build version we are using.
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        //  showBoth(null);
                    }
                });
            }
            try{
                setMapData(new LatLng(Double.parseDouble(addressModel.getLatitude()),Double.parseDouble(addressModel.getLongitude())));
            }catch(Exception e){
                e.printStackTrace();
            }


            //  mapInterface.mapIsReadyToSet(this,position);
        }

        public void setMapData(LatLng newLatLng){
            if(map!=null){
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 13f));
                map.addMarker(new MarkerOptions().position(newLatLng));
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }else{
                Log.e("Map","Map null");
            }
        }

        public void initializeMapView(int pos,AddressModel address) {
            try{
                position=pos;
                addressModel=address;
                if (mapView != null) {
                    // Initialise the MapView
                    mapView.onCreate(null);
                    // Set the map ready callback to receive the GoogleMap object
                    mapView.getMapAsync(this);

                }else{
                    Log.e("Map","Mapview null");
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        public interface MapInterface{
            public void mapIsReadyToSet(ViewHolder holder, int pos);
        }

    }
}
