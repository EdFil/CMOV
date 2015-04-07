package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import pt.ulisboa.tecnico.cmov.airdesk.WorkspaceDetailsActivity;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.WorkspaceListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;

public class WorkspacesFragment extends Fragment {

    public WorkspacesFragment() {}

    public static WorkspacesFragment newInstance() {
        return new WorkspacesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        WorkspaceListAdapter workspaceListAdapter = (WorkspaceListAdapter) WorkspaceManager.getInstance().getWorkspaceListAdapter();

        final View workspaceFragmentView = inflater.inflate(R.layout.fragment_workspaces, container, false);

        ListView listView = (ListView) workspaceFragmentView.findViewById(R.id.myWorkspacesList);
        listView.setAdapter(workspaceListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                FilesFragment filesFragment = new FilesFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("Workspace", (Workspace) parent.getItemAtPosition(position));
                filesFragment.setArguments(bundle);

                transaction.replace(R.id.container, filesFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        registerForContextMenu(listView);

        // Setup create new workspace button
        Button createNewWorkspaceButton = (Button) workspaceFragmentView.findViewById(R.id.newButton);
        createNewWorkspaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                NewWorkspaceFragment.newInstance().show(getActivity().getFragmentManager(), "Create Workspace");
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

        Intent intent;
        switch(item.getItemId()){
            case R.id.menu_my_edit:
                intent = new Intent(getActivity(), WorkspaceDetailsActivity.class);
                intent.putExtra(WorkspaceDetailsActivity.EDIT_MODE, true);
                intent.putExtra(WorkspaceDetailsActivity.WORKSPACE_INDEX_TAG, info.position);
                getActivity().startActivity(intent);
                break;
            case R.id.menu_my_details:
                intent = new Intent(getActivity(), WorkspaceDetailsActivity.class);
                intent.putExtra(WorkspaceDetailsActivity.EDIT_MODE, false);
                intent.putExtra(WorkspaceDetailsActivity.WORKSPACE_INDEX_TAG, info.position);
                getActivity().startActivity(intent);
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