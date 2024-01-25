package com.example.tv_remote_wifi_app;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.command.ServiceCommandError;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ConnectableDeviceListener {

    private static final String TAG = "MyActivity";
    private DiscoveryManager nDiscoveryManager;
    private ConnectableDevice nDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing Discovery Manager
        DiscoveryManager.init(getApplicationContext());

        nDiscoveryManager = DiscoveryManager.getInstance();
        nDiscoveryManager.start();

    }

    @Override
    public void onDeviceAdded(DiscoveryManager manager, ConnectableDevice device) {
        Log.d(TAG, "Device found: " + device.getFriendlyName());
        // Here you can add the device to a list or UI component if needed
    }

    @Override
    public void onDeviceRemoved(DiscoveryManager manager, ConnectableDevice device) {
        Log.d(TAG, "Device lost: " + device.getFriendlyName());
        // Update your list or UI component if the device is lost
    }

    @Override
    public void onDiscoveryFailed(DiscoveryManager manager, ServiceCommandError error) {
        Log.e(TAG, "Discovery failed: " + error.getMessage());
    }
}
