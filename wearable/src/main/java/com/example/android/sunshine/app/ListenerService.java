package com.example.android.sunshine.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Serloman on 22/08/2015.
 */
public class ListenerService extends WearableListenerService {

    private final static String TAG = "ListenerService";
    private GoogleApiClient mGoogleClient;

    public final static String ARG_MAX_TEMP = "max_temp";
    public final static String ARG_MIN_TEMP = "min_temp";
    public final static String ARG_WEATHER_ICON = "weather_icon";
    public final static String ARG_TIMESTAMP = "timestamp";

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleClient = new GoogleApiClient.Builder(this).addApiIfAvailable(Wearable.API).build();
        mGoogleClient.connect();
    }

    @Override
    public void onDestroy() {
        if (null != mGoogleClient && mGoogleClient.isConnected())
            mGoogleClient.disconnect();

        super.onDestroy();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for(DataEvent dataEvent : dataEvents){
            if(dataEvent.getType() == DataEvent.TYPE_CHANGED){
                DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                String path = dataEvent.getDataItem().getUri().getPath();
                if(path.compareTo("/shine")==0){
                    double max = dataMap.getDouble(ARG_MAX_TEMP);
                    double min = dataMap.getDouble(ARG_MIN_TEMP);
                    long time = dataMap.getLong(ARG_TIMESTAMP);

                    Asset iconAsset = dataMap.getAsset(ARG_WEATHER_ICON);
                    Bitmap icon = loadBitmapFromAsset(iconAsset);

                    broadcastMessage(max, min, icon, time);
                }
            }
        }
    }

    private void broadcastMessage(double max, double min, Bitmap icon, Long timestamp){
        // Broadcast message to wearable activity for display
        Intent messageIntent = new Intent();

        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra(ARG_MAX_TEMP, max);
        messageIntent.putExtra(ARG_MIN_TEMP, min);
        messageIntent.putExtra(ARG_TIMESTAMP, timestamp);
        messageIntent.putExtra(ARG_WEATHER_ICON, icon);

        LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(messageIntent);
    }

    // From https://developer.android.com/training/wearables/data-layer/assets.html
    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result = mGoogleClient.blockingConnect(1000, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleClient, asset).await().getInputStream();
        mGoogleClient.disconnect();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }
}
