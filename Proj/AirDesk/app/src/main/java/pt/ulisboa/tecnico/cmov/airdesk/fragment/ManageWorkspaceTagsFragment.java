package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.custom.AddTagsLayout;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;

public class ManageWorkspaceTagsFragment extends DialogFragment {

    OnWorspaceInfoChangedListener mCallback;

    Workspace mWorkspace;

    Button mCancelButton, mSave;
    AddTagsLayout mTagsLayout;

    public static ManageWorkspaceTagsFragment newInstance() {
        return new ManageWorkspaceTagsFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View subscribeFragmentView = inflater.inflate(R.layout.fragment_change_tags_workspace, container, false);

        mSave = (Button) subscribeFragmentView.findViewById(R.id.ok_button);
        mCancelButton = (Button)subscribeFragmentView.findViewById(R.id.cancel_button);
        mTagsLayout = (AddTagsLayout) subscribeFragmentView.findViewById(R.id.workspace_tags);
        mTagsLayout.setAllTags(mWorkspace.getTags());

        // Setup the on click listeners
        mSave.setOnClickListener(mSaveOnClickListener);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
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
        try {
            mCallback = (OnWorspaceInfoChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    // Searches all public workspaces with supplied tags
    private View.OnClickListener mSaveOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Add the subscription to the subscription list of the user
            try {
                if(mWorkspace instanceof LocalWorkspace) {
                    WorkspaceManager.getInstance().changeTagsWorkspace((LocalWorkspace) mWorkspace, mTagsLayout.getAllTags());
                    mCallback.onWorkspaceInfoChanged();
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            // Close dialog fragment
            dismiss();
        }

    };

    public void setWorkspace(Workspace workspace) {
        mWorkspace = workspace;
    }
}
