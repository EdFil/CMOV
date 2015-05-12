package pt.ulisboa.tecnico.cmov.airdesk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.file.MyFile;

public class FileListAdapter extends ArrayAdapter<MyFile> {


    public FileListAdapter(Context context, List<? extends MyFile> files) {
        super(context, 0, (List<MyFile>) files);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        MyFile file = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_file, parent, false);
        }
        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.itemName);
        // Populate the data into the template view using the data object
        title.setText(file.getFile().getName());

        // Return the completed view to render on screen
        return convertView;
    }
}
