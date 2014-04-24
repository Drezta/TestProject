package com.example.bluetoothmodulev4;

import interfaces.DataCall;
import interfaces.IDataProcessor;
import interfaces.OnTaskCompleted;

import java.io.IOException;
import java.net.Socket;
import java.util.TimerTask;

import json.JsonBuilder;

import singleton.ListenerHolder;
import singleton.SessionData;

import nfc.NdefReader;

import bluetoothcomms.BluetoothListen;
import bluetoothcomms.BluetoothWrite;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.tech.Ndef;
import android.nfc.NfcEvent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ChallengeActivity extends Activity implements
		CreateNdefMessageCallback, IDataProcessor {

	public static final String MIME_TEXT_PLAIN = "text/plain";
	private final static String TAG = "CHALLENGEACTIVITY";
	private Long mLastPausedMillis = 0L;
	private BluetoothSocket socket;
	private BluetoothServerSocket serveSock;
	private BluetoothListen listenTask;
	private Button sendString;
	private BluetoothWrite writer;
	private BluetoothDevice device;
	private BluetoothAdapter adapter;
	private ListenerHolder holder;

	// NFC
	private NfcAdapter mNfcAdapter;
	private PendingIntent mNfcPendingIntent;
	private IntentFilter[] mNdefExchangeFilters;
	
	private AlertDialog builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incoming_challenge);
		setTitle("Not logged in");

		holder.setActivity(this);
		holder.setContext(getApplicationContext());
		
		
		
		sendString = (Button) findViewById(R.id.sendData);
		adapter = BluetoothAdapter.getDefaultAdapter();
		//device = adapter.getRemoteDevice("10:68:3F:51:7A:0E");
		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		addListeners();
		nfcHandler();

	}

	public void addListeners() {
		sendString.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				writer = new BluetoothWrite("hello", device);
				writer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
						"hello");
				Toast.makeText(getApplicationContext(), "Request sent",
						Toast.LENGTH_SHORT).show();

			}
		});

	}
	
	private void nfcHandler() {
		mNfcAdapter.setNdefPushMessageCallback(this, this);
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
	public NdefMessage createNdefMessage(NfcEvent event) {
		// TODO Auto-generated method stub
		String text = ("" + adapter.getAddress().toString()); //local mac address!
		//String text = JsonBuilder.createGame(adapter.getAddress().toString());
		// creates the message for NFC
		NdefRecord[] records = { new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				"text/plain".getBytes(), new byte[0], text.getBytes()) };

		NdefMessage msg = new NdefMessage(records);

		return msg;
	}
	
	//timer
	

	@Override
	public void processStringData(String data, Context cont) {
		// TODO Auto-generated method stub
		
		//parse
		//if json type mac address do the else
		//if json type is GAMEACK
		
		Log.d("DATA VAR: ", "VALUE:"+data);
		if(data.compareTo("")==0)
		{
			//test for success or not
			
		}else
		{
		//move into nested if
			if(JsonBuilder.parseGame(data).compareTo("1")==0)
			{
				device = adapter.getRemoteDevice(JsonBuilder.getMacAddress(data));
				BluetoothWrite write = new BluetoothWrite("", device);
				write.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
						"hello");
				
				
				builder = new AlertDialog.Builder(ChallengeActivity.this).create();
				builder.setTitle("Waiting for opponent");  
				builder.setMessage("00:10");
				builder.setCancelable(false);
				builder.show();
				
				
				CountDownTimer start = new CountDownTimer(10000, 1000) {
				    @Override
				    public void onTick(long millisUntilFinished) {
				    	builder.setMessage("00:"+ (millisUntilFinished/1000));
				    }

				    @Override
				    public void onFinish() {
				        builder.dismiss();
				    }
				}.start();
			} else if(JsonBuilder.parseGame(data).compareTo("2")==0)
			{
				Intent tutorialAct = new Intent(getApplicationContext(),
						TutorialActivity.class);
				startActivity(tutorialAct);
			}
			//SessionData.setMacAddress(JsonBuilder.parseGameAck(data));
			

		
		}
		//wait window now
	}

}
