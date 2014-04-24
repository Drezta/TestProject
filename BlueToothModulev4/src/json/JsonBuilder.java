package json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonBuilder {

	public JsonBuilder()
	{
		
	}
	
	public static String createConfirm(String type)
	{
		JSONObject object = new JSONObject();
		  try {
			object.put("MESSAGETYPE", "2");
		    object.put("CONFIRMFLAG", type);
		    
		  } catch (JSONException e) {
		    e.printStackTrace();
		  }
		  
		  return object.toString();
	}
	
	//sends mac address to bring up the dialog for the challenger that counts down from 30
	public static String createGame(String macAddress)
	{
		JSONObject object = new JSONObject();
		  try {
			  //change
			 
		    object.put("MESSAGETYPE", "1");
		    object.put("MACADDRESS", macAddress);
		    
		  } catch (JSONException e) {
		    e.printStackTrace();
		  }
		  
		  return object.toString();
	}
	
	public static boolean acceptOrDecline(String data)
	{
		String temp = null;
		boolean returnValue = false;
		
		try {
		      JSONObject jsonObj = new JSONObject(data);
		      temp = jsonObj.getString("CONFIRMFLAG");	     
		      
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		
		if(temp.compareTo("ACCEPT")==0)
		{
			returnValue = true;
		} else
		{
			
		}
		
		return returnValue;
	}
	
	public static String parseGame(String data)
	{
		
		String temp = null;
		
		try {
		      JSONObject jsonObj = new JSONObject(data);
		      temp = jsonObj.getString("MESSAGETYPE");	     
		      
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		
		return temp;
	}
	
	//returns the mac address
	public static String getMacAddress(String data)
	{
			String temp = null;
		
		try {
		      JSONObject jsonObj = new JSONObject(data);
		      temp = jsonObj.getString("MACADDRESS");	     
		      
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		
		return temp;
	}
}
