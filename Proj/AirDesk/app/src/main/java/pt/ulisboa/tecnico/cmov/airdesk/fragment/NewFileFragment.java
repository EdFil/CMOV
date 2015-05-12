package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class NewFileFragment extends DialogFragment {

    OnNewFileFragmentListener mCallback;

    // AirDeskActivity must implement this interface
    public interface OnNewFileFragmentListener {
        public void updateFileList();
    }

    LocalWorkspace mWorkspace;

    Button mCancelButton;
    Button mCreateButton;
    EditText mFileNameText;

    public static NewFileFragment newInstance() {
        return new NewFileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_file, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Get the WORKSPACE selected in order to retrieve the respective files
        Bundle bundle = getArguments();
        mWorkspace = WorkspaceManager.getInstance().getLocalWorkspaceWithId(bundle.getLong(Constants.WORKSPACE_ID_KEY));

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
            mCallback = (OnNewFileFragmentListener) activity;
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
            String fileName = mFileNameText.getText().toString().trim();

            try {
                // Create workspace with associated user (owner) in database
                WorkspaceManager.getInstance().addFileToWorkspace(fileName, mWorkspace);

                // request the activity to update the FilesFragment's files
                mCallback.updateFileList();

                Toast.makeText(v.getContext(), "File created", Toast.LENGTH_SHORT).show();

                // Close dialog fragment
                dismiss();
            } catch (Exception e) {
                Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

}
