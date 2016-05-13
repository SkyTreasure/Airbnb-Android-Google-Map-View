package com.purvotara.airbnbmapexample.model;

import android.content.Context;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.purvotara.airbnbmapexample.constants.NetworkConstants;
import com.purvotara.airbnbmapexample.controller.BaseInterface;
import com.purvotara.airbnbmapexample.network.BaseNetwork;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by skytreasure on 15/4/16.
 */
public class AddressModel extends BaseModel {

    @SerializedName("address_id")
    @Expose
    private Integer addressId;
    @SerializedName("mPincode")
    @Expose
    private String mPincode;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("line_1")
    @Expose
    private String line1;
    @SerializedName("line_2")
    @Expose
    private String line2;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("address_type")
    @Expose
    private String addressType;
    @SerializedName("default")
    @Expose
    private Boolean _default;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("distance")
    @Expose
    private String distance;


    /**
     * Constructor for the base model
     *
     * @param context       the application mContext
     * @param baseInterface it is the instance of baseinterface,
     */
    public AddressModel(Context context, BaseInterface baseInterface) {
        super(context, baseInterface);
    }

    public void fetchAddressFromServer() {
        BaseNetwork baseNetwork = new BaseNetwork(mContext, this);
        baseNetwork.getJSONObjectForRequest(Request.Method.GET, NetworkConstants.ADDRESS_URL, null, NetworkConstants.ADDRESS_REQUEST);

    }

    @Override
    public void parseAndNotifyResponse(JSONObject response, int requestType) throws JSONException {
        try {
            // boolean error = response.getBoolean(NetworkConstants.ERROR);
            Gson gson = new Gson();
            switch (requestType) {

                case NetworkConstants.ADDRESS_REQUEST:
                    JSONArray addressArray = response.getJSONArray(NetworkConstants.ADDRESS);

                    List<AddressModel> addressList = gson.fromJson(addressArray.toString(),
                            new TypeToken<List<AddressModel>>() {
                            }.getType());

                    mBaseInterface.handleNetworkCall(addressList, requestType);
                    break;
            }
        } catch (Exception e) {
            mBaseInterface.handleNetworkCall(e.getMessage(), requestType);
        }

    }

    @Override
    public HashMap<String, Object> getRequestBodyObject(int requestType) {
        return null;
    }

    @Override
    public HashMap<String, String> getRequestBodyString(int requestType) {
        return null;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getmPincode() {
        return mPincode;
    }

    public void setmPincode(String mPincode) {
        this.mPincode = mPincode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public Boolean get_default() {
        return _default;
    }

    public void set_default(Boolean _default) {
        this._default = _default;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
