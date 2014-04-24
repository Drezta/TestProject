package bluetooth;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class ConnectThread extends Thread {
	private static final UUID MY_UUID = UUID
			.fromString("0b6a4f4b-ee0e-47b4-a57a-90be5ff12765"); // Application
																	// specific
																	// ID for
																	// bluetooth
	private static final int SUCCESS_CONNECT = 0;
	private final BluetoothSocket mmSocket;
	private final BluetoothDevice mmDevice;
	private BluetoothAdapter mBluetoothAdapter; // static to be accessed
												// anywhere?
	private Handler mHandler;

	public ConnectThread(BluetoothDevice device, Handler mHandler) {
		// Use a temporary object that is later assigned to mmSocket,
		// because m mSocket is final
		BluetoothSocket tmp = null;
		mmDevice = device;
		this.mHandler = mHandler;
		this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			// MY_UUID is the app's UUID string, also used by the server code
			tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
		}
		mmSocket = tmp;

		// instantiate handler
	}

	public void run() {
		// Cancel discovery because it will slow down the connection
		// and use up battery

		mBluetoothAdapter.cancelDiscovery();

		try {
			// Connect the device through the socket. This will block
			// until it succeeds or throws an exception
			mmSocket.connect();
		} catch (IOException connectException) {
			// Unable to connect; close the socket and get out
			try {

				mmSocket.close();
			} catch (IOException closeException) {
			}
			return;
		}

		// Do work to manage the connection (in a separate thread)
		manageConnectedSocket(mmSocket);
		//mHandler.obtainMessage(SUCCESS_CONNECT);
		//
	}

	public void manageConnectedSocket(BluetoothSocket mmSocket) {
		// perform sending of data here
		try {
			DataOutputStream dos = new DataOutputStream(
					mmSocket.getOutputStream());
			dos.writeInt(50);
			dos.flush();
			mmSocket.close();
		} catch (Exception e) {

		}

	}

	/** Will cancel an in-progress connection, and close the socket */
	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
		}
	}
}
