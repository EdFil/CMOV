package pt.ulisboa.tecnico.cmov.airdesk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.MyFile;

/**
 * Created by edgar on 30-03-2015.
 */
public class TagListAdapter extends ArrayAdapter<String> {

    public TagListAdapter(Context context, List<String> tags) {
        super(context, 0, tags);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final String tag = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tag_list_adapter, parent, false);
        }
        // Lookup view for data population
        final TextView title = (TextView) convertView.findViewById(R.id.tagName);
        // Populate the data into the template view using the data object
        title.setText(tag);

        Button removeTagButton = (Button) convertView.findViewById(R.id.removeTagButton);
        removeTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(title.getText().toString());
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }


}
