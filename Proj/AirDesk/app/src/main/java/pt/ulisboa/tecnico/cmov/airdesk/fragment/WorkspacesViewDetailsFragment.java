package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import pt.ulisboa.tecnico.cmov.airdesk.AirDeskActivity;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.WorkspaceDetailsActivity;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.WorkspaceListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;

public class WorkspacesViewDetailsFragment extends Fragment {

    TextView mNameInformationText;
    TextView mOwnerInformationText;
    TextView mQuotaInformationText;
    TextView mPrivacyInformationText;

    TableLayout mTagsTableLayout;
    TableLayout mUsersTableLayout;

    public WorkspacesViewDetailsFragment() {}

    public static WorkspacesViewDetailsFragment newInstance() {
        return new WorkspacesViewDetailsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View workspaceFragmentView = inflater.inflate(R.layout.fragment_workspace_details_view, container, false);

        mNameInformationText = (TextView) workspaceFragmentView.findViewById(R.id.nameInformationText);
        mOwnerInformationText = (TextView) workspaceFragmentView.findViewById(R.id.ownerInformationText);
        mQuotaInformationText = (TextView) workspaceFragmentView.findViewById(R.id.quotaInformationText);
        mPrivacyInformationText = (TextView) workspaceFragmentView.findViewById(R.id.privateInformationText);

        mTagsTableLayout = (TableLayout) workspaceFragmentView.findViewById(R.id.tagsTableView);
        mTagsTableLayout.setStretchAllColumns(true);
        mUsersTableLayout = (TableLayout) workspaceFragmentView.findViewById(R.id.usersTableView);
        mUsersTableLayout.setStretchAllColumns(true);

        //mOwnerInformationText.setText(workspace.getOwner().getEmail());

//        long bytesUsed = 0;
//        for(File file : workspace.getFiles())
//            bytesUsed += file.length();
//
//        mQuotaInformationText.setText("Used " + bytesUsed + " out of " + workspace.getQuota());
//        mPrivacyInformationText.setText(workspace.isPrivate() ? "True" : "False");

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