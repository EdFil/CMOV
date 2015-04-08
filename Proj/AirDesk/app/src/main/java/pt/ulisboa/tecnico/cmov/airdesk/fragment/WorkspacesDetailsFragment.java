package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;

public class WorkspacesDetailsFragment extends Fragment {

    TextView mNameInformationText;
    TextView mOwnerInformationText;
    TextView mQuotaInformationText;
    TextView mPrivacyInformationText;

    TableLayout mTagsTableLayout;
    TableLayout mUsersTableLayout;

    Workspace mWorkspace;

    public WorkspacesDetailsFragment() {}

    public static WorkspacesDetailsFragment newInstance() {
        return new WorkspacesDetailsFragment();
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

        mNameInformationText.setText(mWorkspace.getName());
        mOwnerInformationText.setText(mWorkspace.getOwner().getNick());
        mQuotaInformationText.setText(String.valueOf(mWorkspace.getQuota()));
        mPrivacyInformationText.setText(mWorkspace.isPrivate()? "Private" : "Public");

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

    public void sendWorkspaceDetails(Workspace workspace){
        mWorkspace = workspace;
    }
}