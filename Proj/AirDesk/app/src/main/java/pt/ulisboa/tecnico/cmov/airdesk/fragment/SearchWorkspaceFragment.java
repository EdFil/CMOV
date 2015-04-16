package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.AirDeskActivity;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.WorkspaceDetailsActivity;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.WorkspaceListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.RequestWorkspacesFromTagsTask;
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
//        mWorkspaceListAdapter.registerDataSetObserver(new DataSetObserver() {
//            @Override
//            public void onChanged() {
//                super.onChanged();
//            }
//        });

        // Setup create new workspace button
        ImageButton newWorkspaceButton = (ImageButton) searchFragmentView.findViewById(R.id.searchButton);
        newWorkspaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                EditText editText = (EditText) getActivity().findViewById(R.id.workspacesTags);
                String tags = editText.getText().toString();
                String[] tagsList = tags.split(" ");

                (new RequestWorkspacesFromTagsTask(mWorkspaceListAdapter)).execute(tagsList);

                TextView searchHint = (TextView) getActivity().findViewById(R.id.search_hint);
                ListView workspacesTagList = (ListView) getActivity().findViewById(R.id.workspacesList);

                searchHint.setVisibility(View.GONE);
                workspacesTagList.setVisibility(View.VISIBLE);

            }
        });

        // When selecting a workspace replaces this fragment for the FilesFragment
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Workspace workspace = mWorkspaceListAdapter.getItem(position);
                try {
                    workspace.addUser(UserManager.getInstance().getOwner());
                    WorkspaceManager.getInstance().insertWorkspaceToForeignWorkspaces(workspace, UserManager.getInstance().getOwner());
                    Toast.makeText(getActivity(), "Workspace added to Foreign Workspaces", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Workspace already at Foreign Workspaces", Toast.LENGTH_SHORT).show();
                }

                mWorkspaceListAdapter.clear();

                TextView searchHint = (TextView) getActivity().findViewById(R.id.search_hint);
                ListView workspacesTagList = (ListView) getActivity().findViewById(R.id.workspacesList);

                searchHint.setVisibility(View.VISIBLE);
                workspacesTagList.setVisibility(View.GONE);
            }
        });

        return searchFragmentView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AirDeskActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public void updateWorkspaceList() {
        mWorkspaceListAdapter.notifyDataSetChanged();
    }
}