package com.example.tv_remote_wifi_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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

        //Adapter will dynamically update itself using the specified layout, the ID of the layout and the array.
        devicesAdapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.listItemLayout, devicesNameList);
        devicesListView.setAdapter(devicesAdapter);

        //Initializing Discovery Manager and setting up the listener to begin the discovery process
        DiscoveryManager.init(this);
        DiscoveryManager discoveryManager = DiscoveryManager.getInstance();
        DevicePicker devicePicker = new DevicePicker(this);

        discoveryManager.start();

        devicePicker.getListView();
    }

    @Override
    public void onDeviceAdded(DiscoveryManager manager, ConnectableDevice device) {
        Log.d("Discovery", "Device found: " + device.getFriendlyName());
        String deviceId = device.getId();

        //To prevents addition of the the same device twice.
        if(devicesIDList.contains(deviceId)) {
            Log.d("Duplicate","This is a duplicate device");
        }
        else {
            devicesAdapter.add(device.getFriendlyName());
            devicesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDeviceUpdated(DiscoveryManager manager, ConnectableDevice device) {
        Log.d("Update:", "Something has changed");
    }

    //Adapter will remove the device from the arraylist in the case it goes dark
    @Override
    public void onDeviceRemoved(DiscoveryManager manager, ConnectableDevice device) {
        Log.d("Disconnection.", "Device removed: " + device.getFriendlyName());
        devicesNameList.remove(device.getFriendlyName());
        devicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDiscoveryFailed(DiscoveryManager manager, ServiceCommandError error) {
        // Handle discovery failure
        Log.e("Error", "Hmm, an error has occurred somewhere");
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
