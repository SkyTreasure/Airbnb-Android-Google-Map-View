package com.purvotara.airbnbmapexample.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.purvotara.airbnbmapexample.constants.NetworkConstants;
import com.purvotara.airbnbmapexample.model.BaseModel;
import com.purvotara.airbnbmapexample.network.util.CustomVolleyRequestQueue;
import com.purvotara.airbnbmapexample.network.util.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SkyTreasure on 02-01-2016.
 */
public class BaseNetwork {
    private Context mContext;
    private BaseModel mBaseModel;

    public BaseNetwork(Context context, BaseModel baseModel) {
        mContext = context;
        mBaseModel = baseModel;
    }

    /**
     * Generates the generic request header
     *
     * @return RequestHeader HashMap<String,String>
     */
    public HashMap<String, String> getRequestHeaderForAuthorization() {
        HashMap<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Accept", "*/*");
        return requestHeader;
    }

    /**
     * Handle response(JSONObject) as per requirement
     *
     * @param response    The network call response in the form of JSONObject
     * @param requestType The network Request Type
     */
    void handleResponse(JSONObject response, int requestType) throws JSONException {
        try {

            mBaseModel.parseAndNotifyResponse(response, requestType);

        } catch (Exception e) {
            //((BaseActivity) mContext).hideProgressDialog();
            Toast.makeText(mContext, response.getString(NetworkConstants.ERROR_MESSAGE), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * Handle all errors
     *
     * @param error Error generated due to network call
     */
    void handleError(VolleyError error) {

        // ((BaseActivity) mContext).hideProgressDialog();
        String errorMessage = null;
        try {
            errorMessage = new String(error.networkResponse.data, "UTF-8");
            JSONObject jsonObject = new JSONObject(errorMessage);
            JSONArray errorArray = new JSONArray(jsonObject.get("errors").toString());
            JSONObject firstError = new JSONObject(errorArray.get(0).toString());
            Log.d("Error", firstError.get("message").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_LONG).show();

    }

    /**
     * For Post Method with parameters in the content body
     *
     * @param methodType   Type of network call eg, GET,POST, etc.
     * @param url          The url to hit
     * @param paramsObject JsonObject for POST request, null for GET request
     * @param requestType  Type of Network request
     */
    public void getJSONObjectForRequest(int methodType, String url, JSONObject paramsObject, final int requestType) {
        if (NetworkUtil.isInternetConnected(mContext)) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (methodType, url, paramsObject, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                handleResponse(response, requestType);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handleError(error);
                        }
                    }) {

                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return getRequestHeaderForAuthorization();
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    return params;
                }
            };
            int socketTimeout = 5000;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsObjRequest.setRetryPolicy(policy);
            CustomVolleyRequestQueue.getInstance(mContext).getRequestQueue().add(jsObjRequest);
        }
    }

    /**
     * For Post Method with parameters as form data
     *
     * @param methodType
     * @param url
     * @param paramsObject
     * @param requestType
     */
    public void getHashMapForRequest(int methodType, String url, final HashMap<String, String> paramsObject, final int requestType) {
        if (NetworkUtil.isInternetConnected(mContext)) {
            StringRequest jsonObjRequest = new StringRequest(methodType, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                handleResponse(new JSONObject(response), requestType);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    handleError(error);
                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";

                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    return paramsObject;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return getRequestHeaderForAuthorization();
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    if (response.headers == null) {
                        // cant just set a new empty map because the member is final.
                        response = new NetworkResponse(
                                response.statusCode,
                                response.data,
                                Collections.<String, String>emptyMap(), // this is the important line, set an empty but non-null map.
                                response.notModified,
                                response.networkTimeMs);


                    }

                    return super.parseNetworkResponse(response);
                }

            };
            int socketTimeout = 5000;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjRequest.setRetryPolicy(policy);
            CustomVolleyRequestQueue.getInstance(mContext).getRequestQueue().add(jsonObjRequest);
        }
    }

}
