package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TableLayout;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;

public class WorkspacesDetailsEditFragment extends Fragment {

    EditText mNameInformationEdit;
    EditText mOwnerInformationEdit;
    EditText mQuotaInformationEdit;
    Switch mPrivacyInformationSwitch;

    TableLayout mTagsTableLayout;
    TableLayout mUsersTableLayout;

    Workspace mWorkspace;

    public WorkspacesDetailsEditFragment() {}

    public static WorkspacesDetailsEditFragment newInstance() {
        return new WorkspacesDetailsEditFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View workspaceFragmentView = inflater.inflate(R.layout.fragment_workspace_details_edit, container, false);

        mNameInformationEdit = (EditText) workspaceFragmentView.findViewById(R.id.nameInformationEdit);
        mOwnerInformationEdit = (EditText) workspaceFragmentView.findViewById(R.id.ownerInformationEdit);
        mQuotaInformationEdit = (EditText) workspaceFragmentView.findViewById(R.id.quotaInformationEdit);
        mPrivacyInformationSwitch = (Switch) workspaceFragmentView.findViewById(R.id.privateInformationSwitch);

        mTagsTableLayout = (TableLayout) workspaceFragmentView.findViewById(R.id.tagsTable);
        mTagsTableLayout.setStretchAllColumns(true);
        mUsersTableLayout = (TableLayout) workspaceFragmentView.findViewById(R.id.usersTable);
        mUsersTableLayout.setStretchAllColumns(true);

        mNameInformationEdit.setText(mWorkspace.getName());
        mOwnerInformationEdit.setText(mWorkspace.getOwner().getNick());
        mQuotaInformationEdit.setText(String.valueOf(mWorkspace.getQuota()));
        mPrivacyInformationSwitch.setChecked(!mWorkspace.isPrivate());

//        for(Tag tag : workspace.getTags())
//            addTagToTable(tag.getText());
//
//        for(User user : workspace.getUsers())
//            addUserToTable(user.getEmail());


        return workspaceFragmentView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void sendWorkspaceDetails(Workspace workspace){
        mWorkspace = workspace;
    }

    public Workspace getEditedWorkspace() {
        mWorkspace.setName(mNameInformationEdit.getText().toString());
        mWorkspace.setQuota(Long.parseLong(mQuotaInformationEdit.getText().toString().trim()));
        mWorkspace.setIsPrivate(!mPrivacyInformationSwitch.isChecked());
        return mWorkspace;
    }
}