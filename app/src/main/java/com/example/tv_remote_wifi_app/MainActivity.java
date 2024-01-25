package com.example.tv_remote_wifi_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.discovery.DiscoveryManagerListener;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.service.command.ServiceCommandError;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DiscoveryManagerListener {

    private ListView devicesListView;
    private ArrayList<String> deviceNames;
    private ArrayAdapter<String> devicesAdapter;
    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        devicesListView = findViewById(R.id.devicesListView);
        deviceNames = new ArrayList<>();
        devicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceNames);
        devicesListView.setAdapter(devicesAdapter);

        DiscoveryManager.init(this);
        DiscoveryManager discoveryManager = DiscoveryManager.getInstance();
        discoveryManager.addListener(this);
        discoveryManager.start();
    }

    @Override
    public void onDeviceAdded(DiscoveryManager manager, ConnectableDevice device) {
        Log.d("Discovery", "Device found: " + device.getFriendlyName());
        deviceNames.add(device.getFriendlyName());
        devicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeviceUpdated(DiscoveryManager manager, ConnectableDevice device) {
        Log.d("New:", "A device was updated");
    }

    @Override
    public void onDeviceRemoved(DiscoveryManager manager, ConnectableDevice device) {
        Log.d("Disconnection.", "Device removed: " + device.getFriendlyName());
        deviceNames.remove(device.getFriendlyName());
        devicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDiscoveryFailed(DiscoveryManager manager, ServiceCommandError error) {
        // Handle discovery failure
        Log.e(TAG, "Hmm, an error has occurred somewhere");
    }
}
