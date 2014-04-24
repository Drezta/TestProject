package com.example.bluetoothmodulev4;

import interfaces.IDataProcessor;

import java.util.ArrayList;
import java.util.Set;

import singleton.ListenerHolder;

import bluetooth.ConnectThread;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Pairing extends Activity implements IDataProcessor{
	// bluetooth adapter needs to be passed here or re-initialised?

	protected static final int SUCCESS_CONNECT = 0;
	ListView devicesAvailableList;
	ArrayAdapter<String> listAdapter;
	Set<BluetoothDevice> devicesArray;
	BluetoothAdapter btAdapter;

	IntentFilter btFilter; // used to filter out devices on the bluetooth RF
	BroadcastReceiver receiver;
	ArrayList<String> pairedDevices;
	
	Handler mHandler;
	
	private ListenerHolder holder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pairing_alayout);
		
		holder.setActivity(this);
		holder.setContext(getApplicationContext());
		
		setTitle("Not logged in");
		
		//needs creating first so not null
		mHandler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				
				switch(msg.what)
				{
				case SUCCESS_CONNECT:
					Toast.makeText(getApplicationContext(), "A connection was made", Toast.LENGTH_SHORT).show();
					break;
				}
				
			}
		};
		
		init();
		addListeners();
		
		
	}

	private void addListeners()
	{
		devicesAvailableList.setOnItemClickListener(new OnItemClickListener() {

			//connect to selected device
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				 if(btAdapter.isDiscovering())
				 {
					 btAdapter.cancelDiscovery();
				 }
				 if(listAdapter.getItem(arg2).contains("Paired"))
				 {
					 //get device we need to connect to
					 Object[] o = devicesArray.toArray();
					 BluetoothDevice selectedDevice = (BluetoothDevice)o[arg2];
					 //connection handler, pass in device that you wish to connect to
					 ConnectThread connect = new ConnectThread(selectedDevice,mHandler);
					 connect.start();
				 }
				 else
				 {
					 Toast.makeText(getApplicationContext(), "Device is not paired", Toast.LENGTH_SHORT).show();
				 }
			}
		});
	}
	
	private void getPairedDevices() {
		// visibility will need to be set

		/*
		 * ONLY PAIRED DEVICES //use of custom list view to already see paired
		 * devices
		 */
		devicesArray = btAdapter.getBondedDevices();
		if (devicesArray.size() > 0) {
			for (BluetoothDevice device : devicesArray) {
				
				pairedDevices.add(device.getName());
				//listview not currently used 
			}
		}

	}

	public void init() {
	
		pairedDevices = new ArrayList<String>();
		devicesAvailableList = (ListView) findViewById(R.id.devicesAvailableList);
		listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, 0);
		
		devicesAvailableList.setAdapter(listAdapter);
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		
		
		getPairedDevices();
		
		
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();

				//if a device is discovered
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					
					//listAdapter.add(device.getName()+" "+" "+"\n "+device.getAddress());
					
					String s = "";
					for(int a = 0; a < devicesArray.size(); a++)
					{
						if(device.getName().equals(pairedDevices.get(a)))
						{
							
							s = "Paired";
							listAdapter.add(device.getName()+" "+s+" "+"\n "+device.getAddress()); 
							
							break;
						}
						 
						
						// listAdapter.insert(object, index)
					}
					//listAdapter.add(device.getName() + "\n" + device.getName());
					
				} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED
						.equals(action)) {

				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
					


				} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
					if (btAdapter.getState() == btAdapter.STATE_OFF) {
						// If user turns off bluetooth
						Toast.makeText(
								getApplicationContext(),
								"Connection was inturupted, re-enabling bluetooth",
								Toast.LENGTH_SHORT).show();
					}

				}
			}
		};

		// ensure activity is listening for these events (systemwide broadcasted
		// events) 
		btFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(receiver, btFilter);
		btFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		registerReceiver(receiver, btFilter);
		btFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(receiver, btFilter);
		btFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(receiver, btFilter);

		
		
		
		
		//create handler to call us back when a message is received
	}

	@Override
	protected void onPause() {
		super.onPause();

		// prevents crashing
		unregisterReceiver(receiver);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// need to test if bluetooth adapter is running
		if (!btAdapter.isEnabled()) {
			Toast.makeText(getApplicationContext(),
					"Your bluetooth adapter was disabled, it has now been re-enabled",
					Toast.LENGTH_SHORT).show();
			btAdapter.enable();
			
		} else {
			btAdapter.cancelDiscovery();
			btAdapter.startDiscovery();
		}
	}
	

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		Toast.makeText(getApplicationContext(), "Destroyed", Toast.LENGTH_SHORT)
				.show();
		
	
		
	}
	
	@Override
	public void processStringData(String data, Context cont) {
		// TODO Auto-generated method stub
		
		AlertDialog.Builder builder = new AlertDialog.Builder(Pairing.this);
		builder.setMessage("XYZ has challenged you to a battle!")
	       .setTitle("Incoming match");
		builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				// TODO Auto-generated method stub
				Intent charSelect = new Intent(getApplicationContext(),CharacterSelect.class);
				startActivity(charSelect);
				//also signal to other end that you have accepted
			}
		} );	
        builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			  dialog.cancel();
			
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		
	}
}
