package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.airdesk.AirDeskActivity;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.WorkspaceListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;

public class WorkspacesEditDetailsFragment extends Fragment {

    EditText mNameInformationEdit;
    EditText mOwnerInformationEdit;
    EditText mQuotaInformationEdit;
    Switch mPrivacyInformationSwitch;

    TableLayout mTagsTableLayout;
    TableLayout mUsersTableLayout;

    public WorkspacesEditDetailsFragment() {}

    public static WorkspacesEditDetailsFragment newInstance() {
        return new WorkspacesEditDetailsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        WorkspaceListAdapter workspaceListAdapter = (WorkspaceListAdapter) WorkspaceManager.getInstance().getWorkspaceListAdapter();

        final View workspaceFragmentView = inflater.inflate(R.layout.fragment_workspace_details_edit, container, false);

        mNameInformationEdit = (EditText) workspaceFragmentView.findViewById(R.id.nameInformationEdit);
        mOwnerInformationEdit = (EditText) workspaceFragmentView.findViewById(R.id.ownerInformationEdit);
        mQuotaInformationEdit = (EditText) workspaceFragmentView.findViewById(R.id.quotaInformationEdit);
        mPrivacyInformationSwitch = (Switch) workspaceFragmentView.findViewById(R.id.privateInformationSwitch);

        mTagsTableLayout = (TableLayout) workspaceFragmentView.findViewById(R.id.tagsTable);
        mTagsTableLayout.setStretchAllColumns(true);
        mUsersTableLayout = (TableLayout) workspaceFragmentView.findViewById(R.id.usersTable);
        mUsersTableLayout.setStretchAllColumns(true);

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
}