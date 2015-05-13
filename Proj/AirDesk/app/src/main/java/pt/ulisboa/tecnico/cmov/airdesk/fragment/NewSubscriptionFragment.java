package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.subscription.Subscription;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.custom.AddTagsLayout;
import pt.ulisboa.tecnico.cmov.airdesk.manager.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.service.GetWorkspacesWithTagsService;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.AsyncResponse;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.BroadcastTask;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;
import pt.ulisboa.tecnico.cmov.airdesk.util.Util;

public class NewSubscriptionFragment extends DialogFragment {

    OnNewSubscriptionFragmentListener mCallback;

    Button cancelButton, mountButton;
    AddTagsLayout mAddTagsLayout;
    EditText subscriptionName;

    // AirDeskActivity must implement this interface
    public interface OnNewSubscriptionFragmentListener {
        void updateSubscriptionList();
    }

    public static NewSubscriptionFragment newInstance() {
        return new NewSubscriptionFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View subscribeFragmentView = inflater.inflate(R.layout.fragment_mount_workspaces, container, false);

        // Get references to all the views
        mAddTagsLayout = (AddTagsLayout) subscribeFragmentView.findViewById(R.id.sub_workspace_tags);

        mountButton = (Button) subscribeFragmentView.findViewById(R.id.mountButton);
        cancelButton = (Button)subscribeFragmentView.findViewById(R.id.cancelDialog);
        subscriptionName = (EditText) subscribeFragmentView.findViewById(R.id.newSubscriptionName);

        // Setup the on click listeners
        mountButton.setOnClickListener(mMountButtonOnClickListener);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return subscribeFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnNewSubscriptionFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    // Searches all public workspaces with supplied tags
    private View.OnClickListener mMountButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

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
            // TODO : DESCOMENTAR QUANDO SE PASSAR PARA NETWORK
//            BroadcastTask task = new BroadcastTask(
//                    Integer.parseInt(getString(R.string.port)),
//                    GetWorkspacesWithTagsService.class,
//                    tagValues
//            );

//            // Override the callback so we can process the result from the task
//            task.mDelegate = new AsyncResponse() {
//                @Override
//                public void processFinish(String output) {
//                    try {
//                        // Get the response as a JSONObject
//                        JSONObject response = new JSONObject(output);
//
//                        // Check if has error
//                        if (response.has(Constants.ERROR_KEY))
//                            throw new Exception(response.getString(Constants.ERROR_KEY));
//
//                        // Get the list of workspaces returned and add them to our list
//                        JSONArray workspaceArray = response.getJSONArray(Constants.WORKSPACE_LIST_KEY);
//                        for(int i = 0; i < workspaceArray.length(); i++) {
//                            Toast.makeText(getActivity(), "TODO : WORKSPACE TO ADD", Toast.LENGTH_SHORT).show();
//                                WorkspaceManager.getInstance().mountForeignWorkspace(workspaceArray.getJSONObject(i));
//                        }
//                    } catch (Exception e1) {
//                        // Show our error in a toast
//                        Toast.makeText(getActivity(), e1.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            };
//            // Execute our task
//            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



            // TODO : QUANDO ONLINE (COMENTADO EM CIMA) , REMOVER ESTE CICLO MOUNT
            // Workspaces with at least one of the tags
            List<JSONObject> jsonWorkspaceList = WorkspaceManager.getInstance().getJsonWorkspacesWithTags(tagValues);

            for (JSONObject jsonWorkspace : jsonWorkspaceList) {
                try {
                    WorkspaceManager.getInstance().mountForeignWorkspace(jsonWorkspace);
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "NewSubscriptionFragment : " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            // TODO : ATE AQUI

            // Create the subscription with the tags and workspaces
            Subscription subscription = new Subscription(subscriptionName.getText().toString(), tagValues);

            // Add the subscription to the subscription list of the user
            UserManager.getInstance().getSubscriptionList().add(subscription);

            // CALL back to refresh subscription list on the SubscriptionFragment through the AirDeskActivity
            mCallback.updateSubscriptionList();

            // Close dialog fragment
            dismiss();
        }

    };
}
