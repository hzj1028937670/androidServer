package com.android.server.util.server;

public interface OnServerChangeListener {

    void onServerStarted(String ipAddress);

    void onServerStopped();

    void onServerError(String errorMessage);

}
