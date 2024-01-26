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

    ConnectableDeviceListener deviceListener;
    DevicePicker devicePicker = new DevicePicker(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing Discovery Manager to begin discovery
        DiscoveryManager.init(getApplicationContext());
        DiscoveryManager discoveryManager = DiscoveryManager.getInstance();
        discoveryManager.start();

        //Initializing deviceListener
        ConnectableDevice deviceListener = new ConnectableDevice() {

        };

        showOptions(selectDevice);

    }

    AdapterView.OnItemClickListener selectDevice = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            ConnectableDevice smartDevice = (ConnectableDevice) adapterView.getItemAtPosition(position);
            smartDevice.addListener(deviceListener);
            devicePicker.pickDevice(smartDevice);
            smartDevice.connect();
            Log.d("Picked", "The device:" + smartDevice.getFriendlyName() + " was successfully picked.");
        }
    };


    void showOptions(AdapterView.OnItemClickListener listener) {
        AlertDialog dialog = devicePicker.getPickerDialog("Show Options", listener);
        dialog.show();
    }

    @Override
    public void onDeviceReady(ConnectableDevice device) {
        Log.d("Device Ready", "A device is ready to be connected to");
    }

    @Override
    public void onDeviceDisconnected(ConnectableDevice device) {
        Log.d("Disconnected", "A device was disconnected");
    }

    @Override
    public void onPairingRequired(ConnectableDevice device, DeviceService service, DeviceService.PairingType pairingType) {
        Log.d("Pairing Required", "Pairing is required to interact with this device");
    }

    @Override
    public void onCapabilityUpdated(ConnectableDevice device, List<String> added, List<String> removed) {
        Log.d("Update", "A devices capability was updated");
    }

    @Override
    public void onConnectionFailed(ConnectableDevice device, ServiceCommandError error) {
        Log.d("Connection Failed", "The connection to the TV failed");
    }
}
