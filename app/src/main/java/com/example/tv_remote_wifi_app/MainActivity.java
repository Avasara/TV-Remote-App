package com.example.tv_remote_wifi_app;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.discovery.DiscoveryManagerListener;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.config.ServiceConfig;
import com.connectsdk.service.config.ServiceDescription;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DiscoveryManagerListener {

    public DiscoveryManager discoveryManager;

    //Array holding device names that will be displayed to the user.
    private ArrayList<String> devicesNameList;

    //deviceList array holding deviceKeys to ensure we aren't adding duplicates.
    private ArrayList<ConnectableDevice> devicesList;

    //Using deviceKey, it prevents the addition of duplicates.
    private ArrayList<String> deviceKeyList;

    //Making the selectedDevice available for all other methods that might have a need for it.
    private ArrayList<String> selectedDeviceList;

    //Adapter facilitates display of devices to the user using the specified layout the devicesNameList Array.
    private ArrayAdapter<String> devicesAdapter;
    private ConnectableDeviceListener deviceListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        devicesNameList = new ArrayList<>();
        devicesList = new ArrayList<>();
        deviceKeyList = new ArrayList<>();
        selectedDeviceList = new ArrayList<>();

        devicesAdapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.list_item, devicesNameList);

        //Initializing Discovery Manager and setup for Discovery
        DiscoveryManager.init(this);
        discoveryManager = DiscoveryManager.getInstance();
        discoveryManager.setPairingLevel(DiscoveryManager.PairingLevel.ON);
        discoveryManager.registerDefaultDeviceTypes();
        discoveryManager.addListener(MainActivity.this);
        DiscoveryManager.getInstance().start();

        Log.d("Device-Pairing-Level", "The device pairing level is: " + discoveryManager.getPairingLevel());

        showOptions();

        deviceListener = new ConnectableDeviceListener() {
            @Override
            public void onDeviceReady(ConnectableDevice connectableDevice) {

            }

            @Override
            public void onDeviceDisconnected(ConnectableDevice connectableDevice) {

            }

            @Override
            public void onPairingRequired(ConnectableDevice connectableDevice, DeviceService deviceService, DeviceService.PairingType pairingType) {

            }

            @Override
            public void onCapabilityUpdated(ConnectableDevice connectableDevice, List<String> list, List<String> list1) {
                Log.d("Device-Listener", "The device was updated in capabilities");
            }

            @Override
            public void onConnectionFailed(ConnectableDevice connectableDevice, ServiceCommandError serviceCommandError) {

            }
        };

    }

    void connectToDevice(String deviceSelected) {
        //Attempting connection based on the name in the deviceSelected string.
        try {
            for (ConnectableDevice device : devicesList) {
                if (device.getFriendlyName().equals(deviceSelected)) {
                    Log.d("Device-ConnectToDevice", "Beginning connection to device: " + device.getFriendlyName());

                    //Handler delaying the connection by 15 seconds. Giving ConnectSDK enough time to add in all the services.
                    //If adding the services myself works better, I might just remove this handler.
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        try {
                            Log.d("Device-connectToDevice" , "The device is: " + device);
                            device.addListener(deviceListener);
                            device.connect();
                        }
                        catch (Exception exception) {
                            Log.e("Device-connectToDevice-Error", "The connection failed due to: " + exception.getMessage());
                        }
                    }, 15000);
                } else {
                    Log.d("Device-ConnectToDevice", "The selected device does not match up. DeviceName = " + device.getFriendlyName() + ". selectedDevice Name = " + deviceSelected);
                }
            }
        }
        catch (Exception exception) {
            Log.e("Device-connectToDevice-Error", "Failed to connect to the selected device");
        }
    }

    void showOptions() {
        //Displaying list of devices to users.
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
        builder.setTitle("Select a Device");
        builder.setCancelable(false);
        builder.setPositiveButton("Refresh", (dialog, dev) -> {
            discoveryManager.start();
            builder.show();
        });

        builder.setAdapter(devicesAdapter, (dialog, dev) -> {

            //Getting the selected device, updating the array and connecting to it.
            String selectedDevice = devicesNameList.get(dev);
            selectedDeviceList.add(selectedDevice);
            Log.d("Selected-Device" , "The device selected is" + selectedDevice);
            connectToDevice(selectedDevice);
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //Pairing Alert dialog prompting the user to accept the connection on their tv.
    void pairingAlertDialog() {
        AlertDialog.Builder pairingAlertDialog = new AlertDialog.Builder(this);
        pairingAlertDialog.setTitle("Pairing with TV");
        pairingAlertDialog.setMessage("Please confirm your selection on your TV");
        pairingAlertDialog.setPositiveButton("Okay", null);

        //This is lambda. Basically shortens the code? I'll have to learn more about that. It's very neat.
        pairingAlertDialog.setNegativeButton("Nope", (dialog, which) -> dialog.dismiss());
        pairingAlertDialog.show(); //Does both create and show in one.
    }

    @Override
    public void onDeviceAdded(DiscoveryManager manager, ConnectableDevice device) {
        //deviceKey for preventing services posing as devices.
        String deviceKey = "://" + device.getFriendlyName() + "/:/" + device.getIpAddress() + "//:";

        //Making the discovered "device" into a JSON Object
        JSONObject deviceJSONObject = device.toJSONObject();

        Log.d("Device-JsonObject" , "The device object is: " + deviceJSONObject);

        if (deviceKeyList.contains(deviceKey)) {
            Log.d("Device-KeyExists", "A device with key " + deviceKey + " already exists. Adding service to Device");

            //String to hold the required JSON object name.
            String servicesString = "services";

            JSONObject servicesJSONObject = null;
            try {
                servicesJSONObject = deviceJSONObject.getJSONObject(servicesString);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            //Looping through the servicesJSONObject to find the of the service.
           for(int i = 0; i<servicesJSONObject.names().length(); i++){
               String serviceName = null;
               try {
                   serviceName = servicesJSONObject.names().getString(i);
               } catch (JSONException e) {
                   throw new RuntimeException(e);
               }

               Log.d("Device-Service" , "The service is: " + serviceName);

               //todo: So right now this service addition is a massive headache.
               //todo: The plan now is to create a new device service, assign all the parameters of the service we found,
               //todo: so that's the config and the description we saw earlier in the JSON Object and then use that to create it.
               //todo: Once that is done, we will then add that to the device. Simple right?

               //Service config set to the uuid we uncovered from the JSON object.
               ServiceConfig serviceConfig = new ServiceConfig(serviceName);

               //Service Description. We can pass in the device JSON object as our parameter since the class accepts it
               ServiceDescription serviceDescription  = new ServiceDescription(deviceJSONObject);

               //New device service made using the service Config and description.
               DeviceService service = new DeviceService(serviceDescription, serviceConfig);

               Log.d("Device-Service", "The current device service is: " + device.getServices());
               Log.d("Device-Service", "The new service is: " + service.getServiceName());

               device.addListener(deviceListener);
               device.addService(service);

               Log.d("Device-Service", "The current services owned by the device is: " + device.getServices());

           }

        }
        else {
           devicesList.add(device);
           deviceKeyList.add(deviceKey);
           devicesNameList.add(device.getFriendlyName());
           devicesAdapter.notifyDataSetChanged();
           Log.d("Device-Added", "A new device was added to the list. Device: " + deviceKey);
        }
    }

    @Override
    public void onDeviceUpdated(DiscoveryManager manager, ConnectableDevice device) {
        devicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeviceRemoved(DiscoveryManager manager, ConnectableDevice device) {
        devicesNameList.remove(device.getFriendlyName());
        Log.d("Disconnection", "Device: " + device + " has been removed");
        devicesList.remove(device);
        devicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDiscoveryFailed(DiscoveryManager manager, ServiceCommandError error) {
        Log.e("Error", "Hmm, an error has occurred due to: " + error.getMessage());
    }
}