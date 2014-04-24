package singleton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import interfaces.DataCall;

public class ListenerHolder  {
	
	private static Activity currActivity;
	private static Context con;
	private static Intent service;
	
	public ListenerHolder()
	{
		
	}
	
	public static void setIntent(Intent serve)
	{
		service = serve;
	}
	
	public static Intent getIntent()
	{
		return service;
	}
	
	public static void setActivity(Activity act)
	{
		
		currActivity = act;
	}
	
	public static Activity getActivity()
	{
		
		return currActivity;
	}

	public static void setContext(Context c)
	{
		con = c;
	}
	
	public static Context getContext()
	{
		return con;
	}
	
}
