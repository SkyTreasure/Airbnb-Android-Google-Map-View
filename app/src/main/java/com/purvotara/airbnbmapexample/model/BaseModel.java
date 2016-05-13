package com.purvotara.airbnbmapexample.model;

import android.content.Context;

import com.purvotara.airbnbmapexample.controller.BaseInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by skyrreasure on 6/3/16.
 */
public abstract class BaseModel {
    transient Context mContext;
    transient BaseInterface mBaseInterface;

    /**
     * Constructor for the base model
     *
     * @param context       the application mContext
     * @param baseInterface it is the instance of baseinterface,
     *                      which is used for communication between model and the calling activity
     */
    public BaseModel(Context context, BaseInterface baseInterface) {
        mBaseInterface = baseInterface;
        mContext = context;
    }


    /**
     * Parses the response(JSONObject) and notifies the calling object based on the type of request
     *
     * @param response    Response received from the network call
     * @param requestType Type of request raised eg USER_SIGN_IN_REQUEST
     */
    public abstract void parseAndNotifyResponse(JSONObject response, int requestType) throws JSONException;

    /**
     * It is used to get the request body for the network calls.
     *
     * @param requestType Type of request raised eg USER_SIGN_IN_REQUEST
     * @return HashMap<String, Object> returns the map<String,object>
     */
    public abstract HashMap<String, Object> getRequestBodyObject(int requestType);

    /**
     * It is used to get the request body for the network calls.
     *
     * @param requestType Type of request raised eg USER_SIGN_IN_REQUEST
     * @return HashMap<String, Object> returns the map<String,object>
     */
    public abstract HashMap<String, String> getRequestBodyString(int requestType);

}
