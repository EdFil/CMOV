package pt.ulisboa.tecnico.cmov.simpleclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.simpleclient.R;

/**
 * Created by Diogo on 18-Apr-15.
 */
public class ServiceAdapter extends ArrayAdapter {

    public ServiceAdapter(Context context, ArrayList<String> service) {
        super(context, 0, service);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String s = (String) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.service_item, parent, false);
        }
//        // Lookup view for data population
//        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
//        // Populate the data into the template view using the data object
//        tvName.setText(user.name);
        // Return the completed view to render on screen
        return convertView;
    }
}
