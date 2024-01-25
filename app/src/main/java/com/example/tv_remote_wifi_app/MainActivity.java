package com.example.tv_remote_wifi_app;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;

import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.device.ConnectableDevice;

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

    private void showImage() {
        DevicePicker devicePicker = new DevicePicker(this);
        AlertDialog dialog = devicePicker.getPickerDialog("Show Image", selectDevice);
        dialog.show();
    }
}
