package threadding;
 
import java.io.IOException;
import java.io.OutputStream;
 
import android.bluetooth.BluetoothSocket;
 
public class DataSenderThread extends Thread{
 
        private BluetoothSocket socket;
        private String message;
       
       
        public DataSenderThread(BluetoothSocket socket,String message )
        {
                this.socket = socket;
                this.message = message;
        }
       
        @Override
        public void run() {
                // TODO Auto-generated method stub
                super.run();
                manageConnectedSocket();
               
        }
       
        public void manageConnectedSocket() {
                // perform sending of data here
                OutputStream dos = null;
                byte[] toSend = message.getBytes();
               
                try {
                       
                        dos = this.socket.getOutputStream();
//                      dos = new DataOutputStream(
//                                      mmSocket.getOutputStream());
                       
                        dos.write(toSend);
                        dos.flush();
                       
                        //mmSocket.close();
                       
                       
                } catch (Exception e) {
                                e.printStackTrace();
                } finally {
//                      try {
//                              //dos.close();
//                              this.socket.close();
//                      } catch (IOException e) {
//                              // TODO Auto-generated catch block
//                              e.printStackTrace();
//                      }
                }
 
        }
       
}