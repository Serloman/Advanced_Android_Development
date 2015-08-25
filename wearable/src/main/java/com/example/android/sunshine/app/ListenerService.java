package com.example.android.sunshine.app;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Date;

/**
 * Created by Serloman on 22/08/2015.
 */
public class ListenerService extends WearableListenerService {

    public final static String ARG_MAX_TEMP = "max_temp";
    public final static String ARG_MIN_TEMP = "min_temp";
    public final static String ARG_WEATHER_ID = "weather_id";
    public final static String ARG_TIMESTAMP = "timestamp";

/** /
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().compareTo("/shine")==0) {
            final String message = new String(messageEvent.getData());

            broadcastMessage(message, new Date().getTime());
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }
/**/

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for(DataEvent dataEvent : dataEvents){
            if(dataEvent.getType() == DataEvent.TYPE_CHANGED){
                DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                String path = dataEvent.getDataItem().getUri().getPath();
                if(path.compareTo("/shine")==0){
                    double max = dataMap.getDouble(ARG_MAX_TEMP);
                    double min = dataMap.getDouble(ARG_MIN_TEMP);
                    int weatherId = dataMap.getInt(ARG_WEATHER_ID);
                    long time = dataMap.getLong(ARG_TIMESTAMP);

                    broadcastMessage(max, min, weatherId, System.currentTimeMillis());
                }
            }
        }
    }

    private void broadcastMessage(double max, double min, int weatherId, Long timestamp){
        // Broadcast message to wearable activity for display
        Intent messageIntent = new Intent();
        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra(ARG_MAX_TEMP, max);
        messageIntent.putExtra(ARG_MIN_TEMP, min);
        messageIntent.putExtra(ARG_WEATHER_ID, weatherId);
        messageIntent.putExtra(ARG_TIMESTAMP, timestamp);
        LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(messageIntent);
    }
}
