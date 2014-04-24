package services;

import singleton.ListenerHolder;
import interfaces.DataCall;
import interfaces.IDataProcessor;
import interfaces.OnTaskCompleted;
import bluetooth.AcceptThread;
import bluetoothcomms.BluetoothListenV2;
import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

/**
 * Service runs on the main thread
 * @author david
 *
 */
public class CommsService extends Service implements DataCall{

	private static final String TAG = CommsService.class.getSimpleName(); //For logging
	private DataCall callback;
	
	public CommsService() {
		super();
		
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		callback = this;
	}

	@Override
	public void serviceDataCall(String data) {
		// TODO Auto-generated method stub
//		DataCall listen = holder.getDataCall();
//		 data = "a";
//		listen.serviceDataCall(data);	
		Activity a = ListenerHolder.getActivity();
		if(a instanceof IDataProcessor){
			//If this activity implements this interface
			((IDataProcessor)a).processStringData(data, ListenerHolder.getContext());
			
		}
		
	}


	@Override
	public void sendSuccess(boolean ok) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		BluetoothListenV2 listener = new BluetoothListenV2(callback);
		//listener.execute();
		listener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
