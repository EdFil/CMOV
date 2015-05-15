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

import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.service.CreateFileService;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.AsyncResponse;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.RequestTask;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class NewRemoteFileFragment extends DialogFragment {

    OnRemoteNewFileFragmentListener mCallback;

    // AirDeskActivity must implement this interface
    public interface OnRemoteNewFileFragmentListener {
        public void updateRemoteFileList();
    }

    ForeignWorkspace mWorkspace;

    Button mCancelButton;
    Button mCreateButton;
    EditText mFileNameText;

    public static NewRemoteFileFragment newInstance() {
        return new NewRemoteFileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_file, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Get the WORKSPACE selected in order to retrieve the respective files
        Bundle bundle = getArguments();
        mWorkspace = WorkspaceManager.getInstance().getForeignWorkspaceWithId(bundle.getLong(Constants.WORKSPACE_ID_KEY));

        // Store the references to the view elements in this fragment
        mFileNameText = (EditText) view.findViewById(R.id.newFileName);
        mCancelButton = (Button)view.findViewById(R.id.cancelFile);
        mCreateButton = (Button) view.findViewById(R.id.createFile);

        // Setup listeners
        mCancelButton.setOnClickListener(mCancelButtonOnClickListener);
        mCreateButton.setOnClickListener(mCreateButtonOnClickListener);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnRemoteNewFileFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    // -------------------------
    // ------- Listeners -------
    // -------------------------

    // What to do when cancel is pressed
    private View.OnClickListener mCancelButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    // What to do when create is pressed
    private View.OnClickListener mCreateButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String fileName = mFileNameText.getText().toString().trim();

            try {
                String ip = mWorkspace.getOwner().getDevice().getVirtIp();
                int port = Integer.parseInt(getString(R.string.port));

                RequestTask task = new RequestTask(ip, port, CreateFileService.class, mWorkspace.getName(), fileName);
                task.mDelegate = new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        // Get the response as a JSONObject
                        try {
                            JSONObject response = new JSONObject(output);

                            // Check if has error
                            if (response.has(Constants.ERROR_KEY)) {
                                throw new Exception(response.getString(Constants.ERROR_KEY));
                            }

                            // Create workspace with associated user (owner) in database
                            WorkspaceManager.getInstance().addFileToWorkspace(fileName, mWorkspace);

                            // request the activity to update the FilesFragment's files
                            mCallback.updateRemoteFileList();
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);


                // Close dialog fragment
                dismiss();
            } catch (Exception e) {
                Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

}
