package bluetoothcomms;

import interfaces.OnTaskCompleted;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

public class BluetoothListen extends AsyncTask<BluetoothServerSocket, Void, String>{

	private String result;
	private OnTaskCompleted listener;
	
	public BluetoothListen(OnTaskCompleted listener)
	{
		this.listener = listener;
	}
	
	@Override
	protected String doInBackground(BluetoothServerSocket... params) {
		// TODO Auto-generated method stub
	
		BluetoothSocket socket = null;
		
		try {
			socket = params[0].accept();
			manageSocket(socket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return this.result;
	}
	
	private void manageSocket(BluetoothSocket socket)
	{
		try {
			String foo;
			InputStream input = socket.getInputStream();
			DataInputStream din = new DataInputStream(input);
			
			ArrayList<Byte> byteList = new ArrayList<Byte>();
			//int avail = din.available();
			//byte[] data = new byte[avail];
			while(din.available() >0){
				byteList.add(din.readByte());
			}
			byte[] byteData = new byte[byteList.size()];
			for(int i = 0; i < byteList.size(); i++){
				byteData[i] = byteList.get(i);
			}
			String data = new String(byteData);
			//din.read(data);
			Log.d("Data Was:", "" + data);
			
		} catch (Exception e) {
			Log.d("DATA RECEIVED", "READERROR");
		}

		Log.d("DATA RECEIVED", "GOTDATA");

	}
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		listener.onTaskCompleted(result);
	}

}
