package bluetooth;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class AcceptThread extends Thread {
	private final BluetoothServerSocket mmServerSocket;
	private static final UUID MY_UUID = UUID
			.fromString("0b6a4f4b-ee0e-47b4-a57a-90be5ff12765"); // Application
																	// specific
																	// ID for
																	// bluetooth
	private BluetoothAdapter mBluetoothAdapter;

	public AcceptThread() {
		// Use a temporary object that is later assigned to mmServerSocket,
		// because mmServerSocket is final
		BluetoothServerSocket tmp = null;
		this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		try {
			// MY_UUID is the app's UUID string, also used by the client code
			tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(
					"MYAPP", MY_UUID);
		} catch (IOException e) {
		}
		mmServerSocket = tmp;
	}

	public void run() {
		BluetoothSocket socket = null;
		// Keep listening until exception occurs or a socket is returned
		while (true) {
			try {
				socket = mmServerSocket.accept();
			} catch (Exception e) {
				// break;
			}
			// If a connection was accepted
			if (socket != null) {
				// Do work to manage the connection (in a separate thread)
				if(socket.isConnected()){
					manageConnectedSocket(socket);
				} else {
					Log.d("Socket not Connecte", "It lied!");
				}
				try {
					socket.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// break;
			}
		}
	}

	public void manageConnectedSocket(BluetoothSocket socket) {
		try {
			int foo;
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

	/** Will cancel the listening socket, and cause the thread to finish */
	public void cancel() {
		try {
			mmServerSocket.close();
		} catch (IOException e) {
		}
	}
}
