package com.example.android.sunshine.app;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Serloman on 22/08/2015.
 */
public class ListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().compareTo("/test")==0) {
            final String message = new String(messageEvent.getData());

            broadcastMessage(message);
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }

    private void broadcastMessage(String message){
        // Broadcast message to wearable activity for display
        Intent messageIntent = new Intent();
        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(messageIntent);
    }
}
