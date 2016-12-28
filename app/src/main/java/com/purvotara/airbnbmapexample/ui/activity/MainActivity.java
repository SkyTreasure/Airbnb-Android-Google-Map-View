package com.purvotara.airbnbmapexample.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.purvotara.airbnbmapexample.R;
import com.purvotara.airbnbmapexample.constants.NetworkConstants;
import com.purvotara.airbnbmapexample.controller.BaseInterface;
import com.purvotara.airbnbmapexample.model.AddressModel;
import com.purvotara.airbnbmapexample.ui.adapter.MapAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by skyrreasure on 12/5/16.
 */
public class MainActivity extends AppCompatActivity implements
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener,
        SeekBar.OnSeekBarChangeListener,
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener {


    private static boolean flag = true;
    public ArrayList<Marker> mMarkerList = new ArrayList<>();
    Marker prevMarker;
    String prevVendorName;
    boolean doubleBackToExitPressedOnce = false;
    private Map<String, AddressModel> mDealMap = new HashMap<>();
    private List<AddressModel> myDealsList = new ArrayList<AddressModel>();
    private GoogleMap mMap;
    ;
    private ViewPager mViewPager;
    private MapAdapter mAdapter;
    private AddressModel mAddressModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle("Custom Markers and Viewpager");


        mMarkerList = new ArrayList<>();
        myDealsList = new ArrayList<AddressModel>();
        mDealMap = new HashMap<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        myDealsList = new ArrayList<>();


        mViewPager = (ViewPager) findViewById(R.id.vp_details);
        mViewPager.setPadding(16, 0, 16, 0);
        mViewPager.setClipToPadding(false);
        mViewPager.setPageMargin(8);

        mAdapter = new MapAdapter(getSupportFragmentManager(), myDealsList, this);
        mViewPager.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();


        mAddressModel = new AddressModel(this, new BaseInterface() {
            @Override
            public void handleNetworkCall(Object object, int requestCode) {
                if (requestCode == NetworkConstants.ADDRESS_REQUEST) {
                    if (object instanceof ArrayList) {
                        myDealsList = new ArrayList<>();
                        myDealsList = (ArrayList) object;

                        mAdapter = new MapAdapter(getSupportFragmentManager(), myDealsList, MainActivity.this);
                        mViewPager.setAdapter(mAdapter);

                        for (int j = 0; j < myDealsList.size(); j++) {
                            LatLng newLatLngTemp = new LatLng(Double.parseDouble(myDealsList.get(j).getLatitude()), Double.parseDouble(myDealsList.get(j).getLongitude()));

                            MarkerOptions options = new MarkerOptions();
                            IconGenerator iconFactory = new IconGenerator(MainActivity.this);
                            iconFactory.setRotation(0);
                            iconFactory.setBackground(null);

                            View view = View.inflate(MainActivity.this, R.layout.map_marker_text, null);
                            TextView tvVendorTitle;
                            tvVendorTitle = (TextView) view.findViewById(R.id.tv_vendor_title);
                            tvVendorTitle.setText(myDealsList.get(j).getRating());
                            iconFactory.setContentView(view);

                            options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(myDealsList.get(j).getRating())));
                            options.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
                            options.position(newLatLngTemp);
                            options.snippet(String.valueOf(j));

                            Marker mapMarker = mMap.addMarker(options);
                            mMarkerList.add(mapMarker);
                            mDealMap.put(myDealsList.get(j).getRating(), myDealsList.get(j));

                        }
                        if (myDealsList.size() > 0) {
                            LatLng latlngOne = new LatLng(Double.parseDouble(myDealsList.get(0).getLatitude()), Double.parseDouble(myDealsList.get(0).getLongitude()));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngOne, 16));
                        }

                        mAdapter.notifyDataSetChanged();


                    }
                }
            }
        });


        mViewPager.setOffscreenPageLimit(4);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (flag) {
                    flag = false;
                    final AddressModel temp = myDealsList.get(position);
                    LatLng newLatLng = new LatLng(Double.parseDouble(temp.getLatitude()), Double.parseDouble(temp.getLongitude()));
                    // map.animateCenterZoom(newLatLng, 15);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 16));
                    Marker marker = mMarkerList.get(position);


                    if (prevMarker != null) {
                        //Set prevMarker back to default color
                        IconGenerator iconFactory = new IconGenerator(MainActivity.this);
                        iconFactory.setRotation(0);
                        iconFactory.setBackground(null);
                        View view = View.inflate(MainActivity.this, R.layout.map_marker_text, null);
                        TextView tvVendorTitle;

                        tvVendorTitle = (TextView) view.findViewById(R.id.tv_vendor_title);
                        tvVendorTitle.setText(prevVendorName);
                        tvVendorTitle.setBackground(getResources().getDrawable(R.mipmap.map_pin_white));
                        tvVendorTitle.setTextColor(Color.parseColor("#0097a9"));


                        iconFactory.setContentView(view);
                        //prevVendorName = myDealsList.get(position).getmVendorName();
                        prevMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(temp.getRating())));

                    }

                    //leave Marker default color if re-click current Marker
                    if (!marker.equals(prevMarker)) {


                        IconGenerator iconFactory = new IconGenerator(MainActivity.this);
                        iconFactory.setRotation(0);
                        iconFactory.setBackground(null);
                        View view = View.inflate(MainActivity.this, R.layout.map_marker_text_active, null);
                        TextView tvVendorTitle;

                        tvVendorTitle = (TextView) view.findViewById(R.id.tv_vendor_title);
                        tvVendorTitle.setText(myDealsList.get(position).getRating());

                        iconFactory.setContentView(view);
                        //
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon()));
                        prevMarker = marker;
                        prevVendorName = myDealsList.get(position).getRating();

                    }
                    prevMarker = marker;
                    prevVendorName = myDealsList.get(position).getRating();
                    flag = true;
                } else {
                    Log.i("", "" + mMarkerList);
                    Log.i("", "" + position);
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mAddressModel.fetchAddressFromServer();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        if (flag) {
            flag = false;
            mViewPager.setVisibility(View.VISIBLE);
            String aid = marker.getId().substring(1, marker.getId().length());
            //final AddressModel temp = myDealsList.get(Integer.parseInt(marker.getSnippet()));
            // mViewPager.setCurrentItem(Integer.parseInt(marker.getSnippet()));
            final AddressModel temp = myDealsList.get(Integer.parseInt(aid));
            mViewPager.setCurrentItem(Integer.parseInt(aid));

            if (prevMarker != null) {
                //Set prevMarker back to default color
                IconGenerator iconFactory = new IconGenerator(MainActivity.this);
                iconFactory.setRotation(0);
                iconFactory.setBackground(null);
                View view = View.inflate(MainActivity.this, R.layout.map_marker_text, null);
                TextView tvVendorTitle;

                tvVendorTitle = (TextView) view.findViewById(R.id.tv_vendor_title);
                tvVendorTitle.setText(prevVendorName);
                tvVendorTitle.setBackground(getResources().getDrawable(R.mipmap.map_pin_white));
                tvVendorTitle.setTextColor(Color.parseColor("#0097a9"));

                iconFactory.setContentView(view);

                prevMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(temp.getRating())));

            }

            //leave Marker default color if re-click current Marker
            if (!marker.equals(prevMarker)) {

                IconGenerator iconFactory = new IconGenerator(MainActivity.this);
                iconFactory.setRotation(0);
                iconFactory.setBackground(null);
                View view = View.inflate(MainActivity.this, R.layout.map_marker_text, null);
                TextView tvVendorTitle;

                tvVendorTitle = (TextView) view.findViewById(R.id.tv_vendor_title);
                tvVendorTitle.setText(myDealsList.get(Integer.parseInt(marker.getSnippet())).getRating());


                tvVendorTitle.setBackground(getResources().getDrawable(R.mipmap.map_pin_green));
                tvVendorTitle.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.star_icon_white), null, null, null);
                tvVendorTitle.setTextColor(Color.parseColor("#FFFFFF"));
                iconFactory.setContentView(view);
                //
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(temp.getRating())));
                prevMarker = marker;
                prevVendorName = myDealsList.get(Integer.parseInt(marker.getSnippet())).getRating();

            }
            prevMarker = marker;
            prevVendorName = myDealsList.get(Integer.parseInt(marker.getSnippet())).getRating();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            flag = true;
        }


        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        mViewPager.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*if(id == R.id.action_home){
            Intent i=new Intent(MainActivity.this,MainActivity.class);
            startActivity(i);
            return true;
        }*/
        if (id == R.id.action_search) {
            Intent i=new Intent(MainActivity.this,SearchPlaceOnMapActivity.class);
            startActivity(i);
            return true;
        }
        if(id == R.id.action_show_direction){
            Intent i=new Intent(MainActivity.this,ShowDirectionActivity.class);
            startActivity(i);
            return true;
        }
        if(id== R.id.action_moving_marker){
            Intent i=new Intent(MainActivity.this,MovingMarkerActivity.class);
            startActivity(i);
            return true;

        }
        if(id == R.id.action_list_view){
            Intent i=new Intent(MainActivity.this,ListViewActivity.class);
            startActivity(i);
            return true;

        }
        if(id==R.id.action_lite_mode){
            Intent i=new Intent(MainActivity.this,RecyclerViewLiteModeMapActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
