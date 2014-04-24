package bluetoothcomms;

import interfaces.DataCall;
import interfaces.OnTaskCompleted;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.UUID;

import services.CommsService;
import singleton.ListenerHolder;
import threadding.DataProcessorThread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class BluetoothListenV2 extends AsyncTask<Void, Void, Void> {

	private final BluetoothServerSocket mmServerSocket;
	private static final UUID MY_UUID = UUID
			.fromString("0b6a4f4b-ee0e-47b4-a57a-90be5ff12765"); // Application
																	// specific
																	// ID for
																	// bluetooth
	private BluetoothAdapter mBluetoothAdapter;
	private DataCall dataRet;
	private BluetoothSocket socket;
	
	
	
	public BluetoothListenV2(DataCall dataRet)
	{
		
		this.dataRet = dataRet;
		BluetoothServerSocket tmp = null;
		this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mBluetoothAdapter.cancelDiscovery();
		try {
			// MY_UUID is the app's UUID string, also used by the client code
			tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("MYAPP", MY_UUID);
			
		
			 
		} catch (IOException e) {
			
		}
		mmServerSocket = tmp;
		
		this.socket = null;
		
		
	}
	
	
	
	private String manageSocket()
	{
		String data ="";
		BufferedReader in= null;
		try {
			//String foo;
			//int bytesToRead;
			//InputStream input = socket.getInputStream();
			
			//try using final in bluetooth sockets.
			
			in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			
            int charsRead = 0;
            char[] buffer = new char[1024];

            while ((charsRead = in.read(buffer)) != -1) {
                data += new String(buffer).substring(0, charsRead);
            }
			
//			//int avail = din.available();
//			//byte[] data = new byte[avail];
//			while(din.available() >0){
//				byteList.add(din.readByte());
//			}
		    
//			byte[] byteData = new byte[byteList.size()];
//			for(int i = 0; i < byteList.size(); i++){
//				byteData[i] = byteList.get(i);
//			}
//			data = new String(byteData);
			
			//din.read(data);
			Log.d("Data Was:", "" + data);
			
		} catch (Exception e) {
			Log.d("DATA RECEIVED", "READERROR");
			
		} finally{
			
			try {
				//mmServerSocket.close();
				//in.close();
				socket.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				//Close Service
			}
		}

		Log.d("DATA RECEIVED", "GOTDATA");
		return data;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		String received = "";
		DataProcessorThread worker;
		while(true)
		{
		try {
			this.socket = mmServerSocket.accept();
//			worker = new DataProcessorThread(socket);
//			worker.run();
			received = manageSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		//close server sock here?
		
	}
	
	

}
