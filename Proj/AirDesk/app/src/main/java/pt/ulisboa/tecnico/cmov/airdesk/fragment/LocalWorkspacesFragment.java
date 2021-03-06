package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.airdesk.AirDeskActivity;
import pt.ulisboa.tecnico.cmov.airdesk.ManageAccessListActivity;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.WorkspaceDetailsActivity;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.WorkspaceListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class LocalWorkspacesFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static final String TAG = LocalWorkspacesFragment.class.getSimpleName();

    WorkspaceManager manager;

    ListView mWorkspaceListView;
    WorkspaceListAdapter mWorkspaceListAdapter;

    public static LocalWorkspacesFragment newInstance(int sectionNumber) {
        LocalWorkspacesFragment fragment = new LocalWorkspacesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWorkspaceListAdapter = new WorkspaceListAdapter(getActivity(), WorkspaceManager.getInstance().getLocalWorkspaces());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View workspaceFragmentView = inflater.inflate(R.layout.fragment_workspaces, container, false);

        mWorkspaceListView = (ListView) workspaceFragmentView.findViewById(R.id.workspacesList);
        mWorkspaceListView.setAdapter(mWorkspaceListAdapter);

        // When selecting a workspace replaces this fragment for the FilesFragment
        mWorkspaceListView.setOnItemClickListener(mWorkspaceListOnItemClickListener);

        // Register the list of Files for the ContextMenu
        registerForContextMenu(mWorkspaceListView);

        // Setup create new workspace button
        Button newWorkspaceButton = (Button) (workspaceFragmentView.findViewById(R.id.newWorkspaceButton));
        newWorkspaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                NewWorkspaceFragment.newInstance().show(getActivity().getFragmentManager(), NewWorkspaceFragment.class.getSimpleName());
            }
        });

        return workspaceFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AirDeskActivity) getActivity()).updateActionBarTitle();
        onUpdateWorkspaceList();
    }

    // This will be invoked when an item in the listView is long pressed
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.menu_context_my_workspaces, menu);
    }

    // This will be invoked when a menu item is selected
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Intent intent;
        switch(item.getItemId()){
            case R.id.menu_rename:
                RenameWorkspaceFragment renameFrag = RenameWorkspaceFragment.newInstance();
                renameFrag.setWorkspace(mWorkspaceListAdapter.getItem(info.position));
                renameFrag.show(getActivity().getFragmentManager(), RenameWorkspaceFragment.class.getSimpleName());
                break;
            case R.id.menu_tags:
                ManageWorkspaceTagsFragment tagsFrag = ManageWorkspaceTagsFragment.newInstance();
                tagsFrag.setWorkspace(mWorkspaceListAdapter.getItem(info.position));
                tagsFrag.show(getActivity().getFragmentManager(), ManageWorkspaceTagsFragment.class.getSimpleName());
                break;
            case R.id.menu_privacy:
                ChangePrivacyWorkspaceFragment privacyFrag = ChangePrivacyWorkspaceFragment.newInstance();
                privacyFrag.setWorkspace(mWorkspaceListAdapter.getItem(info.position));
                privacyFrag.show(getActivity().getFragmentManager(), ChangePrivacyWorkspaceFragment.class.getSimpleName());
                break;
            case R.id.menu_quota:
                ChangeQuotaWorkspaceFragment quotaFrag = ChangeQuotaWorkspaceFragment.newInstance();
                quotaFrag.setWorkspace(mWorkspaceListAdapter.getItem(info.position));
                quotaFrag.show(getActivity().getFragmentManager(), ChangeQuotaWorkspaceFragment.class.getSimpleName());
                break;
            case R.id.menu_details:
                intent = new Intent(getActivity(), WorkspaceDetailsActivity.class);
                intent.putExtra(WorkspaceDetailsActivity.IS_LOCAL_WS, true);
                intent.putExtra(WorkspaceDetailsActivity.EDIT_MODE, false);
                intent.putExtra(Constants.WORKSPACE_ID_KEY, mWorkspaceListAdapter.getItem(info.position).getDatabaseId());
                getActivity().startActivity(intent);
                break;
            case R.id.menu_delete:
                WorkspaceManager.getInstance().deleteLocalWorkspace(info.position);
                onUpdateWorkspaceList();
                break;
            case R.id.menu_access_list:
                intent = new Intent(getActivity(), ManageAccessListActivity.class);
                intent.putExtra(Constants.WORKSPACE_ID_KEY, mWorkspaceListAdapter.getItem(info.position).getDatabaseId());
                getActivity().startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_air_desk, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == R.id.refresh_workspaces) {
            onUpdateWorkspaceList();
            return true;
        }

        if(item.getItemId() == R.id.delete_all_workspaces) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Delete All")
                    .setMessage("Are you sure you want to delete all your workspaces?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                                deleteAllWorkspaces();
                            Toast.makeText(getActivity(), "DELETED", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AirDeskActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public void addWorkspace() {
        onUpdateWorkspaceList();
    }

    public void deleteAllWorkspaces() {
        manager.deletaAllLocalWorkspaces();
        onUpdateWorkspaceList();
    }

    public void onUpdateWorkspaceList() {
        mWorkspaceListAdapter.notifyDataSetChanged();
    }

    private AdapterView.OnItemClickListener mWorkspaceListOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            LocalFilesFragment filesFragment = new LocalFilesFragment();
            Bundle bundle = new Bundle();
            bundle.putLong(Constants.WORKSPACE_ID_KEY, ((Workspace)parent.getItemAtPosition(position)).getDatabaseId());
            filesFragment.setArguments(bundle);

            transaction.replace(R.id.container, filesFragment);
            transaction.addToBackStack(LocalFilesFragment.class.getSimpleName());

            // Commit the transaction
            transaction.commit();
        }
    };
}