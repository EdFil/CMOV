package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import pt.ulisboa.tecnico.cmov.airdesk.AirDeskActivity;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;

/**
 * Created by Diogo on 01-Apr-15.
 */
public class WorkspaceFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WorkspaceFragment newInstance() {
        WorkspaceFragment fragment = new WorkspaceFragment();
        return fragment;
    }

    public WorkspaceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_air_desk, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.workspacesList);
        listView.setAdapter(WorkspaceManager.getInstance().getWorkspaceAdapter());
        // Registering context menu for the listView
        registerForContextMenu(listView);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AirDeskActivity) activity).onSectionAttached(1);
    }
}