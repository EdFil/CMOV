package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.custom.AddTagsLayout;

public class WorkspacesDetailsEditFragment extends Fragment {

    private static final int NUM_COLUMNS_PER_ROW = 2;
    boolean isLocal;

    EditText mNameInformationEdit;
    EditText mQuotaInformationEdit;
    Switch mPrivacyInformationSwitch;
    TextView mPrivateInformationText;
    TextView mPublicInformationText;
    ViewSwitcher mViewSwitcher;
    TextView mTagsHintText;
    AddTagsLayout mAddTagsLayout;
    TableLayout mUsersTableLayout;

    LinearLayout quotaLayout;

    Workspace mWorkspace;

    public WorkspacesDetailsEditFragment() {}

    public static WorkspacesDetailsEditFragment newInstance() {
        return new WorkspacesDetailsEditFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View workspaceFragmentView = inflater.inflate(R.layout.fragment_workspace_details_edit, container, false);

        // Find Views
        mNameInformationEdit = (EditText) workspaceFragmentView.findViewById(R.id.nameInformationEdit);
        mQuotaInformationEdit = (EditText) workspaceFragmentView.findViewById(R.id.quotaInformationEdit);
        mPrivacyInformationSwitch = (Switch) workspaceFragmentView.findViewById(R.id.privateInformationSwitch);
        mPrivateInformationText = (TextView) workspaceFragmentView.findViewById(R.id.privateInformationText);
        mPublicInformationText = (TextView) workspaceFragmentView.findViewById(R.id.publicInformationText);
        mViewSwitcher = (ViewSwitcher) workspaceFragmentView.findViewById(R.id.viewSwitcher);
        mTagsHintText = (TextView) workspaceFragmentView.findViewById(R.id.tagsHintText);

        quotaLayout = (LinearLayout) workspaceFragmentView.findViewById(R.id.quotaLayout);

        if(isLocal)
            quotaLayout.setVisibility(View.VISIBLE);
        else
            quotaLayout.setVisibility(View.GONE);

        mAddTagsLayout = (AddTagsLayout) workspaceFragmentView.findViewById(R.id.workspacesTagsEdit);

        if(mWorkspace.isPrivate()){
            //DO NOTHING
        } else {
            // Switch to AddTags view
            mViewSwitcher.showNext();
        }

        //mAddTags.setVisibility(mWorkspace.isPrivate() ? View.GONE : View.VISIBLE);
       // mTagsHintText.setVisibility(mWorkspace.isPrivate() ? View.VISIBLE : View.GONE);

        mUsersTableLayout = (TableLayout) workspaceFragmentView.findViewById(R.id.usersTableEdit);
        mUsersTableLayout.setStretchAllColumns(true);

        // Set initial values
        mNameInformationEdit.setText(mWorkspace.getName());
        mQuotaInformationEdit.setText(String.valueOf(mWorkspace.getMaxQuota()));
        mPrivacyInformationSwitch.setChecked(!mWorkspace.isPrivate());

        for(Tag tag : mWorkspace.getTags())
            addTagToTable(tag.getText());

        for(User user : mWorkspace.getUsers())
            addUserToTable(user.getEmail());

        // Set Listeners
        mPrivacyInformationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mViewSwitcher.showNext();
          //  mAddTags.setVisibility(isChecked ? View.VISIBLE : View.GONE);
           // mTagsHintText.setVisibility(isChecked ? View.GONE: View.VISIBLE);
            }
        });

        mPublicInformationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrivacyInformationSwitch.setChecked(true);
            }
        });

        mPrivateInformationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrivacyInformationSwitch.setChecked(false);
            }
        });

        mAddTagsLayout.setCallback(new AddTagsLayout.AddTagsCallback() {
            @Override
            public void addTag(String tag){
                WorkspaceManager.getInstance().addTagToWorkspace(tag, mWorkspace);
            }

            @Override
            public void removeTag(String tag) {
                WorkspaceManager.getInstance().removeTagFromWorkspace(tag, mWorkspace);
            }

        });

        return workspaceFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void sendWorkspaceDetails(Workspace workspace, boolean isLocal){
        mWorkspace = workspace;
        this.isLocal = isLocal;
    }

    public Workspace getEditedWorkspace() {
        String newWorkspaceName = mNameInformationEdit.getText().toString();
        Long newWorkspaceQuota = Long.parseLong(mQuotaInformationEdit.getText().toString().trim());
        boolean newWorkspacePrivacy = !mPrivacyInformationSwitch.isChecked();
        try {
            WorkspaceManager.getInstance().updateWorkspace(mWorkspace, newWorkspaceName, newWorkspaceQuota, newWorkspacePrivacy);
        }catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return mWorkspace;
    }

    private void addTagToTable(final String tag) {
        mAddTagsLayout.addTag(tag);
    }

    private void addUserToTable(String tag) {
        TextView userText = new TextView(getActivity());
        userText.setText(tag);
        userText.setTextAppearance(getActivity(), android.R.style.TextAppearance_DeviceDefault_Medium);
        TableRow rowToInsertTag;
        int numRows = mUsersTableLayout.getChildCount();
        if(numRows == 0 || ((TableRow)mUsersTableLayout.getChildAt(numRows - 1)).getChildCount() >= NUM_COLUMNS_PER_ROW) {
            rowToInsertTag = new TableRow(getActivity());
            mUsersTableLayout.addView(rowToInsertTag);
        } else {
            rowToInsertTag = (TableRow)mUsersTableLayout.getChildAt(numRows - 1);
        }
        rowToInsertTag.addView(userText, ActionBar.LayoutParams.WRAP_CONTENT);
    }



}