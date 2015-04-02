package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import pt.ulisboa.tecnico.cmov.airdesk.FileActivity;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.WorkspaceListAdapter;
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
    WorkspaceListAdapter workspaceListAdapter;

    public WorkspaceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        workspaceListAdapter = (WorkspaceListAdapter) WorkspaceManager.getInstance().getWorkspaceAdapter();

        View workspaceFragmentView = inflater.inflate(R.layout.fragment_air_desk, container, false);

        ListView listView = (ListView) workspaceFragmentView.findViewById(R.id.workspacesList);
        listView.setAdapter(workspaceListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getActivity(), FileActivity.class));
            }
        });

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

    // This will be invoked when an item in the listView is long pressed
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.menu_my_workspaces, menu);
    }

    // This will be invoked when a menu item is selected
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch(item.getItemId()){
            case R.id.menu_my_edit:
                Toast.makeText(getActivity(), "TODO Edit", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_my_details:
                Toast.makeText(getActivity(), "TODO Details", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_my_delete:
                Workspace selectedWorkspace = (Workspace)((ListView)info.targetView.getParent()).getAdapter().getItem(info.position);
                WorkspaceManager.getInstance().deleteWorkspace(selectedWorkspace);
                break;
            case R.id.menu_my_invite:
                Toast.makeText(getActivity(), "TODO Invite", Toast.LENGTH_SHORT).show();
                break;

        }

        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AirDeskActivity) activity).onSectionAttached(1);
    }



}