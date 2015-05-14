package pt.ulisboa.tecnico.cmov.airdesk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;

public class ForeignWorkspaceListAdapter extends ArrayAdapter<Workspace> {


    public ForeignWorkspaceListAdapter(Context context, List<?> workspaces) {
        super(context, 0, (List<Workspace>) workspaces);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Workspace workspace = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_foreign_workspace, parent, false);
        }
        // Lookup view for data population
        TextView workspaceTitle = (TextView) convertView.findViewById(R.id.itemName);
        // Populate the data into the template view using the data object
        workspaceTitle.setText(workspace.getName());

        // Lookup view for data population
        TextView workspaceOwner = (TextView) convertView.findViewById(R.id.itemOwner);
        // Populate the data into the template view using the data object
        workspaceOwner.setText(workspace.getOwner().getNick());

        // Lookup view for data population
        TextView workspacePrivacy = (TextView) convertView.findViewById(R.id.itemPrivacy);
        // Populate the data into the template view using the data object
        if(workspace.isPrivate())
            workspacePrivacy.setText("Private");
        else
            workspacePrivacy.setText("Public");


        // Return the completed view to render on screen
        return convertView;
    }

}
