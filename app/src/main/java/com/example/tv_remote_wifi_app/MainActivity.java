package com.example.tv_remote_wifi_app;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.discovery.DiscoveryManagerListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.device.ConnectableDevice;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DiscoveryManager
        DiscoveryManager.init(getApplicationContext());
        DiscoveryManager discoveryManager = DiscoveryManager.getInstance();
        discoveryManager.start();

        discoveryManager.addListener(new DiscoveryManagerListener() {
            @Override
            public void onDeviceAdded(DiscoveryManager manager, ConnectableDevice device) {
                Log.d(TAG, "We found a Device!");
            }

            @Override
            public void onDeviceUpdated(DiscoveryManager manager, ConnectableDevice device) {
                Log.d(TAG, "We made new discoveries!");
            }

            @Override
            public void onDeviceRemoved(DiscoveryManager manager, ConnectableDevice device) {
                Log.d(TAG, "A device went Dark!");
            }

            @Override
            public void onDiscoveryFailed(DiscoveryManager manager, ServiceCommandError error) {
                Log.d(TAG, "We found no devices!");
            }
        });
    }
}
