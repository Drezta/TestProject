package threadding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class DataProcessorThread extends Thread{

	private BluetoothSocket socket;
	
	public DataProcessorThread(BluetoothSocket socket)
	{
		this.socket = socket;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		manageSocket();
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
}
