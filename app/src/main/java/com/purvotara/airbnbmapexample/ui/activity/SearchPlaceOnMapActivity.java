package com.purvotara.airbnbmapexample.ui.activity;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.purvotara.airbnbmapexample.R;
import com.purvotara.airbnbmapexample.constants.NetworkConstants;
import com.purvotara.airbnbmapexample.ui.adapter.PlaceAutocompleteAdapter;
import com.purvotara.airbnbmapexample.ui.util.PermissionUtils;
import com.purvotara.airbnbmapexample.ui.widget.TouchableWrapper;
/**
 * Created by skyrreasure on 12/5/16.
 */
public class SearchPlaceOnMapActivity  extends AppCompatActivity implements
        GoogleMap.OnMarkerClickListener, TouchableWrapper.UpdateMapAfterUserInterection,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener,
        SeekBar.OnSeekBarChangeListener,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowLongClickListener,
        GoogleMap.OnInfoWindowCloseListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnCameraChangeListener,
        ActivityCompat.OnRequestPermissionsResultCallback  ,GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    //This is for restriciting search only to india, for other countries you can specify lat lng by changing the below values.
     private static final LatLngBounds BOUNDS_GREATER_INDIA = new LatLngBounds(
            new LatLng(8.062148, 68.212642), new LatLng(37.372499, 96.513423));

    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private String mLatitude,mLongitude;
    private GoogleMap mMap;
    private boolean mPermissionDenied = false;
    private Toolbar mToolbar;
    private FloatingActionButton fabAdd ;
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {

        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            hideKeyboard();
            mLatitude=String.valueOf(place.getLatLng().latitude);
            mLongitude=String.valueOf(place.getLatLng().longitude);
            LatLng newLatLngTemp = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
            // LatLng centerLatLng=new LatLng(mMap.getCameraPosition().target.latitude,mMap.getCameraPosition().target.longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLngTemp, 15f));
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);


            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place_on_map);

        getSupportActionBar().setTitle("Search Places");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fabAdd= (FloatingActionButton) findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(this);

        mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.googleplacesearch);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        mAdapter = new PlaceAutocompleteAdapter(this, R.layout.google_places_search_items,
                mGoogleApiClient, null, null);

        //TODO:In order to Restrict search to India uncomment this and comment the above line
        /*
        mAdapter = new PlaceAutocompleteAdapter(this, R.layout.google_places_search_items,
                mGoogleApiClient, BOUNDS_GREATER_INDIA, null);
         */
        mAutocompleteView.setAdapter(mAdapter);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        mAutocompleteView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mAutocompleteView.getRight() - mAutocompleteView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        mAutocompleteView.setText("");

                        return true;
                    }
                }
                if (event.getRawX() <= (mAutocompleteView.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {

                    Intent i = new Intent(SearchPlaceOnMapActivity.this, MainActivity.class);
                    startActivity(i);

                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

        }
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onInfoWindowClose(Marker marker) {

    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnCameraChangeListener(this);
        enableMyLocation();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
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
    public boolean onMyLocationButtonClick() {
        return false;
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
    public void onUpdateMapAfterUserInterection() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_add:
                mLatitude=mMap.getCameraPosition().target.latitude+"";
                mLongitude=mMap.getCameraPosition().target.longitude+"";

                Toast.makeText(SearchPlaceOnMapActivity.this, "Latitude:"+mLatitude +" Longitude:"+mLongitude,Toast.LENGTH_LONG).show();

                break;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
