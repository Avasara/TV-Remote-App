package com.example.tv_remote_wifi_app;

import androidx.appcompat.app.AlertDialog;
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

    //devicesListView to present the user with different options to choose from
    private ListView devicesListView;
    private ArrayList<String> devicesNameList;
    private ArrayAdapter<String> devicesAdapter;

    //deviceList array holding deviceKeys to ensure we aren't adding duplicates.
    private ArrayList<String> devicesList;

    public String selectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //This is what the user will see when a device is discovered.
        devicesListView = findViewById(R.id.devicesListView);

        //The array that will hold the deviceKey for discovered devices and their names that'll be displayed to the user.
        devicesNameList = new ArrayList<>();
        devicesList = new ArrayList<>();

        //Adapter will display devices to the user using the specified layout, the ID of the layout and the deviceName Array.
        devicesAdapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.listItemLayout, devicesNameList);
        //devicesListView.setAdapter(devicesAdapter);

        //Initializing Discovery Manager and setting up the listener to begin the discovery process
        DiscoveryManager.init(this);
        DiscoveryManager discoveryManager = DiscoveryManager.getInstance();
        discoveryManager.addListener(this);
        discoveryManager.start();
    }

    //Composite key used to prevent different services showing up as different devices.
    ConnectableDevice device = new ConnectableDevice();
    String deviceKey;

    public void devOptions() {
        //Alert dialog to show the users the list of devices
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Device");
        builder.setAdapter(devicesAdapter, (dialog, which) -> {
            //Handling device selection
            String selectedDevice = devicesNameList.get(which);
            Log.d("Selected-Device" , "The device selected is" + selectedDevice);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onDeviceAdded(DiscoveryManager manager, ConnectableDevice device) {
        //Composite key to ensure every device added is unique.
        deviceKey = device.getIpAddress() + '-' + device.getFriendlyName();

        try{
            if(devicesList.contains(deviceKey)) {
                Log.d("Device-Exists", "A device with this key already exists. IGNORING");
            }
            else {
                //If the deviceKey is not in the deviceList, then all this happens.
                devicesList.add(deviceKey);
                devicesNameList.add(device.getFriendlyName());
                devicesAdapter.notifyDataSetChanged();
                Log.d("Device-Added", "A new device was added to the list. Devicekey = " + deviceKey);
            }
        }

        catch (Exception exception) {
            Log.wtf("WhaT?!", "This was working just yesterday!");
        }

        //Calling the method that displays the options to the user.
        devOptions();
    }

    @Override
    public void onDeviceUpdated(DiscoveryManager manager, ConnectableDevice device) {
        Log.d("Updated-Device:", "Device:" + deviceKey + " has been updated");
    }

    @Override
    public void onDeviceRemoved(DiscoveryManager manager, ConnectableDevice device) {
        //Handling for device disconnection is more robust now. Accounting for devices that go off then come back on
        devicesNameList.remove(device.getFriendlyName());
        Log.d("Disconnection", "Device with key: " + deviceKey + " has been removed");
        devicesList.remove(deviceKey);
        devicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDiscoveryFailed(DiscoveryManager manager, ServiceCommandError error) {
        // Handle discovery failure
        Log.e("Error", "Hmm, an error has occurred somewhere");
    }
}