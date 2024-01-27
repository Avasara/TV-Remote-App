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
    private ArrayList<String> devicesNameList;
    private ArrayAdapter<String> devicesAdapter;

    //Using devices unique ID to ensure we aren't adding duplicates.
    private ArrayList<String> devicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //This is what the user will see when a device is discovered.
        devicesListView = findViewById(R.id.devicesListView);

        //The array that will hold the deviceKey for discovered devices and their names that'll be displayed to the user.
        devicesNameList = new ArrayList<>();
        devicesList = new ArrayList<>();

        //Adapter will dynamically update itself using the specified layout, the ID of the layout and the array.
        devicesAdapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.listItemLayout, devicesNameList);
        devicesListView.setAdapter(devicesAdapter);

        //Initializing Discovery Manager and setting up the listener to begin the discovery process
        DiscoveryManager.init(this);
        DiscoveryManager discoveryManager = DiscoveryManager.getInstance();
        discoveryManager.addListener(this);
        discoveryManager.start();
    }

    @Override
    public void onDeviceAdded(DiscoveryManager manager, ConnectableDevice device) {
        //Composite key used to prevent different services showing up as different devices.
        String deviceKey = device.getIpAddress() + '-' + device.getFriendlyName();

        //To check and make sure that every devicekey added to devicesList is unique.
        try{
            if(devicesList.contains(deviceKey)) {
                Log.d("Device-Exists", "A device with this key already exists. IGNORING");
            }
            else {
                devicesList.add(deviceKey);
                devicesNameList.add(device.getFriendlyName());
                devicesAdapter.notifyDataSetChanged();
                Log.d("Device-Added", "A new device was added to the list." + deviceKey);
            }
        }

        catch (Exception exception) {
            Log.wtf("WhaT?!", "This was working just yesterday!");
        }
    }

    @Override
    public void onDeviceUpdated(DiscoveryManager manager, ConnectableDevice device) {
        Log.d("Updated_Device:", "Something has changed");
    }

    @Override
    public void onDeviceRemoved(DiscoveryManager manager, ConnectableDevice device) {
        Log.d("Disconnection.", "Device removed: " + device.getFriendlyName());
        devicesNameList.remove(device.getFriendlyName());
        devicesList.remove(device.getIpAddress());
        devicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDiscoveryFailed(DiscoveryManager manager, ServiceCommandError error) {
        // Handle discovery failure
        Log.e("Error", "Hmm, an error has occurred somewhere");
    }
}