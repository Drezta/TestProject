package interfaces;

import java.io.Serializable;

public interface OnTaskCompleted {

	void onTaskCompleted(String result);
	
	void onSent(boolean ok);
	
}
