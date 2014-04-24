package com.example.bluetoothmodulev4;

import interfaces.DataCall;
import interfaces.IDataProcessor;

import java.util.ArrayList;
import java.util.List;

import json.JsonBuilder;

import bluetoothcomms.BluetoothWrite;

import services.CommsService;
import singleton.ListenerHolder;
import singleton.SessionData;

import nfc.NdefReader;

import listview.CustomListViewAdapter;
import listview.MainMenuContent;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements IDataProcessor{

	public static final String MIME_TEXT_PLAIN = "text/plain";
	private final static String TAG = "MAINACTIVITY";
	protected static final int SUCCESS_CONNECT = 0;
	private ListView mainMenu;
	private BluetoothAdapter mBluetoothAdapter;
	private Intent commsService;
	private NfcAdapter mNfcAdapter;
	private ListenerHolder holder;
	private PendingIntent mNfcPendingIntent;
	private IntentFilter[] mNdefExchangeFilters;
	private Intent service;
	private BluetoothWrite writer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		holder.setActivity(this);
		holder.setContext(getApplicationContext());

		service = new Intent(this, CommsService.class);

		holder.setIntent(service);
		testNfc();
		enableBluetooth();
		// Allows device to become searchable by other bluetooth devices
		// Intent discoverableIntent = new Intent(
		// BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		// discoverableIntent.putExtra(
		// BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
		// startActivity(discoverableIntent);

		initUi();
		addListeners();
		startService();
		nfcHandler();

	}

	private void nfcHandler() {
		//mNfcAdapter.setNdefPushMessageCallback(this, this);
	}

	private void testNfc() {
		mNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
		if (mNfcAdapter == null) {
			// Device doesn't support NFC, throw error
		} else {
			if (!mNfcAdapter.isEnabled()) {
				mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
				// NFC not enabled, enable
			} else {
				// NFC works
			}
		}

		//
	}

	private void startService() {
		// will listen for challenger events
		// challengerService = new Intent(this, CommsService.class);
		// this.startService(challengerService);
		// commsService = new Intent(this, CommsService.class);
		// this.startService(commsService);

		startService(service);
		// this.bindService(commsService, this, BIND_AUTO_CREATE);
	}

	private void addListeners() {

		mainMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == 0) // we know item 0 is clicked
				{
					// make device discoverable by other devices, by default
					// this is off

					Intent pairingAct = new Intent(getApplicationContext(),
							Pairing.class);
					startActivity(pairingAct);
				} else if (arg2 == 1) {
					Intent challengeAct = new Intent(getApplicationContext(),
							ChallengeActivity.class);
					startActivity(challengeAct);
				} else if (arg2 == 2) {
					Intent tutorialAct = new Intent(getApplicationContext(),
							TutorialActivity.class);
					startActivity(tutorialAct);

				}

			}

		});
	}

	private ArrayList<MainMenuContent> addMenuList() {
		List<MainMenuContent> menuList = new ArrayList<MainMenuContent>();

		MainMenuContent menuMatch = new MainMenuContent("Find match");
		menuList.add(menuMatch);
		MainMenuContent menuLog = new MainMenuContent("Challenge");
		menuList.add(menuLog);
		MainMenuContent menuTut = new MainMenuContent("Tutorial");
		menuList.add(menuTut);
		MainMenuContent menuReg = new MainMenuContent("Register");
		menuList.add(menuReg);
		MainMenuContent menuLogin = new MainMenuContent("Log in");
		menuList.add(menuLogin);

		return (ArrayList<MainMenuContent>) menuList;
	}

	private void initUi() {
		mainMenu = (ListView) findViewById(R.id.menuList);

		addMenuList();

		CustomListViewAdapter aa = new CustomListViewAdapter(this,
				R.layout.menu_view, addMenuList());
		// initialise listView

		mainMenu.setAdapter(aa);
		setTitle("Not logged in");
	}

	private void enableBluetooth() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// throw message device does not support bluetooth
			Toast.makeText(getApplicationContext(),
					"Your device does not support bluetooth",
					Toast.LENGTH_SHORT).show();
			finish();
		} else {
			if (!mBluetoothAdapter.isEnabled()) {
				mBluetoothAdapter.enable();

			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);

		// intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);

		// onResume gets called after this to handle the intent
		setIntent(intent);
		// above would be called on the new device
		// if the intent !=

	}

	/**
	 * Processes the NDEF message
	 * 
	 * @param intent
	 */
	private void processIntent(Intent intent) {

		Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		// only one message sent during the beam
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		// record 0 contains the MIME type, record 1 is the AAR, if present
		Toast.makeText(this, new String(msg.getRecords()[0].getPayload()),
				Toast.LENGTH_SHORT).show();
		
		BluetoothDevice device;
		device = mBluetoothAdapter.getRemoteDevice(new String(msg.getRecords()[0].getPayload()));
		
		//instantiate writer, connect and pass in own ID, this will also create calback
		//after look at JSON
		
		SessionData.setMacAddress(new String(msg.getRecords()[0].getPayload())); //save session
		String text = JsonBuilder.createGame(mBluetoothAdapter.getAddress().toString());
		
		writer = new BluetoothWrite(text,device );
		writer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
				"hello");
//		writer.execute("");

	}

	private void handleIntent(Intent intent) {
		NdefReader reader = null;
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			String type = intent.getType();
			if (MIME_TEXT_PLAIN.equals(type)) {
				Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

				reader = new NdefReader(getApplicationContext());
				reader.execute(tag);
			} else {
				Log.d(TAG, "Wrong mime type: " + type);
			}
		} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			// In case we would still use the Tech Discovered Intent
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			String[] techList = tag.getTechList();
			String searchedTech = Ndef.class.getName();
			for (String tech : techList) {
				if (searchedTech.equals(tech)) {
					reader = new NdefReader(getApplicationContext());
					reader.execute(tag);
					break;
				}
			}
		}
	}

	public static void stopForegroundDispatch(final Activity activity,
			NfcAdapter adapter) {
		adapter.disableForegroundDispatch(activity);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		Toast.makeText(getApplicationContext(), "Destroyed", Toast.LENGTH_SHORT)
				.show();
		if (!(commsService == null)) {
			stopService(commsService);

		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// Check to see that the Activity started due to an Android Beam
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processIntent(getIntent());
			// message bit
		}
	}

	@Override
	protected void onPause() {
		stopForegroundDispatch(this, mNfcAdapter);
		super.onPause();
	}

	@Override
	public void processStringData(String data, Context a) {
		// TODO Auto-generated method stub

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setMessage("XYZ has challenged you to a battle!").setTitle(
				"Incoming match");
		builder.setPositiveButton("Accept",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						// TODO Auto-generated method stub
						BluetoothDevice device;
						device = mBluetoothAdapter.getRemoteDevice(SessionData.getMacAddress());
						
						writer = new BluetoothWrite(JsonBuilder.createConfirm("ACCEPT"),device );
						writer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
								"hello");
						Intent charSelect = new Intent(getApplicationContext(),
								CharacterSelect.class);
						startActivity(charSelect);
						// also signal to other end that you have accepted
					}
				});
		builder.setNegativeButton("Decline",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						dialog.cancel();
						BluetoothDevice device;
						device = mBluetoothAdapter.getRemoteDevice(SessionData.getMacAddress());
						
						writer = new BluetoothWrite(JsonBuilder.createConfirm("DECLINE"),device );
						writer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
								"hello");

					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();

		// Toast.makeText(a, "ROYYYYYYYYYY", Toast.LENGTH_SHORT).show();
		// testView.setText(data.toString());
	}



}
