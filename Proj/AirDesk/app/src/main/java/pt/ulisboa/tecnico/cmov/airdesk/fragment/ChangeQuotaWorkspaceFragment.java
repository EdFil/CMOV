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
import pt.ulisboa.tecnico.cmov.airdesk.custom.QuotaValueLayout;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;

public class ChangeQuotaWorkspaceFragment extends DialogFragment {

    OnWorspaceInfoChangedListener mCallback;

    Workspace mWorkspace;

    Button mCancelButton, mSave;
    QuotaValueLayout mQuotaValueLayout;

    public static ChangeQuotaWorkspaceFragment newInstance() {
        return new ChangeQuotaWorkspaceFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View subscribeFragmentView = inflater.inflate(R.layout.fragment_change_quota_workspace, container, false);

        mSave = (Button) subscribeFragmentView.findViewById(R.id.ok_button);
        mCancelButton = (Button)subscribeFragmentView.findViewById(R.id.cancel_button);
        mQuotaValueLayout = (QuotaValueLayout) subscribeFragmentView.findViewById(R.id.workspace_quota_layout);
        mQuotaValueLayout.setQuota(mWorkspace.getMaxQuota());

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
                    WorkspaceManager.getInstance().changeQuotaWorkspace((LocalWorkspace)mWorkspace, mQuotaValueLayout.getQuota());
                    mCallback.onWorkspaceInfoChanged();
                }
//                else if (mWorkspace instanceof ForeignWorkspace) {
//                    ForeignWorkspace foreignWorkspace = (ForeignWorkspace) mWorkspace;
//                    String ip = foreignWorkspace.getOwner().getDevice().getVirtIp();
//                    int port = Integer.parseInt(getString(R.string.port));
//                    RequestTask task = new RequestTask(
//                            ip,
//                            port,
//                            ChangeQuotaWorspaceService.class,
//                            foreignWorkspace.getName(), String.valueOf(mQuotaValueLayout.getQuota()));
//                    task.mDelegate = new AsyncResponse() {
//                        @Override
//                        public void processFinish(String output) {
//                            mCallback.onWorkspaceInfoChanged();
//                        }
//                    };
//                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
//                }

                // Close dialog fragment
                dismiss();
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

    };

    public void setWorkspace(Workspace workspace) {
        mWorkspace = workspace;
    }
}
