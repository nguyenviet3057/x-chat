package com.planx.xchat.service;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import com.planx.xchat.interfaces.NetworkChangeListener;

public class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {

    private NetworkChangeListener networkChangeListener;

    public NetworkCallbackImpl(NetworkChangeListener networkChangeListener) {
        this.networkChangeListener = networkChangeListener;
    }

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        networkChangeListener.onNetworkChange(true);
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
        networkChangeListener.onNetworkChange(false);
    }

//    @Override
//    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
//        super.onCapabilitiesChanged(network, networkCapabilities);
//        boolean isConnected = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
//        networkChangeListener.onNetworkChange(isConnected);
//    }
}
