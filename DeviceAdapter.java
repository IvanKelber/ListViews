package com.csc485.ivan.p2ptest;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ivan on 3/15/2015.
 */
public class DeviceAdapter extends BaseAdapter {

    private List<WifiP2pDevice> devices;

    private LayoutInflater layoutInflater;

    public DeviceAdapter(Context context, List<WifiP2pDevice> devices) {
        this.devices = devices;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Check to see if convertView has already been set or not.
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_layout,null);
        }

        TextView name = (TextView) convertView.findViewById(R.id.device_name);
        TextView address = (TextView) convertView.findViewById(R.id.device_address);
        name.setText("Device Name: " + devices.get(position).deviceName);
        address.setText("Device Address: " + devices.get(position).deviceAddress);

        return convertView;
    }
}
