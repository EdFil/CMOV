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
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class ForeignWorkspacesFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String TAG = ForeignWorkspacesFragment.class.getSimpleName();

    WorkspaceManager manager;

    WorkspaceListAdapter mWorkspaceListAdapter;

    public ForeignWorkspacesFragment() {}

    public static ForeignWorkspacesFragment newInstance(int sectionNumber) {
        ForeignWorkspacesFragment fragment = new ForeignWorkspacesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = WorkspaceManager.getInstance();
        manager.refreshWorkspaceLists();
        mWorkspaceListAdapter = new WorkspaceListAdapter(getActivity(), WorkspaceManager.getInstance().getForeignWorkspaces());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View workspaceFragmentView = inflater.inflate(R.layout.fragment_workspaces, container, false);


        ListView listView = (ListView) workspaceFragmentView.findViewById(R.id.workspacesList);
        listView.setAdapter(mWorkspaceListAdapter);

        // When selecting a workspace replaces this fragment for the FilesFragment
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

        // Register the list of Files for the ContextMenu
        registerForContextMenu(listView);

//        // Setup create new workspace button
//        Button newWorkspaceButton = (Button) workspaceFragmentView.findViewById(R.id.newWorkspaceButton);
//        newWorkspaceButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View buttonView) {
//                NewForeignWorkspaceFragment.newInstance().show(getActivity().getFragmentManager(), "New Workspace");
//            }
//        });

        return workspaceFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateWorkspaceList();
    }

    // This will be invoked when an item in the listView is long pressed
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.menu_foreign_workspaces, menu);
    }

    // This will be invoked when a menu item is selected
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Intent intent;
        switch(item.getItemId()){
            case R.id.menu_foreign_edit:
                intent = new Intent(getActivity(), WorkspaceDetailsActivity.class);
                intent.putExtra(WorkspaceDetailsActivity.EDIT_MODE, true);
                intent.putExtra(Constants.WORKSPACE_INDEX, info.position);
                getActivity().startActivity(intent);
                break;
            case R.id.menu_foreign_details:
                intent = new Intent(getActivity(), WorkspaceDetailsActivity.class);
                intent.putExtra(WorkspaceDetailsActivity.EDIT_MODE, false);
                intent.putExtra(Constants.WORKSPACE_INDEX, info.position);
                getActivity().startActivity(intent);
                break;
            case R.id.menu_foreign_delete:
                WorkspaceManager.getInstance().deleteWorkspace(info.position);
                updateWorkspaceList();
                break;
            case R.id.menu_foreign_leave:
                // TODO : LEAVE WORKSPACE
                Toast.makeText(getActivity(), "TODO Leave", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AirDeskActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public void addWorkspace() {
        updateWorkspaceList();
    }

    public void deleteAllWorkspaces() {
        manager.deleteAllUserWorkspaces();
        updateWorkspaceList();
    }

    public void updateWorkspaceList() {
        mWorkspaceListAdapter.notifyDataSetChanged();
    }
}