package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.AirDeskActivity;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.WorkspaceDetailsActivity;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.WorkspaceListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class SearchWorkspaceFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String TAG = SearchWorkspaceFragment.class.getSimpleName();

    WorkspaceManager manager;

    WorkspaceListAdapter mWorkspaceListAdapter;

    public SearchWorkspaceFragment() {}

    public static SearchWorkspaceFragment newInstance(int sectionNumber) {
        SearchWorkspaceFragment fragment = new SearchWorkspaceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = WorkspaceManager.getInstance();
        mWorkspaceListAdapter = new WorkspaceListAdapter(getActivity(), new ArrayList<Workspace>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View searchFragmentView = inflater.inflate(R.layout.fragment_search_workspace, container, false);

        ListView listView = (ListView) searchFragmentView.findViewById(R.id.workspacesList);
        listView.setAdapter(mWorkspaceListAdapter);

        // When selecting a workspace replaces this fragment for the FilesFragment
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // TODO : REMOVE DATABASEID FROM USAGE HEREEEE!
                Workspace workspace = mWorkspaceListAdapter.getItem(position);
                try {
                    WorkspaceManager.getInstance().insertWorkspaceToForeignWorkspaces(workspace);
                    Toast.makeText(getActivity(), "Workspace added to Foreign Workspaces", Toast.LENGTH_SHORT).show();
                } catch(Exception e) {
                    Toast.makeText(getActivity(), "Workspace already at Foreign Workspaces", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Register the list of Files for the ContextMenu
        registerForContextMenu(listView);

        // Setup create new workspace button
        ImageButton newWorkspaceButton = (ImageButton) searchFragmentView.findViewById(R.id.searchButton);
        newWorkspaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                mWorkspaceListAdapter.clear();

                EditText editText = (EditText) getActivity().findViewById(R.id.workspacesTags);
                String tags = editText.getText().toString();
                String[] tagsList = tags.split(" ");

                List<ForeignWorkspace> workspacesFound = WorkspaceManager.getInstance().getForeignWorkspacesWithTags(tagsList);

                for(ForeignWorkspace workspace : workspacesFound)
                    mWorkspaceListAdapter.add(workspace);

                mWorkspaceListAdapter.notifyDataSetChanged();
            }
        });

        return searchFragmentView;
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
            case R.id.menu_my_edit:
                intent = new Intent(getActivity(), WorkspaceDetailsActivity.class);
                intent.putExtra(WorkspaceDetailsActivity.EDIT_MODE, true);
                intent.putExtra(Constants.WORKSPACE_INDEX, info.position);
                getActivity().startActivity(intent);
                break;
            case R.id.menu_my_details:
                intent = new Intent(getActivity(), WorkspaceDetailsActivity.class);
                intent.putExtra(WorkspaceDetailsActivity.EDIT_MODE, false);
                intent.putExtra(Constants.WORKSPACE_INDEX, info.position);
                getActivity().startActivity(intent);
                break;
            case R.id.menu_my_delete:
                WorkspaceManager.getInstance().deleteWorkspace(info.position);
                updateWorkspaceList();
                break;
            case R.id.menu_my_invite:
                // TODO : INVITE FOR WORKSPACE
                Toast.makeText(getActivity(), "TODO Invite", Toast.LENGTH_SHORT).show();
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