package bluetoothcomms;
 
import interfaces.DataCall;
import interfaces.OnTaskCompleted;
 
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;
 
import threadding.DataSenderThread;
 
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
 
public class BluetoothWrite extends AsyncTask<String, Void, Void> {
 
       
        private String message;
        private static final int SUCCESS_CONNECT = 0;
        private   BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private BluetoothAdapter mBluetoothAdapter; // static to be accessed
                                                                                                // anywhere?
       
        private static final UUID MY_UUID = UUID
                        .fromString("0b6a4f4b-ee0e-47b4-a57a-90be5ff12765"); // Application
                                                                                                                                        // specific
                                                                                                                                        // ID for
                                                                                                                                        // bluetooth
 
        public BluetoothWrite(String message, BluetoothDevice device) {
                BluetoothSocket tmp = null;
                mmDevice = device;
                this.message = message;
               
               
                this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                this.mBluetoothAdapter.cancelDiscovery();
                // Get a BluetoothSocket to connect with the given BluetoothDevice
                try {
                        // MY_UUID is the app's UUID string, also used by the server code
                        mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                } catch (IOException e) {
                }
                //mmSocket = tmp;
               
        }
 
       
        public void manageConnectedSocket() {
                // perform sending of data here
                OutputStream dos = null;
                byte[] toSend = message.getBytes();
               
                try {
                       
                        dos = mmSocket.getOutputStream();
//                      dos = new DataOutputStream(
//                                      mmSocket.getOutputStream());
                       
                        dos.write(toSend);
                        dos.flush();
                       
                        //mmSocket.close();
                       
                       
                } catch (Exception e) {
                                e.printStackTrace();
                } finally {
                        try { //send in seperate thread, sleep this thread to allow it to finish
                                //dos.close();
                                mmSocket.close();
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                }
 
        }
 
        protected void onPostExecute(Void result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
               
               
        }
 
        @Override
        protected Void doInBackground(String... params) {
                // TODO Auto-generated method stub
                // Cancel discovery because it will slow down the connection
                // and use up battery
 
                mBluetoothAdapter.cancelDiscovery();
 
                try {
                        // Connect the device through the socket. This will block
                        // until it succeeds or throws an exception
                       
                        mmSocket.connect();
                       
                } catch (IOException connectException) {
                        // Unable to connect; close the socket and get out
                        connectException.printStackTrace();
                        try {
 
                                mmSocket.close();
                        } catch (IOException closeException) {
                                closeException.printStackTrace();
                        }
                       
                }
 
                DataSenderThread worker = new DataSenderThread(mmSocket, message);
                worker.run();
                try {
                        Thread.sleep(3000);
                        mmSocket.close();
                } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (IOException e1)
                {
                        e1.printStackTrace();
                }
                // Do work to manage the connection (in a separate thread)
                //manageConnectedSocket();
                //mHandler.obtainMessage(SUCCESS_CONNECT);
                //
                return null;
 
        }
 
}