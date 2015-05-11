package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.WorkspaceListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceException;
import pt.ulisboa.tecnico.cmov.airdesk.custom.AddTagsLayout;
import pt.ulisboa.tecnico.cmov.airdesk.custom.PredicateLayout;
import pt.ulisboa.tecnico.cmov.airdesk.custom.QuotaValueLayout;
import pt.ulisboa.tecnico.cmov.airdesk.manager.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.service.GetWorkspacesWithTagsService;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.AsyncResponse;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.BroadcastTask;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class SearchWorkspacesByTagsFragment extends DialogFragment {

    OnNewWorkspaceFragmentListener mCallback;

    Button cancelButton, createButton;
    AddTagsLayout mAddTagsLayout;
    ImageButton mSearchButton;
    TextView mSearchHint;
    ListView mWorkspaceListView;
    WorkspaceListAdapter mWorkspaceListAdapter;

    // AirDeskActivity must implement this interface
    public interface OnNewWorkspaceFragmentListener {
        public void updateWorkspaceList();
    }

    public static SearchWorkspacesByTagsFragment newInstance() {
        return new SearchWorkspacesByTagsFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View searchFragmentView = inflater.inflate(R.layout.fragment_search_workspace, container, false);

        // Get references to all the views
        mAddTagsLayout = (AddTagsLayout) searchFragmentView.findViewById(R.id.workspace_tags);
        mWorkspaceListView = (ListView) searchFragmentView.findViewById(R.id.workspace_list);
        mSearchHint = (TextView) searchFragmentView.findViewById(R.id.search_hint);
        mSearchButton = (ImageButton) searchFragmentView.findViewById(R.id.search_button);
        cancelButton = (Button)searchFragmentView.findViewById(R.id.cancelWorkspaceDialog);

        // Set up the workspace list adapter
        mWorkspaceListAdapter = new WorkspaceListAdapter(getActivity(), new ArrayList<Workspace>());
        mWorkspaceListAdapter.registerDataSetObserver(mWorkspaceAdapterDataSetObserver);

        mWorkspaceListView.setAdapter(mWorkspaceListAdapter);

        // Setup the on click listeners
        mSearchButton.setOnClickListener(mSearchButtonOnClickListener);
        mWorkspaceListView.setOnItemClickListener(mListViewOnClickListener);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return searchFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
//            mCallback = (OnNewWorkspaceFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    // ---------------------------
    // -------- Listeners --------
    // ---------------------------

    // Hides and shows elements depending on the workspace list contents
    private DataSetObserver mWorkspaceAdapterDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            if (mWorkspaceListAdapter.isEmpty()) {
                mSearchHint.setVisibility(View.VISIBLE);
                mWorkspaceListView.setVisibility(View.GONE);
            } else {
                mSearchHint.setVisibility(View.GONE);
                mWorkspaceListView.setVisibility(View.VISIBLE);
            }
        }
    };

    // Searches all public workspaces with supplied tags
    private View.OnClickListener mSearchButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Clear workspace list
            mWorkspaceListAdapter.clear();

            // Get and convert to String[] all tags, to send as arguments of service (String...)
            List<String> tagList = mAddTagsLayout.getAllTags();
            String[] tagValues = new String[tagList.size()];
            mAddTagsLayout.getAllTags().toArray(tagValues);

            // If no tags were added exit
            if(tagValues == null || tagValues.length == 0) {
                Toast.makeText(getActivity(), "No tags were added.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create the broadcast task to send the service to all connected peers
            BroadcastTask task = new BroadcastTask(
                    Integer.parseInt(getString(R.string.port)),
                    GetWorkspacesWithTagsService.class,
                    tagValues
            );
            // Override the callback so we can process the result from the task
            task.mDelegate = new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    try {
                        // Get the response as a JSONObject
                        JSONObject response = new JSONObject(output);

                        // Check if has error
                        if (response.has(Constants.ERROR_KEY))
                            throw new Exception(response.getString(Constants.ERROR_KEY));

                        // Get the list of workspaces returned and add them to our list
                        JSONArray workspaceArray = response.getJSONArray(Constants.WORKSPACE_LIST_KEY);
                        for(int i = 0; i < workspaceArray.length(); i++) {
                            mWorkspaceListAdapter.add(new ForeignWorkspace(workspaceArray.getJSONObject(i)));
                        }
                    } catch (Exception e1) {
                        // Show our error in a toast
                        Toast.makeText(getActivity(), e1.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            };
            // Execute our task
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    };

    private AdapterView.OnItemClickListener mListViewOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Workspace workspace = mWorkspaceListAdapter.getItem(position);
            try {
                workspace.addUser(UserManager.getInstance().getOwner());
                //TODO: Foreign Workspace
                //WorkspaceManager.getInstance().insertWorkspaceToForeignWorkspaces(workspace, UserManager.getInstance().getOwner());
                Toast.makeText(getActivity(), "Workspace added to Foreign Workspaces", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Workspace already at Foreign Workspaces", Toast.LENGTH_SHORT).show();
            }
        }
    };

}
