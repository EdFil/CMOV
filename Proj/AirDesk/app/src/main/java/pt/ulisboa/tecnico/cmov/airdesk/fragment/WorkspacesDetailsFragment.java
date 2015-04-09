package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;

public class WorkspacesDetailsFragment extends Fragment {

    private static final int NUM_COLUMNS_PER_ROW = 4;

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
        mPrivacyInformationText = (TextView) workspaceFragmentView.findViewById(R.id.privacyInformationText);

        mTagsTableLayout = (TableLayout) workspaceFragmentView.findViewById(R.id.tagsTableView);
        mTagsTableLayout.setStretchAllColumns(true);
        mUsersTableLayout = (TableLayout) workspaceFragmentView.findViewById(R.id.usersTableView);
        mUsersTableLayout.setStretchAllColumns(true);

        mNameInformationText.setText(mWorkspace.getName());
        mOwnerInformationText.setText(mWorkspace.getOwner().getNick());

        long bytesUsed = 0;
        for(File file : mWorkspace.getFiles())
            bytesUsed += file.length();
        mQuotaInformationText.setText("Used " + bytesUsed + " out of " + mWorkspace.getQuota());

        mPrivacyInformationText.setText(mWorkspace.isPrivate()? "Private" : "Public");

        for(Tag tag : mWorkspace.getTags())
            addTagToTable(tag.getText());

        for(User user : mWorkspace.getUsers())
            addUserToTable(user.getEmail());


        return workspaceFragmentView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void sendWorkspaceDetails(Workspace workspace){
        mWorkspace = workspace;
    }

    private void addTagToTable(String tag) {
        TextView tagText = new TextView(getActivity());
        tagText.setText(tag);
        tagText.setTextAppearance(getActivity(), android.R.style.TextAppearance_DeviceDefault_Medium);
        TableRow rowToInsertTag;
        int numRows = mTagsTableLayout.getChildCount();
        if(numRows == 0 || ((TableRow)mTagsTableLayout.getChildAt(numRows - 1)).getChildCount() >= NUM_COLUMNS_PER_ROW) {
            rowToInsertTag = new TableRow(getActivity());
            mTagsTableLayout.addView(rowToInsertTag);
        } else {
            rowToInsertTag = (TableRow)mTagsTableLayout.getChildAt(numRows - 1);
        }
        rowToInsertTag.addView(tagText, ActionBar.LayoutParams.WRAP_CONTENT);
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