package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.airdesk.AirDeskActivity;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.WorkspaceDetailsActivity;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.ForeignWorkspaceListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WifiDirectManager;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.service.LeaveWorskpaceService;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.AsyncResponse;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.RequestTask;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class ForeignWorkspacesFragment extends Fragment implements WifiDirectManager.NetworkUpdate, OnWorspaceInfoChangedListener {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String TAG = ForeignWorkspacesFragment.class.getSimpleName();

    WorkspaceManager manager;

    ForeignWorkspaceListAdapter mForeignWorkspaceListAdapter;
    OnWorspaceInfoChangedListener mCallback;

    public ForeignWorkspacesFragment() {setHasOptionsMenu(true);}

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
        mForeignWorkspaceListAdapter = new ForeignWorkspaceListAdapter(getActivity(), WorkspaceManager.getInstance().getForeignWorkspaces());
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View workspaceFragmentView = inflater.inflate(R.layout.fragment_workspaces, container, false);


        ListView listView = (ListView) workspaceFragmentView.findViewById(R.id.workspacesList);
        listView.setAdapter(mForeignWorkspaceListAdapter);

        // When selecting a workspace replaces this fragment for the FilesFragment
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                RemoteFilesFragment remoteFilesFragment = new RemoteFilesFragment();

                ForeignWorkspace foreignWorkspace = (ForeignWorkspace) parent.getItemAtPosition(position);

                Bundle bundle = new Bundle();
                bundle.putLong(Constants.WORKSPACE_ID_KEY, foreignWorkspace.getDatabaseId());
                remoteFilesFragment.setArguments(bundle);

                transaction.replace(R.id.container, remoteFilesFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        // Register the list of Files for the ContextMenu
        registerForContextMenu(listView);

        return workspaceFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AirDeskActivity) getActivity()).updateActionBarTitle();
    }

    // This will be invoked when an item in the listView is long pressed
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.menu_context_foreign_workspaces, menu);
    }
    // This will be invoked when a menu item is selected
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Intent intent;
        switch(item.getItemId()){
            case R.id.menu_rename:
                RenameWorkspaceFragment frag = RenameWorkspaceFragment.newInstance();
                frag.setWorkspace(mForeignWorkspaceListAdapter.getItem(info.position));
                frag.show(getActivity().getFragmentManager(), RenameWorkspaceFragment.class.getSimpleName());
                break;
            case R.id.menu_details:
                intent = new Intent(getActivity(), WorkspaceDetailsActivity.class);
                intent.putExtra(Constants.WORKSPACE_ID_KEY, info.position);
                getActivity().startActivity(intent);
                break;
            case R.id.menu_leave:
                ForeignWorkspace foreignWorkspace = (ForeignWorkspace) mForeignWorkspaceListAdapter.getItem(info.position);
                String ip = foreignWorkspace.getOwner().getDevice().getVirtIp();
                int port = Integer.parseInt(getString(R.string.port));
                RequestTask task = new RequestTask(ip, port, LeaveWorskpaceService.class, foreignWorkspace.getName(), UserManager.getInstance().getOwner().getEmail());
                task.mDelegate = new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        try {
                            // Get the response as a JSONObject
                            JSONObject response = new JSONObject(output);

                            // Check if has error
                            if (response.has(Constants.ERROR_KEY)) {
                                throw new Exception(response.getString(Constants.ERROR_KEY));
                            }

                            WorkspaceManager.getInstance().unmountForeignWorkspace(info.position);
                            mCallback.onWorkspaceInfoChanged();

                        } catch (Exception e) {
                            // Show our error in a toast
                            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
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
            WorkspaceManager.getInstance().getForeignWorkspaces().clear();
            WifiDirectManager.getInstance().updateForeignWorkspaceList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AirDeskActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        WifiDirectManager.getInstance().setOnForeignWorkspaceUpdate(this);
        try {
            mCallback = (OnWorspaceInfoChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        WifiDirectManager.getInstance().setOnForeignWorkspaceUpdate(null);
    }

    @Override
    public void onForeignWorkspaceUpdate() {
        mForeignWorkspaceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onWorkspaceInfoChanged() {
        mForeignWorkspaceListAdapter.notifyDataSetChanged();
    }
}