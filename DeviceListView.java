package com.csc485.ivan.p2ptest;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by Ivan on 3/15/2015.
 */
public class DeviceListView extends ListView implements AdapterView.OnItemClickListener {

    private List<WifiP2pDevice> devices;
    private DeviceClickListener deviceClickListener;

    public DeviceListView(Context context) {
        super(context);
    }

    public DeviceListView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }
    public void setDevices(List<WifiP2pDevice> devices) {
        this.devices = devices;
        DeviceAdapter adapter = new DeviceAdapter(getContext(), devices);
        setAdapter(adapter);

        setOnItemClickListener(this);

    }

    public void setOnDeviceClickListener(DeviceClickListener d) {
        this.deviceClickListener = d;
    }

    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        if (deviceClickListener != null) {
            deviceClickListener.onDeviceClicked(devices.get(position));
        }
    }
}
