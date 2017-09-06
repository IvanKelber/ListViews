package com.csc485.ivan.p2ptest;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;

    private List peers = new ArrayList<Object>();
    private HashMap<String,String> buddies = new HashMap<String,String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        registerReceiver(mReceiver, mIntentFilter);

        Button discoverButton = (Button) findViewById(R.id.discover_button);
        discoverButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("discover peers", "successful");
                    }
                    @Override
                    public void onFailure(int reason) {
                        Log.d("discover peers", "failed");
                    }
                });
                /*discoverService();
                mManager.discoverServices(mChannel,new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("discover services", "successful");

                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d("discover services", "failed: " + reason);

                    }
                });*/
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

        private WifiP2pManager mManager;
        private WifiP2pManager.Channel mChannel;
        private MainActivity mActivity;


        public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                           MainActivity activity) {
            super();
            this.mManager = manager;
            this.mChannel = channel;
            this.mActivity = activity;

        }


        private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {

            @Override
            public void onPeersAvailable(WifiP2pDeviceList peersList) {
                Log.d("onPeersAvailable", "Success");
                peers.clear();
                peers.addAll(peersList.getDeviceList());
                DeviceListView deviceListView = (DeviceListView) findViewById(R.id.list);
                deviceListView.setDevices(peers);
                deviceListView.setOnDeviceClickListener(new DeviceClickListener() {
                    @Override
                    public void onDeviceClicked(WifiP2pDevice device) {
                        WifiP2pConfig config = new WifiP2pConfig();
                        config.deviceAddress = device.deviceAddress;
                        config.wps.setup = WpsInfo.PBC;
                        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.d("Connection", "Success");
                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.d("Connection", "Failure");
                            }
                        });
                    }
                });

                for (Object s : peers) {
                    Log.d(s.toString(), "PEERS");
                }

                //Adapter View things to be implemented...
                if (peers.size() == 0) {
                    Log.d("WIFI", "No devices found.");
                }


            }
        };

        @Override
        public void onReceive(final Context context, Intent intent) {
            Log.d("onReceive", "broadcast Received");
            String action = intent.getAction();

            switch (action) {
                case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION: // Check to see if Wifi is enabled and notify activity
                    int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        // Wifi P2P is enabled
                    } else {
                        //Wifi P2P is not enabled
                    }

                    break;
                case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                    if (mManager != null) {
                        mManager.requestPeers(mChannel, peerListListener);
                    }
                    // Call WifiP2pManager.requestPeers() to get a list of current peers

                    break;
                case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION: // Respond to new connection or disconnections

                    break;
                case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION: // Respond to this device's wifi state changing

                    break;
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void discoverService() {
        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(
                    String fullDomainName, Map<String, String> record, WifiP2pDevice device) {
                Log.d("discoverService()", "DnsSdTxtRecord available" + record.toString());
                buddies.put(device.deviceAddress, record.get("buddyname"));
            }
        };
        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(
                    String instanceName, String registrationType, WifiP2pDevice device) {
                device.deviceName = buddies.containsKey(device.deviceAddress) ?
                        buddies.get(device.deviceAddress) : device.deviceName;

                //Adapter stuff...
            }
        };

        mManager.setDnsSdResponseListeners(mChannel,servListener,txtListener);

        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        Log.d("service request: ", serviceRequest.toString());
        mManager.addServiceRequest(mChannel,serviceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("addServiceRequest", "success");
            }

            @Override
            public void onFailure(int reason) {
                Log.d("addServiceRequest", "failure" + reason);
            }
        });
    }
}


