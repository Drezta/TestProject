package singleton;

import interfaces.DataCall;

import java.io.IOException;

import android.bluetooth.BluetoothServerSocket;

public class SocketHolder implements DataCall{
	
	private static BluetoothServerSocket serveSock;
	
	public SocketHolder(BluetoothServerSocket serverSock)
	{
		this.serveSock = serverSock;
	}
	
	public static void closeSock()
	{
		try {
			serveSock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//interface call here?
		}
	}
	
	public static void destroySock()
	{
		serveSock = null;
	}

	@Override
	public void serviceDataCall(String data) {
		// TODO Auto-generated method stub
		
		int i = 0;
		i++;
		
	}

	@Override
	public void sendSuccess(boolean ok) {
		// TODO Auto-generated method stub
		
	}



}
