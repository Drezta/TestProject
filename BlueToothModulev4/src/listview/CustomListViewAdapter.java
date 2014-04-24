package listview;

import java.util.List;

import com.example.bluetoothmodulev4.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListViewAdapter extends ArrayAdapter<MainMenuContent> {
		 
	    Context context;
	 
	    public CustomListViewAdapter(Context context, int resourceId,
	            List<MainMenuContent> items) {
	        super(context, resourceId, items);
	        this.context = context;
	    }
	 
	    /*private view holder class*/
	    private class ViewHolder {
	        //ImageView imageView;
	        TextView txtTitle;
	        
	       // TextView txtDesc;
	    }
	 
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ViewHolder holder = null;
	        MainMenuContent rowItem = getItem(position);
	 
	        LayoutInflater mInflater = (LayoutInflater) context
	                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	        if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.menu_view, null);
	            holder = new ViewHolder();
	            
	            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
	            
	            
	            convertView.setTag(holder);
	        } else
	            holder = (ViewHolder) convertView.getTag();
	 
	       
	        holder.txtTitle.setText(rowItem.getTitle());
	       
	 
	        return convertView;
	    }
	}
