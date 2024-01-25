package com.example.tv_remote_wifi_app;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.command.ServiceCommandError;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";
    private DiscoveryManager nDiscoveryManager;
    private ConnectableDevice nDevice;

    private ListView devicesListView;
    private List<String> deviceNames = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing Discovery Manager
        DiscoveryManager.init(getApplicationContext());
        nDiscoveryManager = DiscoveryManager.getInstance();
        nDiscoveryManager.start();

        devicesListView = findViewById(R.id.devicesListView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceNames);
        devicesListView.setAdapter(arrayAdapter);

    }

    void onDeviceAdded(DiscoveryManager manager, ConnectableDevice device) {
        Log.d(TAG, "Device found: " + device.getFriendlyName());
        deviceNames.add(device.getFriendlyName());
        arrayAdapter.notifyDataSetChanged();
    }

    public void onDeviceRemoved(DiscoveryManager manager, ConnectableDevice device) {
        Log.d(TAG, "Device lost: " + device.getFriendlyName());
        deviceNames.remove(device.getFriendlyName());
        arrayAdapter.notifyDataSetChanged();
    }

    public void onDiscoveryFailed(DiscoveryManager manager, ServiceCommandError error) {
        Log.e(TAG, "Discovery failed: " + error.getMessage());
    }
}
