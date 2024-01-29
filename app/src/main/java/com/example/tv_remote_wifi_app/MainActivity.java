package com.example.tv_remote_wifi_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;

import com.amazon.whisperlink.service.Device;
import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.discovery.DiscoveryManagerListener;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.command.ServiceCommandError;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DiscoveryManagerListener {

    private ArrayList<String> devicesNameList;
    private ArrayList<String> deviceKeyList;
    private ArrayAdapter<String> devicesAdapter;

    //deviceList array holding deviceKeys to ensure we aren't adding duplicates.
    private ArrayList<ConnectableDevice> devicesList;
    private ConnectableDeviceListener deviceListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //The array that will hold the deviceKey for discovered devices and their names that'll be displayed to the user.
        devicesNameList = new ArrayList<>();
        devicesList = new ArrayList<>();
        deviceKeyList = new ArrayList<>();

        deviceListener = new ConnectableDeviceListener() {
            @Override
            public void onDeviceReady(ConnectableDevice device) {
                Log.d("Device-Listener", "WE did it boysss");
            }

            @Override
            public void onDeviceDisconnected(ConnectableDevice device) {
                Log.d("Device-Listener" , "We disconnected from the device");
            }

            @Override
            public void onPairingRequired(ConnectableDevice device, DeviceService service, DeviceService.PairingType pairingType) {
                Log.d("Device-Listener", "We need to pair with them.");
            }

            @Override
            public void onCapabilityUpdated(ConnectableDevice device, List<String> added, List<String> removed) {
                Log.d("Device-Listener", "The device listener detected a capabilities update in: " + device.getFriendlyName());
            }

            @Override
            public void onConnectionFailed(ConnectableDevice device, ServiceCommandError error) {
                Log.e("Device-Listener", "The connection to the device failed. I'm sorry");
            }
        };

        //Adapter will display devices to the user using the specified layout, the ID of the layout and the deviceName Array.
        devicesAdapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.list_item, devicesNameList);

        //Initializing Discovery Manager and setting up the listener to begin the discovery process
        DiscoveryManager.init(this);
        DiscoveryManager discoveryManager = DiscoveryManager.getInstance();
        discoveryManager.addListener(this);
        discoveryManager.start();


        showOptions();

    }

    void connectToDevice(String deviceSelected) {
        //Attempting to connect to the device based on the name in the deviceSelected string.
        try {
            for (ConnectableDevice device : devicesList) {
                if (device.getFriendlyName().equals(deviceSelected)) {
                    //If all goes well. It connects. If not, eh
                    Log.d("Device-Connection", "Beginning connection to device: " + device.getFriendlyName());

                    //Handler to delay the connection by 5 seconds. Giving ConnectSDK enough time to add in all the services.
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.d("Device Log." , "The device is: " + device);
                                device.addListener(deviceListener);
                                device.connect();

                                //Check to see if we're connected to the device.
                                if(device.isConnected()) {
                                    Log.d("Device-Successful-Connection", "WE GOT EM LADS. WE GOT EM");
                                }
                                else {
                                    Log.d("Device-Failed-Connection","It's alright boys. we'll get em next time");
                                }
                            }
                            catch (Exception exception) {
                                Log.e("Device-Failed-Connection", "The connection failed due to: " + exception.getMessage());
                            }
                        }
                    }, 5000);
                } else {
                    Log.d("Device-Mismatch", "The selected device does not match up. DeviceName = " + device.getFriendlyName() + ". selectedDevice Name = " + deviceSelected);
                }
            }
        }
        catch (Exception exception) {
            Log.e("Device-Error", "Failed to connect to the selected device");
        }
    }

    void showOptions() {
        //Alert dialog to show the users the list of devices
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
        builder.setTitle("Select a Device");
        builder.setCancelable(false);
        builder.setAdapter(devicesAdapter, (dialog, which) -> {
            //Handling device selection
            String selectedDevice = devicesNameList.get(which);
            Log.d("Selected-Device" , "The device selected is" + selectedDevice);
            //Once a device is selected, the connectToDevice method is called on its name and it handles the rest.
            connectToDevice(selectedDevice);
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
    String deviceKey;
    @Override
    public void onDeviceAdded(DiscoveryManager manager, ConnectableDevice device) {
        //deviceKey for preventing services posing as devices.
        //Yess, now it definitely looks more encryptedy :D
        deviceKey = "://" + device.getFriendlyName() + "/:/" + device.getIpAddress() + "//:";
        device.addListener(deviceListener);

        try {
            if (deviceKeyList.contains(deviceKey)) {
                Log.d("Device-Exists", "A device with key " + deviceKey + " already exists. Adding service to Device");
                //String deviceService = device.getServiceId();
                //device.addService(deviceService);
            } else {
                //If the deviceKey is not in the list, then all this happens.
                devicesList.add(device);
                deviceKeyList.add(deviceKey);
                devicesNameList.add(device.getFriendlyName());
                devicesAdapter.notifyDataSetChanged();
                Log.d("Device-Added", "A new device was added to the list. Device: " + deviceKey);
            }
        }
        catch (Exception exception) {
            Log.e("Device-Error", "A error was caught with a device. Error: " + exception.getMessage());
        }
    }

    @Override
    public void onDeviceUpdated(DiscoveryManager manager, ConnectableDevice device) {
        Log.d("Updated-Device:", "Device:" + device.getFriendlyName() + " has been updated");
        devicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeviceRemoved(DiscoveryManager manager, ConnectableDevice device) {
        //Handling for device disconnection is more robust now. Accounting for devices that go off then come back on
        devicesNameList.remove(device.getFriendlyName());
        Log.d("Disconnection", "Device: " + device + " has been removed");
        devicesList.remove(device);
        devicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDiscoveryFailed(DiscoveryManager manager, ServiceCommandError error) {
        // Handle discovery failure
        Log.e("Error", "Hmm, an error has occurred due to: " + error.getMessage());
    }
}