package singleton;

//stores data per session
public class SessionData {
	
	private static String macAddress; //mac address of opponent to send to

	public static String getMacAddress() {
		return macAddress;
	}

	public static void setMacAddress(String macAddress) {
		SessionData.macAddress = macAddress;
	}
	
	
	
	
	
}
