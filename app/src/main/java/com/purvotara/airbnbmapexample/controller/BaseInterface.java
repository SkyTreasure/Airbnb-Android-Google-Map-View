package com.purvotara.airbnbmapexample.controller;

/**
 * Created by skytreasure on 06/03/16.<br/>
 * <p/>
 * This is the baseinterface, this should be implemented in all the Activities where network calls are made
 */
public interface BaseInterface {

    /**
     * @param object
     * @param requestCode
     */
    void handleNetworkCall(Object object, int requestCode);

}
