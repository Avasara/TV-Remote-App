package com.example.tv_remote_wifi_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
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

public class MainActivity extends AppCompatActivity implements ConnectableDeviceListener {

    private DiscoveryManager discoveryManager;
    private ConnectableDevice device;
    private ConnectableDeviceListener deviceListener;

    public ListView devicesListView;
    private ArrayList<String> devicesNameList;
    private ArrayAdapter<String> devicesAdapter;

    //Using devices unique ID to ensure we aren't adding duplicates.
    //Also suppressing the Queried but never updated warning. Cause why not ;)
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private ArrayList<String> devicesIDList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //This is what the user will see when a device is discovered.
        devicesListView = findViewById(R.id.devicesListView);

        //The arrays that will hold the names of the discovered devices and their IDs.
        devicesNameList = new ArrayList<>();
        devicesIDList = new ArrayList<>();

        //Initializing Discovery Manager to begin discovery
        DiscoveryManager.init(getApplicationContext());
        discoveryManager = DiscoveryManager.getInstance();
        discoveryManager.start();

        AdapterView.OnItemClickListener selectDevice = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                device = (ConnectableDevice) adapterView.getItemAtPosition(position);
                device.addListener(deviceListener);
                device.connect();
            }
        };

        showOptions(selectDevice);
    }

    void showOptions(AdapterView.OnItemClickListener listener) {
        DevicePicker devicePicker = new DevicePicker(this);
        AlertDialog dialog = devicePicker.getPickerDialog("Show Options", listener);
        dialog.show();
    }

    @Override
    public void onDeviceReady(ConnectableDevice device) {

    }

    @Override
    public void onDeviceDisconnected(ConnectableDevice device) {

    }

    @Override
    public void onPairingRequired(ConnectableDevice device, DeviceService service, DeviceService.PairingType pairingType) {

    }

    @Override
    public void onCapabilityUpdated(ConnectableDevice device, List<String> added, List<String> removed) {

    }

    @Override
    public void onConnectionFailed(ConnectableDevice device, ServiceCommandError error) {

    }
}
