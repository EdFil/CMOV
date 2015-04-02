package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.airdesk.AirDeskActivity;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
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


    Button createNewWorkspaceButton;

    public WorkspaceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View workspaceFragmentView = inflater.inflate(R.layout.fragment_air_desk, container, false);

        ListView listView = (ListView) workspaceFragmentView.findViewById(R.id.workspacesList);
        listView.setAdapter(WorkspaceManager.getInstance().getWorkspaceAdapter());

        registerForContextMenu(listView);

        // Setup create new workspace button
        createNewWorkspaceButton = (Button) workspaceFragmentView.findViewById(R.id.createNewWorkspaceButton);
        createNewWorkspaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                CreateWorkspaceFragment.newInstance().show(getActivity().getFragmentManager(), "Create Workspace");
            }
        });


        return workspaceFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AirDeskActivity) activity).onSectionAttached(1);
    }



}